package com.example.mymusic;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.event.commonevent.CommonEventData;
import ohos.event.commonevent.CommonEventManager;
import ohos.app.Context;
import org.json.JSONObject;

/**
 * ServiceAbility：接收 UI 的 action/want，控制播放并通过 CommonEvent 发布播放状态。
 */
public class MediaServiceAbility extends Ability {
    private PlaybackManager playbackManager;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        if (playbackManager == null) {
            playbackManager = new PlaybackManager(getContext());
        }
        handleIntent(intent);
    }

    @Override
    public void onStop() {
        if (playbackManager != null) {
            playbackManager.release();
            playbackManager = null;
        }
        super.onStop();
    }

    @Override
    public void onCommand(Intent intent, boolean restart, int startId) {
        // 某些场景会走到 onCommand
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        if (action == null) return;

        switch (action) {
            case "PLAY":
                String url = intent.getStringParam("url");
                String id = intent.getStringParam("id");
                String title = intent.getStringParam("title");
                String artist = intent.getStringParam("artist");
                playbackManager.play(url, id, title, artist);
                break;
            case "PAUSE":
                playbackManager.pause();
                break;
            case "RESUME":
                playbackManager.resume();
                break;
            case "NEXT":
                playbackManager.next();
                break;
            case "PREV":
                playbackManager.prev();
                break;
            default:
                break;
        }
    }

    // 工具：发布播放状态到 CommonEvent（供 UI 订阅）
    public static void publishPlaybackState(Context context, JSONObject state) {
        try {
            CommonEventData data = new CommonEventData();
            data.setData(state.toString());
            CommonEventManager.publishCommonEvent(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
