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

import com.example.gymgameproject.R;

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
public class RoutineCreationFragment extends Fragment implements EjercicioAdapterModificar.ViewHolder.ItemClickListener {

    //firebase Satorage ******************************
    private StorageReference storageReference;
    private static Uri imgUriFb = Uri.parse("https://firebasestorage.googleapis.com/v0/b/olimplicacion-3ba86.appspot.com/o/usuario_0%3Bfecha_Mon%20Apr%2001%2006%3A47%3A45%20GMT%202024?alt=media&token=b23df5ae-5c1e-416f-bab5-01474135a55d");//contiendrá el link de la imagen en el Store de firebase
    //firebase Satorage ***************************fin

    //recyclerView ******************************
    private static RecyclerView recyclerView;
    private static EjercicioAdapterModificar ejercicioAdapterModificar;
    private static List<Ejercicio> dataArrayList = new ArrayList<>();
    //recyclerView ***************************fin

    //obtencion de imágenes ******************************
    private static Uri imgUri = Uri.parse(" ");//contiene las imagenes de galeria y camara durante su administración
    private ActivityResultLauncher<Intent> camaraLauncher;
    private ActivityResultLauncher<Intent> galeriaLauncher;
    //obtencion de imágenes ***************************fin

    //Variables globales
    private static FragmentCreacionRutinaBinding binding;
    private static Rutina rutina;
    private boolean confirmacionImg = false;
    private static int index = 0;
    private static int controlErrores = 0;

    public CreacionRutinaFragment() {
        // Required empty public constructor
    }

    /**
     * Carga la rutina seleccionada en 'RutinaFragment' al seleccionar 'Modificar' si hay una rutina ya
     * creada.
     * @param rutinaF
     * @return
     */
    public static CreacionRutinaFragment newInstance(Rutina rutinaF) {
        CreacionRutinaFragment fragment = new CreacionRutinaFragment();
        if (rutinaF != null) {
            rutina = rutinaF;
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreacionRutinaBinding.inflate(inflater, container, false);
        ((MenuPrincipal) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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

        //si se ha ejecutado este fragmento pasándole una rutina, se cargan sus datos
        AppHelper.cambiarToolbarText("Rutina nueva");
        if (rutina != null) {
            cargarRutina(rutina);
            AppHelper.cambiarToolbarText(rutina.getNombre());
        }
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ejercicioAdapterModificar = new EjercicioAdapterModificar(dataArrayList, this::onItemClick, rutina);
        recyclerView.setAdapter(ejercicioAdapterModificar);
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
                reemplazarFragmento(new ListaEjerciciosFragment());
            }
        });

        binding.aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarImagenRutina();
                if(controlErrores==0){
                    reemplazarFragmento(new RutinaFragment());
                }
            }
        });

        //Al cancelar la acción se pondrán los valores del fragmento a null
        binding.cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rutina = null;
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
    public void reemplazarFragmento(Fragment fragmento) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragmento).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemClick(Ejercicio ejercicio) {
        Fragment fragment = DetalleEjercicioFragment.newInstance(ejercicio);
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment, "nota").addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Abre un cuadro de diálogo desde donde se seleccionará añadir una imagen desde la galería
     * o desde la cámara
     */
    public void showImgOpt() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.my_botton_sheet);
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
    public void showSobrescribirSheet(Map<String, Object> mapRutin, List<Ejercicio> ejercicios) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.my_bottom_sheet_sobrescribir_rutina);
        Button si = dialog.findViewById(R.id.si);
        Button no = dialog.findViewById(R.id.no);
        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarRutinaM(DetallesRutinaFragment.getRutina());
                actualizarRutina(mapRutin, ejercicios);

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
                                confirmacionImg = true;
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
                                confirmacionImg = true;
                            }
                        }
                    }
                }
        );
    }


    /**
     * Este método guarda una imagen en el Store de firebase
     */
    public void guardarImagenRutina() {
        //si no se ha pasado una rutina por parámetro
        if (rutina == null) {
            //y si se ha configurado una imagen
            if(confirmacionImg){
                //creamos una referencia en el Store que será el nombre de la imagen
                String[] titulo = String.valueOf(imgUri).split("/");
                storageReference = FirebaseStorage.getInstance().getReference(titulo[titulo.length-1]);
                storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //si no hay problemas durante el proceso obtenemos el link de la imagen en el Store
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri urlimagen = uriTask.getResult();
                        imgUriFb = urlimagen;
                        //se procede a guardar la rutina en el Realtime Database
                        guardarRutinaEnRealtime();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
            }else{
                //si NO se ha recogido una imagen subimos la imagen por defecto y recojo el link
                guardarRutinaEnRealtime();
            }
        }else{
            //si se ha pasado una rutina por parámetro y se ha hecho una foto
            if(confirmacionImg){
                if(rutina.getImg()!=null){
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
                        guardarRutinaEnRealtime();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
            }else{
                //si se ha pasado una rutina por parámetro y no se ha hecho una foto
                guardarRutinaEnRealtime();
            }
        }
    }
    /**
     * Este método guarda los datos de la rutina nueva creada por el usuario. Dentro de esa rutina, una lista de
     * ejercicios en los que se incluyen dos datos más: 'pesos' y 'repeticiones y veces'.
     */
    public void guardarRutinaEnRealtime() {
        //guardo la fecha de hoy
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        String fecha = date.toString();

        //guardando la rutina en la base de datos **********************************
        Rutina rutin = new Rutina();
        rutin.setNombre(binding.nombreDeRutina.getText().toString());
        rutin.setId(MainActivity.getUsuarioOB().getId() + "_" + fecha);
        //si se ha capturado una imagen
        if(confirmacionImg){
            //se guarda
            rutin.setImg(imgUriFb.toString());
        }else{
            //si no y además se está modificando una rutina
            if(rutina!=null){
                //se guarda la imagen que almacenaba la rutina anteriormente
                rutin.setImg(rutina.getImg());
            }
        }
        List<String> dias = new ArrayList<>();
        if (binding.lunes.isChecked()) {
            dias.add("l");
        }
        if (binding.martes.isChecked()) {
            dias.add("m");
        }
        if (binding.miercoles.isChecked()) {
            dias.add("x");
        }
        if (binding.jueves.isChecked()) {
            dias.add("j");
        }
        if (binding.viernes.isChecked()) {
            dias.add("v");
        }
        if (binding.sabado.isChecked()) {
            dias.add("s");
        }
        if (binding.domingo.isChecked()) {
            dias.add("d");
        }
        rutin.setDias(dias);
        List<Ejercicio> ejerciciosLista = dataArrayList;
        rutin.setEjercicios(ejerciciosLista);
        Map<String, Object> mapRutin = new HashMap<>();
        mapRutin.put(binding.nombreDeRutina.getText().toString(), rutin);

        if(!(dias.isEmpty() || ejerciciosLista.isEmpty() || binding.nombreDeRutina.length()<1)){
            //si se está modificando una rutina
            if (rutina != null) {
                //si el nombre se ha cambiado, se comprueba si ya hay otra rutina con el nombre nuevo ya guardada
                if(MainActivity.getUsuarioOB().getRutinas().get(rutin.getNombre())!=null){
                    showSobrescribirSheet(mapRutin, ejerciciosLista);
                }else{
                    eliminarRutinaM(DetallesRutinaFragment.getRutina());
                    actualizarRutina(mapRutin, ejerciciosLista);
                }
            }else{
                actualizarRutina(mapRutin, ejerciciosLista);
                //si no hay rutina se ponen los valores del fragmento a null
                dataArrayList = new ArrayList<>();
                rutina = null;
            }
            controlErrores =0;
        }else{
            controlErrores++;
            if(rutin.getNombre().length()<1){
                AppHelper.escribirToast("El nombre no puede estar en blanco.", getContext());
            }else{
                if(dias.isEmpty()){
                    AppHelper.escribirToast("Debes seleccionar almenos un día.", getContext());
                }else if(ejerciciosLista.isEmpty()){
                    AppHelper.escribirToast("Debes añadir al menos un ejercicio.", getContext());
                }
            }
        }
    }

    /**
     * Elimina la imagen de la base de datos Storage de Firebase;
     */
    public void eliminarImagen(){
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(rutina.getImg());
        ref.delete();
    }

    /**
     * Elimina la rutina de la base de datos de Realtima Firebase.
     * @param rutin
     */
    public void eliminarRutinaM(Rutina rutin) {
        //creo una referencia a la rutina que quiero borrar
        DatabaseReference ref2 = FirebaseDatabase.getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/" + MainActivity.getUsuarioOB().getId() + "/rutinas/"+rutin.getNombre());
        //elimino la rutina
        ref2.removeValue();

        //al acabar el proceso pongo los valores del fragmento a null
        dataArrayList = new ArrayList<>();
        rutina = null;
    }

    /**
     * Eliminará del dataArrayList el ejercicio seleccionado. No se eliminará de la rutina si se hace click
     * en 'cancelar'
     * @param idEjercicio
     */
    public static void eliminarEjercicio(int idEjercicio) {
        for (int i = 0; i < dataArrayList.size(); i++) {
            if(dataArrayList.get(i).getId()==idEjercicio){
                dataArrayList.remove(i);
                recyclerView.setAdapter(ejercicioAdapterModificar);
            }
        }
    }

    /**
     * Al seleccionar un ejercicio de la lista, se usará este método para añadirlo al dataArrayList.
     * Si el ejercicio ya estaba en la lista no se añadirá
     * @param ejercicio
     * @param contexto
     */
    public static void addToDataList(Ejercicio ejercicio, Context contexto) {
        boolean join = true;
        for (Ejercicio dato : dataArrayList) {
            if (dato.getId() == ejercicio.getId()) {
                join = false;
            }
        }
        if (join) {
            dataArrayList.add(ejercicio);
        } else {
            Toast.makeText(contexto, "Ya estába en la lista de ejercicios.", Toast.LENGTH_LONG).show();
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
     * @param rutina
     */
    public void cargarRutina(Rutina rutina) {
        dataArrayList = rutina.getEjercicios();
        Glide.with(getContext())
                .load(rutina.getImg())
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .error(R.drawable.baseline_add_242)//si ocurre algún error se verá por defecto
                .into(binding.editarImagen);
        //inserto el nombre
        binding.nombreDeRutina.setText(rutina.getNombre());
        //cambio el color de los días seleccionados
        if (rutina.getDias().contains("l")) {
            binding.lunes.setChecked(true);
        }
        if (rutina.getDias().contains("m")) {
            binding.martes.setChecked(true);
        }
        if (rutina.getDias().contains("x")) {
            binding.miercoles.setChecked(true);
        }
        if (rutina.getDias().contains("j")) {
            binding.jueves.setChecked(true);
        }
        if (rutina.getDias().contains("v")) {
            binding.viernes.setChecked(true);
        }
        if (rutina.getDias().contains("s")) {
            binding.sabado.setChecked(true);
        }
        if (rutina.getDias().contains("d")) {
            binding.domingo.setChecked(true);
        }
    }

    public void actualizarRutina(Map<String, Object> mapa, List<Ejercicio> ejercicios){

        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+MainActivity.getUsuarioOB().getId()+"/rutinas/");
        ref.updateChildren(mapa).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                actualizarAvance(ejercicios);
                AppHelper.actualizarApp();
            }
        });
    }
    public static void actualizarAvance(List<Ejercicio> ejercicios){
        for (int i = 0; i < ejercicios.size(); i++) {
            if(!MainActivity.getAvanceOB().getEjerciciosNombres().contains(ejercicios.get(i).getNombre())){
                MainActivity.getAvanceOB().getEjerciciosNombres().add(ejercicios.get(i).getNombre());
                MainActivity.getAvanceOB().getPesos().add(ejercicios.get(i).getPeso());
            }
        }
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/"+ MainActivity.getUsuarioOB().getId()+"/avance");
        ref.setValue(MainActivity.getAvanceOB());
    }

}