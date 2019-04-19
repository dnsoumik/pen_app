package in.xlayer.f2h.driver;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManagement {
    public static final String APP_ORDER_MANAGEMENT_TAG = BuildConfig.APPLICATION_ID + "$GET#APPLICATION#ORDER#DATA";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static final String ORDER_PICKEDUP_TEXT = "Picked Up";
    public static final String ORDER_TRIP_STARTED_TEXT = "Start Trip";
    public static final String ORDER_DELIVERED_TEXT = "Deliver";

    /**
     * Current Order Details
     *
     * Component List
     * -------------------
     *
     * 1. ACTIVE_ORDER : BOOL (If there is an active order)
     * 2. ACTIVE_ORDER_ID : STRING (Current Order Id)
     * 3. ACTIVE_ORDER_STATUS: STRING (Current Order Status)
     *
     * Options for ACTIVE_ORDER_STATUS
     * A. Start Trip
     * B. Picked Up
     * C. Delivered
     * */

    public SharedPreferenceManagement(Context context){
        sharedPreferences = context.getSharedPreferences(APP_ORDER_MANAGEMENT_TAG, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createCurrentOrderRecord(String orderId){
        if(hasActiveOrder()){
            clearActiveOrder();
            setHasActiveOrder(true);
            setActiveOrderId(orderId);
        }
    }

    public boolean hasActiveOrder(){
        return sharedPreferences.getBoolean("ACTIVE_ORDER", false);
    }

    public String getActiveOrderId(){
        return sharedPreferences.getString("ACTIVE_ORDER_ID", "");
    }

    public void clearActiveOrder(){
        editor.clear();
        editor.commit();
    }

    public void setActiveOrderId(String orderId){
        editor.putString("ACTIVE_ORDER_ID", orderId);
        editor.commit();
    }

    public void setHasActiveOrder(Boolean hasActiveOrder){
        editor.putBoolean("ACTIVE_ORDER", hasActiveOrder);
        editor.commit();
    }

    public void setOrderTripStarted(){
        editor.putString("ACTIVE_ORDER_STATUS", ORDER_TRIP_STARTED_TEXT);
        editor.commit();
    }

    public void setOrderPickedUp(){
        editor.putString("ACTIVE_ORDER_STATUS", ORDER_PICKEDUP_TEXT);
        editor.commit();
    }

    public void setOrderDeliver(){
        editor.putString("ACTIVE_ORDER_STATUS", ORDER_DELIVERED_TEXT);
        editor.commit();
    }

    public String getActiveOrderStatus(){
        return sharedPreferences.getString("ACTIVE_ORDER_STATUS", "");
    }

    public String getNextOrderState(){
        if(hasActiveOrder()){
            switch(getActiveOrderStatus()){
                case ORDER_TRIP_STARTED_TEXT:
                    return ORDER_PICKEDUP_TEXT;
                case ORDER_PICKEDUP_TEXT:
                    return ORDER_DELIVERED_TEXT;
                default:
                    return null;
            }
        }else{
            return null;
        }
    }

    public String getActiveOrderState(){
        return sharedPreferences.getString("ACTIVE_ORDER_STATUS", "");
    }
}
