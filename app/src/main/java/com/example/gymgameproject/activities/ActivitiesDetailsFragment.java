package com.example.gymgameproject.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gymgameproject.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivitiesDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivitiesDetailsFragment extends Fragment{
    private static Actividad actividad;
    private FragmentDetallesActividadBinding binding;

    public DetallesActividadFragment() {
        // Required empty public constructor
    }
    public static DetallesActividadFragment newInstance(Actividad actividadF) {
        DetallesActividadFragment fragment = new DetallesActividadFragment();
        actividad = actividadF;
        AppHelper.cambiarToolbarText(actividad.getNombre());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetallesActividadBinding.inflate(inflater, container, false);
        ((MenuPrincipal) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppHelper.cargarActividad(binding, getContext(), actividad);
        boolean esta = false;
        //se busca las actividades reservadas por el usuario
        for (int i = 0; i < MainActivity.getActividadesOBs().size(); i++) {
            if(MainActivity.getActividadesOBs().get(i).getNombre().equals(actividad.getNombre())){
                esta = true;
            }
        }
        //si la actividad no está reservada
        if (!esta) {
            //se activa el onclick listener
            binding.reservar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Integer.valueOf(actividad.getVacantes())>1) {
                        AppHelper.reservarActividad(actividad, getContext());
                        getParentFragmentManager().popBackStack();
                    }else{
                        System.out.println("No quedan vacantes para esa actividad");
                        AppHelper.escribirToast("No quedan vacantes para esa actividad", getContext());
                    }
                }
            });
            binding.eliminarReserva.setText("");
            binding.eliminarReserva.setTextColor(Color.BLACK);
            binding.eliminarReserva.setBackgroundColor(Color.TRANSPARENT);
        } else {
            binding.eliminarReserva.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppHelper.eliminarReserva(actividad, getContext());
                    getParentFragmentManager().popBackStack();
                }
            });
            //si no el botoón cambia de aspecto
            binding.reservar.setText("Ya reservado");
            binding.reservar.setTextColor(Color.BLACK);
            binding.reservar.setBackgroundColor(Color.TRANSPARENT);
        }
        return binding.getRoot();
    }
}