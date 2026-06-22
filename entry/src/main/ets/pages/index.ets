<script lang="ts">
// 注意：具体导入路径可能依 SDK 模板不同而需调整。
// 在 DevEco Studio 中按模板导入 ArkUI 组件（此处为示意）。
import { Column, Row, List, Text, Button } from '@ohos.arkui'; // 如有 lint 报错，请用项目模板导入路径
import PlayerBar from '../component/PlayerBar.ets';
import { sendCommand, subscribePlayback } from '../common/serviceBridge';

interface Track {
  id: string;
  title: string;
  artist?: string;
  url: string;
}

export default {
  data: {
    tracks: [] as Track[],
    currentTrackId: '',
    isPlaying: false,
  },
  onInit() {
    // 示例播放列表（你可以替换为从网络接口获取）
    this.tracks = [
      { id: '1', title: '示例曲目 A', artist: '演唱者 A', url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3' },
      { id: '2', title: '示例曲目 B', artist: '演唱者 B', url: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3' }
    ];

    // 订阅播放状态
    subscribePlayback((state: any) => {
      if (state) {
        this.isPlaying = !!state.isPlaying;
        this.currentTrackId = state.trackId || '';
      }
    });
  },

  playTrack(track: Track) {
    sendCommand('PLAY', { id: track.id, url: track.url, title: track.title, artist: track.artist });
  },

  togglePlay() {
    sendCommand(this.isPlaying ? 'PAUSE' : 'RESUME', {});
  },

  next() {
    sendCommand('NEXT', {});
  },

  prev() {
    sendCommand('PREV', {});
  }
};
</script>

<template>
  <Column class="container">
    <Text value="我的音乐" class="title" />
    <List for="{{tracks}}" class="list">
      <Row class="list-item">
        <Column class="meta">
          <Text value="{{item.title}}" class="song-title" />
          <Text value="{{item.artist}}" class="song-artist" />
        </Column>
        <Button value="播放" onClick="{{() => $this.playTrack(item)}}" />
      </Row>
    </List>

    <PlayerBar
      trackId="{{currentTrackId}}"
      isPlaying="{{isPlaying}}"
      onPlayPause="{{togglePlay}}"
      onNext="{{next}}"
      onPrev="{{prev}}"
    />
  </Column>
</template>

<style>
.container { padding: 16px; }
.title { font-size: 20px; margin-bottom: 12px; }
.list-item { justify-content: space-between; align-items: center; padding: 8px 0; }
.song-title { font-size: 16px; }
.song-artist { font-size: 12px; color: #666; }
.player-bar { position: absolute; bottom: 0; left: 0; right: 0; height: 60px; background: #f2f2f2; align-items: center; padding: 8px; }
</style>
