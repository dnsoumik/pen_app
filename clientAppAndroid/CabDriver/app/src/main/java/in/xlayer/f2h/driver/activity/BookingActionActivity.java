package in.xlayer.f2h.driver.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.handler.BroadcastHandler;
import in.xlayer.f2h.driver.handler.HttpRequestBuilder;
import in.xlayer.f2h.driver.handler.LocalMemory;
import in.xlayer.f2h.driver.handler.StaticMemory;
import in.xlayer.f2h.driver.other.PicassoCircleTransformation;

public class BookingActionActivity extends AppCompatActivity {

    private LocalMemory localMemory;
    private ImageView user_icon;
    private TextView user_name, user_mo_num;
    private Button trip_action_button;
    private String TAG = BookingActionActivity.class.getSimpleName();
    private HttpRequestBuilder httpRequestBuilder;
    private AlertDialog.Builder sureAlert;
    private AlertDialog alert;
    private TextView direction_button_start, direction_button_end;
    private View call_view, directionView;
    private Dialog verify_otp_dialog;
    private Button arrival_alert;
    private TextView start_location, stop_location;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private BroadcastReceiver localBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle intentBundle = intent.getExtras();
            assert intentBundle != null;
            if (!BookingActionActivity.this.isDestroyed() && !BookingActionActivity.this.
                    isFinishing()) {
                Log.e(TAG + "BroadcastHandler: ", "onReceive: " +
                        intentBundle.getInt(BookingActionActivity.class.getName() +
                                BroadcastHandler.class.getSimpleName()));
                switch (intentBundle.getInt(BookingActionActivity.class.getName() +
                        BroadcastHandler.class.getSimpleName())) {
                    case 1:
                        break;
                    case 20:
                        setButtonState();
                        verify_otp_dialog.dismiss();
                        break;
                    case 21:
                        StaticMemory.SELECTED_BOOKING.setStatus("ACTIVE");
                        trip_action_button.setVisibility(View.VISIBLE);
                        arrival_alert.setVisibility(View.GONE);
                        break;
                    case 22:
                        finish();
                        Intent bCompleteIntent = new Intent(BookingActionActivity.this,
                                BookingCompleteActivity.class);
                        startActivity(bCompleteIntent);
                        break;
                    case 70:
                        finish();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_action);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.trip_toolbar);
        toolbar.setTitle(StaticMemory.SELECTED_BOOKING.getUserBookingId());
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        httpRequestBuilder = new HttpRequestBuilder(this);
        localMemory = new LocalMemory(this);
        sureAlert = new AlertDialog.Builder(this);

        verify_otp_dialog = new Dialog(BookingActionActivity.this);
        verify_otp_dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        verify_otp_dialog.setContentView(R.layout.dialog_verify_booking_otp);

        user_icon = findViewById(R.id.user_icon);
        direction_button_start = findViewById(R.id.direction_button_start);
        directionView = findViewById(R.id.direction_view);
        direction_button_end = findViewById(R.id.direction_button_end);
        call_view = findViewById(R.id.call_view);
        start_location = findViewById(R.id.start_location_addr);
        stop_location = findViewById(R.id.stop_location_addr);

        user_name = findViewById(R.id.user_name);
        user_mo_num = findViewById(R.id.user_mobile_number);
        trip_action_button = findViewById(R.id.trip_action_button);

        LocalBroadcastManager.getInstance(this).registerReceiver(localBroadCastReceiver,
                new IntentFilter(BookingActionActivity.class.getName()));

        Picasso.with(this)
                .load(StaticMemory.SELECTED_BOOKING.getUserInfo()
                        .get(0)
                        .getProfilePicture())
                .transform(new PicassoCircleTransformation())
                .into(user_icon);
        user_name.setText(StaticMemory.SELECTED_BOOKING.getUserInfo().get(0).getName());
        user_mo_num.setText(StaticMemory.SELECTED_BOOKING.getUserInfo().get(0).getMobileNumber());
        trip_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(StaticMemory.SELECTED_BOOKING.getStatus(), "ACTIVE")) {
                    verify_otp_dialog.show();
                } else if (Objects.equals(StaticMemory.SELECTED_BOOKING.getStatus(), "PROG")) {
                    String tmp = "You can't undo this request, Are you Sure?";
                    sureAlert.setMessage(tmp).
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        JSONObject body = new JSONObject();
                                        body.put("action", "STOP");
                                        body.put("travelled_distance", localMemory.
                                                getActiveTripDistance());
                                        body.put("booking_id", StaticMemory.SELECTED_BOOKING.
                                                getId());
                                        httpRequestBuilder.driverAction(body);
                                    } catch (NullPointerException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            alert.cancel();
                        }
                    });
                    alert = sureAlert.create();
                    alert.show();
                }
            }
        });
        final EditText booking_otp = verify_otp_dialog.findViewById(R.id.booking_otp);
        Button verify_button = verify_otp_dialog.findViewById(R.id.verify_button);
        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (booking_otp.getText().toString().length() == 0) {
                    booking_otp.setError("Enter the Otp");
                    return;
                }
                try {
                    JSONObject body = new JSONObject();
                    body.put("action", "START");
                    body.put("otp", Integer.parseInt(booking_otp.getText().toString()));
                    body.put("booking_id", StaticMemory.SELECTED_BOOKING.getId());
                    httpRequestBuilder.driverAction(body);
                } catch (NullPointerException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Button verify_close = verify_otp_dialog.findViewById(R.id.verify_close);
        verify_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                booking_otp.setText("");
                verify_otp_dialog.dismiss();
            }
        });
        start_location.setText(StaticMemory.SELECTED_BOOKING.getPickInfo().get(0).getAddr());
        stop_location.setText(StaticMemory.SELECTED_BOOKING.getDropInfo().get(0).getAddr());
        call_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:country code is hard coded
                try {
                    Uri number = Uri.parse("tel: +91" + StaticMemory.SELECTED_BOOKING.getUserInfo().
                            get(0).getMobileNumber());
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        direction_button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + StaticMemory.SELECTED_BOOKING
                        .getPickInfo().get(0).getLat() + "," + StaticMemory.SELECTED_BOOKING.
                        getPickInfo().get(0).getLng());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        direction_button_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + StaticMemory.SELECTED_BOOKING
                        .getDropInfo().get(0).getLat() + "," + StaticMemory.SELECTED_BOOKING.
                        getDropInfo().get(0).getLng());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        arrival_alert = findViewById(R.id.arrival_alert_button);
        arrival_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = "You can't undo this request, Are you Sure?";
                sureAlert.setMessage(tmp).
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    JSONObject body = new JSONObject();
                                    body.put("id", StaticMemory.SELECTED_BOOKING.getId());
                                    httpRequestBuilder.postArrivalAlert(body);
                                } catch (NullPointerException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.cancel();
                    }
                });
                alert = sureAlert.create();
                alert.show();
            }
        });
        setButtonState();
    }

    private void setButtonState() {
        if (Objects.equals(StaticMemory.SELECTED_BOOKING.getStatus(), "PROG")) {
            trip_action_button.setBackgroundColor(getResources().getColor(R.color.red_overlay));
            trip_action_button.setText("Stop Trip");
            trip_action_button.setVisibility(View.VISIBLE);
            arrival_alert.setVisibility(View.GONE);
        } else if (Objects.equals(StaticMemory.SELECTED_BOOKING.getStatus(), "ACTIVE")) {
            trip_action_button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            trip_action_button.setVisibility(View.VISIBLE);
            arrival_alert.setVisibility(View.GONE);
            trip_action_button.setText("Start Trip");
        } else if (Objects.equals(StaticMemory.SELECTED_BOOKING.getStatus(), "CONFIRMED")) {
            trip_action_button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            trip_action_button.setVisibility(View.GONE);
            arrival_alert.setVisibility(View.VISIBLE);
            trip_action_button.setText("Start Trip");
        } else if (Objects.equals(StaticMemory.SELECTED_BOOKING.getStatus(), "STOPPED")) {
            trip_action_button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            trip_action_button.setVisibility(View.VISIBLE);
            arrival_alert.setVisibility(View.GONE);
            trip_action_button.setText("Trip is Stopped");
        } else if (Objects.equals(StaticMemory.SELECTED_BOOKING.getStatus(), "COMPLETED")) {
            directionView.setVisibility(View.GONE);
            trip_action_button.setVisibility(View.GONE);
            arrival_alert.setVisibility(View.GONE);
            return;
        }
        directionView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadCastReceiver);
        super.onDestroy();
    }
}
