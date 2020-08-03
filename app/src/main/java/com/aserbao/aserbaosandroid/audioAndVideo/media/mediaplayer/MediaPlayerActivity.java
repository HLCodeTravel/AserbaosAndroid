package com.aserbao.aserbaosandroid.audioAndVideo.media.mediaplayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;

import com.aserbao.aserbaosandroid.R;
import com.example.base.utils.MediaUtil;
import com.example.base.utils.others.MainLooper;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.media.MediaPlayer.SEEK_CLOSEST;

public class MediaPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private static final String TAG = "MediaPlayerActivity";
    private static final int REQUEST_VIDEO_CODE = 0;
    @BindView(R.id.media_surface_view)
    SurfaceView mMediaSurfaceView;

    @BindView(R.id.seekBar)
    SeekBar seekBar;
    public MediaPlayer mMediaPlayer;
    public String mVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/testMedia.mp4";
//    public String mVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/yx.mp4";
//    public String mVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/input.mp3";
    public SurfaceHolder mHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);
        mHolder = mMediaSurfaceView.getHolder();
//        initModule("");
        mHolder.addCallback(this);
        initView();
    }

    private void initView() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MainLooper.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            mMediaPlayer.seekTo(progress,SEEK_CLOSEST);
                            Log.e(TAG, "onProgressChanged: " + progress );
                        }
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHolder.addCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    private void initModule(String path) {
//        mMediaSurfaceView = new SurfaceView(this);
        mMediaPlayer = new MediaPlayer();
        try {
//            path = "http://ivi.bupt.edu.cn/hls/cctv1.m3u8";
            long videoDuration = MediaUtil.INSTANCE.getDuration(path);
            int maxDuration = (int) videoDuration / 1000;
            seekBar.setMax(maxDuration);
            Log.e(TAG, "initModule: duration ="+ maxDuration+ " mVideoPath="+ mVideoPath);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.setDisplay(mHolder);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start();
                }
            });
//            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btn_open_video(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_VIDEO_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                ContentResolver cr = this.getContentResolver();
                /** 数据库查询操作。
                 * 第一个参数 uri：为要查询的数据库+表的名称。
                 * 第二个参数 projection ： 要查询的列。
                 * 第三个参数 selection ： 查询的条件，相当于SQL where。
                 * 第三个参数 selectionArgs ： 查询条件的参数，相当于 ？。
                 * 第四个参数 sortOrder ： 结果排序。
                 */
                Cursor cursor = cr.query(uri, null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        // 视频ID:MediaStore.Audio.Media._ID
                        int videoId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                        // 视频名称：MediaStore.Audio.Media.TITLE
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                        // 视频路径：MediaStore.Audio.Media.DATA
                        mVideoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        Log.e(TAG, "onActivityResult: " + mVideoPath );
                        // 视频时长：MediaStore.Audio.Media.DURATION
                        int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                        // 视频大小：MediaStore.Audio.Media.SIZE
                        long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

                        // 视频缩略图路径：MediaStore.Images.Media.DATA
                        String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        // 缩略图ID:MediaStore.Audio.Media._ID
                        int imageId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                        // 方法一 Thumbnails 利用createVideoThumbnail 通过路径得到缩略图，保持为视频的默认比例
                        // 第一个参数为 ContentResolver，第二个参数为视频缩略图ID， 第三个参数kind有两种为：MICRO_KIND和MINI_KIND 字面意思理解为微型和迷你两种缩略模式，前者分辨率更低一些。
//                        Bitmap bitmap1 = MediaStore.Video.Thumbnails.getThumbnail(cr, imageId, MediaStore.Video.Thumbnails.MICRO_KIND, null);

                        // 方法二 ThumbnailUtils 利用createVideoThumbnail 通过路径得到缩略图，保持为视频的默认比例
                        // 第一个参数为 视频/缩略图的位置，第二个依旧是分辨率相关的kind
//                        Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(imagePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                        // 如果追求更好的话可以利用 ThumbnailUtils.extractThumbnail 把缩略图转化为的制定大小
//                        ThumbnailUtils.extractThumbnail(bitmap, width,height ,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                    }
                    cursor.close();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        if (!TextUtils.isEmpty(mVideoPath)) {
            initModule(mVideoPath);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged: " );
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed: " );
    }

    public void btn_start(View view) {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public void btn_pause(View view) {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }
}
