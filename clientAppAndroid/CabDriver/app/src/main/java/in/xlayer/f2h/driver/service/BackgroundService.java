package in.xlayer.f2h.driver.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import in.xlayer.f2h.driver.BuildConfig;
import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.SharedPreferenceManagement;
import in.xlayer.f2h.driver.activity.MainActivity;
import in.xlayer.f2h.driver.authorization.AuthLocalMemory;
import in.xlayer.f2h.driver.authorization.AuthStaticElements;
import in.xlayer.f2h.driver.dialog.BookingRequestDialog;
import in.xlayer.f2h.driver.handler.AppConstants;
import in.xlayer.f2h.driver.handler.BroadcastHandler;
import in.xlayer.f2h.driver.handler.HttpRequestBuilder;
import in.xlayer.f2h.driver.handler.LocalMemory;
import in.xlayer.f2h.driver.handler.NotificationHandler;
import in.xlayer.f2h.driver.handler.StaticMemory;
import in.xlayer.f2h.driver.other.AlertSoundBuilder;
import in.xlayer.f2h.driver.other.location.DeviceLocationData;
import in.xlayer.f2h.driver.service.LocationService;
import in.xlayer.f2h.driver.util.DeviceUtil;
import in.xlayer.f2h.driver.ws.SocketResponse;
import in.xlayer.f2h.driver.ws.request.WSBookingRequest;

/**
 * Created by Soumik Debnath on 20-01-2018.
 */

public class BackgroundService extends Service {

    private final String TAG = BackgroundService.class.getSimpleName();
    private LocalMemory localMemory = null;
    private DeviceUtil deviceUtil = null;
    private AuthLocalMemory authLocalMemory = null;
    private LocationService locationService;
    private BroadcastHandler broadcastHandler = null;
    private HttpRequestBuilder httpRequestBuilder;
    private WebSocketClient mWebSocketClient = null;
    private AlertSoundBuilder alertSoundBuilder = null;
    private NotificationHandler notificationHandler;
    private long nTimer = 0L;
    private CountDownTimer countDownTimer;
    private SharedPreferenceManagement sharedPreferenceManager;
    private BroadcastReceiver localBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle switchType = intent.getExtras();
            assert switchType != null;
            Log.e(TAG + "BroadcastHandler: ", "onReceive: " +
                    switchType.getInt(BroadcastHandler.class.getName()));
            switch (switchType.getInt(BroadcastHandler.class.getName())) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
//                    alertSoundBuilder.startAlertSound();
                    break;
                case 3:
                    alertSoundBuilder.stopSound();
                    break;
                case 50:
                    try {
                        JSONObject message = new JSONObject();
                        message.put("msg_type", "BOOKING_REJECTED");
                        message.put("request_id", StaticMemory.REJECT_BOOKING_ID);
                        if (mWebSocketClient != null && !mWebSocketClient.isClosed()) {
                            mWebSocketClient.send(message.toString());
                        }
                    } catch (NullPointerException | JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        LocalBroadcastManager.getInstance(this).registerReceiver(localBroadCastReceiver,
                new IntentFilter(BackgroundService.class.getName()));
        deviceUtil = new DeviceUtil(this);
        localMemory = new LocalMemory(this);
        httpRequestBuilder = new HttpRequestBuilder(this);
        authLocalMemory = new AuthLocalMemory(this);
        broadcastHandler = new BroadcastHandler(this);
        StaticMemory.BACKGROUND_SERVICE_STATUS = 1;
        locationService = new LocationService(getApplicationContext());
        notificationHandler = new NotificationHandler(this);
        alertSoundBuilder = new AlertSoundBuilder(this);
        backgroundTimerFunc();
        locationService.startLocationUpdates();
        sharedPreferenceManager = new SharedPreferenceManagement(getApplicationContext());
    }

    void backgroundTimerFunc() {
        countDownTimer = new CountDownTimer(5000L, 2500L) {
            public void onTick(long millisUntilFinished) {
                if (!Objects.equals(authLocalMemory.getAuthorizationTokenToken(), null)) {
                    if (deviceUtil.isOnline()) {
                        if (mWebSocketClient == null) {
                            createWebSocketClient();
                        } else if (mWebSocketClient.isClosed()) {
                            createWebSocketClient();
                        }
                    }
                }
            }

            public void onFinish() {
                if (!Objects.equals(authLocalMemory.getAuthorizationTokenToken(), null)) {
                    if (deviceUtil.isOnline()) {
                        if (mWebSocketClient == null) {
                            createWebSocketClient();
                        } else if (mWebSocketClient.isClosed()) {
                            createWebSocketClient();
                        }
                    }
//                    nTimer = nTimer + 10000L;
//                    if (nTimer >= localMemory.getLocationUpdateInterval()) {
                    try {
                        if (LocationService.LOCATION_ACTIVE) {
                            updateLocationToServer();
                            if (!locationService.SERVICE_STATUS) {
                                locationService.startLocationUpdates();
                            }
                        } else {
                            if (locationService.SERVICE_STATUS) {
//                                        fusedLocationService.stopLocationUpdates();
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
//                        nTimer = 0L;
//                    }
                } else {
                    if (mWebSocketClient != null) {
                        mWebSocketClient.close();
                    }
//                    fusedLocationService.stopLocationUpdates();
                }
                countDownTimer.start();
            }
        }.start();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                1, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(
                this,
                NotificationHandler.backgroundChannelData[0])
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    private void updateLocationToServer() {
        try {
            DeviceLocationData locInfo = localMemory.getFromMyLocation();
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("imei", DeviceUtil.getDeviceIMEINumber(this));
            jsonBody.put("latitude", locInfo.getLatitude());
            jsonBody.put("longitude", locInfo.getLongitude());
            jsonBody.put("type", "android");
            if (localMemory.getFromMyLocation().getAccuracy() < 30.00 &&
                    localMemory.getTrackingButtonState()) {
                httpRequestBuilder.postMyLocationToServer(jsonBody);
            }
        } catch (NumberFormatException | NullPointerException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadCastReceiver);
        Log.e(TAG, "onDestroy: ");
        StaticMemory.BACKGROUND_SERVICE_STATUS = 0;
        super.onDestroy();
    }

    private void createWebSocketClient() {
        URI SOCKET_URL;

        /**
         * Fetching information from shared preferences
         * */

        SharedPreferences sp = getSharedPreferences(AuthStaticElements.APP_SIGN_IN_CACHE_MEMORY_TAG, MODE_PRIVATE);
        String tokenKey = sp.getString(AuthStaticElements.SIGN_IN_KEY,"");

        String SocketURI = "/web/api/rt_socket?application_id=" + BuildConfig.APPLICATION_ID + "&wskey=" + tokenKey;


        try {
            SOCKET_URL = new URI(localMemory.getServerUrl().replace("http", "ws") +
                    SocketURI);
            Log.e(TAG, "createWebSocketClient: " + SOCKET_URL );
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        /**
         * Initiating connection to socket
         * */

        Map<String, String> headers = new ArrayMap<>();
        headers.put("Origin", BuildConfig.APPLICATION_ID);

        mWebSocketClient = new WebSocketClient(SOCKET_URL, headers) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e(TAG, "onOpen: ");
                try {
                    JSONObject requestBody = new JSONObject("{\"code\": 1500}");
                    mWebSocketClient.send(requestBody.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onMessage(String message) {

                Log.e("SOCKETRESPONSE", message);
                handleSocketMessage(message);
            }

            @Override
            public void onClose(int i, String status_code, boolean b) {
                Log.i(TAG, "onClose: " + i + " " + status_code + " " + b);
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "onError: " + e.getMessage());

            }
        };

        if (deviceUtil.isOnline()) {
            mWebSocketClient.connect();
        }
    }

    private void toSwitchService(String message) {
        try {
            Log.e(TAG, "toSwitchService: " + message);
            SocketResponse response = new Gson().fromJson(message, SocketResponse.class);
            JSONObject  resp = new JSONObject(message);
            Log.e(TAG, "Response: "+ response );
            Log.e(TAG, "Resp: "+ resp );
            switch (resp.getString("msg_type")) {
                case "SUB_V_TYPE":
                    break;
                case "BOOKING_REQ":
                    String reqlog = localMemory.getString(AppConstants.BOOKING_REQUEST_LOG);
                    Log.w(TAG, "String reqlog" + reqlog );
                    JSONObject bookingReq;
                    JSONArray reqLogArr = new JSONArray();
                    if (Objects.equals(reqlog,
                            "")) {
                        bookingReq = new JSONObject(resp.getJSONArray("data").getJSONObject(0)
                                .toString());
                        bookingReq.put("status", 0 );
                        StaticMemory.CURRENT_BOOKING_REQUEST = bookingReq;
                        reqLogArr.put(bookingReq);
                    }
                    else {
                        reqLogArr = new JSONArray(reqlog);
                        bookingReq = new JSONObject(resp.getJSONArray("data").getJSONObject(0)
                                .toString());
                        bookingReq.put("status", 0 );
                        StaticMemory.CURRENT_BOOKING_REQUEST = bookingReq;
                        reqLogArr.put(bookingReq);
                    }
                    Log.e(TAG, "RequestLogArray " + reqLogArr );
                    localMemory.putString(AppConstants.BOOKING_REQUEST_LOG,
                            reqLogArr.toString());
                    notificationHandler.notify("New Booking Request has came",
                            "Click here for more information");
                    alertSoundBuilder.startSound();
                    Intent bookingReqDialog = new Intent(this,
                            BookingRequestDialog.class);
                    startActivity(bookingReqDialog);
                    break;
                case "BOOKING_ACT":
                    alertSoundBuilder.stopSound();
                    String id = resp.getJSONArray("data").get(0).toString();
                    List<WSBookingRequest> rList =
                            StaticMemory.NEW_BOOKING_REQUEST;
                    for (int i = 0; i < rList.size(); i++) {
                        if (Objects.equals(rList.get(i).getRequestId(), id)) {
                            StaticMemory.NEW_BOOKING_REQUEST.remove(i);
                            break;
                        }
                    }
                    broadcastHandler.localBroadCastToMainActivity(11);
                    break;
            }
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to handle message from socket
     *
     */

    public void handleSocketMessage(String message){
        try {
            Log.e(TAG, "handleSocketMessage: " + message);
            JSONObject jsonObject = new JSONObject(message);
            String OrderId = jsonObject.getString("order_id");
            Log.e("RequestedOrder", OrderId);
            notificationHandler.notify("New Booking Request has came",
                    "Click here for more information");
            alertSoundBuilder.startSound();
            Intent bookingReqDialog = new Intent(this,
                    BookingRequestDialog.class);
            bookingReqDialog.putExtra("order_id", OrderId);
            startActivity(bookingReqDialog);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
// 5cbb175b3523893120fc728b
// 5cbb175b3523893120fc728b
// 5cbb175b3523893120fc728b