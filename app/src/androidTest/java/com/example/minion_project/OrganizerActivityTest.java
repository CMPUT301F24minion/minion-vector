package com.example.minion_project;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.minion_project.organizer.OrganizerActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
public class OrganizerActivityTest {


    @Test
    public void testCreateEventUI() {
        // Navigate to the Create Event fragment
        onView(withId(R.id.menu_organizer_create_event)).perform(click());

        // Check that the Create Event screen is displayed
        onView(withId(R.id.organizerCreateEvent)).check(matches(isDisplayed()));

        // Enter event title
        onView(withId(R.id.createEventTitleEditText))
                .perform(typeText("Test Event"))
                .check(matches(withText("Test Event")));

        // Enter event details
        onView(withId(R.id.createEventDetailsEditText))
                .perform(typeText("Event Details"))
                .check(matches(withText("Event Details")));

        // Enter number of invitations
        onView(withId(R.id.createEventInvitationsEditText))
                .perform(typeText("50"))
                .check(matches(withText("50")));


    }
}
