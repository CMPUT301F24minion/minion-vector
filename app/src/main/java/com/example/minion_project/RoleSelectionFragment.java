package com.example.minion_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RoleSelectionFragment extends Fragment {

    public static RoleSelectionFragment newInstance() {
        return new RoleSelectionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_view, container, false);

        RadioGroup radioGroup = view.findViewById(R.id.selectViewRadioGroup);
        Button proceedButton = view.findViewById(R.id.jumpToRequestedViewLandingPageButton);

        proceedButton.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == R.id.selectAttendeeViewRadioButton) {
                ((MainActivity) requireActivity()).navigateToAttendeeActivity();
            } else if (selectedId == R.id.selectOrganizerViewRadioButton) {
                ((MainActivity) requireActivity()).navigateToOrganizerActivity();
            }
        });

        return view;
    }
}
