package in.xlayer.f2h.driver.handler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.activity.MainActivity;
import in.xlayer.f2h.driver.util.DeviceUtil;

//import vtp.xlayer.user.other.ws.notify.approch.NotifyApproach;
//import vtp.xlayer.user.other.ws.notify.notice.NotifyNotice;

/**
 * Created by dnsou on 22-01-2018.
 */

public class NotificationHandler {

    public static final String[] backgroundChannelData = {
            "BACKGROUND110011",
            "BACKGROUND SERVICE",
            "Will notify when Driver App is running in background"
    };
    private static int noticeID = 10100;
    private static int approachID = 20100;
    private final String[] noticeChannelData = {
            "NTF26008821",
            "Trip Notification",
            "Will notify when a new notification has came"
    };
    private final String[] approachChannelData = {
            "APPROACH28008822",
            "NOTIFY APPROACH",
            "Will notify when vehicle is nearby"
    };
    private String TAG = this.getClass().getSimpleName(); //For TAG name
    private Context context;
    private LocalMemory localMemory;
    private DeviceUtil deviceUtil;
    private NotificationManager notificationManager;
//    private Broadcast broadcast;

    public NotificationHandler(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel bServiceChannel = new NotificationChannel(backgroundChannelData[0],
                    backgroundChannelData[1], importance);
            bServiceChannel.setDescription(backgroundChannelData[2]);
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(bServiceChannel);

            NotificationChannel noticeChannel = new NotificationChannel(noticeChannelData[0],
                    noticeChannelData[1], importance);
            noticeChannel.setDescription(noticeChannelData[2]);
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(noticeChannel);
            NotificationChannel approachChannel = new NotificationChannel(approachChannelData[0],
                    approachChannelData[1], importance);
            approachChannel.setDescription(approachChannelData[2]);
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(approachChannel);
        }
    }

    public void notify(String title, String description) {
        try {
            android.app.Notification.Builder builder = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new android.app.Notification.Builder(context, noticeChannelData[0]);
            } else {
                builder = new android.app.Notification.Builder(context);
            }
            builder.setSmallIcon(R.mipmap.app_emergency_notification_icon);
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0,
                    intent, PendingIntent.FLAG_NO_CREATE);
            builder.setContentIntent(pendingIntent);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_launcher));
            builder.setContentTitle(title);
            builder.setContentText(description);

            // auto cancel true
            builder.setAutoCancel(true);

            //set notification sound
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(soundUri);
            if (notificationManager != null) {
                ++NotificationHandler.noticeID;
                notificationManager.notify(NotificationHandler.noticeID, builder.build());
            }
        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void newBookingRequest(String title, String description, int nfId) {
        try {
            android.app.Notification.Builder builder = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new android.app.Notification.Builder(context, noticeChannelData[0]);
            } else {
                builder = new android.app.Notification.Builder(context);
            }
            builder.setSmallIcon(R.mipmap.app_emergency_notification_icon);
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_launcher));
            builder.setContentTitle(title);
            builder.setContentText(description);

            // auto cancel true
            builder.setAutoCancel(true);

            //set notification sound
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//            builder.setSound(soundUri);
            if (notificationManager != null) {
                notificationManager.notify(nfId, builder.build());
            }
        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    //
//    public void notifyApproach(NotifyApproach approachData) {
//        Log.e(TAG, "showEmergencyNotification: ");
//        try {
////            sEmergencyMsg = gson.fromJson(emergency_data, EmergencyMsg.class);
//            android.app.NotificationHandler.Builder builder = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                builder = new android.app.NotificationHandler.Builder(context, approachChannelData[0]);
//            } else {
//                builder = new android.app.NotificationHandler.Builder(context);
//            }
//            builder.setSmallIcon(R.mipmap.app_emergency_notification_icon);
//            Intent intent = new Intent(context, MainActivity.class);
////            sMemoryCall.putString(context.getResources().getString(R.string.emergency_vehicle_id), sEmergencyMsg.getVehicleId());
////            sMemoryCall.putBoolean(context.getResources().getString(R.string.emergency_notification_status), true);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0);
////            if (!deviceUtil.isServiceRunning(MainActivity.class.getName())) {
////                builder.setContentIntent(pendingIntent); // TODO for intent
////            }
//            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.school_bus_marker_v3));
//            builder.setContentTitle(approachData.getData().get(0).getTitle());
//            builder.setSubText("ETA: " + approachData.getData().get(0).getEta());
//            builder.setContentText("Distance: " + approachData.getData().get(0).getDistance() + " mtrs");
//            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//            // auto cancel true
//            builder.setAutoCancel(true);
//
//            //set notification sound
//            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            builder.setSound(soundUri);
//            if (notificationManager != null) {
////                Log.e(TAG, "NotifyApproach: " + noticeID);
//                ++NotificationHandler.approachID;
//                notificationManager.notify(NotificationHandler.approachID, builder.build());
//            }
//        } catch (NullPointerException | NumberFormatException e) {
//            e.printStackTrace();
//        }
//    }
    // prepare intent which is triggered if the
// notification is selected

//    Intent intent = new Intent(this, NotificationReceiver.class);
//    // use System.currentTimeMillis() to have a unique ID for the pending intent
//    PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

    // build notification
// the addAction re-use the same intent to keep the example short
    //TODO for any new future notification
}
