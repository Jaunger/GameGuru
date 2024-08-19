package com.daniel.gameguru.Utilities;

import com.daniel.gameguru.Entities.Game;
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
    private static FirebaseFirestore getFireStoreInstance() {
        return FirebaseFirestore.getInstance();
    }
    public static void updateUserImage(String imageUrl, CallBack<Void> callBack) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getFireStoreInstance().collection("users").document(userUid)
                .update("image", imageUrl)
                .addOnSuccessListener(aVoid -> callBack.res(null))
                .addOnFailureListener(e -> {
                    // Log the error or handle it as needed
                });
    }

    public static void isCurrentUser(String userUid, CallBack<Boolean> callBack) {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(userUid == null)
            userUid = currentUserUid;
        callBack.res(userUid.equals(currentUserUid));
    }



    // Callback interface for returning data asynchronously
    public interface CallBack<T> {
        void res(T res);
    }

    public static void isUserExists(String userId,CallBack<Boolean> callBack){
        getFireStoreInstance().collection("users").whereEqualTo("id", userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    callBack.res(true);
                }
            }
        });
        callBack.res(false);
    }
    // Search for guides by title in Firestore
    public static void searchGuides(String query, CallBack<List<Guide>> callBack) {
        getFireStoreInstance().collection("guides")
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
                        callBack.res(null);
                    }
                });
    }

    public static void searchGames(String query, CallBack<List<Game>> callBack) {
        getFireStoreInstance().collection("games")
                .whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title", query + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Game> results = new ArrayList<>();
                        QuerySnapshot snapshot = task.getResult();

                        if (snapshot != null) {
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                Game game = document.toObject(Game.class);
                                if (game != null) {
                                    results.add(game);
                                }
                            }
                        }

                        callBack.res(results);
                    } else {
                        callBack.res(null);
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
        getFireStoreInstance().collection("users").document(userId).get()
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
        getFireStoreInstance().collection("users").document(userId).get()
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
