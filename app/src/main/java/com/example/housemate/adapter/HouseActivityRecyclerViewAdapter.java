package com.example.housemate.adapter;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.housemate.HouseActivity;
import com.example.housemate.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class HouseActivityRecyclerViewAdapter extends RecyclerView.Adapter<HouseActivityRecyclerViewAdapter.ViewHolder> {
    private List<HouseActivity> houseActivityList;
    private Context context;

    public HouseActivityRecyclerViewAdapter(List<HouseActivity> houseActivityList, Context context) {
        this.houseActivityList = houseActivityList;
        this.context = context;
    }
    @NonNull
    @Override
    public HouseActivityRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.house_activity_row, parent, false);
        return new HouseActivityRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseActivityRecyclerViewAdapter.ViewHolder holder, int position) {
        HouseActivity houseActivity = Objects.requireNonNull(houseActivityList).get(position);
        holder.message.setText(houseActivity.getMessage());
        String date = houseActivity.getDate();
        String timeAgoMessage = displayTimeAgo(date);
        holder.date.setText(timeAgoMessage);
        String type_string = houseActivity.getType();
        holder.type.setText(type_string.toUpperCase().charAt(0) + "");
    }

    String displayTimeAgo(String date) {
        /* the date gets formatted to display correctly in the activity view */

        SimpleDateFormat sdf = new SimpleDateFormat(("dd-MM-yyyy HH:mm:ss"));
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        try {
            long time = sdf.parse(date).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
                    return ago + "";
        } catch (ParseException e) {

        }
        return "";
    }

//recycler
    @Override
    public int getItemCount() { /* let the recycler know how much data it will be receiving */
        return Objects.requireNonNull(houseActivityList).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView date;
        public TextView type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.house_activity_message);
            date = itemView.findViewById(R.id.house_activity_date);
            type = itemView.findViewById(R.id.house_activity_type);
        }
    }


}
