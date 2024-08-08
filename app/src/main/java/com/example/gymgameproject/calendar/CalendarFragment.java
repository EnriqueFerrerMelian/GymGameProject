package com.example.gymgameproject.calendar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gymgameproject.MainMenu;
import com.example.gymgameproject.R;
import com.example.gymgameproject.classes.AppHelper;
import com.example.gymgameproject.classes.Event;
import com.example.gymgameproject.databinding.FragmentCalendarBinding;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class CalendarFragment extends Fragment implements OnNavigationButtonClickedListener {
    private FragmentCalendarBinding binding;
    private CustomCalendar customCalendar;
    private static ObjectAdapter eventoAdapter;
    private static List<Event> eventos = new ArrayList<>();
    private static RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        AppHelper.cambiarToolbarText("Calendario");
        ((MainMenu) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        customCalendar = binding.customCalendar;
        AppHelper.cargarCalendario(customCalendar, getContext(), this);
        //recycler
        recyclerView = binding.recyclerViewRutina;
        eventoAdapter = new ObjectAdapter(eventos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(eventoAdapter);
        //despliega en la lista los eventos del día actual, si los hay
        Calendar calendar = Calendar.getInstance();
        AppHelper.todayIsTheDay(calendar);
    }


    @Override
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
        AppHelper.updateArr(newMonth);
        Map<Integer, Object>[] arr = AppHelper.getArr();
        return arr;
    }
    public static void setRecyclerView(List<Event> eventoList){
        eventoAdapter = new ObjectAdapter(eventoList);
        recyclerView.setAdapter(eventoAdapter);
    }
}