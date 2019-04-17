package player.com.p2p;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.HashMap;
import java.util.List;

import in.gauriinfotech.commons.Commons;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    Button discover, sendMsgBtn, UploadBtn;
    ListView wifiDeviceList;
    private static final int READ_REQUEST_CODE = 42;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    /**
     * Following two variables are important as they will contain the FilePath of the in queue
     * file for upload and also the reference id for the same.
     *
     * By default the reference id is "1111111111"
     * */
    String UploadQueueFilePath;
    String UploadQueueReferenceId = "1111111111";

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
                            /**
                             * No need to send a message to any client as the responses
                             * are handled from SocketServer.
                             * */
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
                if(checkPermissionREAD_EXTERNAL_STORAGE(MainActivity.this)){
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, READ_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                HashMap<String, String> fileInfo = dumpFileMetaData(uri);
                String mimeType = getMimeType(uri);

                /** Create request for Server */

                JSONObject fileUploadInfoRequest = new JSONObject();
                try {
                    fileUploadInfoRequest.put("instruction", SocketServer.NEW_FILE_INFO);
                    fileUploadInfoRequest.put("file_name", fileInfo.get("DisplayName"));
                    fileUploadInfoRequest.put("extension", "");
                    fileUploadInfoRequest.put("mime_type", mimeType);

                    masterClient.send(fileUploadInfoRequest.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String realPath = Commons.getPath(data.getData(), getApplicationContext());

                UploadQueueFilePath  = realPath;
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
                    this.server = new SocketServer(address, getApplicationContext());
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
                            try {
                                JSONObject response = new JSONObject(message);
                                switch (response.getString("response_code")){
                                    //TODO: Handle test cases here
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            File ourFile = new File(realPath);
//                            byte[] bytesArray = new byte[(int) ourFile.length()];
//                            FileInputStream fis = null;
//                            try {
//                                fis = new FileInputStream(ourFile);
//                                fis.read(bytesArray); //read file into bytes[]
//                                fis.close();
//
//                                // Send the file through socket
//
//                                masterClient.send(bytesArray);
//
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
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

                }catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
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

    public HashMap<String, String> dumpFileMetaData(Uri uri) {

        HashMap<String, String> fileInfo = new HashMap<>();

        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            cursor = getApplicationContext().getContentResolver()
                    .query(uri, null, null, null, null, null);
        }

        try {

            if (cursor != null && cursor.moveToFirst()) {


                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + displayName);
                fileInfo.put("DisplayName", displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                String size = null;
                fileInfo.put("Size",size);
                if (!cursor.isNull(sizeIndex)) {
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
        }
        return fileInfo;
    }

    public String getMimeType(Uri uri){
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        return contentResolver.getType(uri);
    }
}
