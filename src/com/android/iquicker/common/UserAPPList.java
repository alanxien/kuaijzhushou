package com.android.iquicker.common;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.iquicker.R;



public class UserAPPList extends BaseAdapter {
	
	private ArrayList<ListViewData> list;
	
	private Context context;
	
	private int id;

	public UserAPPList(int id,ArrayList<ListViewData> list, Context context) {
		super();
		this.id=id;
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();  
	}

	@Override
	public Object getItem(int position) {
		if(list==null){
			return null;
		}else{
			return list.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;  
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		ListViewData data=(ListViewData) getItem(position);
		if(convertView==null){
			holder = new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.applist_item, null);
			convertView.setClickable(false);
			holder.image=(ImageView) convertView.findViewById(R.id.image);
			holder.program_name=(TextView) convertView.findViewById(R.id.program_name);
			//holder.package_name=(TextView) convertView.findViewById(R.id.package_name);

			convertView.setTag(holder);
		}
		else
		{
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.image.setImageDrawable(data.getImage());
		holder.program_name.setText(data.getProgram_name());
		//holder.package_name.setText(data.getPackage_name());
        
		return convertView;
	}
	
	public class ViewHolder{
		
		ImageView image;
		
		TextView program_name;
		
		TextView package_name;
	}
	public Drawable getImage(int position){
		return list.get(position).getImage();
	}
	
	public String getProgramName(int position){
		return list.get(position).getProgram_name();
	}

	public int getId(){
		return id;
	}

	public String getPackageName(int position) {
		// TODO Auto-generated method stub
		return list.get(position).getPackage_name();
	}

}
