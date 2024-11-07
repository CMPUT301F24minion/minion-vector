package com.example.minion_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WelcomeUnknownUserFragment extends Fragment {

    public static WelcomeUnknownUserFragment newInstance() {
        return new WelcomeUnknownUserFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_unknown_user, container, false);

        // Set up click listener to proceed to sign-up screen
        view.setOnClickListener(v -> ((MainActivity) requireActivity()).showSignUpFragment());

        return view;
    }
}
