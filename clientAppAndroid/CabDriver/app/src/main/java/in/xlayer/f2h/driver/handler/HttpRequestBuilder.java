package in.xlayer.f2h.driver.handler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UnknownFormatConversionException;

import in.xlayer.f2h.driver.authorization.AuthLocalMemory;
import in.xlayer.f2h.driver.other.HttpResponseBuilder;
import in.xlayer.f2h.driver.other.booking.complete.BookingCompleteResponse;
import in.xlayer.f2h.driver.util.DeviceUtil;

import static android.widget.Toast.LENGTH_SHORT;

public class HttpRequestBuilder {

    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private DeviceUtil deviceUtil;
    private BroadcastHandler broadcastHandler;
    private LocalMemory localMemory;
    private AuthLocalMemory authLocalMemory;
    private RequestQueue requestQueue;
    private JSONObject bookingReq = null;

    public HttpRequestBuilder(Context context) {
        this.context = context;
        deviceUtil = new DeviceUtil(context);
        broadcastHandler = new BroadcastHandler(context);
        localMemory = new LocalMemory(context);
        authLocalMemory = new AuthLocalMemory(context);
        requestQueue = Volley.newRequestQueue(context);
    }

    private void addToRequestQueue(StringRequest jsonRequest) {
        if (deviceUtil.isOnline()) {
            requestQueue.add(jsonRequest);
        }
    }

    // [data] = 0
    public void getDriverInfo() {
        String URL = localMemory.getServerUrl() + "/api/driver_info?registration_id=" +
                localMemory.getFireBaseToken();
        addToRequestQueue(new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        HttpResponseBuilder responseBuilder = new Gson().fromJson(response,
                                HttpResponseBuilder.class);
                        if (responseBuilder.getRespCode()) {
                            localMemory.putString(AppConstants.DRIVER_INFO_RESPONSE, response);
                            broadcastHandler.localBroadCastToMainActivity(10);
                            try {
                                localMemory.putString(AppConstants.DRIVER_TRIP_ACTIVE_TRACKING_ID,
                                        localMemory.getFromDriverInfo()
                                                .getData().get(0).getActiveBookingId());
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // TODO:
                        }

                        if (responseBuilder.getErrorCode() == 3) {
                            // sign out
                            broadcastHandler.localBroadCastToMainActivity(70);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, "error", LENGTH_SHORT).show();
                try {
                    if (error != null) {
//                        sMemoryCall.putString(context.getResources().getString(R.string.http_request_error), String.valueOf(error.networkResponse.statusCode));
//                        sBroadcastCall.localBroadCastToMainAct(20);
                    } else {
//                        sMemoryCall.putString(context.getResources().getString(R.string.http_request_error), "");
//                        sBroadcastCall.localBroadCastToMainAct(20);
                    }
                } catch (NullPointerException | UnknownFormatConversionException | UndeclaredThrowableException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authLocalMemory.getAuthorizationTokenToken());
                return headers;
            }
        });
    }

//     [data] = 1
    public void getBookingRequests() {
        String URL = localMemory.getServerUrl() + "/api/booking_requests";
        addToRequestQueue(new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        HttpResponseBuilder responseBuilder = new Gson().fromJson(response,
                                HttpResponseBuilder.class);
                        localMemory.putString(AppConstants.BOOKING_REQUEST_RESPONSE, response);
                        broadcastHandler.localBroadCastToMainActivity(11);

                        if (responseBuilder.getErrorCode() == 3) {
                            // sign out
                            broadcastHandler.localBroadCastToMainActivity(70);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, "error", LENGTH_SHORT).show();
                try {
                    if (error != null) {
//                        sMemoryCall.putString(context.getResources().getString(R.string.http_request_error), String.valueOf(error.networkResponse.statusCode));
//                        sBroadcastCall.localBroadCastToMainAct(20);
                    } else {
//                        sMemoryCall.putString(context.getResources().getString(R.string.http_request_error), "");
//                        sBroadcastCall.localBroadCastToMainAct(20);
                    }
                } catch (NullPointerException | UnknownFormatConversionException | UndeclaredThrowableException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authLocalMemory.getAuthorizationTokenToken());
                return headers;
            }
        });
    }

    public void acceptRequest(String id) {
        try {
            String URL = localMemory.getServerUrl() + "/api/booking_requests";
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", id);
            addToRequestQueue(new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onResponse(String response) {
                            if (!response.isEmpty()) {
                                Log.e(TAG, "Accept onResponse: "+ response );
                                JSONObject resp = null;
                                try {
                                    JSONObject arr = StaticMemory.CURRENT_BOOKING_REQUEST;
                                    bookingReq = arr;
                                    resp = new JSONObject(response);
                                    if (resp.get("resp_code").toString() == "false"){
                                        deviceUtil.requestUnavailableToast();
                                        Log.e(TAG, "Accepting response timeout");
                                        JSONArray tmp = new JSONArray(localMemory.getString(
                                                AppConstants.
                                                        BOOKING_REQUEST_LOG));
                                        for (int i = 0; i < tmp.length(); i++){
                                            JSONObject obj = tmp.getJSONObject(i);
                                            if (Objects.equals(obj.getString("request_id"),
                                                    bookingReq.getString
                                                            ("request_id"))){
                                                obj.put("status", 0);
                                                tmp.put(i, obj);
                                            }
                                        }

                                        localMemory.putString(AppConstants.BOOKING_REQUEST_LOG,
                                                tmp.toString());
                                    }else{

//                                broadcast.localBroadcastToMainAct(60);
                                        getBookingRequests();
                                        //TODO
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Failed", LENGTH_SHORT).show();
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
                        return jsonBody.toString() == null ? null : jsonBody.toString().getBytes(
                                "utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s" +
                                " using %s", jsonBody.toString(), "utf-8");
                        return null;
                    }
                }

                @NonNull
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String jsonString = "";
                    try {
                        jsonString = new String(response.data, HttpHeaderParser.parseCharset(
                                response.headers));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(
                            response));
                }

                //This is for Headers If You Needed
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + authLocalMemory.
                            getAuthorizationTokenToken());
                    return headers;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void rejectRequest(String id) {
//        StaticMemory.REJECT_BOOKING_ID = id;
//        broadcastHandler.toBackgroundService(50);
        Log.e(TAG, "Reject onResponse: " + id);
        try {
//            JSONObject  resp = new JSONObject(id);
//            Log.e(TAG, "rejectRequest: "+ resp );
            Log.e(TAG, "Reject is not working fine " );
            String URL = localMemory.getServerUrl() + "/api/booking_requests";
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", id);
            addToRequestQueue(new StringRequest(Request.Method.PUT, URL,
                    new Response.Listener<String>() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (!response.isEmpty()) {
                                    Log.e(TAG, "Reject Response: "+ response );
                                    JSONObject resp = new JSONObject(response);
                                    JSONObject arr = StaticMemory.CURRENT_BOOKING_REQUEST;
                                    bookingReq = arr;
                                    if (resp.get("resp_code").toString() == "false" ||
                                            resp.toString() == ""){
                                        deviceUtil.requestUnavailableToast();
                                        Log.e(TAG, "Rejecting response timeout");
                                        JSONArray tmp = new JSONArray(localMemory.getString
                                                (AppConstants.BOOKING_REQUEST_LOG));
                                        for (int i = 0; i < tmp.length(); i++){
                                            JSONObject obj = tmp.getJSONObject(i);
                                            if (Objects.equals(obj.getString("request_id")
                                                    , bookingReq.getString
                                                            ("request_id"))){
                                                obj.put("status", 0);
                                                tmp.put(i, obj);
                                            }
                                        }

                                        localMemory.putString(AppConstants.BOOKING_REQUEST_LOG,
                                                tmp.toString());
                                    }else{

//                                broadcast.localBroadcastToMainAct(60);
                                        getBookingRequests();
                                        //TODO
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Failed", LENGTH_SHORT).show();
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
                        return jsonBody.toString() == null ? null : jsonBody.toString().
                                getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of " +
                                "%s using %s", jsonBody.toString(), "utf-8");
                        return null;
                    }
                }

                @NonNull
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String jsonString = "";
                    try {
                        jsonString = new String(response.data, HttpHeaderParser.parseCharset
                                (response.headers));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders
                            (response));
                }

                //This is for Headers If You Needed
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + authLocalMemory.
                            getAuthorizationTokenToken());
                    return headers;
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void driverAction(final JSONObject jsonBody) {
        String URL = localMemory.getServerUrl() + "/api/driver_action";
        addToRequestQueue(new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty()) {
                            HttpResponseBuilder responseBuilder = new Gson().fromJson(response, HttpResponseBuilder.class);
                            try {
                                if (responseBuilder.getRespCode()) {
                                    if (responseBuilder.getErrorCode() == 40) {
                                        localMemory.putString(AppConstants.DRIVER_TRIP_ACTIVE_TRACKING_ID, StaticMemory.SELECTED_BOOKING.getId());
                                        localMemory.putDouble(AppConstants.DRIVER_TRIP_ACTIVE_TRACKING_DISTANCE, 0);
                                        StaticMemory.SELECTED_BOOKING.setStatus("PROG");
                                        broadcastHandler.toBookingActionActivity(20);
                                    } else if (responseBuilder.getErrorCode() == 50) {
                                        StaticMemory.TRIP_COMPLETE_RESPONSE = new Gson().fromJson(response, BookingCompleteResponse.class);
                                        localMemory.putString(AppConstants.DRIVER_TRIP_ACTIVE_TRACKING_ID, "");
                                        localMemory.putDouble(AppConstants.DRIVER_TRIP_ACTIVE_TRACKING_DISTANCE, 0);
                                        StaticMemory.SELECTED_BOOKING.setStatus("COMPLETED");
                                        broadcastHandler.toBookingActionActivity(22);
                                    }
                                } else {
                                    Toast.makeText(context, responseBuilder.getRespMsg(), LENGTH_SHORT).show();
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Failed", LENGTH_SHORT).show();
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
            public byte[] getBody() {
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
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

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authLocalMemory.getAuthorizationTokenToken());
                return headers;
            }
        });
    }

    public void sendFeedBack(final JSONObject jsonBody) {
        String URL = localMemory.getServerUrl() + "/api/driver_feedback";
        addToRequestQueue(new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty()) {
                            HttpResponseBuilder responseBuilder = new Gson().fromJson(response, HttpResponseBuilder.class);
                            try {
                                if (responseBuilder.getRespCode()) {
                                    broadcastHandler.localBroadCastToBookingActivity(70);
                                    Toast.makeText(context, responseBuilder.getRespMsg(), LENGTH_SHORT).show();
                                    broadcastHandler.toBookingCompleteActivity(30);
                                } else {
                                    Toast.makeText(context, responseBuilder.getRespMsg(), LENGTH_SHORT).show();
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Failed", LENGTH_SHORT).show();
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
            public byte[] getBody() {
                return jsonBody.toString() == null ? null : jsonBody.toString().getBytes(StandardCharsets.UTF_8);
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

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authLocalMemory.getAuthorizationTokenToken());
                return headers;
            }
        });
    }

    public void postCashCollected(final JSONObject jsonBody) {
        String URL = localMemory.getServerUrl() + "/api/payment_received";
        addToRequestQueue(new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty()) {
                            HttpResponseBuilder responseBuilder = new Gson().fromJson(response, HttpResponseBuilder.class);
                            try {
                                if (responseBuilder.getRespCode()) {
                                    broadcastHandler.toBookingCompleteActivity(20);
                                    Toast.makeText(context, responseBuilder.getRespMsg(), LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, responseBuilder.getRespMsg(), LENGTH_SHORT).show();
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Failed", LENGTH_SHORT).show();
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
            public byte[] getBody() {
                return jsonBody.toString() == null ? null : jsonBody.toString().getBytes(StandardCharsets.UTF_8);
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

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authLocalMemory.getAuthorizationTokenToken());
                return headers;
            }
        });
    }

    public void postArrivalAlert(final JSONObject jsonBody) {
        String URL = localMemory.getServerUrl() + "/api/arrival_alert";
        addToRequestQueue(new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty()) {
                            HttpResponseBuilder responseBuilder = new Gson().fromJson(response, HttpResponseBuilder.class);
                            try {
                                if (responseBuilder.getRespCode()) {
                                    Toast.makeText(context, responseBuilder.getRespMsg(), LENGTH_SHORT).show();
                                    broadcastHandler.toBookingActionActivity(21);
                                } else {
                                    Toast.makeText(context, responseBuilder.getRespMsg(), LENGTH_SHORT).show();
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Failed", LENGTH_SHORT).show();
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
            public byte[] getBody() {
                return jsonBody.toString() == null ? null : jsonBody.toString().getBytes(StandardCharsets.UTF_8);
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

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authLocalMemory.getAuthorizationTokenToken());
                return headers;
            }
        });
    }

    public void getBookingsInfo() {
        String URL = localMemory.getServerUrl() + "/api/driver_bookings";
//        URL = "http://www.mocky.io/v2/5b24d59031000084006a7169";
//        URL = "http://www.mocky.io/v2/5b2cb2172f00003800ebd330";
        addToRequestQueue(new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            HttpResponseBuilder responseBuilder = new Gson().fromJson(response,
                                    HttpResponseBuilder.class);
                            localMemory.putString(AppConstants.YOUR_BOOKING_RESPONSE, response);
                            if (responseBuilder.getRespCode()) {
                                broadcastHandler.localBroadCastToBookingActivity(10);
                            } else {
                                broadcastHandler.localBroadCastToBookingActivity(20);
                            }
//                            if (responseBuilder.getErrorCode() == 3) {
//                                //TODO: sign out
//                                broadcastHandler.localBroadCastToMainActivity(20);
//                            }
                        } catch (NullPointerException | NumberFormatException | IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, "error", LENGTH_SHORT).show();
                try {
                    if (error != null) {
//                        sMemoryCall.putString(context.getResources().getString(R.string.http_request_error), String.valueOf(error.networkResponse.statusCode));
//                        sBroadcastCall.localBroadCastToMainAct(20);
                    } else {
//                        sMemoryCall.putString(context.getResources().getString(R.string.http_request_error), "");
//                        sBroadcastCall.localBroadCastToMainAct(20);
                    }
                } catch (NullPointerException | UnknownFormatConversionException | UndeclaredThrowableException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authLocalMemory.getAuthorizationTokenToken());
                return headers;
            }
        });
    }


    public void letSignOut() {
        try {
            String URL = localMemory.getServerUrl() + "/api/sign_out";
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("registration_id", localMemory.getFireBaseToken());
            addToRequestQueue(new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onResponse(String response) {
                            if (!response.isEmpty()) {
//                                broadcast.localBroadcastToMainAct(60);
                                //sign out
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Failed to Sign Out", LENGTH_SHORT).show();
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
                public byte[] getBody() {
                    return jsonBody.toString() == null ? null : jsonBody.toString().getBytes(StandardCharsets.UTF_8);
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

                //This is for Headers If You Needed
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + authLocalMemory.getAuthorizationTokenToken());
                    return headers;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postMyLocationToServer(final JSONObject body) {
        String URL = localMemory.getServerUrl() + "/loc_api/new_location";
        addToRequestQueue(new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty()) {
                            HttpResponseBuilder responseBuilder = new Gson().fromJson(response,
                                    HttpResponseBuilder.class);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, "Failed to update location", LENGTH_SHORT).show();
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
            public byte[] getBody() {
                return body.toString() == null ? null : body.toString().getBytes(StandardCharsets.UTF_8);
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

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authLocalMemory.getAuthorizationTokenToken());
                return headers;
            }
        });

    }

}
