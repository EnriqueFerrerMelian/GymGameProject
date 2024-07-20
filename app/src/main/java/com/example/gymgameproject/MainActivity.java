package com.example.gymgameproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymgameproject.classes.Activity;
import com.example.gymgameproject.classes.Advance;
import com.example.gymgameproject.classes.AppHelper;
import com.example.gymgameproject.classes.Routine;
import com.example.gymgameproject.classes.User;
import com.example.gymgameproject.classes.Weight;
import com.example.gymgameproject.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * NOTA: Al registrarse en el gimnasio, el administrador dará una clave generada de forma aleatoria
 * con la que el usuario podrá entrar en la aplicación. En el apartado 'Configuración de perfil' se podrá cambiar.
 * Esto significa que no hace falta un fragmento de 'Registro'.
 */

public class MainActivity extends AppCompatActivity {
    private static User userOB = new User();
    private static Weight weightOb = new Weight();
    private static Advance advanceOB = new Advance();
    private static List<Activity> activitiesOBs = new ArrayList<>();
    private static List<Routine> routinesOBs = new ArrayList<>();
    private static Boolean confirmed = false;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseObject firstObject = new  ParseObject("FirstClass");
        firstObject.put("message","Hey ! First message from android. Parse is now connected");
        firstObject.saveInBackground(e -> {
            if (e != null){
                Log.e("MainActivity", e.getLocalizedMessage());
                    }else{
                    Log.d("MainActivity","Object saved.");
                }
            });
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        validarFechaActividad(this);
        //AppHelper.hotFixCrearUsuario("admin", "admin");
        /*AppHelper.hotFixCrearEjercicio("Curl femoral con maquina", "Máquina"
        ,"Doble las piernas hacia arriba lo más posible mientras exhala. Sostenga 1 seg. Vuelva a la posición inicial con un movimiento suave, evitando que los pesos en movimiento toquen el resto de la placas.",
                "https://firebasestorage.googleapis.com/v0/b/olimplicacion-3ba86.appspot.com/o/Curl%20femoral%20con%20maquina.png?alt=media&token=d81940ba-7855-4cf0-abaa-b69c6fe44c01",
                "Femorales");*/
        //listeners
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.nombre.getText().length()>0 && binding.clave.getText().length()>0){
                    verificarUsuario(binding.nombre.getText().toString(), binding.clave.getText().toString());
                }else{
                    //si no se rellena algún campo saltará una notificación
                    if(binding.nombre.getText().length()<1){
                        binding.nombre.setError("No puede estar vacío.");
                    }else{
                        binding.clave.setError("No puede estar vacío.");
                    }
                }
            }
        });
    }
    /**
     * Obtiene un usuario de la base de datos a partir de nombre y clave pasados por parámetro.
     * Crea una referencia a FirebaseDatabase que conectará con la RealTime Databade de Firebase.
     * Se apllicará un listener a esa referencia, que ejecutará el siguiente código cuando se acceda a ella:
     * Se crea una lista de rutinas.
     * Se crea un bucle for each, donde se comprobará por cada objeto de la base de datos (usuarios) si contienen
     * el nombre y clave pasados por parámetros. Si concuerda, se pasarán los datos a un objeto Usuario que se usará más adelante.
     * Se obtienen sus rutinas en la lista creada anteriormente.
     * Se ejecuta la siguiente actividad (MenúPrincipal) y se ponen las áreas de texto a null.
     * @param usuarioInput valor de 'usuario' del usuario.
     * @param claveInput valor de 'clave' del usuario.
     */
    public void verificarUsuario(String usuarioInput, String claveInput){
        String claveEncriptada = AppHelper.encriptar(claveInput, claveInput);
        String usuarioEncriptado = AppHelper.encriptar(usuarioInput, usuarioInput);
        DatabaseReference ref = FirebaseDatabase.getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//dataSnapshot son todos los usuarios
                boolean confirmado = false;
                for (DataSnapshot data: dataSnapshot.getChildren()) {//
                    if(data.child("usuario").getValue().equals(usuarioEncriptado) && data.child("clave").getValue().equals(claveEncriptada)){
                        userOB.setId(data.getKey());
                        AppHelper.actualizarApp();
                        confirmado=true;
                        //ejecuto el fragmento 'MenuPrincipal'
                        irAMenuPrincipal();

                        //pongo a null los campos de texto
                        binding.nombre.setText("");
                        binding.clave.setText("");
                    }
                }

                if(!confirmado){
                    AppHelper.escribirToast("No estás registrado", MainActivity.this);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
    /**
     * Inicia un intent al activity MenuPrincipal.
     */
    public void irAMenuPrincipal(){
        Intent intent = new Intent(this, MenuPrincipal.class);
        startActivity(intent);
    }

    /**
     * Administra las reservas del usuario. Se obtiene la fecha de hoy. Se obtiene año, mes y día y se comparan
     * con la fecha registrada del día en que se reservo la actividad.
     * if(a1>a2 || (a1==a2 && b1>b2) || (a1==a2 && b1==b2 && (c1-c2>4))).
     * donde: a1 - año actual; b1 - mes actual; c1 - día de hoy.
     * Un resultado true significaría que han pasado más de 5 días desde la reserva, y se eliminará del registro
     * del usuario.
     */
    private static void validarFechaActividad(Context context){
        //Obtengo la fecha de hoy
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        int a1 = Integer.valueOf(date.getYear()),b1 = Integer.valueOf(date.getMonth()),c1 = Integer.valueOf(date.getDate());
        for (int i = 0; i < activitiesOBs.size(); i++) {
            String[] fechas2 = activitiesOBs.get(i).getDate().split("/");
            int a2 = Integer.valueOf(fechas2[0]),b2 = Integer.valueOf(fechas2[1]),c2 = Integer.valueOf(fechas2[2]);
            if(a1>a2 || (a1==a2 && b1>b2) || (a1==a2 && b1==b2 && (c1-c2>4))){
                AppHelper.deleteReservation(activitiesOBs.remove(i), context);
            }
        }
    }
    //SETTERS Y GETTERS
    public static User getUserOB() {
        return userOB;
    }

    public static void setUserOB(User userOb) {
        MainActivity.userOB = userOb;
    }

    public static Weight getWeightOB() {
        return weightOb;
    }

    public static void setWeightOB(Weight pesoOB) {
        MainActivity.weightOb = pesoOB;
    }

    public static Advance getAdvanceOB() {
        return advanceOB;
    }

    public static void setAdvanceOB(Advance avanceOB) {
        MainActivity.advanceOB = avanceOB;
    }

    public static List<Activity> getActivitiesOBs() {
        return activitiesOBs;
    }

    public static void setActivitiesOBs(List<Activity> activitiesOBs) {
        MainActivity.activitiesOBs = activitiesOBs;
    }

    public static List<Routine> getRoutinsOBs() {
        return routinesOBs;
    }

    public static void setRoutinsOBs(List<Routine> routinesOBs) {
        MainActivity.routinesOBs = routinesOBs;
    }

    public static Boolean getConfirmed() {
        return confirmed;
    }

    public static void setConfirme(Boolean confirmed) {
        MainActivity.confirmed = confirmed;
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }

    public void setBinding(ActivityMainBinding binding) {
        this.binding = binding;
    }
}