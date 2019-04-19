package in.xlayer.f2h.driver.authorization;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import in.xlayer.f2h.driver.BuildConfig;
import in.xlayer.f2h.driver.R;


/**
 * A login screen that offers login via email/password.
 */

public class SignUpActivity extends AppCompatActivity {

    private String TAG = SignInActivity.class.getSimpleName();
    //    private AuthLocalMemory localMemory = null;
    private AuthDeviceStatus status = null;
    private AuthHttpRequestBuilder authHttpRequestBuilder = null;

    private View mProgressView, mSignUpFormView;

    private EditText first_name,last_name, country_code, mobile_number, email_id;
    private BroadcastReceiver localBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle switchType = intent.getExtras();
            assert switchType != null;
            if (!SignUpActivity.this.isDestroyed() && !SignUpActivity.this.isFinishing()) {
                Log.i(TAG + "BroadcastHandler: ", "onReceive: " + switchType.getInt(getResources().getString(R.string.bd_sign_up_act_tag)));
                switch (switchType.getInt(getResources().getString(R.string.bd_sign_up_act_tag))) {
                    case 0:
                        showProgress(false);
                        break;
                    case 1:
                        showProgress(false);
                        if (AuthStaticElements.SIGN_UP_RESPONSE.getStatus() != null) {
                            if (AuthStaticElements.SIGN_UP_RESPONSE.getStatus()) {
                                AuthStaticElements.AUTH_MOBILE_NUMBER = mobile_number.getText().toString();
                                AuthStaticElements.COUNTRY_CODE = country_code.getText().toString();
                                AuthStaticElements.OTP_ACTIVITY_PARENT = SignUpActivity.class.getName();
                                Intent enterOtp = new Intent(SignUpActivity.this, EnterOTPActivity.class);
                                startActivity(enterOtp);
                                finish();
                            } else {
                                if (AuthStaticElements.SIGN_UP_RESPONSE.getCode() == 1) {
                                    mobile_number.setError(AuthStaticElements.SIGN_UP_RESPONSE.getMessage());
                                    mobile_number.requestFocus();
                                } else if (AuthStaticElements.SIGN_UP_RESPONSE.getCode() == 2) {
                                    email_id.setError(AuthStaticElements.SIGN_UP_RESPONSE.getMessage());
                                    email_id.requestFocus();
                                } else {
                                    Toast.makeText(SignUpActivity.this, AuthStaticElements.SIGN_UP_RESPONSE.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        break;
                    case 2:
                        break;
                }
            }
        }
    };
    private boolean signUpStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

//        localMemory = new AuthLocalMemory(this);
        status = new AuthDeviceStatus(this);
        authHttpRequestBuilder = new AuthHttpRequestBuilder(this);

        LocalBroadcastManager.getInstance(SignUpActivity.this).registerReceiver(localBroadCastReceiver,
                new IntentFilter(getResources().getString(R.string.bd_sign_up_act_filter)));

        mProgressView = findViewById(R.id.sign_up_in_progress);
        mSignUpFormView = findViewById(R.id.sign_up_form);

        first_name = findViewById(R.id.reg_first_name);
        last_name = findViewById(R.id.reg_last_name);
        country_code = findViewById(R.id.reg_country_code);
        mobile_number = findViewById(R.id.reg_mobile_number);
        email_id = findViewById(R.id.reg_email);
        Button sign_up_button = findViewById(R.id.sign_up_button);

        sign_up_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(mobile_number.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(email_id.getWindowToken(), 0);
                attemptToSignUp();
            }
        });
        first_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(first_name.getWindowToken(), 0);
                    mobile_number.requestFocus();
                    return true;
                }
                return false;
            }
        });
        last_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(last_name.getWindowToken(), 0);
                    mobile_number.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mobile_number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(mobile_number.getWindowToken(), 0);
                    email_id.requestFocus();
                    return true;
                }
                return false;
            }
        });
        email_id.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(email_id.getWindowToken(), 0);
                    attemptToSignUp();
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

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mProgressView != null && mProgressView.isShown()) {
            showProgress(false);
        } else {
            super.onBackPressed();
            Intent closeIntent;
            closeIntent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(closeIntent);
            finish();
        }
    }

    private void attemptToSignUp() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(email_id.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(email_id.getWindowToken(), 0);

        // TODO: for country code hard coded
        country_code.setText(getResources().getString(R.string.country_code));

        // Reset errors.
        first_name.setError(null);
        last_name.setError(null);
        mobile_number.setError(null);
        email_id.setError(null);

        // Store values at the time of the login attempt.
        String f_name = first_name.getText().toString();
        String l_name = last_name.getText().toString();
        Long mob_no = Long.valueOf(mobile_number.getText().toString());
        String email = email_id.getText().toString();
        Integer c_code = Integer.valueOf(country_code.getText().toString());

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(f_name)) {
            first_name.setError("This field is required");
            focusView = first_name;
            cancel = true;
        }

        if (TextUtils.isEmpty(f_name)) {
            last_name.setError("This field is required");
            focusView = last_name;
            cancel = true;
        }

        if (mob_no == null && !cancel) {
            mobile_number.setError("This field is required");
            focusView = mobile_number;
            cancel = true;
        }

        if (mob_no < 10 && !cancel) {
            mobile_number.setError("Mobile number is too short");
            focusView = mobile_number;
            cancel = true;
        }

        if (!TextUtils.isEmpty(email) && !email.contains("@") && !cancel) {
            email_id.setError("Email is not valid");
            focusView = email_id;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user sign in attempt.
            if (signUpStatus) {
                signUpRequestInterval();
                signUpStatus = false;
                showProgress(true);
                JSONObject body = new JSONObject();
                try {
                    body.put("application_id", BuildConfig.APPLICATION_ID);
                    body.put("phone_number", mob_no);
                    body.put("country_code", c_code);
                    body.put("first_name", f_name);
                    body.put("last_name", l_name);
                    body.put("email", email);
                    if (status.isOnlineWithToast()) {
                        authHttpRequestBuilder.SignUpPost(body);
                        Log.e(TAG, "attemptToSignUp: " + body );
                    }
                    Intent enterOtp = new Intent(SignUpActivity.this, EnterOTPActivity.class);
                    startActivity(enterOtp);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void signUpRequestInterval() {
        new CountDownTimer(5000L, 500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                signUpStatus = true;
            }
        }.start();
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mSignUpFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

