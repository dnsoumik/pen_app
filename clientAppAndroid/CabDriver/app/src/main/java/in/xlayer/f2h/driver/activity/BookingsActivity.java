package in.xlayer.f2h.driver.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.adapter.BookingsAdapter;
import in.xlayer.f2h.driver.handler.BroadcastHandler;
import in.xlayer.f2h.driver.handler.HttpRequestBuilder;
import in.xlayer.f2h.driver.handler.LocalMemory;
import in.xlayer.f2h.driver.handler.StaticMemory;
import in.xlayer.f2h.driver.other.booking.response.Datum;
import in.xlayer.f2h.driver.util.DeviceUtil;

//import android.util.Log;


public class BookingsActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();
    private LocalMemory localMemory = null;
    private HttpRequestBuilder httpRequestBuilder;
    private View bookingEmpty;
    private StaticMemory staticMemory;
    private DeviceUtil deviceUtil;
    private TextView bookingRespText;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private BroadcastReceiver localBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle intentBundle = intent.getExtras();
            assert intentBundle != null;
            if (!BookingsActivity.this.isDestroyed() && !BookingsActivity.this.isFinishing()) {
//                Log.e(TAG + "BroadcastHandler: ", "onReceive: " +
//                        intentBundle.getInt(BookingsActivity.class.getName() +
//                                BroadcastHandler.class.getSimpleName()));
                switch (intentBundle.getInt(BookingsActivity.class.getName() +
                        BroadcastHandler.class.getSimpleName())) {
                    case 1:
                        break;
                    case 10:
                        bookingEmpty.setVisibility(View.GONE);
                        RecyclerView rideList = findViewById(R.id.booking_list);
                        LinearLayoutManager llm = new LinearLayoutManager(
                                BookingsActivity.this, LinearLayoutManager.VERTICAL,
                                false);
                        rideList.setLayoutManager(llm);
                        List<Datum> reList = localMemory.getFromDriverBookingsResponse()
                                .getData();
                        rideList.setAdapter(new BookingsAdapter(
                                BookingsActivity.this, reList));
                        break;
                    case 20:
                        try {
                            bookingRespText.setText(
                                    localMemory.getFromDriverBookingsResponse().getRespMsg());
                        } catch (NumberFormatException | NullPointerException e) {
                            e.printStackTrace();
                            bookingRespText.setText("No bookings available");
                        }
                        bookingEmpty.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_bookings);

        deviceUtil = new DeviceUtil(this);
        localMemory = new LocalMemory(this);
        httpRequestBuilder = new HttpRequestBuilder(this);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        bookingEmpty = findViewById(R.id.booking_empty);
        bookingRespText = findViewById(R.id.booking_resp_text);

        LocalBroadcastManager.getInstance(this).registerReceiver(localBroadCastReceiver,
                new IntentFilter(BookingsActivity.class.getName()));
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
            if (deviceUtil.isOnlineWithToast()) {
                httpRequestBuilder.getBookingsInfo();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        httpRequestBuilder.getBookingsInfo();
        StaticMemory.BOOKINGS_ACTIVITY = true;
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
    protected void onPause() {
        super.onPause();
        StaticMemory.BOOKINGS_ACTIVITY = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StaticMemory.BOOKINGS_ACTIVITY = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
