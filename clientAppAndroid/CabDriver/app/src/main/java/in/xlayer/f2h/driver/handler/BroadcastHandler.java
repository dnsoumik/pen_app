package in.xlayer.f2h.driver.handler;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import in.xlayer.f2h.driver.activity.BookingActionActivity;
import in.xlayer.f2h.driver.activity.BookingCompleteActivity;
import in.xlayer.f2h.driver.activity.BookingsActivity;
import in.xlayer.f2h.driver.activity.MainActivity;

public class BroadcastHandler {

    private Context context;
    private Intent mainActFilter;
    private Intent backgroundServiceFilter;
    private Intent bookingActionActivityFilter;
    private Intent bookingActivityFilter;
    private Intent bookingCompleteActivityFilter;

    public BroadcastHandler(Context context) {
        this.context = context;
        mainActFilter = new Intent(MainActivity.class.getName());
        backgroundServiceFilter = new Intent(in.xlayer.f2h.driver.service.BackgroundService.class.getName());
        bookingActionActivityFilter = new Intent(BookingActionActivity.class.getName());
        bookingActivityFilter = new Intent(BookingsActivity.class.getName());
        bookingCompleteActivityFilter = new Intent(BookingCompleteActivity.class.getName());
    }

    public void localBroadCastToBookingActivity(int type) {
        bookingActivityFilter.putExtra(BookingsActivity.class.getName() + BroadcastHandler.class.getSimpleName(), type);
        LocalBroadcastManager.getInstance(context).sendBroadcast(bookingActivityFilter);
    }

    public void localBroadCastToMainActivity(int type) {
        mainActFilter.putExtra(MainActivity.class.getName() + BroadcastHandler.class.getSimpleName(), type);
        LocalBroadcastManager.getInstance(context).sendBroadcast(mainActFilter);
    }

    public void toBackgroundService(int type) {
        backgroundServiceFilter.putExtra(BroadcastHandler.class.getName(), type);
        LocalBroadcastManager.getInstance(context).sendBroadcast(backgroundServiceFilter);
    }

    public void toBookingActionActivity(int type) {
        bookingActionActivityFilter.putExtra(BookingActionActivity.class.getName() + BroadcastHandler.class.getSimpleName(), type);
        LocalBroadcastManager.getInstance(context).sendBroadcast(bookingActionActivityFilter);
    }

    public void toBookingCompleteActivity(int type) {
        bookingCompleteActivityFilter.putExtra(BookingCompleteActivity.class.getName() + BroadcastHandler.class.getSimpleName(), type);
        LocalBroadcastManager.getInstance(context).sendBroadcast(bookingCompleteActivityFilter);
    }

}
