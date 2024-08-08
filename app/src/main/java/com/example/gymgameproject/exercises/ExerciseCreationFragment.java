package com.example.gymgameproject.exercises;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.gymgameproject.MainMenu;
import com.example.gymgameproject.R;
import com.example.gymgameproject.classes.Exercise;
import com.example.gymgameproject.databinding.FragmentExerciseCreationBinding;
import com.example.gymgameproject.routines.RoutineCreationFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExerciseCreationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseCreationFragment extends Fragment {
    private static Exercise exercise;
    static FragmentExerciseCreationBinding binding;

    public ExerciseCreationFragment() { }
    public static ExerciseCreationFragment newInstance(Exercise exerciseF) {
        ExerciseCreationFragment fragment = new ExerciseCreationFragment();
        Bundle args = new Bundle();
        exercise = exerciseF;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainMenu) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExerciseCreationBinding.inflate(inflater, container, false);
        //codigo
        //inicialización de numberPicker**************************
        binding.numberPeso.setMinValue(1);binding.numberPeso.setMaxValue(100);
        binding.numberPesoDecimal.setMinValue(0);binding.numberPesoDecimal.setMaxValue(5);
        binding.numberRepeticiones.setMinValue(1);binding.numberRepeticiones.setMaxValue(30);
        binding.numberVeces.setMinValue(1);binding.numberVeces.setMaxValue(100);
        binding.numberMinutes.setMinValue(0);binding.numberMinutes.setMaxValue(59);
        binding.numberSeconds.setMinValue(0);binding.numberSeconds.setMaxValue(59);
        //inicialización de numberPicker**********************fin*
        binding.categoria.setText(exercise.getCategory());
        binding.detailName.setText(exercise.getName());
        binding.detailMusculos.setText(exercise.getMuscle());
        binding.detailDesc.setText(exercise.getDescription());
        Glide.with(getContext())
                .load(exercise.getImage())
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .error(R.drawable.baseline_add_242)//si ocurre algún error se verá por defecto
                .into(binding.detailImage);

        binding.agnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String peso = binding.numberPeso.getValue() +"." + binding.numberPesoDecimal.getValue();
                String repeticionesYseries = binding.numberRepeticiones.getValue() +" x "+ binding.numberVeces.getValue();
                String tiempo = binding.numberMinutes.getValue() +" : "+ binding.numberSeconds.getValue();
                exercise.setWeight(peso);exercise.setRepetitionsAndSeries(repeticionesYseries);exercise.setTime(tiempo);
                RoutineCreationFragment.addToDataList(exercise, getContext());
                getParentFragmentManager().popBackStack();
            }
        });
        //codigo fin
        return binding.getRoot();
    }
}