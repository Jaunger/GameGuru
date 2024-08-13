package com.daniel.gameguru.Activities;

import static com.daniel.gameguru.Utilities.Utilities.hideSoftKeyboard;

import android.content.Intent;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class Activity_Home extends AppCompatActivity {

    private RecyclerView featuredGuidesRecycler;
    private RecyclerView recentlyViewedGuidesRecycler;
    private RecyclerView popularGuidesRecycler;
    private BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) { //TODO: maybe remove search bar and head an Hello, UserName
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
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
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // Set up the recycler views
        featuredGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        featuredGuidesRecycler.setAdapter(new GuideAdapter(new ArrayList<Guide>()));

        recentlyViewedGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recentlyViewedGuidesRecycler.setAdapter(new GuideAdapter(new ArrayList<Guide>()));

        popularGuidesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popularGuidesRecycler.setAdapter(new GuideAdapter(new ArrayList<Guide>()));

        featuredGuidesRecycler.setAdapter(new GuideAdapter(getDummyData()));
        recentlyViewedGuidesRecycler.setAdapter(new GuideAdapter(getDummyData()));
        popularGuidesRecycler.setAdapter(new GuideAdapter(getDummyData()));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int curItem = item.getItemId();
            if(curItem == R.id.navigation_home){
                return true;
            } else if (curItem == R.id.navigation_account) {
                startActivity(new Intent(Activity_Home.this, Activity_Profile.class));
                overridePendingTransition(R.anim.dark_screen, R.anim.light_screen);
                finish();
                return true;
            }
            return true;
        });
    }
    private List<Guide> getDummyData() {
        // Generate dummy data for example purposes
        List<Guide> guides = new ArrayList<>();
        guides.add(new Guide("Guide 1", "https://example.com/guide1.jpg"));
        guides.add(new Guide("Guide 2", "https://example.com/guide2.jpg"));
        guides.add(new Guide("Guide 3", "https://example.com/guide3.jpg"));
        return guides;
    }
}