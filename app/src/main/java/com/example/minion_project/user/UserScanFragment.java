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
    public UserScanFragment() {
        // Required empty public constructor
    }

    public static UserScanFragment newInstance(String param1, String param2) {
        UserScanFragment fragment = new UserScanFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

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

    private void scanQr(){
        launchUserEventFragment("scannedValue");

//        ScanOptions options=new ScanOptions();
//        options.setCaptureActivity(CaptureAct.class);
//        barLaucher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        String scannedValue = result.getContents();
        launchUserEventFragment(scannedValue);
    });

    // launch user event fragment
    private void launchUserEventFragment(String scannedValue) {
        // Create a new instance of UserEventFragment
        UserEventFragment userEventFragment = new UserEventFragment(eventController);

        // Bundle data to pass to the fragment
        Bundle bundle = new Bundle();
//        bundle.putString("scanned_value", scannedValue);

        bundle.putString("scanned_value", "2gmP7IgKGHLZnMMMP9GS");
        userEventFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.user_event, userEventFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
