package com.example.servicetest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    final int notifyID = 1;
    private Button play;
    private Boolean isplaying;
    private SeekBar seekBar;
    private Receiver receiver;
    private NotificationManager notificationManager;
    private Notification notification;
    private Object Context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        取得通知服務

        seekBar=findViewById(R.id.seekbar);
        play=findViewById(R.id.play);
            notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        isplaying=false;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    Intent intent=new Intent(MainActivity.this, MusicPlayMyService.class);
                    intent.putExtra("action",MusicPlayMyService.Action_Seekto);
                    intent.putExtra("rightnow",i);
                    startService(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private Notification notification_method(String title,String text){
        Log.d("Debug","notification");
        return notification=new Notification.Builder(this)
                .setContentTitle(title).setContentText(text).
                        setSmallIcon(R.drawable.ic_baseline_audiotrack_24).build();
    }
    @Override
    protected void onResume() {

        super.onResume();
        notificationManager.notify(0,notification_method("音樂播放","音樂播放中"));
        Log.d("Debug","onResume");
    }
    @Override
    protected void onPause() {

        super.onPause();
        notificationManager.notify(1,notification_method("音樂播放","暫停撥放"));
        Log.d("Debug","onPause");
    }
    @Override
    protected void onStart(){
        super.onStart();

        receiver=new Receiver();
        IntentFilter filter=new IntentFilter("fromService");
        registerReceiver(receiver,filter);
    }
    @Override
    protected void onStop(){
        super.onStop();

        unregisterReceiver(receiver);
    }

    public void stop(View view) {
    isplaying =false;
    play.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
    Intent intent =new Intent(this,MusicPlayMyService.class);
    stopService(intent);
    }


    public void play(View view) {

        isplaying=!isplaying;
        if (!isplaying){
            play.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        }else {
            play.setBackgroundResource(R.drawable.ic_baseline_pause_24);
        }
        Intent intent=new Intent(this, MusicPlayMyService.class);
        intent.putExtra("action",isplaying?MusicPlayMyService.Action_Play:MusicPlayMyService.Action_Pause);
        startService(intent);


    }
    private class Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int max=intent.getIntExtra("max",-1);
            if (max>=0){
                seekBar.setMax(max);
            }
            int now=intent.getIntExtra("now",-1);
            if (now>=0){
                seekBar.setProgress(now);
            }
        }
    }
}