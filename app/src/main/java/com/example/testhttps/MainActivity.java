package com.example.testhttps;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button buttonLogin;
    TextView textViewStatus;
    NetworkClient networkClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkClient = new NetworkClient(this);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewStatus = findViewById(R.id.textViewStatus);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();

            }
        });



    }

    private void performLogin() {
        // Use your self-signed certificate HTTPS URL
        String url = "https://signage.combank.net/exchange/Service2.svc/Login/test/test";

        // Run network request in a background thread
        new Thread(() -> {
            try {
                String response = networkClient.run(url);
                runOnUiThread(() -> {
                    // Display the response or status in a Toast
                    Toast.makeText(this, "Response "+response, Toast.LENGTH_SHORT).show();
                    textViewStatus.setText(response);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_LONG).show();
                    textViewStatus.setText(e.getMessage());

                });
            }
        }).start();
    }
}