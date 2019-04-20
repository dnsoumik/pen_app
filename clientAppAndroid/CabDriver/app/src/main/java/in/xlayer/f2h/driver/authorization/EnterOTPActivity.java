package in.xlayer.f2h.driver.authorization;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
import in.xlayer.f2h.driver.activity.MainActivity;


public class EnterOTPActivity extends AppCompatActivity {

    int timer_count = 31;
    private String TAG = EnterOTPActivity.class.getSimpleName();
    //    private AuthLocalMemory localMemory = null;
    private AuthDeviceStatus status = null;
    private AuthHttpRequestBuilder authHttpRequestBuilder;
    private View mEnterOtpView, mSignInSuccessView, mProgressView;
    private EditText entered_otp,country_code;
    private TextView resend_timer;
    private int otp_count = 1;
    private boolean isResend_sms = true;
    private AlertDialog.Builder resendSmsAlert;
    private BroadcastReceiver localBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle switchType = intent.getExtras();
            assert switchType != null;
            if (!EnterOTPActivity.this.isDestroyed() && !EnterOTPActivity.this.isFinishing()) {
                Log.i(TAG + "BroadcastHandler: ", "onReceive: " + switchType.getInt(getResources().getString(R.string.bd_enter_otp_act_tag)));
                switch (switchType.getInt(getResources().getString(R.string.bd_enter_otp_act_tag))) {
                    case 0:
                        showProgress(false);
                        break;
                    case 1:
                        showProgress(false);
                        if (AuthStaticElements.OTP_RESPONSE != null) {
                            if (AuthStaticElements.OTP_RESPONSE.getStatus()) {
                                showSignInSuccess(true);
                                signInSuccessfulTimer();
                            } else {
                                entered_otp.setError(AuthStaticElements.OTP_RESPONSE.getMessage());
                                entered_otp.requestFocus();
                            }
                        }
                        break;
                    case 2:
                        showProgress(false);
                        if (AuthStaticElements.OTP_RESPONSE != null) {
                            if (AuthStaticElements.OTP_RESPONSE.getStatus()) {
                                resendRequest();
                                otp_count++;
                            } else {
                                Toast.makeText(EnterOTPActivity.this, AuthStaticElements.OTP_RESPONSE.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case 10:
                        // auto gen otp
                        showProgress(true);
                        JSONObject body = new JSONObject();
                        try {
                            body.put("application_id", BuildConfig.APPLICATION_ID);
                            body.put("phone_number", AuthStaticElements.AUTH_MOBILE_NUMBER);
                            body.put("country_code", 91);
                            body.put("otp", AuthStaticElements.AUTO_GEN_OTP);
                            if (status.isOnline()) {
                                authHttpRequestBuilder.verifyOtpPost(body);
                            } else {
                                Toast.makeText(EnterOTPActivity.this, "Auto Sign In Failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

//        localMemory = new AuthLocalMemory(this);
        status = new AuthDeviceStatus(this);
        authHttpRequestBuilder = new AuthHttpRequestBuilder(this);

        LocalBroadcastManager.getInstance(EnterOTPActivity.this).registerReceiver(localBroadCastReceiver,
                new IntentFilter(getResources().getString(R.string.bd_enter_otp_act_filter)));

        country_code = findViewById(R.id.reg_country_code);
        mEnterOtpView = findViewById(R.id.enter_otp_form);
        mProgressView = findViewById(R.id.login_progress);
        mSignInSuccessView = findViewById(R.id.sign_in_successful_view);

        final TextView mobile_number = findViewById(R.id.phoneNumView);
        resend_timer = findViewById(R.id.resend_timer);
        mobile_number.setText(AuthStaticElements.COUNTRY_CODE + " " + AuthStaticElements.AUTH_MOBILE_NUMBER);
        TextView wrong_number = findViewById(R.id.wrongNum);
        wrong_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        entered_otp = findViewById(R.id.enterOTP);
        entered_otp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(entered_otp.getWindowToken(), 0);
                    attemptToVerify();
                    return true;
                }
                return false;
            }
        });
        entered_otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Objects.equals(entered_otp.length(), 6)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(entered_otp.getWindowToken(), 0);
                }
            }
        });
        Button verify_button = findViewById(R.id.otp_verify_button);
        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(entered_otp.getWindowToken(), 0);
                attemptToVerify();
            }
        });
        TextView resendSmsView = findViewById(R.id.resendSMS);
        resendSmsAlert = new AlertDialog.Builder(this);
        resendSmsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String tmp = getResources().getString(R.string.resend_alert_message) + "\n\n"
                        + mobile_number.getText().toString();
                if (isResend_sms) {
                    resendSmsAlert.setMessage(tmp).
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (otp_count < 6) {
                                        try {
                                            JSONObject body = new JSONObject();
                                            body.put("phone_number", AuthStaticElements.AUTH_MOBILE_NUMBER);
                                            Log.i(TAG, "onClick: " + body.toString());
                                            authHttpRequestBuilder.resendOtpPost(body);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.too_many_attempts),
                                                Toast.LENGTH_LONG).show();
                                        onBackPressed();
                                    }
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    AlertDialog alert = resendSmsAlert.create();
                    alert.show();
                }
            }
        });
        showSignInSuccess(false);
    }

    private void resendRequest() {
        isResend_sms = false;
        new CountDownTimer(30000L, 1000L) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timer_count--;
                resend_timer.setText(" ( " + timer_count + " ) ");
            }

            @Override
            public void onFinish() {
                resend_timer.setText("");
                isResend_sms = true;
            }
        }.start();
    }

    void signInSuccessfulTimer() {
        new CountDownTimer(2500L, 500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent mainIntent = new Intent(EnterOTPActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }.start();
    }

    private void attemptToVerify() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(entered_otp.getWindowToken(), 0);

        // Reset errors.
        entered_otp.setError(null);
// Store values at the time of the login attempt.
//

    boolean cancel = false;
    View focusView = null;

    if (entered_otp.getText().toString() == "") {
        entered_otp.setError("This field is required");
        focusView = entered_otp;
        cancel = true;
    }
//        Integer otp = Integer.valueOf(entered_otp.getText().toString());
    if (entered_otp.getText().toString() == null) {
        entered_otp.setError("Otp is not valid");
        focusView = entered_otp;
        cancel = true;
    }

    if (cancel) {
        // There was an error; don't attempt login and focus the first
        // form field with an error.
        focusView.requestFocus();
    } else {
        // Show a progress spinner, and kick off a background task to
        // perform the user sign in attempt.
        showProgress(true);
        JSONObject body = new JSONObject();
        try {
            Log.e(TAG, "phone_number: " + AuthStaticElements.AUTH_MOBILE_NUMBER);
            body.put("application_id", BuildConfig.APPLICATION_ID);
            body.put("phone_number", 91 + AuthStaticElements.AUTH_MOBILE_NUMBER);
//            body.put("country_code", 91);
            body.put("otp", entered_otp.getText().toString());
            Log.e(TAG, "attemptToVerify: " + body);
            if (status.isOnlineWithToast()) {
                authHttpRequestBuilder.verifyOtpPost(body);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
            if (Objects.equals(SignInActivity.class.getName(), AuthStaticElements.OTP_ACTIVITY_PARENT)) {
                closeIntent = new Intent(EnterOTPActivity.this, SignInActivity.class);
                startActivity(closeIntent);
                finish();
            } else if (Objects.equals(SignUpActivity.class.getName(), AuthStaticElements.OTP_ACTIVITY_PARENT)) {
                closeIntent = new Intent(EnterOTPActivity.this, SignUpActivity.class);
                startActivity(closeIntent);
                finish();
            } else {
                finish();
            }
        }
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mEnterOtpView.setVisibility(show ? View.GONE : View.VISIBLE);
        mEnterOtpView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mEnterOtpView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void showSignInSuccess(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mEnterOtpView.setVisibility(show ? View.GONE : View.VISIBLE);
        mEnterOtpView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mEnterOtpView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mSignInSuccessView.setVisibility(show ? View.VISIBLE : View.GONE);
        mSignInSuccessView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSignInSuccessView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

}
