package com.websarva.wings.android.presentimer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView _tvTimer;              // 時間表示テキストビュー
    private FloatingActionButton _btStop;   // 停止ボタン
    private FloatingActionButton _btPlay;   // 開始ボタン
    private FloatingActionButton _btPause;  // 一時停止ボタン
    private MyCountDownTimer _cdt;  // カウントダウンタイマー
    private MediaPlayer _player;
    private MediaPlayer _player2;
    private MediaPlayer _player3;// メディアプレーヤー
    private long _millisUntilFinished_Save;  // 中断時の残り時間保存
    private boolean _pauseFlag = false; // 中断状態フラグ
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private int count;
    private boolean flg[] = {false,false,false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _btStop = findViewById(R.id.btStop);    // 停止ボタンビューを取得
        _btPlay = findViewById(R.id.btPlay);    // 開始ボタンビューを取得
        _btPause = findViewById(R.id.btPause);  // 一時停止ボタンビューを取得
        _btPause.setEnabled(false);             // 一時停止ボタンを無効化
        _tvTimer = findViewById(R.id.tvTimer);  // 時間表示テキストビューを取得

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);

        count = 0;



    }

    /**
     * カウントダウンタイマ(インナークラス)：CountDownTimerクラスを継承
     */
    public class MyCountDownTimer extends CountDownTimer {
        String mediaFileUriStr = "";    //音声ファイルのURI文字列

        // コンストラクタ
        // millisInFuture   :完了までの時間(ミリ秒単位)
        // countDownInterval:onTickの間隔(ミリ秒単位)
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            // 親クラスのコンストラクタに渡す
            super(millisInFuture, countDownInterval);
        }

        // インターバル(countDownInterval)毎に呼ばれる処理
        @Override
        public void onTick(long millisUntilFinished) {
            // onTickでのmillisUntilFinishedは正確にcountDownInterval間隔ではなく数ミリ秒誤差あり
            // 残り時間を表示
            dispTime(millisUntilFinished);
            // 中断に備えて残り時間を保存
            _millisUntilFinished_Save = millisUntilFinished;
            Long round1 = Long.parseLong(et1.getText().toString())* 60;
            Long round2 = Long.parseLong(et2.getText().toString()) * 60;
            Long round3 = Long.parseLong(et3.getText().toString()) * 60;
            long nokori = millisUntilFinished/1000;



            //発表終了予告時間になったら
            if ( (round3 - round1) == nokori) {
                flg[0] = true;
                mediaFileUriStr = "android.resource://" + getPackageName() + "/" + R.raw.bell;
                // 音声を再生
                myMediaPlay(mediaFileUriStr);


                Toast.makeText(MainActivity.this,R.string.yokoku,Toast.LENGTH_LONG).show();
            }
            //質疑応答の時間になったら
            if ( (round3 - round2) == nokori) {
                flg[1] = true;
                mediaFileUriStr = "android.resource://" + getPackageName() + "/" + R.raw.bell;
                // 音声を再生

                myMediaPlay(mediaFileUriStr);


                Toast.makeText(MainActivity.this,R.string.situgi,Toast.LENGTH_LONG).show();
            }


        }

        // カウントダウン完了後に行われる処理
        @Override
        public void onFinish() {
            flg[2] = true;
            // 残り時間を表示
            dispTime(0);
            //音声ファイルのURI文字列を作成。
            mediaFileUriStr = "android.resource://" + getPackageName() + "/" + R.raw.bell;
            // 音声を再生
            myMediaPlay(mediaFileUriStr);
        }
    }

    private void dispTime(long timeLeft) {
        // 残り時間を分：秒で表示する
        long minute = timeLeft / 1000 / 60;
        long second = timeLeft / 1000 % 60;
        _tvTimer.setText(String.format("%2d:%02d",minute, second));
    }

    /**
     * 開始ボタンが押されたときの処理メソッド
     */
    public void onPlayButtonClick(View view) {

        if(et1.length() != 0 && et2.length() != 0 && et3.length() != 0){

            String time_happyou = et3.getText().toString();

            int r = Integer.parseInt(time_happyou);



            if(_pauseFlag == false) {
                // カウントダウンタイマーインスタンスの生成(タイマー時間:3分、インターバル1秒)
                _cdt = new MyCountDownTimer(r * 60 * 1000 + 50, 1000);
            }
            else {
                // カウントダウンタイマーインスタンスの生成(タイマー時間:中断時の残り時間、インターバル1秒)
                _cdt = new MyCountDownTimer(_millisUntilFinished_Save, 1000);
                _pauseFlag = false;
            }
            _btPlay.setEnabled(false);  // 開始ボタンを無効化
            _btPause.setEnabled(true);   // 一時停止ボタンを有効化
            _cdt.start();       // カウントダウン開始

        }else{
            Toast.makeText(MainActivity.this,"入力してください",Toast.LENGTH_LONG).show();
        }



    }

    /**
     * 一時停止ボタンが押されたときの処理メソッド
     */
    public void onPauseButtonClick(View view) {
        if (_cdt != null)
            _cdt.cancel();      // カウントダウン停止
        _pauseFlag = true; // 中断フラグを設定
        _btPause.setEnabled(false);   // 一時停止ボタンを無効化
        _btPlay.setEnabled(true);   // 開始ボタンを有効化
    }

    /**
     * 停止ボタンが押されたときの処理メソッド
     */
    public void onStopButtonClick(View view) {
        dispTime(0);   // 残り時間をクリア
        if (_cdt != null)
            _cdt.cancel();      // カウントダウン停止
        _pauseFlag = false; // 中断フラグを設定
        _btPlay.setEnabled(true);   // 開始ボタンを有効化
        _btPause.setEnabled(true);   // 一時停止ボタンを有効化
    }

    /**
     * 音声を再生するメソッド
     */
    private void myMediaPlay(String mediaFileUriStr) {
        // 音声ファイルのURI文字列を元にURIオブジェクトを生成。
        Uri mediaFileUri = Uri.parse(mediaFileUriStr);
        // メディアプレーヤーオブジェクトを生成
        _player = new MediaPlayer();
        _player2 = new MediaPlayer();
        _player3 = new MediaPlayer();


        try {
            //メディアプレーヤーに音声ファイルを指定。
            _player.setDataSource(MainActivity.this, mediaFileUri);
            //メディア再生が終了した際のリスナを設定。
            _player.setOnCompletionListener(new PlayerCompletionListener());
            _player2.setDataSource(MainActivity.this, mediaFileUri);
            _player2.setOnCompletionListener(new PlayerCompletionListener());
            _player3.setDataSource(MainActivity.this, mediaFileUri);
            _player3.setOnCompletionListener(new PlayerCompletionListener());
            //再生回数

            if(flg[1]){
                _player.setNextMediaPlayer(_player2);

            }
            if(flg[2]){
                _player.setNextMediaPlayer(_player2);
                _player2.setNextMediaPlayer(_player3);

            }


            //メディア再生を準備。
            _player.prepare();
            _player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 音声再生が終了したときのリスナクラス。
     */
    private class PlayerCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            _player.release();

        }
    }
}
