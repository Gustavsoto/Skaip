package com.example.skaip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {
    private Preferences preferences;
    private TextView top_panel_text;
    private RecyclerView groupRecyclerView;
    private ImageView user_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_screen);
        ImageButton logOut = findViewById(R.id.logout);
        ImageButton addGroup = findViewById(R.id.add_group);
        top_panel_text = findViewById(R.id.top_panel_text);
        user_icon = findViewById(R.id.user_icon);
        groupRecyclerView = findViewById(R.id.groupsRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        preferences = new Preferences(getApplicationContext());
        loadUserData();
        getToken();
        getGroups();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddGroupActivity.class));
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

    private Bitmap decodeImage(String base64String){
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void loadUserData(){
        top_panel_text.setText(preferences.getString(Constants.KEY_NAME));
        Bitmap profileImage = decodeImage(preferences.getString(Constants.KEY_PROFILE_IMAGE));
        user_icon.setImageBitmap(profileImage);
    }
    private void getGroups(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_GROUPS)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null){
                        List<Group> groups = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            Group group = new Group();
                            group.setName(queryDocumentSnapshot.getString(Constants.KEY_GROUP_NAME));
                            group.setImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                            groups.add(group);
                        }
                        if(groups.size() > 0){
                            GroupAdapter groupAdapter = new GroupAdapter(groups);
                            groupRecyclerView.setAdapter(groupAdapter);
                        } else {
                            Toast.makeText(getApplicationContext(), "There are no groups", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Token successfully updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateFcmToken);
    }
    private void updateFcmToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database
                .collection(Constants.KEY_COLLECTION_USERS)
                .document(preferences.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(), "Token successfully updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(exception -> Toast.makeText(getApplicationContext(), "Error updating token", Toast.LENGTH_SHORT).show());
    }
    private void signOut(){
        Toast.makeText(getApplicationContext(), "Logging out", Toast.LENGTH_SHORT).show();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS)
                        .document(preferences.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {
            preferences.clear();
            startActivity(new Intent(getApplicationContext(), WelcomeScreenActivity.class));
            finish();
        })
                .addOnFailureListener(exception -> Toast.makeText(getApplicationContext(), "Error signing out", Toast.LENGTH_SHORT).show());
    }
}
