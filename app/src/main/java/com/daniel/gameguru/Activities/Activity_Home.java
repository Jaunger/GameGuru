package com.daniel.gameguru.Activities;

import static com.daniel.gameguru.Utilities.Utilities.hideKeyboard;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.gameguru.Entities.Guide;
import com.daniel.gameguru.GuideAdapter;
import com.daniel.gameguru.R;
import com.daniel.gameguru.Utilities.NavigationBarManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Activity_Home extends AppCompatActivity {

    private RecyclerView featuredGuidesRecycler;
    private RecyclerView recentlyViewedGuidesRecycler;
    private RecyclerView popularGuidesRecycler;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        overridePendingTransition(R.anim.dark_screen, R.anim.light_screen);

        db = FirebaseFirestore.getInstance();

        setupUI(findViewById(R.id.homeParent));
        findViews();
        initView();
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
        popularGuidesRecycler = findViewById(R.id.popular_guides_recycler);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void initView() {
        NavigationBarManager.getInstance().setupBottomNavigationView(bottomNavigationView, this);
        NavigationBarManager.getInstance().setNavigation(bottomNavigationView, this, R.id.navigation_home);

        featuredGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recentlyViewedGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popularGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));



        loadGuidesFromFireStore();
    }



    private void loadGuidesFromFireStore() {
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
                Log.e("FireStoreError", "Error getting documents: ", task.getException());
                Toast.makeText(Activity_Home.this, "Failed to load guides. Please try again later.", Toast.LENGTH_SHORT).show();            }
        });
    }


}
