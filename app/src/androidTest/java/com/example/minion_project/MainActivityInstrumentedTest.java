package com.example.minion_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.minion_project.admin.Admin;
import com.example.minion_project.admin.AdminActivity;
import com.example.minion_project.organizer.OrganizerActivity;
import com.example.minion_project.user.UserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    @Before
    public void setup() {
        // Launch MainActivity before each test
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void testLoginButtonIsDisplayedOnLaunch() {
        // Check if the login button is displayed when MainActivity is launched
        onView(withId(R.id.start)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginButtonClickShowsSignUpFragment() {
        // Simulate click on the login button
        onView(withId(R.id.start)).perform(click());

        // Check if the SignUpFragment's container is displayed
        onView(withId(R.id.fragment_sign_up)).check(matches(isDisplayed()));
    }

    @Test
    public void testUserButtonNavigation() {
        init();
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        // Simulate any actions that make the button visible
        scenario.onActivity(activity -> {
            // Mock data to ensure the button is displayed
            Map<String, Boolean> mockRoles = new HashMap<>();
            mockRoles.put("User", true);  // Set the User role to true to make the button visible
            activity.displayButtons(mockRoles);
        });

        // Perform the click on the user button
        onView(withId(R.id.userBtn)).perform(click());

        // Check that UserActivity is launched
        intended(hasComponent(UserActivity.class.getName()));
        release();
    }

    @Test
    public void testOrganizerButtonNavigation() {
        init();
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        // Simulate any actions that make the button visible
        scenario.onActivity(activity -> {
            // Mock data to ensure the button is displayed
            Map<String, Boolean> mockRoles = new HashMap<>();
            mockRoles.put("Organizer", true);  // Set the User role to true to make the button visible
            activity.displayButtons(mockRoles);
        });

        // Perform the click on the organizer button
        onView(withId(R.id.organizerBtn)).perform(click());

        // Check that UserActivity is launched
        intended(hasComponent(OrganizerActivity.class.getName()));
        release();
    }

    @Test
    public void testDisplayButtonsBasedOnRoles() {
        // Simulate the behavior of calling displayButtons() with a mock role map
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            // Create a mock role map
            Map<String, Boolean> mockRoles = new HashMap<>();
            mockRoles.put("User", true);
            mockRoles.put("Organizer", true);
            mockRoles.put("Admin", true);

            // Call the displayButtons() method with mock data
            activity.displayButtons(mockRoles);
        });

        // Verify that all role buttons are displayed
        onView(withId(R.id.userBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.organizerBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.adminBtn)).check(matches(isDisplayed()));
    }

}