package com.daniel.gameguru.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.gameguru.Entities.Guide;
import com.daniel.gameguru.Adapters.GuideAdapter;
import com.daniel.gameguru.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;



public class GuideListFragment extends Fragment {

    private RecyclerView guideRecyclerView;
    private GuideAdapter guideAdapter;
    private List<Guide> guideList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String authorId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_guide_list, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize the RecyclerView
        guideRecyclerView = view.findViewById(R.id.guideRecyclerView);
        guideRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the guide list and adapter
        guideList = new ArrayList<>();
        guideAdapter = new GuideAdapter(guideList);

        // Set the adapter to the RecyclerView
        guideRecyclerView.setAdapter(guideAdapter);
        loadGuidesFromFireStore();


        return view;
    }


    private void loadGuidesFromFireStore() {
        db.collection("guides").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Guide> guides = new ArrayList<>();
                task.getResult().forEach(document -> {
                    Guide guide = document.toObject(Guide.class);
                    authorId = authorId == null ? mAuth.getCurrentUser().getUid() : authorId;
                    if(guide.getAuthorId().equals(authorId))
                        guides.add(guide);
                });
                guideRecyclerView.setAdapter(new GuideAdapter(guides)); // Replace the dummy data

            } else {
                // Log the error
                Log.e("FireStoreError", "Error getting documents: ", task.getException());
                // Show an appropriate message to the user
                Toast.makeText(getContext(), "Failed to load guides. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public GuideListFragment setAuthorId(String authorId) {
        this.authorId = authorId;
        return this;
    }

}