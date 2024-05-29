package com.example.nstagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nstagram.Model.Gonderi;
import com.example.nstagram.R;
import com.example.nstagram.cerceve.GonderiDetayiFragment;

import java.util.List;

public class FotografAdapter extends RecyclerView.Adapter<FotografAdapter.ViewHolder>{

    private Context context;
    private List <Gonderi> mGonderiler;

    public FotografAdapter(Context context, List mGonderiler) {
        this.context = context;
        this.mGonderiler = mGonderiler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fotograflar_ogesi,viewGroup,false);
        return new FotografAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Gonderi gonderi = mGonderiler.get(i);

        Glide.with(context).load(gonderi.getGonderiResmi()).into(viewHolder.gonderi_resmi);

        //Gonderi resmine tıklandiğinda
        viewHolder.gonderi_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREPS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",gonderi.getGonderiId());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new GonderiDetayiFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGonderiler.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView gonderi_resmi;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gonderi_resmi = itemView.findViewById(R.id.gonderi_resmi_fotograflarOgesi);
        }
    }

}
