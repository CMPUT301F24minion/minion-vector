package com.example.minion_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;



public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //comment
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        login = findViewById(R.id.start);
        usersRef = db.collection("Users");


        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Assume you have a valid document ID or you need to specify it


                usersRef.document("1riLKOf2Wh3ialkdZTV8").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get your data from the document, e.g., a field called "username"
                                String username = document.getString("Name");

                                // Show the data in a Toast
                                Toast.makeText(MainActivity.this, "Username: " + username, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "No such user", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Button clicked!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });





    }
}