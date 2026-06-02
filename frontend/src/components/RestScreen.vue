<template>
  <div class="panel" style="text-align:center;">
    <div style="font-size:48px;margin:16px 0;">🏕️</div>
    <h2 style="color:var(--gold);">休息处</h2>
    <p style="color:var(--gray);margin:10px 0;">
      你可以在这里恢复 <b style="color:var(--green);">30%</b> 最大生命值
    </p>
    <p style="color:var(--gray);margin:10px 0;">
      当前生命：<b style="color:var(--red);">{{ state.hp }}</b> / {{ state.maxHP }}
      →
      恢复后：<b style="color:var(--green);">{{ Math.min(state.maxHP, state.hp + Math.floor(state.maxHP * 0.3)) }}</b> / {{ state.maxHP }}
    </p>
    <button class="btn btn-green btn-large" style="margin:12px;" @click="$emit('rest')">
      💤 休息
    </button>
  </div>
</template>

<script setup>
defineProps({ state: Object })
defineEmits(['rest'])
</script>
