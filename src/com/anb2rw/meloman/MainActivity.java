package com.anb2rw.meloman;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.perm.kate.api.Api;
import com.perm.kate.api.Audio;
import com.perm.kate.api.KException;

public class MainActivity extends Activity {
private final int REQUEST_LOGIN=1;
	
	private Account account=new Account();
    Api api;
    
    ArrayList<Audio> list;
    
//    private LinkedList<String> mListItems;
    private InteractiveArrayAdapter mAdapter;
    private PullToRefreshListView mPullRefreshListView;
    ListView actualListView;
    
    public static MusicPlayer player=new MusicPlayer();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main_title);
        
        setupUI();
        
        //Восстановление сохранённой сессии
        account.restore(this);
        
        //Если сессия есть создаём API для обращения к серверу
        if(account.access_token!=null)
            api=new Api(account.access_token, Constants.API_ID);
        else
        	startLoginActivity();
        loadAudioList();
    }
    
    private void setupUI() {
    	TextView title = (TextView) findViewById(R.id.main_title);
    	title.setText("Songs");
    	Button player_button = (Button) findViewById(R.id.button_player);
    	player_button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),MusicPlayerActivity.class);
				startActivity(intent);
			}
		});
    	
    	mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mPullRefreshListView.setLastUpdatedLabel(DateUtils.formatDateTime(getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL));

				loadAudioList();
				
				new GetDataTask().execute();
			}
		});
		
		actualListView = mPullRefreshListView.getRefreshableView();
		
//    	ListView lw = (ListView) findViewById(R.id.listView_audio);
		actualListView.setOnItemClickListener(new OnItemClickListener() {

	      public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
	    	  --position;//Первый элемент - PullToRefresh
	      // Получение элемента, который был нажат
//	      Toast.makeText(v.getContext(), "N", 300).show();
			if(list!=null) {
				player.loadAudio(position);
				if(!player.play) player.play();
				Intent intent = new Intent(v.getContext(),MusicPlayerActivity.class);
				startActivity(intent);
//				startActivityForResult(intent, REQUEST_LOGIN);
				}
//				player.play(list.get(position).url);
	
	      }
		});
    }
    
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		protected void onPostExecute(String[] result) {
//			mListItems.addFirst("Added after refresh...");
			mAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Intent i=new Intent(this,SettingsActivity.class);
			startActivity(i);
			return true;
		}
		return false;
	}
    
    private void startLoginActivity() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                //авторизовались успешно 
                account.access_token=data.getStringExtra("token");
                account.user_id=data.getLongExtra("user_id", 0);
                account.save(MainActivity.this);
                api=new Api(account.access_token, Constants.API_ID);
                loadAudioList();
            }
        }
    }
    
    private void logOut() {
        api=null;
        account.access_token=null;
        account.user_id=0;
        account.save(MainActivity.this);
    }
    
    private void loadAudioList() {
    	if(api!=null) {
	    	try {
				list = api.getAudio(account.user_id, null, null);
				player.loadList(list);

				mAdapter = new InteractiveArrayAdapter(this,list);

				// You can also just use setListAdapter(mAdapter)
				actualListView.setAdapter(mAdapter);
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
}