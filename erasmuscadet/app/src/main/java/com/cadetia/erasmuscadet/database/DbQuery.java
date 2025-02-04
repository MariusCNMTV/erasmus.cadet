package com.cadetia.erasmuscadet.database;

import android.util.ArrayMap;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.cadetia.erasmuscadet.listeners.MyCompleteListener;
import com.cadetia.erasmuscadet.model.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbQuery {

    public static final String TAG = "DbQuery";
    public static FirebaseFirestore g_firestore;

    public interface UserScoreListener {
        void onUserScoresReceived(List<UserModel> userList);
        void onFailure();
    }

    public static void getUsersSortedByScore(UserScoreListener userScoreListener) {
        g_firestore.collection("USERS")
                .orderBy("TOTAL_SCORE", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserModel> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        UserModel user = document.toObject(UserModel.class);
                        userList.add(user);
                    }
                    userScoreListener.onUserScoresReceived(userList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting users sorted by score: ", e);
                    userScoreListener.onFailure();
                });
    }

    public static void createUserData(String email, String name, String photo, MyCompleteListener completeListener) {
        // Check if user already exists
        g_firestore.collection("USERS")
                .document(email) // Use email as document ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User already exists, update user's name and photo
                        DocumentReference userDocRef = documentSnapshot.getReference();
                        Map<String, Object> updates = new ArrayMap<>();
                        updates.put("NAME", name);
                        updates.put("PHOTO", photo);

                        userDocRef.update(updates)
                                .addOnSuccessListener(unused -> completeListener.onSucces())
                                .addOnFailureListener(e -> completeListener.onFailure());
                    } else {
                        // User does not exist, proceed with user creation
                        Map<String, Object> userData = new ArrayMap<>();
                        userData.put("EMAIL_ID", email);
                        userData.put("NAME", name);
                        userData.put("PHOTO", photo);
                        userData.put("TOTAL_SCORE", 0);

                        // Create a new user with email as document ID
                        g_firestore.collection("USERS")
                                .document(email)
                                .set(userData)
                                .addOnSuccessListener(unused -> {
                                    // Increment the count
                                    incrementUserCount(completeListener);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure to create user
                                    completeListener.onFailure();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to check user existence
                    completeListener.onFailure();
                });
    }

    private static void incrementUserCount(MyCompleteListener completeListener) {
        DocumentReference countDoc = g_firestore.collection("USERS").document("TOTAL_USERS");
        countDoc.update("COUNT", FieldValue.increment(1))
                .addOnSuccessListener(unused -> completeListener.onSucces())
                .addOnFailureListener(e -> completeListener.onFailure());
    }


}
