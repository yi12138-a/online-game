<template>
  <div class="panel">
    <h2 style="color:var(--gold);margin-bottom:6px;">🗺️ 第 {{ state.act }} 幕 · 第 {{ state.floor }} 层</h2>
    <p style="color:var(--gray);margin-bottom:16px;">选择你的路线：</p>
    <div
      v-for="(opt, i) in state.mapOptions"
      :key="i"
      class="map-option"
      @click="$emit('choose', i)"
    >
      <div class="icon">{{ opt.icon }}</div>
      <div class="label">{{ opt.label }}</div>
      <div class="desc">{{ opt.description }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({ state: Object })
defineEmits(['choose'])
</script>
