package player.com.p2p;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.io.ByteStreams;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import in.gauriinfotech.commons.Commons;

public class MainActivity extends AppCompatActivity {

    Button discover, sendMsgBtn, UploadBtn;
    ListView wifiDeviceList;
    private static final int READ_REQUEST_CODE = 42;

    IntentFilter intentFilter;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    WifiP2PBroadcastReceiver receiver;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    static final int MESSAGE_READ = 1;

    Server server;
    Client client;
    SendReceive sendReceive;

    String message;

    SocketServer masterSocketServer;
    WebSocketClient masterClient;

    int MASTER_ROLE; // 1 When Server, 0 When Client

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        discover = (Button) findViewById(R.id.wifi_discovery);
        sendMsgBtn = (Button) findViewById(R.id.wifi_send);
        wifiDeviceList = (ListView) findViewById(R.id.wifi_device_list);
        UploadBtn = (Button) findViewById(R.id.wifi_upload);

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
                        sendMsgBtn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(int i) {
                        Log.e("WIFIDiscovery", " FAILED");

                        Toast.makeText(getApplicationContext(), "Discovery start failed", Toast.LENGTH_SHORT).show();
                        sendMsgBtn.setVisibility(View.GONE);
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

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("SOCKET MESSAGE", "sending message" + message);
                        if(MASTER_ROLE == 1){
                            // TODO: Find a way to send message to client
                        }else{
                            masterClient.send(message.getBytes());
                        }
                    }
                }).start();
            }
        });

        UploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                //dumpFileMetaData(uri);

                String realPath = Commons.getPath(data.getData(), getApplicationContext());
                File ourFile = new File(realPath);
                byte[] bytesArray = new byte[(int) ourFile.length()];
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(ourFile);
                    fis.read(bytesArray); //read file into bytes[]
                    fis.close();

                    // Send the file through socket

                    masterClient.send(bytesArray);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String responseMessage = (String) msg.obj;
            Log.e("RESONSE MSG", responseMessage);
            switch(msg.what){
                case MESSAGE_READ:
                    byte[] readBuffer = (byte[]) msg.obj;
                    String tempMsg = new String(readBuffer, 0, msg.arg1);
                    Log.d("SOCKET RESPONSE", tempMsg);
                    Toast.makeText(getApplicationContext(), "Message: " + tempMsg, Toast.LENGTH_SHORT).show();
                    break;
            }

            return true;
        }
    });

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
                message = "Server Sending message to Client";
                MASTER_ROLE = 1;

                /**Start the server thread*/
                server = new Server(groupOwnerAddress);
                server.start();

            }else if(wifiP2pInfo.groupFormed){
                Toast.makeText(getApplicationContext(), "This is Client", Toast.LENGTH_SHORT).show();
                message = "Client sending message to Server";
                MASTER_ROLE = 0;

                /**Start the client thread*/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        client = new Client(groupOwnerAddress);
                        client.start();
                    }
                }, 3000);
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

    public class Server extends Thread {
        SocketServer server;
        String hostAddress;

        public Server(InetAddress ownerAddress){
            this.hostAddress = ownerAddress.getHostAddress();
        }

        @Override
        public void run() {
            if(masterSocketServer == null){
                try {
                    InetSocketAddress address  = new InetSocketAddress(hostAddress, 9999);
                    this.server = new SocketServer(address);
                    this.server.start();
                    masterSocketServer = this.server;
                    Log.d("SOCKET SERVER", "Server established");

                    /**Start the send receive thread*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class SendReceive extends Thread {
        Socket socket;
        InputStream inputStream;
        OutputStream outputStream;

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while(socket != null){
                try {
                    bytes = inputStream.read(buffer);
                    if(bytes > 0){
                        Log.d("SOCKET RESPONSE", "Response received");
                        //handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer);
                        Message msg = new Message();
                        String message = new String(ByteStreams.toByteArray(inputStream));
                        System.out.println("Response text is "+ message);
                        msg.obj = message;
                        msg.what = MESSAGE_READ;
                        Log.d("SOCKET MESSAGE", message);
                        handler.handleMessage(msg);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(final byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public SendReceive(Socket skt){
            this.socket = skt;
            try{
                this.inputStream = this.socket.getInputStream();
                this.outputStream = this.socket.getOutputStream();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public class Client extends Thread {
        WebSocketClient socket;
        String hostAddress;
        String CLIENT_SOCKET_TAG = "ClientSocket";

        public Client(InetAddress hostAddress){
            this.hostAddress = hostAddress.getHostAddress();
        }


        @Override
        public void run() {
            if(socket == null){
                try {
                    socket = new WebSocketClient(new URI("ws://" + this.hostAddress + ":9999")) {
                        @Override
                        public void onOpen(ServerHandshake handshakedata) {
                            Log.e(CLIENT_SOCKET_TAG, " Connected");
                        }

                        @Override
                        public void onMessage(String message) {
                            Log.e(CLIENT_SOCKET_TAG, " Message: " + message);
                        }

                        @Override
                        public void onClose(int code, String reason, boolean remote) {
                            Log.e(CLIENT_SOCKET_TAG, " Disconnected");
                        }

                        @Override
                        public void onError(Exception ex) {
                            Log.e(CLIENT_SOCKET_TAG, " Error");
                            ex.printStackTrace();
                        }
                    };
                    socket.connect();
                    masterClient = socket;
//                socket.connect(new InetSocketAddress(this.hostAddress,8888), 500);
//
//                /**Code for sending message*/
//                /**Start the send receive thread*/
//                sendReceive = new SendReceive(socket);
//                sendReceive.start();

//                sendMsgBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        sendReceive.write(message.getBytes());
//                    }
//                });

                }catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class SocketServer extends WebSocketServer {

        String SOCKET_TAG = "SocketServer";

        public SocketServer(InetSocketAddress address){
            super(address);
        }

        @Override
        public void onOpen(org.java_websocket.WebSocket conn, ClientHandshake handshake) {
            Log.e(SOCKET_TAG, " Server Listening...");
        }

        @Override
        public void onClose(org.java_websocket.WebSocket conn, int code, String reason, boolean remote) {
            Log.e(SOCKET_TAG, " Server Closed");
        }

        @Override
        public void onMessage(org.java_websocket.WebSocket conn, String message) {
            Log.e(SOCKET_TAG, " Server Message: " + message);
            conn.send("Thank you");
        }

        @Override
        public void onMessage(WebSocket conn, ByteBuffer message) {
            //super.onMessage(conn, message);
            byte[] bytes = new byte[message.capacity()];
        }

        @Override
        public void onError(org.java_websocket.WebSocket conn, Exception ex) {
            Log.e(SOCKET_TAG, " Server Error");
            ex.printStackTrace();
        }

        @Override
        public void onStart() {

        }
    }
}
