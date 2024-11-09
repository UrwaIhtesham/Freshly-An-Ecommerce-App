package com.example.l215404.freshlyanecommerceapp.Activities.SignUp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.l215404.freshlyanecommerceapp.R;

public class GenderSpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public GenderSpinnerAdapter(Context context, String[] values) {
        super(context, R.layout.spinner_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_dropdown, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.spinnerText);
        textView.setText(values[position]);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.spinnerText);
        textView.setText(values[position]);
        return convertView;
    }
}
