package com.gy.utils.audio.mpdplayer.manage;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.gy.utils.audio.mpdplayer.exception.MPDServerException;
import com.gy.utils.audio.mpdplayer.helpers.MPDAsyncHelper;
import com.gy.utils.audio.mpdplayer.mpd.Item;
import com.gy.utils.audio.mpdplayer.mpd.MPD;
import com.gy.utils.audio.mpdplayer.mpd.MPDPlaylist;
import com.gy.utils.audio.mpdplayer.mpd.MPDStatus;
import com.gy.utils.audio.mpdplayer.mpd.Music;
import com.gy.utils.audio.mpdplayer.tools.SettingsHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by lwk on 2016/2/23.
 */
public class MpdManage {

    private static MpdManage mpdManage;

    public static Application mpdApplication;

    public MPDAsyncHelper oMPDAsyncHelper = null;
    private SettingsHelper settingsHelper = null;
    private ApplicationState state = new ApplicationState();

    private Collection<Object> connectionLocks = new LinkedList<Object>();
    private Activity currentActivity;

    protected int iJobID = -1;
    protected int iSongJobID = -1;
    private List<Item> items;
    private List<Music> songs;
//    private Map<String, Object> map;
    private List<Map<String, Object>> list;

    public class ApplicationState {
        public boolean streamingMode = false;
        public boolean settingsShown = false;
        public boolean warningShown = false;
        public MPDStatus currentMpdStatus = null;
    }


    private MpdManage(){

    }

    public static MpdManage getInstance(Application app){
        if(mpdManage == null){
            mpdManage = new MpdManage();
        }
        if (app != null) {
            mpdApplication = app;
        }
        return mpdManage;
    }

    public void addConnectionListener (MPDAsyncHelper.ConnectionListener listener) {
        oMPDAsyncHelper.addConnectionListener(listener);
    }

    public void removeConnectionListener (MPDAsyncHelper.ConnectionListener listener) {
        oMPDAsyncHelper.removeConnectionListener(listener);
    }

    public void initMpd(MPDAsyncHelper.ConnectionListener listener){
        MPD.setApplicationContext(mpdApplication);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.VmPolicy vmpolicy = new StrictMode.VmPolicy.Builder().penaltyLog().build();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            StrictMode.setVmPolicy(vmpolicy);
        }

        oMPDAsyncHelper = new MPDAsyncHelper();
        oMPDAsyncHelper.addConnectionListener(listener);

        settingsHelper = new SettingsHelper(mpdApplication, oMPDAsyncHelper);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mpdApplication);
        if(!settings.contains("albumTrackSort"))
            settings.edit().putBoolean("albumTrackSort", true).commit();
    }

    public void connect(String server) {
        if (!oMPDAsyncHelper.isMonitorAlive()) {
            oMPDAsyncHelper.startMonitor();
        }
        // really connect
        if(TextUtils.isEmpty(server)) {
            SharedPreferences mpdPreferences = mpdApplication.getSharedPreferences(
                    "bevabb_mpd", Context.MODE_PRIVATE);
            server = mpdPreferences.getString("server", "");
            Log.d("wl", "------connect--------IP:" + server);
        }
        oMPDAsyncHelper.connect(server);
    }

    public void disconnect() {
        oMPDAsyncHelper.stopMonitor();
        oMPDAsyncHelper.disconnect();
        Log.d("wl", "-----断开连接1111------");
    }

    public void setActivity(Object activity, String server) {
        if (activity instanceof Activity)
            currentActivity = (Activity) activity;

        connectionLocks.add(activity);
        checkConnectionNeeded(server);
    }

    public void unsetActivity(Object activity, String server) {
        connectionLocks.remove(activity);
        checkConnectionNeeded(server);

        if (currentActivity == activity)
            currentActivity = null;
    }

    private void checkConnectionNeeded(String server) {
        if (connectionLocks.size() > 0) {
            if (!oMPDAsyncHelper.isMonitorAlive()) {
                oMPDAsyncHelper.startMonitor();
            }
            if (!oMPDAsyncHelper.oMPD.isConnected() && (currentActivity == null)) {
                connect(server);
                Log.d("wl", "-----重连1111----");
            }
        } else {
            disconnect();
        }
    }

    public void updateTrackInfo(final UpdateTrackInfoListener updateTrackInfoListener){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    MPDStatus mpdStatus = oMPDAsyncHelper.oMPD.getStatus(true);
                    Music actSong = null;
                    if(mpdStatus != null){
                        String state = mpdStatus.getState();
                        if (state != null) {
                            int songPos = mpdStatus.getSongPos();
                            if (songPos >= 0) {
                                actSong = oMPDAsyncHelper.oMPD.getPlaylist().getByIndex(songPos);
                            }
                        }
                    }
                    updateTrackInfoListener.onSuccess(mpdStatus, actSong);
                } catch (MPDServerException e) {
                    updateTrackInfoListener.onError(e.toString());
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public int changePlayMode(int mode){
        mode = (mode + 1) % 3;
        switch (mode) {
            case 0:
                try {
                    if(oMPDAsyncHelper.oMPD.getStatus().isRandom()){
                        Log.d("wl", "1111111111111111111111");
                        oMPDAsyncHelper.oMPD.setRandom(false);
                    }
                    if(oMPDAsyncHelper.oMPD.getStatus().isSingle()){
                        Log.d("wl", "222222222222222222222222");
                        oMPDAsyncHelper.oMPD.setSingle(false);
                    }
                    oMPDAsyncHelper.oMPD.setRepeat(true);
                    Log.d("wl", "-----切换列表循环播放模式-------");
                } catch (MPDServerException e) {
                    Log.d("wl", "-----列表循环模式异常-------"+e.toString());
                }
                break;
            case 1:
                try {
                    if(oMPDAsyncHelper.oMPD.getStatus().isRepeat()){
                        Log.d("wl", "333333333333333333333");
                        oMPDAsyncHelper.oMPD.setRepeat(false);
                    }
                    if(oMPDAsyncHelper.oMPD.getStatus().isSingle()){
                        Log.d("wl", "4444444444444444444444");
                        oMPDAsyncHelper.oMPD.setSingle(false);
                    }
                    oMPDAsyncHelper.oMPD.setRandom(true);
                    Log.d("wl", "-----切换随机播放模式-------");
                } catch (MPDServerException e) {
                    Log.d("wl", "-----随机播放模式异常-------"+e.toString());
                }
                break;
            case 2:
                try {
                    if(oMPDAsyncHelper.oMPD.getStatus().isRepeat()){
                        Log.d("wl", "555555555555555555555555555");
                        oMPDAsyncHelper.oMPD.setRepeat(false);
                    }
                    if(oMPDAsyncHelper.oMPD.getStatus().isRandom()){
                        Log.d("wl", "66666666666666666666666666666");
                        oMPDAsyncHelper.oMPD.setRandom(false);
                    }
                    oMPDAsyncHelper.oMPD.setSingle(true);
                    oMPDAsyncHelper.oMPD.setRepeat(true);
                    Log.d("wl", "-----切换单曲循环播放模式-------"+oMPDAsyncHelper.oMPD.getStatus().isSingle());
                } catch (MPDServerException e) {
                    Log.d("wl", "-----单曲循环播放模式异常-------"+e.toString());
                }
                break;
            default:
                break;
        }
        return  mode;
    }

    public List<Music> getPlaylist() {
        MPDPlaylist playlist = oMPDAsyncHelper.oMPD.getPlaylist();
        return  playlist.getMusicList();
    }

    public void getBevabbPlayLists(final GetBevabbPlayListsListener listsListener){
        list = new ArrayList<>();
        new Thread(){
            @Override
            public void run() {
                super.run();
                    try {
                        Log.d("wl", "------获取宝宝歌单列表22--------"+items);
                        items = oMPDAsyncHelper.oMPD.getPlaylists(true);
                        Log.d("wl", "------获取宝宝歌单列表11--------"+items);
                        for (int i=0; i<items.size(); i++){
                            final Item item = items.get(i);
                            final int index = i;
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    try{
                                        Map<String, Object> map = new HashMap<>();
                                        Log.d("wl", "------获取宝宝歌单下单曲列表22--------"+songs);
                                        if(item.getName().equals("0") || item.getName().equals("1") || item.getName().equals("2") || item.getName().equals("3") || item.getName().equals("4") || item.getName().equals("5") || item.getName().equals("6") || item.getName().equals("7") || item.getName().equals("8")){
                                            songs = oMPDAsyncHelper.oMPD.getPlaylistSongs(item.getName());
                                            Log.d("wl", "------获取宝宝歌单下单曲列表11--------"+songs);
                                            if(songs != null && songs.size() > 0){
                                                map.put("num", songs.size());
                                            }else{
                                                map.put("num", 0);
                                            }
                                            map.put("id", item.getName());

                                            switch (Integer.parseInt(item.getName())){
//                                                case 8:
//                                                    map.put("name", "Happy English");
//                                                    break;
//
//                                                case 0:
//                                                    map.put("name", "收藏列表");
//                                                    break;
//
//                                                case 1:
//                                                    map.put("name", "贝瓦儿歌");
//                                                    break;
//
//                                                case 2:
//                                                    map.put("name", "贝瓦童谣");
//                                                    break;
//
//                                                case 3:
//                                                    map.put("name", "贝瓦故事");
//                                                    break;
//
//                                                case 4:
//                                                    map.put("name", "贝瓦学堂");
//                                                    break;
//
//                                                case 5:
//                                                    map.put("name", "睡前听听");
//                                                    break;
//
//                                                case 6:
//                                                    map.put("name", "路上听听");
//                                                    break;
//
//                                                case 7:
//                                                    map.put("name", "习惯养成");
//                                                    break;

                                                case 8:
                                                    map.put("name", "边听边动");
                                                    break;

                                                case 0:
                                                    map.put("name", "收藏列表");
                                                    break;

                                                case 1:
                                                    map.put("name", "贝瓦童谣");
                                                    break;

                                                case 2:
                                                    map.put("name", "贝瓦儿歌");
                                                    break;

                                                case 3:
                                                    map.put("name", "贝瓦学堂");
                                                    break;

                                                case 4:
                                                    map.put("name", "HappyEnglish");
                                                    break;

                                                case 5:
                                                    map.put("name", "随便听听");
                                                    break;

                                                case 6:
                                                    map.put("name", "睡前听听");
                                                    break;

                                                case 7:
                                                    map.put("name", "路上听听");
                                                    break;
                                            }

//                                            if (list != null) {
//                                                for (Map m: list) {
//                                                    if (map.get("name").equals(m.get("name"))) {
//                                                        list.remove(m);
//                                                        break;
//                                                    }
//                                                }
//                                            }
                                            list.add(map);
                                            if(list != null && list.size() >= 9){
                                                listsListener.onSuccess(list);
                                            }
                                        }
                                    }catch (MPDServerException e){
                                        Log.d("wl", "获取宝宝歌单下单曲列表失败"+e.toString());
                                    }
                                }
                            }.start();

                        }
                    } catch (MPDServerException e) {
                        e.printStackTrace();
                    }
            }
        }.start();
    }

    public void getBevabbTracksByPlistId(final String id, final GetBevabbTracksListener listener){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Log.d("wl", "-----获取宝宝歌单下单曲列表11---------"+songs);
                    List<Music> songs = oMPDAsyncHelper.oMPD.getPlaylistSongs(id);
                    Log.d("wl", "-----获取宝宝歌单下单曲列表22---------"+songs);
                    listener.onSuccess(songs);
                }catch (MPDServerException e){
                    Log.d("wl", "获取宝宝歌单下单曲列表失败"+e.toString());
                    listener.onError(e.toString());
                }
            }
        }.start();
    }

    public void clearCurrentPlaylist(){
        try {
            oMPDAsyncHelper.oMPD.getPlaylist().clear();
        } catch (MPDServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void playBySongId(final int songId){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    oMPDAsyncHelper.oMPD.skipToId(songId);
                } catch (MPDServerException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public ApplicationState getApplicationState() {
        return state;
    }

    public void replacePlaylist(String playlist, boolean replace, boolean play){
        try {
            oMPDAsyncHelper.oMPD.getPlaylist().clear();
            oMPDAsyncHelper.oMPD.add(playlist, replace, play);
            Log.d("wl", "切换贝瓦宝宝歌单"+playlist);
        } catch (MPDServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addUrlToPlaylist(URL url,boolean replace, boolean play ){
        try {
            oMPDAsyncHelper.oMPD.add(url, replace, play);
            Log.d("wl", "添加url到贝瓦宝宝歌单"+url);
        } catch (MPDServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean deleteTrackByPlistIds(int plistId, int id){
        List<String> result = null;
        try {
            result = oMPDAsyncHelper.oMPD.removeFromPlaylist(String.valueOf(plistId), id);
            Log.d("wl", "删除贝瓦宝宝歌单下歌曲"+plistId+"-----"+id+"----删除结果-----"+result);
        } catch (MPDServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(result !=null){
            return true;
        }else{
            return  false;
        }
    }

    public boolean addTrackToPlaylist(String playlistName, String url){
        List<String> result = null;
        try {
            result = oMPDAsyncHelper.oMPD.addToPlaylist(playlistName, url);
            Log.d("wl", "添加单曲到指定歌单"+playlistName+"----添加结果-----"+result);
        } catch (MPDServerException e) {
            // TODO Auto-generated catch block
            Log.d("wl", "------addTrackToPlaylist------"+e.toString());
            e.printStackTrace();
        }
        if(result !=null){
            return true;
        }else{
            return  false;
        }
    }

    public void removeCurrentPlaylistSongById(int songId){
        try {
            oMPDAsyncHelper.oMPD.getPlaylist().removeById(songId);
        } catch (MPDServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
