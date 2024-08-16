package com.daniel.gameguru.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daniel.gameguru.Entities.Guide;
import com.daniel.gameguru.GuideAdapter;
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
        loadGuidesFromFirestore();


        return view;
    }


    private void loadGuidesFromFirestore() {
        db.collection("guides").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Guide> guides = new ArrayList<>();
                task.getResult().forEach(document -> {
                    Guide guide = document.toObject(Guide.class);
                    if(guide.getAuthorId().equals(mAuth.getCurrentUser().getUid()))
                        guides.add(guide);
                });
                guideRecyclerView.setAdapter(new GuideAdapter(guides)); // Replace the dummy data

            } else {
                // Handle the error appropriately
            }
        });
    }
}