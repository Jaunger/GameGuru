package com.daniel.gameguru.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.gameguru.Adapters.UserAdapter;
import com.daniel.gameguru.Entities.User;
import com.daniel.gameguru.R;
import com.daniel.gameguru.Utilities.DbManager;
import com.daniel.gameguru.Utilities.NavigationBarManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class Activity_Following extends AppCompatActivity {

    private RecyclerView followingRecyclerView;
    private UserAdapter userAdapter;
    private List<User> followingUsers;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        overridePendingTransition(R.anim.dark_screen, R.anim.light_screen);

        findViews();
        initViews();
        loadFollowingUsers();
    }

    private void findViews() {
        followingRecyclerView = findViewById(R.id.followingRecyclerView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void initViews() {
        followingUsers = new ArrayList<>();
        userAdapter = new UserAdapter(this, followingUsers);

        followingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        followingRecyclerView.setAdapter(userAdapter);

        NavigationBarManager.getInstance().setupBottomNavigationView(bottomNavigationView, this);
        NavigationBarManager.getInstance().setNavigation(bottomNavigationView, this, R.id.navigation_notifications);
    }

    private void loadFollowingUsers() {
        DbManager.getFollowingUsers(followingList -> {
            if (followingList != null) {
                this.followingUsers.clear();
                this.followingUsers.addAll(followingList);
                userAdapter.notifyItemRangeInserted(0, followingUsers.size());
            } else {
                Log.e("Activity_Following", "Failed to load following users");
            }
        });
    }

}
