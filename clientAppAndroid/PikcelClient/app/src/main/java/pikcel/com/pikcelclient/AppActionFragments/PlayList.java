package pikcel.com.pikcelclient.AppActionFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pikcel.com.pikcelclient.AppAction;
import pikcel.com.pikcelclient.MainActivity;
import pikcel.com.pikcelclient.R;

import static java.security.AccessController.getContext;

public class PlayList extends Activity {

    RecyclerView recyclerView;
    PlayListAdpter adapter;
    FloatingActionButton uploadFab;
    ProgressDialog progressDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        recyclerView = findViewById(R.id.play_list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        ImageButton backButton = findViewById(R.id.btn_back_playlist);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent mainActivity = new Intent(PlayList.this, AppAction.class);
//                startActivity(mainActivity);
//                finish();
//            }
//        });

        uploadFab = findViewById(R.id.upload_btn);

        uploadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Intent fileUpload = new Intent(PlayList.this, FileFragment.class);
                                startActivity(fileUpload);
                                finish();
                            }
                        });

                    }
                }).start();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.GET, "https://dev.trakiga.com/sample/api/files", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("API Response", response);
                try {
                    final JSONObject res = new JSONObject(response);
                    if (!res.getBoolean("status")) {
                        final String errorText = res.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(PlayList.this)
                                        .setMessage(errorText)
                                        .setTitle("Oops! Something went wrong")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        }).create().show();

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    adapter = new PlayListAdpter(getApplicationContext(), res.getJSONArray("result"));
                                    recyclerView.setAdapter(adapter);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(PlayList.this)
                                .setTitle("Something went wrong")
                                .setMessage("This happens to the best of us.\n\nWe are already working to fix this.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).create().show();
                    }
                });
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(sr);
    }

    public class PlayListAdpter extends RecyclerView.Adapter<PlayListAdpter.ViewHolder>{

        public ArrayList<String> playList;
        private LayoutInflater mInflater;

        public PlayListAdpter(Context context, JSONArray fileList){
            this.mInflater = LayoutInflater.from(context);
            this.playList = new ArrayList<>();
            for(int i = 0; i<fileList.length(); i++){
                try {
                    this.playList.add(fileList.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.playlist_items, parent, false);
            return new ViewHolder(view, getApplication());
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.fileName.setText(playList.get(position));
        }

        @Override
        public int getItemCount() {
            return playList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView fileName;

            public ViewHolder(View itemView, final Context context) {
                super(itemView);
                fileName = itemView.findViewById(R.id.file_name_playlist);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position  = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            String file_name = playList.get(position);

                            Intent intent=new Intent();
                            intent.putExtra("FILENAME",file_name);
                            setResult(101,intent);
                            finish();

                        }
                    }
                });
            }
        }
    }
}
