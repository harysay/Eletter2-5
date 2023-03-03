package id.go.kebumenkab.eletterkebumen.fragment.desa;

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
import id.go.kebumenkab.eletterkebumen.arsip.desa.ArsipDisetujuiActivity;
import id.go.kebumenkab.eletterkebumen.helper.Logger;
import id.go.kebumenkab.eletterkebumen.helper.NotifikasiDialog;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;
import id.go.kebumenkab.eletterkebumen.helper.Tag;


public class ArsipFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String jabatan;
    private PrefManager prefManager;
    private Logger logger;
    private NotifikasiDialog notifikasiDialog;

    private LinearLayout disetujui,  containerTitleKonsep;

    private OnFragmentInteractionListener mListener;

    public ArsipFragment() {
        // Required empty public constructor
    }


    public static ArsipFragment newInstance(String param1, String param2) {
        ArsipFragment fragment = new ArsipFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_arsip_desa, container, false);

        logger = new Logger();
        notifikasiDialog  = new NotifikasiDialog(getContext(), getActivity());
        disetujui = (LinearLayout)view.findViewById(R.id.disetujui);
        disetujui.setOnClickListener(this);
        disetujui.setVisibility(View.VISIBLE);

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
        logger = new Logger();
        logger.d("Jabatan", jabatan);

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

            case R.id.disetujui:
                bukaArsipActivity(Tag.TAG_SETUJU);
                break;

            default:
                break;
        }
    }

    public void bukaArsipActivity(String jenisArsip){

        if(jenisArsip.equals(Tag.TAG_SETUJU)){
            Intent intent = new Intent(getActivity(), ArsipDisetujuiActivity.class);
            intent.putExtra(Tag.TAG_JENISARSIP, jenisArsip);
            startActivity(intent);
        }

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
