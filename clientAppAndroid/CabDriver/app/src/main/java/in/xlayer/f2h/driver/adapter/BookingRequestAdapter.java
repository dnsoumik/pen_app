package in.xlayer.f2h.driver.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.handler.BroadcastHandler;
import in.xlayer.f2h.driver.handler.HttpRequestBuilder;
import in.xlayer.f2h.driver.util.TimeUtil;
import in.xlayer.f2h.driver.ws.request.WSBookingRequest;

public class BookingRequestAdapter extends RecyclerView.Adapter<BookingRequestAdapter.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private List<WSBookingRequest> data;
    private HttpRequestBuilder httpRequestBuilder;
    private AlertDialog.Builder sureAlert;
    private BroadcastHandler broadcastHandler;
    private AlertDialog alert;

    public BookingRequestAdapter(Context context, List<WSBookingRequest> data) {
        this.context = context;
        this.data = data;
        this.broadcastHandler = new BroadcastHandler(context);
        this.httpRequestBuilder = new HttpRequestBuilder(context);
        sureAlert = new AlertDialog.Builder(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.adapter_booking_requests, parent, false);
        return new BookingRequestAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final WSBookingRequest info = data.get(position);
        try {
            holder.request_time.setText(TimeUtil
                    .parseTimeStampToDateFormat(info.getTime(),
                            "hh:mm:ss a"));
            holder.price.setText(context.getResources().getString(R.string.rs_symbol) + " " + info.getEstimate().get(0).getPrice().toString());
            holder.start_location.setText(info.getStartPoint().get(0).getAddr());
            holder.stop_location.setText(info.getEndPoint().get(0).getAddr());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        holder.accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = "You can't cancel after accepting this request, Are you Sure?";
                sureAlert.setMessage(tmp).
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                broadcastHandler.toBackgroundService(3);
                                httpRequestBuilder.acceptRequest(info.getRequestId());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        broadcastHandler.toBackgroundService(3);
                                        alert.cancel();
                                    }
                                }
                        );
                alert = sureAlert.create();
                alert.show();
            }
        });
        holder.reject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmp = "You can't accept after rejecting this request, Are you Sure?";
                sureAlert.setMessage(tmp).
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                broadcastHandler.toBackgroundService(3);
                                httpRequestBuilder.rejectRequest(info.getRequestId());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        broadcastHandler.toBackgroundService(3);
                                        alert.cancel();
                                    }
                                }
                        );
                alert = sureAlert.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView price, start_location, stop_location, request_time;
        Button accept_button, reject_button;

        ViewHolder(View itemView) {
            super(itemView);
            start_location = itemView.findViewById(R.id.start_location_addr);
            stop_location = itemView.findViewById(R.id.stop_location_addr);
            accept_button = itemView.findViewById(R.id.booking_accept);
            reject_button = itemView.findViewById(R.id.booking_reject);
        }
    }
}
