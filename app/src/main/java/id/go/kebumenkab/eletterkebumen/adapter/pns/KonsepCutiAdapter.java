package id.go.kebumenkab.eletterkebumen.adapter.pns;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.CircleTransform;
import id.go.kebumenkab.eletterkebumen.model.DataItemCuti;
import id.go.kebumenkab.eletterkebumen.model.Konsep;

/** **/

public class KonsepCutiAdapter extends RecyclerView.Adapter<KonsepCutiAdapter.MyViewHolder>
    implements Filterable {
    private Context mContext;
    private List<DataItemCuti> konseps;
    private List<DataItemCuti> messagesMaster;
    private List<DataItemCuti> messagesFiltered;

    private MessageAdapterListenerCuti listener;
    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public int getSize(){
        return konseps.size();
    }


    public void ambilKonsepBelumDitandai(){

        List<DataItemCuti> konsepCategorized = new ArrayList<DataItemCuti>();

        for(DataItemCuti row : messagesMaster){
            konsepCategorized.add(row);
//            if (row.getTandai().toLowerCase().contains("0") ) {
//                Log.e("Filter", row.getFrom()+"/"+ row.getStatus());
//                konsepCategorized.add(row);
//            }
        }

        messagesFiltered = konsepCategorized;

        konseps = messagesFiltered;
        notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    messagesFiltered = messagesMaster;
                } else {
                    List<DataItemCuti> filteredList = new ArrayList<>();
                    for (DataItemCuti row : messagesMaster) {
                       if (row.getPemohon().getNama().toLowerCase().contains(charString.toLowerCase())
                               || row.getCuti().getJenis().toLowerCase().contains(charString.toLowerCase()) ) {
                            Log.e("Filter", row.getPemohon().getNama()+"/"
                                    + row.getCuti().getJenis()+"/"+ charString);

                            filteredList.add(row);
                        }
                    }
                    messagesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = messagesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                konseps = (ArrayList<DataItemCuti>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView from, subject, message, iconText, timestamp, jumlah_koreksi;
        public ImageView iconImp, imgProfile;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;

        public MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.fromcuti);
            subject = (TextView) view.findViewById(R.id.txt_primarycuti);
            message = (TextView) view.findViewById(R.id.txt_secondarycuti);
            iconText = (TextView) view.findViewById(R.id.icon_textcuti);
            timestamp = (TextView) view.findViewById(R.id.timestampcuti);
            jumlah_koreksi = (TextView) view.findViewById(R.id.jumlah_koreksicuti);

            iconFront = (RelativeLayout) view.findViewById(R.id.icon_frontcuti);
            iconImp = (ImageView) view.findViewById(R.id.icon_starcuti);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profilecuti);
            messageContainer = (LinearLayout) view.findViewById(R.id.message_containercuti);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_containercuti);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClickedCuti(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


    public KonsepCutiAdapter(Context mContext, List<DataItemCuti> konseps, MessageAdapterListenerCuti listener) {

        this.mContext = mContext;
        this.konseps = konseps;
        this.listener = listener;
        this.messagesFiltered = konseps;
        this.messagesMaster = konseps;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_row_cuti, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        DataItemCuti konsep = konseps.get(position);

        // displaying text view data
        holder.from.setText(properCase(konsep.getPemohon().getNama()));
        holder.subject.setText(konsep.getCuti().getMulaiCuti()+" s.d "+ konsep.getCuti().getSelesaiCuti());
        holder.message.setText(konsep.getPemohon().getUnitKerja());

        holder.jumlah_koreksi.setText(konsep.getCuti().getJenis());
        holder.jumlah_koreksi.setVisibility(View.VISIBLE);

//        if(konsep.getPernahDikoreksi().equals("0"))  holder.jumlah_koreksi.setVisibility(View.GONE);

        long now = System.currentTimeMillis();
        holder.timestamp.setText(konsep.getPemohon().getUnitKerja());

        // displaying the first letter of From in icon text
        holder.iconText.setText(konsep.getPemohon().getNama().substring(0, 1).toUpperCase());

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));
        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClickedCuti(position);
            }
        });

        holder.iconImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconImportantClickedCuti(position);
            }
        });

        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClickedCuti(position);
            }
        });

        holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClickedCuti(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    String properCase (String inputVal) {
        if (inputVal.length() == 0) return "";
        if (inputVal.length() == 1) return inputVal.toUpperCase();
        return inputVal.substring(0,1).toUpperCase()
                + inputVal.substring(1).toLowerCase();
    }

        // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }



    @Override
    public long getItemId(int position) {
        return Long.parseLong(konseps.get(position).getCutiId());
    }
    @Override
    public int getItemCount() {
        return messagesFiltered.size();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }


    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }



    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }
    public interface MessageAdapterListenerCuti {
        void onIconClickedCuti(int position);

        void onIconImportantClickedCuti(int position);

        void onMessageRowClickedCuti(int position);

        void onRowLongClickedCuti(int position);
    }
}