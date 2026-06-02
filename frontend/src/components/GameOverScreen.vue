<template>
  <div class="gameover panel">
    <div style="font-size:56px;">{{ state.phase === 'victory' ? '👑' : '💀' }}</div>
    <h1 :style="{ color: state.phase === 'victory' ? '#f0c040' : '#e74c3c' }">
      {{ state.phase === 'victory' ? '恭喜通关！' : '游戏结束' }}
    </h1>
    <div class="stats">
      <div>到达：第 <span>{{ state.act }}</span> 幕 第 <span>{{ state.floor }}</span> 层</div>
      <div>金币：<span>{{ state.gold }}</span></div>
      <div>牌组大小：<span>{{ state.deckSize }}</span></div>
    </div>
    <div style="margin-top:10px;">
      <div style="font-size:12px;color:var(--gray);margin-bottom:4px;">遗物：</div>
      <div v-for="(name, i) in state.relicNames" :key="i" style="font-size:12px;color:var(--purple);">
        {{ name }}
      </div>
    </div>
    <button class="btn btn-gold btn-large" style="margin-top:24px;" @click="$emit('restart')">
      🔄 重新攀登
    </button>
  </div>
</template>

<script setup>
defineProps({ state: Object })
defineEmits(['restart'])
</script>
