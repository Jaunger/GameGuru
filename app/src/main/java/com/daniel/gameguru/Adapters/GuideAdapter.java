package com.daniel.gameguru.Adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.daniel.gameguru.Activities.Activity_Guide;
import com.daniel.gameguru.Entities.Guide;
import com.daniel.gameguru.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class GuideAdapter extends RecyclerView.Adapter<GuideAdapter.GuideViewHolder> {

    private final List<Guide> guides;

    public GuideAdapter(List<Guide> guides) {
        this.guides = guides;
    }

    @NonNull
    @Override
    public GuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guide, parent, false);
        return new GuideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideViewHolder holder, int position) {
        Guide guide = guides.get(position);

        holder.guideTitle.setText(guide.getTitle());


        // Load the guide image using Glide
        Glide.with(holder.itemView.getContext())
                .load(guide.getImageUrl())
                .placeholder(R.drawable.img_white)
                .into(holder.guideImage);


        holder.openGuideButton.setOnClickListener(v -> {
            Log.d("GuideAdapter", "Opening guide: " + guide.getTitle());
            Intent i = new Intent(v.getContext(), Activity_Guide.class);
            i.putExtra("gameId", guide.getGameId());
            i.putExtra("guideId", guide.getId());
            i.putExtra("authorId", guide.getAuthorId());
            v.getContext().startActivity(i);


        });
    }

    @Override
    public int getItemCount() {
        return guides.size();
    }

    public static class GuideViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView guideTitle;
        ImageView guideImage;
        MaterialButton openGuideButton;

        public GuideViewHolder(@NonNull View itemView) {
            super(itemView);
            guideTitle = itemView.findViewById(R.id.guideTitle);
            guideImage = itemView.findViewById(R.id.guideImage);
            openGuideButton = itemView.findViewById(R.id.openGuideButton);
        }
    }
}
