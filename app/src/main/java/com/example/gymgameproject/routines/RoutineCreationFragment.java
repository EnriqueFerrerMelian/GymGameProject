package com.example.gymgameproject.routines;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gymgameproject.MainActivity;
import com.example.gymgameproject.MainMenu;
import com.example.gymgameproject.R;
import com.example.gymgameproject.classes.AppHelper;
import com.example.gymgameproject.classes.Exercise;
import com.example.gymgameproject.classes.Routine;
import com.example.gymgameproject.databinding.FragmentRoutineCreationBinding;
import com.example.gymgameproject.exercises.ExerciseAdapterModify;
import com.example.gymgameproject.exercises.ExerciseDetailFragment;
import com.example.gymgameproject.exercises.ExercisesListFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoutineCreationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutineCreationFragment extends Fragment implements ExerciseAdapterModify.ViewHolder.ItemClickListener {

    //firebase Satorage ******************************
    private StorageReference storageReference;
    private static Uri imgUriFb = Uri.parse("https://firebasestorage.googleapis.com/v0/b/olimplicacion-3ba86.appspot.com/o/usuario_0%3Bfecha_Mon%20Apr%2001%2006%3A47%3A45%20GMT%202024?alt=media&token=b23df5ae-5c1e-416f-bab5-01474135a55d");//contiendrá el link de la imagen en el Store de firebase
    //firebase Satorage ***************************fin

    //recyclerView ******************************
    private static RecyclerView recyclerView;
    private static ExerciseAdapterModify exerciseAdapterModify;
    private static List<Exercise> dataArrayList = new ArrayList<>();
    //recyclerView ***************************fin

    //obtencion de imágenes ******************************
    private static Uri imgUri = Uri.parse(" ");//contiene las imagenes de galeria y camara durante su administración
    private ActivityResultLauncher<Intent> camaraLauncher;
    private ActivityResultLauncher<Intent> galeriaLauncher;
    //obtencion de imágenes ***************************fin

    //Variables globales
    private static FragmentRoutineCreationBinding binding;
    private static Routine routine;
    private boolean imageConfirmed = false;
    private static int index = 0;
    private static int controlErrores = 0;

    public RoutineCreationFragment() {
        // Required empty public constructor
    }

    /**
     * Carga la routine seleccionada en 'RutinaFragment' al seleccionar 'Modificar' si hay una routine ya
     * creada.
     * @param routineF
     * @return
     */
    public static RoutineCreationFragment newInstance(Routine routineF) {
        RoutineCreationFragment fragment = new RoutineCreationFragment();
        if (routineF != null) {
            routine = routineF;
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRoutineCreationBinding.inflate(inflater, container, false);
        ((MainMenu) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Se cargará la imagen del icono del negocio por defecto
        Glide.with(getContext())
                .load(R.drawable.logo)
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .error(R.drawable.baseline_add_242)//si ocurre algún error se verá por defecto
                .into(binding.editarImagen);

        //si se ha ejecutado este fragmento pasándole una routine, se cargan sus datos
        AppHelper.cambiarToolbarText("New Routine");
        if (routine != null) {
            loadRoutine(routine);
            AppHelper.cambiarToolbarText(routine.getName());
        }
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseAdapterModify = new ExerciseAdapterModify(dataArrayList, this::onItemClick, routine);
        recyclerView.setAdapter(exerciseAdapterModify);
        cameraLauncher();// Inicializar el ActivityResultLauncher de la camara
        galleryLauncher();// Inicializar el ActivityResultLauncher, de la galeria

        //si el fragmento se ejecuta pasándole un objeto Rutina, este cargará los datos en el fragmento.

        //abre un panel para seleccionar una imagen desde galeria o la cámara
        binding.editarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImgOpt();
            }
        });

        //abre el fragmento EjercicioListaFragment para seleccionar un ejercicio a añadir
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new ExercisesListFragment());
            }
        });

        binding.aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarImagenRutina();
                if(controlErrores==0){
                    replaceFragment(new RoutineFragment());
                }
            }
        });

        //Al cancelar la acción se pondrán los valores del fragmento a null
        binding.cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routine = null;
                dataArrayList = new ArrayList<>();
                getParentFragmentManager().popBackStack();
            }
        });
    }

    /**
     * Reemplaza el fragmento en el contenedor 'fragmentContainerView' por el pasado por
     * parámetro
     *
     * @param fragmento
     */
    public void replaceFragment(Fragment fragmento) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragmento).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemClick(Exercise exercise) {
        Fragment fragment = ExerciseDetailFragment.newInstance(exercise);
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment, "note").addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Abre un cuadro de diálogo desde donde se seleccionará añadir una imagen desde la galería
     * o desde la cámara
     */
    public void showImgOpt() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.my_bottom_sheet);
        LinearLayout galeriaLayout = dialog.findViewById(R.id.galeriaLayout);
        LinearLayout camaraLayout = dialog.findViewById(R.id.camaraLayout);
        galeriaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galeriaLauncher.launch(intent);
                dialog.dismiss();
            }
        });
        camaraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) { // Si no tiene permiso
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 225); // Solicitar permiso
                } else {
                    // Crear un Intent para abrir la cámara
                    Intent camaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Iniciar el ActivityResultLauncher para la cámara
                    camaraLauncher.launch(camaraIntent);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    public void showSobrescribirSheet(Map<String, Object> mapRutin, List<Exercise> exercises) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.my_bottom_sheet_overwrite_routine);
        Button si = dialog.findViewById(R.id.si);
        Button no = dialog.findViewById(R.id.no);
        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeRoutine(RoutineDetailFragment.getRoutine());
                updateRoutine(mapRutin, exercises);

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

    public void cameraLauncher() {
        //obtendra la imagen como Bitmap por defecto
        camaraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        //capturamos imagen
                        Intent data = result.getData();
                        if (data != null) {
                            // Obtener la imagen como un bitmap
                            Bundle extras = data.getExtras();
                            if (extras != null) {
                                Bitmap bitmap = (Bitmap) extras.get("data");
                                Glide.with(getContext())
                                        .load(bitmap)
                                        .placeholder(R.drawable.iconogris)//si no hay imagen carga una por defecto
                                        .circleCrop()
                                        .error(R.drawable.iconogris)//si ocurre algún error se verá por defecto
                                        .into(binding.editarImagen);
                                if(bitmap!=null) {
                                    imgUri = getImageUri(getContext(), bitmap);
                                }
                                imageConfirmed = true;
                            } else {
                                // Si no hay datos extras, utilizar la Uri para cargar la imagen
                            }
                        }
                    }
                });
    }

    public void galleryLauncher() {
        //obtendra la imagen como Uri por defecto
        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        //capturamos imagen
                        Intent data = result.getData();

                        if (data != null) {
                            // Obtener la imagen como un bitmap
                            Bundle extras = data.getExtras();
                            if (extras != null) {
                                Bitmap bitmap = (Bitmap) extras.get("data");
                            } else {
                                // Si no hay datos extras, utilizar la Uri para cargar la imagen
                                Glide.with(getContext())
                                        .load(data.getData())
                                        .placeholder(R.drawable.iconogris)//si no hay imagen carga una por defecto
                                        .circleCrop()
                                        .error(R.drawable.iconogris)//si ocurre algún error se verá por defecto
                                        .into(binding.editarImagen);
                                imgUri = data.getData();
                                imageConfirmed = true;
                            }
                        }
                    }
                }
        );
    }


    /**
     * Este método guarda una imagen en el Store de firebase.
     * On one side, we take the image Uri text, and pick one portion of it as a name on the store.
     * On the other side, we catch the url that we will use to access and load the image. The imgUriFb.
     * It needs to run before saveRoutine(), so we can get the imgUriFb first, because the actions are asynchronous.
     */
    public void guardarImagenRutina() {
        //a sustituir por saveImageRoutine. Probar antes
        //si no se ha pasado una rutina por parámetro
        if (routine == null) {
            //y si se ha configurado una imagen
            if(imageConfirmed){
                //creamos una referencia en el Store que será el nombre de la imagen
                String[] title = String.valueOf(imgUri).split("/");
                storageReference = FirebaseStorage.getInstance().getReference(title[title.length-1]);
                storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //si no hay problemas durante el proceso obtenemos el link de la imagen en el Store
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri urlimagen = uriTask.getResult();
                        imgUriFb = urlimagen;
                        //se procede a guardar la rutina en el Realtime Database
                        saveRoutine();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
            }else{
                //si NO se ha recogido una imagen subimos la imagen por defecto y recojo el link
                saveRoutine();
            }
        }else{
            //si se ha pasado una rutina por parámetro y se ha hecho una foto
            if(imageConfirmed){
                if(routine.getImage()!=null){
                    eliminarImagen();
                }
                //creamos una referencia en el Store que será el nombre de la imagen
                String[] titulo = String.valueOf(imgUri).split("/");
                storageReference = FirebaseStorage.getInstance().getReference(titulo[titulo.length-1]);
                //subimos la imagen y recojo el link
                //putFile debe contener una imagen, no un link a una imagen
                storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri urlimagen = uriTask.getResult();
                        imgUriFb = urlimagen;
                        saveRoutine();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
            }else{
                //si se ha pasado una rutina por parámetro y no se ha hecho una foto
                saveRoutine();
            }
        }
    }
    /**
     * Este método guarda los datos de la rutina nueva creada por el usuario. Dentro de esa rutina, una lista de
     * ejercicios en los que se incluyen dos datos más: 'pesos' y 'repeticiones y veces'.
     */
    public void saveRoutine() {//guardarRutinaEnRealtime
        //saving today's date
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        String fecha = date.toString();

        //saving routine on firebase **********************************
        Routine routin = new Routine();
        routin.setName(binding.nombreDeRutina.getText().toString());
        routin.setId(MainActivity.getUserOB().getId() + "_" + fecha);
        //si se ha capturado una imagen
        if(imageConfirmed){
            //se guarda
            routin.setImage(imgUriFb.toString());
        }else{
            //si no y además se está modificando una rutina
            if(routine!=null){
                //se guarda la imagen que almacenaba la rutina anteriormente
                routin.setImage(routine.getImage());
            }
        }
        List<String> days = new ArrayList<>();
        if (binding.lunes.isChecked()) {
            days.add("l");
        }
        if (binding.martes.isChecked()) {
            days.add("m");
        }
        if (binding.miercoles.isChecked()) {
            days.add("x");
        }
        if (binding.jueves.isChecked()) {
            days.add("j");
        }
        if (binding.viernes.isChecked()) {
            days.add("v");
        }
        if (binding.sabado.isChecked()) {
            days.add("s");
        }
        if (binding.domingo.isChecked()) {
            days.add("d");
        }
        routin.setDays(days);
        List<Exercise> exercisesList = dataArrayList;
        routine.setExercises(exercisesList);
        Map<String, Object> mapRutin = new HashMap<>();
        mapRutin.put(binding.nombreDeRutina.getText().toString(), routin);

        if(!(days.isEmpty() || exercisesList.isEmpty() || binding.nombreDeRutina.length()<1)){
            //si se está modificando una rutina
            if (routine != null) {
                //si el nombre se ha cambiado, se comprueba si ya hay otra rutina con el nombre nuevo ya guardada
                if(MainActivity.getUserOB().getRoutines().get(routine.getName())!=null){
                    showSobrescribirSheet(mapRutin, exercisesList);
                }else{
                    removeRoutine(RoutineDetailFragment.getRoutine());
                    updateRoutine(mapRutin, exercisesList);
                }
            }else{
                updateRoutine(mapRutin, exercisesList);
                //si no hay rutina se ponen los valores del fragmento a null
                dataArrayList = new ArrayList<>();
                routine = null;
            }
            controlErrores =0;
        }else{
            controlErrores++;
            if(routine.getName().length()<1){
                AppHelper.escribirToast("El nombre no puede estar en blanco.", getContext());
            }else{
                if(days.isEmpty()){
                    AppHelper.escribirToast("Debes seleccionar almenos un día.", getContext());
                }else if(exercisesList.isEmpty()){
                    AppHelper.escribirToast("Debes añadir al menos un ejercicio.", getContext());
                }
            }
        }
    }

    /**
     * Elimina la imagen de la base de datos Storage de Firebase;
     */
    public void eliminarImagen(){
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(routine.getImage());
        ref.delete();
    }

    /**
     * Elimina la rutina de la base de datos de Realtima Firebase.
     * @param routin
     */
    public void removeRoutine(Routine routin) {//eliminarRutinaM
        //creo una referencia a la rutina que quiero borrar
        DatabaseReference ref2 = FirebaseDatabase.getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/" + MainActivity.getUserOB().getId() + "/rutinas/"+routin.getName());
        //elimino la rutina
        ref2.removeValue();

        //al acabar el proceso pongo los valores del fragmento a null
        dataArrayList = new ArrayList<>();
        routine = null;
    }

    /**
     * Eliminará del dataArrayList el ejercicio seleccionado. No se eliminará de la rutina si se hace click
     * en 'cancelar'
     * @param idEjercicio
     */
    public static void removeExercise(int idEjercicio) {//eliminarEjercicio
        for (int i = 0; i < dataArrayList.size(); i++) {
            if(dataArrayList.get(i).getId()==idEjercicio){
                dataArrayList.remove(i);
                recyclerView.setAdapter(exerciseAdapterModify);
            }
        }
    }

    /**
     * Al seleccionar un ejercicio de la lista, se usará este método para añadirlo al dataArrayList.
     * Si el ejercicio ya estaba en la lista no se añadirá
     * @param exercise
     * @param context
     */
    public static void addToDataList(Exercise exercise, Context context) {
        boolean join = true;
        for (Exercise dato : dataArrayList) {
            if (dato.getId() == exercise.getId()) {
                join = false;
            }
        }
        if (join) {
            dataArrayList.add(exercise);
        } else {
            Toast.makeText(context, "Ya estába en la lista de ejercicios.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Este método convierte una imagen Bitmap a tipo Uri
     *
     * @param inContext
     * @param inImage
     * @return
     */
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        Date fecha = new Date();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title" + fecha.toString(), null);
        return Uri.parse(path);
    }

    /**
     * Si el fragmento se abre mara modificar una rutina ya existente, el objeto global rutina
     * guardará la información de la rutina seleccionada y se cargarán sus datos en este fragmento.
     *
     * @param routine
     */
    public void loadRoutine(Routine routine) {//cargarRutina
        dataArrayList = routine.getExercises();
        Glide.with(getContext())
                .load(routine.getImage())
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .error(R.drawable.baseline_add_242)//si ocurre algún error se verá por defecto
                .into(binding.editarImagen);
        //inserto el nombre
        binding.nombreDeRutina.setText(routine.getName());
        //cambio el color de los días seleccionados
        if (routine.getDays().contains("l")) {
            binding.lunes.setChecked(true);
        }
        if (routine.getDays().contains("m")) {
            binding.martes.setChecked(true);
        }
        if (routine.getDays().contains("x")) {
            binding.miercoles.setChecked(true);
        }
        if (routine.getDays().contains("j")) {
            binding.jueves.setChecked(true);
        }
        if (routine.getDays().contains("v")) {
            binding.viernes.setChecked(true);
        }
        if (routine.getDays().contains("s")) {
            binding.sabado.setChecked(true);
        }
        if (routine.getDays().contains("d")) {
            binding.domingo.setChecked(true);
        }
    }

    public void updateRoutine(Map<String, Object> mapa, List<Exercise> exercises){

        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+MainActivity.getUserOB().getId()+"/rutinas/");
        ref.updateChildren(mapa).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateAdvance(exercises);
                AppHelper.updateApp();
            }
        });
    }
    public static void updateAdvance(List<Exercise> exercises){
        for (int i = 0; i < exercises.size(); i++) {
            if(!MainActivity.getAdvanceOB().getExercisesName().contains(exercises.get(i).getName())){
                MainActivity.getAdvanceOB().getExercisesName().add(exercises.get(i).getName());
                MainActivity.getAdvanceOB().getWeights().add(exercises.get(i).getWeight());
            }
        }
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUserOB().getId()+"/avance");
        ref.setValue(MainActivity.getAdvanceOB());
    }
    public void saveImageRoutine() {
        if (routine != null && imageConfirmed) {
            if(routine.getImage()!=null){
                eliminarImagen();
            }
        }
        if(imageConfirmed){
            //creamos una referencia en el Store que será el nombre de la imagen
            String[] title = String.valueOf(imgUri).split("/");
            storageReference = FirebaseStorage.getInstance().getReference(title[title.length-1]);
            storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //si no hay problemas durante el proceso obtenemos el link de la imagen en el Store
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri urlimagen = uriTask.getResult();
                    imgUriFb = urlimagen;
                    //se procede a guardar la rutina en el Realtime Database
                    saveRoutine();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e);
                }
            });
        }else{
            //si NO se ha recogido una imagen subimos la imagen por defecto y recojo el link
            saveRoutine();
        }
    }
}