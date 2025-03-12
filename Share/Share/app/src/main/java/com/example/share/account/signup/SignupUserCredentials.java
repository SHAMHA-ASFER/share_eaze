package com.example.share.account.signup;

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

public class SignupUserCredentials extends AppCompatActivity {
    EditText username;
    EditText email;
    EditText password;
    EditText confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_user_credentials);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.signupUsername);
        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signupPassword);
        confirm = findViewById(R.id.signupConfirm);
    }

    public void goBackToLogin(View view) {
        startActivity(new Intent(SignupUserCredentials.this, Login.class));
    }

    public void createAccount(View view) {
//        ServerAPI.createAccount(new AccountCreateListener() {
//            @Override
//            public void AccountCreatedSuccessfully() {
//                startActivity(new Intent(SignupUserCredentials.this, SignupCodeVerification.class));
//                finish();
//            }
//
//            @Override
//            public void AccountAlreadyExists() {
//                popMessage("Account Error!", "This account is already exists.");
//            }
//
//            @Override
//            public void PasswordAndConfirmNotMatched() {
//                popMessage("Password Error!", "The password and confirm password you entered are not matched.");
//            }
//        }, username.getText().toString(), email.getText().toString(), password.getText().toString(), confirm.getText().toString());
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