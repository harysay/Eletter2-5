package id.go.kebumenkab.eletterkebumen.adapter.pns;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.model.DetailItemKhusus;

public class DetailItemKhususAdapter extends RecyclerView.Adapter<DetailItemKhususAdapter.DetailViewHolder> {
    private ArrayList<DetailItemKhusus> detailItems;

    public DetailItemKhususAdapter(ArrayList<DetailItemKhusus> detailItems) {
        this.detailItems = detailItems;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_detail_konsep_khusus_item, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        DetailItemKhusus detailItem = detailItems.get(position);
        holder.labelTextView.setText(detailItem.getLabel());
        holder.valueTextView.setText(detailItem.getValue());
    }

    @Override
    public int getItemCount() {
        return detailItems.size();
    }

    public static class DetailViewHolder extends RecyclerView.ViewHolder {
        public TextView labelTextView;
        public TextView valueTextView;

        public DetailViewHolder(View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.labelTextView);
            valueTextView = itemView.findViewById(R.id.valueTextView);
        }
    }
}
