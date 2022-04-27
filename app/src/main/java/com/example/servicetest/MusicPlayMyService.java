package com.example.servicetest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayMyService extends Service {
    private MediaPlayer mediaPlayer;
    private NotificationManager mNotificationManager;
    private Notification notification;

    public static  final  int Action_Play=1;
    public static  final  int Action_Pause=2;
    public static  final  int Action_Seekto=3;
    private Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer =MediaPlayer.create(this,R.raw.test);

        Intent intent=new Intent( "fromService");
        intent.putExtra("max",mediaPlayer.getDuration());
        sendBroadcast(intent);

        timer=new Timer();
        timer.schedule(new UpdataTask(),0,100);



    }
    private class UpdataTask extends TimerTask{
        @Override
        public void run() {
            if (mediaPlayer!=null && mediaPlayer.isPlaying()){
                Intent intent=new Intent( "fromService");
                intent.putExtra("now",mediaPlayer.getCurrentPosition());
                sendBroadcast(intent);
            }
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action=intent.getIntExtra("action",-1);
        switch (action){
            case Action_Play:
                if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                break;
            case Action_Pause:
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case Action_Seekto:
                int rightnow =intent.getIntExtra("rightnow",-1);
                if (rightnow >= 0) mediaPlayer.seekTo(rightnow);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        //timer.cancel()將計時器取消，timer.purge()清除計時器
        if (timer!=null){
            timer.cancel();
            timer.purge();
            timer=null ;
        }
    }
}
