package com.example.share.account.signin;

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
import com.example.share.account.signin.forgot.ForgotEmailVerification;
import com.example.share.account.signup.SignupUserCredentials;
import com.example.share.application.Dashboard;
import com.example.share.connection.ServerAPI;
import com.example.share.connection.inteface.AccountListener;
import com.example.share.constants.StaticData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {
    EditText username;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        username = findViewById(R.id.loginUsername);
        password = findViewById(R.id.loginPassword);
    }

    public void loginAccount(View view) {
        ServerAPI.LoginUser(new AccountListener() {
            @Override
            public void AuthenticationSuccess(String email, String uid, String bid) {
                Intent intent = new Intent(Login.this, Dashboard.class);
                StaticData.username = username.getText().toString();
                StaticData.email = email;
                intent.putExtra("USERNAME", username.getText().toString());
                intent.putExtra("EMAIL", email);
                intent.putExtra("UID", uid);
                intent.putExtra("BID", bid);
                startActivity(intent);
                finish();
            }

            @Override
            public void AuthenticationFailed() {
                popMessage("Login Error!", "Invalid username or password.");
            }
        }, username.getText().toString(), password.getText().toString());
    }

    public void forgotPassword(View view) {
        startActivity(new Intent(Login.this, ForgotEmailVerification.class));
    }

    public void signupAccount(View view) {
        startActivity(new Intent(Login.this, SignupUserCredentials.class));
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