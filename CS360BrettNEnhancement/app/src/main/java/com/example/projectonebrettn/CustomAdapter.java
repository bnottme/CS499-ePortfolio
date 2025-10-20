package com.example.projectonebrettn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Work Cited: Stevdza-san https://www.youtube.com/watch?v=hJPk50p7xwA

// This class connects the event data to the RecyclerView list on screen.
// It tells the RecyclerView how each row should look and what data to show.
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private final Context context;
    private final Activity activity;

    // Lists that hold event data
    private final ArrayList<Integer> ids;
    private final ArrayList<String> names;
    private final ArrayList<String> addresses;
    private final ArrayList<String> dates;

    // Gets the data and where to show it
    public CustomAdapter(Activity activity, Context context, ArrayList<Integer> ids,
                         ArrayList<String> names, ArrayList<String> addresses, ArrayList<String> dates){
        this.activity = activity;
        this.context = context;
        this.ids = ids;
        this.names = names;
        this.addresses = addresses;
        this.dates = dates;
    }

    // This sets up the look of each item in the list
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    // This puts the correct data into each row
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Get the event information from each list
        holder.event_number.setText(String.valueOf(ids.get(position))); // event ID
        holder.event_name.setText(names.get(position));                  // event name
        holder.event_address.setText(addresses.get(position));           // event address
        holder.event_date.setText(dates.get(position));                  // event date

        // When you tap on an event it opens the AddEvent screen to edit that event
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, AddEvent.class);
            i.putExtra("event_id", ids.get(position)); // send ID of clicked event
            i.putExtra("is_edit", true); // tell next screen we are editing
            activity.startActivity(i);
        });
    }

    // This tells how many items are in the list
    @Override
    public int getItemCount() {
        return ids.size();
    }

    // Holds the layout elements for each row event number, name, address, date
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public final TextView event_number, event_name, event_address, event_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            event_number = itemView.findViewById(R.id.event_number);
            event_name = itemView.findViewById(R.id.event_name);
            event_address = itemView.findViewById(R.id.event_address);
            event_date = itemView.findViewById(R.id.event_date);
        }
    }
}