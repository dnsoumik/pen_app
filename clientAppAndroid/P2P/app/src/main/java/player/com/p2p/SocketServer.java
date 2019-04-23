package player.com.p2p;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class SocketServer extends WebSocketServer {
    String SOCKET_TAG = "SocketServer";

    /** GENERAL REQUEST CODES FROM CLIENT */
    public static final int NEW_FILE_INFO = 1;
    public static final int MEDIA_PLAY = 2;
    public static final int MEDIA_PAUSE = 3;
    public static final int MEDIA_PLAY_STARTOVER = 4;
    public static final int MEDIA_PLAY_NEXT_FILE = 5;
    public static final int MEDIA_PLAY_PREVIOUS_FILE = 6;
    public static final int MEDIA_PLAY_AT_SEEK = 7;
    public static final int MEDIA_PLAY_LIST = 8;

    /** GENERAL RESPONSE CODE TO CLIENT */
    public static final int MEDIA_LIST_RESPONSE = 9;
    public static final int FILE_INFO_RESPONSE = 10;
    public static final int FILE_UPLOAD_RESPONSE = 11;
    public static final int MEDIA_CONTROL_RESPONSE = 12;

    Context applicationContext;

    /**New file components*/
    String fileName, reference_number, extension;

    public SocketServer(InetSocketAddress address, Context applicationContext){
        super(address);
        this.applicationContext = applicationContext;
    }

    public void checkoutParentFolderAvailability() throws IOException {
        final File f = new File(Environment.getExternalStorageDirectory() + "/"
                + this.applicationContext.getPackageName() + "/" + this.fileName);

        /** Check if the parent directory exist */
        File dir = new File(f.getParent());
        Log.e("File Creation Process","Parent Folder: " + dir.getPath());
        if (!dir.exists()){
            Log.e("File Creation Process", "Creating parent directory");
            dir.mkdirs();
        }else{
            Log.e("File Creation Process", "Parent directory already exists");
        }
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
        try {
            JSONObject instruction = new JSONObject(message);
            switch(instruction.getInt("instruction")){
                case NEW_FILE_INFO:
                    /**
                     * New file info JSON request format
                     * {
                     *      "instruction" : <int> Refer from general server instruction codes,
                     *      "file_name" : <String> Name of the file,
                     *      "reference_id" : <String> Reference id (can be any unique text)
                     *      "extension" : <String> Extension of the new file.
                     * }
                     * */
                    this.fileName = instruction.getString("file_name");
                    this.reference_number = instruction.getString("reference_id");
                    //this.extension = instruction.getString("extension");

                    /** Check if the parent folder already exist if not create that */

                    this.checkoutParentFolderAvailability();

                    /**
                     * Send the client confirmation that the file information is stored
                     *
                     * JSON format for response
                     * {
                     *     "status" : <Bool> request Status,
                     *     "reference_id" : <String> Reference id of the request received,
                     *     "code" : <int> status code 200 for OK response, 500 for Exception
                     *     "response_code" : <int> response code, refer General Response Code to Client
                     * }
                     *
                     * */

                    JSONObject response = new JSONObject();
                    response.put("status", true);
                    response.put("reference_id", this.reference_number);
                    response.put("code", 200);
                    response.put("response_code", FILE_INFO_RESPONSE);

                    /** Send acknowledgement to Client */
                    conn.send(response.toString());

                    break;

                case MEDIA_PLAY:
                    break;

                case MEDIA_PLAY_STARTOVER:
                    break;

                case MEDIA_PAUSE:
                    break;

                case MEDIA_PLAY_NEXT_FILE:
                    break;

                case MEDIA_PLAY_PREVIOUS_FILE:
                    break;

                case MEDIA_PLAY_AT_SEEK:
                    break;

                case MEDIA_PLAY_LIST:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        //super.onMessage(conn, message);

        Log.e("SOCKET-RESPONSE", "Receving bytes: " + Integer.toString(message.capacity()));

        byte[] bytes = new byte[message.capacity()];

        final File f = new File(Environment.getExternalStorageDirectory() + "/"
                + this.applicationContext.getPackageName() + "/" + this.fileName);

        CreateFile storeNewFile = new CreateFile(f.getAbsolutePath());
        storeNewFile.writeByte(bytes);

        /**
         * Send response to the client about the successful file creation
         *
         * {
         *     "response_code" : <int> response code, refer General Response Code to Client,
         *     "status" : <Bool> request response code,
         *     "code" : <int> Status code 200 for OK, 500 for Exception
         * }
         * */

        JSONObject response = new JSONObject();
        try {
            response.put("status", true);
            response.put("reference_id", this.reference_number);
            response.put("code", 200);
            response.put("response_code", FILE_UPLOAD_RESPONSE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //this.extension = null;
        this.fileName = null;
        this.reference_number = null;

        Log.e("SOCKET-RESPONSE", "Upload is complete on server side");

        /** Send acknowledgement to Client */
        conn.send(response.toString());

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
