package com.cadetia.erasmuscadet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.cadetia.erasmuscadet.R;

import java.util.Objects;


public class Signup extends AppCompatActivity {

    private Button signup_button;

    private FirebaseAuth firebaseAuth;

    TextInputEditText signup_name, signup_email, signup_password;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use WindowCompat to set fitsSystemWindows to false
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        } else {
            // For versions below Android 11
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_signup);
        firebaseAuth = FirebaseAuth.getInstance();


        ImageButton back_button = findViewById(R.id.back_button);
        signup_button = findViewById(R.id.signup_button);

        signup_name = findViewById(R.id.signup_name);
        signup_email = findViewById(R.id.signup_email);
        signup_password = findViewById(R.id.signup_password);


        signup_button.setOnClickListener(view -> {
            animateButtonOnClick(signup_button);
            String name = Objects.requireNonNull(signup_name.getText()).toString().trim();
            String email = Objects.requireNonNull(signup_email.getText()).toString().trim();
            String password = Objects.requireNonNull(signup_password.getText()).toString().trim();

            // Name

            if (TextUtils.isEmpty(name)) {
                signup_name.setError("Please enter your name.");
                signup_name.requestFocus();
                return;
            }
            if (!Character.isUpperCase(name.charAt(0))) {
                signup_name.setError("Name must start with a capital letter.");
                signup_name.requestFocus();
                return;
            }
            if (name.length() > 20) {
                signup_name.setError("Name cannot be longer than 20 characters.");
                signup_name.requestFocus();
                return;
            }

            // Email

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                signup_email.setError("Please enter a valid email address.");
                signup_email.requestFocus();
                return;
            }

            // Password

            if (TextUtils.isEmpty(password)) {
                signup_password.setError("Please enter your password.");
                signup_password.requestFocus();
                return;
            }

            if (password.length() < 8 || password.length() > 20 || password.contains(" ")) {
                signup_password.setError("Password must be between 8 and 20 characters and not contain spaces.");
                signup_password.requestFocus();
                return;
            }
            if (!password.matches(".*[A-Z].*")) {
                signup_password.setError("Password must contain at least one uppercase letter.");
                signup_password.requestFocus();
                return;
            }
            if (password.matches(".*\\d{5,}.*")) {
                signup_password.setError("Password cannot contain more than 4 consecutive digits.");
                signup_password.requestFocus();
                return;
            }

            signupAdmin(name, email, password);

        });

        back_button.setOnClickListener(view -> {
            // Close the current Signup activity and go back to the previous MainActivity
            finish();
        });

    }

    private void signupAdmin(String name, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null) {
                            user.updateProfile(new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build())
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            openLoginActivity();
                                        } else {
                                            Toast.makeText(Signup.this, "Failed to update display name", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Handle signup failures
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(Signup.this, "Email already registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Signup.this, "Signup failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void openLoginActivity() {
        try {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception or log the error here
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

}