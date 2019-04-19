package in.xlayer.f2h.driver.authorization;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthLocalMemory {

    private Context context;

    private SharedPreferences mShare;

    public AuthLocalMemory(Context context) {
        this.context = context;
        mShare = context.getSharedPreferences(AuthStaticElements.APP_SIGN_IN_CACHE_MEMORY_TAG, Context.MODE_PRIVATE);
    }

    public String getString(String Key) {
        return mShare.getString(Key, "");
    }

    public int getInt(String Key) {
        return mShare.getInt(Key, -1);
    }

    public void storeAuthorizationToken(String token) {
        SharedPreferences.Editor mEditor = mShare.edit();
        mEditor.putString(AuthStaticElements.SIGN_IN_KEY, token);
        mEditor.apply();
    }

    public String getAuthorizationTokenToken() throws NullPointerException {
        return mShare.getString(AuthStaticElements.SIGN_IN_KEY, null);
    }

}
