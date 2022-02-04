package com.guillaume.myapplication.ui.restaurants_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.guillaume.myapplication.R;
import com.guillaume.myapplication.ui.restaurant_profil.RestaurantProfilActivity;
import com.guillaume.myapplication.model.Restaurant;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.List;

public class RestaurantsListAdapter extends RecyclerView.Adapter<RestaurantsListAdapter.RestaurantsListViewHolder> {

    private List<Restaurant> dataList;
    private final Context context;
    private String photoData, photoWidth, rating, restaurantLatitude, restaurantLongitude;
    private LatLng mlatlng;
    private Restaurant restaurant;


    public RestaurantsListAdapter(final List<Restaurant> dataList, LatLng latlng, Context context) {
        this.dataList = dataList;
        this.mlatlng = latlng;
        this.context = context;
    }

    @NonNull
    @Override
    public RestaurantsListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, viewGroup, false);
        return new RestaurantsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantsListViewHolder holder, int position) {

        restaurant = dataList.get(holder.getAdapterPosition());
        String placeID = restaurant.getPlace_id();
        rating = restaurant.getRating();
        restaurantLatitude = restaurant.getGeometry().getLocation().getLat();
        restaurantLongitude = restaurant.getGeometry().getLocation().getLng();

        displayRestaurantName(restaurant.getName(), holder.nameField);

        if (restaurant.getVicinity() != null) {
            displayRestaurantVicinity(restaurant.getVicinity(), holder.addressField);
        }

        if (restaurant.getType() != null) {
            holder.kindField.setText(restaurant.getType());
        }

        if (restaurant.getOpening_hours() != null) {
            if (restaurant.getOpening_hours().getOpenNow().equals("true")) {
                String open = "Open";
                holder.informationField.setText(open);
                holder.informationField.setTypeface(null, Typeface.BOLD);
                holder.informationField.setTextColor(Color.GREEN);
            } else {
                String close = "Close";
                holder.informationField.setText(close);
                holder.informationField.setTypeface(null, Typeface.BOLD);
                holder.informationField.setTextColor(Color.RED);
            }
            if (restaurant.getDetails() != null) {
                String text = displayOpeningTime(restaurant);
                displayRestaurantText(text, holder.openingTime);
            }
        }

        if (restaurant.getRating() != null) {
            holder.displayStarsRating();
        }else{
            holder.noDisplayStars();
        }

        if(restaurant.getParticipantsNumber() > 0){
            int participants = restaurant.getParticipantsNumber();
            String intConvert = String.valueOf(participants);
            String participantsText = "(" + intConvert + ")";
            holder.participantsField.setText(participantsText);
        }else{
            holder.participantsIcon.setVisibility(View.INVISIBLE);
            holder.participantsField.setVisibility(View.INVISIBLE);
        }
        

        String distance = getDistanceInMeters() + " m";
        holder.distanceField.setText(distance);


        if(restaurant.getPhotoReference() != null){
            photoData = restaurant.getPhotoReference();
            photoWidth = restaurant.getPhotoWidth();

            Glide.with(context).load(parseDataPhotoToImage())
                    .into(holder.imageField);
        }else{
            Glide.with(context).load(R.drawable.gardenrestaurant).into(holder.imageField);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, RestaurantProfilActivity.class);
                i.putExtra("place_id", placeID);
                i.putExtra("name", restaurant.getName());
                if(restaurant.getPhotoReference() != null){
                    i.putExtra("photo", photoData);
                    i.putExtra("photoWidth", photoWidth);
                }
                i.putExtra("vicinity", restaurant.getVicinity());
                i.putExtra("type", restaurant.getType());
                i.putExtra("rate", rating);
                ((Activity) context).startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        if (dataList.size() == 0) {
            return 0;
        }else
        return dataList.size();
    }

    public void updateData(List<Restaurant> restaurants) {
        this.dataList = restaurants;
        this.notifyDataSetChanged();
    }

    public void updateLocation(LatLng latLng) {
        this.mlatlng = latLng;
        this.notifyDataSetChanged();
    }


    class RestaurantsListViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        private final ImageView imageField;
        private final TextView nameField;
        private final TextView kindField;
        private final TextView addressField;
        private final TextView informationField;
        private final TextView openingTime;
        private final TextView distanceField;
        private final ImageView participantsIcon;
        private final TextView participantsField;
        private final ImageView noteField1;
        private final ImageView noteField2;
        private final ImageView noteField3;

        RestaurantsListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            imageField = mView.findViewById(R.id.list_view_image);
            nameField = mView.findViewById(R.id.list_view_name);
            kindField = mView.findViewById(R.id.list_view_kind_restaurant);
            addressField = mView.findViewById(R.id.list_view_address);
            informationField = mView.findViewById(R.id.list_view_informations);
            openingTime = mView.findViewById(R.id.list_view_opening_time);
            distanceField = mView.findViewById(R.id.list_view_distance);
            participantsIcon = mView.findViewById(R.id.list_view_workmates_image);
            participantsField = mView.findViewById(R.id.list_view_number_workmates);
            noteField1 = mView.findViewById(R.id.list_view_star_1);
            noteField2 = mView.findViewById(R.id.list_view_star_2);
            noteField3 = mView.findViewById(R.id.list_view_star_3);
        }

        private void displayStarsRating() {
            double dRating = Double.parseDouble(rating);
            if (dRating <= 1.67) {
                noteField3.setVisibility(View.INVISIBLE);
                noteField2.setVisibility(View.INVISIBLE);
            }
            if (dRating > 1.67 && dRating < 3.4) {
                noteField3.setVisibility(View.INVISIBLE);
            }
        }

        private void noDisplayStars(){
            noteField1.setVisibility(View.INVISIBLE);
            noteField2.setVisibility(View.INVISIBLE);
            noteField3.setVisibility(View.INVISIBLE);
        }
    }



    private String parseDataPhotoToImage() {

            String API_KEY = "&key=AIzaSyCfIzqvYUkoerRn0a3nnUsLcpooxnZElxI";
            String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/photo?";
            String MAX_WIDTH = "maxwidth=";
            String PHOTOREFERENCE = "&photoreference=";
            return new StringBuilder().append(PLACES_API_BASE).append(MAX_WIDTH).append(photoWidth)
                    .append(PHOTOREFERENCE).append(photoData).append(API_KEY).toString();

    }

    private String getDistanceInMeters() {

        // get current location
        double currentLatitude = mlatlng.latitude;
        double currentLongitude = mlatlng.longitude;

        double rLatitude = Double.parseDouble(String.valueOf(restaurantLatitude));
        double rLongitude = Double.parseDouble(String.valueOf(restaurantLongitude));

        Location loc1 = new Location("");
        loc1.setLatitude(currentLatitude);
        loc1.setLongitude(currentLongitude);

        Location loc2 = new Location("");
        loc2.setLatitude(rLatitude);
        loc2.setLongitude(rLongitude);

        float distanceInMeters = loc1.distanceTo(loc2);
        int distanceRound = (int) distanceInMeters;
        String distance = String.valueOf(distanceRound);

        return distance;
    }


    private String displayOpeningTime(Restaurant restaurant) {
        List<String> weekdayText = restaurant.getDetails().getOpening_hours().getWeekday_text();
        String dayOpening = null;

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY:
                dayOpening = weekdayText.get(0);
                break;
            case Calendar.TUESDAY:
                dayOpening = weekdayText.get(1);
                break;
            case Calendar.WEDNESDAY:
                dayOpening = weekdayText.get(2);
                break;
            case Calendar.THURSDAY:
                dayOpening = weekdayText.get(3);
                break;
            case Calendar.FRIDAY:
                dayOpening = weekdayText.get(4);
                break;
            case Calendar.SATURDAY:
                dayOpening = weekdayText.get(5);
                break;
            case Calendar.SUNDAY:
                dayOpening = weekdayText.get(6);
                break;
        }

        return dayOpening;
    }

    private int countCharacter(String word) {
        String string = word;
        int count = 0;

        //Counts each character except space
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != ' ')
                count++;
        }
        return count;
    }

    private void displayRestaurantText(String text, TextView textView) {
        int numberOfCharacter = countCharacter(text);
        if (numberOfCharacter > 30 && numberOfCharacter <= 35) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
            textView.setText(text);
        }
        if (numberOfCharacter > 35 && numberOfCharacter <= 40) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            textView.setText(text);
        }
        if (numberOfCharacter > 40) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);
            textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
            textView.setText(text);
        } else
            textView.setText(text);
    }

    private void displayRestaurantName(String text, TextView textView) {
        int numberOfCharacter = countCharacter(text);
        if (numberOfCharacter > 15 && numberOfCharacter <= 25) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            textView.setText(text);
        }
        if (numberOfCharacter > 25 && numberOfCharacter <= 30) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setText(text);
        }
        if (numberOfCharacter > 30) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(36)});
            textView.setText(text);
        }else
            textView.setText(text);
    }

    private void displayRestaurantVicinity(String text, TextView textView) {
        int numberOfCharacter = countCharacter(text);
        if (numberOfCharacter > 30 && numberOfCharacter <= 35) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
            textView.setText(text);
        }
        if (numberOfCharacter > 35 && numberOfCharacter <= 40) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            textView.setText(text);
        }
        if (numberOfCharacter > 40) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);
            textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(45)});
            textView.setText(text);
        } else
            textView.setText(text);
    }

}

