package com.example.minion_project;

import android.content.Context;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.minion_project.admin.Admin;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Notification {

    private String receiver;

    private String uniqueDocumentID;

    private static final String CHANNEL_ID = "my_channel_id";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Predefined messages
    private static final String JOINED_EVENT_MESSAGE = "Thank you for joining the event!";
    private static final String NOT_CHOSEN_MESSAGE = "You have not been chosen for the event, sorry.";
    private static final String CHOSEN_MESSAGE = "You have been chosen to attend the event, congratulations!";
    private static final String REMOVED_PROFILE_IMAGE = "Your profile picture has been removed for violating policy.";
    private static final String REMOVED_EVENT_IMAGE = "Your event image has been removed for violating policy.";
    private static final String REMOVE_ACCOUNT = "Your account has been removed because it violated policy.";
    private static final String REMOVE_EVENT = "Your event has been removed for violating policy.";
    private static final String ANOTHER_CHANCE_MESSAGE = "You have another chance to participate in the event.";

    // Default constructor
    public Notification() {
        this.receiver = "";
    }

    // Constructor with receiver
    public Notification(String receiver) {
        this.receiver = receiver;
        this.uniqueDocumentID = db.collection("Notifications").document().getId(); // Generate unique ID at creation

    }


    public String getUniqueDocumentID() {
        return uniqueDocumentID;
    }

    // Getter and Setter
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    // Dynamic Notification Method for Joined Event
    public void sendJoinedEventNotification(Context context) {
        saveNotificationToFirestore(getReceiver(), "Event Update", JOINED_EVENT_MESSAGE);
    }

    // Dynamic Notification Method for Not Chosen Entrant
    public void sendNotChosenEntrantNotification(Context context) {
        saveNotificationToFirestore(getReceiver(), "Event Update", NOT_CHOSEN_MESSAGE);
    }

    // Dynamic Notification Method for Chosen Entrant
    public void sendChosenEntrantNotification(Context context) {
        saveNotificationToFirestore(getReceiver(), "Event Update", CHOSEN_MESSAGE);
    }
    public void sendAnotherChanceNotification(Context context) {
        saveNotificationToFirestore(getReceiver(), "Event Update", ANOTHER_CHANCE_MESSAGE);
    }

    // General sendNotification method
    private static void sendNotification(Context context, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_add)  // Replace with your app's icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    /**
     * Queries Firestore for notifications matching the target Android ID and sends them.
     * @param context The application context.
     */
    public void checkAndSendNotifications(Context context) {
        db.collection("Notifications")  // Collection name
                .whereEqualTo("androidID", getReceiver())  // Query for matching androidID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String title = document.getString("title");  // Notification title
                                String message = document.getString("message");  // Notification message
                                // Send notification using dynamic content from Firestore
                                sendNotification(context, title, message);
                            }
                        } else {
                            System.out.println("No matching notifications for this Android ID.");
                        }
                    } else {
                        System.err.println("Error fetching documents: " + task.getException().getMessage());
                    }
                });
    }

    private void saveNotificationToFirestore(String androidID, String title, String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a unique document ID

        // Prepare notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("androidID", androidID);
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("timestamp", new Date()); // Optional: to sort notifications

        // Save the data to Firestore under the unique document ID
        db.collection("Notifications").document(uniqueDocumentID).set(notificationData)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Notification saved successfully.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error saving notification: " + e.getMessage());
                });
    }


}
