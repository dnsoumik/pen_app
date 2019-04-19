package in.xlayer.f2h.driver.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Objects;

import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.adapter.RequestAdapter;
import in.xlayer.f2h.driver.handler.AppConstants;
import in.xlayer.f2h.driver.handler.LocalMemory;

public class RequestLogActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();
    private LocalMemory localMemory = null;
    private View bookingEmpty;

    private void loadBookingRequest() {
        try {
            RecyclerView rideList = findViewById(R.id.request_list);
            LinearLayoutManager llm = new LinearLayoutManager(
                    RequestLogActivity.this, LinearLayoutManager.VERTICAL,
                    true);
            llm.setStackFromEnd(true);
            rideList.setLayoutManager(llm);
            String reqLog = localMemory.getString(AppConstants.BOOKING_REQUEST_LOG);
            Log.i(TAG, "loadBookingRequestCURR: " + reqLog);
            if (Objects.equals(reqLog, "")){
                bookingEmpty.setVisibility(View.VISIBLE);
                Log.e(TAG, "Json object is null: " );
            }else {
                JSONArray Jarray = new JSONArray(reqLog);
                Log.e(TAG, "Json object is not null: " );
                rideList.setAdapter(new RequestAdapter(
                        RequestLogActivity.this, Jarray));
                bookingEmpty.setVisibility(View.GONE);

            }
        } catch (IllegalArgumentException | NullPointerException |JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_log);

        localMemory = new LocalMemory(this);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        bookingEmpty = findViewById(R.id.request_empty);

        bookingEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookingRequest();
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

}
