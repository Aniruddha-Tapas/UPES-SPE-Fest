package com.myapps.upesse.upes_spefest.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.myapps.upesse.upes_spefest.R;
import com.myapps.upesse.upes_spefest.ui.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListViewAdapter extends BaseAdapter {

	// Declare Variables
	Context mContext;
	LayoutInflater inflater;
	//private List<WorldPopulation> worldpopulationlist = null;
	//private ArrayList<WorldPopulation> arraylist;
	private List<SearchUser> userslist = null;
	private ArrayList<SearchUser> arraylist;

	public ListViewAdapter(Context context,
						   List<SearchUser> userslist) {
		mContext = context;
		this.userslist = userslist;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = new ArrayList<SearchUser>();
		this.arraylist.addAll(userslist);
	}

	public class ViewHolder {
		CircleImageView udp;
		TextView user;
	}

	@Override
	public int getCount() {
		return userslist.size();
	}

	@Override
	public SearchUser getItem(int position) {
		return userslist.get(position);
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
			holder.user = (TextView) view.findViewById(R.id.userlabel);
			holder.udp = (CircleImageView) view.findViewById(R.id.user_icon);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		// Set the results into TextViews
		holder.user.setText(userslist.get(position).getUname());
		if(userslist.get(position).getUdp().equals("NO_PHOTO_URL") || userslist.get(position).getUdp() == ("NO_PHOTO_URL")){
			holder.udp.setImageResource(R.drawable.ic_person_outline_black);
		}else {
			GlideUtil.loadProfileIcon(userslist.get(position).getUdp(), holder.udp);
		}

		// Listen for ListView Item Click
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//Intent intent = new Intent(mContext, SingleItemView.class);
				// Start SingleItemView Class
				//mContext.startActivity(intent);
				//Toast.makeText(mContext,userslist.get(position),Toast.LENGTH_LONG).show();

				DatabaseReference ref = FirebaseUtil.getBaseRef();
				DatabaseReference peopleref = FirebaseUtil.getPeopleRef();
				peopleref.addValueEventListener(
						new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot dataSnapshot) {
								//iterate through each user, ignoring their UID
								Map<String, Object> people = (Map<String, Object>) dataSnapshot.getValue();
								for (Map.Entry<String, Object> entry : people.entrySet()) {
									//Get user map
									Map singleUser = (Map) entry.getValue();
									//Get UserNames and append to list
									String uname = (String) singleUser.get("displayName");
									if ((((uname == userslist.get(position).getUname())
											|| (uname.equals(userslist.get(position)))))) {
										///userNames.add(uname);
										//Toast.makeText(this, uname, Toast.LENGTH_SHORT).show();
										//SuggestionProvider.addFirebaseUsers(uname);
										String unid = entry.getKey();
										//Toast.makeText(getApplicationContext(), "UID : " + unid, Toast.LENGTH_SHORT).show();
										if (unid != null) {
											Context context = mContext;
											Intent userDetailIntent = new Intent(context, UserDetailActivity.class);
											userDetailIntent.putExtra(UserDetailActivity.USER_ID_EXTRA_NAME,
													unid);
											context.startActivity(userDetailIntent);
										}

									}
								}
							}

							@Override
							public void onCancelled(DatabaseError databaseError) {
								//handle databaseError
							}
						});
			}
		});

		return view;
	}

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		userslist.clear();
		if (charText.length() == 0) {
			userslist.addAll(arraylist);
		} else {
			for (SearchUser item : arraylist) {
				if (item.getUname().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					userslist.add(item);
				}
			}
		}
		notifyDataSetChanged();
	}

}
