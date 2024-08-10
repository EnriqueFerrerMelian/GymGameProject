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

import com.bumptech.glide.Glide;
import com.example.gymgameproject.R;
import com.example.gymgameproject.classes.AppHelper;
import com.example.gymgameproject.databinding.FragmentProfileBinding;
import com.example.gymgameproject.routines.RoutineCreationFragment;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private static FragmentProfileBinding binding;
    private static Uri imgUri = Uri.parse(" ");// contains de images of the gallery and camera during the management
    private ActivityResultLauncher<Intent> camaraLauncher;
    private ActivityResultLauncher<Intent> galeriaLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        cameraLauncher();//  initialize the camera ActivityResultLauncher
        galleryLauncher();// initialize the gallery ActivityResultLauncher
        AppHelper.cambiarToolbarText("Profile");
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
    public void showBottonSheetCambiar(TextView nombreHeader, FragmentProfileBinding binding){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.my_bottom_sheet_change_password);
        Button accept = dialog.findViewById(R.id.accept);
        Button cancel = dialog.findViewById(R.id.cancel);
        EditText name = dialog.findViewById(R.id.name);
        EditText oldPass = dialog.findViewById(R.id.oldPass);
        EditText newPass = dialog.findViewById(R.id.newPass);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppHelper.cambiarDatos(name,oldPass,newPass, getContext());
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
     * Opens a dialog box where you can add images from the gallery
     * or camera
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
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) { // if don't have permision
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 225); // ask permision
                } else {
                    // create an intent to open the camera
                    Intent camaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // initialize camera ActivityResultLauncher
                    camaraLauncher.launch(camaraIntent);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    public void cameraLauncher() {
        // obtains a bitmap image by default
        camaraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // get an image
                        Intent data = result.getData();
                        if (data != null) {
                            // get bitmap image
                            Bundle extras = data.getExtras();
                            if (extras != null) {
                                Bitmap bitmap = (Bitmap) extras.get("data");
                                Glide.with(getContext())
                                        .load(bitmap)
                                        .placeholder(R.drawable.iconogris)// if there is no image, will load one by default
                                        .circleCrop()
                                        .error(R.drawable.iconogris)
                                        .into(binding.img);
                                if(bitmap!=null) {
                                    AppHelper.saveUserAvatar(RoutineCreationFragment.getImageUri(getContext(), bitmap));
                                }
                            }
                        }
                    }
                });
    }
    public void galleryLauncher() {
        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();

                        if (data != null) {
                            Bundle extras = data.getExtras();
                            if (extras != null) {
                                Bitmap bitmap = (Bitmap) extras.get("data");
                            } else {
                                Glide.with(getContext())
                                        .load(data.getData())
                                        .placeholder(R.drawable.iconogris)
                                        .circleCrop()
                                        .error(R.drawable.iconogris)
                                        .into(binding.img);
                                AppHelper.saveUserAvatar(data.getData());
                            }
                        }
                    }
                }
        );
    }
    public static FragmentProfileBinding getPerfilBinding(){
        return binding;
    }

}