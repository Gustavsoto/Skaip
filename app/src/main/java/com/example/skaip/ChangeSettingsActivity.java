package com.example.skaip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeSettingsActivity extends AppCompatActivity {
    private Preferences preferences;
    private TextView name, course, year, password, email;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new Preferences(getApplicationContext());
        EdgeToEdge.enable(this);
        setContentView(R.layout.change_settings);
        name = findViewById(R.id.name_field);
        course = findViewById(R.id.course_field);
        year = findViewById(R.id.year_field);
        password = findViewById(R.id.password_field);
        email = findViewById(R.id.email_field);
        profileImage = findViewById(R.id.profile_image);
        Button changeName = findViewById(R.id.buttonChangeName);
        Button changeCourse = findViewById(R.id.buttonChangeCourse);
        Button changeYear = findViewById(R.id.buttonChangeYear);
        Button changePassword = findViewById(R.id.buttonChangePassword);
        Button changeEmail = findViewById(R.id.buttonChangeEmail);
        Button saveChanges = findViewById(R.id.save_preferences);
        loadUserPreferences(name, Constants.KEY_NAME);
        loadUserPreferences(course, Constants.KEY_COURSE);
        loadUserPreferences(year, Constants.KEY_YEAR);
        loadUserPreferences(password, Constants.KEY_PASSWORD);
        loadUserPreferences(email, Constants.KEY_EMAIL);
        loadUserImage(profileImage, Constants.KEY_PROFILE_IMAGE);
        setFieldChangeListener(changeName, name);
        setFieldChangeListener(changeCourse, course);
        setFieldChangeListener(changeYear, year);
        setFieldChangeListener(changePassword, password);
        setFieldChangeListener(changeEmail, email);
        saveChangesListener(saveChanges);
    }
    private void loadUserImage(ImageView image, String key){
        Bitmap profileImage = decodeImage(preferences.getString(key));
        image.setImageBitmap(profileImage);
    }
    private void loadUserPreferences(TextView field, String key) {
        String value = preferences.getString(key);
        field.setText(value);
    }
    private void setFieldChangeListener(final Button button, final TextView field) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEnabled = field.isEnabled();
                toggleField(field, isEnabled);
                button.setText(isEnabled ? "Change" : "Save");
            }
        });
    }
    private void saveChangesListener(final Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSetting();
            }
        });
    }
    private Bitmap decodeImage(String base64String){
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
    private void toggleField(TextView field, boolean isEnabled) {
        field.setEnabled(!isEnabled);
        field.setFocusable(!isEnabled);
        field.setFocusableInTouchMode(!isEnabled);
        field.setCursorVisible(!isEnabled);
    }
    private void saveSetting(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database
                .collection(Constants.KEY_COLLECTION_USERS)
                .document(preferences.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_NAME, name.getText().toString().trim());
        documentReference.update(Constants.KEY_COURSE, course.getText().toString().trim());
        documentReference.update(Constants.KEY_YEAR, year.getText().toString().trim());
        documentReference.update(Constants.KEY_PASSWORD, password.getText().toString().trim());
        documentReference.update(Constants.KEY_EMAIL, email.getText().toString().trim());
        Toast.makeText(getApplicationContext(), "Successfully saved settings", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
        finish();
    }
}