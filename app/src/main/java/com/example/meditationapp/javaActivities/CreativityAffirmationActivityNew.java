package com.example.meditationapp.javaActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.MediaRouteButton;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.meditationapp.Api.ApiInterface;
import com.example.meditationapp.Api.RetrofitClientInstance;
import com.example.meditationapp.Custom_Widgets.CustomBoldtextView;
import com.example.meditationapp.ModelClasses.MusicPlayerResponse;
import com.example.meditationapp.R;
import com.example.meditationapp.activities.MainActivity;
import com.example.meditationapp.adapter.PlayerNatureAdapter;
import com.example.meditationapp.utilityClasses.BackgroundSoundService;
import com.example.meditationapp.utilityClasses.NatureSoundService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.tankery.lib.circularseekbar.CircularSeekBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreativityAffirmationActivityNew extends AppCompatActivity {

    public static LinearLayout ll_options, img_vol_bar;
    ImageView musicbtn, back_btn;
    static AppCompatImageView player_play, player_backward, player_forward, player_vol_low, player_vol_high;
    CustomBoldtextView player_timer;
    String song,userID;
    int total_duration, current_time, volume, default_volume;
    Boolean playing;
    CircularSeekBar circularSeekBar;
    SeekBar player_vol_bar;
    private boolean blockGUIUpdate;
    AudioManager audioManager;
    RecyclerView nature_recycler;
    ArrayList<String> playallList;
    ApiInterface apiInterface;
    String mypreference = "mypref", user_id = "user_id";
    //    private GuiReceiver receiver;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creativity_two_fragment);

//        SharedPreferences preferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
//        playing = preferences.getBoolean("playing", false);
        SharedPreferences pref = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        userID = pref.getString(user_id, "");

        ll_options = findViewById(R.id.ll_options);
        musicbtn = findViewById(R.id.img_music);
        player_vol_bar = findViewById(R.id.player_vol_bar);
        player_play = findViewById(R.id.player_play);
        player_timer = findViewById(R.id.player_timer);
        back_btn = findViewById(R.id.img_back_four);
        circularSeekBar = findViewById(R.id.seekbar);
        img_vol_bar = findViewById(R.id.player_vol);
        nature_recycler = findViewById(R.id.player_nature_recyclerView);
        player_backward = findViewById(R.id.player_backward);
        player_forward = findViewById(R.id.player_forward);
        player_vol_low = findViewById(R.id.player_vol_low);
        player_vol_high = findViewById(R.id.player_vol_high);

        playallList = getIntent().getStringArrayListExtra("playlist");

        song = getIntent().getStringExtra("song");
        if (song == null) {
            if (playallList.size() != 0) {
                song = playallList.get(0);
            }
//            song = "https://clientstagingdev.com/meditation/public/voice/1586425636.mp3";
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CreativityAffirmationActivityNew.this, LinearLayoutManager.HORIZONTAL, false);
        nature_recycler.setLayoutManager(linearLayoutManager);

        getNatureData(userID);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        player_play.setImageResource(R.mipmap.pause);
        Intent m_intent = new Intent(CreativityAffirmationActivityNew.this, BackgroundSoundService.class);
        m_intent.putExtra("main_song", song);
        m_intent.putStringArrayListExtra("playlist", playallList);
        m_intent.putExtra("player", "Play");
        startService(m_intent);
        playing = true;
        Log.e("switch", "a");

        circularSeekBar.setMax(total_duration);

        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            int time;

            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                time = (int) progress;
//
                circularSeekBar.setProgress(this.time);
                if (fromUser)
                    player_timer.setText(getTimeString(time));
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                unblockGUIUpdate();
                Intent m_intent = new Intent(CreativityAffirmationActivityNew.this, BackgroundSoundService.class);
                m_intent.putExtra("seek_to", time * 1000);
                m_intent.putExtra("player", "ACTION_SEEK_TO");
                startService(m_intent);
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                blockGUIUpdate = true;
            }
        });

        circularSeekBar.setPointerColor(ResourcesCompat.getColor(getResources(), R.color.weight_loss_pointer, null));
//        circularSeekBar.setPointerColor(Color.parseColor("#C0C0C0"));
        circularSeekBar.setCircleProgressColor(ResourcesCompat.getColor(getResources(), R.color.weight_loss, null));
        circularSeekBar.setPointerHaloColor(ResourcesCompat.getColor(getResources(), R.color.white, null));

        CreativityAffirmationActivityNew.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(CreativityAffirmationActivityNew.this, BackgroundSoundService.class);
                intent.putExtra("player", "ACTION_SEND_INFO");
                startService(intent);
                handler.postDelayed(this, 1000);
            }
        });

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            default_volume = volume;
        }
        player_vol_bar.setProgress(volume);
        player_vol_bar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));

        player_vol_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Intent n_intent = new Intent(CreativityAffirmationActivityNew.this, NatureSoundService.class);
                n_intent.putExtra("player", "Vol_bar");
                n_intent.putExtra("volume", progress);
                startService(n_intent);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        player_vol_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent n_intent = new Intent(CreativityAffirmationActivityNew.this, NatureSoundService.class);
                n_intent.putExtra("player", "Vol_low");
                startService(n_intent);

            }
        });

        player_vol_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent n_intent = new Intent(CreativityAffirmationActivityNew.this, NatureSoundService.class);
                n_intent.putExtra("player", "Vol_high");
                startService(n_intent);

            }
        });

        musicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_options.getVisibility() == View.VISIBLE) {
                    ll_options.setVisibility(View.INVISIBLE);
                    if (img_vol_bar.getVisibility() == View.VISIBLE) {
                        img_vol_bar.setVisibility(View.INVISIBLE);
                    }
                    if (isMyServiceRunning(NatureSoundService.class)) {
                        stopService(new Intent(CreativityAffirmationActivityNew.this, NatureSoundService.class));
                    }
                } else {
                    ll_options.setVisibility(View.VISIBLE);
                }
            }
        });

//        ll_options.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                img_vol_bar.setVisibility(View.VISIBLE);
//                if (isMyServiceRunning(NatureSoundService.class)) {
//                    stopService(new Intent(CreativityAffirmationActivityNew.this, NatureSoundService.class));
//                } else {
//                    Intent n_intent = new Intent(CreativityAffirmationActivityNew.this, NatureSoundService.class);
//                    n_intent.putExtra("nature_song", "https://clientstagingdev.com/meditation/public/voice/1586425636.mp3");
//                    n_intent.putExtra("player", "Play");
//                    startService(n_intent);
//                }
//            }
//        });

        player_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning(BackgroundSoundService.class)) {
                    if (playing) {
                        player_play.setImageResource(R.mipmap.player_sound_play);
                        Intent m_intent = new Intent(CreativityAffirmationActivityNew.this, BackgroundSoundService.class);
                        m_intent.putExtra("player", "Pause");
                        startService(m_intent);
                        if (isMyServiceRunning(NatureSoundService.class)) {
                            Intent n_intent = new Intent(CreativityAffirmationActivityNew.this, NatureSoundService.class);
                            n_intent.putExtra("player", "Pause");
                            startService(n_intent);
                        }
                        playing = false;
                        Log.e("switch", "b");
                    } else {
                        player_play.setImageResource(R.mipmap.pause);
                        Intent m_intent = new Intent(CreativityAffirmationActivityNew.this, BackgroundSoundService.class);
                        m_intent.putExtra("player", "Resume");
                        startService(m_intent);
                        if (isMyServiceRunning(NatureSoundService.class)) {
                            Intent n_intent = new Intent(CreativityAffirmationActivityNew.this, NatureSoundService.class);
                            n_intent.putExtra("player", "Resume");
                            startService(n_intent);
                        }
                        playing = true;
                        Log.e("switch", "c");
                    }
                }
            }
        });

        player_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n_intent = new Intent(CreativityAffirmationActivityNew.this, BackgroundSoundService.class);
                n_intent.putExtra("player", "Seek_backward");
                startService(n_intent);
            }
        });

        player_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n_intent = new Intent(CreativityAffirmationActivityNew.this, BackgroundSoundService.class);
                n_intent.putExtra("player", "Seek_forward");
                startService(n_intent);
            }
        });

    }

    private void unblockGUIUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                blockGUIUpdate = false;
            }
        }, 150);
    }

    private static String getTimeString(int totalTime) {
        long s = totalTime % 60;
        long m = (totalTime / 60) % 60;
        long h = totalTime / 3600;

        String stringTotalTime;
        if (h != 0)
            stringTotalTime = String.format("%02d:%02d:%02d", h, m, s);
        else
            stringTotalTime = String.format("%02d:%02d", m, s);
        return stringTotalTime;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("reume", "resume______");

        LocalBroadcastManager.getInstance(CreativityAffirmationActivityNew.this).registerReceiver(
                MReceiver, new IntentFilter("GUI_UPDATE_ACTION"));

        Intent mintent = new Intent(CreativityAffirmationActivityNew.this, BackgroundSoundService.class);
        mintent.putExtra("player", "ACTION_SEND_INFO");
        startService(mintent);

        LocalBroadcastManager.getInstance(CreativityAffirmationActivityNew.this).registerReceiver(
                NReceiver, new IntentFilter("VOICE_UPDATE_ACTION"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(CreativityAffirmationActivityNew.this, NatureSoundService.class));
        stopService(new Intent(CreativityAffirmationActivityNew.this, BackgroundSoundService.class));
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, default_volume, 0);
//        if (MReceiver != null) {
//            unregisterReceiver(MReceiver);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (MReceiver != null) {
//            unregisterReceiver(MReceiver);
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("playing", playing);
        editor.apply();
    }

    public static void pause() {
        player_play.setImageResource(R.mipmap.player_sound_play);
        Log.e("switch", "d");
    }
    //

    private BroadcastReceiver MReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            current_time = intent.getIntExtra("ACTUAL_TIME_VALUE_EXTRA", 0);
            total_duration = intent.getIntExtra("TOTAL_TIME_VALUE_EXTRA", 0);
//            Log.e("time", String.valueOf(intent.getIntExtra("ACTUAL_TIME_VALUE_EXTRA", 200)));
//            Log.e("duration", String.valueOf(intent.getIntExtra("TOTAL_TIME_VALUE_EXTRA", 200)));
            String time = getTimeString(current_time);
            Log.e("switcha", String.valueOf(current_time));
            if (circularSeekBar != null) {
                circularSeekBar.setProgress(current_time);
                circularSeekBar.setMax(total_duration);
            }
            if (player_timer != null) {
                player_timer.setText(time);
            }
        }
    };

    private BroadcastReceiver NReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            volume = intent.getIntExtra("Volume", 50);
//            Log.e("time", String.valueOf(intent.getIntExtra("ACTUAL_TIME_VALUE_EXTRA", 200)));
//            Log.e("duration", String.valueOf(intent.getIntExtra("TOTAL_TIME_VALUE_EXTRA", 200)));
            if (player_vol_bar != null) {
                player_vol_bar.setProgress(volume);
                player_vol_bar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
            }
        }
    };

    public void getNatureData(String user_Id) {
        apiInterface = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<MusicPlayerResponse> call = apiInterface.getMusicResponse(user_Id);

        call.enqueue(new Callback<MusicPlayerResponse>() {
            @Override
            public void onResponse(Call<MusicPlayerResponse> call, Response<MusicPlayerResponse> response) {
                if (response.isSuccessful()){

                    MusicPlayerResponse resource = response.body();
                    if (resource.getSuccess()){
                        PlayerNatureAdapter adapter = new PlayerNatureAdapter(CreativityAffirmationActivityNew.this,resource.getData().getNature());
                        nature_recycler.setAdapter(adapter);

                    }else {
                        Toast.makeText(CreativityAffirmationActivityNew.this, ""+resource.getMessages(), Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(CreativityAffirmationActivityNew.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<MusicPlayerResponse> call, Throwable t) {
                Toast.makeText(CreativityAffirmationActivityNew.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
