package com.example.minion_project;

import static java.lang.Boolean.TRUE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignUpFragment extends Fragment {

    private EditText nameEditText, emailEditText, phoneEditText, cityEditText;
    private CheckBox organizerCheckBox, userCheckBox;
    private Button signupButton;
    private CollectionReference usersRef, All_UsersRef, organizersRef;
    private String android_id;
    private ImageView profileImageView;
    private Button setImageButton;
    private TextView userNotRecognized;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    // New additions for randomization feature
    private static final int[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};
    private static final int[] PROFILE_DRAWABLES = {
            R.drawable.baseline_account_circle_24,  // Replace these with actual drawable resources

    };

    // instantiate a fragment and set the arguments passed
    public static SignUpFragment newInstance(CollectionReference All_UsersRef, String android_id, CollectionReference usersRef, CollectionReference organizersRef) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.usersRef = usersRef;
        fragment.android_id = android_id;
        fragment.All_UsersRef = All_UsersRef;
        fragment.organizersRef = organizersRef;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        nameEditText = view.findViewById(R.id.name);
        emailEditText = view.findViewById(R.id.email);
        profileImageView = view.findViewById(R.id.profileImageView);
        setImageButton = view.findViewById(R.id.setImageButton);
        userNotRecognized = view.findViewById(R.id.userNotRecognized);
        phoneEditText = view.findViewById(R.id.phone_number);
        cityEditText = view.findViewById(R.id.city);
        signupButton = view.findViewById(R.id.signup_button);
        organizerCheckBox = view.findViewById(R.id.organizer_checkbox);
        userCheckBox = view.findViewById(R.id.user_checkbox);



        setImageButton.setOnClickListener(v -> openFileChooser());

        signupButton.setOnClickListener(v -> {
            uploadImageToFirebase();
            randomizeProfile();
        });

        return view;
    }

    private void randomizeProfile() {
        Random random = new Random();

        // Assign a random background color
        int color = COLORS[random.nextInt(COLORS.length)];
        profileImageView.setBackgroundColor(color);

        // Assign a random profile image from the drawable resources
        int drawable = PROFILE_DRAWABLES[random.nextInt(PROFILE_DRAWABLES.length)];
        profileImageView.setImageResource(drawable);
    }

    private void signUpUser(String imageUrl) {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();

        Boolean organizerSelected = organizerCheckBox.isChecked();
        Boolean userSelected = userCheckBox.isChecked();

        if (name.isEmpty()) {
            Toast.makeText(getActivity(), "Please, enter your name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!(organizerSelected || userSelected)) {
            Toast.makeText(getActivity(), "You forgot to select your role!", Toast.LENGTH_SHORT).show();
        }

        Map<String, Boolean> roles = new HashMap<>();
        roles.put("Admin", Boolean.FALSE);
        roles.put("Organizer", organizerSelected);
        roles.put("User", userSelected);

        Map<String, Object> alluser = new HashMap<>();
        alluser.put("Name", name);
        alluser.put("Email", email);
        alluser.put("Phone_number", phone);
        alluser.put("City", city);
        alluser.put("profileImage", imageUrl);
        alluser.put("Roles", roles);

        if (organizerSelected) {
            Map<String, Object> organizer = new HashMap<>();
            organizer.put("Name", name);
            organizer.put("Email", email);
            organizer.put("Phone_number", phone);
            ArrayList events = new ArrayList<>();
            organizer.put("Events", events);
            saveDocument(organizersRef, organizer);
        }
        if (userSelected) {
            Map<String, Object> user = new HashMap<>();
            user.put("Name", name);
            user.put("Email", email);
            user.put("Phone_number", phone);
            user.put("Location", city);
            user.put("profileImage", imageUrl);
            HashMap events = new HashMap<>();
            user.put("Events", events);
            user.put("AllowNotication", TRUE);

            HashMap<String, ArrayList> notifications = new HashMap<>();
            ArrayList<String> temp = new ArrayList<>();
            notifications.put("join_event_notification", temp);
            notifications.put("not_selected_notification", temp);
            user.put("Notification", notifications);

            saveDocument(usersRef, user);
        }

        All_UsersRef.document(android_id)
                .set(alluser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        showLoginButtons(roles);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error writing document", e);
                    }
                });
    }

    private void saveDocument(CollectionReference collectionRef, Map<String, Object> data) {
        collectionRef.document(android_id).set(data)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
    }

    private void showLoginButtons(Map<String, Boolean> role) {
        nameEditText.setVisibility(View.GONE);
        emailEditText.setVisibility(View.GONE);
        phoneEditText.setVisibility(View.GONE);
        cityEditText.setVisibility(View.GONE);
        organizerCheckBox.setVisibility(View.GONE);
        userCheckBox.setVisibility(View.GONE);
        signupButton.setVisibility(View.GONE);

        profileImageView.setVisibility(View.GONE);
        setImageButton.setVisibility(View.GONE);
        userNotRecognized.setVisibility(View.GONE);

        ((MainActivity) getActivity()).displayButtons(role);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);  // Display the selected image
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // Existing code to upload image using imageUri
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("profile_images/" + android_id + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        signUpUser(imageUrl);  // Save the image URL in Firestore
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            // New code to handle drawable image when imageUri is null
            Drawable drawable = profileImageView.getDrawable();
            if (drawable != null) {
                Bitmap bitmap;
                if (drawable instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                } else {
                    // For other drawable types (e.g., VectorDrawable), convert drawable to bitmap
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference("profile_images/" + android_id + ".jpg");

                UploadTask uploadTask = storageRef.putBytes(data);
                uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            signUpUser(imageUrl);  // Save the image URL in Firestore
                        }))
                        .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getActivity(), "Please upload an image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

