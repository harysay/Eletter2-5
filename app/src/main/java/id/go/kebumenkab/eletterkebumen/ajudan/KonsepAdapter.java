package id.go.kebumenkab.eletterkebumen.ajudan;

import android.content.Context;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.CircleTransform;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.model.Konsep;

/** **/

public class KonsepAdapter extends RecyclerView.Adapter<KonsepAdapter.MyViewHolder>
    implements Filterable , SwipeAndDragHelper.ActionCompletionContract{
    private Context mContext;
    private List<Konsep> konseps;
    private List<Konsep> messagesMaster;
    private List<Konsep> messagesFiltered;

    private MessageAdapterListener listener;
    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    private ItemTouchHelper touchHelper;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public int getSize(){
        return konseps.size();
    }

    public void getMessageByStatus(CharSequence charSequence){
        String status = charSequence.toString().trim();

        if(status.length() > 0){
            List<Konsep> konsepCategorized = new ArrayList<Konsep>();

            for(Konsep row : messagesMaster){
                if (row.getStatus().toLowerCase().contains(status.toLowerCase()) ) {
                    Log.e("Filter", row.getFrom()+"/"+ row.getStatus()+"/"+ status);
                    konsepCategorized.add(row);
                }
            }

            messagesFiltered = konsepCategorized;
        }else{
            messagesFiltered = messagesMaster;
        }

         konseps = messagesFiltered;
         notifyDataSetChanged();
    }

    public void getMessageIsRead(){

        List<Konsep> konsepCategorized = new ArrayList<Konsep>();

        for(Konsep row : messagesMaster){
            if (row.getIsRead().toLowerCase().contains("1") ) {
                Log.e("Filter", row.getFrom()+"/"+ row.getStatus());
                konsepCategorized.add(row);
            }
        }

        messagesFiltered = konsepCategorized;

        konseps = messagesFiltered;
        notifyDataSetChanged();
    }

    public void getMessageUnRead(){

        List<Konsep> konsepCategorized = new ArrayList<Konsep>();

        for(Konsep row : messagesMaster){
            if (row.getIsRead().toLowerCase().contains("0") ) {
                Log.e("Filter", row.getFrom()+"/"+ row.getStatus());
                konsepCategorized.add(row);
            }
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

                    List<Konsep> filteredList = new ArrayList<>();


                    for (Konsep row : messagesMaster) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match


                       if (row.getFrom().toLowerCase().contains(charString.toLowerCase())
                               || row.getSubject().toLowerCase().contains(charString.toLowerCase()) ) {
                            Log.e("Filter", row.getFrom()+"/"
                                    + row.getSubject()+"/"+ charString);

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
                konseps = (ArrayList<Konsep>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Konsep target  =konseps.get(oldPosition);
        Konsep konsep = new Konsep(target);

        konseps.remove(oldPosition);
        konseps.add(newPosition, konsep);


        notifyItemMoved(oldPosition, newPosition);
        sendOrderAjudan(konseps);
    }

    @Override
    public void onViewSwiped(int position) {
        //konseps.remove(position);
       // notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView from, subject, message, iconText, timestamp, jumlah_koreksi;
        public ImageView iconImp, imgProfile;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer, iconBack, iconFront, layoutParent;

        public MyViewHolder(View view) {
            super(view);

            from = (TextView) view.findViewById(R.id.from);
            subject = (TextView) view.findViewById(R.id.txt_primary);
            message = (TextView) view.findViewById(R.id.txt_secondary);
            iconText = (TextView) view.findViewById(R.id.icon_text);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            jumlah_koreksi = (TextView) view.findViewById(R.id.jumlah_koreksi);

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


    public KonsepAdapter(Context mContext, List<Konsep> konseps, MessageAdapterListener listener) {
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
                .inflate(R.layout.message_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Konsep konsep = konseps.get(position);


        holder.iconContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    touchHelper.startDrag(holder);
                }
                return false;
            }
        });
        // displaying text view data
        holder.from.setText(konsep.getFrom());
        holder.subject.setText(konsep.getStatus()+" : "+ konsep.getSubject());
        holder.message.setText(konsep.getMessage());

        holder.jumlah_koreksi.setText("dikoreksi "+ konsep.getPernahDikoreksi()+" kali");
        holder.jumlah_koreksi.setVisibility(View.VISIBLE);

        if(konsep.getPernahDikoreksi().equals("0"))  holder.jumlah_koreksi.setVisibility(View.GONE);


        long now = System.currentTimeMillis();
        holder.timestamp.setText(DateUtils.getRelativeTimeSpanString(timeStringtoMilis(konsep.getTimestamp()), now, DateUtils.DAY_IN_MILLIS));

        // displaying the first letter of From in icon text
        holder.iconText.setText(konsep.getFrom().substring(0, 1));

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        // change the font style depending on konsep read status
        //applyReadStatus(holder, konsep);

        // handle konsep star
       // applyImportant(holder, konsep);

        // handle icon animation
        //applyIconAnimation(holder, position);

        // display profile image
        applyProfilePicture(holder, konsep);

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

    private void applyProfilePicture(MyViewHolder holder, Konsep konsep) {
        if (!TextUtils.isEmpty(konsep.getPicture())) {
            Glide.with(mContext).load(konsep.getPicture())
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgProfile);
            holder.imgProfile.setColorFilter(null);
            holder.iconText.setVisibility(View.GONE);
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle);
            holder.imgProfile.setColorFilter(konsep.getColor());
            holder.iconText.setVisibility(View.VISIBLE);
        }
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
        return konseps.get(position).getId();
    }

    private void applyImportant(MyViewHolder holder, Konsep konsep) {
        if (konsep.isIsImportant()) {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_selected));
        } else {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_border_white_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_normal));
        }
    }

    private void applyReadStatus(MyViewHolder holder, Konsep konsep) {
        if (konsep.getIsRead().equalsIgnoreCase("1")) {
            holder.from.setTypeface(null, Typeface.NORMAL);
            holder.subject.setTypeface(null, Typeface.NORMAL);
            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.konsep));
        } else {
            holder.from.setTypeface(null, Typeface.BOLD);
            holder.subject.setTypeface(null, Typeface.BOLD);
            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.from));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
        }
    }

    @Override
    public int getItemCount() {
        return messagesFiltered.size();
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

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
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

    public void sendOrderAjudan(final List<Konsep> listKonsep) {
        PrefManager prefManager = new PrefManager(mContext);
        final String token = prefManager.getSessionToken();
        if (token == null) {
            Toast.makeText(mContext, "Token tidak ada", Toast.LENGTH_LONG).show();
            return;
        }else{
            // Karena belum ada API  nya maka dinonaktifkan

            /*StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_AJUDAN_ORDER_KONSEP,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Login", response.toString());
                            try {
                                JSONObject obj = new JSONObject(response);
                                Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    for(int i=1;i<listKonsep.size();i++){
                        Konsep konsep = listKonsep.get(i);
                        params.put("history["+konsep.getIdHistory()+"]", String.valueOf(i));
                        Log.d("histori", "history["+konsep.getIdHistory()+"]="+String.valueOf(i));
                    }

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", token);
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(stringRequest);*/
        }


    }
}