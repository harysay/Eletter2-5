package id.go.kebumenkab.eletterkebumen.adapter.pns;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.model.Bawahan;

/** **/

public class BawahanAdapter extends RecyclerView.Adapter<BawahanAdapter.MyViewHolder>
    implements Filterable {
    private Context mContext;
    private List<Bawahan> bawahans;
    private List<Bawahan> bawahanMaster;
    private List<Bawahan> bawahanFiltered;

    private BawahanAdapterListener listener;
    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;



    public int getSize(){
        return bawahans.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    bawahanFiltered = bawahanMaster;
                } else {

                    List<Bawahan> filteredList = new ArrayList<>();


                    for (Bawahan row : bawahanMaster) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                       if (row.getNama().toLowerCase().contains(charString.toLowerCase())
                               || row.getUnitKerja().toLowerCase().contains(charString.toLowerCase())
                               || row.getJabatan().toLowerCase().contains(charString.toLowerCase())
                               ) {
                            Log.e("Filter", row.getNama()+"/"
                                    + row.getUnitKerja()+"/"+ charString);

                            filteredList.add(row);
                        }
                    }

                    bawahanFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = bawahanFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                bawahans = (ArrayList<Bawahan>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public Bawahan getItem(int position) {
        return bawahanFiltered.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nama, jabatan;
        public CheckBox checkBox;
        public RelativeLayout container;

        public MyViewHolder(View view) {
            super(view);
            nama = (TextView) view.findViewById(R.id.nama);
            jabatan = (TextView) view.findViewById(R.id.jabatan);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            container = (RelativeLayout)view.findViewById(R.id.container);
        }


    }


    public BawahanAdapter(Context mContext, List<Bawahan> bawahans, BawahanAdapterListener listener) {
        this.mContext = mContext;
        this.bawahans = bawahans;
        this.listener = listener;
        this.bawahanFiltered = bawahans;
        this.bawahanMaster = bawahans;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_disposisi_list_bawahan, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Bawahan bawahan = bawahans.get(position);

        // displaying text view data
        holder.nama.setText(bawahan.getNama());
        holder.jabatan.setText(bawahan.getJabatan());
        holder.checkBox.setChecked(false);

        if(bawahan.isSelect()) holder.container.setBackgroundColor(Color.CYAN);
        else holder.container.setBackgroundColor(Color.WHITE);

        applyClickEvents(holder, position);

    }

    private void applyClickEvents(final MyViewHolder holder, final int position) {

        final Bawahan bawahan = bawahans.get(position);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Logger logger = new Logger();

                listener.onMessageRowClicked(position);


                logger.d("Bawahan Adapter", String.valueOf(position));


            }
        });
    }


    @Override
    public long getItemId(int position) {
        // return Integer.parseInt(bawahans.get(position).getNipBaru());
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return bawahanFiltered.size();
    }



    public int getSelectedItemCount() {
        return selectedItems.size();
    }


    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface BawahanAdapterListener {

        void onMessageRowClicked(int position);

    }


}