package com.example.android.musicplayer;

/**
 * Created by Administrator on 2016/3/15.
 */
public interface ControllerInterface {

    void  play();
    void pause();

    void continuePlay();
    void  seekTo(int progress);//拖放进度条从而改变播进度
}
