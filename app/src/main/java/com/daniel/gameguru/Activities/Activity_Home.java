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
        // Set up touch listener for non-text box views to hide keyboard.
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
        // Setup bottom navigation
        NavigationBarManager.getInstance().setupBottomNavigationView(bottomNavigationView, this);
        NavigationBarManager.getInstance().setNavigation(bottomNavigationView, this, R.id.navigation_home);

        // Set up the recycler views with horizontal scrolling
        featuredGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recentlyViewedGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popularGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Set adapters with dummy data
        featuredGuidesRecycler.setAdapter(new GuideAdapter(getDummyData()));
        recentlyViewedGuidesRecycler.setAdapter(new GuideAdapter(getDummyData()));
        popularGuidesRecycler.setAdapter(new GuideAdapter(getDummyData()));

        // Optionally, you can remove the dummy data and replace it with real data fetched from Firestore or your backend.
        loadGuidesFromFirestore(); // Uncomment this to load real data from Firestore
    }

    private List<Guide> getDummyData() {
        // Generate dummy data for example purposes
        List<Guide> guides = new ArrayList<>();
        guides.add(new Guide("Guide 1", "https://example.com/guide1.jpg"));
        guides.add(new Guide("Guide 2", "https://example.com/guide2.jpg"));
        guides.add(new Guide("Guide 3", "https://example.com/guide3.jpg"));
        return guides;
    }

    private void loadGuidesFromFirestore() {
        // Example of loading real data from Firestore (this could be adjusted based on your Firestore structure)
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
            // Display a welcome message, e.g., "Hello, John!"
            // You can set this message in a TextView or similar UI element in your layout.
        }
    }
}
