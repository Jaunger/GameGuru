package com.daniel.gameguru.Activities;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daniel.gameguru.Entities.Game;
import com.daniel.gameguru.Entities.Guide;
import com.daniel.gameguru.GuideAdapter;
import com.daniel.gameguru.R;
import com.daniel.gameguru.Utilities.NavigationBarManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Activity_Game extends AppCompatActivity {

    private AppCompatImageView gameBannerImage;
    private AppCompatTextView gameTitle, gameDetails, gameDescription;
    private LinearLayoutCompat genresContainer;
    private RecyclerView guidesRecycler;
    private BottomNavigationView bottomNavigationView;

    private GuideAdapter guideAdapter;
    private List<Guide> relatedGuides;

    private FirebaseFirestore db;
    private String gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        db = FirebaseFirestore.getInstance();
        gameId = getIntent().getStringExtra("gameId");

        findViews();
        NavigationBarManager.getInstance().setupBottomNavigationView(bottomNavigationView, this);
        NavigationBarManager.getInstance().setNavigation(bottomNavigationView, this, R.id.navigation_account);
        loadGameData(gameId);
    }

    private void findViews() {
        gameBannerImage = findViewById(R.id.gameBannerImage);
        gameTitle = findViewById(R.id.gameTitle);
        gameDetails = findViewById(R.id.gameDetails);
        gameDescription = findViewById(R.id.gameDescription);
        genresContainer = findViewById(R.id.genresContainer);
        guidesRecycler = findViewById(R.id.relatedGuidesRecyclerView);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        relatedGuides = new ArrayList<>();
        guideAdapter = new GuideAdapter(relatedGuides);
        guidesRecycler.setAdapter(guideAdapter);
    }

    private void loadGameData(String gameId) {
        db.collection("games").document(gameId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Game game = documentSnapshot.toObject(Game.class);
                    if (game != null) {
                        gameTitle.setText(game.getTitle());
                        gameDetails.setText(String.format("%s, %s, Released %s",
                                game.getDeveloper(), game.getPublisher(), game.getReleaseDate()));
                        gameDescription.setText(game.getDescription());

                        Glide.with(this)
                                .load(game.getImageUrl())
                                .placeholder(R.drawable.img_white)
                                .into(gameBannerImage);

                        populateGenres(game.getGenres());
                        loadRelatedGuides(game.getGuideIds());
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void populateGenres(List<String> genres) {
        genresContainer.removeAllViews();
        for (String genre : genres) {
            MaterialButton genreButton = new MaterialButton(this, null);
            genreButton.setText(genre);
            genreButton.setTextColor(ContextCompat.getColor(this,R.color.primaryColor));
            genreButton.setBackgroundTintList(null);  // Makes the button look like a text button
            genreButton.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            genresContainer.addView(genreButton);
        }
    }

    private void loadRelatedGuides(List<String> guideIds) {
        for (String guideId : guideIds) {
            db.collection("guides").document(guideId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Guide guide = documentSnapshot.toObject(Guide.class);
                        if (guide != null) {
                            relatedGuides.add(guide);
                            guideAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
        }
    }
}
