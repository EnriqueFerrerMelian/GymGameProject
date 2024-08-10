package com.example.gymgameproject;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gymgameproject.activities.ActivitiesFragment;
import com.example.gymgameproject.calendar.CalendarFragment;
import com.example.gymgameproject.databinding.ActivityMenuPrincipalBinding;
import com.example.gymgameproject.fragments.ProfileFragment;
import com.example.gymgameproject.fragments.StadisticsFragment;
import com.example.gymgameproject.routines.RoutineFragment;
import com.example.gymgameproject.tabloid.TabloidFragment;

public class MainMenu extends AppCompatActivity {
    private static Fragment fragmentoDesechable = null;//se usa para controlar la transacción entre fragmentos
    private static ActivityMenuPrincipalBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuPrincipalBinding.inflate(getLayoutInflater());//carga la vista xml al cargar la aplicación
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolBar);

        replaceFragment(new RoutineFragment());
        //listeners
        binding.bottomNav.setOnItemSelectedListener(item ->{
            if(item.getItemId()==R.id.ejercicio){
                replaceFragment(new RoutineFragment());
            }
            if(item.getItemId()==R.id.estadisticas){
                replaceFragment(new StadisticsFragment());
            }
            if(item.getItemId()==R.id.actividades){
                replaceFragment(new ActivitiesFragment());
            }
            if(item.getItemId()==R.id.calendario){
                replaceFragment(new CalendarFragment());
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.perfil){
            replaceFragment(new ProfileFragment());
        }
        if(item.getItemId()==R.id.tablon){
            replaceFragment(new TabloidFragment());
        }
        if(item.getItemId()==16908332){
            getSupportFragmentManager().popBackStack();
        }
        //return true;
        return super.onOptionsItemSelected(item);
    }

    /**
     * Replace the fragment on the container 'fragmentContainerView' by the one passed as param
     */
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(fragmentoDesechable!=null){
            fragmentTransaction.remove(fragmentoDesechable);
        }
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentoDesechable = fragment;
        fragmentTransaction.commit();
    }

    public static ActivityMenuPrincipalBinding getBinding(){
        return binding;
    }
}