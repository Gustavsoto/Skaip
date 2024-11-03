package com.example.skaip;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class AddGroupActivity extends AppCompatActivity {
    private String encodedImage, encodedFile;
    private TextView fileNameTextView;
    private ImageView groupImage;
    private EditText groupName, edit_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_group_screen);

        ImageButton back = findViewById(R.id.back_button);
        Button chooseFiles = findViewById(R.id.button_choose_file);
        Button createGroup = findViewById(R.id.button_create_group);
        groupImage = findViewById(R.id.group_image);
        fileNameTextView = findViewById(R.id.file_name);
        edit_description = findViewById(R.id.edit_description);
        groupName = findViewById(R.id.edit_name);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        chooseFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/msword"); // for .doc
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {
                        "application/msword", // .doc
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // .docx
                });
                pickFile.launch(intent);
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    createGroup();
                }
            }
        });
        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
    }
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 80;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private String encodeFile(Uri fileUri) {
        String encodedFile = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            byte[] fileBytes = byteArrayOutputStream.toByteArray();
            encodedFile = Base64.encodeToString(fileBytes, Base64.DEFAULT);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedFile;
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
                            groupImage.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private final ActivityResultLauncher<Intent> pickFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        String fileName = getFileName(fileUri);
                        fileNameTextView.setText(fileName);
                        encodedFile = encodeFile(fileUri);
                    }
                }
            }
    );
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) { // Check if the index is valid
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close(); // Close the cursor if it was opened
                }
            }
        }
        if (result == null) {
            // Fallback to getting the file name from the Uri path
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private boolean validateFields() {
        // Validate if encodedImage and encodedFile are not null
        if (encodedImage == null || encodedFile == null) {
            Toast.makeText(this, "Please select an image and a file.", Toast.LENGTH_SHORT).show();
            return false; // Return false if either is null
        }

        // Validate if the group name is empty
        if (groupName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a group name.", Toast.LENGTH_SHORT).show();
            return false; // Return false if the group name is empty
        }

        // Validate if the description TextView is empty
        if (edit_description.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a description.", Toast.LENGTH_SHORT).show();
            return false; // Return false if the description is empty
        }

        // All validations passed
        return true;
    }

    private void createGroup(){
        //firebase datubaze
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        //hashmaps kurs tiks padots datubazei, lai noglaba
        HashMap<String, Object> group = new HashMap<>();
        group.put(Constants.KEY_IMAGE, encodedImage);
        group.put(Constants.KEY_GROUP_FILE, encodedFile);
        group.put(Constants.KEY_GROUP_NAME, groupName.getText().toString().trim());
        group.put(Constants.KEY_GROUP_DESCRIPTION, edit_description.getText().toString().trim());
        //seit saglaba ievadito datubaze
        database.collection(Constants.KEY_COLLECTION_GROUPS)
                .add(group)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Group registered successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                    //Notira staku
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), "Error registering the user", Toast.LENGTH_SHORT).show();
                });
    }
}