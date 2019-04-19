package in.xlayer.f2h.driver.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.handler.BroadcastHandler;
import in.xlayer.f2h.driver.handler.HttpRequestBuilder;
import in.xlayer.f2h.driver.handler.LocalMemory;
import in.xlayer.f2h.driver.handler.StaticMemory;

public class BookingCompleteActivity extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    private LocalMemory localMemory;
    private Button cashCollected, sendFeedBack;
    private TextView totalDistance, totalAmount;
    private HttpRequestBuilder httpRequestBuilder;
    private View feedbackView;
    private EditText feedbackDescription;

    private AlertDialog.Builder sureAlert;
    private AlertDialog alert;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private BroadcastReceiver localBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle intentBundle = intent.getExtras();
            assert intentBundle != null;
            if (!BookingCompleteActivity.this.isDestroyed() && !BookingCompleteActivity.this.
                    isFinishing()) {
                Log.e(TAG + "BroadcastHandler: ", "onReceive: " +
                        intentBundle.getInt(BookingCompleteActivity.class.getName() +
                                BroadcastHandler.class.getSimpleName()));
                switch (intentBundle.getInt(BookingCompleteActivity.class.getName() +
                        BroadcastHandler.class.getSimpleName())) {
                    case 20:
                        cashCollected.setVisibility(View.GONE);
                        feedbackView.setVisibility(View.VISIBLE);
                        break;
                    case 30:
                        finish();
                        break;
                }
            }
        }
    };
    private ImageView oneStar, twoStar, threeStar, fourStar, fiveStar;
    private int givenStar = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_complete);

        localMemory = new LocalMemory(this);
        httpRequestBuilder = new HttpRequestBuilder(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(localBroadCastReceiver,
                new IntentFilter(BookingCompleteActivity.class.getName()));

        sureAlert = new AlertDialog.Builder(this);
        cashCollected = findViewById(R.id.cash_collect);
        sendFeedBack = findViewById(R.id.send_feedback);
        totalAmount = findViewById(R.id.total_amount);
        totalDistance = findViewById(R.id.total_distance);
        feedbackView = findViewById(R.id.send_feedback_view);
        feedbackDescription = findViewById(R.id.feedback_description);

        loadDetails();
        cashCollected.setVisibility(View.VISIBLE);
        cashCollected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = "Are you Sure?";
                sureAlert.setMessage(tmp).
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    JSONObject body = new JSONObject();
                                    body.put("id", StaticMemory.TRIP_COMPLETE_RESPONSE.getData().
                                            get(0).getBookingId());
                                    httpRequestBuilder.postCashCollected(body);
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
        loadReviewStars();
    }

    @SuppressLint("SetTextI18n")
    private void loadDetails() {
        try {
            totalAmount.setText(getResources().getString(R.string.rs_symbol) + " " + String.valueOf(StaticMemory.
                    TRIP_COMPLETE_RESPONSE.getData()
                    .get(0).getPaymentInfo().get(0).getPrice()));
            totalDistance.setText("Total Distance | " + String.valueOf(StaticMemory.
                    TRIP_COMPLETE_RESPONSE.getData()
                    .get(0).getTravelledDistance()) + " KM");
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void loadReviewStars() {

        oneStar = findViewById(R.id.start_one);
        twoStar = findViewById(R.id.start_two);
        threeStar = findViewById(R.id.start_three);
        fourStar = findViewById(R.id.start_four);
        fiveStar = findViewById(R.id.start_five);

        sendFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject body = new JSONObject();
                    if (givenStar != 0) {
                        body.put("rating", givenStar);
                        body.put("booking_id", StaticMemory.TRIP_COMPLETE_RESPONSE.getData().get(0).
                                getBookingId());
                        body.put("description", feedbackDescription.getText().toString());
                        httpRequestBuilder.sendFeedBack(body);
                    } else {
                        Toast.makeText(BookingCompleteActivity.this, "Please give a " +
                                "Rating", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        oneStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                twoStar.setImageResource(R.drawable.ic_star_border_black_50dp);
                threeStar.setImageResource(R.drawable.ic_star_border_black_50dp);
                fourStar.setImageResource(R.drawable.ic_star_border_black_50dp);
                fiveStar.setImageResource(R.drawable.ic_star_border_black_50dp);
                givenStar = 0;
            }
        });

        twoStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                twoStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                threeStar.setImageResource(R.drawable.ic_star_border_black_50dp);
                fourStar.setImageResource(R.drawable.ic_star_border_black_50dp);
                fiveStar.setImageResource(R.drawable.ic_star_border_black_50dp);
                givenStar = 2;
            }
        });

        threeStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                twoStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                threeStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                fourStar.setImageResource(R.drawable.ic_star_border_black_50dp);
                fiveStar.setImageResource(R.drawable.ic_star_border_black_50dp);
                givenStar = 3;
            }
        });

        fourStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                twoStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                threeStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                fourStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                fiveStar.setImageResource(R.drawable.ic_star_border_black_50dp);
                givenStar = 4;
            }
        });

        fiveStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                twoStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                threeStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                fourStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                fiveStar.setImageResource(R.drawable.ic_star_yellow_50dp);
                givenStar = 5;
            }
        });
    }

}
