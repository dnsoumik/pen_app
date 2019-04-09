package pikcel.com.pikcelclient;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;

import pikcel.com.pikcelclient.AppActionFragments.FileFragment;
import pikcel.com.pikcelclient.AppActionFragments.RemoteControlFragment;

public class AppAction extends Activity {

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_file:
                    loadFragment(new FileFragment());
                    return true;
                case R.id.navigation_remote:
                    loadFragment(new RemoteControlFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_action);



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Intent intent = getIntent();
        int action = intent.getIntExtra("ACTION", 2);
        if(action == 1)
            loadFragment(new FileFragment());
        else{
            loadFragment(new RemoteControlFragment());
        }

    }

}
