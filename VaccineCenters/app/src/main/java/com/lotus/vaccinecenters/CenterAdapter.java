package com.lotus.vaccinecenters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CenterAdapter extends BaseAdapter {    // 커스텀 리스트 어댑터

    private final List<Center> list;


    public CenterAdapter(List<Center> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        if (convertView == null) {
            // 아이템 뷰를 인플레이트한다
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.center_item, parent, false);
        }

        // 위젯을 획득한다
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewFacility = convertView.findViewById(R.id.textViewFacility);
        TextView textViewAddress = convertView.findViewById(R.id.textViewAddress);
        TextView textViewPhone = convertView.findViewById(R.id.textViewPhone);

        // 아이템을 위젯에 뿌려준다
        Center center = list.get(position);

        textViewName.setText(center.getName());
        textViewFacility.setText(center.getFacility());
        textViewAddress.setText(center.getAddress());
        textViewPhone.setText(center.getPhone());

        return convertView;
    }
}










