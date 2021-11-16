package com.guillaume.myapplication.ui.workmates;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.guillaume.myapplication.NavigationActivity;
import com.guillaume.myapplication.R;
import com.guillaume.myapplication.di.Injection;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.ui.restaurant_profil.RestaurantProfilActivity;
import com.guillaume.myapplication.viewModel.FirestoreUserViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmatesViewHolder> {

    private List<UserFirebase> workmatesList;
    private List<Restaurant> workmatesRestaurantList;
    private final Context context;

    public WorkmatesAdapter(final List<UserFirebase> workmatesList, Context context) {
        this.workmatesList = workmatesList;
        this.context = context;

    }

    @NotNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workmate, parent, false);
        return new WorkmatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull WorkmatesViewHolder holder, int position) {

        UserFirebase user = workmatesList.get(holder.getAdapterPosition());

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


        String restaurantName = user.getRestaurantName();
        if(restaurantName != null){

            if (restaurantName.length() > 20){
                restaurantName = restaurantName.substring(0, 20);
            }
            String workmateEating = username + " is eating.";
            String restaurantText = "(" + restaurantName + ")";
            holder.usernameText.setText(workmateEating);
            holder.restaurantText.setText(restaurantText);
        }else {
            String restaurantLess = username + " hasn't decided yet.";
            holder.usernameText.setText(restaurantLess);
            holder.usernameText.setTextColor(Color.GRAY);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.getRestaurantChoosed(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (workmatesList.size() == 0){
            return 0;
        }else
        return workmatesList.size();
    }

    public void updateWorkmateList(List<UserFirebase> workmates){
        this.workmatesList = workmates;
        this.notifyDataSetChanged();
    }

    public void updateWorkmateRestaurantList(List<Restaurant> restaurants){
        this.workmatesRestaurantList = restaurants;
        this.notifyDataSetChanged();
    }

    class WorkmatesViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        private final TextView usernameText;
        private final TextView restaurantText;
        private final ImageView userPhoto;



        public WorkmatesViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mView = itemView;

            usernameText = mView.findViewById(R.id.workmate_text_item);
            restaurantText = mView.findViewById(R.id.restaurant_text_item);
            userPhoto = mView.findViewById(R.id.workmate_image_item);
        }

        private void getRestaurantChoosed(UserFirebase user){
            if(user.getRestaurantChoosed() != null && workmatesRestaurantList.size() > 0) {
                boolean isGood = false;
                Restaurant restaurant = new Restaurant();
                for (Restaurant r : workmatesRestaurantList) {
                    if (r.getPlace_id().equals(user.getRestaurantChoosed())) {
                        restaurant = r;
                        isGood = true;
                    }
                }
                if (isGood){
                    Intent i = new Intent(context, RestaurantProfilActivity.class);
                    i.putExtra("place_id", restaurant.getPlace_id());
                    i.putExtra("name", restaurant.getName());
                    i.putExtra("photo", restaurant.getPhotoReference());
                    i.putExtra("photoWidth", restaurant.getPhotoWidth());
                    i.putExtra("vicinity", restaurant.getVicinity());
                    i.putExtra("type", restaurant.getType());
                    i.putExtra("rate", restaurant.getRating());
                    context.startActivity(i);
                }
            }
        }



    }
}
