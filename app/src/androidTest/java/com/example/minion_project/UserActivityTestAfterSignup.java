package com.example.minion_project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.minion_project.user.UserActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserActivityTestAfterSignup {

    @Rule
    public ActivityScenarioRule<UserActivity> activityScenarioRule =
            new ActivityScenarioRule<>(UserActivity.class);

    @Test
    public void testBottomNavigationSwitchesFragments() {

        // Click on the 'Attending' menu item
        onView(withId(R.id.menu_user_attending)).perform(click());

        // Check that the textView displays "What's Popping"
        onView(withId(R.id.textView))
                .check(matches(withText("What's Popping")));

        // Click on the 'Updates' menu item
        onView(withId(R.id.menu_user_updates)).perform(click());

        // Check that the textView displays "Notifications"
        onView(withId(R.id.textView))
                .check(matches(withText("Notifications")));

        // Click on the 'Scan QR' menu item
        onView(withId(R.id.menu_user_scan_qr)).perform(click());

        // Check that the textView displays "Scan QR"
        onView(withId(R.id.textView))
                .check(matches(withText("Scan QR")));

        // Click on the 'Settings' menu item
        onView(withId(R.id.menu_user_settings)).perform(click());

        // Check that the textView displays "Settings"
        onView(withId(R.id.textView)).check(matches(withText("Settings")));
    }
}
