package in.xlayer.f2h.driver.authorization;


import in.xlayer.f2h.driver.BuildConfig;

public class AuthStaticElements {
    // cache memory constants
    public static final String APP_SIGN_IN_CACHE_MEMORY_TAG = BuildConfig.APPLICATION_ID + "$GET#APPLICATION#CACHE#DATA";
    // sign in key
    public static final String SIGN_IN_KEY = APP_SIGN_IN_CACHE_MEMORY_TAG + "$SET$REG#KEY";
    public static AuthHttpResponseBuilder SIGN_IN_RESPONSE = null;
    public static AuthHttpResponseBuilder SIGN_UP_RESPONSE = null;
    public static AuthHttpResponseBuilder OTP_RESPONSE = null;
    public static String OTP_ACTIVITY_PARENT = SignInActivity.class.getName();
    public static String AUTH_MOBILE_NUMBER = "";
    public static String COUNTRY_CODE = "";
    public static String AUTO_GEN_OTP = "";

}
