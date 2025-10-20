package com.example.projectonebrettn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;


// Work Cited: Stevdza-san https://www.youtube.com/watch?v=hJPk50p7xwA


// This class handles saving, updating, deleting, and showing event information in the database.
public class EventDataBase extends SQLiteOpenHelper {

    // Database name and version
    public static final String DB_NAME = "event_library.db";
    public static final int DB_VERSION = 1;

    // Table name and column names
    public static final String TABLE = "event_library";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "event_name";
    public static final String COL_ADDRESS = "event_address";
    public static final String COL_DATE = "event_date";

    // This sets up the database connection
    public EventDataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // This runs the first time the database is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold events with id, name, address, and date
        String create = "CREATE TABLE " + TABLE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_ADDRESS + " TEXT, " +
                COL_DATE + " TEXT);";
        db.execSQL(create);
    }

    // This runs when the database version changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete the old table and make a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // Add a new event to the database
    public boolean insertEvent(String name, String address, String date){
        // Make sure all fields are filled in
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(date)) return false;
        SQLiteDatabase db = null;
        try {
            // Open database for writing
            db = this.getWritableDatabase();

            // Create a new row with event details
            ContentValues cv = new ContentValues();
            cv.put(COL_NAME, name);
            cv.put(COL_ADDRESS, address);
            cv.put(COL_DATE, date);

            // Insert it into the table
            long res = db.insert(TABLE, null, cv);

            // Return true if insert worked, false if it failed
            return res != -1;
        } catch (Exception e){
            return false;
        }
    }

    // Update an existing event using its ID
    public boolean updateEvent(int id, String name, String address, String date){
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            // Create new values to replace the old ones
            ContentValues cv = new ContentValues();
            cv.put(COL_NAME, name);
            cv.put(COL_ADDRESS, address);
            cv.put(COL_DATE, date);

            // Update the row that matches the event ID
            int rows = db.update(TABLE, cv, COL_ID + "=?", new String[]{String.valueOf(id)});

            // Return true if something was updated
            return rows > 0;
        } catch (Exception e){
            return false;
        }
    }

    // Delete ALL events (returns number of rows deleted)
    public int deleteAllEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE, null, null);
    }

    // Delete an event using its ID
    public boolean deleteEvent(int id){
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            // Delete the event with this ID
            int rows = db.delete(TABLE, COL_ID + "=?", new String[]{String.valueOf(id)});

            // Return true if something was deleted
            return rows > 0;
        } catch (Exception e){
            return false;
        }
    }

    // Get all events from the database
    public Cursor getAllEvents(){
        SQLiteDatabase db = this.getReadableDatabase();

        // Return all rows, newest first
        return db.rawQuery("SELECT * FROM " + TABLE + " ORDER BY " + COL_ID + " DESC", null);
    }

    // Get a single event by its ID
    public Cursor getEventById(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        // Search for the event that matches this ID
        return db.rawQuery("SELECT * FROM " + TABLE + " WHERE " + COL_ID + "=?",
                new String[]{String.valueOf(id)});
    }
}
