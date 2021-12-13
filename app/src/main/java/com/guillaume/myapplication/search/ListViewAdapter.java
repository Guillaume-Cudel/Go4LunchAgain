package com.guillaume.myapplication.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guillaume.myapplication.R;
import com.guillaume.myapplication.model.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<Restaurant> suggestedList = null;
    private ArrayList<Restaurant> arraylist;

    public ListViewAdapter(Context context, List<Restaurant> suggestedList) {
        mContext = context;
        this.suggestedList = suggestedList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Restaurant>();
        this.arraylist.addAll(suggestedList);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return suggestedList.size();
    }

    @Override
    public Restaurant getItem(int position) {
        return suggestedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(suggestedList.get(position).getName());

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        suggestedList.clear();
        if (charText.length() == 0) {
            suggestedList.addAll(arraylist);
        } else {
            for (Restaurant r : arraylist) {
                if(r.getName().toLowerCase(Locale.getDefault()).contains(charText)){
                    suggestedList.add(r);
                }
            }
        }
        notifyDataSetChanged();
    }
}
