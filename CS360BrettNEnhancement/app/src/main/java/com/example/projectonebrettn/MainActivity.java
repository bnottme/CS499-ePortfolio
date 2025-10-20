package com.example.projectonebrettn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import at.favre.lib.crypto.bcrypt.BCrypt;

// Source Code: The Code City, https://www.youtube.com/watch?v=WAejZCkLJAI

// This is the main screen for logging in.
public class MainActivity extends AppCompatActivity {

    // Text boxes for typing username and password
    private EditText etUsername, etPassword;

    // Buttons for login and going to the register screen
    private Button btnLogin, btnGoRegister;

    // Database helper for checking users
    private UsernameDB udb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This connects the Java code to the layout file (activity_main.xml)

        // Get the text boxes and buttons from the layout
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoRegister = findViewById(R.id.btnGoRegister);

        // Create a new connection to the username database
        udb = new UsernameDB(this);

        // When Go to Register button is clicked, open the register screen
        btnGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ActivityRegister.class));
            finish(); // closes this screen
        });

        // When Login button is clicked
        btnLogin.setOnClickListener(v -> {

            // Get the username and password the user typed
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            // If either box is empty show a message and stop
            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Look up the saved password hash from the database
            String storedHash = udb.getPasswordHash(user);

            // If the username isnt found in the database
            if (storedHash == null) {
                Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the password typed matches the saved hash
            BCrypt.Result result = BCrypt.verifyer().verify(pass.toCharArray(), storedHash);

            // If the password is correct log in and move to the next screen
            if (result.verified) {
                Toast.makeText(MainActivity.this, "Login success", Toast.LENGTH_SHORT).show();

                // Move to the SMS permission screen
                Intent i = new Intent(MainActivity.this, SmsAgreement.class);
                i.putExtra("username", user); // pass username to next activity
                startActivity(i);
                finish(); // close this screen
            } else {
                // If the password does not match show error
                Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}