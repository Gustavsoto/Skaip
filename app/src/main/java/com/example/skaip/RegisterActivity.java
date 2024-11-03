package com.example.skaip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout nameLayout, courseLayout, yearLayout, emailLayout, passwordLayout;
    private Preferences preferences;
    private String encodedImage;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_screen);
        //sharedpreferences seit
        preferences = new Preferences(getApplicationContext());

        Button backButton = findViewById(R.id.back_button);
        Button signUpButton = findViewById(R.id.sign_up_button);
        nameLayout = findViewById(R.id.name);
        courseLayout = findViewById(R.id.course);
        yearLayout = findViewById(R.id.year);
        emailLayout = findViewById(R.id.email);
        passwordLayout = findViewById(R.id.password);
        profileImage = findViewById(R.id.profile_image);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //parbauda fieldus
                if(validateFields()){
                    //ja izdevas tad pieregistre lietotaju firebase
                    signUp();
                }
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            profileImage.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 80;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void signUp(){
        //firebase datubaze
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        //hashmaps kurs tiks padots datubazei, lai noglaba
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, nameLayout.getEditText().getText().toString().trim());
        user.put(Constants.KEY_COURSE, courseLayout.getEditText().getText().toString().trim());
        user.put(Constants.KEY_YEAR, yearLayout.getEditText().getText().toString().trim());
        user.put(Constants.KEY_EMAIL, emailLayout.getEditText().getText().toString().trim());
        user.put(Constants.KEY_PASSWORD, passwordLayout.getEditText().getText().toString().trim());
        user.put(Constants.KEY_PROFILE_IMAGE, encodedImage.trim());
        //seit saglaba ievadito datubaze
        database.collection(Constants.KEY_COLLECTION_USERS)
            .add(user)
            .addOnSuccessListener(documentReference -> {
                Toast.makeText(getApplicationContext(), "User successfully registered!", Toast.LENGTH_SHORT).show();
                //Pievieno shared preferences
                preferences.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                preferences.putString(Constants.KEY_USER_ID, documentReference.getId());
                preferences.putString(Constants.KEY_NAME, nameLayout.getEditText().getText().toString().trim());
                preferences.putString(Constants.KEY_PROFILE_IMAGE, encodedImage.trim());
                Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                //Notira staku
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            })
            .addOnFailureListener(exception -> {
                Toast.makeText(getApplicationContext(), "Error registering the user", Toast.LENGTH_SHORT).show();
            });
    }
    private boolean validateFields() {
        // Profile image validation
        if (encodedImage == null) {
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();
            return false; // Return false if is null
        }

        // Name validation
        if (isEmpty(nameLayout)) {
            nameLayout.setError("Name is required");
            return false;
        } else {
            nameLayout.setError(null);
        }

        // Course validation
        if (isEmpty(courseLayout)) {
            courseLayout.setError("Course is required");
            return false;
        } else {
            courseLayout.setError(null);
        }

        // Year validation
        if (isEmpty(yearLayout)) {
            yearLayout.setError("Year is required");
            return false;
        } else {
            yearLayout.setError(null);
        }

        // Email validation
        String email = emailLayout.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Enter a valid email");
            return false;
        } else {
            emailLayout.setError(null);
        }

        // Password validation (min 6 chars)
        String password = passwordLayout.getEditText().getText().toString();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            return false;
        } else {
            passwordLayout.setError(null);
        }

        return true;
    }
    //parbauda vai nav pilniba tukss
    private boolean isEmpty(TextInputLayout layout) {
        String input = layout.getEditText().getText().toString().trim();
        return TextUtils.isEmpty(input);
    }
}
