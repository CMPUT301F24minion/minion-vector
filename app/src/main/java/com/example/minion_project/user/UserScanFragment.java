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

import com.example.minion_project.R;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class UserScanFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button scanQrBtn;
    private TextView scanQrText;

    private String mParam1;
    private String mParam2;

    public UserScanFragment() {
        // Required empty public constructor
    }

    public static UserScanFragment newInstance(String param1, String param2) {
        UserScanFragment fragment = new UserScanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_scan_q_r, container, false);

        scanQrBtn = view.findViewById(R.id.QrBtn);
        scanQrText = view.findViewById(R.id.QRvalue);

        scanQrBtn.setOnClickListener(v->{
            scanQr();
        });
        return view;
    }

    private void scanQr(){
        ScanOptions options=new ScanOptions();
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });
}
