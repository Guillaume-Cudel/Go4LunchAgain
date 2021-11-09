package com.guillaume.myapplication.ui.workmates;

import android.app.ProgressDialog;
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

import com.guillaume.myapplication.R;
import com.guillaume.myapplication.di.Injection;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.viewModel.FirestoreUserViewModel;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragment extends Fragment {

    private RecyclerView recyclerView;
    @NonNull
    private final ArrayList<UserFirebase> workmatesList = new ArrayList<>();
    private ArrayList<Restaurant> workmatesRestaurantsList = new ArrayList<>();
    private WorkmatesAdapter adapter = new WorkmatesAdapter(workmatesList, this.getActivity());
    private FirestoreUserViewModel mFirestoreUserVM;

    public WorkmatesFragment(){}

    public static WorkmatesFragment newInstance() {
        return (new WorkmatesFragment());
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_workmates_list);
        mFirestoreUserVM = Injection.provideFirestoreUserViewModel(requireActivity());

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProgressDialog loading = ProgressDialog.show(getActivity(), "", getString(R.string.messageRecoveWorkmates), true);

        Injection.provideFirestoreUserViewModel(getActivity()).getWorkmatesList().observe(this.getActivity(), new Observer<List<UserFirebase>>() {
            @Override
            public void onChanged(List<UserFirebase> users) {
                WorkmatesFragment.this.workmatesList.clear();
                WorkmatesFragment.this.workmatesList.addAll(users);
                updateWorkmates();
                setRestaurantChoosedList();
                loading.cancel();
            }
        });
        configureRecyclerView();
    }


    private void configureRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WorkmatesAdapter(workmatesList, getActivity());
        recyclerView.setAdapter(adapter);
    }

    private void updateWorkmates(){
        adapter.updateWorkmateList(workmatesList);
    }

    private void updateWormatesRestaurantList(){
        adapter.updateWorkmateRestaurantList(workmatesRestaurantsList);
    }

    // todo boucle method to get all restaurants for any users and add them to a list

    private void setRestaurantChoosedList(){
        if (workmatesList.size() > 0){
            for (UserFirebase user : workmatesList){
                if(user.getRestaurantChoosed() != null) {
                    mFirestoreUserVM.getRestaurant(user.getUid(), user.getRestaurantChoosed()).observe(requireActivity(), new Observer<Restaurant>() {
                        @Override
                        public void onChanged(Restaurant restaurant) {
                            workmatesRestaurantsList.add(restaurant);
                        }
                    });
                }
            }
            updateWormatesRestaurantList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setRestaurantChoosedList();
    }
}