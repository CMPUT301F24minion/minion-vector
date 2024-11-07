package com.example.minion_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WelcomeKnownUserFragment extends Fragment {

    private static final String ARG_USER_NAME = "userName";
    private String userName;

    public static WelcomeKnownUserFragment newInstance(String userName) {
        WelcomeKnownUserFragment fragment = new WelcomeKnownUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(ARG_USER_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_known_user, container, false);
        TextView welcomeTextView = view.findViewById(R.id.welcomeBackTextView);
        welcomeTextView.setText("Welcome back, " + userName + "!");

        // Set up the click listener to proceed to role selection
        view.setOnClickListener(v -> ((MainActivity) requireActivity()).showRoleSelectionFragment());

        return view;
    }
}
