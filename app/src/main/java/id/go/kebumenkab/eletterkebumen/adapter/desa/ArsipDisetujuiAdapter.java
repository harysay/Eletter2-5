package id.go.kebumenkab.eletterkebumen.adapter.desa;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.CircleTransform;
import id.go.kebumenkab.eletterkebumen.model.ArsipDisetujuiData;
import id.go.kebumenkab.eletterkebumen.model.ArsipSuratDitandatanganiDesa;

/** **/

public class ArsipDisetujuiAdapter extends RecyclerView.Adapter<ArsipDisetujuiAdapter.MyViewHolder>
    implements Filterable {
    private Context mContext;
    private List<ArsipSuratDitandatanganiDesa> messages;
    private List<ArsipSuratDitandatanganiDesa> messagesMaster;
    private List<ArsipSuratDitandatanganiDesa> messagesFiltered;

    private MessageAdapterListener listener;
    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public int getSize(){
        return messages.size();
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

                    List<ArsipSuratDitandatanganiDesa> filteredList = new ArrayList<>();


                    for (ArsipSuratDitandatanganiDesa row : messagesMaster) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match


                       if (row.getNamaPenduduk().toLowerCase().contains(charString.toLowerCase())
                               || row.getJenisSurat().toLowerCase().contains(charString.toLowerCase()) ) {
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
                messages = (ArrayList<ArsipSuratDitandatanganiDesa>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView from, subject, message, iconText, timestamp, jenis_surat;
        public ImageView iconImp, imgProfile;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;

        public MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            subject = (TextView) view.findViewById(R.id.txt_primary);
            message = (TextView) view.findViewById(R.id.txt_secondary);
            iconText = (TextView) view.findViewById(R.id.icon_text);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            jenis_surat = (TextView) view.findViewById(R.id.jumlah_koreksi);

            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
            iconImp = (ImageView) view.findViewById(R.id.icon_star);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
            messageContainer = (LinearLayout) view.findViewById(R.id.message_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


    public ArsipDisetujuiAdapter(Context mContext, List<ArsipSuratDitandatanganiDesa> messages, MessageAdapterListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.listener = listener;
        this.messagesFiltered = messages;
        this.messagesMaster = messages;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_row, parent, false);

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_row, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ArsipSuratDitandatanganiDesa message = messages.get(position);

        // displaying text view data

        if(message.getNamaPenduduk()!= null)
        holder.from.setText(message.getNamaPenduduk());

        if(message.getJenisSurat()!= null)
            holder.subject.setText(message.getJenisSurat());

        holder.subject.setVisibility(View.VISIBLE);

        if(message.getTanggalSurat()!= null)
        holder.timestamp.setText(message.getTanggalSurat());

        // displaying the first letter of From in icon text
        if(message.getNamaPenduduk()!= null)
        holder.iconText.setText(message.getNamaPenduduk().substring(0, 1).toUpperCase());

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        holder.iconImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconImportantClicked(position);
            }
        });

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
        // return Long.parseLong(messages.get(position).getIdSurat());
        return position;
    }



    @Override
    public int getItemViewType(int position) {
        return messages.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }



    @Override
    public int getItemCount() {
        return messagesFiltered.size();
    }



    public int getSelectedItemCount() {
        return selectedItems.size();
    }


    private class LoadingViewHolder extends MyViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

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