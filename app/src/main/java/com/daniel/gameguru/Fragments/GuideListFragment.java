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

import java.util.ArrayList;
import java.util.List;



public class GuideListFragment extends Fragment {

    private RecyclerView guideRecyclerView;
    private GuideAdapter guideAdapter;
    private List<Guide> guideList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_guide_list, container, false);

        // Initialize the RecyclerView
        guideRecyclerView = view.findViewById(R.id.guideRecyclerView);
        guideRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize the guide list and adapter
        guideList = new ArrayList<>();
        guideAdapter = new GuideAdapter(guideList);

        // Set the adapter to the RecyclerView
        guideRecyclerView.setAdapter(guideAdapter);



        return view;
    }
}