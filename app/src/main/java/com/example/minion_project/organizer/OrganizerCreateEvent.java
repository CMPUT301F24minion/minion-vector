package com.example.minion_project.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.minion_project.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerCreateEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerCreateEvent extends Fragment {

    private Button selectTime;
    private Button selectDate;
    private Button uploadImage;

    public OrganizerCreateEvent() {
        // Required empty public constructor
    }

    public static OrganizerCreateEvent newInstance(String param1, String param2) {
        OrganizerCreateEvent fragment = new OrganizerCreateEvent();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_create_event, container, false);

        // Find views by their IDs
        selectTime = view.findViewById(R.id.selectTimeButton);
        selectDate = view.findViewById(R.id.selectDateButton);
        uploadImage = view.findViewById(R.id.uploadImageButton);

        // Set click listener for the Select Time button
        selectTime.setOnClickListener(v -> openTimePickerDialog());

        // You can also set listeners for the other buttons here
        selectDate.setOnClickListener(v -> openDatePickerDialog());
        uploadImage.setOnClickListener(v -> uploadImage());

        return view;
    }

    // Method to open the TimePickerDialog
    public void openTimePickerDialog() {
        // Get the current time to set as default in the TimePickerDialog
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a new TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                    // Format the selected time and show it in a Toast message or update your UI
                    String selectedTime = hourOfDay + ":" + String.format("%02d", minuteOfHour);
                    Toast.makeText(getContext(), "Selected Time: " + selectedTime, Toast.LENGTH_SHORT).show();
                    // You can update the button text or store the selected time here
                }, hour, minute, true);  // Set true for 24-hour format

        timePickerDialog.show();  // Show the dialog
    }

    // Method to open the DatePickerDialog (you can implement this similarly to TimePickerDialog)
    // Method to open the DatePickerDialog
    public void openDatePickerDialog() {
        // Get the current date to show in the picker by default
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());

        datePickerDialog.show();
    }


    // Method to handle image upload (just a placeholder for now)
    public void uploadImage() {
        // You can implement the logic for uploading an image, maybe using an intent to open the gallery
        Toast.makeText(getContext(), "Upload Image clicked!", Toast.LENGTH_SHORT).show();
    }
}
