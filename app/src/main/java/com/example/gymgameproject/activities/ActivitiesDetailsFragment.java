package com.example.gymgameproject.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gymgameproject.MainActivity;
import com.example.gymgameproject.MainMenu;
import com.example.gymgameproject.classes.Activity;
import com.example.gymgameproject.classes.AppHelper;
import com.example.gymgameproject.databinding.FragmentActivitiesDetailsBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivitiesDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivitiesDetailsFragment extends Fragment{
    private static Activity activity;
    private FragmentActivitiesDetailsBinding binding;//FragmentDetallesActividadBinding

    public ActivitiesDetailsFragment() {
        // Required empty public constructor
    }
    public static ActivitiesDetailsFragment newInstance(Activity activityF) {
        ActivitiesDetailsFragment fragment = new ActivitiesDetailsFragment();
        activity = activityF;
        AppHelper.cambiarToolbarText(activity.getName());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentActivitiesDetailsBinding.inflate(inflater, container, false);
        ((MainMenu) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppHelper.loadActivity(binding, getContext(), activity);
        boolean esta = false;
        //se busca las actividades reservadas por el usuario
        for (int i = 0; i < MainActivity.getActivitiesOBs().size(); i++) {
            if(MainActivity.getActivitiesOBs().get(i).getName().equals(activity.getName())){
                esta = true;
            }
        }
        //si la actividad no está reservada
        if (!esta) {
            //se activa el onclick listener
            binding.reserve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Integer.valueOf(activity.getVacancies())>1) {
                        AppHelper.bookActivity(activity, getContext());
                        getParentFragmentManager().popBackStack();
                    }else{
                        System.out.println("No quedan vacantes para esa actividad");
                        AppHelper.escribirToast("No quedan vacantes para esa actividad", getContext());
                    }
                }
            });
            binding.deleteReservation.setText("");
            binding.deleteReservation.setTextColor(Color.BLACK);
            binding.deleteReservation.setBackgroundColor(Color.TRANSPARENT);
        } else {
            binding.deleteReservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppHelper.deleteReservation(activity, getContext());
                    getParentFragmentManager().popBackStack();
                }
            });
            //si no el botoón cambia de aspecto
            binding.reserve.setText("Ya reservado");
            binding.reserve.setTextColor(Color.BLACK);
            binding.reserve.setBackgroundColor(Color.TRANSPARENT);
        }
        return binding.getRoot();
    }
}