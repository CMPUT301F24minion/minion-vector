package com.example.minion_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private String android_id;

    Button loginBtn,userBtn,organizerBtn,adminBtn;
    TextView choosePageText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        loginBtn = findViewById(R.id.start);
        userBtn = findViewById(R.id.userBtn);
        organizerBtn = findViewById(R.id.organizerBtn);
        adminBtn=findViewById(R.id.adminBtn);
        choosePageText=findViewById(R.id.choose_page_launch);
        usersRef = db.collection("Users");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get device id
                android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

                // Check if user exists
                usersRef.document(android_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                Map<String, Object> data = document.getData();
                                if (data!=null) {
                                    // User exists, retrieve data
                                    // this type checking prevents app to still function if something
                                    // breaks
                                    Object roleObj = data.get("Roles");
                                    if (roleObj instanceof Map) {
                                        Map<String, Boolean> role = (Map<String, Boolean>) roleObj;
                                        loginBtn.setVisibility(View.GONE); //hide log in button
                                        choosePageText.setVisibility(View.VISIBLE);
                                        // check which roles user can access then allow those buttons
                                        if (role.get("Admin") != null && role.get("Admin")) {
                                            adminBtn.setVisibility(View.VISIBLE);
                                        }
                                        if (role.get("User") != null && role.get("User")) {
                                            userBtn.setVisibility(View.VISIBLE);
                                        }
                                        if (role.get("Organizer") != null && role.get("Organizer")) {
                                            organizerBtn.setVisibility(View.VISIBLE);
                                        }

                                    }
                                    }
                            } else {
                                // User does not exist, show sign-up fragment
                                showSignUpFragment();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }
        });
        organizerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this, OrganizerActivity.class));
            }
        });
        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this, AdminActivity.class));
            }
        });


    }

    private void showSignUpFragment() {
        loginBtn.setVisibility(View.GONE); //hide log in button
        SignUpFragment signUpFragment = SignUpFragment.newInstance(usersRef,android_id);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_sign_up, signUpFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
