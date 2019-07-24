package com.example.notify.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.notify.R;
import com.example.notify.model.EventModel;

import java.io.FileInputStream;
import java.util.List;

public class EventAdapter extends BaseAdapter  {

    private List<EventModel> eventsList;
    private Activity mActivity;

    public EventAdapter(Activity activity, List<EventModel> eventsList) {
        this.mActivity = activity;
        this.eventsList = eventsList;
    }


    @Override
    public int getCount() {
        return  eventsList.size();
    }

    @Override
    public Object getItem(int position) {
        return  eventsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewholder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_items, parent, false);

            viewholder = new ViewHolder();

            viewholder.name = convertView.findViewById(R.id.text_name);
            viewholder.location = convertView.findViewById(R.id.text_location);
            viewholder.date = convertView.findViewById(R.id.text_date);
            viewholder.poster = convertView.findViewById(R.id.poster_img);

            convertView.setTag(viewholder);

        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        setEventItem(viewholder,  eventsList.get(position));

        return convertView;
    }

    /**
     * Set data in list item
     *
     * @param viewholder
     * @param events
     */
    private void setEventItem(ViewHolder viewholder, EventModel events) {
        viewholder.name.setText(events.getName());
        viewholder.location.setText(events.getLocation());
        viewholder.date.setText(events.getDate().toString());
        FileInputStream file = null;
        if(events.getposter() != null && !events.getposter().trim().isEmpty()) {
            viewholder.poster.setImageResource(R.drawable.ocr);
        }
    }


    private static class ViewHolder {
        private TextView name;
        private TextView location;
        private TextView date;
        private ImageView poster;
    }

}
