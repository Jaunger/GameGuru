package com.daniel.gameguru.Activities;

import static com.daniel.gameguru.Utilities.Utilities.hideSoftKeyboard;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.gameguru.Entities.Guide;
import com.daniel.gameguru.GuideAdapter;
import com.daniel.gameguru.R;
import com.daniel.gameguru.Utilities.NavigationBarManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Activity_Home extends AppCompatActivity {

    private RecyclerView featuredGuidesRecycler;
    private RecyclerView recentlyViewedGuidesRecycler;
    private RecyclerView popularGuidesRecycler;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        overridePendingTransition(R.anim.dark_screen, R.anim.light_screen);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupUI(findViewById(R.id.homeParent));
        findViews();
        initView();
    }

    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Activity_Home.this);
                    v.clearFocus();
                    return false;
                }
            });
        }

    }

    private void findViews() {
        featuredGuidesRecycler = findViewById(R.id.featured_guides_recycler);
        recentlyViewedGuidesRecycler = findViewById(R.id.recently_viewed_guides_recycler);
        popularGuidesRecycler = findViewById(R.id.popular_guides_recycler);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void initView() {
        NavigationBarManager.getInstance().setupBottomNavigationView(bottomNavigationView, this);
        NavigationBarManager.getInstance().setNavigation(bottomNavigationView, this, R.id.navigation_home);

        featuredGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recentlyViewedGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popularGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));



        loadGuidesFromFirestore(); // Uncomment this to load real data from Firestore
    }



    private void loadGuidesFromFirestore() {
        db.collection("guides").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Guide> guides = new ArrayList<>();
                task.getResult().forEach(document -> {
                    Guide guide = document.toObject(Guide.class);
                    guides.add(guide);
                });
                featuredGuidesRecycler.setAdapter(new GuideAdapter(guides)); // Replace the dummy data
                recentlyViewedGuidesRecycler.setAdapter(new GuideAdapter(guides));
                popularGuidesRecycler.setAdapter(new GuideAdapter(guides));
            } else {
                // Handle the error appropriately
            }
        });
    }

    // Optionally, you can include a welcome message or greeting based on the user's name
    private void greetUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userName = user.getDisplayName();

        }
    }
}
