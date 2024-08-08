package com.example.gymgameproject.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.gymgameproject.MainActivity;
import com.example.gymgameproject.MainMenu;
import com.example.gymgameproject.R;
import com.example.gymgameproject.calendar.CalendarFragment;
import com.example.gymgameproject.databinding.FragmentActivitiesDetailsBinding;
import com.example.gymgameproject.databinding.FragmentNewsDetailBinding;
import com.example.gymgameproject.databinding.FragmentProfileBinding;
import com.example.gymgameproject.databinding.FragmentStadisticsBinding;
import com.example.gymgameproject.fragments.ProfileFragment;
import com.example.gymgameproject.fragments.StadisticsFragment;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener;
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AppHelper {
    private static Uri imgUriFb = Uri.parse(" ");
    private static StorageReference storageReference;
    //SECURITY****************************************************************

    private static final String algorithSks = "AES";
    private static final String algorithSha = "SHA-256";//algoritmo de hal seguro
    private static final String charSet = "UTF-8";//codificacion de texto

    /**
     * Cypher the password using codification key
     * @param clave
     * @param password
     * @return String
     */
    public static String encript(String clave, String password){
        try{
            SecretKeySpec secretKeySpec = generateKey(clave);//crea una llave de encriptacion
            Cipher cipher = Cipher.getInstance(algorithSks);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);//genera un modo de encriptacion con la key como raiz
            byte[] datosEncriptadosBytes = cipher.doFinal(password.getBytes());//encriptamos el password y lo pasamos a bytes
            String datosEncriptadosString = Base64.encodeToString(datosEncriptadosBytes, Base64.DEFAULT);//lo pasamos a String
            return datosEncriptadosString;

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    /**
     * Crea una llave de codificación
     * @param clave
     * @return SecretKeySpec
     */
    private static SecretKeySpec generateKey(String clave) {
        try {
            MessageDigest sha = MessageDigest.getInstance(algorithSha);
            byte[] key = clave.getBytes(charSet);
            key = sha.digest(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithSks);
            return secretKeySpec;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    //SEGURIDAD*************************************************************FIN

    // ESTADÍSTICAS**********************************ESTADÍSTICAS**********************************
    //TODOS LOS MÉTODOS DEL FRAGMENTO ESTADISTICAS AQUÍ
    /**
     * Update weight data, then will pass them
     * to the database.
     * @param weight Weight selected, transformed to String.
     * @return Objeto Peso con los datos actualizados
     */
    public static Weight addWeightData(String weight){//addDatosPeso
        //Get today's date
        Calendar cal = new GregorianCalendar();
        Date todayDate = cal.getTime();
        String date = todayDate.getDate() +"/"+ todayDate.getMonth();

        //create a map storing the axis
        Map<String, String> weightData = new HashMap<>();
        weightData.put("x", String.valueOf(MainActivity.getWeightOB().getWeightData().size()+1));
        weightData.put("y", weight);
        MainActivity.getWeightOB().getDate().add(date);
        MainActivity.getWeightOB().getWeightData().add(weightData);

        return MainActivity.getWeightOB();
    }
    public static Weight addTargetData(String target){//addDatosObjetivo
        MainActivity.getWeightOB().setTarget(target);
        return MainActivity.getWeightOB();
    }

    /**
     * Configura la apariencia por defecto del chart y carga los datos del objeto Peso
     */
    public static void weightChartConfiguration(FragmentStadisticsBinding binding){//configurarChartPeso
        float maxView = 0;float minView = 0;

        //removing borders
        binding.lineChart.setDrawBorders(false);
        binding.lineChart.getAxisRight().setDrawLabels(false);
        binding.lineChart.getAxisRight().setDrawGridLines(false);
        binding.lineChart.getAxisLeft().setDrawLabels(false);
        binding.lineChart.getAxisLeft().setDrawGridLines(false);
        binding.lineChart.getXAxis().setDrawGridLines(false);
        binding.lineChart.setDrawGridBackground(false);
        Description desc = new Description();
        desc.setText(" ");
        binding.lineChart.setDescription(desc);

        //leyends
        Legend l = binding.lineChart.getLegend();
        l.setEnabled(true);
        l.setTextSize(15);
        l.setForm(Legend.LegendForm.LINE);
        LegendEntry[] legendEntry = new LegendEntry[2];
        LegendEntry lEntry1 = new LegendEntry();
        lEntry1.formColor = Color.GREEN;
        lEntry1.label = "Weight";
        legendEntry[0] = lEntry1;
        LegendEntry lEntry2 = new LegendEntry();
        lEntry2.formColor = Color.rgb(255,135,0);
        lEntry2.label = "Target";
        legendEntry[1] = lEntry2;
        l.setCustom(legendEntry);

        binding.lineChart.canScrollHorizontally(1);
        binding.lineChart.setVisibleXRangeMaximum(7f);
        binding.lineChart.setNoDataText("No data saved.");

        //>>>****LOADING DATA********
        //loading dates on axis X
        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final List<String> dates = MainActivity.getWeightOB().getDate();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));

        //Loading entries
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < MainActivity.getWeightOB().getWeightData().size(); i++) {
            entries.add(new Entry((float) i,Float.parseFloat(Objects.requireNonNull(MainActivity.getWeightOB().getWeightData().get(i).get("y")))));
            if(Float.parseFloat(Objects.requireNonNull(MainActivity.getWeightOB().getWeightData().get(i).get("y")))>maxView){
                maxView = Float.parseFloat(Objects.requireNonNull(MainActivity.getWeightOB().getWeightData().get(i).get("y")));
            }
            if(Float.parseFloat(Objects.requireNonNull(MainActivity.getWeightOB().getWeightData().get(i).get("y")))<minView){
                minView = Float.parseFloat(Objects.requireNonNull(MainActivity.getWeightOB().getWeightData().get(i).get("y")));
            }
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "Weight");
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setCircleColor(Color.GREEN); // color for highlight indicator
        lineDataSet.setCircleRadius(6); // color for highlight indicator
        lineDataSet.setColor(Color.GREEN);
        lineDataSet.setValueTextSize(15);
        lineDataSet.setLineWidth(3);
        LineData lineData = new LineData(lineDataSet);
        if(MainActivity.getWeightOB().getWeightData().size()>0){
            binding.ultimoPeso.setText(MainActivity.getWeightOB().getWeightData().get(MainActivity.getWeightOB().getWeightData().size()-1).get("y") + " Kgs");
        }
        //>>>****LOADING DATA*****FIN

        //If target is selected
        if(MainActivity.getWeightOB().getTarget()!=null){
            if(Float.parseFloat(MainActivity.getWeightOB().getTarget())>0){
                float leyendVal =Float.parseFloat(MainActivity.getWeightOB().getTarget());
                YAxis yAxis = binding.lineChart.getAxisLeft();
                LimitLine ll = new LimitLine(leyendVal, "");
                ll.setLineColor(Color.rgb(255,135,0));
                ll.setLineWidth(3f);
                ll.setTextColor(Color.BLACK);
                ll.setTextSize(10f);
                yAxis.addLimitLine(ll);
                float valorMaximoVisible = (maxView<leyendVal)? leyendVal : maxView;
                float valorMinimoVisible = (minView>leyendVal)? leyendVal : minView;
                yAxis.setAxisMaximum(valorMaximoVisible+10);
                yAxis.setAxisMinimum(valorMinimoVisible);
            }
        }
        binding.lineChart.moveViewToX(entries.size()-1);
        binding.lineChart.setData(lineData);
        binding.lineChart.invalidate();
    }

    public static void ChartAdvanceconfiguration(FragmentStadisticsBinding binding){//configurarChartAvance
        //removing borders
        binding.barChart.setDrawBorders(false);
        binding.barChart.getAxisRight().setDrawLabels(false);
        binding.barChart.getAxisRight().setDrawGridLines(false);
        binding.barChart.getAxisLeft().setDrawLabels(false);
        binding.barChart.getAxisLeft().setDrawGridLines(false);
        binding.barChart.getXAxis().setDrawGridLines(false);
        binding.barChart.setDrawGridBackground(false);
        Description desc = new Description();
        desc.setText(" ");
        binding.barChart.setDescription(desc);

        //leyends
        Legend l = binding.barChart.getLegend();
        l.setEnabled(true);
        l.setTextSize(15);
        l.setForm(Legend.LegendForm.LINE);
        LegendEntry[] legendEntry = new LegendEntry[1];
        LegendEntry lEntry1 = new LegendEntry();
        lEntry1.formColor = Color.RED;
        lEntry1.label = "Kgs";
        legendEntry[0] = lEntry1;
        l.setCustom(legendEntry);

        //>>>**** INPUT DATA ********
        //inserting names on axe X
        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(recortarNombres(MainActivity.getAdvanceOB().getExercisesName())));

        //inserting entries
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < MainActivity.getAdvanceOB().getWeights().size(); i++) {//exercises weights, string array
            entries.add(new BarEntry(Float.valueOf(i),Float.valueOf(MainActivity.getAdvanceOB().getWeights().get(i))));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Advance");
        barDataSet.setColor(Color.rgb(255,127,39));
        barDataSet.setValueTextSize(15);
        BarData barData = new BarData(barDataSet);
        String lastInputValue = MainActivity.getAdvanceOB().getExercisesName()
                .isEmpty() ? " " : MainActivity.getAdvanceOB()
                .getExercisesName().get(MainActivity.getAdvanceOB().getExercisesName().size()-1);
        binding.ultimoProgreso.setText(lastInputValue);
        //>>>**** INPUT DATA *****end
        binding.barChart.canScrollHorizontally(1);

        binding.barChart.setNoDataText("No data saved.");
        binding.barChart.moveViewToX(entries.size()-1);
        binding.barChart.setData(barData);
        binding.barChart.invalidate();
        binding.barChart.setVisibleXRangeMaximum(5);
    }

    public static void advanceUpdate(Advance advance){//actualizarAvance
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUserOB().getId()+"/avance");
        ref.setValue(advance).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateApp();
                ChartAdvanceconfiguration(StadisticsFragment.getBinding());
            }
        });
    }
    /**
     * Actualiza los datos del peso del usuario en Firebase.
     * @param weight
     */
    public static void updateWeight(Weight weight){//actualizarPeso
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUserOB().getId()+"/peso");
        ref.setValue(weight).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateApp();
                weightChartConfiguration(StadisticsFragment.getBinding());
            }
        });
    }
    public static void actualizarObjetivo(Weight weight){
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUserOB().getId()+"/peso");
        ref.setValue(weight).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateApp();
                weightChartConfiguration(StadisticsFragment.getBinding());
            }
        });
    }
    /**
     * Recoge una lista de nombres, los separa en un array por segmentos e introduce el primer
     * segmento en una lista nueva
     * @param nombres Lista de nombres introducida
     * @return Lista de la primera palabra de cada nombre.
     */
    public static List<String> recortarNombres(List<String> nombres){
        List<String> nombresCortos = new ArrayList<>();
        for (int i = 0; i < nombres.size(); i++) {
            if(nombres.get(i).split(" ").length>0){
                String[] arrayNombres = nombres.get(i).split(" ");
                nombresCortos.add(arrayNombres[0]);
            }
        }
        return nombresCortos;
    }

    // ESTADÍSTICAS**********************************ESTADÍSTICAS**********************************

    // ACTIVIDADES**********************************ACTIVIDADES**********************************
    public static void loadActivity(FragmentActivitiesDetailsBinding binding, Context context, Activity activity){
        if (activity.getDays().contains("l")) {
            binding.monday.setTextColor(Color.rgb(255, 127, 39));
        }
        if (activity.getDays().contains("m")) {
            binding.tuesday.setTextColor(Color.rgb(255, 127, 39));
        }
        if (activity.getDays().contains("x")) {
            binding.wednesday.setTextColor(Color.rgb(255, 127, 39));
        }
        if (activity.getDays().contains("j")) {
            binding.thursday.setTextColor(Color.rgb(255, 127, 39));
        }
        if (activity.getDays().contains("v")) {
            binding.friday.setTextColor(Color.rgb(255, 127, 39));
        }
        if (activity.getDays().contains("s")) {
            binding.saturday.setTextColor(Color.rgb(255, 127, 39));
        }
        if (activity.getDays().contains("d")) {
            binding.sunday.setTextColor(Color.rgb(255, 127, 39));
        }
        Glide.with(context)
                .load(activity.getImg2())
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .error(R.drawable.logo)//si ocurre algún error se verá por defecto
                .fitCenter()
                .override(1000)
                .into(binding.activityImg);
        binding.activityName.setText(activity.getName());
        binding.activityDescription.append(activity.getDescription());
        binding.activitySchedules.append(activity.getSchedule());
        binding.activityTeacher.append(activity.getTeacher());
        binding.activityVacancies.append(activity.getVacancies());
        binding.activityPrices.append(activity.getPrice());
    }
    public static void bookActivity(Activity activity, Context context){
        //si hay vacantes
        //Obtengo la fecha de hoy
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        String fecha = date.getYear() +"/"+ date.getMonth()+"/"+ date.getDate()+"";
        int vacantes = Integer.parseInt(activity.getVacancies())-1;
        activity.setVacancies(String.valueOf(vacantes));
        activity.setDate(fecha);
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUserOB().getId()+"/actividades/"+activity.getName());
        ref.setValue(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                escribirToast("Actividad reservada", context);
                updateVacancy(activity);
            }
        });
    }
    public static void deleteReservation(Activity activity, Context context){
        int vacancies = Integer.parseInt(activity.getVacancies())+1;
        activity.setVacancies(String.valueOf(vacancies));
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUserOB().getId()+"/actividades/"+activity.getName());
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                escribirToast("Reserva eliminada", context);
                updateVacancy(activity);
            }
        });
    }
    public static void updateVacancy(Activity activity){
        DatabaseReference ref1 = FirebaseDatabase
                .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("actividades/"+activity.getName());
        ref1.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DatabaseReference ref2 = FirebaseDatabase
                        .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                        .getReference("actividades/"+activity.getName());
                ref2.setValue(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateApp();
                    }
                });
            }
        });
    }

    // ACTIVIDADES**********************************ACTIVIDADES**********************************

    // CALENDARIO**********************************CALENDARIO************************************
    private static Map<Integer, Object>[] arr = new Map[2];
    private static List<String> deLunesADomingo = new ArrayList();
    /**
     * devuelve una lista del tamaño de los dias que tenga el mes en cuestion. En vez de contener los números del día
     * contendrá el día de la semana en la posición del día
     * @param calendar
     * @return
     */
    public static List<String> daysPerWeekList(Calendar calendar){//listaDeDiasPorSemana
        String somethingWithDays = somethingWithDays(calendar);
        int monthDays = calendar.getActualMaximum(5);
        String[] daysArray = {"l", "m", "x", "j", "v", "s", "d"};
        int diaIndex = 0;
        List<String> diasEnDiasSemanas = new ArrayList<>();
        //se recorre el array de dias
        for (int i = 0; i < daysArray.length; i++) {
            //si el dia de hoy coincide con uno de la lista
            if(daysArray[i].equals(somethingWithDays)){
                //se obtiene el índice en la lista de diasArray[]
                diaIndex = i;
            }
        }
        //
        for (int i = 0; i < monthDays; i++) {
            diasEnDiasSemanas.add(daysArray[diaIndex]);
            diaIndex++;
            if(diaIndex>daysArray.length-1){
                diaIndex=0;
            }
        }
        deLunesADomingo = diasEnDiasSemanas;

        return diasEnDiasSemanas;
    }
    /**
     * devuelve el dia de la semana del primer dia del mes
     * @param calendar
     * @return
     */
    public static String somethingWithDays(Calendar calendar){//algoConLosDias
        String daysToWeek = daysToWeekDays(calendar);
        String[] diasArray = {"l", "m", "x", "j", "v", "s", "d"};
        String days = "";
        int dayIndex = 0;
        int dayFlag = 0;
        int dayOfTheMonth = calendar.get(5);
        //se recorre el array de dias
        for (int i = 0; i < diasArray.length; i++) {
            //si el dia de hoy coincide con uno de la lista
            if(diasArray[i].equals(daysToWeek)){
                //se obtiene el índice en la lista que representa ese día
                dayIndex = i;
                dayFlag = dayIndex;
            }
        }

        for (int i = 0; i < dayOfTheMonth; i++) {
            //se recorre la lista de dias hacia atras
            days =  diasArray[dayFlag];
            dayFlag--;

            //si el indice de la lista de dias llega a cero, se reasigna la variable para evitar desbordamiento
            if(dayFlag<0 && i<dayOfTheMonth){
                dayFlag = diasArray.length-1;
                days =  diasArray[0];
            }
        }
        return days;
    }
    public static String daysToWeekDays(Calendar calendar){//convertirDiaSemana
        switch(calendar.get(7)) {
            case 1:
                return "d";
            case 2:
                return "l";
            case 3:
                return "m";
            case 4:
                return "x";
            case 5:
                return "j";
            case 6:
                return "v";
            case 7:
                return "s";
            default:
                return null;
        }
    }
    public static void arrConfiguration(Calendar calendar){//configurarArr
        List<String> listaDiasSemanas = daysPerWeekList(calendar);
        for (int i = 0; i < MainActivity.getRoutinesOBs().size(); i++) {
            for (int j = 0; j < listaDiasSemanas.size(); j++) {
                if(MainActivity.getRoutinesOBs().get(i).getDays().contains(listaDiasSemanas.get(j))){
                    arr[0].put(j+1, "rutina");
                }
            }
        }
        for (int i = 0; i < MainActivity.getActivitiesOBs().size(); i++) {
            for (int j = 0; j < listaDiasSemanas.size(); j++) {
                if(MainActivity.getActivitiesOBs().get(i).getDays().contains(listaDiasSemanas.get(j))){
                    if(arr[0].containsKey(j+1)){
                        arr[0].put(j+1, "mix");
                    }else{
                        arr[0].put(j+1, "actividad");
                    }
                }
            }
        }
    }
    public static void updateArr(Calendar calendar){
        arr[0].clear();
        List<String> listaDiasSemanas = daysPerWeekList(calendar);
        for (int i = 0; i < MainActivity.getRoutinesOBs().size(); i++) {
            for (int j = 0; j < listaDiasSemanas.size(); j++) {
                if(MainActivity.getRoutinesOBs().get(i).getDays().contains(listaDiasSemanas.get(j))){
                    arr[0].put(j+1, "rutina");
                }
            }
        }
        for (int i = 0; i < MainActivity.getActivitiesOBs().size(); i++) {
            for (int j = 0; j < listaDiasSemanas.size(); j++) {

                if(MainActivity.getActivitiesOBs().get(i).getDays().contains(listaDiasSemanas.get(j))){
                    if(arr[0].containsKey(j+1)){
                        arr[0].put(j+1, "mix");
                    }else{
                        arr[0].put(j+1, "actividad");
                    }
                }
            }
        }
    }
    public static void cargarCalendario(CustomCalendar customCalendar, Context context, OnNavigationButtonClickedListener onbcl){
        arr[0] = new HashMap<>();
        HashMap<Object, Property> mapDescToProp = new HashMap<>();
        Property propDefault = new Property();
        propDefault.layoutResource = R.layout.cal_default_view;
        propDefault.dateTextViewResource = R.id.textView;
        mapDescToProp.put("default", propDefault);

        Property propVarios = new Property();
        propVarios.layoutResource = R.layout.cal_mix_view;
        propVarios.dateTextViewResource = R.id.textView;
        mapDescToProp.put("mix", propVarios);

        Property propActividad = new Property();
        propActividad.layoutResource = R.layout.cal_activity_view;
        propActividad.dateTextViewResource = R.id.textView;
        mapDescToProp.put("actividad", propActividad);

        Property propRutina = new Property();
        propRutina.layoutResource = R.layout.cal_routine_view;
        propRutina.dateTextViewResource = R.id.textView;
        mapDescToProp.put("rutina", propRutina);

        customCalendar.setMapDescToProp(mapDescToProp);
        Calendar calendar = Calendar.getInstance();
        arrConfiguration(calendar);

        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, onbcl);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, onbcl);
        customCalendar.setDate(calendar, arr[0]);
        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                todayIsTheDay(selectedDate);
            }
        });
    }
    public static Map<Integer, Object>[] getArr(){
        return arr;
    }

    /**
     * Obtiene las actividades y rutinas del usuario y las mete en una lista como eventos en común
     * @return List<Evento> eventos
     */
    public static List<Event> getObjectos(){
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < MainActivity.getActivitiesOBs().size(); i++) {
            Event event = new Event();
            event.setDias(MainActivity.getActivitiesOBs().get(i).getDays());
            event.setHorario(MainActivity.getActivitiesOBs().get(i).getSchedule());
            event.setNombre(MainActivity.getActivitiesOBs().get(i).getName());
            event.setImg2(MainActivity.getActivitiesOBs().get(i).getImg2());
            events.add(event);
        }
        for (int i = 0; i < MainActivity.getRoutinesOBs().size(); i++) {
            Event event = new Event();
            event.setDias(MainActivity.getRoutinesOBs().get(i).getDays());
            event.setNombre(MainActivity.getRoutinesOBs().get(i).getName());
            event.setImg2(MainActivity.getRoutinesOBs().get(i).getImage());
            events.add(event);
        }
        return events;
    }

    /**
     * Muestra en la lista de eventos las rutinas y actividades programadas para el día de hoy.
     * @param selectedDate
     */
    public static void todayIsTheDay(Calendar selectedDate){
        List<Event> eventsList = new ArrayList<>();
        List<Event> events = getObjectos();
        for (int i = 0; i < events.size(); i++) {
            if(events.get(i).getDias().contains(deLunesADomingo.get(selectedDate.get(Calendar.DAY_OF_MONTH)-1))){
                eventsList.add(events.get(i));
            }
        }
        CalendarFragment.setRecyclerView(eventsList);
    }

    // CALENDARIO**********************************CALENDARIO*********************************FIN

    // PERFIL****************************************PERFIL**************************************
    @SuppressLint("SetTextI18n")
    public static void cargaPerfil(FragmentProfileBinding binding, Context context){
        binding.nombre.setText(MainActivity.getUserOB().getName());
        binding.mayorpesolevantado.setText(calcularMxPesoLevantado(MainActivity.getAdvanceOB().getWeights()));
        binding.kilosyfechadePesolevantado.setText(MainActivity.getWeightOB().getWeightData().get(MainActivity.getWeightOB().getWeightData().size()-1).get("y")+ "Kg - " +
                MainActivity.getWeightOB().getDate().get(MainActivity.getWeightOB().getDate().size()-1));
        Glide.with(context)
                .load(MainActivity.getUserOB().getImage())
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .error(R.drawable.logo)//si ocurre algún error se verá por defecto
                .fitCenter()
                .override(1000)
                .into(binding.img);
    }
    public static void cambiarDatos(EditText name, EditText oldPassword, EditText newPassword, Context context){
        if(!name.getText().toString().equals("")){
            User user = MainActivity.getUserOB();
            user.setName(name.getText().toString());
            user.setUser(encript(user.getName(), user.getName()));
            DatabaseReference ref = FirebaseDatabase
                    .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("usuarios/"+MainActivity.getUserOB().getId());
            ref.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    System.out.println("Guardando actividades");
                    List<Activity> activities = MainActivity.getActivitiesOBs();
                    for (int i = 0; i < activities.size(); i++) {
                        DatabaseReference ref = FirebaseDatabase
                                .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                                .getReference("usuarios/"+MainActivity.getUserOB().getId()+"/actividades/"+activities.get(i).getName());
                        ref.setValue(activities.get(i)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                System.out.println("Actializando App");
                                updateApp();
                            }
                        });
                    }
                    cargaPerfil(ProfileFragment.getPerfilBinding(), context);
                }
            });
        }
        if(!newPassword.getText().toString().equals("")){
            if(!oldPassword.getText().toString().equals("")){
                User user = MainActivity.getUserOB();
                //abtener clave, codificarla y compararla
                String passAntiguoEncriptado = encript(oldPassword.getText().toString(), oldPassword.getText().toString());
                if(passAntiguoEncriptado.equals(user.getPassword())){
                    String newEncriptPassword = encript(newPassword.getText().toString(),newPassword.getText().toString());
                    user.setPassword(newEncriptPassword);
                    DatabaseReference ref = FirebaseDatabase
                            .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                            .getReference("usuarios/"+MainActivity.getUserOB().getId());
                    ref.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            List<Activity> activities = MainActivity.getActivitiesOBs();
                            for (int i = 0; i < activities.size(); i++) {
                                DatabaseReference ref = FirebaseDatabase
                                        .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                                        .getReference("usuarios/"+MainActivity.getUserOB().getId()+"/actividades/"+activities.get(i).getName());
                                ref.setValue(activities.get(i)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        updateApp();
                                    }
                                });
                            }
                            cargaPerfil(ProfileFragment.getPerfilBinding(), context);
                        }
                    });
                }else{
                    escribirToast("La clave antigua no coincide.", context);
                }
            }else{
                escribirToast("La clave antigua no puede estar vacía.", context);
            }
        }
    }
    public static String calcularMxPesoLevantado(List<String> lista){
        float max = 0;
        if(!lista.isEmpty()){
            max = Float.valueOf(lista.get(0));
            int index = 0;
            for (int i = 0; i < lista.size(); i++) {
                if(Float.valueOf(lista.get(i))>max){
                    max = Float.valueOf(lista.get(i));
                    index = i;
                }
            }
            return max + " Kg - " + MainActivity.getAdvanceOB().getExercisesName().get(index);
        }
        return "0";
    }
    public static void pasarActividadAMapa(){
        List<Activity> activities = MainActivity.getActivitiesOBs();
        for (int i = 0; i < MainActivity.getActivitiesOBs().size(); i++) {
            Map<String, Object> activity = new HashMap<>();
            //nombre, precio, descripcion, vacantes, profesor, horario, img1, img2, fecha;
            activity.put("name", activities.get(i).getName());
            activity.put("price", activities.get(i).getPrice());
            activity.put("description", activities.get(i).getDescription());
            activity.put("vacancies", activities.get(i).getVacancies());
            activity.put("teacher", activities.get(i).getTeacher());
            activity.put("schedule", activities.get(i).getSchedule());
            activity.put("img1", activities.get(i).getImg1());
            activity.put("img2", activities.get(i).getImg2());
            activity.put("date", activities.get(i).getDate());
        }
    }
    // PERFIL****************************************PERFIL***********************************FIN


    // TABLON****************************************TABLON**************************************
    public static void cargarNoticia(FragmentNewsDetailBinding binding, Context context, News news){
        Glide.with(context)
                .load(news.getImagen())
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .error(R.drawable.logo)//si ocurre algún error se verá por defecto
                .fitCenter()
                .override(1000)
                .into(binding.imageView);
        binding.titulo.setText(news.getTitulo());
        binding.contenido.setText(news.getContenido());
    }

    // TABLON****************************************TABLON***********************************FIN




    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        Date fecha = new Date();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title" + fecha.toString(), null);
        return Uri.parse(path);
    }
    /**
     * Inserta un toast con el texto deseado.
     * @param texto Texto introducido por el usuario
     * @param context Contexto del fragmento o actividad donde debe visualizarse
     */
    public static void escribirToast(String texto, Context context){
        Toast.makeText(context, texto, Toast.LENGTH_LONG).show();
    }
    /**
     * Actualiza los objetos usuario y peso de la aplicación con los de FireBase.
     * Cuando se actualizan se vuelve a cargar el gráfico
     */
    public static void updateApp(){//actualizarApp
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+MainActivity.getUserOB().getId());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //actualizo el usuario
                MainActivity.setUserOB(dataSnapshot.getValue(User.class));

                //actualizo el peso
                Weight weight = dataSnapshot.child("peso")
                        .getValue(Weight.class)==null ? new Weight() : dataSnapshot.child("peso").getValue(Weight.class);
                MainActivity.setWeightOB(weight);
                //actualizo el progreso
                Advance advance = dataSnapshot.child("avance")
                        .getValue(Advance.class)==null ? new Advance() : dataSnapshot.child("avance").getValue(Advance.class);
                MainActivity.setAvanceOB(advance);
                //actualizo las actividades
                List<Activity> activities = new ArrayList<>();
                if(dataSnapshot.child("actividades").getValue()!=null){
                    for (DataSnapshot data: dataSnapshot.child("actividades").getChildren()) {
                        Activity activity = data.getValue(Activity.class);
                        activities.add(activity);
                    }
                }
                MainActivity.setActivitiesOBs(activities);
                //actualizo las rutinas
                List<Routine> routines = new ArrayList<>();
                if(dataSnapshot.child("rutinas").getValue()!=null){
                    for (DataSnapshot data2: dataSnapshot.child("rutinas").getChildren()) {
                        Routine rutina = data2.getValue(Routine.class);
                        routines.add(rutina);
                    }
                }
                MainActivity.setRoutinesOBs(routines);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
    public static void cambiarToolbarText(String titulo){
        MainMenu.getBinding().toolbar.setTitle(titulo);
    }

    /**
     * Recoge una uri, que puede provenir de capturar una imagen de la camara o de la galeria
     * del dispositivo y la Uri donde está alojada en el Storage de Firabase.
     * @param uri uri de la imagen capturada
     * @return imgUri Ruta donde se aloja la imagen en el Storage de Firebase
     */
    public static Uri saveUserAvatar(Uri uri){//guardarImagenUserAvatar
        final Uri[] imgUri = {Uri.parse(" ")};
        //creamos una referencia en el Store que será el nombre de la imagen
        String[] titulo = String.valueOf(uri).split("/");
        storageReference = FirebaseStorage.getInstance().getReference(titulo[titulo.length-1]);
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //si no hay problemas durante el proceso obtenemos el link de la imagen en el Store
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlimagen = uriTask.getResult();
                imgUri[0] = urlimagen;
                User user = MainActivity.getUserOB();
                if(user.getImage()!=null){
                    eliminarImagen(user.getImage());
                }else{
                }
                user.setImage(imgUri[0].toString());
                DatabaseReference ref = FirebaseDatabase
                        .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                        .getReference("usuarios/"+MainActivity.getUserOB().getId());
                ref.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateApp();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e);
            }
        });
        return imgUri[0];
    }
    public static void eliminarImagen(String imagen){
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(imagen);
        ref.delete();
    }

    //HOTFIXES********************************************************
    public static String nextID = "";
/*
    public static void hotFixCrearUsuario(String nombre, String clave){
        //encripto clave y nombre
        String nombreEncriptado = encriptar(nombre, nombre);
        String claveEncriptada = encriptar(clave, clave);
        //Obtengo la fecha de hoy
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        String fecha = date.getDate() +"/"+ date.getMonth();
        //creo un id
        nextID = encriptar(nombre+clave, String.valueOf(date));
        nextID = nextID.substring(0, 12);
        //creo un mapa que guarde los ejes
        Map<String, String> datosPeso = new HashMap<>();
        datosPeso.put("x", String.valueOf(0));
        datosPeso.put("y", String.valueOf(0.0));
        Peso peso = new Peso();peso.getDatosPeso().add(datosPeso);
        peso.getFecha().add(fecha);
        //creo el usuario
        Usuario usuario = new Usuario();usuario.setUsuario(nombreEncriptado);
        usuario.setClave(claveEncriptada);usuario.setId(nextID);
        usuario.setNombre(nombre);
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+nextID);
        ref.setValue(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                ref.child("peso").setValue(peso);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e);
            }
        });
    }
    public static void hotFixAvtividad(){
        Actividad actividad = new Actividad("Esgrima", "20.00€",
                " es un deporte de combate en el que se enfrentan dos contrincantes debidamente protegidos que deben intentar tocarse con un arma blanca, en función de la cual se diferencian tres modalidades: sable, espada y florete.",
                "10", "Cirano de Vergerac", "De 8:30 a 9:30");
        actividad.setImg1("https://firebasestorage.googleapis.com/v0/b/olimplicacion-3ba86.appspot.com/o/2131165449?alt=media&token=212d3b2f-e1f8-4876-b1ee-42008c1cda5a");
        actividad.setImg2("https://firebasestorage.googleapis.com/v0/b/olimplicacion-3ba86.appspot.com/o/2131165448?alt=media&token=2db03e43-bd07-4aaf-bf29-f9360660280a");
        List<String> dias = new ArrayList<>();dias.add("m");dias.add("j");
        actividad.setDias(dias);
        Map<String, Object> actividadMapa = new HashMap<>();
        actividadMapa.put("esgrima", actividad);
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("actividades");
        ref.updateChildren(actividadMapa).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Actividad actividad = new Actividad("Kárate", "15.00€",
                        " es un arte marcial tradicional basada en algunos estilos de las artes marciales chinas y en otras disciplinas provenientes de Okinawa. A la persona que lo practica se la llama karateca.",
                        "15", "Andrés", "De 6:30 a 7:30");
                actividad.setImg1("https://firebasestorage.googleapis.com/v0/b/olimplicacion-3ba86.appspot.com/o/2131165450?alt=media&token=6cfbbc04-9029-41bc-987b-59e08fc92cd6");
                actividad.setImg2("https://firebasestorage.googleapis.com/v0/b/olimplicacion-3ba86.appspot.com/o/2131165452?alt=media&token=89be4d90-e92b-4a7f-abcc-9c975517b76a");
                List<String> dias = new ArrayList<>();dias.add("m");dias.add("j");dias.add("s");
                actividad.setDias(dias);
                Map<String, Object> actividadMapa = new HashMap<>();
                actividadMapa.put("karate", actividad);
                ref.updateChildren(actividadMapa).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Actividad actividad = new Actividad("Zumba", "10.00€",
                                " es una disciplina enfocada por una parte a mantener un cuerpo saludable y por otra a desarrollar, fortalecer y dar flexibilidad al cuerpo mediante movimientos de baile combinados con una serie de rutinas aeróbicas.",
                                "15", "Señora Profesora", "De 7:30 a 8:30");
                        actividad.setImg1("https://firebasestorage.googleapis.com/v0/b/olimplicacion-3ba86.appspot.com/o/2131165449?alt=media&token=212d3b2f-e1f8-4876-b1ee-42008c1cda5a");
                        actividad.setImg2("https://firebasestorage.googleapis.com/v0/b/olimplicacion-3ba86.appspot.com/o/2131165448?alt=media&token=2db03e43-bd07-4aaf-bf29-f9360660280a");
                        List<String> dias = new ArrayList<>();dias.add("l");dias.add("x");dias.add("v");
                        actividad.setDias(dias);
                        Map<String, Object> actividadMapa = new HashMap<>();
                        actividadMapa.put("zumba", actividad);
                        ref.updateChildren(actividadMapa);
                    }
                });
            }
        });
    }

    public static void hotFixNoticia(){
        String base = "https://firebasestorage.googleapis.com/v0/b/olimplicacion-3ba86.appspot.com/o/";
        Noticia noticia = new Noticia(base + "organizate.png?alt=media&token=ea950b78-5d8a-4aba-83e2-5c4968a694b4"
                ,"Organizate", "-Organiza tu semana.", "Organiza tu semana siguiendo los siguientes consejos:"
                +"1: no dejes que te venza la apatía. 2: descansa tus 8h al día. 3: evita las grasas innecesarias. 4: ten dinero.");
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("noticias/" + noticia.getTituloTrans());
        ref.setValue(noticia);
    }
    public static void hotFixPesos(){
        Map<String, String> datos = new HashMap<>(); datos.put("x", "0.0");datos.put("y", "0.0");
        List<Map<String, String>> datosPeso = new ArrayList<>();
        datosPeso.add(datos);
        List<String> fechas = new ArrayList<>();fechas.add("1/12");
        Peso peso = new Peso(datosPeso, fechas, "0.0");
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUsuarioOB().getId()+"/peso");
        ref.setValue(peso);
    }
    public static void hotFixImagen(Context context, int drawable){//int corresponde a un drawable
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), drawable);
        Uri imgUri = getImageUri(context, bm);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(String.valueOf(drawable));
        storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //si no hay problemas durante el proceso obtenemos el link de la imagen en el Store
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlimagen = uriTask.getResult();
                imgUriFb = urlimagen;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e);
            }
        });
    }
    public static void hotFixCrearEjercicio (String nombre, String categoria,
                                             String descripcion, String imagen, String musculos) {
        //accedo a la lista de ejercicios
        DatabaseReference ref = FirebaseDatabase.getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("ejercicios");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//dataSnapshot son todos los usuarios
                int count = 0;
                for (DataSnapshot data: dataSnapshot.getChildren()) {//
                    //obtengo el número de ejercicios
                    count++;
                }
                System.out.println("hay :" + count);
                Ejercicio ejercicio = new Ejercicio();
                ejercicio.setId(count+1);ejercicio.setNombre(nombre);ejercicio.setCategoria(categoria);
                ejercicio.setDescripcion(descripcion);ejercicio.setImg(imagen);ejercicio.setMusculos(musculos);
                DatabaseReference ref2 = FirebaseDatabase.getInstance("https://gymgameproject-default-rtdb.europe-west1.firebasedatabase.app")
                        .getReference("ejercicios/"+(count));
                ref2.setValue(ejercicio).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("Añadido: " + ejercicio);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println(error);
            }
        });
    }
*/
}
