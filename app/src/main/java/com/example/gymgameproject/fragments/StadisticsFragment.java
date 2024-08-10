package com.example.gymgameproject.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.gymgameproject.MainActivity;
import com.example.gymgameproject.MainMenu;
import com.example.gymgameproject.R;
import com.example.gymgameproject.classes.Advance;
import com.example.gymgameproject.classes.AppHelper;
import com.example.gymgameproject.classes.Weight;
import com.example.gymgameproject.databinding.FragmentStadisticsBinding;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.Objects;

public class StadisticsFragment extends Fragment {
    private static FragmentStadisticsBinding binding;
    private static Entry pesoSeleccionado = new Entry();
    private static BarEntry progresoSeleccionado = new BarEntry(0,0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStadisticsBinding.inflate(inflater, container, false);
        AppHelper.cambiarToolbarText("Estadísticas");
        ((MainMenu) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(MainActivity.getWeightOB()==null){
            MainActivity.setWeightOB(new Weight());
        }else{
            binding.textoInfo.setVisibility(View.GONE);
        }
        if(MainActivity.getAdvanceOB()==null){
            MainActivity.setAdvanceOB(new Advance());
        }
        AppHelper.weightChartConfiguration(binding);
        AppHelper.ChartAdvanceconfiguration(binding);

        binding.addWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottonSheetPeso(true);
            }
        });
        binding.addTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottonSheetPeso(false);
            }
        });

        /**
         * selecciona un dato del chart y lo guarda en una variable global 'pesoSeleccionado'
         */
        binding.lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                pesoSeleccionado = e;
            }
            @Override
            public void onNothingSelected() {}
        });
        binding.barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                progresoSeleccionado = (BarEntry) e;
            }

            @Override
            public void onNothingSelected() {}
        });
        binding.removeWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEliminarElemento("¿Desehas eliminar ese registro?", true);
            }
        });
        binding.removeAdvance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEliminarElemento("¿Desehas eliminar ese progreso?", false);
            }
        });
    }
    /**
     * Abre un cuadro de diálogo con el que se podrá introducir un peso en el registro
     */
    public void showBottonSheetPeso(boolean status){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.my_bottom_sheet_add_weight);
        LinearLayout peso = dialog.findViewById(R.id.weight);
        LinearLayout objetivo = dialog.findViewById(R.id.target);
        NumberPicker numberWeight = dialog.findViewById(R.id.numberWeight);
        NumberPicker numberDecimalWeight = dialog.findViewById(R.id.numberDecimalWeight);
        NumberPicker numberObWeight = dialog.findViewById(R.id.numberObWeight);
        NumberPicker numberDecimalObWeight = dialog.findViewById(R.id.numberDecimalObWeight);
        Button add = dialog.findViewById(R.id.add);
        Button cancel = dialog.findViewById(R.id.cancel);
        //inicialización de numberPicker**************************
        if(status){
            peso.setVisibility(View.VISIBLE);
            objetivo.setVisibility(View.INVISIBLE);
            numberWeight.setMinValue(1);numberWeight.setMaxValue(150);
            numberDecimalWeight.setMinValue(0);numberDecimalWeight.setMaxValue(9);
            if(!MainActivity.getWeightOB().getWeightData().isEmpty()){
                String[] objetivoArray = MainActivity.getWeightOB().getWeightData().get(MainActivity.getWeightOB().getWeightData().size()-1).get("y").split("\\.");
                numberWeight.setValue(Integer.valueOf(objetivoArray[0]));
                numberDecimalWeight.setValue(Integer.valueOf(objetivoArray[1]));
            }
        }else{
            peso.setVisibility(View.INVISIBLE);
            objetivo.setVisibility(View.VISIBLE);
            numberObWeight.setMinValue(0);numberObWeight.setMaxValue(150);
            numberDecimalObWeight.setMinValue(0);numberDecimalObWeight.setMaxValue(9);
            if(MainActivity.getWeightOB().getTarget()!=null){
                String[] objetivoArray = MainActivity.getWeightOB().getTarget().split("\\.");
                numberObWeight.setValue(Integer.parseInt(objetivoArray[0]));
                numberDecimalObWeight.setValue(Integer.parseInt(objetivoArray[1]));
            }
        }
        //inicialización de numberPicker**********************fin*
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status){
                    String outputWeight = numberWeight.getValue() +"." + numberDecimalWeight.getValue();
                    AppHelper.updateWeight(AppHelper.addWeightData(outputWeight));
                }else{
                    String objetivoOut = numberObWeight.getValue() +"." + numberDecimalObWeight.getValue();
                    AppHelper.updateWeight(AppHelper.addTargetData(objetivoOut));
                }
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
     * Opens a confirmation dialog box to confirm whether to delete the selected item.
     * Depending on the item removed (weight or progress), appropriate and true text is entered for
     * remove a selected weight or false for a selected progress.
     * @param textInput Text to be displayed in the dialog
     * @param isWeight True or false for selected weight or progress respectively.
     */
    public void showEliminarElemento(String textInput, boolean isWeight) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.my_bottom_sheet_overwrite_routine);
        TextView texto = dialog.findViewById(R.id.textView2);
        texto.setText(textInput);
        Button si = dialog.findViewById(R.id.si);
        Button no = dialog.findViewById(R.id.no);
        System.out.println(pesoSeleccionado);
        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isWeight){
                    if(binding.lineChart.getLineData().getDataSets().get(0).getEntryCount()>1){
                        //! no puede ser aplicaco a tipo float
                        if(pesoSeleccionado.getY()==0.0){
                            AppHelper.writeToast("Debes seleccionar un registro", getContext());
                        }else{
                            int index = binding.lineChart.getLineData().getDataSetByIndex(0).getEntryIndex(pesoSeleccionado);
                            MainActivity.getWeightOB().getWeightData().remove(index);
                            MainActivity.getWeightOB().getDate().remove(index);
                            AppHelper.updateWeight(MainActivity.getWeightOB());
                        }
                    }else{
                        AppHelper.writeToast("Debe haber al menos un registro", getContext());
                    }
                }else{
                    if(binding.barChart.getBarData().getDataSets().get(0).getEntryCount()>0){
                        int index = binding.barChart.getBarData().getDataSetByIndex(0).getEntryIndex(progresoSeleccionado);
                        MainActivity.getAdvanceOB().getExercisesName().remove(index);
                        MainActivity.getAdvanceOB().getWeights().remove(index);
                        AppHelper.advanceUpdate(MainActivity.getAdvanceOB());
                    }
                }
                dialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
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

    public static FragmentStadisticsBinding getBinding(){
        return StadisticsFragment.binding;
    }
}