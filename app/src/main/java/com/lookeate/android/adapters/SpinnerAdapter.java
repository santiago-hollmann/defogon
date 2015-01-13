package com.lookeate.android.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.olx.olx.LeChuckApplication;
import com.olx.olx.R;
import com.olx.smaug.api.model.Value;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<Value> {

    public SpinnerAdapter(Context context, int textViewResourceId, List<Value> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    public TextView getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setTypeface(LeChuckApplication.Fonts.HELVETICA_LIGHT);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(R.dimen.forms_text_size));
        v.setTextColor(getContext().getResources().getColor(R.color.forms_text_color));
        return v;
    }

    @Override
    public TextView getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getDropDownView(position, convertView, parent);
        v.setTypeface(LeChuckApplication.Fonts.HELVETICA_LIGHT);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(R.dimen.forms_medium_text_size));
        v.setBackgroundResource(R.drawable.spinner_selector);
        return v;
    }

}
