package in.xlayer.f2h.driver.util;

import android.webkit.URLUtil;

public class ValidateUtil {
    public static boolean isValidWebsiteUrl(String url) {
        return URLUtil.isHttpUrl(url.toLowerCase()) || URLUtil.isHttpsUrl(url.toLowerCase());
    }
}
