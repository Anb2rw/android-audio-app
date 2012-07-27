package com.anb2rw.meloman;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.perm.kate.api.Api;
import com.perm.kate.api.KException;
import com.perm.kate.api.User;

public class SettingsActivity extends Activity {
	private final int REQUEST_LOGIN=1;
	
	Account account=new Account();
    Api api;
    
    TextView profile;
    ImageView photo;
    Button logout;
    Button login;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.settings);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main_title);
        
        account.restore(this);
        
      //Если сессия есть создаём API для обращения к серверу
        if(account.access_token!=null)
            api=new Api(account.access_token, Constants.API_ID);
        
        setupUI();
        
    }

	private void setupUI() {
		TextView title = (TextView) findViewById(R.id.main_title);
		title.setText("Settings");
		
		Button b = (Button) findViewById(R.id.button_player);
		b.setVisibility(View.GONE);
		
		profile = (TextView) findViewById(R.id.text_account);
		photo = (ImageView) findViewById(R.id.photo_account);
		logout = (Button) findViewById(R.id.button_logout);
		logout.setOnClickListener(new OnClickListener() {
				
			public void onClick(View v) {
				logOut();
				
			}
		});
		login = (Button) findViewById(R.id.button_login);
		login.setOnClickListener(new OnClickListener() {
				
			public void onClick(View v) {
				startLoginActivity();
				
			}
		});

		loadUserProfile();
		
	}
	
	private void loadUserProfile() {
		if(account!=null) {
			List<Long> l=new ArrayList<Long>();
			l.add(account.user_id);
			
			ArrayList<User> users = new ArrayList<User>();
			User user=null;
			try {
				users=api.getProfiles(l, null, null, null);
				user = users.get(0);
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
			
				if(user!=null) {
					
					try {
						Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(user.photo).getContent());
						photo.setImageBitmap(bitmap);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					profile.setText(user.first_name+" "+user.last_name);
				}
			}
	}
	
	private void postToWall() {
        //Общение с сервером в отдельном потоке чтобы не блокировать UI поток
        new Thread(){
            @Override
            public void run(){
                try {
//                    String text=messageEditText.getText().toString();
//                    api.createWallPost(account.user_id, text, null, null, false, false, false, null, null, null, null);
                    //Показать сообщение в UI потоке 
                    runOnUiThread(successRunnable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
	
	Runnable successRunnable=new Runnable() {
        public void run() {
            Toast.makeText(getApplicationContext(), "Запись успешно добавлена", Toast.LENGTH_LONG).show();
        }
    };
	
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
                account.save(this);
                api=new Api(account.access_token, Constants.API_ID);
                loadUserProfile();
                logout.setVisibility(View.VISIBLE);
                login.setVisibility(View.GONE);
            }
        }
    }
    
	private void logOut() {
        api=null;
        account.access_token=null;
        account.user_id=0;
        account.save(this);
        
        profile.setText("Not autorised");
        photo.setImageBitmap(null);
        
        logout.setVisibility(View.GONE);
        login.setVisibility(View.VISIBLE);
//        loadAudioList();
    }
	

}
