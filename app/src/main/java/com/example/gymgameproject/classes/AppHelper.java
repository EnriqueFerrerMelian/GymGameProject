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
    //SEGURIDAD****************************************************************

    private static final String algoritmoSks = "AES";
    private static final String algoritmoSha = "SHA-256";//algoritmo de hal seguro
    private static final String charSet = "UTF-8";//codificacion de texto

    /**
     * Cifra la clave usando una llave de codificación
     * @param clave
     * @param password
     * @return String
     */
    public static String encriptar(String clave, String password){
        try{
            SecretKeySpec secretKeySpec = generateKey(clave);//crea una llave de encriptacion
            Cipher cipher = Cipher.getInstance(algoritmoSks);
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
            MessageDigest sha = MessageDigest.getInstance(algoritmoSha);
            byte[] key = clave.getBytes(charSet);
            key = sha.digest(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, algoritmoSks);
            return secretKeySpec;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    //SEGURIDAD*************************************************************FIN

    // ESTADÍSTICAS**********************************ESTADÍSTICAS**********************************
    //TODOS LOS MÉTODOS DEL FRAGMENTO ESTADISTICAS AQUÍ
    /**
     * Actualiza los datos del peso, que luego se pasarán
     * a la base de datos.
     * @param peso Peso seleccionado, transformado en string.
     * @return Objeto Peso con los datos actualizados
     */
    public static Peso addDatosPeso(String peso){
        //Obtengo la fecha de hoy
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        String fecha = date.getDate() +"/"+ date.getMonth();

        //creo un mapa que guarde los ejes
        Map<String, String> datosPeso = new HashMap<>();
        datosPeso.put("x", String.valueOf(MainActivity.getPesoOB().getDatosPeso().size()+1));
        datosPeso.put("y", peso);
        MainActivity.getPesoOB().getFecha().add(fecha);
        MainActivity.getPesoOB().getDatosPeso().add(datosPeso);

        return MainActivity.getPesoOB();
    }
    public static Peso addDatosObjetivo(String objetivo){
        MainActivity.getPesoOB().setObjetivo(objetivo);
        return MainActivity.getPesoOB();
    }

    /**
     * Configura la apariencia por defecto del chart y carga los datos del objeto Peso
     */
    public static void configurarChartPeso(FragmentEstadisticasBinding binding){
        float maxView = 0;float minView = 0;

        //borrando bordes
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
        lEntry1.label = "Peso";
        legendEntry[0] = lEntry1;
        LegendEntry lEntry2 = new LegendEntry();
        lEntry2.formColor = Color.rgb(255,135,0);
        lEntry2.label = "Objetivo";
        legendEntry[1] = lEntry2;
        l.setCustom(legendEntry);

        binding.lineChart.canScrollHorizontally(1);
        binding.lineChart.setVisibleXRangeMaximum(7f);
        binding.lineChart.setNoDataText("No se ha guardado ningún dato.");

        //>>>****INSERCIÓN DE DATOS********
        //insertando fechas en eje X
        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final List<String> fechas = MainActivity.getPesoOB().getFecha();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(fechas));

        //inserción de entradas
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < MainActivity.getPesoOB().getDatosPeso().size(); i++) {
            entries.add(new Entry((float) i,Float.parseFloat(Objects.requireNonNull(MainActivity.getPesoOB().getDatosPeso().get(i).get("y")))));
            if(Float.parseFloat(Objects.requireNonNull(MainActivity.getPesoOB().getDatosPeso().get(i).get("y")))>maxView){
                maxView = Float.parseFloat(Objects.requireNonNull(MainActivity.getPesoOB().getDatosPeso().get(i).get("y")));
            }
            if(Float.parseFloat(Objects.requireNonNull(MainActivity.getPesoOB().getDatosPeso().get(i).get("y")))<minView){
                minView = Float.parseFloat(Objects.requireNonNull(MainActivity.getPesoOB().getDatosPeso().get(i).get("y")));
            }
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "Peso");
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setCircleColor(Color.GREEN); // color for highlight indicator
        lineDataSet.setCircleRadius(6); // color for highlight indicator
        lineDataSet.setColor(Color.GREEN);
        lineDataSet.setValueTextSize(15);
        lineDataSet.setLineWidth(3);
        LineData lineData = new LineData(lineDataSet);
        if(MainActivity.getPesoOB().getDatosPeso().size()>0){
            binding.ultimoPeso.setText(MainActivity.getPesoOB().getDatosPeso().get(MainActivity.getPesoOB().getDatosPeso().size()-1).get("y") + " Kgs");
        }
        //>>>****INSERCIÓN DE DATOS*****FIN

        //si se ha seleccionado una marca de objetivo
        if(MainActivity.getPesoOB().getObjetivo()!=null){
            if(Float.parseFloat(MainActivity.getPesoOB().getObjetivo())>0){
                float leyendVal =Float.parseFloat(MainActivity.getPesoOB().getObjetivo());
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

    public static void configurarChartAvance(FragmentEstadisticasBinding binding){
        //borrando bordes
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

        //>>>****INSERCIÓN DE DATOS********
        //insertando nombres en eje X
        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(recortarNombres(MainActivity.getAvanceOB().getEjerciciosNombres())));

        //inserción de entradas
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < MainActivity.getAvanceOB().getPesos().size(); i++) {//pesos de los ejercicios, array de Strings
            entries.add(new BarEntry(Float.valueOf(i),Float.valueOf(MainActivity.getAvanceOB().getPesos().get(i))));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Avance");
        barDataSet.setColor(Color.rgb(255,127,39));
        barDataSet.setValueTextSize(15);
        BarData barData = new BarData(barDataSet);
        String ultimoValorIntroducido = MainActivity.getAvanceOB().getEjerciciosNombres()
                .isEmpty() ? " " : MainActivity.getAvanceOB()
                .getEjerciciosNombres().get(MainActivity.getAvanceOB().getEjerciciosNombres().size()-1);
        binding.ultimoProgreso.setText(ultimoValorIntroducido);
        //>>>****INSERCIÓN DE DATOS*****fin
        binding.barChart.canScrollHorizontally(1);

        binding.barChart.setNoDataText("No se ha guardado ningún dato.");
        binding.barChart.moveViewToX(entries.size()-1);
        binding.barChart.setData(barData);
        binding.barChart.invalidate();
        binding.barChart.setVisibleXRangeMaximum(5);
    }

    public static void actualizarAvance(Avance avance){
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUsuarioOB().getId()+"/avance");
        ref.setValue(avance).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                actualizarApp();
                configurarChartAvance(EstadisticasFragment.getBinding());
            }
        });
    }
    /**
     * Actualiza los datos del peso del usuario en Firebase.
     * @param peso
     */
    public static void actualizarPeso(Peso peso){
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUsuarioOB().getId()+"/peso");
        ref.setValue(peso).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                actualizarApp();
                configurarChartPeso(EstadisticasFragment.getBinding());
            }
        });
    }
    public static void actualizarObjetivo(Peso peso){
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUsuarioOB().getId()+"/peso");
        ref.setValue(peso).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                actualizarApp();
                configurarChartPeso(EstadisticasFragment.getBinding());
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
    public static void cargarActividad(FragmentDetallesActividadBinding binding, Context context, Actividad actividad){
        if (actividad.getDias().contains("l")) {
            binding.lunes.setTextColor(Color.rgb(255, 127, 39));
        }
        if (actividad.getDias().contains("m")) {
            binding.martes.setTextColor(Color.rgb(255, 127, 39));
        }
        if (actividad.getDias().contains("x")) {
            binding.miercoles.setTextColor(Color.rgb(255, 127, 39));
        }
        if (actividad.getDias().contains("j")) {
            binding.jueves.setTextColor(Color.rgb(255, 127, 39));
        }
        if (actividad.getDias().contains("v")) {
            binding.viernes.setTextColor(Color.rgb(255, 127, 39));
        }
        if (actividad.getDias().contains("s")) {
            binding.sabado.setTextColor(Color.rgb(255, 127, 39));
        }
        if (actividad.getDias().contains("d")) {
            binding.domingo.setTextColor(Color.rgb(255, 127, 39));
        }
        Glide.with(context)
                .load(actividad.getImg2())
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .error(R.drawable.logo)//si ocurre algún error se verá por defecto
                .fitCenter()
                .override(1000)
                .into(binding.imagenActividad);
        binding.nombreActividad.setText(actividad.getNombre());
        binding.descripcionActividad.append(actividad.getDescripcion());
        binding.profesorActividad.append(actividad.getProfesor());
        binding.horariosActividad.append(actividad.getHorario());
        binding.vacantesActividad.append(actividad.getVacantes());
        binding.precioActividad.append(actividad.getPrecio());
    }
    public static void reservarActividad(Actividad actividad, Context context){
        //si hay vacantes
        //Obtengo la fecha de hoy
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        String fecha = date.getYear() +"/"+ date.getMonth()+"/"+ date.getDate()+"";
        int vacantes = Integer.parseInt(actividad.getVacantes())-1;
        actividad.setVacantes(String.valueOf(vacantes));
        actividad.setFecha(fecha);
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUsuarioOB().getId()+"/actividades/"+actividad.getNombre());
        ref.setValue(actividad).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                escribirToast("Actividad reservada", context);
                actualizarVacante(actividad);
            }
        });
    }
    public static void eliminarReserva(Actividad actividad, Context context){
        int vacantes = Integer.parseInt(actividad.getVacantes())+1;
        actividad.setVacantes(String.valueOf(vacantes));
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUsuarioOB().getId()+"/actividades/"+actividad.getNombre());
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                escribirToast("Reserva eliminada", context);
                actualizarVacante(actividad);
            }
        });
    }
    public static void actualizarVacante(Actividad actividad){
        //.out.println("actualizarVacante()");
        DatabaseReference ref1 = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("actividades/"+actividad.getNombre());
        ref1.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DatabaseReference ref2 = FirebaseDatabase
                        .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                        .getReference("actividades/"+actividad.getNombre());
                ref2.setValue(actividad).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        actualizarApp();
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
    public static List<String> listaDeDiasPorSemanas(Calendar calendar){
        String algoConDias = algoConLosDias(calendar);
        int diasDelMes = calendar.getActualMaximum(5);
        String[] diasArray = {"l", "m", "x", "j", "v", "s", "d"};
        int diaIndex = 0;
        List<String> diasEnDiasSemanas = new ArrayList<>();
        //se recorre el array de dias
        for (int i = 0; i < diasArray.length; i++) {
            //si el dia de hoy coincide con uno de la lista
            if(diasArray[i].equals(algoConDias)){
                //se obtiene el índice en la lista de diasArray[]
                diaIndex = i;
            }
        }
        //
        for (int i = 0; i < diasDelMes; i++) {
            diasEnDiasSemanas.add(diasArray[diaIndex]);
            diaIndex++;
            if(diaIndex>diasArray.length-1){
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
    public static String algoConLosDias(Calendar calendar){
        String convertirDiaSemana = convertirDiaSemana(calendar);
        String[] diasArray = {"l", "m", "x", "j", "v", "s", "d"};
        String diasd = "";
        int diaIndex = 0;
        int diaBandera = 0;
        int diaDelMes = calendar.get(5);
        //se recorre el array de dias
        for (int i = 0; i < diasArray.length; i++) {
            //si el dia de hoy coincide con uno de la lista
            if(diasArray[i].equals(convertirDiaSemana)){
                //se obtiene el índice en la lista que representa ese día
                diaIndex = i;
                diaBandera = diaIndex;
            }
        }

        for (int i = 0; i < diaDelMes; i++) {
            //se recorre la lista de dias hacia atras
            diasd =  diasArray[diaBandera];
            diaBandera--;

            //si el indice de la lista de dias llega a cero, se reasigna la variable para evitar desbordamiento
            if(diaBandera<0 && i<diaDelMes){
                diaBandera = diasArray.length-1;
                diasd =  diasArray[0];
            }
        }
        return diasd;
    }
    public static String convertirDiaSemana(Calendar calendar){
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
    public static void configurarArr(Calendar calendar){
        List<String> listaDiasSemanas = listaDeDiasPorSemanas(calendar);
        for (int i = 0; i < MainActivity.getRutinasOBs().size(); i++) {
            for (int j = 0; j < listaDiasSemanas.size(); j++) {
                if(MainActivity.getRutinasOBs().get(i).getDias().contains(listaDiasSemanas.get(j))){
                    arr[0].put(j+1, "rutina");
                }
            }
        }
        for (int i = 0; i < MainActivity.getActividadesOBs().size(); i++) {
            for (int j = 0; j < listaDiasSemanas.size(); j++) {
                if(MainActivity.getActividadesOBs().get(i).getDias().contains(listaDiasSemanas.get(j))){
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
        List<String> listaDiasSemanas = listaDeDiasPorSemanas(calendar);
        for (int i = 0; i < MainActivity.getRutinasOBs().size(); i++) {
            for (int j = 0; j < listaDiasSemanas.size(); j++) {
                if(MainActivity.getRutinasOBs().get(i).getDias().contains(listaDiasSemanas.get(j))){
                    arr[0].put(j+1, "rutina");
                }
            }
        }
        for (int i = 0; i < MainActivity.getActividadesOBs().size(); i++) {
            for (int j = 0; j < listaDiasSemanas.size(); j++) {

                if(MainActivity.getActividadesOBs().get(i).getDias().contains(listaDiasSemanas.get(j))){
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
        propActividad.layoutResource = R.layout.cal_actividad_view;
        propActividad.dateTextViewResource = R.id.textView;
        mapDescToProp.put("actividad", propActividad);

        Property propRutina = new Property();
        propRutina.layoutResource = R.layout.cal_rutina_view;
        propRutina.dateTextViewResource = R.id.textView;
        mapDescToProp.put("rutina", propRutina);

        customCalendar.setMapDescToProp(mapDescToProp);
        Calendar calendar = Calendar.getInstance();
        configurarArr(calendar);

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
    public static List<Evento> getObjectos(){
        List<Evento> eventos = new ArrayList<>();
        for (int i = 0; i < MainActivity.getActividadesOBs().size(); i++) {
            Evento evento = new Evento();
            evento.setDias(MainActivity.getActividadesOBs().get(i).getDias());
            evento.setHorario(MainActivity.getActividadesOBs().get(i).getHorario());
            evento.setNombre(MainActivity.getActividadesOBs().get(i).getNombre());
            evento.setImg2(MainActivity.getActividadesOBs().get(i).getImg2());
            eventos.add(evento);
        }
        for (int i = 0; i < MainActivity.getRutinasOBs().size(); i++) {
            Evento evento = new Evento();
            evento.setDias(MainActivity.getRutinasOBs().get(i).getDias());
            evento.setNombre(MainActivity.getRutinasOBs().get(i).getNombre());
            evento.setImg2(MainActivity.getRutinasOBs().get(i).getImg());
            eventos.add(evento);
        }
        return eventos;
    }

    /**
     * Muestra en la lista de eventos las rutinas y actividades programadas para el día de hoy.
     * @param selectedDate
     */
    public static void todayIsTheDay(Calendar selectedDate){
        List<Evento> eventosList = new ArrayList<>();
        List<Evento> eventos = getObjectos();
        for (int i = 0; i < eventos.size(); i++) {
            if(eventos.get(i).getDias().contains(deLunesADomingo.get(selectedDate.get(Calendar.DAY_OF_MONTH)-1))){
                eventosList.add(eventos.get(i));
            }
        }
        CalendarioFragment.setRecyclerView(eventosList);
    }

    // CALENDARIO**********************************CALENDARIO*********************************FIN

    // PERFIL****************************************PERFIL**************************************
    @SuppressLint("SetTextI18n")
    public static void cargaPerfil(FragmentPerfilBinding binding, Context context){
        binding.nombre.setText(MainActivity.getUsuarioOB().getNombre());
        binding.mayorpesolevantado.setText(calcularMxPesoLevantado(MainActivity.getAvanceOB().getPesos()));
        binding.kilosyfechadePesolevantado.setText(MainActivity.getPesoOB().getDatosPeso().get(MainActivity.getPesoOB().getDatosPeso().size()-1).get("y")+ "Kg - " +
                MainActivity.getPesoOB().getFecha().get(MainActivity.getPesoOB().getFecha().size()-1));
        Glide.with(context)
                .load(MainActivity.getUsuarioOB().getImagen())
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .error(R.drawable.logo)//si ocurre algún error se verá por defecto
                .fitCenter()
                .override(1000)
                .into(binding.img);
    }
    public static void cambiarDatos(EditText nombre, EditText passAntiguo, EditText passNuevo, Context context){
        if(!nombre.getText().toString().equals("")){
            Usuario usuario = MainActivity.getUsuarioOB();
            usuario.setNombre(nombre.getText().toString());
            usuario.setUsuario(encriptar(usuario.getNombre(), usuario.getNombre()));
            DatabaseReference ref = FirebaseDatabase
                    .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("usuarios/"+MainActivity.getUsuarioOB().getId());
            ref.setValue(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    System.out.println("Guardando actividades");
                    List<Actividad> actividades = MainActivity.getActividadesOBs();
                    for (int i = 0; i < actividades.size(); i++) {
                        DatabaseReference ref = FirebaseDatabase
                                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                                .getReference("usuarios/"+MainActivity.getUsuarioOB().getId()+"/actividades/"+actividades.get(i).getNombre());
                        ref.setValue(actividades.get(i)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                System.out.println("Actializando App");
                                actualizarApp();
                            }
                        });
                    }
                    cargaPerfil(PerfilFragment.getPerfilBinding(), context);
                }
            });
        }
        if(!passNuevo.getText().toString().equals("")){
            if(!passAntiguo.getText().toString().equals("")){
                Usuario usuario = MainActivity.getUsuarioOB();
                //abtener clave, codificarla y compararla
                String passAntiguoEncriptado = encriptar(passAntiguo.getText().toString(), passAntiguo.getText().toString());
                if(passAntiguoEncriptado.equals(usuario.getClave())){
                    String passNuevoEncriptado = encriptar(passNuevo.getText().toString(),passNuevo.getText().toString());
                    usuario.setClave(passNuevoEncriptado);
                    DatabaseReference ref = FirebaseDatabase
                            .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                            .getReference("usuarios/"+MainActivity.getUsuarioOB().getId());
                    ref.setValue(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            List<Actividad> actividades = MainActivity.getActividadesOBs();
                            for (int i = 0; i < actividades.size(); i++) {
                                DatabaseReference ref = FirebaseDatabase
                                        .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                                        .getReference("usuarios/"+MainActivity.getUsuarioOB().getId()+"/actividades/"+actividades.get(i).getNombre());
                                ref.setValue(actividades.get(i)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        actualizarApp();
                                    }
                                });
                            }
                            cargaPerfil(PerfilFragment.getPerfilBinding(), context);
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
            return max + " Kg - " + MainActivity.getAvanceOB().getEjerciciosNombres().get(index);
        }
        return "0";
    }
    public static void pasarActividadAMapa(){
        List<Actividad> actividades = MainActivity.getActividadesOBs();
        for (int i = 0; i < MainActivity.getActividadesOBs().size(); i++) {
            Map<String, Object> actividad = new HashMap<>();
            //nombre, precio, descripcion, vacantes, profesor, horario, img1, img2, fecha;
            actividad.put("nombre", actividades.get(i).getNombre());
            actividad.put("precio", actividades.get(i).getPrecio());
            actividad.put("descripcion", actividades.get(i).getDescripcion());
            actividad.put("vacantes", actividades.get(i).getVacantes());
            actividad.put("profesor", actividades.get(i).getProfesor());
            actividad.put("horario", actividades.get(i).getHorario());
            actividad.put("img1", actividades.get(i).getImg1());
            actividad.put("img2", actividades.get(i).getImg2());
            actividad.put("fecha", actividades.get(i).getFecha());
        }
    }
    // PERFIL****************************************PERFIL***********************************FIN


    // TABLON****************************************TABLON**************************************
    public static void cargarNoticia(FragmentDetalleNoticiaBinding binding, Context context, Noticia noticia){
        Glide.with(context)
                .load(noticia.getImagen())
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .error(R.drawable.logo)//si ocurre algún error se verá por defecto
                .fitCenter()
                .override(1000)
                .into(binding.imageView);
        binding.titulo.setText(noticia.getTitulo());
        binding.contenido.setText(noticia.getContenido());
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
    public static void actualizarApp(){
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+MainActivity.getUsuarioOB().getId());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //actualizo el usuario
                MainActivity.setUsuarioOB(dataSnapshot.getValue(Usuario.class));

                //actualizo el peso
                Peso peso = dataSnapshot.child("peso")
                        .getValue(Peso.class)==null ? new Peso() : dataSnapshot.child("peso").getValue(Peso.class);
                MainActivity.setPesoOB(peso);
                //actualizo el progreso
                Avance avance = dataSnapshot.child("avance")
                        .getValue(Avance.class)==null ? new Avance() : dataSnapshot.child("avance").getValue(Avance.class);
                MainActivity.setAvanceOB(avance);
                //actualizo las actividades
                List<Actividad> actividades = new ArrayList<>();
                if(dataSnapshot.child("actividades").getValue()!=null){
                    for (DataSnapshot data: dataSnapshot.child("actividades").getChildren()) {
                        Actividad actividad = data.getValue(Actividad.class);
                        actividades.add(actividad);
                    }
                }
                MainActivity.setActividadesOBs(actividades);
                //actualizo las rutinas
                List<Rutina> rutinas = new ArrayList<>();
                if(dataSnapshot.child("rutinas").getValue()!=null){
                    for (DataSnapshot data2: dataSnapshot.child("rutinas").getChildren()) {
                        Rutina rutina = data2.getValue(Rutina.class);
                        rutinas.add(rutina);
                    }
                }
                MainActivity.setRutinasOBs(rutinas);
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
    public static void cambiarToolbarText(String titulo){
        MenuPrincipal.getBinding().toolbar.setTitle(titulo);
    }

    /**
     * Recoge una uri, que puede provenir de capturar una imagen de la camara o de la galeria
     * del dispositivo y la Uri donde está alojada en el Storage de Firabase.
     * @param uri uri de la imagen capturada
     * @return imgUri Ruta donde se aloja la imagen en el Storage de Firebase
     */
    public static Uri guardarImagenUserAvatar(Uri uri){
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
                Usuario usuario = MainActivity.getUsuarioOB();
                if(usuario.getImagen()!=null){
                    eliminarImagen(usuario.getImagen());
                }else{
                }
                usuario.setImagen(imgUri[0].toString());
                DatabaseReference ref = FirebaseDatabase
                        .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                        .getReference("usuarios/"+MainActivity.getUsuarioOB().getId());
                ref.setValue(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        actualizarApp();
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
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
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
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
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
        DatabaseReference ref = FirebaseDatabase.getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
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
                DatabaseReference ref2 = FirebaseDatabase.getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
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
}
