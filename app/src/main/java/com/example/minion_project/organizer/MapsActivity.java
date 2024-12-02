package com.example.minion_project.organizer;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.minion_project.databinding.ActivityMapsBinding;
import com.example.minion_project.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MapsActivity is a class that extends FragmentActivity and implements OnMapReadyCallback.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private  ArrayList<HashMap<String, Double>> locations;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<HashMap<String, Double>> mapped = (ArrayList<HashMap<String, Double>>) getIntent().getSerializableExtra("waitlisted");
        this.locations=mapped;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (HashMap<String, Double> locationData : locations) {
            Double latitude = locationData.get("latitude");
            Double longitude = locationData.get("longitude");
            LatLng place = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(place).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place));


        }

    }
}