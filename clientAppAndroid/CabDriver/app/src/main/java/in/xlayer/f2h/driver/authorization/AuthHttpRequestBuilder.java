package in.xlayer.f2h.driver.authorization;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import in.xlayer.f2h.driver.handler.LocalMemory;

public class AuthHttpRequestBuilder {
    private String TAG = AuthHttpRequestBuilder.class.getSimpleName();
    private AuthBroadCast broadCast;
    private AuthDeviceStatus status;
    private LocalMemory localMemory;
    private AuthLocalMemory authLocalMemory;
    private RequestQueue requestQueue;
    private int count = 0;

    public AuthHttpRequestBuilder(Context context) {
        broadCast = new AuthBroadCast(context);
        status = new AuthDeviceStatus(context);
        localMemory = new LocalMemory(context);
        authLocalMemory = new AuthLocalMemory(context);
        requestQueue = Volley.newRequestQueue(context);
    }

    private void addToRequestQueue(StringRequest jsonRequest) {
        if (status.isOnlineWithToast()) {
            requestQueue.add(jsonRequest);
        }
    }

    void SignInPost(final JSONObject body) {
        String URL = localMemory.getServerUrl() + "/web/api/sign_in";
        addToRequestQueue(new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(TAG, "onSignInResponse : " + response );
                            AuthHttpResponseBuilder httpResp = new Gson().fromJson(response, AuthHttpResponseBuilder.class);
                            Log.e("RESPONSE", "httpResp");
                            if (httpResp.getStatus()) {
                                Log.e("RESPONSE", "httpResp1");
                                AuthStaticElements.SIGN_IN_RESPONSE = httpResp;
                            } else {
                                Log.e("RESPONSE", "httpResp2");
                                AuthStaticElements.SIGN_IN_RESPONSE = httpResp;
                            }
                            Log.e("RESPONSE", "httpResp3");
                            broadCast.localBroadCastToSignIn(1);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(context, "error", LENGTH_SHORT).show();
//                if (parent) {
//                    sBroadcastCall.disableRegisterLoading();
//                } else {
//                    sBroadcastCall.disableEnterOtpLoading();
//                }
//                try {
//                    broadCast.localBroadCastToSignIn(0);
//                    AuthStaticElements.SIGN_IN_RESPONSE = null;
//                    Toast.makeText(context, error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
            }
        }) {
            @NonNull
            @Contract(pure = true)
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Nullable
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return body.toString() == null ? null : body.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", body.toString(), "utf-8");
                    return null;
                }
            }

            @NonNull
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String jsonString = "";
                try {
                    jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
            }
        });
    }

    void verifyOtpPost(final JSONObject body) throws JSONException {
        String URL = "https://f2h.trakiga.com/web/api/sign_in";
        addToRequestQueue(new StringRequest(Request.Method.PUT, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(TAG, "onEnterOTPResponse : " +response );
                            AuthHttpResponseBuilder httpResp = new Gson().fromJson(response, AuthHttpResponseBuilder.class);
                            if (httpResp.getStatus()) {
                                authLocalMemory.storeAuthorizationToken(httpResp.getResult().get(0).toString());
                                AuthStaticElements.OTP_RESPONSE = httpResp;
                            } else {
                                AuthStaticElements.OTP_RESPONSE = httpResp;
                            }
                            broadCast.localBroadCastToEnterOtpActivity(1);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(context, "error", LENGTH_SHORT).show();
//                if (parent) {
//                    sBroadcastCall.disableRegisterLoading();
//                } else {
//                    sBroadcastCall.disableEnterOtpLoading();
//                }
//                try {
//                    broadCast.localBroadCastToSignIn(0);
//                    AuthStaticElements.SIGN_IN_RESPONSE = null;
//                    Toast.makeText(context, error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
            }
        }) {
            @NonNull
            @Contract(pure = true)
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Nullable
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return body.toString() == null ? null : body.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", body.toString(), "utf-8");
                    return null;
                }
            }

            @NonNull
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String jsonString = "";
                try {
                    jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
            }
        });
    }

    void resendOtpPost(final JSONObject body) {
        String URL = "https://f2h.trakiga.com/web/api/sign_in";
        addToRequestQueue(new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            AuthHttpResponseBuilder httpResp = new Gson().fromJson(response, AuthHttpResponseBuilder.class);
                            if (httpResp.getStatus()) {
                                AuthStaticElements.OTP_RESPONSE = httpResp;
                            } else {
                                AuthStaticElements.OTP_RESPONSE = httpResp;
                            }
                            broadCast.localBroadCastToEnterOtpActivity(2);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(context, "error", LENGTH_SHORT).show();
//                if (parent) {
//                    sBroadcastCall.disableRegisterLoading();
//                } else {
//                    sBroadcastCall.disableEnterOtpLoading();
//                }
//                try {
//                    broadCast.localBroadCastToSignIn(0);
//                    AuthStaticElements.SIGN_IN_RESPONSE = null;
//                    Toast.makeText(context, error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
            }
        }) {
            @NonNull
            @Contract(pure = true)
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Nullable
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return body.toString() == null ? null : body.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", body.toString(), "utf-8");
                    return null;
                }
            }

            @NonNull
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String jsonString = "";
                try {
                    jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
            }
        });
    }

    void SignUpPost(final JSONObject body) throws JSONException {
        String URL = "https://f2h.trakiga.com/web/api/sign_up";
        addToRequestQueue(new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(TAG, "onRegisterResponse : " +response );
                            AuthHttpResponseBuilder httpResp = new Gson().fromJson(response, AuthHttpResponseBuilder.class);
                            if (httpResp.getStatus()) {
                                Log.e(TAG, "onRegisterResponse :--> " +httpResp );
                                AuthStaticElements.SIGN_UP_RESPONSE = httpResp;
                            } else {
                                AuthStaticElements.SIGN_UP_RESPONSE = httpResp;
                            }
                            broadCast.localBroadCastToSignUp(1);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(context, "error", LENGTH_SHORT).show();
//                if (parent) {
//                    sBroadcastCall.disableRegisterLoading();
//                } else {
//                    sBroadcastCall.disableEnterOtpLoading();
//                }
//                try {
//                    broadCast.localBroadCastToSignIn(0);
//                    AuthStaticElements.SIGN_IN_RESPONSE = null;
//                    Toast.makeText(context, error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
            }
        }) {
            @NonNull
            @Contract(pure = true)
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Nullable
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return body.toString() == null ? null : body.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", body.toString(), "utf-8");
                    return null;
                }
            }

            @NonNull
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String jsonString = "";
                try {
                    jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
            }
        });
    }

}
