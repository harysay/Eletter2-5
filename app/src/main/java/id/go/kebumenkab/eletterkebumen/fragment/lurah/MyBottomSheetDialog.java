package id.go.kebumenkab.eletterkebumen.fragment.lurah;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import id.go.kebumenkab.eletterkebumen.R;

public class MyBottomSheetDialog extends BottomSheetDialogFragment
    {
        private BottomSheetListener mListener;
        public static final String TAG = "Eletter";
        public String pesan;
        public int kode;

    public MyBottomSheetDialog(int kode, String pesan){
        this.kode =  kode;
        this.pesan = pesan;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

        TextView tvDetail = (TextView) view.findViewById(R.id.tv_detail);
        tvDetail.setText(pesan);

        view.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked("Close");
                dismiss();
            }
        });

        view.findViewById(R.id.button_ok).setVisibility(View.GONE);

        if(kode == 2){
            view.findViewById(R.id.button_ok).setVisibility(View.VISIBLE);
            view.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onButtonClicked("Update");
                    dismiss();
                }
            });
        }

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);



        }

        @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }
}
