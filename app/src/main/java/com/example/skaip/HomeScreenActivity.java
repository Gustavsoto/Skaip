package com.example.skaip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class HomeScreenActivity extends AppCompatActivity {
    private Preferences preferences;
    private TextView top_panel_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_screen);
        ImageButton logOut = findViewById(R.id.logout);
        top_panel_text = findViewById(R.id.top_panel_text);
        ImageView user_icon = findViewById(R.id.user_icon);
        preferences = new Preferences(getApplicationContext());
        loadUserData();
        getToken();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }
    private void loadUserData(){
        top_panel_text.setText(preferences.getString(Constants.KEY_NAME));
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
