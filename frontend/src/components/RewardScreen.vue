<template>
  <div class="panel">
    <h2 style="color:var(--gold);">🎁 战斗奖励</h2>
    <p style="color:var(--gray);margin:8px 0;">选择一张卡牌加入你的牌组（点击空白处跳过）：</p>
    <div class="card-grid">
      <div
        v-for="(card, i) in state.rewardCards"
        :key="i"
        :class="['card', 'card-' + card.type]"
        @click="$emit('choose', i)"
      >
        <div class="card-cost">{{ card.costDisplay }}</div>
        <div class="card-name">{{ card.name }}</div>
        <div class="card-desc">{{ card.description }}</div>
        <div class="card-type-badge">{{ card.type === 'attack' ? '攻击' : card.type === 'skill' ? '技能' : '能力' }}</div>
      </div>
    </div>
    <button class="btn btn-outline" style="margin-top:14px;width:100%;" @click="$emit('choose', -1)">
      跳过（不选卡）
    </button>
  </div>
</template>

<script setup>
defineProps({ state: Object })
defineEmits(['choose'])
</script>
