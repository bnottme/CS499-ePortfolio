package com.example.projectonebrettn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import at.favre.lib.crypto.bcrypt.BCrypt;

// Work Cited:  The Code City, https://www.youtube.com/watch?v=WAejZCkLJAI

//Work Cited: OWASP https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html

//Work Cited: patrickfav https://github.com/patrickfav/bcrypt

// This screen lets new users create an account.
public class ActivityRegister extends AppCompatActivity {

    // Boxes for typing username and passwords
    private EditText etUsername, etPassword, etRePassword;

    // Buttons for register and go back to login
    private Button btnRegister, btnGoLogin;

    // Database helper for saving new users
    private UsernameDB udb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
        // Connects this Java code to the layout file (register_page.xml)

        // Link the text boxes and buttons to their matching IDs in the layout
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoLogin = findViewById(R.id.btnGoLogin);

        // Make a new connection to the username database
        udb = new UsernameDB(this);

        // When "Go to Login" is pressed take the user back to the login screen
        btnGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(ActivityRegister.this, MainActivity.class));
            finish(); // close this screen
        });

        // When Register is pressed
        btnRegister.setOnClickListener(v -> {
            // Get what the user typed into the boxes
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String rePass = etRePassword.getText().toString().trim();

            // Make sure all boxes are filled in
            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(rePass)) {
                Toast.makeText(ActivityRegister.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Make sure both password boxes match
            if (!pass.equals(rePass)) {
                Toast.makeText(ActivityRegister.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Make sure the password is long enough
            if (pass.length() < 6) {
                Toast.makeText(ActivityRegister.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the username is already taken
            if (udb.checkUsername(user)) {
                Toast.makeText(ActivityRegister.this, "User already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            // Turn the password into a secure hash
            // "12" means the strength of the encryption how many rounds it uses
            String hash = BCrypt.withDefaults().hashToString(12, pass.toCharArray());

            // Try to save the new user and their hashed password in the database
            boolean insertOk = udb.insertUser(user, hash);

            // If the database save worked show success and go back to login screen
            if (insertOk) {
                Toast.makeText(ActivityRegister.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ActivityRegister.this, MainActivity.class));
                finish();
            } else {
                // If something went wrong show an error message
                Toast.makeText(ActivityRegister.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
