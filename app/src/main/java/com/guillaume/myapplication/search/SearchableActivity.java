package com.guillaume.myapplication.search;

import static pub.devrel.easypermissions.RationaleDialogFragment.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.SearchManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.guillaume.myapplication.R;
import com.guillaume.myapplication.di.Injection;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.viewModel.LocationViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchableActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private PlacesClient placesClient;
    private ListView list;
    private ListViewAdapter adapter;
    private SearchView editsearch;
    private List<Restaurant> restaurantsSuggested = new ArrayList<Restaurant>();
    private LocationViewModel locationViewModel;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        //Places.initialize(this, getResources().getString(R.string.API_KEY));
        //placesClient = Places.createClient(this);
        locationViewModel = Injection.provideLocationViewModel(this);
        recoveRestaurantsList();
        list = (ListView) findViewById(R.id.search_list_view);
        handleIntent();
        //db =

        adapter = new ListViewAdapter(this, restaurantsSuggested);
        list.setAdapter(adapter);

        editsearch = findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    private void handleIntent(){
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //doMySearch(query);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //doMySearch(newText);
        String text = newText;
        //todo add the results to the list after filtered
        adapter.filter(text);
        return false;
    }

    private void recoveRestaurantsList(){
        locationViewModel.currentRestaurantsDisplayedLiveData.observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                restaurantsSuggested = restaurants;
            }
        });
    }


}