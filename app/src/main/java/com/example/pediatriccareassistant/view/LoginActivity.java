package com.example.pediatriccareassistant.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.controller.AuthenticationController;
import com.example.pediatriccareassistant.model.callback.LoadingCallback;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    TextView registerLink;
    Button loginButton;
    ImageButton googleLoginButton;
    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        lauchMainActivityIfUserLoggedIn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.login_email_edit_text);
        passwordInput = findViewById(R.id.login_password_edit_text);
        loginButton = findViewById(R.id.login_login_button);
        googleLoginButton = findViewById(R.id.login_google_signin_button);
        registerLink = findViewById(R.id.login_not_registered_link);
        progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);

        AuthenticationController.getInstance().configureGoogleSignIn(this);

        googleLoginButton.setOnClickListener(this::signInWithGoogle);

        setRegisterLink();
        setLogginButton();
    }

    public void signInWithGoogle(View view) {
        AuthenticationController.getInstance().signInWithGoogle(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AuthenticationController.RC_SIGN_IN) {
            AuthenticationController.getInstance().handleGoogleSignInResult(data, new LoadingCallback() {
                @Override
                public void onStartLoading() {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingSuccessful() {
                    progressBar.setVisibility(View.GONE);
                    lauchMainActivityIfUserLoggedIn();
                }

                @Override
                public void onLoadingFailed(Exception e) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setLogginButton() {
        loginButton.setOnClickListener(v -> {
            String email, password;
            email = emailInput.getText().toString();
            password = passwordInput.getText().toString();

            if (email.isEmpty()) {
                // error
                return;
            }

            if (password.isEmpty()) {
                // error
                return;
            }

            AuthenticationController.getInstance().signInUser(email, password, new LoadingCallback() {
                @Override
                public void onStartLoading() {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingSuccessful() {
                    progressBar.setVisibility(View.GONE);
                    lauchMainActivityIfUserLoggedIn();
                }

                @Override
                public void onLoadingFailed(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setRegisterLink() {
        registerLink.setOnClickListener(v1 -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void lauchMainActivityIfUserLoggedIn() {
        if (AuthenticationController.getInstance().isCurrentUser()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}