package com.example.share.account.signin.forgot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.share.R;
import com.example.share.connection.ServerAPI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForgotCodeVerification extends AppCompatActivity {
    private static EditText c1;
    private static EditText c2;
    private static EditText c3;
    private static EditText c4;
    private static EditText c5;
    private static EditText c6;

    private static Button v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_code_verification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        c1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);
        c4 = findViewById(R.id.c4);
        c5 = findViewById(R.id.c5);
        c6 = findViewById(R.id.c6);

        v = findViewById(R.id.verify);
        setCodeIdentification();
    }

    public void verify(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
//            ServerAPI.verifyCode(new CodeVerificationListener() {
//                @Override
//                public void IsCodeVerified() {
//                    startActivity(new Intent(ForgotCodeVerification.this, ForgotNewPassword.class));
//                }
//
//                @Override
//                public void IsCodeNotVerified() {
//                    popMessage("Code Error", "You are entered verification code is not valid!");
//                }
//            }, c1.getText().toString() + c2.getText().toString() + c3.getText().toString() + c4.getText().toString() + c5.getText().toString() + c6.getText().toString());
        });
        executorService.shutdown();
    }

    public void resend(View view) {

    }

    public void popMessage(String title, String msg) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(title);
                builder.setMessage(msg);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            });
        });
        executorService.shutdown();
    }

    private static void setCodeIdentification() {
        c1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!c1.toString().isEmpty()) {
                    c2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        c2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!c2.toString().isEmpty()) {
                    c3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        c3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!c3.toString().isEmpty()) {
                    c4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        c4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!c4.toString().isEmpty()) {
                    c5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        c5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!c5.toString().isEmpty()) {
                    c6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        c6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!c6.toString().isEmpty()) {
                    v.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}