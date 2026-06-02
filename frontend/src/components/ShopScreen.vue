<template>
  <div class="panel">
    <h2 style="color:var(--gold);">🛒 商店</h2>
    <p style="color:var(--gray);">金币：<b style="color:var(--gold);">{{ state.gold }}</b> — 选择要购买的物品</p>

    <h3 style="color:var(--gold);margin-top:16px;">卡牌（每张 {{ state.shopCardPrice }} 金币）</h3>
    <div class="card-grid">
      <div
        v-for="(card, i) in state.shopCards"
        :key="'c'+i"
        :class="['card', 'card-' + card.type, { disabled: state.gold < state.shopCardPrice }]"
        @click="$emit('buy', i)"
      >
        <div class="card-cost">{{ card.costDisplay }}</div>
        <div class="card-name">{{ card.name }}</div>
        <div class="card-desc">{{ card.description }}</div>
        <div class="card-type-badge">{{ card.type === 'attack' ? '攻击' : card.type === 'skill' ? '技能' : '能力' }}</div>
      </div>
      <div v-if="!state.shopCards?.length" style="color:#555;">卡牌已售罄</div>
    </div>

    <h3 v-if="state.shopRelicName" style="color:var(--gold);margin-top:20px;">遗物（{{ state.shopRelicPrice }} 金币）</h3>
    <div v-if="state.shopRelicName" class="panel" style="background:#111;cursor:pointer;"
      :style="{ opacity: state.gold < state.shopRelicPrice ? 0.4 : 1 }"
      @click="$emit('buy-relic')">
      <b style="color:var(--purple);">{{ state.shopRelicName }}</b>
      <div style="font-size:13px;color:#aaa;">{{ state.shopRelicDesc }}</div>
    </div>

    <button class="btn btn-outline" style="margin-top:20px;width:100%;" @click="$emit('leave')">
      离开商店
    </button>
  </div>
</template>

<script setup>
defineProps({ state: Object })
defineEmits(['buy', 'buy-relic', 'leave'])
</script>
