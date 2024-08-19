package com.daniel.gameguru.Activities;

import static com.daniel.gameguru.Utilities.Utilities.hideSoftKeyboard;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.gameguru.Adapters.GuideAdapter;
import com.daniel.gameguru.Entities.Game;
import com.daniel.gameguru.Entities.Guide;
import com.daniel.gameguru.Adapters.GameAdapter;
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
    private List<Guide> guideResults;
    private GuideAdapter guideAdapter;
    private List<Game> gameResults;
    private GameAdapter gameAdapter;
    private BottomNavigationView bottomNavigationView;

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
        guideResults = new ArrayList<>();
        gameResults = new ArrayList<>();

        guideAdapter = new GuideAdapter(guideResults);
        gameAdapter = new GameAdapter(gameResults);

        searchResultsRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecycler.setAdapter(gameAdapter);

        NavigationBarManager.getInstance().setupBottomNavigationView(bottomNavigationView, this);
        NavigationBarManager.getInstance().setNavigation(bottomNavigationView, this, R.id.navigation_search);
    }

    private void setupSearch() {
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = v.getText().toString();
                searchGuides(query);
                searchGames(query);
                return true;
            }
            return false;
        });
    }

    private void searchGuides(@NonNull String query) {
        guideResults.clear();
        guideAdapter.notifyDataSetChanged(); // Notify that the dataset is cleared

        if (!query.isEmpty()) {
            DbManager.searchGuides(query, results -> {
                if (results != null && !results.isEmpty()) {
                    guideResults.addAll(results);
                    guideAdapter.notifyItemRangeInserted(0, guideResults.size());
                }
            });
        }
    }

    private void searchGames(@NonNull String query) {
        gameResults.clear();
        gameAdapter.notifyDataSetChanged(); // Notify that the dataset is cleared

        if (!query.isEmpty()) {
            DbManager.searchGames(query, results -> {
                if (results != null && !results.isEmpty()) {
                    gameResults.addAll(results);
                    gameAdapter.notifyItemRangeInserted(0, gameResults.size());
                }
            });
        }
    }

    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideSoftKeyboard(Activity_Search.this);
                v.clearFocus();
                v.performClick();
                return false;
            });
        }
    }
}
