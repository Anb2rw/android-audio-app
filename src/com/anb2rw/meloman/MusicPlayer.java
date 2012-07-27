package com.anb2rw.meloman;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.perm.kate.api.Audio;

public class MusicPlayer {
	
	public static String MP_URL="url";

	Audio audio;
	ArrayList<Audio> list;
	
	MediaPlayer mediaPlayer;
	int index=0;
    boolean play=false;
    boolean repeat=false;
    boolean shuffle=false;
    
    public MusicPlayer() {
    	mediaPlayer = new MediaPlayer();
    }
    
    public void loadList(ArrayList<Audio> list) {
    	this.list = list;
    }
    
    public void loadAudio(int index) {
    	boolean prev_play=play;
    	if(play) stop();
    	if(list==null) return;
    	
    		mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				Audio file=list.get(index);
				mediaPlayer.setDataSource(file.url);
				mediaPlayer.prepare(); // might take long! (for buffering, etc)
				audio=file;
				this.index=index;
				if(prev_play) play();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
    }
    
	public void play() {
		mediaPlayer.start();
		play=true;
		
	}
	
	public void pause() {
		mediaPlayer.pause();
		play=false;
	}
	
	public void stop() {
			mediaPlayer.stop();
			mediaPlayer.release();
			play=false;
			audio=null;
	}
	
	public void next() {
		if(repeat) {
			mediaPlayer.seekTo(0);
			return;
		}
		
		if(shuffle && list!=null && list.size()>1) {
			Random rnd = new Random();
			loadAudio(rnd.nextInt(list.size()));
			return;
		}
		
		
		int next = index+1;
		if(list==null || next>=list.size()) next=0;
		
			loadAudio(next);
			return;
	}
	
	public void previous() {
		if(repeat) {
			mediaPlayer.seekTo(0);
			return;
		}
		
		if(shuffle && list!=null && list.size()>1) {
			Random rnd = new Random();
			loadAudio(rnd.nextInt(list.size()));
			return;
		}
		
		int next = index-1;
		if(list!=null && next<0) next=list.size()-1;
		
			loadAudio(next);
			return;
	}
	
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
		mediaPlayer.setLooping(repeat);
	}
}
