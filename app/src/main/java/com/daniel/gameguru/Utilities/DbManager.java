package com.daniel.gameguru.Utilities;

import android.util.Log;

import com.daniel.gameguru.Entities.Game;
import com.daniel.gameguru.Entities.Guide;
import com.daniel.gameguru.Entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DbManager {




    public interface FireStoreCallback<T> {
        void res(T value);
    }

    public interface FireStoreCallbackList {
        void res(List<Guide> guides);
    }

    // FireStore instance
    private static FirebaseFirestore getFireStoreInstance() {
        return FirebaseFirestore.getInstance();
    }
    public static void updateUserImage(String imageUrl, FireStoreCallback<Void> callBack) {
        String userUid = getCurrentId();
        if (userUid == null) return;
        getFireStoreInstance().collection("users").document(userUid)
                .update("image", imageUrl)
                .addOnSuccessListener(aVoid -> callBack.res(null))
                .addOnFailureListener(e -> {
                    // Log the error or handle it as needed
                });
    }

    public static void isCurrentUser(String userUid, FireStoreCallback<Boolean> callBack) {
        String currentUserUid = getCurrentId();
        if(userUid == null){
            callBack.res(true);
            return;
        }
        callBack.res(userUid.equals(currentUserUid));
    }

    private static String getCurrentId() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static void getCurrentUser(FireStoreCallback<User> callBack) {
        String userUid = getCurrentId();
        if (userUid == null) return;
        getFireStoreInstance().collection("users").document(userUid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            User user = document.toObject(User.class);
                            callBack.res(user);
                        } else {
                            callBack.res(null);
                        }
                    } else {
                        Log.d("FireStoreError", "Error getting documents: ", task.getException());
                    }
                });
    }

    public static void updateCurrentUser(User user, FireStoreCallback<Void> callBack) {
        String userUid = getCurrentId();
        if (userUid == null) return;
        getFireStoreInstance().collection("users").document(userUid)
                .set(user)
                .addOnSuccessListener(aVoid -> callBack.res(null))
                .addOnFailureListener(e -> {
                    Log.e("DbManager", "Failed to update user", e);
                });
    }

    public static void isUserExists(String userId,FireStoreCallback<Boolean> callBack){
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
    // Search for guides by title in FireStore
    public static void searchGuides(String query, FireStoreCallback<List<Guide>> callBack) {
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

    public static void searchGames(String query, FireStoreCallback<List<Game>> callBack) {
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


    public static void addRecentGuide(String id, String guideId, FireStoreCallback<Boolean> callBack) {
        getFireStoreInstance().collection("users").document(id)
                .get().addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        Map<String, Integer> recentGuides = user.getRecentGuides();

                        recentGuides.put(guideId, (int) (System.currentTimeMillis() / 1000));

                        if(recentGuides.size() > 3){
                            List<String> keys = new ArrayList<>(recentGuides.keySet());
                            keys.sort(Comparator.comparing(recentGuides::get));
                            recentGuides.remove(keys.get(0));
                        }
                        user.setRecentGuides(recentGuides);
                        getFireStoreInstance().collection("users").document(id)
                                .set(user)
                                .addOnSuccessListener(aVoid -> callBack.res(true))
                                .addOnFailureListener(e -> callBack.res(false));
                    }

                });
    }

    // Get a specific user's profile image URL from FireStore
    public static void getUserImage(FireStoreCallback<String> callBack) {
        String userUid = getCurrentId();
        if (userUid == null) return;
        getFireStoreInstance().collection("users").document(userUid).get()
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
                        Log.d("FireStoreError", "Error getting documents: ", task.getException());
                    }
                });
    }

    // Get a specific user's name from FireStore
    public static void getUserName(FireStoreCallback<String> callBack) {
        String userUid = getCurrentId();
        if (userUid == null) return;
        getFireStoreInstance().collection("users").document(userUid).get()
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
                        Log.d("FireStoreError", "Error getting documents: ", task.getException());
                    }
                });
    }


    public static void getFeaturedGuides(FireStoreCallbackList callBack) {
        getFireStoreInstance().collection("guides")
                .get()
                .addOnSuccessListener(featGuides -> {
                    List<Guide> guides = featGuides.toObjects(Guide.class);

                    if (guides.size() > 3) {
                        Collections.shuffle(guides);
                        guides = guides.subList(0, 3);
                    }

                    callBack.res(guides);
                })
                .addOnFailureListener(e -> callBack.res(null));
    }

    public static void getRecentlyViewedGuides(FireStoreCallback<List<Guide>> callBack) {
        String userUid = getCurrentId();
        if (userUid == null) return;
        getFireStoreInstance().collection("users").document(userUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    User user = documentSnapshot.toObject(User.class);
                    if (user != null && user.getRecentGuides() != null) {
                        Map<String, Integer> userRecentGuides = user.getRecentGuides();
                        List<String> guideIds = new ArrayList<>(userRecentGuides.keySet());

                        if (guideIds.isEmpty()) {
                            callBack.res(new ArrayList<>()); // No recent guides
                        }


                        getFireStoreInstance().collection("guides").whereIn("id", guideIds)
                                .get()
                                .addOnSuccessListener(recentGuides -> {
                                    List<Guide> guides = recentGuides.toObjects(Guide.class);
                                    callBack.res(guides);
                                })
                                .addOnFailureListener(e -> callBack.res(null));

                    }
                }).addOnFailureListener(e -> callBack.res(null));

    }

}
