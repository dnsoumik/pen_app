package in.xlayer.f2h.driver.activity;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Objects;

import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.handler.AppConstants;
import in.xlayer.f2h.driver.handler.LocalMemory;

public class SettingsActivity extends AppCompatActivity {

    final String TAG = "SettingsActivity: ";
    private LocalMemory sLocalMemory;
    private RadioGroup intervalRadioGroup;
    private Drawable action_back = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sLocalMemory = new LocalMemory(this);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        intervalRadioGroup = (RadioGroup) findViewById(R.id.intervalRadioGroup);
        intervalRadioGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        saveInterval();
                    }
                });
        TextView phoneView = (TextView) findViewById(R.id.phoneNum);
        action_back = getDrawable(R.drawable.curved_area);
        try {
            Log.e(TAG, "onCreate: " + sLocalMemory.getFromDriverInfo()
                    .getData().get(0).getMobileNumber());
            phoneView.setText("+91 " + sLocalMemory.getFromDriverInfo()
                    .getData().get(0).getMobileNumber());//TODO: country code has been hard coded
        } catch (NullPointerException e) {
            e.printStackTrace();
            phoneView.setText("+91 ");
        }
        displayUserSettings();
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

    private void saveInterval() {
        switch (intervalRadioGroup.getCheckedRadioButtonId()) {
            case R.id.i10s:
                sLocalMemory.putLong(AppConstants.SETTINGS_LOCATION_UPDATE_INTERVAL, 10000L);
                break;
            case R.id.i30s:
                sLocalMemory.putLong(AppConstants.SETTINGS_LOCATION_UPDATE_INTERVAL, 30000L);
                break;
            case R.id.i1:
                sLocalMemory.putLong(AppConstants.SETTINGS_LOCATION_UPDATE_INTERVAL, 60000L);
                break;
            case R.id.i5:
                sLocalMemory.putLong(AppConstants.SETTINGS_LOCATION_UPDATE_INTERVAL, 300000L);
                break;
            case R.id.i15:
                sLocalMemory.putLong(AppConstants.SETTINGS_LOCATION_UPDATE_INTERVAL, 900000L);
                break;
        }
    }

    private void displayUserSettings() {
        Log.e(TAG, "displayUserSettings: " + sLocalMemory.getLocationUpdateInterval());
        switch (String.valueOf(sLocalMemory.getLocationUpdateInterval())) {
            case "10000":
                intervalRadioGroup.check(R.id.i10s);
                break;
            case "30000":
                intervalRadioGroup.check(R.id.i30s);
                break;
            case "60000":
                intervalRadioGroup.check(R.id.i1);
                break;
            case "300000":
                intervalRadioGroup.check(R.id.i5);
                break;
            case "900000":
                intervalRadioGroup.check(R.id.i15);
                break;
        }
        final Button track_button = findViewById(R.id.tracking_button);
        track_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sLocalMemory.getTrackingButtonState()) {
                    sLocalMemory.putBoolean(AppConstants.SETTINGS_TRACKING_BUTTON, false);
                } else {
                    sLocalMemory.putBoolean(AppConstants.SETTINGS_TRACKING_BUTTON, true);
                }
                setButtonState(track_button);
            }
        });
        setButtonState(track_button);
    }

    private void setButtonState(Button track_button) {
        if (sLocalMemory.getTrackingButtonState()) {
            action_back.setColorFilter(getResources().getColor(R.color.colorPrimary),
                    PorterDuff.Mode.SRC);
            track_button.setBackground(action_back);
            track_button.setText(getResources().getString(R.string.tracking_is_on));
        } else {
            action_back.setColorFilter(getResources().getColor(R.color.red_overlay),
                    PorterDuff.Mode.SRC);
            track_button.setBackground(action_back);
            track_button.setText(getResources().getString(R.string.tracking_is_off));
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }
}
