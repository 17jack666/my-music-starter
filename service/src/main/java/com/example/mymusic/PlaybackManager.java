package com.example.mymusic;

import ohos.app.Context;
import ohos.media.player.Player;
import ohos.media.player.PlayerCallback;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单播放管理（队列 + Player 封装）。
 * 提示：生产环境需处理更多细节（缓冲、重试、音频焦点、电话中断、异常回调详尽处理）。
 */
public class PlaybackManager {
    private Context context;
    private Player player;
    private List<Track> queue = new ArrayList<>();
    private int currentIndex = -1;

    public PlaybackManager(Context ctx) {
        this.context = ctx;
        initPlayer();
    }

    private void initPlayer() {
        player = new Player(context);
        player.setPlayerCallback(new PlayerCallback() {
            @Override
            public void onError(Player player, int what, int extra) {
                // 发布错误或停止状态
                publishState(false);
            }

            @Override
            public void onPlayBackComplete(Player player) {
                next();
            }

            @Override
            public void onBufferingChange(Player player, int percent) {
                // 可把缓冲进度发布给 UI（扩展）
            }
        });
    }

    public void play(String url, String id, String title, String artist) {
        // 简化：清空队列并播放单曲
        queue.clear();
        queue.add(new Track(id, title, artist, url));
        currentIndex = 0;
        prepareAndPlay(url);
    }

    private void prepareAndPlay(String url) {
        try {
            // 某些 SDK 需要 setSource(Uri) 或 setSource(fileDescriptor)，示例使用 setSource(String)
            player.setSource(url);
            player.prepare();
            player.play();
            publishState(true);
            showNotification();
        } catch (Exception e) {
            e.printStackTrace();
            publishState(false);
        }
    }

    public void pause() {
        try {
            if (player != null && player.isNowPlaying()) {
                player.pause();
                publishState(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        try {
            if (player != null && !player.isNowPlaying()) {
                player.play();
                publishState(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void next() {
        if (currentIndex + 1 < queue.size()) {
            currentIndex++;
            prepareAndPlay(queue.get(currentIndex).url);
        } else {
            // 没有下一首，停止播放
            if (player != null) {
                player.stop();
            }
            publishState(false);
        }
    }

    public void prev() {
        if (currentIndex - 1 >= 0) {
            currentIndex--;
            prepareAndPlay(queue.get(currentIndex).url);
        }
    }

    public void release() {
        try {
            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishState(boolean isPlaying) {
        try {
            JSONObject state = new JSONObject();
            Track t = currentIndex >= 0 ? queue.get(currentIndex) : null;
            state.put("isPlaying", isPlaying);
            state.put("trackId", t != null ? t.id : JSONObject.NULL);
            MediaServiceAbility.publishPlaybackState(context, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNotification() {
        // 通用示例：构建并展示前台通知（Notification）
        // 注意：不同 SDK/版本 Notification API 名称或参数有差异，以下为通用伪实现与步骤，
        // 你可能需要按 DevEco Studio 生成的 NotificationHelper 或示例代码做小修改。

        // 1) 构建 Notification/NotificationRequest（包含控件：暂停/继续/下一首/上一首）
        // 2) 为控件创建对应的 Want（action 为 "PAUSE"/"RESUME"/"NEXT"/"PREV"），并将 Want 设置为 notification 点击/按钮的回调
        // 3) 调用 Ability 的 startForeground(notificationId, notification) 或相应 API 将 ServiceAbility 提升为前台

        // 示例伪代码（非确定性 API；如编译报错请按你的 SDK 替换实现）：
        /*
        try {
            NotificationRequest request = new NotificationRequest();
            request.setContentTitle(queue.get(currentIndex).title);
            request.setContentText(queue.get(currentIndex).artist);
            // 添加操作按钮，用 Want 启动当前 MediaServiceAbility 并带 action 字段
            Want pauseWant = new Want();
            pauseWant.setParam("action", "PAUSE");
            // ... 构建更多 Want
            // 将 notificationRequest 与 action 按钮关联
            NotificationHelper.publishNotification(request);
            // 将 ability 提升为前台（若 SDK 支持）
            startForeground(NOTIFY_ID, request.toNotification());
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        // 为避免因 API 差异导致无法编译，我把此处留作“按 SDK 适配”的位置。
    }

    static class Track {
        String id, title, artist, url;
        Track(String id, String title, String artist, String url) {
            this.id = id; this.title = title; this.artist = artist; this.url = url;
        }
    }
}
