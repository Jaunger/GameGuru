package com.daniel.gameguru.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daniel.gameguru.Activities.Activity_Profile;
import com.daniel.gameguru.Entities.User;
import com.daniel.gameguru.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> followingList;
    private final Context context;

    public UserAdapter(Context context, List<User> followingList) {
        this.context = context;
        this.followingList = followingList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = followingList.get(position);
        holder.userName.setText(user.getName());
        holder.userDescription.setText(user.getDescription());

        Glide.with(context)
                .load(user.getImage())
                .placeholder(R.drawable.img_white)
                .into(holder.userImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Activity_Profile.class);
            intent.putExtra("authorId", user.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return followingList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private final ImageView userImage;
        private final TextView userName;
        private final TextView userDescription;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_profile_image);
            userName = itemView.findViewById(R.id.user_name);
            userDescription = itemView.findViewById(R.id.user_description);
        }
    }
}
