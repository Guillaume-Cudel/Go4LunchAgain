package com.guillaume.myapplication.ui.restaurants_list;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guillaume.myapplication.R;
import com.guillaume.myapplication.di.Injection;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.viewModel.FirestoreRestaurantViewModel;
import com.guillaume.myapplication.viewModel.FirestoreUserViewModel;
import com.guillaume.myapplication.viewModel.UtilsViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RestaurantsListFragment extends Fragment {



    private RecyclerView recyclerView;
    @NonNull
    private final ArrayList<Restaurant> restaurantsList = new ArrayList<>();
    private ArrayList<Restaurant> filteredRestaurantsList = new ArrayList<>();
    private LatLng mLatlng;
    private RestaurantsListAdapter adapter = new RestaurantsListAdapter(restaurantsList, mLatlng, this.getActivity());
    private FirestoreRestaurantViewModel mFirestoreRestaurantVM;
    private FirestoreUserViewModel mFirestoreUserVM;
    private ProgressDialog loading;
    private String mRadius;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser authUser = mAuth.getCurrentUser();


    public RestaurantsListFragment( ) {
    }

    public static RestaurantsListFragment newInstance() {
        return (new RestaurantsListFragment());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loading = ProgressDialog.show(getActivity(), "", getString(R.string.messageRecovingRestaurants), true);
        mFirestoreRestaurantVM = Injection.provideFirestoreRestaurantViewModel(getActivity());
        mFirestoreUserVM = Injection.provideFirestoreUserViewModel(getActivity());

        recoveLocation();
        recoveRadius();
        configureRecyclerView();

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurants_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_restaurant_list);

        return view;
    }

    private void recoveRadius(){
        mFirestoreUserVM.getUser(authUser.getUid()).observe(Objects.requireNonNull(getActivity()), new Observer<UserFirebase>() {
            @Override
            public void onChanged(UserFirebase userFirebase) {
                mRadius = userFirebase.getCurrentRadius();
                recoveAllRestaurants();
            }
        });
    }

    private void recoveLocation(){

        UtilsViewModel utilsViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(UtilsViewModel.class);
        utilsViewModel.locationLiveData.observe(requireActivity(), new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                mLatlng = latLng;
                updateLocation();
            }
        });
    }

    private void recoveAllRestaurants(){
        mFirestoreRestaurantVM.getAllRestaurants().observe(getActivity(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                RestaurantsListFragment.this.restaurantsList.clear();
                RestaurantsListFragment.this.restaurantsList.addAll(restaurants);
                filterRestaurantsWithinReach();
                updateRestaurants();
                loading.cancel();
            }
        });
    }

    private void filterRestaurantsWithinReach(){
        int radius = Integer.parseInt(mRadius);
        if(restaurantsList.size() > 0){
            for(Restaurant r : restaurantsList){
                LatLng restaurantLocation = recoveLatLng(r);
                int distance = displayRestaurantsWithinRange(restaurantLocation);
                if(distance < radius){
                    filteredRestaurantsList.add(r);
                }
            }

        }
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
        this.adapter = new RestaurantsListAdapter(restaurantsList, mLatlng, this.getActivity());
        recyclerView.setAdapter(adapter);
    }


    private void updateRestaurants(){
        adapter.updateData(filteredRestaurantsList);
    }

    private void updateLocation(){
        adapter.updateLocation(mLatlng);
    }


}