package com.example.gymgameproject.exercises;

import static android.text.TextUtils.isEmpty;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;

import com.bumptech.glide.Glide;
import com.example.gymgameproject.MainActivity;
import com.example.gymgameproject.MainMenu;
import com.example.gymgameproject.R;
import com.example.gymgameproject.classes.Exercise;
import com.example.gymgameproject.classes.Routine;
import com.example.gymgameproject.classes.User;
import com.example.gymgameproject.databinding.FragmentExerciseDetailBinding;
import com.example.gymgameproject.routines.RoutineDetailFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class ExerciseDetailFragment extends Fragment {
    private static Exercise exercise;
    private static FragmentExerciseDetailBinding binding;

    public static ExerciseDetailFragment newInstance(Exercise exerciseF) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        exercise = exerciseF;
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExerciseDetailBinding.inflate(inflater, container, false);
        Objects.requireNonNull(((MainMenu) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        System.out.println("Ejercicio id: "+ exercise.getId());
        //codigo
        if(!isEmpty(exercise.getTime())) {binding.category.setText(exercise.getTime());}
        if(!isEmpty(exercise.getName())) {binding.detailName.setText(exercise.getName());}
        if(!isEmpty(exercise.getMuscle())) {binding.detailMuscles.setText(exercise.getMuscle());}
        if(!isEmpty(exercise.getDescription())) {binding.detailDesc.setText(exercise.getDescription());}
        Glide.with(getContext())
                .load(Uri.parse(exercise.getImage()))
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .error(R.drawable.baseline_add_242)//si ocurre algún error se verá por defecto
                .into(binding.detailImage);
        if(!isEmpty(exercise.getWeight())) {binding.weigthNum.setText(exercise.getWeight());}
        if(!isEmpty(exercise.getRepetitionsAndSeries())) { binding.numRepetitions.setText(exercise.getRepetitionsAndSeries());}
        if(!isEmpty(exercise.getTime())) {binding.numTime.setText(exercise.getTime());}

        binding.modBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottonSheet();
            }
        });
        //codigo fin
        return binding.getRoot();
    }

    public void showBottonSheet(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.my_bottom_sheet_exercise);
        Button acept = dialog.findViewById(R.id.acept);
        Button cancel = dialog.findViewById(R.id.cancel);
        //inicialización de numberPicker**************************
        //peso
        NumberPicker numberPeso = dialog.findViewById(R.id.weigthNumber);
        NumberPicker numberPesoDecimal = dialog.findViewById(R.id.weigthNumberDecimal);
        numberPeso.setMinValue(1);numberPeso.setMaxValue(100);
        String[] setPeso = binding.weigthNum.getText().toString().split("\\.");
        numberPeso.setValue(Integer.valueOf(setPeso[0]));
        numberPesoDecimal.setMinValue(0);numberPesoDecimal.setMaxValue(9);
        numberPesoDecimal.setValue(Integer.valueOf(setPeso[1]));
        //repeticiones y series
        String setRepSer1 = binding.numRepetitions.getText().toString().replace(" ","");
        String [] setRepSer2= setRepSer1.split("x");
        NumberPicker repeticiones = dialog.findViewById(R.id.repetitions);
        repeticiones.setMinValue(1);repeticiones.setMaxValue(100);
        repeticiones.setValue(Integer.valueOf(setRepSer2[0]));
        NumberPicker series = dialog.findViewById(R.id.series);
        series.setMinValue(1);series.setMaxValue(30);
        series.setValue(Integer.valueOf(setRepSer2[1]));
        //tiempo
        String setTime1 = binding.numTime.getText().toString().replace(" ","");
        String [] setTime2= setTime1.split(":");
        NumberPicker minutos = dialog.findViewById(R.id.minutes);
        minutos.setMinValue(0);minutos.setMaxValue(59);
        minutos.setValue(Integer.valueOf(setTime2[0]));
        NumberPicker segundos = dialog.findViewById(R.id.seconds);
        segundos.setMinValue(0);segundos.setMaxValue(59);
        segundos.setValue(Integer.valueOf(setTime2[1]));

        //inicialización de numberPicker**********************fin*
        acept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strNumRepetitions = repeticiones.getValue() + " x " + series.getValue();
                String strweigthNum = numberPeso.getValue() +"."+ numberPesoDecimal.getValue();
                String strNumTime = minutos.getValue() + " : " + segundos.getValue();
                binding.weigthNum.setText(strweigthNum);
                binding.numRepetitions.setText(strweigthNum);
                binding.numTime.setText(strNumTime);
                updateExercise(RoutineDetailFragment.getRoutine());
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    /**
     * Actualiza el ejercicio en la base de datos. Obtiene la posición del ejercicio en la lista de
     * ejercicios de la rutina seleccionada. Guarda en el ejercicio seleccionado el nuevo peso y
     * repeticiones. Con el index seleccionamos el ejercicio a actualizar en la base de datos
     * añadiendolo a la referencia.
     * @param routine seleccionada.
     */
    public void updateExercise(Routine routine) {//updateEjercicio
        int index = 0;
        for (int i = 0; i < routine.getExercises().size(); i++) {
            if(routine.getExercises().get(i).getId()== exercise.getId()){
                index = i;
            }
        }
        exercise.setWeight(binding.weigthNum.getText().toString());
        exercise.setRepetitionsAndSeries(binding.numRepetitions.getText().toString());
        exercise.setTime(binding.numTime.getText().toString());
        System.out.println(exercise);
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUserOB().getId()+"/rutinas/"+
                        routine.getName()+"/ejercicios/"+index);
        ref.setValue(exercise).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                actualizarAvance();
            }
        });
    }
    /**
     * Compara el nombre del ejercicio actualizado con la lista de nombres de ejercicios del objeto 'avance'.
     * Si encuentra una coincidencia, elimina esa coincidencia del objeto avance, y guarda en nue
     */
    public static void actualizarAvance(){
        System.out.println("actualizarAvance()");
        int index = 0;
        boolean bandera = false;
        if(!MainActivity.getAdvanceOB().getExercisesName().isEmpty()) {
            for (int i = 0; i < MainActivity.getAdvanceOB().getExercisesName().size(); i++) {
                if (MainActivity.getAdvanceOB().getExercisesName().get(i).equals(exercise.getName())) {
                    index = i;
                    bandera = true;
                }
            }
            if(bandera){
                MainActivity.getAdvanceOB().getWeights().set(index, exercise.getWeight());
            }else{
                MainActivity.getAdvanceOB().getExercisesName().add(exercise.getName());
                MainActivity.getAdvanceOB().getWeights().add(exercise.getWeight());
            }
        }else{
            MainActivity.getAdvanceOB().getExercisesName().add(exercise.getName());
            MainActivity.getAdvanceOB().getWeights().add(exercise.getWeight());
        }
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/" + MainActivity.getUserOB().getId() + "/avance");
        ref.setValue(MainActivity.getAdvanceOB()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                actualizarUsuario();
            }
        });
    }
    public static void actualizarUsuario(){
        System.out.println("Actualizando usuario");
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+MainActivity.getUserOB().getId());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//dataSnapshot son todos los usuarios
                MainActivity.setUserOB(dataSnapshot.getValue(User.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println( "actualizarUsuario:"  + error);
            }
        });
    }
}