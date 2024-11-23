/**
 * Main activity: handles user login, role verification, and navigation to different activities
 */
package com.example.minion_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.admin.AdminActivity;
import com.example.minion_project.organizer.OrganizerActivity;
import com.example.minion_project.user.UserActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private CollectionReference usersRef,All_UsersRef,organizersRef;
    private String android_id;
    public FireStoreClass Our_Firestore=new FireStoreClass();
    Button loginBtn,userBtn,organizerBtn,adminBtn;
    TextView choosePageText;
    TextView appName;
    /**
     * Called when the activity is starting
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginBtn = findViewById(R.id.start);
        appName=findViewById(R.id.choose_page_launch);
        userBtn = findViewById(R.id.userBtn);
        organizerBtn = findViewById(R.id.organizerBtn);
        adminBtn=findViewById(R.id.adminBtn);
        choosePageText=findViewById(R.id.choose_page_launch);
        All_UsersRef = Our_Firestore.getAll_UsersRef();
        usersRef = Our_Firestore.getUsersRef();
        organizersRef=Our_Firestore.getOrganizersRef();
        createNotificationChannel();
        android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        Notification notification = new Notification(android_id);
        notification.addUserToNotificationWon(android_id);



        loginBtn.setOnClickListener(new View.OnClickListener()


        {
            /**
             * Called when a view has been clicked.
             * @param v
             */
            @Override
            public void onClick(View v) {// Get device id
                android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);


                // Check if user exists
                All_UsersRef.document(android_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    /**
                     * Called when a task completes.
                     * @param task
                     */
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                Map<String, Object> data = document.getData();
                                if (data!=null) {
                                    // User exists, retrieve data
                                    Object roleObj = data.get("Roles");
                                    if (roleObj instanceof Map) {
                                        Map<String, Boolean> role = (Map<String, Boolean>) roleObj;
                                        displayButtons(role);
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



        /**
         * navigates to user activity when user button is clicked
         */
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }
        });

        /**
         * navigates to organizer activity when organizer button is clicked
         */
        organizerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OrganizerActivity.class));
            }
        });

        /**
         * navigates to admin activity when admin button is clicked
         */
        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AdminActivity.class));
            }
        });


    }

    /**
     * Displays buttons based on user roles
     * @param role the user's roles
     */
    public void displayButtons(Map<String, Boolean> role){
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

    /**
     * Shows the sign-up fragment
     */
    private void showSignUpFragment() {
        appName.setVisibility(View.GONE);
        loginBtn.setVisibility(View.GONE); //hide log in button
        SignUpFragment signUpFragment = SignUpFragment.newInstance(All_UsersRef,android_id,usersRef,organizersRef);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_sign_up, signUpFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "my_channel_id";
            CharSequence name = "My Notifications";
            String description = "Channel for app notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
