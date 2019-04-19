package in.xlayer.f2h.driver;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import in.xlayer.f2h.driver.activity.MainActivity;
import in.xlayer.f2h.driver.authorization.AuthLocalMemory;
import in.xlayer.f2h.driver.authorization.SignInActivity;
import in.xlayer.f2h.driver.handler.LocalMemory;
import in.xlayer.f2h.driver.util.DeviceUtil;

public class StartUpScreen extends AppCompatActivity {

    private LocalMemory localMemory = null;
    private AuthLocalMemory authLocalMemory;
    private AlertDialog updateAlert;
    private RequestQueue reqQueue;
    private DeviceUtil deviceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_screen);
        localMemory = new LocalMemory(this);
        authLocalMemory = new AuthLocalMemory(this);
        deviceUtil = new DeviceUtil(this);
        reqQueue = Volley.newRequestQueue(this);
        AlertDialog.Builder updateDialog = new AlertDialog.Builder(this);
        updateDialog.setMessage("A new update is now available.").
                setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String market_uri = "https://play.google.com/store/apps/details?id=" +
                                StartUpScreen.this.getApplicationInfo().packageName;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(market_uri));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent
                                .FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        updateDialog.setCancelable(false);
        updateAlert = updateDialog.create();
        checkForUpdate();
    }

    private void addToHttpRequestQueue(StringRequest stringRequest) {
        if (deviceUtil.isOnline()) {
            reqQueue.add(stringRequest);
        } else {
            Toast.makeText(StartUpScreen.this, "Network Unavailable",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // check for update
    public void checkForUpdate() {
        String URL = localMemory.getServerUrl() + "/api/check_for_update?id=" +
                StartUpScreen.this.getApplicationInfo().packageName;
        addToHttpRequestQueue(new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(String response) {
                        if (!response.isEmpty()) {
                            try {
                                JSONObject info = new JSONObject(response);
                                if (info.getBoolean("resp_code")) {
                                    if (BuildConfig.VERSION_CODE <
                                            info.getJSONArray("data")
                                                    .getJSONObject(0)
                                                    .getLong("version_code")) {
                                        updateAlert.show();
                                    } else {
                                        goOn();
                                    }
                                } else {
                                    goOn();
                                }
                            } catch (JSONException | NumberFormatException |
                                    NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onErrorResponse(VolleyError error) {
                goOn();
            }
        }));
    }

    void goOn() {
        if (Objects.equals(authLocalMemory.getAuthorizationTokenToken(), null)) {
            Intent signInIntent = new Intent(StartUpScreen.this,
                    SignInActivity.class);
            startActivity(signInIntent);
        } else {
            Intent main = new Intent(StartUpScreen.this,
                    MainActivity.class);
            startActivity(main);
        }
        finish();
    }

}
