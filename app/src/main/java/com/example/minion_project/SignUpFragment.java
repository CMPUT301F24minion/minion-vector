package com.example.minion_project;

import static java.lang.Boolean.TRUE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
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

/**
 * In the event a user is not recognized by the user authentication system (device identifier has not previously been logged)
 * we must fetch personal information from them using objects defined in the associated layout file. Input constraints are enforced.
 * The new individual is then logged in the database in whatever collections they request permissions from.
 */
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
            R.drawable.baseline_add,
            R.drawable.baseline_adjust_24,
            R.drawable.baseline_tag_faces,
    };

    /**
     * Factory method to create a new instance of this fragment.
     * @param All_UsersRef  reference to all_users collection in Firestore
     * @param android_id device identifier
     * @param usersRef reference to users collection in Firestore
     * @param organizersRef reference to organizers collection in Firestore
     * @return
     */
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

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return A new instance of SignUpFragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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
            if (imageUri == null) {
                randomizeProfile();
                uploadImageToFirebase();

            }
            else {
                uploadImageToFirebase();
            }
        });

        return view;
    }

    /**
     * Sets the profile image to the first letter of the user's name
     * with a default background color.
     */
    private void randomizeProfile() {
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            // Set default image or background if name is empty
            profileImageView.setBackgroundColor(Color.LTGRAY); // Default light gray background
            profileImageView.setImageDrawable(null); // Clear any previous image
            return;
        }

        char firstLetter = name.charAt(0); // Get the first letter of the name

        // Create a Bitmap to draw the profile
        int size = 200; // Size of the profile image
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Fill the background with a default color
        canvas.drawColor(Color.WHITE); // Light gray background

        // Configure the Paint object to draw the text
        Paint paint = new Paint();
        paint.setColor(Color.BLACK); // Text color
        paint.setTextSize(80); // Text size
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        // Calculate the text position
        Rect textBounds = new Rect();
        paint.getTextBounds(String.valueOf(firstLetter), 0, 1, textBounds);
        float x = size / 2f;
        float y = size / 2f - textBounds.exactCenterY();

        // Draw the first letter
        canvas.drawText(String.valueOf(firstLetter).toUpperCase(), x, y, paint);

        // Set the Bitmap as the profile picture
        profileImageView.setImageBitmap(bitmap);
    }


    /**
     * Signs up a new user in the database.
     * @param imageUrl URL of the user's profile image
     */
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
            return;
        }

        Map<String, Boolean> roles = new HashMap<>();
        roles.put("Admin", Boolean.TRUE);
        roles.put("Organizer", organizerSelected);
        roles.put("User", userSelected);

        Map<String, Object> alluser = new HashMap<>();
        alluser.put("Name", name);
        alluser.put("Email", email);
        alluser.put("Phone_number", phone);
        alluser.put("City", city);
        alluser.put("profileImage", imageUrl);
        alluser.put("Roles", roles);
        alluser.put("AllowNotifications", TRUE);

        if (organizerSelected) {
            Map<String, Object> organizer = new HashMap<>();
            organizer.put("Name", name);
            organizer.put("Email", email);
            organizer.put("Phone_number", phone);
            ArrayList<String> events = new ArrayList<>();
            organizer.put("Events", events);
            organizer.put("Facility", false);
            saveDocument(organizersRef, organizer);
        }
        if (userSelected) {
            Map<String, Object> user = new HashMap<>();
            user.put("Name", name);
            user.put("Email", email);
            user.put("Phone_number", phone);
            user.put("Location", city);
            user.put("profileImage", imageUrl);
            HashMap<String, String> events = new HashMap<>();
            user.put("Events", events);
            user.put("AllowNotifications", TRUE);
            user.put("longitude", null);
            user.put("latitude", null);

            HashMap<String, ArrayList<String>> notifications = new HashMap<>();
            ArrayList<String> temp = new ArrayList<>();
            notifications.put("join_event_notification", temp);
            notifications.put("not_selected_notification", temp);
            user.put("Notification", notifications);

            saveDocument(usersRef, user);
        }

        All_UsersRef.document(android_id)
                .set(alluser)
                .addOnSuccessListener(result -> showLoginButtons(roles))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
    }

    /**
     * Saves a document to a Firestore collection
     * @param collectionRef reference to the collection
     * @param data data to be saved
     */
    private void saveDocument(CollectionReference collectionRef, Map<String, Object> data) {
        collectionRef.document(android_id).set(data)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
    }

    /**
     * Displays login buttons to user for recognized roles
     * @param role
     */
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

    /**
     * Opens a file chooser to allow the user to select an image.
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result from a child activity that was started for a result
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);  // Display the selected image
        }
    }

    /**
     * Uploads the selected image to Firebase Storage.
     */
    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // Upload image using imageUri
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("profile_images/" + android_id + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        signUpUser(imageUrl);  // Save the image URL in Firestore
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            // Handle case when no image is selected
            Bitmap bitmap = Bitmap.createBitmap(profileImageView.getWidth(), profileImageView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            profileImageView.draw(canvas);

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
        }
    }
}
