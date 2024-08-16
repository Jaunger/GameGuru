package com.daniel.gameguru.Utilities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.daniel.gameguru.Activities.Activity_CreateGuide;
import com.daniel.gameguru.Activities.Activity_Home;
import com.daniel.gameguru.Activities.Activity_Profile;
import com.daniel.gameguru.Activities.Activity_Search;
import com.daniel.gameguru.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationBarManager {

    private static final String TAG = "NavigationBarManager";
    private static NavigationBarManager instance;

    private NavigationBarManager() {
        // Private constructor to prevent instantiation
    }

    public static NavigationBarManager getInstance() {
        if (instance == null) {
            instance = new NavigationBarManager();
        }
        return instance;
    }



    public void setupBottomNavigationView(BottomNavigationView bottomNavigationView, Context context) {
        Log.d(TAG, "Setting up BottomNavigationView for role: ");
        Menu menu = bottomNavigationView.getMenu();
        menu.clear();

            menu.add(Menu.NONE, R.id.navigation_home, Menu.NONE, R.string.home)
                    .setIcon(R.drawable.ic_home);
            menu.add(Menu.NONE, R.id.navigation_search, Menu.NONE, R.string.following) //TODo: change to search
                    .setIcon(R.drawable.ic_search);
            menu.add(Menu.NONE, R.id.navigation_create, Menu.NONE, R.string.create)
                    .setIcon(R.drawable.ic_create);
            menu.add(Menu.NONE, R.id.navigation_notifications, Menu.NONE, R.string.notifications)
                    .setIcon(R.drawable.ic_notifications);
            menu.add(Menu.NONE, R.id.navigation_account, Menu.NONE, R.string.account)
                    .setIcon(R.drawable.ic_account);





    }

    public void setNavigation(BottomNavigationView bottomNavigationView, Context context, int selected) {
        bottomNavigationView.setSelectedItemId(selected);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            Intent intent = null;
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                intent = new Intent(context, Activity_Home.class);
            } else if (id == R.id.navigation_account) {
                intent = new Intent(context, Activity_Profile.class);
            }
             else if (id == R.id.navigation_search) {
                intent = new Intent(context, Activity_Search.class);
            } else if (id == R.id.navigation_notifications) {
               // intent = new Intent(context, Ac.class);
                return true;
            }if (id == R.id.navigation_create) {
                intent = new Intent(context, Activity_CreateGuide.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                if (context instanceof AppCompatActivity) {
                    ((AppCompatActivity) context).finish();
                }
            }
            return true;
        });
    }
}
