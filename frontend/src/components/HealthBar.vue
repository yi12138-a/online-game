<template>
  <div class="hp-bar-wrap">
    <div class="hp-header">
      <span>{{ label || 'HP' }}</span>
      <span>{{ hp }} / {{ maxHP }}</span>
    </div>
    <div class="hp-bar">
      <div class="fill" :style="{ width: pct + '%', background: barColor }"></div>
      <div class="label">{{ pct }}%</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  hp: Number, maxHP: Number, color: { type: String, default: '#2ecc71' }, label: String
})
const pct = computed(() => props.maxHP > 0 ? Math.round(props.hp / props.maxHP * 100) : 0)
const barColor = computed(() => {
  if (pct.value > 60) return props.color || '#2ecc71'
  if (pct.value > 30) return '#f39c12'
  return '#e74c3c'
})
</script>
