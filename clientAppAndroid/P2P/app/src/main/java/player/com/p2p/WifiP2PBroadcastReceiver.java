package player.com.p2p;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WifiP2PBroadcastReceiver extends BroadcastReceiver {

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    MainActivity activity;

    public WifiP2PBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity){
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                //activity.setIsWifiP2pEnabled(true);
                Log.e("WIFIP2PSTATE", "ACTIVE");
            } else {
                //activity.setIsWifiP2pEnabled(false);
                Log.e("WIFIP2PSTATE", "INACTIVE");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed! We should probably do something about
            // that.

            if (this.manager != null) {
                this.manager.requestPeers(channel, activity.peerListListener);
            }
            Log.e("WIFIP2P", "P2P peers changed");

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed! We should probably do something about
            // that.

            if(manager==null){
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo.isConnected()){
                manager.requestConnectionInfo(channel, activity.connectionInfoListener);
            }else{
                Toast.makeText(activity.getApplicationContext(), "Device Disconnected", Toast.LENGTH_SHORT).show();
            }

            Log.e("BroadCastP2P", "Connection state changed");

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
//            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
            WifiP2pDevice device = (WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            Log.e("WIFI Device", device.deviceName);
            //Log.e("WIFIP2P", "Devices found");

        }
    }
}
