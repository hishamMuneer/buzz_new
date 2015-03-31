package com.teamtreehouse.ribbit;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class InboxFragment extends ListFragment {

	protected List<ParseObject> mMessages;
	protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected ListView mListView;
    private AlertDialog.Builder build;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox, container,
				false);
		mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorSchemeResources(
				R.color.refresh1, R.color.refresh2,
				R.color.refresh3, R.color.refresh4);
		return rootView;
	}

	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

	        @Override
	        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
	                int arg2, long arg3) {
	        	final ParseObject msg = mMessages.get(arg2);
	        //    Toast.makeText(getActivity(), "On long click listener", Toast.LENGTH_LONG).show();
	        	build = new AlertDialog.Builder(getActivity());
				build.setTitle("Delete!");
				build.setMessage("Do you want to delete ?");
				build.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						List<String> ids =  msg.getList(ParseConstants.KEY_RECIPIENT_IDS);
						
						if (ids.size() == 1) {
							// last recipient - delete the whole thing!
							msg.deleteInBackground();
						}
						else {
							// remove the recipient and save
							ids.remove(ParseUser.getCurrentUser().getObjectId());
							
							ArrayList<String> idsToRemove = new ArrayList<String>();
							idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
							
							msg.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
							msg.saveInBackground();
							 //Toast.makeText(getActivity(), "Ola amigos", Toast.LENGTH_LONG).show();
						}
						retrieveMessages();
						mOnRefreshListener.onRefresh();
						dialog.cancel();
					}
					
				});
				build.setNegativeButton("NO", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});

				AlertDialog alert = build.create();
				alert.show();
	        	return true;
	        }
	    });
	}

	
	
	@Override
	public void onResume() {
		super.onResume();

		getActivity().setProgressBarIndeterminateVisibility(true);

		retrieveMessages();
	}

	private void retrieveMessages() {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				ParseConstants.CLASS_MESSAGES);
		query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser
				.getCurrentUser().getObjectId());
		query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);

				if(mSwipeRefreshLayout.isRefreshing())
				{
					mSwipeRefreshLayout.setRefreshing(false);
				}
				if (e == null) {
					// We found messages!
					mMessages = messages;

					String[] usernames = new String[mMessages.size()];
					int i = 0;
					for (ParseObject message : mMessages) {
						usernames[i] = message
								.getString(ParseConstants.KEY_SENDER_NAME);
						i++;
					}
					if(getListView().getAdapter()==null)
					{
					MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
					setListAdapter(adapter);
					}
					else
					{
						//getListView().getAdapter().
						((MessageAdapter)getListView().getAdapter()).refill(mMessages);
					}
				}
			}
		});
	}
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		ParseObject message = mMessages.get(position);
		String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
		ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
		Uri fileUri = Uri.parse(file.getUrl());
		
		if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
			// view the image
			Intent intent = new Intent(getActivity(), ViewImageActivity.class);
			intent.setData(fileUri);
			startActivity(intent);
		}
		else {
			// view the video
			Intent intent = new Intent(Intent.ACTION_VIEW,fileUri);
			intent.setDataAndType(fileUri,"video/*");
			startActivity(intent);
		}
	}
	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		
		@Override
		public void onRefresh() {

			retrieveMessages();
		}
	};

}
