package pikcel.com.pikcelclient;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class SocketIOUtil {

    public SocketIOUtil(){

    }

    /**
     * Format of Socket Server Request and Response
     *
     * {"status": true, "message": "play", "code": 2000, "result": []}
     *
     * This will be maintained further for communication between the client.
     * */

    public String createMessage(String request, @Nullable Boolean status, @Nullable Integer code, @Nullable String[] result) throws JSONException {

        /**
         * <request> parameter is a string that provides the instruction to be sent to the server.
         * Typically this will be "PLAY", "PAUSE", "PREVIOUS", "NEXT"
         * @Nullable <status> parameter is a boolean that provides the status of the request.
         * @Nullable <code> parameter is an integer that provides the status code of the response message.
         * @Nullable <result> parameter is a JSONArray that provides the result objects.
         * */


        JSONObject message = new JSONObject();
        message.put("status", status);
        message.put("message", request);
        message.put("code", code);
        message.put("result", new JSONArray(result));


        //System.out.print(message.toString());
        return message.toString();
    }

    public String createMessage(String request) throws JSONException {

        /**
         * <request> parameter is a string that provides the instruction to be sent to the server.
         * Typically this will be "PLAY", "PAUSE", "PREVIOUS", "NEXT"
         * */

        JSONObject message = new JSONObject();
        message.put("status", true);
        message.put("message", request);
        message.put("code", 200);
        message.put("result", new JSONArray());

        System.out.println(message.toString());

        return message.toString();
    }

    public String createMessage(String request, @Nullable Boolean status, @Nullable Integer code) throws JSONException {

        /**
         * <request> parameter is a string that provides the instruction to be sent to the server.
         * Typically this will be "PLAY", "PAUSE", "PREVIOUS", "NEXT"
         * @Nullable <status> parameter is a boolean that provides the status of the request.
         * @Nullable <code> parameter is an integer that provides the status code of the response message.
         * */

        JSONObject message = new JSONObject();
        message.put("status", status == null ? true: status);
        message.put("message", request);
        message.put("code", code == null ? 200: code);
        message.put("result", new JSONArray());

        return message.toString();
    }

    private String arrayToStringArray(String[] array){
        StringBuilder result = new StringBuilder();
        result.append("[");
        for(int i = 0; i< array.length; i++){
            result.append("\"" + array[i] + "\"");
            if(i < array.length-1){
                result.append(",");
            }
        }
        result.append("]");
        return result.toString();
    }

}
