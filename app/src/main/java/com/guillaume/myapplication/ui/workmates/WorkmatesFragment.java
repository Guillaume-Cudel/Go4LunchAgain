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
import com.guillaume.myapplication.model.firestore.UserFirebase;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragment extends Fragment {

    private RecyclerView recyclerView;
    @NonNull
    private final ArrayList<UserFirebase> workmatesList = new ArrayList<>();
    private WorkmatesAdapter adapter = new WorkmatesAdapter(workmatesList, this.getActivity());

    public WorkmatesFragment(){}

    public static WorkmatesFragment newInstance() {
        return (new WorkmatesFragment());
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_workmates_list);

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
        adapter.updateData(workmatesList);
    }


}