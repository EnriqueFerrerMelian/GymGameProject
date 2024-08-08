package com.example.gymgameproject.routines;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.gymgameproject.MainMenu;
import com.example.gymgameproject.R;
import com.example.gymgameproject.activities.ActivitiesFbAdapter;
import com.example.gymgameproject.classes.AppHelper;
import com.example.gymgameproject.classes.Exercise;
import com.example.gymgameproject.classes.Routine;
import com.example.gymgameproject.databinding.FragmentRoutineBinding;
import com.example.gymgameproject.databinding.FragmentRoutineDetailBinding;
import com.example.gymgameproject.exercises.ExerciseAdapter;
import com.example.gymgameproject.exercises.ExerciseDetailFragment;
import com.example.gymgameproject.exercises.ExerciseFbAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoutineDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutineDetailFragment extends Fragment implements ExerciseFbAdapter.ItemClickListener {
    private static Routine routine;
    FragmentRoutineDetailBinding binding;
    private static RecyclerView recyclerView;
    private ExerciseAdapter ejercicioAdapter;
    private static List<Exercise> dataArrayList;


    public RoutineDetailFragment() {
        // Required empty public constructor
    }
    public static RoutineDetailFragment newInstance(Routine routineF) {
        RoutineDetailFragment fragment = new RoutineDetailFragment();
        routine = routineF;
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRoutineDetailBinding.inflate(inflater, container, false);
        AppHelper.cambiarToolbarText(routine.getName());
        ((MainMenu) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //codigo
        //cargo el recyclerview *******************
        dataArrayList = routine.getExercises();
        ejercicioAdapter = new ExerciseAdapter(dataArrayList, this::onItemClick);
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(ejercicioAdapter);
        //cargo el recyclerview ****************fin
        loadRoutine(routine);

        binding.modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificarRutina(routine);
            }
        });
        //codigo fin

        return binding.getRoot();
    }

    @Override
    public void onItemClick(Exercise exercise) {
        Fragment fragment = ExerciseDetailFragment.newInstance(exercise);
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment, "nota").addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void modificarRutina(Routine routine){
        Fragment fragment = RoutineCreationFragment.newInstance(routine);
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment, "nota").addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static Routine getRoutine(){
        return routine;
    }
    public static void setRutina(Routine routine){
        RoutineDetailFragment.routine = routine;
    }
    public void loadRoutine(Routine routine){//cargarRutina
        Glide.with(getContext())
                .load(routine.getImage())
                .placeholder(R.drawable.logo)//si no hay imagen carga una por defecto
                .circleCrop()
                .error(R.drawable.logo)//si ocurre algún error se verá por defecto
                .into(binding.imagen);
        binding.nombreDeRutina.setText(routine.getName());
        if(routine.getDays().contains("l")){
            binding.lunes.setTextColor(Color.rgb(255, 127, 39));
        }if(routine.getDays().contains("m")){
            binding.martes.setTextColor(Color.rgb(255, 127, 39));
        }if(routine.getDays().contains("x")){
            binding.miercoles.setTextColor(Color.rgb(255, 127, 39));
        }if(routine.getDays().contains("j")){
            binding.jueves.setTextColor(Color.rgb(255, 127, 39));
        }if(routine.getDays().contains("v")){
            binding.viernes.setTextColor(Color.rgb(255, 127, 39));
        }if(routine.getDays().contains("s")){
            binding.sabado.setTextColor(Color.rgb(255, 127, 39));
        }if(routine.getDays().contains("d")){
            binding.domingo.setTextColor(Color.rgb(255, 127, 39));
        }
    }
}