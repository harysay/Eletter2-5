package id.go.kebumenkab.eletterkebumen.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.model.Histori;


public class HistoriAdapter extends RecyclerView.Adapter<HistoriAdapter.MyViewHolder> {

    /** Pembuatan variabel global **/
    private Context mContext;
    private List<Histori> historis;

    /** Inisiasi variabel komponen layout**/
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tanggal, waktu, status, user, pesan;
        public View startBar, endBar;
        public ImageView pointStatus;

        public MyViewHolder(View view) {
            super(view);
            pesan     = (TextView) view.findViewById(R.id.pesan);
            tanggal     = (TextView) view.findViewById(R.id.tanggal);
            waktu       = (TextView) view.findViewById(R.id.waktu);
            status      = (TextView) view.findViewById(R.id.status);
            user        = (TextView) view.findViewById(R.id.user);
            startBar    = (View) view.findViewById(R.id.start_bar);
            endBar      = (View) view.findViewById(R.id.end_bar);
            pointStatus = (ImageView)view.findViewById(R.id.point_status);

        }
    }

    /** Inisiasi adapter  **/
    public HistoriAdapter(Context mContext, List<Histori> historis) {
        this.mContext = mContext;
        this.historis = historis;
    }

    /** **/
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_history, parent, false);

        return new MyViewHolder(itemView);
    }


    /** Memasukkan data ke komponenen **/
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Histori histori = historis.get(position);
        int jumlahData = historis.size();

        /** parsing token **/
        StringTokenizer tk = new StringTokenizer(histori.getTimestamp());

        String date = tk.nextToken();  // <---  yyyy-mm-dd
        String time = tk.nextToken();  // <---  hh:mm:ss

        // displaying text view data
        holder.tanggal.setText(ambilTanggal(histori.getTimestamp()));
        holder.waktu.setText(ambilJam(histori.getTimestamp()));
        holder.status.setText(histori.getAlur());
        holder.user.setText("Dari "+ histori.getPengirim() +"\n\nKepada "+ histori.getPenerima());
        holder.pesan.setText(histori.getPesan());

        if(position==0) holder.startBar.setVisibility(View.INVISIBLE);
        if(position==jumlahData-1) holder.endBar.setVisibility(View.INVISIBLE);

        if(histori.getAlur().contains("koreksi"))
            holder.pointStatus.setImageResource(R.drawable.ic_radio_red_24dp);
        if(histori.getAlur().contains("setuju"))
            holder.pointStatus.setImageResource(R.drawable.ic_setuju_blue_24dp);

    }


    @Override
    public int getItemCount() {
        return historis.size();
    }

    /** Mengubah format jam **/
    public String ambilJam(String inputDate) {
       // inputDate = "2020-04-04 12:05:00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (date == null) {
            return "";
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        return timeFormat.format(date);
    }

    /** Mengubah format tanggal **/
    public String ambilTanggal(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (date == null) {
            return "";
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("d MMM");

        return timeFormat.format(date);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}