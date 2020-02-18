package com.khoa.calculator.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.calculator.R;
import com.khoa.calculator.model.Calculation;

import java.util.ArrayList;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {

    private ArrayList<Calculation> calList;
    private ItemClickListener listener;

    public AdapterHistory(ItemClickListener listener) {
        this.listener = listener;
    }

    public void setCalList(ArrayList<Calculation> calList){
        this.calList = calList;
        notifyDataSetChanged();
    }

    public void insertCal(Calculation cal){
        this.calList.add(cal);
        notifyItemInserted(calList.size()-1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calculator, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.bind(calList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(calList.get(position).getResult());
            }
        });
    }

    @Override
    public int getItemCount() {
        return calList ==null ? 0 : calList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtCalculation;
        private TextView txtResult;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCalculation = itemView.findViewById(R.id.txt_calculation);
            txtResult = itemView.findViewById(R.id.txt_result);
        }

        public void bind(Calculation cal){
            txtCalculation.setText(cal.toCalculation());
            txtResult.setText(cal.getResult());
        }
    }

    public interface ItemClickListener{
        public void onClick(String result);
    }

}
