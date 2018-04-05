package com.upv.rosiebelt.safefit.utility;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.upv.rosiebelt.safefit.R;

import java.util.ArrayList;

public class MedRecordAdapter extends RecyclerView.Adapter<MedRecordAdapter.ViewHolder>{
    private ArrayList<String[]> dataset;
    public MedRecordAdapter(ArrayList<String[]> dataset){
        this.dataset = dataset;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView content;
        public TextView label;
        public ViewHolder(View v){
            super(v);
            label = (TextView) v.findViewById(R.id.label);
            content = (TextView) v.findViewById(R.id.content);
        }
    }

    @Override
    public MedRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.md_cardview, parent, false);
        MedRecordAdapter.ViewHolder vh = new MedRecordAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.label.setText(dataset.get(position)[0]);
        holder.content.setText(dataset.get(position)[1]);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
