package com.example.overlay;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListViewList> listViewLists = new ArrayList<ListViewList>();

    public ListViewAdapter() {}
    @Override
    public int getCount() {
        return listViewLists.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // listview_item의 layout을 inflate하여 xml을 view로 만들고 convertView 참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item,parent,false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1);
        TextView descTextView = (TextView) convertView.findViewById(R.id.textView2);

        // Data Set (listViewItemList) 에서 position에 위치한 데이터참조 획득
        ListViewList listViewItem = listViewLists.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getIconDrawable());
        titleTextView.setText(listViewItem.getName());
        descTextView.setText(listViewItem.getId()+"");

        return convertView;
    }
    public void addItem(Drawable icon, String title, int desc) {
        ListViewList item = new ListViewList();
        item.setIconDrawable(icon);
        item.setName(title);
        item.setId(desc);
        listViewLists.add(item);
    }

    // item 삭제
    public void delItem(int position) {
        listViewLists.remove(position);
    }
}
