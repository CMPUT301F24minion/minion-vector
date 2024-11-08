/**
 * Fragment class to handle QR code scanning for users and navigating to event details.
 */

package com.example.minion_project.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentTransaction;

import com.example.minion_project.R;
import com.example.minion_project.events.EventController;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class UserScanFragment extends Fragment {
    private Button scanQrBtn;
    private TextView scanQrText;
    private EventController eventController=new EventController();
    private UserController userController;

    /**
     * Constructor for UserScanFragment.
     * @param userController
     */
    public UserScanFragment(UserController userController) {
        this.userController=userController;

    }

    /**
     * Creates a new instance of UserScanFragment.
     * @param userController The user controller for managing user interactions with events.
     * @return new instance of UserScanFragment
     */
    public static UserScanFragment newInstance(UserController userController) {
        UserScanFragment fragment = new UserScanFragment(userController);

        return fragment;
    }

    /**
     * Called to do initial creation of a fragment.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Inflates the layout and initializes the views for the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_scan_q_r, container, false);

        scanQrBtn = view.findViewById(R.id.QrBtn);

        scanQrBtn.setOnClickListener(v->{
            scanQr();
        });
        return view;
    }

    /**
     * Launches the QR code scanning process.
     */
    private void scanQr(){
        launchUserEventFragment("scannedValue");

        ScanOptions options=new ScanOptions();
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        String scannedValue = result.getContents();
        launchUserEventFragment(scannedValue);
    });

    /**
     * Launches the UserEventFragment with the provided scanned value.
     * @param scannedValue  The scanned QR code value representing the event ID
     */
    private void launchUserEventFragment(String scannedValue) {
        // Create a new instance of UserEventFragment
        UserEventFragment userEventFragment = new UserEventFragment(eventController,userController);

        // Bundle data to pass to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("scanned_value", scannedValue);
        // uncomment this to test out
        // bundle.putString("scanned_value", "NoV1RN1bTBltx0DLFw4y");
        userEventFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.user_event, userEventFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
