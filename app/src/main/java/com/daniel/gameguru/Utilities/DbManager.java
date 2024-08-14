package com.daniel.gameguru.Utilities;

import androidx.annotation.NonNull;

import com.daniel.gameguru.Entities.Guide;
import com.daniel.gameguru.Entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DbManager {

    // Firestore instance
    private final static FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Update user profile image URL in Firestore
    public static void updateUserImage(String imageUrl, CallBack<Void> callBack) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userUid)
                .update("image", imageUrl)
                .addOnSuccessListener(aVoid -> callBack.res(null))
                .addOnFailureListener(e -> {
                    // Log the error or handle it as needed
                });
    }

    // Callback interface for returning data asynchronously
    public interface CallBack<T> {
        void res(T res);
    }

    // Search for guides by title in Firestore
    public static void searchGuides(String query, CallBack<List<Guide>> callBack) {
        db.collection("guides")
                .whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title", query + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Guide> results = new ArrayList<>();
                        QuerySnapshot snapshot = task.getResult();

                        if (snapshot != null) {
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                Guide guide = document.toObject(Guide.class);
                                if (guide != null) {
                                    results.add(guide);
                                }
                            }
                        }

                        callBack.res(results);
                    } else {
                        // Log the error or handle it as needed
                    }
                });
    }

    // Get the current user's profile image URL from Firestore
    public static void getUserImage(CallBack<String> callBack) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserImage(userUid, callBack);
    }

    // Get a specific user's profile image URL from Firestore
    public static void getUserImage(String userId, CallBack<String> callBack) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            User user = document.toObject(User.class);
                            if (user != null) {
                                callBack.res(user.getImage());
                            } else {
                                callBack.res(null);
                            }
                        } else {
                            callBack.res(null);
                        }
                    } else {
                        // Log the error or handle it as needed
                    }
                });
    }

    // Get a specific user's name from Firestore
    public static void getUserName(String userId, CallBack<String> callBack) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            User user = document.toObject(User.class);
                            if (user != null) {
                                callBack.res(user.getName());
                            } else {
                                callBack.res(null);
                            }
                        } else {
                            callBack.res(null);
                        }
                    } else {
                        // Log the error or handle it as needed
                    }
                });
    }
}
