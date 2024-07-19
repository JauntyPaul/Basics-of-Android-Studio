package com.jaunty.voicerecorder;

import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_AUDIO_PERMISSION_CODE=101;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    ImageView record;
    ImageView play;
    TextView recording_file;
    TextView tv_time;
    ImageView music;
    boolean isRecording=false;
    boolean isPlaying=false;

    int seconds=0;

    String path = null;
    LottieAnimationView audio_playing;
    int dummySeconds=0;
    int playableSeconds=0;
    Handler handler;


    ExecutorService executorService = Executors.newSingleThreadExecutor();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }



        record= findViewById(R.id.record);
        play = findViewById(R.id.play);
        recording_file= findViewById(R.id.recording_file);
        tv_time= findViewById(R.id.tv_time);
        music= findViewById(R.id.music);
        audio_playing= findViewById(R.id.audio_playing);
        mediaPlayer = new MediaPlayer();

        // When someone clicks on the record button

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkRecordingPermission())
                {
                    if(!isRecording)
                    {
                        isRecording=true;
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                mediaRecorder= new MediaRecorder();
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                mediaRecorder.setOutputFile(getRecordingFilePath());
                                path=getRecordingFilePath();
                                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                                try {
                                    mediaRecorder.prepare();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                mediaRecorder.start();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        music.setVisibility(View.VISIBLE);
                                        audio_playing.setVisibility(view.GONE);
                                        recording_file.setText(getRecordingFilePath());
                                        playableSeconds=0;
                                        seconds=0;
                                        dummySeconds=0;
                                        record.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.mic_active));

                                        runTimer();


                                    }
                                });
                            }
                        });
                    }
                    else
                    {
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                mediaRecorder.stop();
                                mediaRecorder.release();
                                mediaRecorder=null;
                                playableSeconds=seconds;
                                dummySeconds=seconds;
                                seconds=0;
                                isRecording=false;


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        music.setVisibility(View.VISIBLE);
                                        audio_playing.setVisibility(view.GONE);
                                        handler.removeCallbacksAndMessages(null);
                                        record.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.mic_inactive));
                                    }
                                });
                            }
                        });
                    }
                }
                else
                {
                    requestRecordingPermission();
                }
            }
        });










        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying)
                {
                    if(path!=null)
                    {
                        try {
                            mediaPlayer.setDataSource(getRecordingFilePath());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "No Recording Present", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    mediaPlayer.start();
                    isPlaying=true;
                    play.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.pause));
                    music.setVisibility(View.GONE);
                    audio_playing.setVisibility(View.VISIBLE);
                    runTimer();

                }
                else
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer=null;
                    mediaPlayer=new MediaPlayer();
                    isPlaying=false;
                    seconds=0;
                    handler.removeCallbacksAndMessages(null);
                    music.setVisibility(View.VISIBLE);
                    audio_playing.setVisibility(View.GONE);
                    play.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.play));

                }
            }
        });
    }

    private void runTimer()
    {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes=(seconds%3600)/60;
                int secs=seconds%60;
                String time=String.format(Locale.getDefault(),"%02d:%02d",minutes,secs);
                tv_time.setText(time);

                if(isRecording || (isPlaying && playableSeconds !=-1 ))
                {
                    seconds++;
                    playableSeconds--;

                    if(playableSeconds==-1 && isPlaying)
                    {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        isPlaying=false;
                        mediaPlayer=null;
                        mediaPlayer=new MediaPlayer();
                        playableSeconds=dummySeconds;
                        seconds=0;
                        handler.removeCallbacksAndMessages(null);
                        music.setVisibility(View.VISIBLE);
                        audio_playing.setVisibility(View.GONE);
                        play.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.play));
                        return;

                    }
                }
                handler.postDelayed(this,1000);

            }
        });
    }

    private void requestRecordingPermission()
    {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_AUDIO_PERMISSION_CODE);
    }

    public boolean checkRecordingPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_DENIED)
        {
            requestRecordingPermission();
            return false;
        }
       return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_AUDIO_PERMISSION_CODE)
        {
            if(grantResults.length>0)
            {
                boolean permissionToRecord=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(permissionToRecord)
                {
                    Toast.makeText(getApplicationContext(), "Permission Given", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getRecordingFilePath()
    {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File music = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(music,"testFile" + ".mp3");
        return file.getPath();
    }

}
