package com.anb2rw.meloman;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicPlayerActivity extends Activity {
	public static String MP_PLAYER="musicplayer";
//	private int NOTIFICATION_ID=20071992;
	
	ScheduledExecutorService executorService;
	MusicPlayer player;
	
	TextView title;
	
	TextView artist;
	TextView positionTextView;
	TextView durationTextView;
	
	SeekBar progressBar;
	ImageView control_play;
	ImageView control_next;
	ImageView control_prev;
	
	ImageView repeat;
	ImageView shuffle;

	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.musicplayer);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.player_title);
        
        player=MainActivity.player;
        
        setupUI();
        
        
    }

	private void setupUI() {
		title = (TextView) findViewById(R.id.player_title);
		
		Button button_back = (Button)findViewById(R.id.button_player_back);
		button_back.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
			
		artist=(TextView) findViewById(R.id.song_author);
		positionTextView = (TextView) findViewById(R.id.timer_gone);
		durationTextView = (TextView) findViewById(R.id.timer_left);
		
		progressBar=(SeekBar) findViewById(R.id.song_progress);
		
		control_play=(ImageView) findViewById(R.id.control_playpause);
		control_next=(ImageView) findViewById(R.id.control_next);
		control_prev=(ImageView) findViewById(R.id.control_prev);
		
		repeat=(ImageView) findViewById(R.id.repeat);
		shuffle=(ImageView) findViewById(R.id.shuffle);
		
		reloadInfo();
			
		control_play.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(player!=null && player.audio!=null)
					if(player.play) {
						player.pause();
						control_play.setImageResource(android.R.drawable.ic_media_play);
						
//						deleteNotification();
					}
					else {
						player.play();
						control_play.setImageResource(android.R.drawable.ic_media_pause);
						
//						addDefaultNotification();
					}
				
			}
		});
		
		control_next.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(player!=null && player.audio!=null) {
					player.next();
					reloadInfo();
				}
			}
		});
		
		control_prev.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(player!=null && player.audio!=null) {
					player.previous();
					reloadInfo();
				}
			}
		});
		
		repeat.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(player!=null) {
					player.setRepeat(!player.repeat);

					if(player.repeat) repeat.setImageResource(android.R.drawable.ic_menu_day);
					else repeat.setImageResource(android.R.drawable.ic_menu_revert);
				}
			}
		});
		
		shuffle.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(player!=null) {
					player.shuffle=!player.shuffle;

					if(player.shuffle) shuffle.setImageResource(android.R.drawable.ic_menu_month);
					else shuffle.setImageResource(android.R.drawable.ic_menu_sort_alphabetically);
				}
			}
		});
		// TODO Auto-generated method stub
		
	}
	
	public void reloadInfo() {
		if(player!=null) {
			if(player.audio!=null) {
				title.setText(player.audio.title);
				artist.setText(player.audio.artist);
			}
			if(player.play) control_play.setImageResource(android.R.drawable.ic_media_pause);
			else control_play.setImageResource(android.R.drawable.ic_media_play);
			
			if(player.repeat) repeat.setImageResource(android.R.drawable.ic_menu_day);
			else repeat.setImageResource(android.R.drawable.ic_menu_revert);
			
			if(player.shuffle) shuffle.setImageResource(android.R.drawable.ic_menu_month);
			else shuffle.setImageResource(android.R.drawable.ic_menu_sort_alphabetically);
		}
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        executorService.shutdown();
    }
	
	@Override
    protected void onResume() {
        super.onResume();

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        update();
                    }
                });
            }
        };
        
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(runnable, 0L, 1000L, TimeUnit.MILLISECONDS);
	}
	
	private void update() {
        onProgressChanged();
    }
	
	 private void onProgressChanged() {
	        if (player == null) {
	            return;
	        }

	        if (player.audio != null) {

	            int millisPlayed = Math.max(0, player.mediaPlayer.getCurrentPosition());
	            Integer duration = player.mediaPlayer.getDuration();
	            int millisTotal = duration == null ? 0 : duration;

	            positionTextView.setText(formatDuration(millisPlayed / 1000));
	            durationTextView.setText("-"+formatDuration((millisTotal-millisPlayed) / 1000));
	            progressBar.setMax(millisTotal == 0 ? 100 : millisTotal); // Work-around for apparent bug.
	            progressBar.setProgress(millisPlayed);
//	            progressBar.setSlidingEnabled(currentPlaying.isCompleteFileAvailable() || getDownloadService().isJukeboxEnabled());
	        } else {
	            positionTextView.setText("0:00");
	            durationTextView.setText("-:--");
	            progressBar.setProgress(0);
//	            progressBar.setSlidingEnabled(false);
	        }
	 }
	 
	 public static String formatDuration(Integer seconds) {
	        if (seconds == null) {
	            return null;
	        }

	        int minutes = seconds / 60;
	        int secs = seconds % 60;

	        StringBuilder builder = new StringBuilder(6);
	        builder.append(minutes).append(":");
	        if (secs < 10) {
	            builder.append("0");
	        }
	        builder.append(secs);
	        return builder.toString();
	    }
	 
//	 private void addDefaultNotification(){
//	        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//	        
//	        int icon = R.drawable.ic_launcher;
//	        CharSequence text = "Notification Text";
//	        CharSequence contentTitle = "Notification Title";
//	        CharSequence contentText = "Sample notification text.";
//	        long when = System.currentTimeMillis();
//	        
//	        Intent intent = new Intent(this, MusicPlayerActivity.class);
//	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
//	        Notification notification = new Notification(icon,text,when);
//	        
//	        notification.ledARGB = Color.RED;
//	        notification.ledOffMS = 300;
//	        notification.ledOnMS = 300;
//	        
//	        notification.defaults |= Notification.DEFAULT_LIGHTS;
//	        //notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//	        
//	        notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
//	        
//	        notificationManager.notify(NOTIFICATION_ID, notification);
//	    }
//	 
//	 private void deleteNotification() {
//		 NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//         
//         notificationManager.cancel(NOTIFICATION_ID);
//	 }
}
