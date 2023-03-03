package id.go.kebumenkab.eletterkebumen.adapter.pns;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.model.Tindakan;

/** **/

public class TindakanAdapter extends RecyclerView.Adapter<TindakanAdapter.MyViewHolder>
    {
    private Context mContext;
    private List<Tindakan> tindakans;

    private TindakanAdapterListener listener;
    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public int getSize(){
        return tindakans.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView nama, jabatan;
        public CheckBox checkBox;
        public RelativeLayout container;

        public MyViewHolder(View view) {
            super(view);
            nama = (TextView) view.findViewById(R.id.nama);
            jabatan = (TextView) view.findViewById(R.id.jabatan);
            jabatan.setVisibility(View.GONE);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            container = (RelativeLayout)view.findViewById(R.id.container);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


    public TindakanAdapter(Context mContext, List<Tindakan> tindakans, TindakanAdapterListener listener) {
        this.mContext = mContext;
        this.tindakans = tindakans;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_disposisi_list_tindakan, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Tindakan tindakan = tindakans.get(position);

        // displaying text view data
        holder.nama.setText(tindakan.getTindakan());
        holder.checkBox.setChecked(false);

        //holder.checkBox.setEnabled(false);

        applyClickEvents(holder, position);

    }

    private void applyClickEvents(final MyViewHolder holder, final int position) {

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                    holder.container.setBackgroundColor(Color.WHITE);
                }
                else {
                    holder.checkBox.setChecked(true);

                    holder.container.setBackgroundColor(Color.CYAN);
                }
                listener.onMessageRowClicked(position);
            }
        });


    }


    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public int getItemCount() {
        return tindakans.size();
    }



    public int getSelectedItemCount() {
        return selectedItems.size();
    }


    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface TindakanAdapterListener {

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }


}