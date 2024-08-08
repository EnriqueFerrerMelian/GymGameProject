package com.example.gymgameproject.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gymgameproject.MainMenu;
import com.example.gymgameproject.R;
import com.example.gymgameproject.classes.Activity;
import com.example.gymgameproject.classes.AppHelper;
import com.example.gymgameproject.databinding.FragmentActivitiesBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ActivitiesFragment extends Fragment implements ActivitiesFbAdapter.ItemClickListener{
    //recyclerView **********
    private ActivitiesFbAdapter activitiesFbAdapter;//adaptador
    private RecyclerView recyclerView;
    //recyclerView *******fin

    //variables globales
    private FragmentActivitiesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActivitiesBinding.inflate(inflater, container, false);
        AppHelper.cambiarToolbarText("Actividades");
        ((MainMenu) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FirebaseRecyclerOptions<Activity> options =
                new FirebaseRecyclerOptions.Builder<Activity>()
                        .setQuery(FirebaseDatabase.getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                                .getReference("actividades"), Activity.class)
                        .build();
        activitiesFbAdapter = new ActivitiesFbAdapter(options, this::onItemClick);
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(activitiesFbAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        activitiesFbAdapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        activitiesFbAdapter.stopListening();
    }
    /**
     * Este método se pasará a rutinaFbAdapter. De manera que cuando se haga click en una rutina se ejecute el
     * fragmento DetallesRutinaFragmento
     * @param actividad
     */
    @Override
    public void onItemClick(Activity actividad) {
        Fragment fragment = ActivitiesDetailsFragment.newInstance(actividad);
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment, "nota").addToBackStack(null);
        fragmentTransaction.commit();
    }
}