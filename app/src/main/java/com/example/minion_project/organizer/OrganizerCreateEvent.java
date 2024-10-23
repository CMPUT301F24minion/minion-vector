package com.example.minion_project.organizer;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
                    selectTime.setText("Time: " + selectedTime);
                }, hour, minute, true);  // Set true for 24-hour format

        timePickerDialog.show();  // Show the dialog
    }

    // Method to open the DatePickerDialog (you can implement this similarly to TimePickerDialog)
    public void openDatePickerDialog() {
        // Implementation for opening the date picker
        // You can use DatePickerDialog similar to how TimePickerDialog is used
    }

    // Method to handle image upload (just a placeholder for now)
    public void uploadImage() {
        // You can implement the logic for uploading an image, maybe using an intent to open the gallery
        Toast.makeText(getContext(), "Upload Image clicked!", Toast.LENGTH_SHORT).show();
    }
}
