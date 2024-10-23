package com.example.minion_project.organizer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.minion_project.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerCreateEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerCreateEvent extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrganizerCreateEvent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerCreateEvent.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerCreateEvent newInstance(String param1, String param2) {
        OrganizerCreateEvent fragment = new OrganizerCreateEvent();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    private Button selectTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        selectTime = findViewById(R.id.select_time_button);
        selectTime.setOnClickListener(v -> openTimePickerDialog());

    }

    // Opens select time dialogue for user to select time
    public void openTimePickerDialog() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_organizer_create_event, container, false);
    }
}