package com.example.gymgameproject.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gymgameproject.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static FragmentPerfilBinding binding;
    private static Uri imgUri = Uri.parse(" ");//contiene las imagenes de galeria y camara durante su administración
    private ActivityResultLauncher<Intent> camaraLauncher;
    private ActivityResultLauncher<Intent> galeriaLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        cameraLauncher();// Inicializar el ActivityResultLauncher de la camara
        galleryLauncher();// Inicializar el ActivityResultLauncher, de la galeria
        AppHelper.cambiarToolbarText("Perfil");
        AppHelper.cargaPerfil(binding, getContext());
        binding.modificarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottonSheetCambiar(binding.nombre, binding);
            }
        });
        binding.editarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImgOpt();
            }
        });
        return binding.getRoot();
    }
    public void showBottonSheetCambiar(TextView nombreHeader, FragmentPerfilBinding binding){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.my_bottom_sheet_cambiar_password);
        Button aceptar = dialog.findViewById(R.id.aceptar);
        Button cancel = dialog.findViewById(R.id.cancel);
        EditText nombre = dialog.findViewById(R.id.nombre);
        EditText passAntiguo = dialog.findViewById(R.id.passAntiguo);
        EditText passNuevo = dialog.findViewById(R.id.passNuevo);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppHelper.cambiarDatos(nombre,passAntiguo,passNuevo, getContext());
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
                                        .into(binding.img);
                                if(bitmap!=null) {
                                    AppHelper.guardarImagenUserAvatar(CreacionRutinaFragment.getImageUri(getContext(), bitmap))
                                            .toString();
                                }
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
                                        .into(binding.img);
                                AppHelper.guardarImagenUserAvatar(data.getData());
                            }
                        }
                    }
                }
        );
    }
    public static FragmentPerfilBinding getPerfilBinding(){
        return binding;
    }

}