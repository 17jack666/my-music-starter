<script lang="ts">
// 简单的底部播放条组件
export default {
  props: ['trackId', 'isPlaying', 'onPlayPause', 'onNext', 'onPrev'],
  render() {
    return (
      <div class="player-bar">
        <button onClick={this.props.onPrev}>上一首</button>
        <button onClick={this.props.onPlayPause}>{this.props.isPlaying ? '暂停' : '播放'}</button>
        <button onClick={this.props.onNext}>下一首</button>
        <span class="now"> {this.props.trackId ? `正在播放：${this.props.trackId}` : '未播放'} </span>
      </div>
    );
  }
};
</script>

<style>
.player-bar { display: flex; align-items: center; justify-content: space-around; padding: 8px; background: #fafafa; border-top: 1px solid #ddd; }
.now { font-size: 12px; color: #333; }
</style>
