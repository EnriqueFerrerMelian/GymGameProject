package com.example.gymgameproject.activities;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gymgameproject.R;
import com.example.gymgameproject.classes.Activity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ActivitiesFbAdapter extends FirebaseRecyclerAdapter<Activity, ActivitiesFbAdapter.ViewHolder> {
    private ItemClickListener clickListener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ActividadFbAdapter(@NonNull FirebaseRecyclerOptions<Activity> options, ItemClickListener clickListener) {
        super(options);
        this.clickListener = clickListener;
    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ActivitiesFbAdapter(@NonNull FirebaseRecyclerOptions<Activity> options) {
        super(options);
        this.clickListener = clickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ActivitiesFbAdapter.ViewHolder holder, int position, @NonNull Activity model) {
        holder.nombre.setText(model.getName());
        holder.descripcion.setText(model.getDescription());
        if(model.getImg1()!=null){
            Glide.with(holder.imagen.getContext())
                    .load(model.getImg1())
                    .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                    .error(R.drawable.baseline_add_242)//si ocurre algún error se verá por defecto
                    .into(holder.imagen);
        }
        for (int i = 0; i < model.getDays().size(); i++) {
            if(model.getDays().get(i).equals("l")){
                holder.lunes.setTextColor(Color.rgb(255, 127, 39));
            }
            if(model.getDays().get(i).equals("m")){
                holder.martes.setTextColor(Color.rgb(255, 127, 39));
            }
            if(model.getDays().get(i).equals("x")){
                holder.miercoles.setTextColor(Color.rgb(255, 127, 39));
            }
            if(model.getDays().get(i).equals("j")){
                holder.jueves.setTextColor(Color.rgb(255, 127, 39));
            }
            if(model.getDays().get(i).equals("v")){
                holder.viernes.setTextColor(Color.rgb(255, 127, 39));
            }
            if(model.getDays().get(i).equals("s")){
                holder.sabado.setTextColor(Color.rgb(255, 127, 39));
            }
            if(model.getDays().get(i).equals("d")){
                holder.domingo.setTextColor(Color.rgb(255, 127, 39));
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(model);
            }
        });
    }

    @NonNull
    @Override
    public ActivitiesFbAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ActivitiesFbAdapter.ViewHolder(view);
    }
    class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
        TextView nombre, descripcion, lunes, martes, miercoles, jueves, viernes, sabado, domingo;
        ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.activityName);
            descripcion = itemView.findViewById(R.id.activityDescription);
            imagen = itemView.findViewById(R.id.activityImg);
            lunes = itemView.findViewById(R.id.monday);
            martes = itemView.findViewById(R.id.tuesday);
            miercoles = itemView.findViewById(R.id.wednesday);
            jueves = itemView.findViewById(R.id.thursday);
            viernes = itemView.findViewById(R.id.friday);
            sabado = itemView.findViewById(R.id.saturday);
            domingo = itemView.findViewById(R.id.sunday);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return false;
        }
    }



    public interface ItemClickListener{
        public void onItemClick(Activity actividad);
    }
}
