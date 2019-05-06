package com.lptv_12110101.library;

import com.lptv_12110101.R;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareListAdapter extends ArrayAdapter<Object>{

    private final Context context;
    Object[] items;


	public ShareListAdapter(Context context, Object[] items) {
		super(context, R.layout.share_list_item, items);
        this.context = context;
        this.items = items;

    }// end HomeListViewPrototype

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    	if(convertView == null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.share_list_item, null);
    	}

        TextView shareName = (TextView) convertView.findViewById(R.id.shareName);
        ImageView imageShare = (ImageView) convertView.findViewById(R.id.shareImage);

        shareName.setText(((ResolveInfo)items[position]).activityInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
        imageShare.setImageDrawable(((ResolveInfo)items[position]).activityInfo.applicationInfo.loadIcon(context.getPackageManager()));

        return convertView;
    }// end getView

}// end main onCreate
