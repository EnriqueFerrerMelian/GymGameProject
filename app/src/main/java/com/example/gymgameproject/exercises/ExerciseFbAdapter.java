package com.example.gymgameproject.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

public class ExerciseFbAdapter extends FirebaseRecyclerAdapter<Ejercicio, EjercicioFbAdapter.ViewHolder> {
    private ItemClickListener clickListener;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public EjercicioFbAdapter(@NonNull FirebaseRecyclerOptions<Ejercicio> options, ItemClickListener clickListener) {
        super(options);
        this.clickListener = clickListener;
    }

    /**
     * Extraerá la información de los objetos y las insertará en la vista si el id del ejercicio está contenido en
     * la lista de ejercicios del usuario.
     * Para seleccionar las que pertenecen al usuario logeado, se crea una lista de integros que contendrán los
     * ejercicios que pertenecen a ese usuario.
     * Luego se comparan los ejercicios descargados con los de la lista y se cargan en el recyclerView los que coincidan.
     * @param holder
     * @param position
     * @param model the model object containing the data that should be used to populate the view.
     */
    @Override
    protected void onBindViewHolder(@NonNull EjercicioFbAdapter.ViewHolder holder, int position, @NonNull Ejercicio model) {
        holder.nombre.setText(model.getNombre());
        Glide.with(holder.imagen.getContext())
                .load(model.getImg())
                .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                .circleCrop()
                .error(R.drawable.baseline_add_242)//si ocurre algún error se verá por defecto
                .into(holder.imagen);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(model);
            }
        });
    }

    @NonNull
    @Override
    public EjercicioFbAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ejercicio, parent, false);
        return new EjercicioFbAdapter.ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView imagen;
        TextView nombre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.listImage01);
            nombre = itemView.findViewById(R.id.listName01);
        }
    }
    public interface ItemClickListener{
        public void onItemClick(Ejercicio ejercicio);
    }
}
