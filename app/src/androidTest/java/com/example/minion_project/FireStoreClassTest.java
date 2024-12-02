package com.example.minion_project;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import com.google.android.gms.tasks.Tasks;

import java.util.List;
import java.util.Map;

public class FireStoreClassTest {

    private FireStoreClass fireStoreClass;

    @Before
    public void setUp() {
        fireStoreClass = new FireStoreClass();
    }

    @Test
    public void testGetFirestore() {
        FirebaseFirestore db = fireStoreClass.getFirestore();
        assertNotNull(db);
    }

    @Test
    public void testGetUsersRef() {
        CollectionReference usersRef = fireStoreClass.getUsersRef();
        assertNotNull(usersRef);
    }

    @Test
    public void testGetAllUsersRef() {
        CollectionReference allUsersRef = fireStoreClass.getAll_UsersRef();
        assertNotNull(allUsersRef);
    }

    @Test
    public void testGetFacilitiesRef() {
        CollectionReference facilitiesRef = fireStoreClass.getFacilitiesRef();
        assertNotNull(facilitiesRef);
    }

    @Test
    public void testGetOrganizersRef() {
        CollectionReference organizersRef = fireStoreClass.getOrganizersRef();
        assertNotNull(organizersRef);
    }

    @Test
    public void testGetEventsRef() {
        CollectionReference eventsRef = fireStoreClass.getEventsRef();
        assertNotNull(eventsRef);
    }

    @Test
    public void testGetNotificationsAsList() throws Exception {
        String androidID = "test_android_id";
        Task<List<Map<String, Object>>> notificationsTask = fireStoreClass.getNotificationsAsList(androidID);

        List<Map<String, Object>> notifications = Tasks.await(notificationsTask);
        assertNotNull(notifications);
        assertTrue(notifications.size() >= 0);
    }
}
