package com.example.gymgameproject.tabloid;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

public class NewsFbAdapter extends FirebaseRecyclerAdapter<Noticia, NoticiaFbAdapter.ViewHolder> {

    private NoticiaFbAdapter.ItemClickListener clickListener;
    public NoticiaFbAdapter(@NonNull FirebaseRecyclerOptions<Noticia> options, NoticiaFbAdapter.ItemClickListener clickListener) {
        super(options);
        this.clickListener = clickListener;
    }
    @NonNull
    @Override
    public NoticiaFbAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noticia, parent, false);
        return new NoticiaFbAdapter.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Noticia model) {
        holder.bind(model);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

        TextView titulo,subhead;
        ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.title);
            imagen = itemView.findViewById(R.id.header_image);
            subhead = itemView.findViewById(R.id.subhead);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return false;
        }
        @SuppressLint("ResourceAsColor")
        public void bind(Noticia model){
            titulo.setText(model.getTitulo());
            subhead.setText(model.getSubtitulo());
            if(model.getImagen()!=null){
                Picasso.get()
                        .load(model.getImagen())
                        .resize(120, 160)
                        .centerCrop()
                        .into(imagen);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(model);
                }
            });
        }
    }
    public interface ItemClickListener{
        public void onItemClick(Noticia noticia);
    }
}
