package id.go.kebumenkab.eletterkebumen.adapter.pns;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.model.Pemberitahuan;

/** **/

public class PemberitahuanAdapter extends RecyclerView.Adapter<PemberitahuanAdapter.MyViewHolder>
    implements Filterable {
    private Context mContext;
    private List<Pemberitahuan> pemberitahuans;
    private List<Pemberitahuan> messagesMaster;
    private List<Pemberitahuan> messagesFiltered;

    private MessageAdapterListener listener;
    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public int getSize(){
        return pemberitahuans.size();
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

                    List<Pemberitahuan> filteredList = new ArrayList<>();


                    for (Pemberitahuan row : messagesMaster) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                       if (row.getNamaSurat().toLowerCase().contains(charString.toLowerCase())
                               || row.getNamaPelaksana().toLowerCase().contains(charString.toLowerCase()) ) {
                            Log.e("Filter", row.getNamaSurat()+"/"
                                    + row.getNamaPelaksana()+"/"+ charString);

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
                pemberitahuans = (ArrayList<Pemberitahuan>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView from, subject, message, iconText, timestamp, jenis_pemberitahuan;
        public LinearLayout messageContainer;

        public MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            subject = (TextView) view.findViewById(R.id.txt_primary);
            message = (TextView) view.findViewById(R.id.txt_secondary);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            jenis_pemberitahuan = (TextView) view.findViewById(R.id.jumlah_koreksi);

            messageContainer = (LinearLayout) view.findViewById(R.id.message_container);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


    public PemberitahuanAdapter(Context mContext, List<Pemberitahuan> pemberitahuans, MessageAdapterListener listener) {
        this.mContext = mContext;
        this.pemberitahuans = pemberitahuans;
        this.listener = listener;
        this.messagesFiltered = pemberitahuans;
        this.messagesMaster = pemberitahuans;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pemberitahuan_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Pemberitahuan pemberitahuan = pemberitahuans.get(position);

        // displaying text view data
        holder.from.setText(pemberitahuan.getNamaPelaksana());
        holder.subject.setText(pemberitahuan.getNamaSurat());
        holder.jenis_pemberitahuan.setText(pemberitahuan.getJenis());
        holder.jenis_pemberitahuan.setVisibility(View.VISIBLE);

        if(pemberitahuan.getJenis().length()== 0 )  holder.jenis_pemberitahuan.setVisibility(View.GONE);


        long now = System.currentTimeMillis();
       // holder.timestamp.setText(DateUtils.getRelativeTimeSpanString(timeStringtoMilis(pemberitahuan.getTimestamp()), now, DateUtils.DAY_IN_MILLIS));

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        applyClickEvents(holder, position);

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {


        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

        holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return Integer.parseInt(pemberitahuans.get(position).getIdPemberitahuan());
    }



    @Override
    public int getItemCount() {
        return messagesFiltered.size();
    }



    public int getSelectedItemCount() {
        return selectedItems.size();
    }


    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface MessageAdapterListener {
        void onIconClicked(int position);

        void onIconImportantClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }

    private long tampilkanWaktu (String timestamp){
        long now = System.currentTimeMillis();
        long monthsAgo      = timeStringtoMilis(timestamp);
        return monthsAgo;
    }

    private long timeStringtoMilis(String time) {
        long milis = 0;

        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date 	= sd.parse(time);
            milis 		= date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return milis;
    }
}