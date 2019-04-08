package pikcel.com.pikcelclient.AppActionFragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import pikcel.com.pikcelclient.AppAction;
import pikcel.com.pikcelclient.BuildConfig;
import pikcel.com.pikcelclient.R;
import pikcel.com.pikcelclient.SocketIOUtil;

public class RemoteControlFragment extends Fragment {

    View parentHolder;
    WebSocketClient remoteClient;
    ImageView previous, next, play, pause;
    FloatingActionButton playlistFab;
    SocketIOUtil socketUtil = new SocketIOUtil();
    Boolean playState = false;
    TextView playingFileName;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        super.onCreate(savedInstanceState);

        URI uri;
        try{
            uri = new URI("wss://dev.trakiga.com/sample/api/rt_socket");
        }catch (URISyntaxException e){
            e.printStackTrace();
            return null;
        }


        Map<String, String> headers = new ArrayMap<>();
        headers.put("Origin", BuildConfig.APPLICATION_ID);



        remoteClient = new WebSocketClient(uri, headers) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("SOCKET", "CONNECTED");
            }

            @Override
            public void onMessage(String message) {

                Log.d("SOCKET", message);
                try {
                    JSONObject response = new JSONObject(message);
                    Log.d("SERVER-SAYS: ", response.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("SOCKET", "DISCONNECTED");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        parentHolder = inflater.inflate(R.layout.remote_fragment_layout, container, false);

        remoteClient.connect();

        previous = parentHolder.findViewById(R.id.btn_prev);
        next = parentHolder.findViewById(R.id.btn_next);
        play = parentHolder.findViewById(R.id.btn_play);
        playlistFab = parentHolder.findViewById(R.id.play_list);
        playingFileName = (TextView) parentHolder.findViewById(R.id.active_file_name_remote);

        playlistFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent playListActivity = new Intent(getActivity(), PlayList.class);
                startActivityForResult(playListActivity, FileFragment.FILENAME_SELECTION_CODE);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    remoteClient.send(socketUtil.createMessage("PREV"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    remoteClient.send(socketUtil.createMessage("NEXT"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(playState){
                        remoteClient.send(socketUtil.createMessage("PAUSE"));
                        playState = false;
                        play.setImageResource(R.drawable.ic_play_arrow);
                    }else{
                        remoteClient.send(socketUtil.createMessage("PLAY"));
                        playState = true;
                        play.setImageResource(R.drawable.ic_pause);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return parentHolder;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if(requestCode == FileFragment.FILENAME_SELECTION_CODE){
            try {
                String[] resultArray = {resultData.getStringExtra("FILENAME")};
                String message = socketUtil.createMessage("PLAY", true, 200, resultArray);
                System.out.println(message);
                remoteClient.send(message);
                playingFileName.setText(resultData.getStringExtra("FILENAME"));
            } catch (Exception e) {
                Log.e("ERROR", "something went wrong");
                e.printStackTrace();
            }
        }
    }
}
