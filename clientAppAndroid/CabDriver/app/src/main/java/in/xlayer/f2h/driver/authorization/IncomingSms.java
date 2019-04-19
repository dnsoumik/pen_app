package in.xlayer.f2h.driver.authorization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.Objects;

import in.xlayer.f2h.driver.R;


/**
 * Created by dnsou on 10-01-2018.
 */

public class IncomingSms extends BroadcastReceiver {

    private String TAG = this.getClass().getSimpleName();

    public void onReceive(Context context, Intent intent) {
        AuthBroadCast autoBroadCast = new AuthBroadCast(context);
        final Bundle bundle = intent.getExtras();
        AuthLocalMemory authLocalMemory = new AuthLocalMemory(context);
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                assert pdusObj != null;
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    ArrayList<String> SENDER = new ArrayList<>();
                    SENDER.add("NJCABS");
//                    SENDER.add("HP-TRACKI");
                    //SENDER.add("001100"); //TODO use only for debug purpose
                    String mSender = currentMessage.getDisplayOriginatingAddress();
                    String DefMessage = context.getResources().getString(R.string.incoming_sms_format);
                    String mMessage = currentMessage.getDisplayMessageBody().split("s")[0] + "s";
                    String mOtp = currentMessage.getDisplayMessageBody().split("s")[1]
                            .replace(" ", "").replace(".", "");
                    Log.e(TAG, "onReceive: " + mSender + " " + mMessage + " " + mOtp);
                    //if((Objects.equals(SENDER.get(0), mSender) || Objects.equals(SENDER.get(1), mSender) || Objects.equals(SENDER.get(2), mSender))
                    if (Objects.equals(authLocalMemory.getAuthorizationTokenToken(), null)) {
                        if (mSender.contains(SENDER.get(0)) && mMessage.contains(DefMessage)) {
                            AuthStaticElements.AUTO_GEN_OTP = mOtp;
                            autoBroadCast.localBroadCastToEnterOtpActivity(10);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver " + e);
        }
    }

}