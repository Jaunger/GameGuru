package com.daniel.gameguru.Activities;

import static com.daniel.gameguru.Utilities.Utilities.hideSoftKeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.gameguru.Entities.Guide;
import com.daniel.gameguru.GuideAdapter;
import com.daniel.gameguru.R;
import com.daniel.gameguru.Utilities.DbManager;
import com.daniel.gameguru.Utilities.NavigationBarManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class Activity_Search extends AppCompatActivity {

    private TextInputEditText searchInput;
    private RecyclerView searchResultsRecycler;
    private GuideAdapter searchAdapter;
    private BottomNavigationView bottomNavigationView;
    private List<Guide> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        overridePendingTransition(R.anim.dark_screen, R.anim.light_screen);

        findViews();
        initView();
        setupUI(findViewById(R.id.searchParent));
        setupSearch();
    }

    private void findViews() {
        searchInput = findViewById(R.id.search_input);
        searchResultsRecycler = findViewById(R.id.search_results_recycler);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void initView() {
        searchResults = new ArrayList<>();
        searchAdapter = new GuideAdapter(searchResults);

        // Setting up RecyclerView with the adapter
        searchResultsRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecycler.setAdapter(searchAdapter);

        // Setting up bottom navigation
        NavigationBarManager.getInstance().setupBottomNavigationView(bottomNavigationView, this);
        NavigationBarManager.getInstance().setNavigation(bottomNavigationView, this, R.id.navigation_search);
    }

    private void setupSearch() {
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchGuides(v.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void searchGuides(String query) {
        searchResults.clear();
        if (!query.isEmpty()) {
            DbManager.searchGuides(query, results -> {
                searchResults.addAll(results);
                searchAdapter.notifyDataSetChanged();
            });
        } else {
            searchAdapter.notifyDataSetChanged();
        }
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Activity_Search.this);
                    v.clearFocus();
                    return false;
                }
            });
        }

    }

}
