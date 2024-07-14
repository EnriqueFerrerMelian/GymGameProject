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

import com.example.gymgameproject.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoutineDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutineDetailFragment extends Fragment implements EjercicioFbAdapter.ItemClickListener {
    private static Rutina rutina;
    FragmentDetallesRutinaBinding binding;
    private static RecyclerView recyclerView;
    private EjercicioAdapter ejercicioAdapter;
    private static List<Ejercicio> dataArrayList;


    public DetallesRutinaFragment() {
        // Required empty public constructor
    }
    public static DetallesRutinaFragment newInstance(Rutina rutinaF) {
        DetallesRutinaFragment fragment = new DetallesRutinaFragment();
        rutina = rutinaF;
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetallesRutinaBinding.inflate(inflater, container, false);
        AppHelper.cambiarToolbarText(rutina.getNombre());
        ((MenuPrincipal) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //codigo
        //cargo el recyclerview *******************
        dataArrayList = rutina.getEjercicios();
        ejercicioAdapter = new EjercicioAdapter(dataArrayList, this::onItemClick);
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(ejercicioAdapter);
        //cargo el recyclerview ****************fin
        cargarRutina(rutina);

        binding.modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificarRutina(rutina);
            }
        });
        //codigo fin

        return binding.getRoot();
    }

    @Override
    public void onItemClick(Ejercicio ejercicio) {
        Fragment fragment = DetalleEjercicioFragment.newInstance(ejercicio);
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment, "nota").addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void modificarRutina(Rutina rutina){
        Fragment fragment = CreacionRutinaFragment.newInstance(rutina);
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment, "nota").addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static Rutina getRutina(){
        return rutina;
    }
    public static void setRutina(Rutina rutina){
        DetallesRutinaFragment.rutina = rutina;
    }
    public void cargarRutina(Rutina rutina){
        Glide.with(getContext())
                .load(rutina.getImg())
                .placeholder(R.drawable.logo)//si no hay imagen carga una por defecto
                .circleCrop()
                .error(R.drawable.logo)//si ocurre algún error se verá por defecto
                .into(binding.imagen);
        binding.nombreDeRutina.setText(rutina.getNombre());
        if(rutina.getDias().contains("l")){
            binding.lunes.setTextColor(Color.rgb(255, 127, 39));
        }if(rutina.getDias().contains("m")){
            binding.martes.setTextColor(Color.rgb(255, 127, 39));
        }if(rutina.getDias().contains("x")){
            binding.miercoles.setTextColor(Color.rgb(255, 127, 39));
        }if(rutina.getDias().contains("j")){
            binding.jueves.setTextColor(Color.rgb(255, 127, 39));
        }if(rutina.getDias().contains("v")){
            binding.viernes.setTextColor(Color.rgb(255, 127, 39));
        }if(rutina.getDias().contains("s")){
            binding.sabado.setTextColor(Color.rgb(255, 127, 39));
        }if(rutina.getDias().contains("d")){
            binding.domingo.setTextColor(Color.rgb(255, 127, 39));
        }
    }
}