package com.guillaume.myapplication.ui.restaurants_list;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guillaume.myapplication.R;
import com.guillaume.myapplication.di.Injection;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.ui.restaurant_profil.RestaurantProfilActivity;
import com.guillaume.myapplication.viewModel.FirestoreRestaurantViewModel;
import com.guillaume.myapplication.viewModel.FirestoreUserViewModel;
import com.guillaume.myapplication.viewModel.UtilsViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RestaurantsListFragment extends Fragment implements RestaurantListClickInterface {



    private RecyclerView recyclerView;
    @NonNull
    private final ArrayList<Restaurant> restaurantsList = new ArrayList<>();
    private ArrayList<Restaurant> filteredRestaurantsList = new ArrayList<>();
    private LatLng mLatlng;
    private RestaurantsListAdapter adapter = new RestaurantsListAdapter(restaurantsList, mLatlng, this.getActivity(), this);
    private FirestoreRestaurantViewModel mFirestoreRestaurantVM;
    private FirestoreUserViewModel mFirestoreUserVM;
    private UtilsViewModel utilsVM;
    private ProgressDialog loading;
    private FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();


    public RestaurantsListFragment( ) {
    }

    public static RestaurantsListFragment newInstance() {
        return (new RestaurantsListFragment());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loading = ProgressDialog.show(getActivity(), "", getString(R.string.messageRecovingRestaurants), true);
        mFirestoreRestaurantVM = Injection.provideFirestoreRestaurantViewModel(requireActivity());
        mFirestoreUserVM = Injection.provideFirestoreUserViewModel(requireActivity());
        utilsVM = Injection.provideUtilsViewModel(requireActivity());

        recoveLocation();
        configureRecyclerView();

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurants_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_restaurant_list);

        return view;
    }

    private void recoveLocation(){
        utilsVM.locationLiveData.observe(requireActivity(), new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                mLatlng = latLng;
                updateLocation(mLatlng);
                recoveRadius();
            }
        });
    }

    private void recoveRadius(){
        mFirestoreUserVM.getUser(authUser.getUid()).observe(Objects.requireNonNull(getActivity()), new Observer<UserFirebase>() {
            @Override
            public void onChanged(UserFirebase userFirebase) {
                String mRadius = userFirebase.getCurrentRadius();
                recoveAllRestaurants(mRadius);
            }
        });
    }

    private void recoveAllRestaurants(String radius){
        mFirestoreRestaurantVM.getAllRestaurants().observe(Objects.requireNonNull(getActivity()), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                RestaurantsListFragment.this.restaurantsList.clear();
                RestaurantsListFragment.this.restaurantsList.addAll(restaurants);
                filterRestaurantsWithinReach(radius);
                loading.cancel();
            }
        });
    }

    private void filterRestaurantsWithinReach(String sRadius){
        filteredRestaurantsList.clear();
        int radius = Integer.parseInt(sRadius);
        if(restaurantsList.size() > 0){
            for(Restaurant r : restaurantsList){
                LatLng restaurantLocation = recoveLatLng(r);
                int distance = displayRestaurantsWithinRange(restaurantLocation);
                if(distance < radius){
                    filteredRestaurantsList.add(r);
                }
            }

        }
        updateRestaurants(filteredRestaurantsList);
    }

    private int displayRestaurantsWithinRange(LatLng restaurantLocation) {
        double currentLatitude = mLatlng.latitude;
        double currentLongitude = mLatlng.longitude;

        Location loc1 = new Location("");
        loc1.setLatitude(currentLatitude);
        loc1.setLongitude(currentLongitude);

        Location loc2 = new Location("");
        loc2.setLatitude(restaurantLocation.latitude);
        loc2.setLongitude(restaurantLocation.longitude);

        float distanceInMeters = loc1.distanceTo(loc2);

        return (int) distanceInMeters;
    }

    private LatLng recoveLatLng(Restaurant r) {
        String restaurantLatitude = r.getGeometry().getLocation().getLat();
        String restaurantLongitude = r.getGeometry().getLocation().getLng();
        double rLatitude = Double.parseDouble(restaurantLatitude);
        double rLongitude = Double.parseDouble(restaurantLongitude);

        return new LatLng(rLatitude, rLongitude);
    }

    private void configureRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        this.adapter = new RestaurantsListAdapter(restaurantsList, mLatlng, this.getActivity(), this);
        recyclerView.setAdapter(adapter);
    }


    private void updateRestaurants(List<Restaurant> filtered){
        adapter.updateData(filtered);
    }

    private void updateLocation(LatLng location){
        adapter.updateLocation(location);
    }


    @Override
    public void onItemClick(int position) {

        Restaurant restaurant = filteredRestaurantsList.get(position);

        Intent i = new Intent(getActivity(), RestaurantProfilActivity.class);
        i.putExtra("place_id", restaurant.getPlace_id());
        i.putExtra("name", restaurant.getName());
        if(restaurant.getPhotoReference() != null){
            i.putExtra("photo", restaurant.getPhotoReference());
            i.putExtra("photoWidth", restaurant.getPhotoWidth());
        }
        i.putExtra("vicinity", restaurant.getVicinity());
        i.putExtra("type", restaurant.getType());
        if(restaurant.getRating() != null){
            i.putExtra("rate", restaurant.getRating());
        }

        getActivity().startActivity(i);
    }
}