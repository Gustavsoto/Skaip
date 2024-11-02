package com.example.skaip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_in_screen);

        // Back button
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Recover password button
        Button recoverButton = findViewById(R.id.recover_button);
        recoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, RecoverActivity.class);
                startActivity(intent);
            }
        });

        // Variables for firebase auth
        mAuth = FirebaseAuth.getInstance();
        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.buttonLogin);

        // Sign in logic
        loginButton.setOnClickListener(v -> {
            //implement null checks for these
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            // Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignInActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d("auth", "signed in with email");
                        assert Objects.requireNonNull(user).getEmail() != null;
                        Log.d("auth", user.getEmail());
                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d("auth", "sign in failed");
                    }
                });
        });
    }
}
