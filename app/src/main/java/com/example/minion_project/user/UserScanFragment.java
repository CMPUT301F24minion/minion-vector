package com.example.minion_project.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private EventController eventController = new EventController();
    private UserController userController;

    public UserScanFragment(UserController userController) {
        this.userController = userController;
    }

    public static UserScanFragment newInstance(UserController userController) {
        return new UserScanFragment(userController);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_scan_q_r, container, false);
    }

    public void scanQr() {
        ScanOptions options = new ScanOptions();
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        String scannedValue = result.getContents();
        launchUserEventFragment(scannedValue);
    });

    private void launchUserEventFragment(String scannedValue) {
        UserEventFragment userEventFragment = new UserEventFragment(eventController, userController);
        Bundle bundle = new Bundle();
        bundle.putString("scanned_value", scannedValue);
        userEventFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.user_event, userEventFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
