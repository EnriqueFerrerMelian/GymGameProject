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
import com.example.gymgameproject.MenuPrincipal;
import com.example.gymgameproject.R;
import com.example.gymgameproject.classes.Advance;
import com.example.gymgameproject.classes.AppHelper;
import com.example.gymgameproject.classes.Weight;
import com.example.gymgameproject.databinding.FragmentStadisticsBinding;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StadisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StadisticsFragment extends Fragment {
    private static FragmentStadisticsBinding binding;
    private static Entry pesoSeleccionado = new Entry();
    private static BarEntry progresoSeleccionado = new BarEntry(0,0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStadisticsBinding.inflate(inflater, container, false);
        AppHelper.cambiarToolbarText("Estadísticas");
        ((MenuPrincipal) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
        AppHelper.configurarChartPeso(binding);
        AppHelper.configurarChartAvance(binding);

        binding.aniadirPeso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottonSheetPeso(true);
            }
        });
        binding.aniadirObjetivo.setOnClickListener(new View.OnClickListener() {
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
        binding.eliminarPeso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEliminarElemento("¿Desehas eliminar ese registro?", true);
            }
        });
        binding.eliminarAvance.setOnClickListener(new View.OnClickListener() {
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
        NumberPicker numberPeso = dialog.findViewById(R.id.numberWeight);
        NumberPicker numberPesoDecimal = dialog.findViewById(R.id.numberDecimalWeight);
        NumberPicker numberPesoOb = dialog.findViewById(R.id.numberObWeight);
        NumberPicker numberPesoDecimalOb = dialog.findViewById(R.id.numberDecimalObWeight);
        Button add = dialog.findViewById(R.id.add);
        Button cancel = dialog.findViewById(R.id.cancel);
        //inicialización de numberPicker**************************
        if(status){
            peso.setVisibility(View.VISIBLE);
            objetivo.setVisibility(View.INVISIBLE);
            numberPeso.setMinValue(1);numberPeso.setMaxValue(150);
            numberPesoDecimal.setMinValue(0);numberPesoDecimal.setMaxValue(9);
            if(!MainActivity.getWeightOB().getWeightData().isEmpty()){
                String[] objetivoArray = MainActivity.getWeightOB().getWeightData().get(MainActivity.getWeightOB().getWeightData().size()-1).get("y").split("\\.");
                numberPeso.setValue(Integer.valueOf(objetivoArray[0]));
                numberPesoDecimal.setValue(Integer.valueOf(objetivoArray[1]));
            }
        }else{
            peso.setVisibility(View.INVISIBLE);
            objetivo.setVisibility(View.VISIBLE);
            numberPesoOb.setMinValue(0);numberPesoOb.setMaxValue(150);
            numberPesoDecimalOb.setMinValue(0);numberPesoDecimalOb.setMaxValue(9);
            if(MainActivity.getWeightOB().getTarget()!=null){
                String[] objetivoArray = MainActivity.getWeightOB().getTarget().split("\\.");
                numberPesoOb.setValue(Integer.valueOf(objetivoArray[0]));
                numberPesoDecimalOb.setValue(Integer.valueOf(objetivoArray[1]));
            }
        }
        //inicialización de numberPicker**********************fin*
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status){
                    String outputWeight = numberWeight.getValue() +"." + numberDecimalWeight.getValue();
                    AppHelper.actualizarPeso(AppHelper.addDatosPeso(outputWeight));
                }else{
                    String objetivoOut = numberObWeight.getValue() +"." + numberDecimalObWeight.getValue();
                    AppHelper.actualizarPeso(AppHelper.addDatosObjetivo(objetivoOut));
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
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    /**
     * Abre un cuadro de diálogo de confirmación para confirmar si se elimina el elemento seleccionado.
     * En función del elemento eliminado (peso o progreso), se introduce un texto adecuado y true para
     * eliminar un peso seleccionado o false para un progreso seleccionado.
     * @param textoInput Texto que se mostrará en el cuadro de diálogo
     * @param esPeso True o false para peso seleccionado o progreso respectivamente.
     */
    public void showEliminarElemento(String textoInput, boolean esPeso) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.my_bottom_sheet_sobrescribir_rutina);
        TextView texto = dialog.findViewById(R.id.textView2);
        texto.setText(textoInput);
        Button si = dialog.findViewById(R.id.si);
        Button no = dialog.findViewById(R.id.no);
        System.out.println(pesoSeleccionado);
        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(esPeso){
                    if(binding.lineChart.getLineData().getDataSets().get(0).getEntryCount()>1){
                        //! no puede ser aplicaco a tipo float
                        if(pesoSeleccionado.getY()==0.0){
                            AppHelper.escribirToast("Debes seleccionar un registro", getContext());
                        }else{
                            int index = binding.lineChart.getLineData().getDataSetByIndex(0).getEntryIndex(pesoSeleccionado);
                            MainActivity.getPesoOB().getDatosPeso().remove(index);
                            MainActivity.getPesoOB().getFecha().remove(index);
                            AppHelper.actualizarPeso(MainActivity.getPesoOB());
                        }
                    }else{
                        AppHelper.escribirToast("Debe haber al menos un registro", getContext());
                    }
                }else{
                    if(binding.barChart.getBarData().getDataSets().get(0).getEntryCount()>0){
                        int index = binding.barChart.getBarData().getDataSetByIndex(0).getEntryIndex(progresoSeleccionado);
                        MainActivity.getAvanceOB().getEjerciciosNombres().remove(index);
                        MainActivity.getAvanceOB().getPesos().remove(index);
                        AppHelper.actualizarAvance(MainActivity.getAvanceOB());
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
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    /**
     * @return El objeto binding del fragmento.
     */
    public static FragmentStadisticsBinding getBinding(){
        return StadisticsFragment.binding;
    }
}