package in.xlayer.f2h.driver.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.handler.LocalMemory;
import in.xlayer.f2h.driver.util.TimeUtil;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class RequestAdapter extends  RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private Context context;
    private JSONArray data = null;
    private LocalMemory localMemory = null;

    public RequestAdapter(Context context, JSONArray data){
        this.context = context;
        this.data = data;
        this.localMemory = new LocalMemory(context);
    }


    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.adapter_request, parent, false);
        Log.e("TAG", "onCreateViewHolder: RequestAdapter ");
        return new RequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
                JSONObject info = data.getJSONObject(position);
                Log.e(TAG, "Response in Request Adapter "+ info );
                String tmp = info.getJSONArray("start_point").getJSONObject(0)
                        .get("addr").toString();
                holder.start_loc_addr.setText(tmp);
                if (info.getInt("time") < 1) {
                    holder.start_time.setText("N/A");

                } else {
                    holder.start_time.setText(TimeUtil.parseStampFromUnixToLocalWithAMPM
                            (info.getInt("time")));
                }
                switch (info.getInt("status")) {
                    case 0:
                        holder.status.setTextColor(context.getResources().getColor(R.color.
                                status_light_brown));
                        holder.status.setText("Missed");
                        holder.icon.setImageResource(R.drawable.ic_missed_call);
                        holder.icon.setColorFilter(context.getResources().getColor(R.color.
                                status_light_brown));
                        break;
                    case 1:
                        holder.status.setTextColor(context.getResources().getColor(R.color.
                                status_green));
                        holder.status.setText("Accepted");
                        holder.icon.setImageResource(R.drawable.ic_phone_accept_call);
                        holder.icon.setColorFilter(context.getResources().getColor(R.color.
                                status_green));
                        break;
                    case 2:
                        holder.status.setTextColor(context.getResources().getColor(R.color.
                                colorError));
                        holder.status.setText("Rejected");
                        holder.icon.setImageResource(R.drawable.ic_phone_reject_call);
                        holder.icon.setColorFilter(context.getResources().getColor(R.color.
                                colorError));
                        break;
                    default:
                        holder.status.setTextColor(context.getResources().getColor(R.color.
                                colorError));
                        holder.status.setText("N/A");
                        holder.icon.setImageResource(R.drawable.ic_adb);
                        break;
                }
        } catch (JSONException | NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            holder.start_time.setText("N/A");
            holder.start_loc_addr.setText("N/A");
            holder.status.setText("N/A");
            holder.icon.setImageResource(R.drawable.ic_adb);
        }
    }

    @Override
    public int getItemCount() {
        return data.length();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView start_time, start_loc_addr, status;
        ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.request_status);
            start_time = itemView.findViewById(R.id.request_start_time);
            start_loc_addr = itemView.findViewById(R.id.start_req_location_addr);
            icon = itemView.findViewById(R.id.log_icon);
        }
    }
}
