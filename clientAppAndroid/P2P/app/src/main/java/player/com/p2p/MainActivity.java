package player.com.p2p;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button discover;
    ListView wifiDeviceList;

    IntentFilter intentFilter;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    WifiP2PBroadcastReceiver receiver;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        discover = (Button) findViewById(R.id.wifi_discovery);
        wifiDeviceList = (ListView) findViewById(R.id.wifi_device_list);

        intentFilter = new IntentFilter();

        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        receiver = new WifiP2PBroadcastReceiver(manager, channel, this);

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        // Do nothing
                        Log.e("WIFIDiscovery", " SUCCESS");
                        Toast.makeText(getApplicationContext(), "Discovery started successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Log.e("WIFIDiscovery", " FAILED");

                        Toast.makeText(getApplicationContext(), "Discovery start failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        wifiDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final WifiP2pDevice device = deviceArray[position];
                WifiP2pConfig  config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(), "Not connected", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];

                int index = 0;

                for(WifiP2pDevice device: peerList.getDeviceList()){
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                wifiDeviceList.setAdapter(adapter);

                if(peers.size() == 0){
                    Log.e("PEERLIST", "No device found");
                    Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Log.e("PEERLIST", "Size: " + Integer.toString(peers.size()));
                    Toast.makeText(getApplicationContext(), "Device list soze: "+ Integer.toString(peers.size()), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
                Toast.makeText(getApplicationContext(), "This is Server", Toast.LENGTH_SHORT).show();
            }else if(wifiP2pInfo.groupFormed){
                Toast.makeText(getApplicationContext(), "This is Client", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
