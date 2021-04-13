package com.example.housemate.adapter;
import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.example.housemate.Bills.BillActivity;
import com.example.housemate.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class BillsActivityRecyclerViewAdapter extends RecyclerView.Adapter<BillsActivityRecyclerViewAdapter.ViewHolder> {
    private List<BillActivity> billsActivityList;
    private Context context;

    public BillsActivityRecyclerViewAdapter(List<BillActivity> billsList, Context context) {
        this.billsActivityList = billsList;
        this.context = context;
    }
    @NonNull
    @Override
    public BillsActivityRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bills_activity_row, parent, false);
        return new BillsActivityRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillsActivityRecyclerViewAdapter.ViewHolder holder, int position) {
        BillActivity billActivity = Objects.requireNonNull(billsActivityList).get(position);
        holder.message.setText(billActivity.getMessage());
        String date = billActivity.getDate();
        String timeAgoMessage = displayTimeAgo(date);
        holder.date.setText(timeAgoMessage);
        //        Date d = null;
//        PrettyTime p = new PrettyTime();
//        try {
//            d = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(date);
//            Log.d("bills", d.toString());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        holder.date.setText(p.format(d));
    }

    String displayTimeAgo(String date) {
        /* the date gets formatted to display correctly in the activity view */

        SimpleDateFormat sdf = new SimpleDateFormat(("dd-MM-yyyy hh:mm:ss"));
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        try {
            long time = sdf.parse(date).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
                    return ago + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public int getItemCount() { /* let the recycler know how much data it will be receiving */
        return Objects.requireNonNull(billsActivityList).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.bills_activity_message);
            date = itemView.findViewById(R.id.bills_activity_date);
        }
    }


}
