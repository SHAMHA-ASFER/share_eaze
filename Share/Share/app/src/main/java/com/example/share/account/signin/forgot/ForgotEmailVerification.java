package com.example.share.account.signin.forgot;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class ForgotEmailVerification extends AppCompatActivity {
    EditText forgotEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_email_verification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        forgotEmail = findViewById(R.id.forgotEmail);
    }

    public void goBackToLogin(View view) {
        startActivity(new Intent(ForgotEmailVerification.this, Login.class));
    }

    public void sendCode(View view) {
//        ServerAPI.forgotEmail(new ForgotListener() {
//            @Override
//            public void IsEmailVerified() {
//                startActivity(new Intent(ForgotEmailVerification.this, ForgotCodeVerification.class));
//            }
//
//            @Override
//            public void IsEmailNotVerified() {
//                popMessage("Account Not Exists!", "You are entered email account is not exists.");
//            }
//        }, forgotEmail.getText().toString());
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
}