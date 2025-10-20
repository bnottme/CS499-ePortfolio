package com.example.projectonebrettn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// Work Cited: Stevdza-san https://www.youtube.com/watch?v=hJPk50p7xwA

// This screen is used to add a new event or edit an existing one.
public class AddEvent extends AppCompatActivity {

    // Text boxes where the user types event details
    private EditText event_name_input, event_address_input, event_date_input;

    // Button to save the event
    private Button add_button;

    // The database that stores all event information
    private EventDataBase db;

    // These help the app know if we are editing an old event or adding a new one
    private boolean is_edit = false;
    private int edit_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        // Connects this Java file to the screen layout (activity_add_event.xml)

        // Make a new database helper
        db = new EventDataBase(this);

        // Connect layout elements to variables in the code
        event_name_input = findViewById(R.id.event_name_input);
        event_address_input = findViewById(R.id.event_address_input);
        event_date_input = findViewById(R.id.event_date_input);
        add_button = findViewById(R.id.add_button);

        // Check if this screen was opened to edit an existing event
        is_edit = getIntent().getBooleanExtra("is_edit", false);
        if (is_edit) {
            // If editing get the ID of the event we want to edit
            edit_id = getIntent().getIntExtra("event_id", -1);

            // Make sure the ID is valid
            if (edit_id != -1){
                // Get the event data from the database
                Cursor c = db.getEventById(edit_id);
                if (c != null) {
                    try {
                        // If we find the event show its data in the text boxes
                        if (c.moveToFirst()){
                            event_name_input.setText(c.getString(c.getColumnIndexOrThrow(EventDataBase.COL_NAME)));
                            event_address_input.setText(c.getString(c.getColumnIndexOrThrow(EventDataBase.COL_ADDRESS)));
                            event_date_input.setText(c.getString(c.getColumnIndexOrThrow(EventDataBase.COL_DATE)));
                        }
                    } finally {
                        // Always close the cursor when done
                        c.close();
                    }
                }
            }
        }

        // When the Save button is clicked
        add_button.setOnClickListener(v -> {
            // Get what the user typed
            String name = event_name_input.getText().toString().trim();
            String address = event_address_input.getText().toString().trim();
            String date = event_date_input.getText().toString().trim();

            // Make sure all boxes are filled in
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(date)){
                Toast.makeText(AddEvent.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // If we are editing update the event. Otherwise add a new one
            boolean ok = (is_edit && edit_id != -1)
                    ? db.updateEvent(edit_id, name, address, date)
                    : db.insertEvent(name, address, date);

            // If the save worked show success and go back to the list screen
            if (ok){
                Toast.makeText(AddEvent.this, "Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddEvent.this, data_grid.class));
                finish();
            } else {
                // If something went wrong show an error
                Toast.makeText(AddEvent.this, "Save failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
