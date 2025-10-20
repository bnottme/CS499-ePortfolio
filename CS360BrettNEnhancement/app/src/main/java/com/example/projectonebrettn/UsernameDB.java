package com.example.projectonebrettn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

// Work Cited: The Code City, https://www.youtube.com/watch?v=WAejZCkLJAI

// This class handles saving and checking usernames and passwords in a local database.
public class UsernameDB extends SQLiteOpenHelper {

    // Name of the database file and version
    public static final String DB_NAME = "user_db.db";
    public static final int DB_VERSION = 1;

    // Table and column names
    public static final String TABLE = "users";
    public static final String COL_USER = "username";
    public static final String COL_PASS = "password";

    // This sets up the database
    public UsernameDB(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    // This runs the first time the app makes the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table called "users" with two columns: username and password
        String create = "CREATE TABLE " + TABLE + " (" +
                COL_USER + " TEXT PRIMARY KEY, " +
                COL_PASS + " TEXT)";
        db.execSQL(create);
    }

    // This runs if the database version changes to update or reset it
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete the old table and make a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // This adds a new user and their password hash to the database
    public boolean insertUser(String username, String bcryptHash){

        // Make sure there’s actually a username and password hash to save
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(bcryptHash)) return false;
        try {
            // Open database so we can write data into it
            SQLiteDatabase db = this.getWritableDatabase();

            // Create a row of data with username and hashed password
            ContentValues cv = new ContentValues();
            cv.put(COL_USER, username);
            cv.put(COL_PASS, bcryptHash);

            // Insert it into the users table
            long res = db.insert(TABLE, null, cv);

            // If insert worked return true otherwise return false
            return res != -1;

        } catch (Exception e) {
            // If something went wrong return false
            return false;
        }
    }

    // This checks if a username already exists in the database
    public boolean checkUsername(String username){
        Cursor c = null;
        try {
            // Open the database for reading
            SQLiteDatabase db = this.getReadableDatabase();

            // Search for the username
            c = db.rawQuery("SELECT " + COL_USER + " FROM " + TABLE + " WHERE " + COL_USER + "=?",
                    new String[]{username});

            // If we find a match, return true (username exists)
            return (c != null && c.moveToFirst());
        } finally {
            // Always close the search when done
            if (c != null) c.close();
        }
    }

    // This gets the saved password hash for a username
    public String getPasswordHash(String username){
        Cursor c = null;
        try {
            // Open the database for reading
            SQLiteDatabase db = this.getReadableDatabase();

            // Look for the user’s password hash
            c = db.rawQuery("SELECT " + COL_PASS + " FROM " + TABLE + " WHERE " + COL_USER + "=?",
                    new String[]{username});


            // If found return the hash
            if (c != null && c.moveToFirst()) {
                return c.getString(0);
            }


            // If not found return null
            return null;
        } finally {
            // Always close the search when done
            if (c != null) c.close();
        }
    }
}