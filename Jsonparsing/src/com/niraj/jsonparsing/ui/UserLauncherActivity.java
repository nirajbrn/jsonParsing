package com.niraj.jsonparsing.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.niraj.jsonparsing.data.UserInfo;
import com.niraj.jsonparsing.data.UserInfo.UserData;
import com.niraj.jsonparsing.service.JsonServicehandler;

public class UserLauncherActivity extends ListActivity implements LoaderCallbacks<List<UserData>>{
    static String TAG = UserLauncherActivity.class.getSimpleName();
    
    Context aContext = null;
    ListView mListView = null;
    private MyArrayAdapter mParseAdapter;
    ArrayList<UserData> mUserList; // list of data  to be added
    ArrayList<UserData> mtempList;

    private static final String uri = "https://api.github.com/gists/public";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aContext = UserLauncherActivity.this;
       
        mUserList = new ArrayList<UserData>();
        mtempList = new ArrayList<UserData>();
        
        mListView = (ListView)findViewById(android.R.id.list);
        mParseAdapter = new MyArrayAdapter(UserLauncherActivity.this,null);
        mListView.setAdapter(mParseAdapter);
        getLoaderManager().initLoader(0, null, UserLauncherActivity.this);
        
     // Delete the selected item from list
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
        	
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               
                Log.d(TAG, "onItemLongClick position = "+position+" id = "+id);
                final int pos = position;
               
                AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
                builder.setTitle("Delete Item");
                builder.setMessage("Message will be deleted..");
                builder.setPositiveButton("Yes", new OnClickListener() {
                   
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.d(TAG, "setPositiveButton(): which = "+which);
                        mParseAdapter.DeleteItem(pos);
                    }
                });
               
                builder.setNegativeButton("No", new OnClickListener() {
                   
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
               
                builder.show();
                return false;
            }
        });
        
        Button addBtn = (Button)findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mtempList != null && mtempList.size() != 0){
					UserData selectedUser = mtempList.get(0);
					mUserList.add(selectedUser);
					mtempList.remove(selectedUser);
					Log.d(TAG, "addBtn onClick(): mUserList.size() = "+mUserList.size()+" mtempList.size() = "+mtempList.size());
					mParseAdapter.setData(mUserList);
				}
				
			}
		});
       
    }
   
    public static class parsingListLoader extends AsyncTaskLoader<List<UserData>>{

        List<UserData> mUserDataList;
        ProgressDialog progressDialog = null;
       
        public parsingListLoader(Context context) {
            super(context);
            progressDialog = new ProgressDialog(context);
        }

        @Override
        public List<UserData> loadInBackground() {
           
        	JsonServicehandler httpRequest = new JsonServicehandler();
            String jsonRes = httpRequest.userDetailRequest(uri);
            UserInfo userData = new UserInfo();
           
            Log.d(TAG, "doInBackground(): jsonRes = "+jsonRes);
            if(jsonRes != null){
               
                try {
                    JSONArray contacts = new JSONArray(jsonRes);
                    for (int i = 0; i < contacts.length(); i++) {
                       
                        JSONObject c = contacts.getJSONObject(i);
                       
                        JSONObject user = c.getJSONObject("user");
                        String login = user.getString("login");
                        String id = user.getString("id");
                        userData.setData(id, login);
                    }
                   
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return userData.getData();
        }
       
        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override
        public void deliverResult(List<UserData> data) {

            Log.d(TAG, "deliverResult() called. data="+data);

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            if (isReset()) {
                if (data != null) {
                    onReleaseResources(data);
                }
            }

            if (isStarted()) {
                super.deliverResult(data);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override
        protected void onStartLoading() {
            Log.d(TAG, "onStartLoading() called.");

            progressDialog.setMessage("Please wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();
           
            if (mUserDataList != null) {
                deliverResult(mUserDataList);
            }

            if (takeContentChanged() || mUserDataList == null) {
                forceLoad();
            }

        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override
        protected void onStopLoading() {
            Log.d(TAG, "onStopLoading() called.");
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override
        public void onCanceled(List<UserData> data) {
            super.onCanceled(data);
            Log.d(TAG, "onCanceled() called.");

            // At this point we can release the resources associated with 'data'
            // if needed.
            onReleaseResources(data);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override
        protected void onReset() {
            super.onReset();
            Log.d(TAG, "onReset() called.");

            onStopLoading();
            mUserDataList = null;

        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<UserData> data) {
            Log.d(TAG, "onReleaseResources() called. data="+data);
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
       
    }
   
   
    private class MyArrayAdapter extends BaseAdapter{

        Context mContext;
        List<UserData> mUserDataList;
        private LayoutInflater mInflater = null;
       
        public MyArrayAdapter(Context mContext, ArrayList<UserData> data){
           
            this.mContext = mContext;
            this.mUserDataList = data;
            mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
       
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mUserDataList != null ? mUserDataList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }
       
        public void setData(List<UserData> data) {
            mUserDataList = data;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.user_list_layout, null);
                holder.id = (TextView)convertView.findViewById(R.id.login_id);
                holder.login_name = (TextView)convertView.findViewById(R.id.login_name);
                convertView.setTag(holder);
               
            }else {
                holder = (ViewHolder)convertView.getTag();
               
            }
            UserData data = mUserDataList.get(position);
           
            holder.id.setText("Id : "+data.mId);
            holder.login_name.setText("Name : "+data.mLoginName);
           
            return convertView;
        }
       
        public void DeleteItem(int position){
            Log.d(TAG, "DeleteItem(): before mUserDataList.size() = "+mUserDataList.size());
           
            //mUserDataList.remove(getItem(position));
           
            mUserDataList.remove(position);
           
            Log.d(TAG, "DeleteItem(): after mUserDataList.size() = "+mUserDataList.size());
            notifyDataSetChanged();
        }
       
        class ViewHolder{

            TextView id;
            TextView login_name;

        }
       
    }

    @Override
    public Loader<List<UserData>> onCreateLoader(int id, Bundle args) {
    	Log.d(TAG, "onCreateLoader(): id = "+id);
        return new parsingListLoader(aContext);
    }

    @Override
    public void onLoadFinished(Loader<List<UserData>> loader, List<UserData> data) {
    	if(mtempList != null && mtempList.size() == 0){
    		mtempList.addAll(data);
    		Log.d(TAG, "onLoadFinished(): mtempList.size() = "+mtempList.size());
    	}
        mParseAdapter.setData(mUserList);
    }

    @Override
    public void onLoaderReset(Loader<List<UserData>> arg0) {
        mParseAdapter.setData(null);
    }

}
