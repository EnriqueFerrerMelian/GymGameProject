package com.example.gymgameproject.tabloid;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gymgameproject.MainMenu;
import com.example.gymgameproject.R;
import com.example.gymgameproject.classes.AppHelper;
import com.example.gymgameproject.classes.News;
import com.example.gymgameproject.databinding.FragmentNewsDetailBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsDetailFragment extends Fragment {
    FragmentNewsDetailBinding binding;
    private static News noticia;
    public NewsDetailFragment() {
        // Required empty public constructor
    }
    public static NewsDetailFragment newInstance(News noticiaF) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        noticia = noticiaF;
        AppHelper.cambiarToolbarText(noticia.getTitulo());
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewsDetailBinding.inflate(inflater, container, false);
        ((MainMenu) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AppHelper.cargarNoticia(binding, getContext(), noticia);
        return binding.getRoot();
    }
}