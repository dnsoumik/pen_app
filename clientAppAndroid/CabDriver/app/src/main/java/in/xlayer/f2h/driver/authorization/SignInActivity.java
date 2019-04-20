package in.xlayer.f2h.driver.authorization;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import in.xlayer.f2h.driver.BuildConfig;
import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.handler.LocalMemory;
import in.xlayer.f2h.driver.util.ValidateUtil;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A login screen that offers login via email/password.
 */
public class SignInActivity extends AppCompatActivity {

    private String TAG = SignInActivity.class.getSimpleName();
    private LocalMemory localMemory = null;
    private AuthDeviceStatus status = null;
    private AuthHttpRequestBuilder authHttpRequestBuilder = null;

    private View mSignInView, mProgressView, dev_link;
    private TextView mobile_number, dev_website_link;
    private int link_count = 0;
    private EditText country_code;
    private BroadcastReceiver localBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle switchType = intent.getExtras();
            assert switchType != null;
            if (!SignInActivity.this.isDestroyed() && !SignInActivity.this.isFinishing()) {
                Log.i(TAG + "BroadcastHandler: ", "onReceive: " + switchType.getInt(getResources().getString(R.string.bd_sign_in_act_tag)));
                switch (switchType.getInt(getResources().getString(R.string.bd_sign_in_act_tag))) {
                    case 0:
                        showProgress(false);
                        break;
                    case 1:
                        showProgress(false);
                        if (AuthStaticElements.SIGN_IN_RESPONSE.getCode() != null) {
                            if (AuthStaticElements.SIGN_IN_RESPONSE.getStatus()) {
                                AuthStaticElements.AUTH_MOBILE_NUMBER = "91" + mobile_number.getText().toString();
                                AuthStaticElements.OTP_ACTIVITY_PARENT = SignInActivity.class.getName();
                                Intent enterOtp = new Intent(SignInActivity.this, EnterOTPActivity.class);
                                enterOtp.putExtra("phone_number", AuthStaticElements.AUTH_MOBILE_NUMBER);
                                startActivity(enterOtp);
                                finish();
                            } else {
                                mobile_number.setError(AuthStaticElements.SIGN_IN_RESPONSE.getMessage());
                                mobile_number.requestFocus();
                            }
                        }
                        break;
                    case 2:
                        break;
                }
            }
        }
    };
    private boolean signInStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        localMemory = new LocalMemory(this);
        status = new AuthDeviceStatus(this);
        authHttpRequestBuilder = new AuthHttpRequestBuilder(this);

        LocalBroadcastManager.getInstance(SignInActivity.this).registerReceiver(localBroadCastReceiver,
                new IntentFilter(getResources().getString(R.string.bd_sign_in_act_filter)));

        mSignInView = findViewById(R.id.sign_in_form);
        mProgressView = findViewById(R.id.sign_in_progress);
        Button next_button = findViewById(R.id.sign_in_next_button);

        // developer link
        dev_link = findViewById(R.id.sign_in_dev_link);
        dev_website_link = (EditText) findViewById(R.id.dev_website_link);
        dev_website_link.setText(localMemory.getServerUrl());
        ImageView app_icon = findViewById(R.id.app_logo);
        app_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                link_count++;
                if (link_count == 5) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(mobile_number.getWindowToken(), 0);
                    dev_link.setVisibility(View.VISIBLE);
                    Snackbar.make(v, "Link option activated", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    link_count = 0;
                }
            }
        });

        mobile_number = findViewById(R.id.sign_in_mobile_number);
        country_code = findViewById(R.id.countryCode);
        mobile_number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(mobile_number.getWindowToken(), 0);
                    attemptToSignIn();
                    return true;
                }
                return false;
            }
        });
        mobile_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Objects.equals(mobile_number.length(), 10)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(mobile_number.getWindowToken(), 0);
                }
            }
        });
        next_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(mobile_number.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(dev_website_link.getWindowToken(), 0);
                attemptToSignIn();
            }
        });
        requestPermission();
        final TextView register_now = findViewById(R.id.register_now);
        register_now.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerNow = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(registerNow);
                finish();
            }
        });
    }

    void requestPermission() {
        String[] permissions = getResources().getStringArray(R.array.asked_permissions);
        int permsRequestCode = 200;
        ActivityCompat.requestPermissions(this, permissions, permsRequestCode);
    }

    @Override
    public void onBackPressed() {
        if (mProgressView != null && mProgressView.isShown()) {
            showProgress(false);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED
                || grantResults[2] == PackageManager.PERMISSION_GRANTED || grantResults[3] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Permission required", LENGTH_SHORT).show();
            finish();
        }
    }

    private void attemptToSignIn() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(mobile_number.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(dev_website_link.getWindowToken(), 0);

        // TODO: for country code hard coded
        country_code.setText(getResources().getString(R.string.country_code));

        // Reset errors.
        dev_website_link.setError(null);
        mobile_number.setError(null);

        // Store values at the time of the login attempt.
        String link = dev_website_link.getText().toString();
        String mob_no = mobile_number.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(link) || !ValidateUtil.isValidWebsiteUrl(link)) {
            dev_website_link.setError("Server is not valid");
            focusView = dev_website_link;
            cancel = true;
        } else {
            Log.e(TAG, "attemptToSignIn: " + dev_website_link.getText().toString());
            localMemory.changeServerUrl(dev_website_link.getText().toString());
        }

        if (TextUtils.isEmpty(mob_no)) {
            mobile_number.setError("Mobile number is not valid");
            focusView = mobile_number;
            cancel = true;
        }

        if (mob_no.length() < 10) {
            mobile_number.setError("Mobile number is too short");
            focusView = mobile_number;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            Log.e("SIGNIN", "here id the error");
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user sign in attempt.
            if (signInStatus) {
                signInRequestInterval();
                signInStatus = false;
                showProgress(true);
                JSONObject body = new JSONObject();
                try {
                    body.put("application_id", BuildConfig.APPLICATION_ID);
                    body.put("phone_number", 91 + mob_no);
//                    body.put("country_code", 91);
                    if (status.isOnlineWithToast()) {
                        authHttpRequestBuilder.SignInPost(body);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void signInRequestInterval() {
        new CountDownTimer(5000L, 500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                signInStatus = true;
            }
        }.start();
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
        mSignInView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

}

