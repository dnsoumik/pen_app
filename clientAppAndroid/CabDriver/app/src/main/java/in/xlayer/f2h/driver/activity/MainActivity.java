package in.xlayer.f2h.driver.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import in.xlayer.f2h.driver.BuildConfig;
import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.SharedPreferenceManagement;
import in.xlayer.f2h.driver.adapter.BookingRequestAdapter;
import in.xlayer.f2h.driver.adapter.RequestAdapter;
import in.xlayer.f2h.driver.authorization.AuthStaticElements;
import in.xlayer.f2h.driver.authorization.SignInActivity;
import in.xlayer.f2h.driver.dialog.BookingRequestDialog;
import in.xlayer.f2h.driver.handler.AppConstants;
import in.xlayer.f2h.driver.handler.BroadcastHandler;
import in.xlayer.f2h.driver.handler.HttpRequestBuilder;
import in.xlayer.f2h.driver.handler.LocalMemory;
import in.xlayer.f2h.driver.handler.StaticMemory;
import in.xlayer.f2h.driver.other.PicassoCircleTransformation;
import in.xlayer.f2h.driver.service.BackgroundService;
import in.xlayer.f2h.driver.util.DeviceUtil;
import in.xlayer.f2h.driver.ws.request.WSBookingRequest;

import static android.view.View.GONE;
import static android.widget.Toast.LENGTH_SHORT;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = this.getClass().getSimpleName();
    public SharedPreferenceManagement sharedPreferenceManager;
    SharedPreferences authSharedPreference;
    CardView orderDetailsCard;
    TextView orderStatusText, NoBookingText;
    private LocalMemory localMemory;
    private BroadcastHandler broadcastHandler;
    private DeviceUtil deviceUtil;
    private HttpRequestBuilder httpRequestBuilder;
    private View bookingEmpty;
    private Button ActionButton;
    private ListView orderItemsList;

    /**
     * Order Card related text views
     * */
    private TextView deliveryTime, orderIdText, customerName, customerPhone, farmerName, farmerPhone;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private BroadcastReceiver localBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle intentBundle = intent.getExtras();
            assert intentBundle != null;
            if (!MainActivity.this.isDestroyed() && !MainActivity.this.isFinishing()) {
                Log.e(TAG + "BroadcastHandler: ", "onReceive: " +
                        intentBundle.getInt(MainActivity.class.getName() + BroadcastHandler.
                                class.getSimpleName()));
                switch (intentBundle.getInt(MainActivity.class.getName() + BroadcastHandler.
                        class.getSimpleName())) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 10:
                        // driver info
                        loadNavigationDrawer();
                        break;
                    case 11:
                        // booking requests
                        loadBookingRequests();
                        break;
                    case 40:
                        Intent book_intent = new Intent(MainActivity.this,
                                BookingsActivity.class);
                        startActivity(book_intent);
                        break;
                    case 70:
                        if (!MainActivity.this.isFinishing()) {
                            localMemory.clearAllUserData();
                            List<ApplicationInfo> packages;
                            PackageManager pm;
                            pm = getPackageManager();
                            packages = pm.getInstalledApplications(0);

                            ActivityManager mActivityManager = (ActivityManager) context.
                                    getSystemService(Context.ACTIVITY_SERVICE);

                            for (ApplicationInfo packageInfo : packages) {
                                if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                                    continue;
                                if (packageInfo.packageName.equals(getApplicationContext().
                                        getPackageName()))
                                    continue;
                                assert mActivityManager != null;
//                                mActivityManager.killBackgroundProcesses(packageInfo.packageName);
                            }
                            Intent intent1 = new Intent(MainActivity.this,
                                    SignInActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                        break;
                    case 90:
                        finish();
                        break;
                }
            }
        }
    };
    private int gIndex;

    @SuppressLint("SetTextI18n")
    private void loadBookingRequests() {
        try {
            List<WSBookingRequest> reList = StaticMemory.NEW_BOOKING_REQUEST;
            RecyclerView rideList = findViewById(R.id.booking_request_list);
            LinearLayout empty = findViewById(R.id.request_empty);
            // TODO: need to fix
            if (reList.size() > 0) {
                rideList.setVisibility(View.VISIBLE);
                LinearLayoutManager llm = new LinearLayoutManager(
                        MainActivity.this,
                        LinearLayoutManager.VERTICAL, false);
                rideList.setLayoutManager(llm);
                rideList.setAdapter(new BookingRequestAdapter(
                        MainActivity.this, reList));
            } else {
                empty.setVisibility(View.VISIBLE);
                rideList.setVisibility(GONE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferenceManager = new SharedPreferenceManagement(getApplicationContext());
        authSharedPreference = getSharedPreferences(AuthStaticElements.APP_SIGN_IN_CACHE_MEMORY_TAG, MODE_PRIVATE);


//        bookingEmpty = findViewById(R.id.request_empty);
        ActionButton = findViewById(R.id.btn_change_order_status);
        orderStatusText = findViewById(R.id.current_order_status_tv);
        orderDetailsCard = findViewById(R.id.order_details_card);
        bookingEmpty = findViewById(R.id.booking_resp);
        deliveryTime = findViewById(R.id.delivery_time);
        orderIdText = findViewById(R.id.order_id);
        customerName = findViewById(R.id.customer_name);
        customerPhone = findViewById(R.id.customer_phone);
        farmerName = findViewById(R.id.farmer_name);
        farmerPhone = findViewById(R.id.farmer_phone);
        orderItemsList = findViewById(R.id.order_contents_list_view);

        ActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://f2h.trakiga.com/web/api/order_status";
                RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("OrderResponse", response);
                                try {
                                    JSONObject res = new JSONObject(response);
                                    if (res.getBoolean("status")) {
                                        switch (sharedPreferenceManager.getActiveOrderStatus()) {
                                            case SharedPreferenceManagement.ORDER_TRIP_STARTED_TEXT:
                                                sharedPreferenceManager.setOrderPickedUp();
                                                ActionButton.setText("Drop Order");
                                                orderStatusText.setText("Order Picked Up");
                                                orderDetailsCard.setVisibility(View.VISIBLE);
                                                break;
                                            case SharedPreferenceManagement.ORDER_PICKEDUP_TEXT:
                                                sharedPreferenceManager.setOrderDeliver();
                                                sharedPreferenceManager.clearActiveOrder();
                                                orderDetailsCard.setVisibility(GONE);
                                                bookingEmpty.setVisibility(View.VISIBLE);
                                                break;
                                            default:
                                                Log.e("Order State", " Not Reachable");
                                        }

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
                            request.put("status", sharedPreferenceManager.getNextOrderState());
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

                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer "
                                + authSharedPreference.getString(AuthStaticElements.SIGN_IN_KEY, ""));
                        return headers;
                    }
                };
                rq.add(stringRequest);
            }
        });

        StaticMemory.MAIN_ACTIVITY_STATUS = 1;
        localMemory = new LocalMemory(getApplicationContext());
        broadcastHandler = new BroadcastHandler(getApplicationContext());
        deviceUtil = new DeviceUtil(getApplicationContext());
        httpRequestBuilder = new HttpRequestBuilder(getApplicationContext());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(localBroadCastReceiver,
                new IntentFilter(MainActivity.class.getName()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        if(sharedPreferenceManager.hasActiveOrder()){
            /**Query the active order*/
            Log.e("ACTIVE-ORDER", "TRUE");



            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final Context context = MainActivity.this;
            String URL = localMemory.getServerUrl() + "/web/api/user_info?order_id=" + sharedPreferenceManager.getActiveOrderId();
            requestQueue.add(new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onResponse(String response) {
                            if (!response.isEmpty()) {
                                try{
                                    JSONObject order_info = new JSONObject(response);
                                    if(order_info.getBoolean("status")){

                                        JSONArray order = order_info.getJSONArray("result");

                                        JSONObject orderComponents = order.getJSONObject(0);

                                        /**
                                         * Print order components to their respective TextViews
                                         * */

                                        /**
                                         * Delivery Time
                                         * */

                                        deliveryTime.setText(new java.util.Date((long)orderComponents.getDouble("delivery_time")*1000).toString());
                                        orderIdText.setText(sharedPreferenceManager.getActiveOrderId());

                                        /**
                                         * Invoice Details
                                         * */

                                        JSONObject invoiceDetails = orderComponents.getJSONObject("inv_info");

                                        List<String> orderContents = new ArrayList<>();

                                        JSONArray itemList = invoiceDetails.getJSONArray("items");

                                        for(int i = 0; i < itemList.length(); i++){
                                            JSONObject item = itemList.getJSONObject(i);

                                            String itemText = "Item: " + item.getString("title")
                                                    + "\nQuantity: " + Integer.toString(item.getInt("cost"))
                                                    + "\nDescription: " + item.getString("desc") + "\n\n";

                                            orderContents.add(itemText);
                                        }

                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                getApplicationContext(),
                                                android.R.layout.simple_list_item_1,
                                                orderContents );
                                        orderItemsList.setAdapter(adapter);

                                        Log.e("Invoice Details", invoiceDetails.toString());

                                        /**
                                         * User Details
                                         * */

                                        JSONObject userDetails = orderComponents.getJSONObject("user_info");

                                        customerName.setText(userDetails.getString("first_name") + " " + userDetails.getString("last_name"));
                                        customerPhone.setText(userDetails.getString("phone_number"));

                                        Log.e("User Details", userDetails.toString());


                                        /**
                                         * Farmer Details
                                         * */

                                        JSONObject farmerDetails = orderComponents.getJSONObject("farmer_info");

                                        farmerName.setText(farmerDetails.getString("first_name") + " " + farmerDetails.getString("last_name"));
                                        farmerPhone.setText(farmerDetails.getString("phone_number"));

                                        Log.e("Farmer Details", farmerDetails.toString());

                                        /**
                                         * Finally hide the "No Orders Available" text and show the Order Card
                                         * */

                                        bookingEmpty.setVisibility(GONE);
                                        orderDetailsCard.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                            Log.e("ORDER_RESPONSE", response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Failed", LENGTH_SHORT).show();
                }
            }) {

                //This is for Headers If You Needed
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + authSharedPreference.getString(AuthStaticElements.SIGN_IN_KEY, ""));
                    return headers;
                }
            });

        }else{
            bookingEmpty.setVisibility(View.VISIBLE);
            orderDetailsCard.setVisibility(GONE);
        }


    }


    private void loadBookingRequest() {
        try {
            RecyclerView rideList = findViewById(R.id.booking_request_list);
            LinearLayoutManager llm = new LinearLayoutManager(
                    MainActivity.this, LinearLayoutManager.VERTICAL,
                    true);
            llm.setStackFromEnd(true);
            rideList.setLayoutManager(llm);
            String reqLog = localMemory.getString(AppConstants.BOOKING_REQUEST_LOG);
            Log.i(TAG, "loadBookingRequestCURR: " + reqLog);
            if (Objects.equals(reqLog, "")) {
                bookingEmpty.setVisibility(GONE);
                Log.e(TAG, "Json object is null: ");
            } else {
                JSONArray Jarray = new JSONArray(reqLog);
                Log.e(TAG, "Json object is not null: ");
                rideList.setAdapter(new RequestAdapter(
                        MainActivity.this, Jarray));
                bookingEmpty.setVisibility(GONE);

            }
        } catch (IllegalArgumentException | NullPointerException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    void loadNavigationDrawer() {

        ImageView profile_picture = findViewById(R.id.profile_picture);
        ImageView profile_background = findViewById(R.id.profile_background);
        TextView name = findViewById(R.id.profile_name);
        TextView email = findViewById(R.id.profile_email);
        try {
            name.setText(localMemory.getFromDriverInfo().getData().get(0).getFullName());
            email.setText(localMemory.getFromDriverInfo().getData().get(0).getEmailId());
            Picasso.with(MainActivity.this)
                    .load(localMemory.getFromDriverInfo().getData().get(0).getProfileInfo().get(0).
                            getProfilePic())
                    .transform(new PicassoCircleTransformation())
                    .into(profile_picture);
            Picasso.with(MainActivity.this)
                    .load(localMemory.getFromDriverInfo().getData().get(0).getProfileInfo().get(0).
                            getCoverPic())
                    .into(profile_background);
            localMemory.putString(AppConstants.DRIVER_TRIP_ACTIVE_TRACKING_ID, localMemory.
                    getFromDriverInfo().getData().get(0).getActiveBookingId());
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            loadBookingRequests();
            broadcastHandler.toBackgroundService(3);
            return true;
        }
        broadcastHandler.toBackgroundService(3);
        return super.onOptionsItemSelected(item);
    }

    private void askForGPS() {
        final boolean[] value = {false};
        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
        Log.e(TAG, "askForGPS: ");
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().
                addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.
                checkLocationSettings(client, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                gIndex++;
                try {
                    result.getStatus().startResolutionForResult(MainActivity.this, 0x07);
                    Log.e(TAG, gIndex + ". onResult: try");
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    Log.e(TAG, gIndex + ". onResult: catch");
                }
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_your_trips) {
            // Handle the camera action
            Intent bookingsActivityIntent = new Intent(MainActivity.this,
                    BookingsActivity.class);
            startActivity(bookingsActivityIntent);
        } else if (id == R.id.nav_report) {
            Intent settingsActivity = new Intent(MainActivity.this,
                    DriverReportActivity.class);
            startActivity(settingsActivity);
        } else if (id == R.id.nav_settings) {
            Intent settingsActivity = new Intent(MainActivity.this,
                    SettingsActivity.class);
            startActivity(settingsActivity);
        } else if (id == R.id.nav_request_log) {
            Intent requestLogIntent = new Intent(this, RequestLogActivity.class);
            startActivity(requestLogIntent);
        } else if (id == R.id.nav_sign_out) {
            broadcastHandler.localBroadCastToMainActivity(70);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        StaticMemory.MAIN_ACTIVITY_STATUS = -2;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deviceUtil.isOnlineWithToast()) {
            httpRequestBuilder.getDriverInfo();
//            loadBookingRequests();
            loadBookingRequest();
        }
        StaticMemory.MAIN_ACTIVITY_STATUS = 1;
        askForGPS();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!deviceUtil.isServiceRunning(BackgroundService.class, MainActivity.this)) {
            Intent serviceIntent = new Intent(this, BackgroundService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
        StaticMemory.MAIN_ACTIVITY_STATUS = 4;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StaticMemory.MAIN_ACTIVITY_STATUS = 0;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadCastReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StaticMemory.MAIN_ACTIVITY_STATUS = 1;
    }

}
