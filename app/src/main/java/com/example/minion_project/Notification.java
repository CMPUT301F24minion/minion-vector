/**
 * this class handles all the notifications, from sending, to adding the user to the system and etc.
 */

package com.example.minion_project;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

public class Notification {

    private String receiver;

    private String uniqueDocumentID;

    private static final String CHANNEL_ID = "my_channel_id";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Predefined document IDs for the four notification types
    private static final String Won = "Won_lottery";
    private static final String Lost = "lost_lottery";
    private static final String cancelled = "cancelled_event";
    private static final String waitlisted = "waitlistlist_entrants";

    // Predefined messages (assumed to be stored in Firestore documents)
    // If you have constants in code, you can use them here

    // Default constructor
    public Notification() {
        this.receiver = "";
    }

    // Constructor with receiver
    public Notification(String receiver) {
        this.receiver = receiver;
        this.uniqueDocumentID = db.collection("Notifications").document().getId(); // Generate unique ID at creation
    }

    /**
     *
     * @return the uniqueDocumentID
     */

    public String getUniqueDocumentID() {
        return uniqueDocumentID;
    }

    /**
     *
     * @return the receiver, this is their androidID
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * this method sets the receiver androidID
     * @param receiver
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * this method sends the notification to the users' notification tab when they scroll down from
     * the top of the screen
     * @param context
     * @param title
     * @param message
     */
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
     * Checks each notification document to see if the user's Android ID is present in the array.
     * If found, sends the notification and removes the Android ID from the array.
     *
     * @param context The application context.
     */
    public void checkAndSendNotifications(Context context) {
        checkAndSendNotificationForDocument(context, Won);
        checkAndSendNotificationForDocument(context, Lost);
        checkAndSendNotificationForDocument(context, cancelled);
        checkAndSendNotificationForDocument(context, waitlisted);
    }

    /**
     * this method checks whether the Users' ID is in the notification collection (any. win, lose, cancelled or joined lottery)
     * if its in there, it will call sendNotification, and remove the Users' ID from that document.
     * @param context
     * @param documentID the document with the associated ID
     */

    private void checkAndSendNotificationForDocument(Context context, String documentID) {
        DocumentReference docRef = db.collection("Notifications").document(documentID);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Get the IDs array
                    List<String> androidIDs = (List<String>) document.get("ID");
                    if (androidIDs != null && androidIDs.contains(getReceiver())) {
                        // Retrieve the user's allowNotifications setting
                        DocumentReference userDocRef = db.collection("All_Users").document(getReceiver());
                        userDocRef.get().addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful()) {
                                DocumentSnapshot userDocument = userTask.getResult();
                                if (userDocument.exists()) {
                                    Boolean allowNotifications = userDocument.getBoolean("allowNotifications");
                                    if (allowNotifications != null && allowNotifications) {
                                        String title = document.getString("Title");
                                        String message = document.getString("Message");

                                        // Send notification
                                        sendNotification(context, title, message);
                                    } else {
                                        System.out.println("User has disabled notifications.");
                                    }
                                } else {
                                    System.out.println("User document does not exist.");
                                }

                                // Remove the androidID from the array after processing
                                androidIDs.remove(getReceiver());
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("ID", androidIDs);

                                docRef.update(updates).addOnSuccessListener(aVoid -> {
                                    System.out.println("AndroidID removed from notification document after processing.");
                                }).addOnFailureListener(e -> {
                                    System.err.println("Error updating notification document: " + e.getMessage());
                                });

                            } else {
                                System.err.println("Error getting user document: " + userTask.getException().getMessage());
                            }
                        });

                    } else {
                        System.out.println("AndroidID not found in notification document: " + documentID);
                    }
                } else {
                    System.out.println("Notification document does not exist: " + documentID);
                }
            } else {
                System.err.println("Error getting notification document: " + task.getException().getMessage());
            }
        });
    }


    /**
     * this method calls the method that adds the user to the documentID of winning the lottery
     * @param androidID
     */
    public void addUserToNotificationWon(String androidID) {
        addUserToNotificationDocument(Won, androidID);
    }

    /**
     * this method calls the method that adds the user to the documentID of losing the lottery system
     * @param androidID
     */
    public void addUserToNotificationLost(String androidID) {
        addUserToNotificationDocument(Lost, androidID);
    }

    /**
     * this method calls the method that adds the user to the documentID of cancelling the event
     * @param androidID
     */
    public void addUserToNotificationCancelled(String androidID) {
        addUserToNotificationDocument(cancelled, androidID);
    }

    /**
     * this method calls the method that adds the user to the documentID of being waitlisted
     * @param androidID
     */
    public void addUserToNotificationWaitlisted(String androidID) {
        addUserToNotificationDocument(waitlisted, androidID);
    }

    /**
     * this method adds the user to the collection of Notifications, the documentID is a String of
     * the type of notification we want to send to the User
     * @param documentID
     * @param androidID
     */
    public void addUserToNotificationDocument(String documentID, String androidID) {
        DocumentReference docRef = db.collection("Notifications").document(documentID);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Get the existing IDs array
                    List<String> androidIDs = (List<String>) document.get("ID");
                    if (androidIDs == null) {
                        androidIDs = new ArrayList<>();
                    }
                    // Add the androidID if not already present
                    if (!androidIDs.contains(androidID)) {
                        androidIDs.add(androidID);

                        // Update the document with new IDs array
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("ID", androidIDs);

                        docRef.update(updates).addOnSuccessListener(aVoid -> {
                            System.out.println("Notification document updated successfully.");
                        }).addOnFailureListener(e -> {
                            System.err.println("Error updating notification document: " + e.getMessage());
                        });
                    } else {
                        System.out.println("AndroidID already exists in notification document: " + documentID);
                    }
                } else {
                    // Document does not exist, create it
                    Map<String, Object> data = new HashMap<>();
                    List<String> androidIDs = new ArrayList<>();
                    androidIDs.add(androidID);
                    data.put("ID", androidIDs);
                    // message and title are constants; assume they are already set in Firestore

                    docRef.set(data).addOnSuccessListener(aVoid -> {
                        System.out.println("Notification document created successfully.");
                    }).addOnFailureListener(e -> {
                        System.err.println("Error creating notification document: " + e.getMessage());
                    });
                }
            } else {
                System.err.println("Error getting notification document: " + task.getException().getMessage());
            }
        });
    }


}
