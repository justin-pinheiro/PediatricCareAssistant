package com.example.pediatriccareassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.controller.AuthenticationController;
import com.example.pediatriccareassistant.model.callback.LoadingCallback;

public class RegisterActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    TextView loginLink;
    Button registerButton;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.register_email_edit_text);
        passwordInput = findViewById(R.id.register_password_edit_text);
        loginLink = findViewById(R.id.register_already_registered_link);
        registerButton = findViewById(R.id.register_register_button);
        progressBar = findViewById(R.id.register_progress_bar);
        progressBar.setVisibility(View.GONE);

        setLogginLink();
        setRegisterButton();
    }

    private void setLogginLink() {
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lauchLoginActivity();
            }
        });
    }

    private void setRegisterButton() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                progressBar.setVisibility(View.VISIBLE);

                if (email.isEmpty()) {
                    // error
                    return;
                }

                if (password.isEmpty()) {
                    // error
                    return;
                }

                AuthenticationController.getInstance().registerUser(email, password, new LoadingCallback() {
                    @Override
                    public void onStartLoading() {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingSuccessful() {
                        progressBar.setVisibility(View.GONE);
                        lauchLoginActivity();
                    }

                    @Override
                    public void onLoadingFailed(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    private void lauchLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}