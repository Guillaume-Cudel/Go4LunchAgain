package com.guillaume.myapplication.ui.restaurant_profil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.guillaume.myapplication.R;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.ui.workmates.WorkmatesAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RestaurantProfilAdapter extends RecyclerView.Adapter<RestaurantProfilAdapter.RestaurantProfilViewHolder> {

    private List<UserFirebase> participantsList;
    private final Context context;

    public RestaurantProfilAdapter(final List<UserFirebase> list, Context context){
        this.participantsList = list;
        this.context = context;
    }


    @NonNull
    @Override
    public RestaurantProfilAdapter.RestaurantProfilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workmate, parent, false);
        return new RestaurantProfilViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RestaurantProfilAdapter.RestaurantProfilViewHolder holder, int position) {

        UserFirebase user = participantsList.get(holder.getBindingAdapterPosition());

        String photo = user.getUrlPicture();
        if (photo != null){
            Glide.with(context).load(photo)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.userPhoto);
        }else{
            Glide.with(context).load(R.drawable.ic_user_profile)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.userPhoto);
        }

        String username = user.getUsername();
        String restaurantLess = username + context.getString(R.string.joining);
        holder.usernameText.setText(restaurantLess);
    }

    @Override
    public int getItemCount() {
        if (participantsList.size() == 0){
            return 0;
        }else
            return participantsList.size();
    }

    public void updateData(List<UserFirebase> participants){
        this.participantsList = participants;
        this.notifyDataSetChanged();
    }

    class RestaurantProfilViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        private final TextView usernameText;
        private final ImageView userPhoto;



        public RestaurantProfilViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mView = itemView;

            usernameText = mView.findViewById(R.id.workmate_text_item);
            userPhoto = mView.findViewById(R.id.workmate_image_item);
        }
    }
}
