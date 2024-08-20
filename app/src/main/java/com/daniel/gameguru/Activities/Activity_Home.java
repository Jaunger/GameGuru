package com.daniel.gameguru.Activities;

import static com.daniel.gameguru.Utilities.Utilities.hideKeyboard;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.gameguru.Adapters.GuideAdapter;
import com.daniel.gameguru.R;
import com.daniel.gameguru.Utilities.DbManager;
import com.daniel.gameguru.Utilities.NavigationBarManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Activity_Home extends AppCompatActivity {

    private RecyclerView featuredGuidesRecycler;
    private RecyclerView recentlyViewedGuidesRecycler;
    private TextView welcomeText;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        overridePendingTransition(R.anim.dark_screen, R.anim.light_screen);


        setupUI(findViewById(R.id.homeParent));
        findViews();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGuides();
    }



    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideKeyboard(Activity_Home.this);
                v.clearFocus();
                v.performClick();
                return false;
            });
        }

    }

    private void findViews() {
        featuredGuidesRecycler = findViewById(R.id.featured_guides_recycler);
        recentlyViewedGuidesRecycler = findViewById(R.id.recently_viewed_guides_recycler);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        welcomeText = findViewById(R.id.welcome_text);
    }

    private void initView() {
        NavigationBarManager.getInstance().setupBottomNavigationView(bottomNavigationView, this);
        NavigationBarManager.getInstance().setNavigation(bottomNavigationView, this, R.id.navigation_home);

        featuredGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recentlyViewedGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
       DbManager.getUserName(res -> {
            if (res != null) {
                welcomeText.setText(String.format("Welcome, %s!", res));
            } else {
                Log.e("FireStoreError", "Error getting user name: ");
                Toast.makeText(Activity_Home.this, "Failed to load user name. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        }
        updateGuides();
    }
    private void updateGuides() {
        DbManager.getFeaturedGuides(guideList -> featuredGuidesRecycler.setAdapter(new GuideAdapter(guideList)));
        DbManager.getRecentlyViewedGuides(guideList -> recentlyViewedGuidesRecycler.setAdapter(new GuideAdapter(guideList)));
    }





}
