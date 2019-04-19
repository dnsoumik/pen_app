package in.xlayer.f2h.driver.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.activity.DriverReportActivity;

//import android.util.Log;

public class DriverReportDailyFragment extends Fragment {

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View layoutInflater = inflater.inflate(R.layout.fragment_driver_report_daily,
                container, false);
        TextView activeTrips = layoutInflater.findViewById(R.id.accept_value);
        TextView rejectTrips = layoutInflater.findViewById(R.id.reject_value);
        TextView amount = layoutInflater.findViewById(R.id.earn_value);
        try {
            JSONObject indexData = DriverReportActivity.DAILY_REPORT_DATA
                    .getJSONArray("data").getJSONObject(0);
//            Log.e("DriverDailyFragment", "onCreateView: " + indexData );
            activeTrips.setText(String.valueOf(indexData.get("total_trips")));
            rejectTrips.setText(indexData.get("reject_trip").toString());
            amount.setText(getResources().getString(R.string.rs_symbol) + " " +
                    indexData.getString("total_amount"));
        } catch (JSONException | NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            activeTrips.setText("0");
            rejectTrips.setText("0");
            amount.setText("\u20B9 0");
        }
        return layoutInflater;
    }
}
