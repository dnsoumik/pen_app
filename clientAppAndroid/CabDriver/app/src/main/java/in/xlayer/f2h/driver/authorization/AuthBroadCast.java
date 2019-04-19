package in.xlayer.f2h.driver.authorization;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import in.xlayer.f2h.driver.R;


public class AuthBroadCast {

    private Context context = null;
    private Intent signInActFilter, signUpActFilter, enterOtpActFilter;
    private String signInActTag, signUpActTag, enterOtpActTag;

    AuthBroadCast(Context context) {
        this.context = context;

        signInActFilter = new Intent(context.getResources().getString(R.string.bd_sign_in_act_filter));
        signInActTag = context.getResources().getString(R.string.bd_sign_in_act_tag);

        signUpActFilter = new Intent(context.getResources().getString(R.string.bd_sign_up_act_filter));
        signUpActTag = context.getResources().getString(R.string.bd_sign_up_act_tag);

        enterOtpActFilter = new Intent(context.getResources().getString(R.string.bd_enter_otp_act_filter));
        enterOtpActTag = context.getResources().getString(R.string.bd_enter_otp_act_tag);
    }

    // SignIn activity
    public void localBroadCastToSignIn(int type) {
        signInActFilter.putExtra(signInActTag, type);
        LocalBroadcastManager.getInstance(context).sendBroadcast(signInActFilter);
    }

    // SignUp activity
    public void localBroadCastToSignUp(int type) {
        signUpActFilter.putExtra(signUpActTag, type);
        LocalBroadcastManager.getInstance(context).sendBroadcast(signUpActFilter);
    }

    // Enter Otp Activity
    public void localBroadCastToEnterOtpActivity(int type) {
        enterOtpActFilter.putExtra(enterOtpActTag, type);
        LocalBroadcastManager.getInstance(context).sendBroadcast(enterOtpActFilter);
    }

}
