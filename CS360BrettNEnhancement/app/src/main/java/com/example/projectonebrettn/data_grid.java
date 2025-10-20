package com.example.projectonebrettn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton; // ✅ import

import java.util.ArrayList;

// Work Cited: Stevdza-san https://www.youtube.com/watch?v=hJPk50p7xwA

// This screen shows a list of all events saved in the database.
public class data_grid extends AppCompatActivity {

    // RecyclerView displays the list of events on the screen
    private RecyclerView recyclerView;

    // Buttons for adding and deleting events
    private FloatingActionButton add_button, delete_button;

    // Our database helper to get event data
    private EventDataBase db;

    // Lists to hold data pulled from the database
    private final ArrayList<Integer> ids = new ArrayList<>();
    private final ArrayList<String> names = new ArrayList<>();
    private final ArrayList<String> addresses = new ArrayList<>();
    private final ArrayList<String> dates = new ArrayList<>();

    // Adapter to connect the data to the RecyclerView
    private CustomAdapter customAdapter;

    // Stores the username of the person who logged in
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_grid);

        // Get the username from the previous screen
        username = getIntent().getStringExtra("username");

        // Connect the layout elements to the Java variables
        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);
        delete_button = findViewById(R.id.delete_button);

        // Make a new connection to the Event database
        db = new EventDataBase(this);

        // Load event data from the database into our lists
        storeDataInArrays();

        // Set up the adapter to show data in the RecyclerView
        customAdapter = new CustomAdapter(data_grid.this, this, ids, names, addresses, dates);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(data_grid.this));

        // When the Add button is clicked go to the Add Event screen
        add_button.setOnClickListener(v -> {
            Intent i = new Intent(data_grid.this, AddEvent.class);
            i.putExtra("username", username); // send username to next screen
            startActivity(i);
        });

        // When the Delete button trash is clicked ask if we should delete ALL events
        delete_button.setOnClickListener(v -> {
            // If there is nothing to delete, show a quick message and stop
            if (ids.isEmpty()) {
                Toast.makeText(data_grid.this, "No events to delete", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show a confirmation box so the user does not delete by accident
            new AlertDialog.Builder(data_grid.this)
                    .setTitle("Delete all events?")
                    .setMessage("This will remove every event. This cannot be undone.")
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Delete everything from the table
                        int removed = db.deleteAllEvents();

                        // Clear the lists and reload from the database (now empty)
                        ids.clear();
                        names.clear();
                        addresses.clear();
                        dates.clear();
                        storeDataInArrays();
                        customAdapter.notifyDataSetChanged(); // refresh the screen

                        // Tell the user how many were removed
                        Toast.makeText(data_grid.this, "Deleted " + removed + " events", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });
    }

    // This method reads all event data from the database and puts it into lists
    private void storeDataInArrays(){
        Cursor cursor = db.getAllEvents(); // get all event rows
        if (cursor == null) return;

        try {
            if (cursor.getCount() == 0) return; // if no events stop
            while (cursor.moveToNext()){
                // Pull data from each column
                ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(EventDataBase.COL_ID)));
                names.add(cursor.getString(cursor.getColumnIndexOrThrow(EventDataBase.COL_NAME)));
                addresses.add(cursor.getString(cursor.getColumnIndexOrThrow(EventDataBase.COL_ADDRESS)));
                dates.add(cursor.getString(cursor.getColumnIndexOrThrow(EventDataBase.COL_DATE)));
            }
        } finally {
            // Always close the cursor when finished
            cursor.close();
        }
    }

    // This runs when the user comes back to this screen
    @Override
    protected void onResume() {
        super.onResume();
        // Clear old data and reload the newest data from the database
        ids.clear();
        names.clear();
        addresses.clear();
        dates.clear();
        storeDataInArrays();
        customAdapter.notifyDataSetChanged(); // refresh the list on screen
    }
}