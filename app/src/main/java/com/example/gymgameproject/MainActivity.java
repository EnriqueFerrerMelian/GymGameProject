package com.example.gymgameproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

/**
 * NOTE: When registering at the gym, the administrator will give a randomly generated password
 *  * with which the user can enter the application. In the 'Profile settings' section it can be changed.
 *  * This means that a 'Log' fragment is not needed.
 */

public class MainActivity extends AppCompatActivity {
    private static User userOB = new User();
    private static Weight weightOB = new Weight();
    private static final Advance advanceOB = new Advance();
    private static List<Activity> activitiesOBs = new ArrayList<>();
    private static List<Routine> routinesOBs = new ArrayList<>();
    private static final Boolean confirm = false;


    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        validateActivityDate(this);
        //AppHelper.hotFixCrearUsuario("admin", "admin");
        /*AppHelper.hotFixCrearEjercicio("Curl femoral con maquina", "Máquina"
        ,"Doble las piernas hacia arriba lo más posible mientras exhala. Sostenga 1 seg. Vuelva a la posición inicial con un movimiento suave, evitando que los pesos en movimiento toquen el resto de la placas.",
                "https://firebasestorage.googleapis.com/v0/b/olimplicacion-3ba86.appspot.com/o/Curl%20femoral%20con%20maquina.png?alt=media&token=d81940ba-7855-4cf0-abaa-b69c6fe44c01",
                "Femorales");*/
        //listeners
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.requireNonNull(binding.name.getText()).length()>0 && Objects.requireNonNull(binding.password.getText()).length()>0){
                    verificarUsuario(binding.name.getText().toString(), binding.password.getText().toString());
                }else{
                    //si no se rellena algún campo saltará una notificación
                    if(binding.name.getText().length()<1){
                        binding.name.setError("No puede estar vacío.");
                    }else{
                        binding.password.setError("No puede estar vacío.");
                    }
                }
            }
        });
    }
    /**
     * Obtains a user from the database based on the name and password passed by parameter.
     * Creates a reference to FirebaseDatabase that will connect to the Firebase RealTime Databade.
     * A listener will be applied to that reference, which will execute the following code when it is accessed:
     * A list of routines is created.
     * A for each loop is created, where each database object (users) will be checked if they contain
     * the name and key passed by parameters. If it matches, the data will be passed to a User object that will be used later.
     ** You get your routines in the list created above.
     * The following activity (MainMenu) is executed and the text areas are set to null.
     * @param userInput 'user' value of the user.
     * @param keyInput user 'key' value.
     */
    public void verificarUsuario(String userInput, String keyInput){
        String encryptedPassword = AppHelper.encript(keyInput, keyInput);
        String encryptedUser = AppHelper.encript(userInput, userInput);
        DatabaseReference ref = FirebaseDatabase.getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//dataSnapshot son todos los usuarios
                boolean confirm = false;
                for (DataSnapshot data: dataSnapshot.getChildren()) {//
                    if( Objects.equals(data.child("user").getValue(),encryptedUser) && Objects.equals(data.child("password").getValue(), encryptedPassword)){
                        userOB.setId(data.getKey());
                        AppHelper.updateApp();
                        confirm=true;

                        irAMenuPrincipal();

                        binding.name.setText("");
                        binding.password.setText("");
                    }
                }

                if(!confirm){
                    AppHelper.writeToast("Not registered", MainActivity.this);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });
    }

    public void irAMenuPrincipal(){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    /**
     *Manage user reservations. You get today's date. Year, month and day are obtained and compared
     * with the registered date of the day the activity was booked.
     * if(a1>a2 || (a1==a2 && b1>b2) || (a1==a2 && b1==b2 && (c1-c2>4))).
     * where: a1 - current year; b1 - current month; c1 - today.
     * A true result would mean that more than 5 days have passed since the reservation, and it will
     * be removed from the registry
     * from the user.
     */
    private static void validateActivityDate(Context context){

        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        int a1 = date.getYear(),b1 = date.getMonth(),c1 = date.getDate();
        for (int i = 0; i < activitiesOBs.size(); i++) {
            String[] dates = activitiesOBs.get(i).getDate().split("/");
            int a2 = Integer.parseInt(dates[0]),b2 = Integer.parseInt(dates[1]),c2 = Integer.parseInt(dates[2]);
            if(a1>a2 || (a1==a2 && b1>b2) || (a1==a2 && b1==b2 && (c1-c2>4))){
                AppHelper.deleteReservation(activitiesOBs.remove(i), context);
            }
        }
    }
    //SETTERS Y GETTERS
    public static User getUserOB() {
        return userOB;
    }

    public static void setUserOB(User userOB) {
        MainActivity.userOB = userOB;
    }

    public static Weight getWeightOB() {
        return weightOB;
    }

    public static void setWeightOB(Weight weightOB) {
        MainActivity.weightOB = weightOB;
    }

    public static Advance getAdvanceOB() {
        return advanceOB;
    }

    public static void setAdvanceOB(Advance advance) {
    }
    public static List<Activity> getActivitiesOBs() {
        return activitiesOBs;
    }

    public static void setActivitiesOBs(List<Activity> activitiesOBs) {
        MainActivity.activitiesOBs = activitiesOBs;
    }

    public static List<Routine> getRoutinesOBs() {
        return routinesOBs;
    }

    public static void setRoutinesOBs(List<Routine> routinesOBs) {
        MainActivity.routinesOBs = routinesOBs;
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }

    public void setBinding(ActivityMainBinding binding) {
        this.binding = binding;
    }
}