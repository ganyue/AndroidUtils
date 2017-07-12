package com.gy.utils.audio.mediaplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.gy.utils.R;
import com.gy.utils.img.ImageLoaderUtils;
import com.gy.utils.img.OnImageloadListener;

/**
 * Created by ganyu on 2017/4/10.
 *
 */

public class MediaNotifier {
    private static Class jumpPage;
    public static void setJumpPage (Class clazz) {
        jumpPage = clazz;
    }

    private final String ACTION_NOTIFICATION = "action_notification";
    private NotifierReceiver notifierReceiver;
    private NotificationManager notificationManager;
    private RemoteViews remoteViews;
    private ImageLoaderUtils imageLoaderUtils;
    private Context context;
    private PendingIntent pendingIntent;
    private String currentPicUrl;
    private String currentName;
    private String currentSinger;
    private boolean isPlaying;

    public MediaNotifier (Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        pendingIntent = PendingIntent.getActivity(context, 123, new Intent(context, jumpPage), PendingIntent.FLAG_UPDATE_CURRENT);

        notifierReceiver = new NotifierReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_NOTIFICATION);
        context.registerReceiver(notifierReceiver, intentFilter);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.util_media_notification_contentv);
        imageLoaderUtils = ImageLoaderUtils.getInstance(context);

        registClickListener(ACTION_NOTIFICATION, "event", 1, 1, R.id.iv_playOrPause);
        registClickListener(ACTION_NOTIFICATION, "event", 2, 2, R.id.iv_next);
    }

    private void registClickListener (String action, String extraName, int extraValue, int requestCode, int id) {
        Intent intent = new Intent(action);
        intent.putExtra(extraName, extraValue);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(id, pendingIntent);
    }

    public void sendNotification (String name, String singer, final String picUrl, boolean isPlaying) {
        if (TextUtils.isEmpty(name)) name = "贝瓦儿歌";
        if (TextUtils.isEmpty(singer)) singer = "贝瓦儿歌";
        currentPicUrl = ""+picUrl;
        currentName = ""+name;
        currentSinger = ""+singer;
        this.isPlaying = isPlaying;

        remoteViews.setTextViewText(R.id.tv_name, ""+name);
        remoteViews.setTextViewText(R.id.tv_singer, ""+singer);
        remoteViews.setImageViewResource(R.id.iv_icon, R.mipmap.bg_imageloader_default);
        if (isPlaying) remoteViews.setImageViewResource(R.id.iv_playOrPause, R.drawable.notification_player_stop);
        else remoteViews.setImageViewResource(R.id.iv_playOrPause, R.drawable.notification_player_play);
        remoteViews.setImageViewResource(R.id.iv_next, R.drawable.notification_player_next);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(""+name)
                .setContentText(""+singer)
                .setSmallIcon(R.drawable.util_notification_default)
                .setContent(remoteViews)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.contentView = remoteViews;
        notification.icon = R.drawable.util_notification_default;
        notificationManager.notify(123, notification);

        currentPicUrl = picUrl;
        imageLoaderUtils.loadImage(context, picUrl, onImageloadListener);
    }

    private void sendNotification (String name, String singer, Bitmap bitmap, boolean isPlaying) {
        if (TextUtils.isEmpty(name)) name = "贝瓦儿歌";
        if (TextUtils.isEmpty(singer)) singer = "贝瓦儿歌";
        remoteViews.setTextViewText(R.id.tv_name, ""+name);
        remoteViews.setTextViewText(R.id.tv_singer, ""+singer);
        remoteViews.setImageViewBitmap(R.id.iv_icon, bitmap);
        if (isPlaying) remoteViews.setImageViewResource(R.id.iv_playOrPause, R.drawable.notification_player_stop);
        else remoteViews.setImageViewResource(R.id.iv_playOrPause, R.drawable.notification_player_play);
        remoteViews.setImageViewResource(R.id.iv_next, R.drawable.notification_player_next);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(""+name)
                .setContentText(""+singer)
                .setSmallIcon(R.drawable.util_notification_default)
                .setContent(remoteViews)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.contentView = remoteViews;
        notification.icon = R.drawable.util_notification_default;
        notificationManager.notify(123, notification);
    }


    private OnImageloadListener onImageloadListener = new OnImageloadListener() {
        @Override
        public void onImageLoadSuccess(String url, Bitmap bitmap) {
            if (url.equals(currentPicUrl)) sendNotification(currentName, currentSinger, bitmap, isPlaying);
        }

        @Override
        public void onImageLoadError(String url, Object extra) {
        }
    };

    class NotifierReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int event = intent.getIntExtra("event", 0);
            switch (event) {
                case 1:
                    if (notificationClickListener != null) {
                        notificationClickListener.playOrPause();
                    }
                    break;
                case 2:
                    if (notificationClickListener != null) {
                        notificationClickListener.next();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static NotificationClickListener notificationClickListener;
    public static void setNotificationClickListener (NotificationClickListener listener) {
        notificationClickListener = listener;
    }

    public interface NotificationClickListener {
        void playOrPause ();
        void next ();
    }
}
