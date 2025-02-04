package com.cadetia.erasmuscadet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.cadetia.erasmuscadet.R;
import com.cadetia.erasmuscadet.entities.LoadingView;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private Button login_button;
    private LoadingView loadingView;

    TextInputEditText login_email, login_password;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        loadingView = findViewById(R.id.loadingView);

        // Use WindowCompat to set fitsSystemWindows to false
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        } else {
            // For versions below Android 11
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        ImageButton back_button = findViewById(R.id.back_button);
        login_button = findViewById(R.id.login_button);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);

        firebaseAuth = FirebaseAuth.getInstance();

        // Retrieve saved email and password and pre-fill input fields
        SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String savedEmail = preferences.getString("email", "");
        String savedPassword = preferences.getString("password", "");
        login_email.setText(savedEmail);
        login_password.setText(savedPassword);

        login_button.setOnClickListener(v -> {
            animateButtonOnClick(login_button);
            String email = Objects.requireNonNull(login_email.getText()).toString().trim();
            String password = Objects.requireNonNull(login_password.getText()).toString().trim();

            // Email

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                login_email.setError("Please enter a valid email address.");
                login_email.requestFocus();
                return;
            }

            // Password

            if (TextUtils.isEmpty(password)) {
                login_password.setError("Please enter your password.");
                login_password.requestFocus();
                return;
            }
            if (password.length() < 8 || password.length() > 20 || password.contains(" ")) {
                login_password.setError("Password must be between 8 and 20 characters and not contain spaces.");
                login_password.requestFocus();
                return;
            }
            if (!password.matches(".*[A-Z].*")) {
                login_password.setError("Password must contain at least one uppercase letter.");
                login_password.requestFocus();
                return;
            }
            if (password.matches(".*\\d{5,}.*")) {
                login_password.setError("Password cannot contain more than 4 consecutive digits.");
                login_password.requestFocus();
                return;
            }

            loginAdmin(email, password);
        });

        back_button.setOnClickListener(view -> finish());
    }

    private void loginAdmin(String email, String password) {
        startLoadingAnimation();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully logged in, save email and password
                        saveCredentials(email, password);

                        firebaseUser = firebaseAuth.getCurrentUser(); // Get the currently logged-in user

                        // Store Login Information
                        assert firebaseUser != null;
                        String userName = firebaseUser.getDisplayName();
                        saveUserData(userName, email, "no_photo");

                        openHomeActivity();

                        if (firebaseUser != null) {
                            Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser();
                            // Save user data to the database (you can call the saveDataToDatabase method here if needed)
                            // saveDataToDatabase(hashMap);
                        }
                    } else {
                        // Error occurred during login
                        Exception exception = task.getException();
                        if (exception != null) {
                            String errorMessage = exception.getMessage();
                            Toast.makeText(Login.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            assert errorMessage != null;
                            Log.e("login failed", errorMessage);
                        }
                        stopLoadingAnimation();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    stopLoadingAnimation();
                });
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    public void openHomeActivity() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            stopLoadingAnimation();
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateButtonOnClick(Button button) {
        button.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100) // Set a shorter animation duration (in milliseconds)
                .setInterpolator(new LinearInterpolator()) // Use a LinearInterpolator for a snappy animation
                .withEndAction(() -> button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50).start())
                .start();
    }
    private void startLoadingAnimation() {
        loadingView.startLoadingAnimation(R.raw.loading, true);
    }
    private void stopLoadingAnimation() {
        loadingView.stopLoadingAnimation();
    }

    private void saveUserData(String userName, String userEmail,String userPhoto) {
        SharedPreferences.Editor editor = getSharedPreferences("UserData", MODE_PRIVATE).edit();
        editor.putString("userName", userName);
        editor.putString("userEmail", userEmail);
        editor.putString("userPhoto", userPhoto);
        editor.apply();
    }

}
