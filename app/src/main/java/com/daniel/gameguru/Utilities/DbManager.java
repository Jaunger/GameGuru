package com.daniel.gameguru.Utilities;

import androidx.annotation.NonNull;

import com.daniel.gameguru.Entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DbManager {
    public static void updateUserImage(String imageUrl, CallBack callBack) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.child(userUid).child("image").setValue(imageUrl)
                .addOnSuccessListener(unused -> {
                    callBack.res(null);
                });
    }


    public interface CallBack<T> {
        void res(T res);
    }


    public static void getUserImage(CallBack<String> callBack) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserImage(userUid, callBack);
    }

    public static void getUserImage(String userId, CallBack<String> callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null) {
                    callBack.res(user.getImage());
                } else {
                    callBack.res(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static void getUserName(String userId, CallBack<String> callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null) {
                    callBack.res(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
