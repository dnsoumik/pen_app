package pikcel.com.pikcelclient;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import pikcel.com.pikcelclient.AppActionFragments.PlayList;

public class MainActivity extends Activity {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    FontManager fontManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fontManager = new FontManager(getApplicationContext());
        ConstraintLayout rootView = findViewById(R.id.main_activity_root);
        fontManager.replaceFonts(rootView, fontManager.getRegular());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(checkPermissionREAD_EXTERNAL_STORAGE(MainActivity.this)){
                    Intent dashboard = new Intent(MainActivity.this, PlayList.class);
                    startActivity(dashboard);
                    finish();
                }
            }
        }, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent dashboard = new Intent(MainActivity.this, AppAction.class);
                    startActivity(dashboard);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "External Storage permission denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }


    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
//        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    showDialog("External storage", context,Manifest.permission.READ_EXTERNAL_STORAGE);
//                } else {
//                    ActivityCompat
//                            .requestPermissions(
//                                    (Activity) context,
//                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
//                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//                }
//                return false;
//            } else {
//                return true;
//            }
//
//        } else {
//            return true;
//        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showDialog("External storage", context,Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                ActivityCompat
                        .requestPermissions(
                                (Activity) context,
                                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

}
