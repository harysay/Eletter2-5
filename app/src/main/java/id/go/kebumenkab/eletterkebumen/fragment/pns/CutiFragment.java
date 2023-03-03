package id.go.kebumenkab.eletterkebumen.fragment.pns;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.arsip.ArsipDiajukanActivity;
import id.go.kebumenkab.eletterkebumen.arsip.ArsipDikoreksiActivity;
import id.go.kebumenkab.eletterkebumen.arsip.ArsipDisetujuiActivity;
import id.go.kebumenkab.eletterkebumen.arsip.ArsipDisposisiActivity;
import id.go.kebumenkab.eletterkebumen.arsip.ArsipDitelaahActivity;
import id.go.kebumenkab.eletterkebumen.arsip.ArsipDitindaklanjutiActivity;
import id.go.kebumenkab.eletterkebumen.arsip.ArsipLainActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;


public class CutiFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String jabatan;
    private String unit_kerja;
    private PrefManager prefManager;
    private Logger logger;
    private NotifikasiDialog notifikasiDialog;

    private LinearLayout diajukan, dikoreksi, disetujui, ditelaah, didisposisi, dikerjakan,
            ditindaklanjuti, tembusan, lainnya, containerTitleKonsep, disetujui_lurah;

    private OnFragmentInteractionListener mListener;

    public CutiFragment() {
        // Required empty public constructor
    }


    public static CutiFragment newInstance(String param1, String param2) {
        CutiFragment fragment = new CutiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_arsip, container, false);

        logger = new Logger();
        notifikasiDialog  = new NotifikasiDialog(getContext(), getActivity());


        diajukan = (LinearLayout)view.findViewById(R.id.diajukan);
        dikoreksi = (LinearLayout)view.findViewById(R.id.dikoreksi);
        disetujui = (LinearLayout)view.findViewById(R.id.disetujui);
        disetujui_lurah = (LinearLayout)view.findViewById(R.id.disetujui_lurah);
        ditelaah = (LinearLayout)view.findViewById(R.id.ditelaah);
        didisposisi = (LinearLayout)view.findViewById(R.id.didisposisi);
        dikerjakan = (LinearLayout)view.findViewById(R.id.dikerjakan);
        ditindaklanjuti = (LinearLayout)view.findViewById(R.id.ditindaklanjuti);
        tembusan     = (LinearLayout)view.findViewById(R.id.tembusan);
        lainnya     = (LinearLayout)view.findViewById(R.id.lainnya);
        containerTitleKonsep = (LinearLayout)view.findViewById(R.id.container_title_konsep);

        disetujui_lurah.setVisibility(View.GONE); // selain lurah disembunyikan

        diajukan.setOnClickListener(this);
        dikoreksi.setOnClickListener(this);
        disetujui.setOnClickListener(this);
        disetujui_lurah.setOnClickListener(this);
        ditelaah.setOnClickListener(this);
        didisposisi.setOnClickListener(this);
        dikerjakan.setOnClickListener(this);
        ditindaklanjuti.setOnClickListener(this);
        tembusan.setOnClickListener(this);
        lainnya.setOnClickListener(this);


        tembusan.setVisibility(View.GONE);

        if(jabatan.equals("kabid")){
            diajukan.setVisibility(View.VISIBLE);
            dikoreksi.setVisibility(View.VISIBLE);
            disetujui.setVisibility(View.GONE);
            ditelaah.setVisibility(View.GONE);
            didisposisi.setVisibility(View.VISIBLE);
            dikerjakan.setVisibility(View.GONE);
            ditindaklanjuti.setVisibility(View.VISIBLE);
            tembusan.setVisibility(View.GONE);
            lainnya.setVisibility(View.VISIBLE);
        }

        if(jabatan.equals("kasi")){
            diajukan.setVisibility(View.VISIBLE);
            dikoreksi.setVisibility(View.VISIBLE);
            disetujui.setVisibility(View.GONE);
            ditelaah.setVisibility(View.GONE);
            didisposisi.setVisibility(View.VISIBLE);
            dikerjakan.setVisibility(View.GONE);
            ditindaklanjuti.setVisibility(View.VISIBLE);
            tembusan.setVisibility(View.GONE);
            lainnya.setVisibility(View.VISIBLE);
        }
        if(jabatan.equals("kasubbid")){
            diajukan.setVisibility(View.VISIBLE);
            dikoreksi.setVisibility(View.VISIBLE);
            disetujui.setVisibility(View.GONE);
            ditelaah.setVisibility(View.GONE);
            didisposisi.setVisibility(View.VISIBLE);
            dikerjakan.setVisibility(View.GONE);
            ditindaklanjuti.setVisibility(View.VISIBLE);
            tembusan.setVisibility(View.GONE);
            lainnya.setVisibility(View.VISIBLE);
        }

        if(jabatan.equals("kasubbag")){
            diajukan.setVisibility(View.VISIBLE);
            dikoreksi.setVisibility(View.VISIBLE);
            disetujui.setVisibility(View.GONE);
            ditelaah.setVisibility(View.VISIBLE);

            didisposisi.setVisibility(View.VISIBLE);
            dikerjakan.setVisibility(View.GONE);
            ditindaklanjuti.setVisibility(View.VISIBLE);
            tembusan.setVisibility(View.GONE);
            lainnya.setVisibility(View.VISIBLE);
        }

        if(jabatan.equals("kasubbagtu")){
            diajukan.setVisibility(View.VISIBLE);
            dikoreksi.setVisibility(View.VISIBLE);
            disetujui.setVisibility(View.GONE);
            ditelaah.setVisibility(View.VISIBLE);

            didisposisi.setVisibility(View.VISIBLE);
            dikerjakan.setVisibility(View.GONE);
            ditindaklanjuti.setVisibility(View.VISIBLE);
            tembusan.setVisibility(View.GONE);
            lainnya.setVisibility(View.VISIBLE);
        }

        if(jabatan.equals("")){
            diajukan.setVisibility(View.GONE);
            dikoreksi.setVisibility(View.GONE);
            disetujui.setVisibility(View.GONE);
            ditelaah.setVisibility(View.GONE);

            didisposisi.setVisibility(View.GONE);
            dikerjakan.setVisibility(View.GONE);
            ditindaklanjuti.setVisibility(View.VISIBLE);
            tembusan.setVisibility(View.GONE);
            lainnya.setVisibility(View.VISIBLE);
            containerTitleKonsep.setVisibility(View.GONE);
        }

        if(unit_kerja.toLowerCase().contains("lurah")){

            dikoreksi.setVisibility(View.VISIBLE);
            disetujui.setVisibility(View.VISIBLE);
            disetujui_lurah.setVisibility(View.VISIBLE);
            didisposisi.setVisibility(View.VISIBLE);
            dikerjakan.setVisibility(View.VISIBLE);
            ditindaklanjuti.setVisibility(View.VISIBLE);

            diajukan.setVisibility(View.GONE);
            tembusan.setVisibility(View.GONE);
            lainnya.setVisibility(View.VISIBLE);
            containerTitleKonsep.setVisibility(View.GONE);
        }


        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        prefManager = new PrefManager(context);
        jabatan = prefManager.getStatusJabatan();
        unit_kerja = prefManager.getSessionUnit();

        logger = new Logger();
        logger.d("Arsip jabatan dan unit", jabatan +","+ unit_kerja);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.diajukan:
                bukaArsipActivity(Tag.TAG_AJUAN);
                break;
            case R.id.dikoreksi:
                bukaArsipActivity(Tag.TAG_KOREKSI);
                break;
                case R.id.disetujui_lurah:
                bukaArsipActivity(Tag.TAG_SETUJU_LURAH);
                break;
            case R.id.disetujui:
                bukaArsipActivity(Tag.TAG_SETUJU);
                break;
            case R.id.ditelaah:
                bukaArsipActivity(Tag.TAG_TELAAH);
                break;
            case R.id.didisposisi:
                bukaArsipActivity(Tag.TAG_DISPOSISI);
                break;
            case R.id.dikerjakan:
                bukaArsipActivity(Tag.TAG_KERJAKAN);
                break;
            case R.id.ditindaklanjuti:
                bukaArsipActivity(Tag.TAG_TINDAKLANJUTI);
                break;
            case R.id.tembusan:
                bukaArsipActivity(Tag.TAG_TEMBUSAN);
                break;
            case R.id.lainnya:
                bukaArsipActivity(Tag.TAG_LAINNYA);
                break;
            default:
                break;
        }
    }

    public void bukaArsipActivity(String jenisArsip){

        if(jenisArsip.equalsIgnoreCase(Tag.TAG_DISPOSISI)){
            Intent intent = new Intent(getActivity(), ArsipDisposisiActivity.class);
            intent.putExtra(Tag.TAG_JENISARSIP, jenisArsip);
            startActivity(intent);
        }

        if(jenisArsip.equalsIgnoreCase(Tag.TAG_AJUAN)){
            Intent intent = new Intent(getActivity(), ArsipDiajukanActivity.class);
            intent.putExtra(Tag.TAG_JENISARSIP, jenisArsip);
            startActivity(intent);
        }

        if(jenisArsip.equals(Tag.TAG_KOREKSI) ){
            Intent intent = new Intent(getActivity(), ArsipDikoreksiActivity.class);
            intent.putExtra(Tag.TAG_JENISARSIP, jenisArsip);
            startActivity(intent);
        }

        if(jenisArsip.equals(Tag.TAG_SETUJU)){
            Intent intent = new Intent(getActivity(), ArsipDisetujuiActivity.class);
            intent.putExtra(Tag.TAG_JENISARSIP, jenisArsip);
            startActivity(intent);
        }

        if(jenisArsip.equals(Tag.TAG_SETUJU_LURAH)){
            Intent intent = new Intent(getActivity(), id.go.kebumenkab.eletterkebumen.arsip.desa.ArsipDisetujuiActivity.class);
            intent.putExtra(Tag.TAG_JENISARSIP, Tag.TAG_SETUJU);
            startActivity(intent);
        }

        if(jenisArsip.equalsIgnoreCase(Tag.TAG_TELAAH)){
            Intent intent = new Intent(getActivity(), ArsipDitelaahActivity.class);
            intent.putExtra(Tag.TAG_JENISARSIP, jenisArsip);
            startActivity(intent);
        }

        if(jenisArsip.equalsIgnoreCase(Tag.TAG_TINDAKLANJUTI)){
            Intent intent = new Intent(getActivity(), ArsipDitindaklanjutiActivity.class);
            intent.putExtra(Tag.TAG_JENISARSIP, jenisArsip);
            startActivity(intent);
        }

        if(jenisArsip.equals(Tag.TAG_LAINNYA)){
            Intent intent = new Intent(getActivity(), ArsipLainActivity.class);
            intent.putExtra(Tag.TAG_JENISARSIP, jenisArsip);
            startActivity(intent);
        }

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
