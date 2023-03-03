package id.go.kebumenkab.eletterkebumen.ajudan;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import id.go.kebumenkab.eletterkebumen.R;

/**
 * Created by Srijith on 08-10-2017.
 */

public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {

    TextView sectionTitle;

    public SectionHeaderViewHolder(View itemView) {
        super(itemView);
        sectionTitle = itemView.findViewById(R.id.textview_section_header);
    }

}
