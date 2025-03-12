package com.example.share.account.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.share.R;
import com.example.share.account.signin.Login;
import com.example.share.connection.ServerAPI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignupPersonalInformation extends AppCompatActivity {
    EditText first;
    EditText last;
    EditText nic;
    EditText phone;
    EditText no;
    EditText road;
    EditText city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_personal_information);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        first = findViewById(R.id.firstName);
        last = findViewById(R.id.lastName);
        nic= findViewById(R.id.nic);
        phone = findViewById(R.id.phone);
        no = findViewById(R.id.no);
        road = findViewById(R.id.road);
        city = findViewById(R.id.city);
    }

    public void submit(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
//            ServerAPI.addPersonalInformation(new AddPersonalInfoListener() {
//                @Override
//                public void personalDetailsAdded() {
//                    startActivity(new Intent(SignupPersonalInformation.this, Login.class));
//                }
//            }, nic.getText().toString(), first.getText().toString(), last.getText().toString(), no.getText().toString() + ", " + road.getText().toString() + ", " + city.getText().toString(), phone.getText().toString());
        });
        executorService.shutdown();
    }
}