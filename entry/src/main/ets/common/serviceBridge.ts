// UI -> ServiceAbility 命令封装与 CommonEvent 订阅封装
// 注意：featureAbility / commonEvent API 路径可能随 SDK 版本不同，请按你项目模板调整 import/使用方式。

declare const featureAbility: any;
declare const commonEvent: any;

const BUNDLE_NAME = 'com.example.mymusic';
const SERVICE_ABILITY = 'com.example.mymusic.MediaServiceAbility';
const PLAYBACK_EVENT = 'com.example.mymusic.PLAYBACK_STATE';

// 发送控制命令到后台 ServiceAbility（通过 startAbility + action + parameters）
export function sendCommand(action: string, params: any) {
  try {
    const want = {
      bundleName: BUNDLE_NAME,
      abilityName: SERVICE_ABILITY,
      action: action,
      parameters: params
    };
    // startAbility 可用于启动 service ability 并传递 action
    if (typeof featureAbility !== 'undefined' && featureAbility.startAbility) {
      featureAbility.startAbility(want);
    } else {
      console.warn('featureAbility.startAbility 未找到，请按 SDK 调整调用方式');
    }
  } catch (e) {
    console.error('sendCommand error', e);
  }
}

// 订阅播放状态（ServiceAbility 发布 CommonEvent）
// callback 接收解析后的 state 对象
export function subscribePlayback(callback: (state: any) => void) {
  try {
    const subscriberInfo = {
      events: [PLAYBACK_EVENT]
    };
    if (typeof commonEvent !== 'undefined' && commonEvent.subscribe) {
      commonEvent.subscribe(subscriberInfo, (data: any) => {
        try {
          // data 可能为字符串或对象
          if (typeof data === 'string') {
            const obj = JSON.parse(data);
            callback(obj);
          } else if (data && data.data) {
            // 有些 SDK 回调会把 CommonEventData 放在 data.data
            const obj = typeof data.data === 'string' ? JSON.parse(data.data) : data.data;
            callback(obj);
          } else {
            callback(data);
          }
        } catch (err) {
          console.error('subscribePlayback parse error', err);
          callback(null);
        }
      });
    } else {
      console.warn('commonEvent.subscribe 未找到，请按 SDK 调整调用方式');
    }
  } catch (e) {
    console.error('subscribePlayback error', e);
  }
}
