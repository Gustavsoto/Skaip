package com.example.skaip;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
    private TextInputLayout emailLayout, passwordLayout;
    private Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_in_screen);
        //shared preferences
        preferences = new Preferences(getApplicationContext());
        //automatiski ielogos ieksa ja glabajas signed in vertiba
        if(preferences.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
            startActivity(intent);
            finish();
        }

        Button backButton = findViewById(R.id.back_button);
        Button signInButton = findViewById(R.id.sign_in_button);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    //Galvena funkcija prieks logina
                    signIn();
                }
            }
        });

    }
    public void signIn(){
        //Firebase datubaze
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        //Salidzina users tabulas email un password
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, emailLayout.getEditText().getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, passwordLayout.getEditText().getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Successfully signed in", Toast.LENGTH_SHORT).show();
                        //Uztaisa snapshotu tam, ko ieguva no datubazes un ieksa sharedPreferences saglaba
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferences.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferences.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferences.putString(Constants.KEY_COURSE, documentSnapshot.getString(Constants.KEY_COURSE));
                        preferences.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        preferences.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferences.putString(Constants.KEY_PASSWORD, documentSnapshot.getString(Constants.KEY_PASSWORD));
                        preferences.putString(Constants.KEY_YEAR, documentSnapshot.getString(Constants.KEY_YEAR));
                        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                        //Notira staku
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error signing in", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    public boolean validateFields() {
        boolean isValid = true;

        //Valide epastu
        if (isEmpty(emailLayout)) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailLayout.getEditText().getText().toString()).matches()) {
            emailLayout.setError("Invalid email format");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        //Valide paroli
        if (isEmpty(passwordLayout)) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else if (passwordLayout.getEditText().getText().toString().length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        return isValid;
    }
    //parbauda vai vispar kaut kas ievadits ieksa edit fielda
    private boolean isEmpty(TextInputLayout layout) {
        String input = layout.getEditText().getText().toString().trim();
        return TextUtils.isEmpty(input);
    }
}
