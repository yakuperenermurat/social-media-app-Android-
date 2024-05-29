package com.example.nstagram.cerceve;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nstagram.Adapter.BildirimAdapter;
import com.example.nstagram.Model.Bildirim;
import com.example.nstagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BildirimFragment extends Fragment {

    private RecyclerView recyclerView;
    private BildirimAdapter bildirimAdapter;
    private List<Bildirim> bildirimListesi;

    public BildirimFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bildirim, container, false);

        recyclerView = view.findViewById(R.id.recyler_view_bildirimCercevesi);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        bildirimListesi = new ArrayList<>();
        bildirimAdapter = new BildirimAdapter(getContext(),bildirimListesi);
        recyclerView.setAdapter(bildirimAdapter);

        bildirimlkeriOku();
        return view ;
    }

    private void bildirimlkeriOku() {
        FirebaseUser mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference bildirimYolu = FirebaseDatabase.getInstance().getReference("Bildirimler").child(mevcutKullanici.getUid());

        bildirimYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bildirimListesi.clear();
                for (DataSnapshot snapshot :dataSnapshot.getChildren())
                {
                    Bildirim bildirim =snapshot.getValue(Bildirim.class);
                    bildirimListesi.add(bildirim);
                }

                Collections.reverse(bildirimListesi);
                bildirimAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}