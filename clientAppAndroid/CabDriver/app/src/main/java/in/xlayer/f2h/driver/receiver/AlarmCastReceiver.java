package in.xlayer.f2h.driver.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.xlayer.f2h.driver.service.BackgroundService;
import in.xlayer.f2h.driver.handler.BroadcastHandler;
import in.xlayer.f2h.driver.handler.StaticMemory;
import in.xlayer.f2h.driver.service.BackgroundService;

import static android.content.Context.ALARM_SERVICE;

public class AlarmCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BroadcastHandler broadcastHandler = new BroadcastHandler(context);
        Intent aintent = new Intent(context, AlarmCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), 234324243, aintent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (10000), pendingIntent);
        if (StaticMemory.BACKGROUND_SERVICE_STATUS == 0) {
            try {
                Intent backgroundService = new Intent(context, BackgroundService.class);
                context.startService(backgroundService);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } else {
            broadcastHandler.toBackgroundService(0);
        }
    }
}
