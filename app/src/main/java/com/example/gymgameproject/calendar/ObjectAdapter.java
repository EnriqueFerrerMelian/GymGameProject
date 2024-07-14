package com.example.gymgameproject.calendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ObjectAdapter extends RecyclerView.Adapter<ObjetoAdapter.ViewHolder>{
    private List<Evento> dataArrayList = new ArrayList<Evento>();
    public ObjetoAdapter(List<Evento> dataArrayList) {
        this.dataArrayList = dataArrayList;

    }
    @NonNull
    @Override
    public ObjetoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_objeto, parent, false);
        return new ObjetoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObjetoAdapter.ViewHolder holder, int position) {
        holder.bind(dataArrayList.get(position));

    }

    @Override
    public int getItemCount() { return dataArrayList.size();}

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView nombre, lunes, martes, miercoles, jueves, viernes, sabado, domingo, horario;
        ImageView imagen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.img);
            nombre = itemView.findViewById(R.id.nombre);
            horario = itemView.findViewById(R.id.horario);
            lunes = itemView.findViewById(R.id.lunes);
            martes = itemView.findViewById(R.id.martes);
            miercoles = itemView.findViewById(R.id.miercoles);
            jueves = itemView.findViewById(R.id.jueves);
            viernes = itemView.findViewById(R.id.viernes);
            sabado = itemView.findViewById(R.id.sabado);
            domingo = itemView.findViewById(R.id.domingo);

        }
        public void bind(Evento model){
            nombre.setText(model.getNombre());
            horario.setText(model.getHorario());
            if(model.getImg2()!=null){
                Glide.with(imagen.getContext())
                        .load(model.getImg2())
                        .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                        .circleCrop()
                        .error(R.drawable.baseline_add_242)//si ocurre algún error se verá por defecto
                        .into(imagen);
            }
            for (int i = 0; i < model.getDias().size(); i++) {
                if(model.getDias().get(i).equals("l")){
                    lunes.setTextColor(Color.rgb(255, 127, 39));
                }
                if(model.getDias().get(i).equals("m")){
                    martes.setTextColor(Color.rgb(255, 127, 39));
                }
                if(model.getDias().get(i).equals("x")){
                    miercoles.setTextColor(Color.rgb(255, 127, 39));
                }
                if(model.getDias().get(i).equals("j")){
                    jueves.setTextColor(Color.rgb(255, 127, 39));
                }
                if(model.getDias().get(i).equals("v")){
                    viernes.setTextColor(Color.rgb(255, 127, 39));
                }
                if(model.getDias().get(i).equals("s")){
                    sabado.setTextColor(Color.rgb(255, 127, 39));
                }
                if(model.getDias().get(i).equals("d")){
                    domingo.setTextColor(Color.rgb(255, 127, 39));
                }
            }
        }
    }
}
