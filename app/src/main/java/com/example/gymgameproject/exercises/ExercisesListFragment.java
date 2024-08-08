package com.example.gymgameproject.exercises;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.gymgameproject.MainMenu;
import com.example.gymgameproject.R;
import com.example.gymgameproject.classes.Exercise;
import com.example.gymgameproject.databinding.FragmentExercisesListBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ExercisesListFragment extends Fragment implements ExerciseFbAdapter.ItemClickListener {
    private ExerciseFbAdapter ejercicioFbAdapter;
    private RecyclerView recyclerView;
    static FragmentExercisesListBinding binding;
    private static String valorSpinner = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExercisesListBinding.inflate(inflater, container, false);
        ((MainMenu) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //cargo la lista
        cargarRecycler();
        instanciarSpinner();

        binding.categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    cargarRecycler();
                }
                if(i==1){
                    valorSpinner = "Mancuernas";
                    cargarDesdeSpiner(valorSpinner);
                }
                if(i==2){
                    valorSpinner = "Kettlebells";
                    cargarDesdeSpiner(valorSpinner);
                }
                if(i==3){
                    valorSpinner = "Banco de pesas";
                    cargarDesdeSpiner(valorSpinner);
                }
                if(i==4){
                    valorSpinner = "Barra";
                    cargarDesdeSpiner(valorSpinner);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Este método configura el spinner
     */
    public void instanciarSpinner(){
        List<String> listaCategorias = new ArrayList<>();
        listaCategorias.add("Todos");
        listaCategorias.add("Mancuernas");
        listaCategorias.add("Kettlebells");
        listaCategorias.add("Banco de pesas");
        listaCategorias.add("Barra");
        ArrayAdapter<String> spinnerAdapter= new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listaCategorias);
        spinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        binding.categoria.setAdapter(spinnerAdapter);
    }

    /**
     * Este método carga el recyclerView con todos los ejercicios de la base de datos
     */
    public void cargarRecycler(){
        FirebaseRecyclerOptions<Exercise> options =
                new FirebaseRecyclerOptions.Builder<Exercise>()
                        .setQuery(FirebaseDatabase
                                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                                .getReference()
                                .child("ejercicios"), Exercise.class)
                        .build();

        ejercicioFbAdapter = new ExerciseFbAdapter(options, this::onItemClick);
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(ejercicioFbAdapter);
        ejercicioFbAdapter.startListening();
    }

    /**
     * Este método carga los ejercicios en el recyclerView según la categoría
     * @param valor
     */
    public void cargarDesdeSpiner(String valor){
        FirebaseRecyclerOptions<Exercise> options =
                new FirebaseRecyclerOptions.Builder<Exercise>()
                        .setQuery(FirebaseDatabase
                                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                                .getReference()
                                .child("ejercicios").orderByChild("categoria").equalTo(valor), Exercise.class)
                        .build();
        ejercicioFbAdapter = new ExerciseFbAdapter(options, this::onItemClick);
        recyclerView.setAdapter(ejercicioFbAdapter);
        ejercicioFbAdapter.startListening();
    }
    @Override
    public void onItemClick(Exercise ejercicio) {
        Fragment fragment = ExerciseCreationFragment.newInstance(ejercicio);
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment, "nota").addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        ejercicioFbAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        ejercicioFbAdapter.stopListening();
    }
}