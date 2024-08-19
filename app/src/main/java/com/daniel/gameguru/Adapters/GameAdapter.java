package com.daniel.gameguru.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daniel.gameguru.Activities.Activity_Game;
import com.daniel.gameguru.Entities.Game;
import com.daniel.gameguru.R;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private final List<Game> gameList;



    public GameAdapter(List<Game> gameList) {
        this.gameList = gameList;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gameList.get(position);
        holder.gameTitle.setText(game.getTitle());
        holder.gameGenre.setText(game.getGenres().toString());
        Glide.with(holder.itemView.getContext())
                .load(game.getImageUrl())
                .placeholder(R.drawable.img_white) // placeholder image
                .into(holder.gameThumbnail);


        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), Activity_Game.class);
            i.putExtra("gameId", game.getId());
            v.getContext().startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {

        private ImageView gameThumbnail;
        private TextView gameTitle;
        private TextView gameGenre;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameThumbnail = itemView.findViewById(R.id.game_thumbnail);
            gameTitle = itemView.findViewById(R.id.game_title);
            gameGenre = itemView.findViewById(R.id.game_genre);
        }


    }
}
