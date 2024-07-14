package com.example.gymgameproject.exercises;

import static android.text.TextUtils.isEmpty;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;

import com.example.gymgameproject.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExerciseDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseDetailFragment extends Fragment {
    private static Ejercicio ejercicio;
    private static FragmentDetalleEjercicioBinding binding;

    public static DetalleEjercicioFragment newInstance(Ejercicio ejercicioF) {
        DetalleEjercicioFragment fragment = new DetalleEjercicioFragment();
        ejercicio = ejercicioF;
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalleEjercicioBinding.inflate(inflater, container, false);
        ((MenuPrincipal) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        System.out.println("Ejercicio id: "+ ejercicio.getId());
        //codigo
        if(!isEmpty(ejercicio.getTiempo())) {binding.categoria.setText(ejercicio.getCategoria());}
        if(!isEmpty(ejercicio.getNombre())) {binding.detailName.setText(ejercicio.getNombre());}
        if(!isEmpty(ejercicio.getMusculos())) {binding.detailMusculos.setText(ejercicio.getMusculos());}
        if(!isEmpty(ejercicio.getDescripcion())) {binding.detailDesc.setText(ejercicio.getDescripcion());}
        Glide.with(getContext())
                .load(Uri.parse(ejercicio.getImg()))
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .error(R.drawable.baseline_add_242)//si ocurre algún error se verá por defecto
                .into(binding.detailImage);
        if(!isEmpty(ejercicio.getPeso())) {binding.numPeso.setText(ejercicio.getPeso());}
        if(!isEmpty(ejercicio.getRepecitionesYseries())) { binding.numRepeticiones.setText(ejercicio.getRepecitionesYseries());}
        if(!isEmpty(ejercicio.getTiempo())) {binding.numTiempo.setText(ejercicio.getTiempo());}

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
        dialog.setContentView(R.layout.my_bottom_sheet_ejercicio);
        Button aceptar = dialog.findViewById(R.id.aceptar);
        Button cancel = dialog.findViewById(R.id.cancel);
        //inicialización de numberPicker**************************
        //peso
        NumberPicker numberPeso = dialog.findViewById(R.id.numberPeso);
        NumberPicker numberPesoDecimal = dialog.findViewById(R.id.numberPesoDecimal);
        numberPeso.setMinValue(1);numberPeso.setMaxValue(100);
        String[] setPeso = binding.numPeso.getText().toString().split("\\.");
        numberPeso.setValue(Integer.valueOf(setPeso[0]));
        numberPesoDecimal.setMinValue(0);numberPesoDecimal.setMaxValue(9);
        numberPesoDecimal.setValue(Integer.valueOf(setPeso[1]));
        //repeticiones y series
        String setRepSer1 = binding.numRepeticiones.getText().toString().replace(" ","");
        String [] setRepSer2= setRepSer1.split("x");
        NumberPicker repeticiones = dialog.findViewById(R.id.repeticiones);
        repeticiones.setMinValue(1);repeticiones.setMaxValue(100);
        repeticiones.setValue(Integer.valueOf(setRepSer2[0]));
        NumberPicker series = dialog.findViewById(R.id.series);
        series.setMinValue(1);series.setMaxValue(30);
        series.setValue(Integer.valueOf(setRepSer2[1]));
        //tiempo
        String setTime1 = binding.numTiempo.getText().toString().replace(" ","");
        String [] setTime2= setTime1.split(":");
        NumberPicker minutos = dialog.findViewById(R.id.minutos);
        minutos.setMinValue(0);minutos.setMaxValue(59);
        minutos.setValue(Integer.valueOf(setTime2[0]));
        NumberPicker segundos = dialog.findViewById(R.id.segundos);
        segundos.setMinValue(0);segundos.setMaxValue(59);
        segundos.setValue(Integer.valueOf(setTime2[1]));

        //inicialización de numberPicker**********************fin*
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.numPeso.setText(numberPeso.getValue() +"."+ numberPesoDecimal.getValue());
                binding.numRepeticiones.setText(repeticiones.getValue() + " x " + series.getValue());
                binding.numTiempo.setText(minutos.getValue() + " : " + segundos.getValue());
                updateEjercicio(DetallesRutinaFragment.getRutina());
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
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    /**
     * Actualiza el ejercicio en la base de datos. Obtiene la posición del ejercicio en la lista de
     * ejercicios de la rutina seleccionada. Guarda en el ejercicio seleccionado el nuevo peso y
     * repeticiones. Con el index seleccionamos el ejercicio a actualizar en la base de datos
     * añadiendolo a la referencia.
     * @param rutina seleccionada.
     */
    public void updateEjercicio(Rutina rutina) {
        int index = 0;
        for (int i = 0; i < rutina.getEjercicios().size(); i++) {
            if(rutina.getEjercicios().get(i).getId()== ejercicio.getId()){
                index = i;
            }
        }
        ejercicio.setPeso(binding.numPeso.getText().toString());
        ejercicio.setRepecitionesYseries(binding.numRepeticiones.getText().toString());
        ejercicio.setTiempo(binding.numTiempo.getText().toString());
        System.out.println(ejercicio);
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+MainActivity.getUsuarioOB().getId()+"/rutinas/"+
                        rutina.getNombre()+"/ejercicios/"+index);
        ref.setValue(ejercicio).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        if(MainActivity.getAvanceOB().getEjerciciosNombres().size()>0) {
            for (int i = 0; i < MainActivity.getAvanceOB().getEjerciciosNombres().size(); i++) {
                if (MainActivity.getAvanceOB().getEjerciciosNombres().get(i).equals(ejercicio.getNombre())) {
                    index = i;
                    bandera = true;
                }
            }
            if(bandera){
                MainActivity.getAvanceOB().getPesos().set(index, ejercicio.getPeso());
            }else{
                MainActivity.getAvanceOB().getEjerciciosNombres().add(ejercicio.getNombre());
                MainActivity.getAvanceOB().getPesos().add(ejercicio.getPeso());
            }
        }else{
            MainActivity.getAvanceOB().getEjerciciosNombres().add(ejercicio.getNombre());
            MainActivity.getAvanceOB().getPesos().add(ejercicio.getPeso());
        }
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/" + MainActivity.getUsuarioOB().getId() + "/avance");
        ref.setValue(MainActivity.getAvanceOB()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                .getReference("usuarios/"+MainActivity.getUsuarioOB().getId());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//dataSnapshot son todos los usuarios
                MainActivity.setUsuarioOB(dataSnapshot.getValue(Usuario.class));
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}