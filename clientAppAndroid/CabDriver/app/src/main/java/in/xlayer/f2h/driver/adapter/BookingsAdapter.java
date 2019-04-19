package in.xlayer.f2h.driver.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import in.xlayer.f2h.driver.R;
import in.xlayer.f2h.driver.activity.BookingActionActivity;
import in.xlayer.f2h.driver.handler.StaticMemory;
import in.xlayer.f2h.driver.other.PicassoCircleTransformation;
import in.xlayer.f2h.driver.other.booking.response.Datum;
import in.xlayer.f2h.driver.util.TimeUtil;


public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {

    private String TAG = BookingsAdapter.class.getSimpleName();
    private Context context = null;
    private List<Datum> data = null;

    public BookingsAdapter(Context context, List<Datum> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public BookingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.adapter_bookings, parent, false);
        return new BookingsAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BookingsAdapter.ViewHolder holder, int position) {
        final Datum info = data.get(position);
        Drawable white = context.getDrawable(R.drawable.white_curved);
        if (info.getVehicleInfo().size() != 0) {
            if (!Objects.equals(info.getVehicleInfo().get(0).getImage(), "")) {
                Picasso.with(context)
                        .load(info.getVehicleInfo().get(0).getImage())
                        .error(Objects.requireNonNull(white))
                        .into(holder.v_img);
            }
        }
        if (info.getUserInfo().size() != 0) {
            if (!Objects.equals(info.getUserInfo().get(0).getProfilePicture(), "")) {
                Picasso.with(context)
                        .load(info.getUserInfo().get(0).getProfilePicture())
                        .error(Objects.requireNonNull(white))
                        .transform(new PicassoCircleTransformation())
                        .into(holder.driver_img);
            }
        }
        if (!Objects.equals(info.getStatus(), "")) {
            switch (info.getStatus()) {
                case "PENDING":
                    holder.status.setTextColor(context.getResources().getColor(R.color.
                            status_light_brown));
                    holder.status.setText("Pending");
                    break;
                case "CONFIRMED":
                    holder.status.setTextColor(context.getResources().getColor(R.color.
                            status_light_brown));
                    holder.status.setText("Confirmed");
                    break;
                case "PROG":
                    holder.status.setTextColor(context.getResources().getColor(R.color.
                            status_light_brown));
                    holder.status.setText("In Progress");
                    break;
                case "COMPLETED":
                    holder.status.setTextColor(context.getResources().getColor(R.color.
                            status_green));
                    holder.status.setText("Completed");
                    break;
                case "CANCELLED":
                    holder.status.setTextColor(context.getResources().getColor(R.color.colorError));
                    holder.status.setText("Cancelled");
                    break;
                case "ACTIVE":
                    holder.status.setTextColor(context.getResources().getColor(R.color.
                            status_green));
                    holder.status.setText("Active");
                    break;
                case "U_CANCELLED":
                    holder.status.setTextColor(context.getResources().getColor(R.color.colorError));
                    holder.status.setText("Cancelled");
                    break;
                case "A_CANCELLED":
                    holder.status.setTextColor(context.getResources().getColor(R.color.colorError));
                    holder.status.setText("Cancelled");
                    break;
                case "D_CANCELLED":
                    holder.status.setTextColor(context.getResources().getColor(R.color.colorError));
                    holder.status.setText("Cancelled");
                    break;
            }
        }
        try {
            if (info.getStartTime() > 0) {
                holder.start_time.setText(TimeUtil.parseStampFromUnixToLocalWithAMPM(info.
                        getStartTime()));
            } else {
                holder.start_time.setText("N/A");
            }
        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            holder.start_time.setText("N/A");
        }
        if (info.getVehicleInfo().size() != 0) {
            holder.v_type_and_reg_no.setText(info.getVehicleInfo().get(0).getType() + " : " + info.
                    getVehicleInfo().get(0).getRegNum());
        } else {
            holder.v_type_and_reg_no.setText("");
        }
        if (info.getPaymentInfo().size() != 0) {
            holder.total_cost.setText(context.getResources().getString(R.string.rs_symbol) + info.getPaymentInfo().get(0).getTotalAmount());
        }
        try {
            holder.start_loc_addr.setText(info.getPickInfo().get(0).getAddr());
            holder.end_loc_addr.setText(info.getDropInfo().get(0).getAddr());
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        holder.booking_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(info.getStatus(), "CONFIRMED") || Objects.equals(info.
                        getStatus(), "ACTIVE")
                        || Objects.equals(info.getStatus(), "PROG") || Objects.equals(info.
                        getStatus(), "COMPLETED")) {
                    StaticMemory.SELECTED_BOOKING = info;
                    Log.e(TAG, "onClick: ID: " + info.getId());
                    Intent mainIntent = new Intent(context, BookingActionActivity.class);
                    context.startActivity(mainIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView v_img, driver_img;
        TextView start_time, v_type_and_reg_no, total_cost,
                start_loc_addr, end_loc_addr, status;
        View booking_info;

        ViewHolder(View itemView) {
            super(itemView);
            booking_info = itemView.findViewById(R.id.booking_info);
            v_img = itemView.findViewById(R.id.v_logo);
            driver_img = itemView.findViewById(R.id.driver_img);
            status = itemView.findViewById(R.id.booking_status);
            start_time = itemView.findViewById(R.id.booking_start_time);
            v_type_and_reg_no = itemView.findViewById(R.id.v_type_and_reg_number);
            total_cost = itemView.findViewById(R.id.total_cost);
            start_loc_addr = itemView.findViewById(R.id.start_location_addr);
            end_loc_addr = itemView.findViewById(R.id.stop_location_addr);
        }
    }
}
