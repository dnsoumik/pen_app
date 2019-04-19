package in.xlayer.f2h.driver.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.authorization.AuthLocalMemory;
import in.xlayer.f2h.driver.fragment.DriverReportDailyFragment;
import in.xlayer.f2h.driver.fragment.DriverReportMonthlyFragment;
import in.xlayer.f2h.driver.fragment.DriverReportWeeklyFragment;
import in.xlayer.f2h.driver.handler.BroadcastHandler;
import in.xlayer.f2h.driver.handler.HttpRequestBuilder;
import in.xlayer.f2h.driver.handler.LocalMemory;
import in.xlayer.f2h.driver.util.DeviceUtil;
import in.xlayer.f2h.driver.util.TimeUtil;

public class DriverReportActivity extends AppCompatActivity {

    public static JSONObject DAILY_REPORT_DATA = null;
    public static JSONObject WEEKLY_REPORT_DATA = null;
    public static JSONObject MONTHLY_REPORT_DATA = null;
    private RequestQueue httpRequestQueue;
    private BroadcastHandler broadcastHandler;
    private DeviceUtil deviceUtil;
    private HttpRequestBuilder httpRequestBuilder;
    private String TAG = this.getClass().getSimpleName();
    private AuthLocalMemory authLocalMemory;
    private LocalMemory localMemory;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_report);
        broadcastHandler = new BroadcastHandler(this);
        deviceUtil = new DeviceUtil(this);
        httpRequestBuilder = new HttpRequestBuilder(this);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        authLocalMemory = new AuthLocalMemory(this);
        localMemory = new LocalMemory(this);
        httpRequestQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDriverDailyReport();
    }

    public void getDriverDailyReport() {
        String URL = localMemory.getServerUrl() + "/api/driver_trip_report?interval=" +
                String.valueOf(getDailyInterval());
        httpRequestQueue.add(new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            DriverReportActivity.DAILY_REPORT_DATA = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            DriverReportActivity.DAILY_REPORT_DATA = null;
                        }
                        getDriverWeeklyReport();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " +
                        authLocalMemory.getAuthorizationTokenToken());
                return headers;
            }
        });
    }

    private void loadFragmentData() {
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.fragment_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.report_tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener
                (mViewPager));
    }

    public void getDriverWeeklyReport() {
        String URL = localMemory.getServerUrl() + "/api/driver_trip_report?interval=" +
                String.valueOf(getWeeklyInterval());
        httpRequestQueue.add(new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            DriverReportActivity.WEEKLY_REPORT_DATA = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            DriverReportActivity.WEEKLY_REPORT_DATA = null;
                        }
                        getDriverMonthlyReport();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " +
                        authLocalMemory.getAuthorizationTokenToken());
                return headers;
            }
        });
    }

    public void getDriverMonthlyReport() {
        String URL = localMemory.getServerUrl() + "/api/driver_trip_report?interval=" +
                String.valueOf(getMonthlyInterval());
        httpRequestQueue.add(new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            DriverReportActivity.MONTHLY_REPORT_DATA = new JSONObject(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            DriverReportActivity.MONTHLY_REPORT_DATA = null;
                        }
                        loadFragmentData();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " +
                        authLocalMemory.getAuthorizationTokenToken());

                return headers;
            }
        });
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
                getDriverDailyReport();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    // Return Today's 12:00 AM Local Timestamp
    private Long getDailyInterval() {
        try {
            String dateTime = TimeUtil.parseTimeStampToDateFormat(
                    System.currentTimeMillis() / 1000L,
                    "dd-MMM-yyyy");
            return TimeUtil.getFromTimeStringLocalToTimeStamp(
                    dateTime + " 00:00", "dd-MMM-yyy HH:mm");
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    // Weekly
    private Long getWeeklyInterval() {
        try {
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            int date = Integer.valueOf(TimeUtil.parseTimeStampToDateFormat(
                    System.currentTimeMillis() / 1000L,
                    "dd"));
            date -= dayOfWeek - 1;
            String maAndY = TimeUtil.parseTimeStampToDateFormat(
                    System.currentTimeMillis() / 1000L,
                    "MMM-yyyy");

            return TimeUtil.getFromTimeStringLocalToTimeStamp(
                    date + "-" + maAndY + " 00:00", "dd-MMM-yyy HH:mm");
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    // Monthly
    private Long getMonthlyInterval() {
        try {
            String dateFormat = TimeUtil.parseTimeStampToDateFormat(
                    System.currentTimeMillis() / 1000L,
                    "MMM-yyy");
            return TimeUtil.getFromTimeStringLocalToTimeStamp(
                    "01-" + dateFormat + " 00:00", "dd-MMM-yyy HH:mm");
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            return 0L;
        }

    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_driver_report_view, container,
                    false);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new DriverReportDailyFragment();
                    break;
                case 1:
                    fragment = new DriverReportWeeklyFragment();
                    break;
                case 2:
                    fragment = new DriverReportMonthlyFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}


