<template>
  <div>
    <div class="section-title" style="margin-top: 14px;">选择行动</div>
    <button
      class="btn-skill"
      :disabled="busy"
      @click="$emit('action', 1)"
    >
      <div class="skill-name">⚔️ 普通攻击</div>
      <div class="skill-desc">对敌人造成攻击力-防御力的伤害</div>
    </button>
    <button
      class="btn-skill"
      :disabled="busy || playerHP <= 10"
      @click="$emit('action', 2)"
    >
      <div class="skill-name">💥 强力一击</div>
      <div class="skill-desc">消耗10HP，造成180%伤害</div>
    </button>
    <button
      class="btn-skill"
      :disabled="busy || playerHP <= 10"
      @click="$emit('action', 3)"
    >
      <div class="skill-name">💖 生命汲取</div>
      <div class="skill-desc">消耗10HP，随机恢复0-20点生命值</div>
    </button>
  </div>
</template>

<script setup>
defineProps({
  busy: Boolean,
  playerHP: Number
})

defineEmits(['action'])
</script>
