package id.go.kebumenkab.eletterkebumen.adapter.pns;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.model.Penerima;

import static id.go.kebumenkab.eletterkebumen.R.id.image;

/** **/

public class PenerimaAdapter extends RecyclerView.Adapter<PenerimaAdapter.MyViewHolder>
    implements Filterable {
    private Context mContext;
    private List<Penerima> bawahans;
    private List<Penerima> bawahanMaster;
    private List<Penerima> bawahanFiltered;

    private AdapterListener listener;
    private SparseBooleanArray selectedItems;

    private boolean reverseAllAnimations = false;
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

                    List<Penerima> filteredList = new ArrayList<>();


                    for (Penerima row : bawahanMaster) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                       if (row.getNama().toLowerCase().contains(charString.toLowerCase())) {
                            Log.e("Filter", row.getNama());

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
                bawahans = (ArrayList<Penerima>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public Penerima getItem(int position) {
        return bawahanFiltered.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nama, jabatan;
        public ImageView imageTick;
        public RelativeLayout container;

        public MyViewHolder(View view) {
            super(view);
            nama = (TextView) view.findViewById(R.id.nama);
            jabatan = (TextView) view.findViewById(R.id.jabatan);
            imageTick = (ImageView) view.findViewById(image);
            container = (RelativeLayout)view.findViewById(R.id.container);
        }


    }


    public PenerimaAdapter(Context mContext, List<Penerima> bawahans, AdapterListener listener) {
        this.mContext = mContext;
        this.bawahans = bawahans;
        this.listener = listener;
        this.bawahanFiltered = bawahans;
        this.bawahanMaster = bawahans;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_disposisi_penerima, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Penerima bawahan = bawahans.get(position);

        // displaying text view data
        holder.nama.setText(bawahan.getNama());
        holder.jabatan.setText(bawahan.getJabatan());

        if(bawahan.getProses().equalsIgnoreCase("0")) {
//            holder.container.setBackgroundColor(Color.LTGRAY);
            holder.imageTick.setImageResource(R.drawable.single_tick);
            /*if(Build.MANUFACTURER.contains("Xiaomi")){
                holder.imageTick.setImageDrawable(VectorDrawableCompat.create(mContext.getResources(), R.drawable.single_tick, null));

            }else{
                holder.imageTick.setImageDrawable(mContext.getResources().getDrawable(R.drawable.single_tick));
            }*/
        }else{
//            holder.container.setBackgroundColor(Color.LTGRAY);
            holder.imageTick.setImageResource(R.drawable.double_tick);
           /* if(Build.MANUFACTURER.contains("Xiaomi")){
                holder.imageTick.setImageDrawable(VectorDrawableCompat.create(mContext.getResources(), R.drawable.double_tick, null));

            }else{
                holder.imageTick.setImageDrawable(mContext.getResources().getDrawable(R.drawable.double_tick));
            }*/
        }

        applyClickEvents(holder, position);

    }

    private void applyClickEvents(final MyViewHolder holder, final int position) {

        final Penerima bawahan = bawahans.get(position);

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


    public interface AdapterListener {

        void onMessageRowClicked(int position);

    }


}