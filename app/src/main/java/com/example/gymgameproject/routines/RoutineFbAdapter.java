package com.example.gymgameproject.routines;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

public class RoutineFbAdapter extends FirebaseRecyclerAdapter<Rutina, RutinaFbAdapter.ViewHolder> {
    private ItemClickListener clickListener;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RutinaFbAdapter(@NonNull FirebaseRecyclerOptions<Rutina> options, ItemClickListener clickListener) {
        super(options);
        this.clickListener = clickListener;
    }

    /**
     * Extraerá la información de los objetos y las insertará en la vista si el id del ejercicio está contenido en
     * la lista de ejercicios del usuario.
     * Para seleccionar las que pertenecen al usuario logeado, se crea una lista de integros que contendrán los
     * ejercicios que pertenecen a ese usuario.
     * Luego se comparan los ejercicios descargados con los de la lista y se cargan los que coincidan en el recyclerView
     * @param holder
     * @param position
     * @param model the model object containing the data that should be used to populate the view.
     */
    @Override
    protected void onBindViewHolder(@NonNull RutinaFbAdapter.ViewHolder holder, int position, @NonNull Rutina model) {
        holder.nombre.setText(model.getNombre());
        holder.rutinaId = model.getId();
        if(model.getImg()!=null){
            Glide.with(holder.imagen.getContext())
                    .load(model.getImg())
                    .placeholder(R.drawable.baseline_add_242)//si no hay imagen carga una por defecto
                    .circleCrop()
                    .error(R.drawable.baseline_add_242)//si ocurre algún error se verá por defecto
                    .into(holder.imagen);
        }
        for (int i = 0; i < model.getDias().size(); i++) {
            if(model.getDias().get(i).equals("l")){
                holder.lunes.setTextColor(Color.rgb(255, 127, 39));
            }
            if(model.getDias().get(i).equals("m")){
                holder.martes.setTextColor(Color.rgb(255, 127, 39));
            }
            if(model.getDias().get(i).equals("x")){
                holder.miercoles.setTextColor(Color.rgb(255, 127, 39));
            }
            if(model.getDias().get(i).equals("j")){
                holder.jueves.setTextColor(Color.rgb(255, 127, 39));
            }
            if(model.getDias().get(i).equals("v")){
                holder.viernes.setTextColor(Color.rgb(255, 127, 39));
            }
            if(model.getDias().get(i).equals("s")){
                holder.sabado.setTextColor(Color.rgb(255, 127, 39));
            }
            if(model.getDias().get(i).equals("d")){
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
    public RutinaFbAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rutina, parent, false);
        return new RutinaFbAdapter.ViewHolder(view);//
    }

    class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
        TextView nombre, lunes, martes, miercoles, jueves, viernes, sabado, domingo;
        ImageView imagen;
        ImageButton mas;
        String rutinaId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.rutinaImg);
            nombre = itemView.findViewById(R.id.rutinaNombre);
            lunes = itemView.findViewById(R.id.lunes);
            martes = itemView.findViewById(R.id.martes);
            miercoles = itemView.findViewById(R.id.miercoles);
            jueves = itemView.findViewById(R.id.jueves);
            viernes = itemView.findViewById(R.id.viernes);
            sabado = itemView.findViewById(R.id.sabado);
            domingo = itemView.findViewById(R.id.domingo);
            mas = itemView.findViewById(R.id.mas);
            mas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupMenu();
                }
            });
        }
        public void popupMenu() {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), mas);
            popupMenu.inflate(R.menu.popup_rutina_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId()==R.id.borrar){
                eliminarRutina(rutinaId);
                return true;
            }
            return false;
        }
    }
    /**
     * Elimina la rutina con el id pasado por parámetro tanto de la lista de rutinas como de
     * la lista de rutinas que pertenecen al usuario conectado.
     *
     * @param id
     */
    public void eliminarRutina(String id) {
        DatabaseReference ref2 = FirebaseDatabase.getInstance("https://olimplicacion-3ba86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios/" + MainActivity.getUsuarioOB().getId() + "/rutinas");
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot rut : dataSnapshot.getChildren()) {//
                    if (rut.child("id").getValue().equals(id)) {
                        ref2.child(rut.getKey()).removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("No se encuentra o hay un error");
            }
        });
    }
    public interface ItemClickListener{
        public void onItemClick(Rutina rutina);
    }

}