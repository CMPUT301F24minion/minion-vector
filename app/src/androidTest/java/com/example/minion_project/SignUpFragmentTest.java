package com.example.minion_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    private String android_id;

    @Before
    public void setUp() {
        // Initialize Firebase and get device ID
        Context context = ApplicationProvider.getApplicationContext();
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Clean up any existing data associated with this device ID before running tests
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("All_Users").document(android_id).delete();
        db.collection("Users").document(android_id).delete();
        db.collection("Organizers").document(android_id).delete();
    }


    @Test
    public void testSignUpAsUserAndOrganizer_Success() {

        // Click on the start button to navigate to SignUpFragment
        onView(withId(R.id.start)).perform(click());

        // Wait until the SignUpFragment is fully displayed by checking signup_button visibility
//        onView(withId(R.id.signup_button)).check(matches(isDisplayed()));

        // Enter valid data in each field
        onView(withId(R.id.organizerBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).perform(typeTextIntoFocusedView("Test User"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeTextIntoFocusedView("testuser@example.com"), closeSoftKeyboard());
        onView(withId(R.id.phone_number)).perform(typeTextIntoFocusedView("1234567890"), closeSoftKeyboard());
        onView(withId(R.id.city)).perform(typeTextIntoFocusedView("Test City"), closeSoftKeyboard());

        // Select both roles
        onView(withId(R.id.organizer_checkbox)).perform(click());
        onView(withId(R.id.user_checkbox)).perform(click());

        // Click on the sign-up button
        onView(withId(R.id.signup_button)).perform(click());
        Log.d("TestDebug", "Clicked signup button to submit data");

        // Short delay to allow transition to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that input fields are hidden after signing up
        onView(withId(R.id.name)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
//        onView(withId(R.id.email)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
//        onView(withId(R.id.phone_number)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
//        onView(withId(R.id.city)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
//        onView(withId(R.id.organizer_checkbox)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
//        onView(withId(R.id.user_checkbox)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
//        onView(withId(R.id.signup_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        // Verify that both User and Organizer buttons are displayed in MainActivity after signup
        onView(withId(R.id.userBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.organizerBtn)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("All_Users").document(android_id).delete();
        db.collection("Users").document(android_id).delete();
        db.collection("Organizers").document(android_id).delete();
    }
}
