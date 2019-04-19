package in.xlayer.f2h.driver.dialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.gson.JsonObject;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import in.xlayer.f2h.driver.BuildConfig;
import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.SharedPreferenceManagement;
import in.xlayer.f2h.driver.activity.BookingsActivity;
import in.xlayer.f2h.driver.activity.MainActivity;
import in.xlayer.f2h.driver.authorization.AuthHttpResponseBuilder;
import in.xlayer.f2h.driver.authorization.AuthStaticElements;
import in.xlayer.f2h.driver.handler.AppConstants;
import in.xlayer.f2h.driver.handler.BroadcastHandler;
import in.xlayer.f2h.driver.handler.HttpRequestBuilder;
import in.xlayer.f2h.driver.handler.LocalMemory;
import in.xlayer.f2h.driver.handler.StaticMemory;
import in.xlayer.f2h.driver.other.booking.response.Datum;
import in.xlayer.f2h.driver.util.TimeUtil;

public class BookingRequestDialog extends FragmentActivity {
    Button accept_button, reject_button;
    private HttpRequestBuilder httpRequestBuilder;
    private LocalMemory localMemory;
    private JSONObject bookingReq = null;
    private BroadcastHandler broadcastHandler;
    private SharedPreferenceManagement sharedPreferenceManager;
    TextView time, price, start_location;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StaticMemory.BOOKING_REQUEST_DIALOG_STATUS = 1;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_booking_request);
        localMemory = new LocalMemory(this);
        httpRequestBuilder = new HttpRequestBuilder(this);
        this.broadcastHandler = new BroadcastHandler(this);
        final Datum info = new Datum();
        sharedPreferenceManager = new SharedPreferenceManagement(getApplicationContext());

        time = findViewById(R.id.booking_time);
        price = findViewById(R.id.booking_price);
        start_location = findViewById(R.id.request_satrting_loc);
        accept_button = findViewById(R.id.request_accept);
        reject_button = findViewById(R.id.request_reject);

        try {

            JSONObject arr = StaticMemory.CURRENT_BOOKING_REQUEST;
            bookingReq = arr;
            Log.e("TAG", "Data retrieving" + arr );
            price.setText( "Rs. " + arr.getJSONArray("estimate").getJSONObject(0).
                    get("price").toString());
            start_location.setText(arr.getJSONArray("start_point").getJSONObject(0).
                    get("addr").toString());
            if (arr.getInt("time") > 0) {
                time.setText(TimeUtil.parseStampFromUnixToLocalWithAMPM(arr.getInt
                        ("time")));
            } else {
                time.setText("N/A");
            }
        }catch (JSONException | NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            Log.e("TAG", "JSON Error" );
        }

        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "On Accept Button on click " );
//                try {
//                    JSONArray requestArray = new JSONArray(localMemory.getString(AppConstants.
//                            BOOKING_REQUEST_LOG));
//                    for (int i = 0; i < requestArray.length(); i++){
//                        JSONObject obj = requestArray.getJSONObject(i);
//                        if (Objects.equals(obj.getString("request_id"), bookingReq.getString
//                                ("request_id"))){
//                            obj.put("status", 1);
//                            requestArray.put(i, obj);
//                        }
//                    }
//
//                    localMemory.putString(AppConstants.BOOKING_REQUEST_LOG,
//                            requestArray.toString());
//                    httpRequestBuilder.acceptRequest(bookingReq.getString("request_id"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                broadcastHandler.toBackgroundService(3);
//                if (!StaticMemory.BOOKINGS_ACTIVITY ){
//                    Log.e("BOOKINGS_ACTIVITY", "BOOKINGS_ACTIVITY is true ");
//                    Intent bookingsActivity = new Intent(BookingRequestDialog.this,
//                            BookingsActivity.class);
//                    startActivity(bookingsActivity);
//                    finish();
//                }else{
//                    Log.e("BOOKINGS_ACTIVITY", "BOOKINGS_ACTIVITY is false ");
////                    Intent bookingsActivity = new Intent(BookingRequestDialog.this,
////                            BookingsActivity.class);
////                    startActivity(bookingsActivity);
//                    finish();
//                }

                String URL = "https://f2h.trakiga.com/web/api/order_status";

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("OrderResponse", response);
                                try {
                                    JSONObject res = new JSONObject(response);
                                    if (res.getBoolean("status")){
                                        sharedPreferenceManager.setOrderTripStarted();
                                        Intent MainActivity = new Intent(BookingRequestDialog.this, in.xlayer.f2h.driver.activity.MainActivity.class);
                                        startActivity(MainActivity);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
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
                        byte[] requestBody = new byte[1024];
                        JSONObject request = new JSONObject();
                        try {
                            request.put("order_id", sharedPreferenceManager.getActiveOrderId());
                            request.put("application_id", BuildConfig.APPLICATION_ID);
                            request.put("status", "Start Trip");
                            requestBody = request.toString().getBytes("utf-8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            return requestBody;
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
                };
                requestQueue.add(stringRequest);
            }
        });

        reject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcastHandler.toBackgroundService(3);
                Log.e("TAG", "On Reject Button on click " );
                try {
                    JSONArray requestArray = new JSONArray(localMemory.getString(AppConstants.
                            BOOKING_REQUEST_LOG));
                    for (int i = 0; i < requestArray.length(); i++){
                        JSONObject obj = requestArray.getJSONObject(i);
                        if (Objects.equals(obj.getString("request_id"), bookingReq.getString
                                ("request_id"))){
                            obj.put("status", 2);
                            requestArray.put(i, obj);
                        }
                    }

                    localMemory.putString(AppConstants.BOOKING_REQUEST_LOG,
                            requestArray.toString());
                    httpRequestBuilder.rejectRequest(bookingReq.getString("request_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent main = new Intent(BookingRequestDialog.this,
                        MainActivity.class);
                startActivity(main);
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
