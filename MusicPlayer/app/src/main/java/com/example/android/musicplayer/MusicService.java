package com.example.android.musicplayer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.view.View;

public class MusicService extends Service {

    MediaPlayer player;
    private Timer timer;
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicController();
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        player = new MediaPlayer();
    }

    //销毁播放器
    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁player
        player.stop();
        player.release();

        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    class MusicController extends Binder implements ControllerInterface{

        //下面的连续四个方法是实现了接口的时所继承过来的方法

        @Override
        public void play() {
            MusicService.this.play();

        }

        @Override
        public void pause() {
            MusicService.this.pause();

        }

        @Override
        public void continuePlay() {
            MusicService.this.continuePlay();

        }


        @Override
        public void seekTo(int progress) {          //改变播放进度的方法，就是获取当前播放的进度
            MusicService.this.seekTo(progress);

        }

    }

    public void play(){
        player.reset();
        try {
            player.setDataSource("/sdcard/kugoumusic/yigerendedifang.mp3");
//			player.setDataSource("http://169.254.244.136:8080/bzj.mp3");
            //同步准备
//			player.prepare();
            //异步准备
            player.prepareAsync();
            player.setOnPreparedListener(new OnPreparedListener() {

                //准备完毕时调用
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                    addTimer();  //改变播放时所调用的方法
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void pause(){
        player.pause();
    }
    public void continuePlay(){
        player.start();
    }

    public void seekTo(int progress){
        player.seekTo(progress);
    }

    public void addTimer(){
        if(timer == null){
            timer = new Timer(); //Timer计时器，其可以启动一个计时任务
            //设置计时任务来获取播放的时间
            timer.schedule(new TimerTask() {

                //这个run方法也是在子线程执行
                @Override
                public void run() {
                    //获取播放总时长，获取的时间单位为毫秒
                    int duration = player.getDuration();
                    //获取当前播放进度
                    int currentPosition = player.getCurrentPosition();

                    Message msg = MainActivity.handler.obtainMessage();

                    //把上面的两个数据封装至消息对象（总时常，当前位）
                    Bundle data = new Bundle();
                    data.putInt("duration", duration);
                    data.putInt("currentPosition", currentPosition);
                    //消息里的一个方法，其传进去的参数是一个Buddle对象，其将对象发送至主线程供HandlMessage调用
                    msg.setData(data);

                    MainActivity.handler.sendMessage(msg);
                    //计时任务开始后，run方法开始执行，每 500毫秒执行一次
                }
                //计时任务开始5毫秒后，run方法执行，每500毫秒执行一次
            }, 5, 500);
        }
    }

}
