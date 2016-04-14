package com.example.android.musicplayer;


import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {

    ControllerInterface ci;     //接口的对对象
    private static SeekBar sb;

    /**
     *  显示播放进度
     */
    public static Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
            //调用service里的计时器所获得的时间
            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");

            //设置进度条显示进度
            sb.setMax(duration);//总时长
            sb.setProgress(currentPosition);//当前时长
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sb = (SeekBar) findViewById(R.id.sb);
        /**
         * 监听拖动进度位置
         */
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {//其有三种回调方式
            //seekBar：触发该方法执行的seekBar对象
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {//方式一：手指抬起时触发，其是要例所用的方法
                //获取sb的当前进度，然后设置给音乐服务的播放进度
                ci.seekTo(seekBar.getProgress());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {//方式二：手指按下进触发
                ci.seekTo(seekBar.getProgress());
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {//方式三：手指滑动里触发
            }
        });

        Intent intent = new Intent(this, MusicService.class);
        //把进程变成服务进程
        startService(intent);
        //绑定服务获取中间人对象
        bindService(intent, new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ci = (ControllerInterface) service;

            }
        }, BIND_AUTO_CREATE);
    }


    //对应布局里的三个onclick事件，因为ci为接口的对象，所以其所能调用接口里的方法
    public void play(View v){
        ci.play();
    }
    public void pause(View v){
        ci.pause();
    }
    public void continuePlay(View v){
        ci.continuePlay();
    }
}
