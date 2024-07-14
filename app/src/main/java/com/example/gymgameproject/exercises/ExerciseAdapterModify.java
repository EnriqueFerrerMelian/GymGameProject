package com.example.gymgameproject.exercises;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapterModify extends RecyclerView.Adapter<EjercicioAdapterModificar.ViewHolder>{
    private static List<Ejercicio> dataArrayList = new ArrayList<Ejercicio>();
    private ViewHolder.ItemClickListener clickListener;
    private static Rutina rutina;


    public EjercicioAdapterModificar(List<Ejercicio> dataArrayList, ViewHolder.ItemClickListener clickListener, Rutina rutina) {
        this.dataArrayList = dataArrayList;
        this.clickListener = clickListener;
        this.rutina = rutina;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ejercicio_modificar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(dataArrayList.get(position));
        holder.idEjercicio = dataArrayList.get(position).getId();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(dataArrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        //declaración de variables
        TextView nombre;
        ImageView imagen;
        ImageButton borrar;int idEjercicio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.listName01);
            imagen = itemView.findViewById(R.id.listImage01);
            borrar = itemView.findViewById(R.id.borrar);
            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreacionRutinaFragment.eliminarEjercicio(idEjercicio);
                }
            });
        }

        public void bind(Ejercicio ejercicio) {
            nombre.setText(ejercicio.getNombre());
            Glide.with(imagen.getContext())
                    .load(ejercicio.getImg())
                    .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                    .circleCrop()
                    .error(R.drawable.baseline_add_242)//si ocurre algún error se verá por defecto
                    .into(imagen);

        }

        public interface ItemClickListener{
            public void onItemClick(Ejercicio ejercicio);
        }
    }

}
