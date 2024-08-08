package com.example.gymgameproject.exercises;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gymgameproject.R;
import com.example.gymgameproject.classes.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder>{
    private static List<Exercise> dataArrayList = new ArrayList<Exercise>();
    private ViewHolder.ItemClickListener clickListener;


    public ExerciseAdapter(List<Exercise> dataArrayList, ViewHolder.ItemClickListener clickListener) {
        this.dataArrayList = dataArrayList;
        this.clickListener = clickListener;
    }

    public void setListaFiltrada(List<Exercise> listaFiltrada){
        this.dataArrayList = listaFiltrada;
        notifyDataSetChanged();//esto es para un buscador
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(dataArrayList.get(position));
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.listName01);
            imagen = itemView.findViewById(R.id.listImage01);
        }

        public void bind(Exercise ejercicio) {
            nombre.setText(ejercicio.getName());
            Glide.with(imagen.getContext())
                    .load(ejercicio.getImage())
                    .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                    .circleCrop()
                    .error(R.drawable.baseline_add_242)//si ocurre algún error se verá por defecto
                    .into(imagen);

        }

        public interface ItemClickListener{
            public void onItemClick(Exercise ejercicio);
        }
    }

}
