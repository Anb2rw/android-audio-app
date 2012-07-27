package com.anb2rw.meloman;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.perm.kate.api.Audio;

public class InteractiveArrayAdapter extends ArrayAdapter<Audio> {

    private final List<Audio> list;
    private final Activity context;
    
    

    public InteractiveArrayAdapter(Activity context, List<Audio> list) {
        super(context, R.layout.rowbuttonlayout, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
    	public ImageView imageView;
        public TextView text;
    }

    int pos;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        this.pos=position;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.rowbuttonlayout, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.icon);
//            viewHolder.button = (Button) view.findViewById(R.id.play);
//            viewHolder.checkbox
//                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//                        public void onCheckedChanged(CompoundButton buttonView,
//                                boolean isChecked) {
////                        	Audio element = (Audio) viewHolder.checkbox
////                                    .getTag();
//////                            element.setSelected(buttonView.isChecked());
//
//                        }
//                    });
            view.setTag(viewHolder);
//            viewHolder.checkbox.setTag(list.get(position));
        } else {
            view = convertView;
//            ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        String str=list.get(position).artist+" - "+list.get(position).title;
        holder.text.setText(str);
//        holder.checkbox.setChecked(false);
        return view;
    }
}
