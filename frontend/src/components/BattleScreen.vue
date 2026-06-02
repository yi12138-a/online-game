<template>
  <div>
    <!-- Enemy -->
    <div class="enemy-panel" v-if="state.enemyName">
      <div class="enemy-name">{{ state.enemyName }}</div>
      <HealthBar :hp="state.enemyHP" :maxHP="state.enemyMaxHP" color="#e74c3c" />
      <div style="margin-top:4px;font-size:12px;color:#aaa;">
        格挡: {{ state.enemyBlock || 0 }}
        <span v-if="state.enemyVulnerable" style="color:#e74c3c"> 易伤×{{ state.enemyVulnerable }}</span>
        <span v-if="state.enemyWeak" style="color:#f39c12"> 虚弱×{{ state.enemyWeak }}</span>
      </div>
      <div class="enemy-intent">{{ state.enemyIntent }}</div>
    </div>

    <!-- Your stats -->
    <div class="panel" style="padding:10px 16px;">
      <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;">
        <div>
          ⚡ 能量: <b style="color:var(--gold);font-size:18px;">{{ state.energy }}</b>/{{ state.maxEnergy }}
        </div>
        <div>
          格挡: <b style="color:var(--blue)">{{ state.block || 0 }}</b>
        </div>
        <div>
          🃏 抽牌堆: {{ state.drawSize }} | 弃牌堆: {{ state.discardSize }}
        </div>
        <div class="status-bar" style="margin-top:4px;">
          <span v-if="state.strength" class="status-badge buff">💪力量+{{ state.strength }}</span>
          <span v-if="state.dexterity" class="status-badge buff">🏃敏捷+{{ state.dexterity }}</span>
          <span v-if="state.ritual" class="status-badge buff">🔥仪式+{{ state.ritual }}</span>
          <span v-if="state.vulnerable" class="status-badge debuff">💔易伤×{{ state.vulnerable }}</span>
          <span v-if="state.weak" class="status-badge debuff">😵虚弱×{{ state.weak }}</span>
        </div>
      </div>
    </div>

    <!-- Hand -->
    <div class="hand-area">
      <div
        v-for="(card, i) in state.hand"
        :key="i"
        :class="['card', 'card-' + card.type, { disabled: state.energy < (card.cost === -1 ? 1 : card.cost) || state.battleOver }]"
        @click="$emit('play', i)"
      >
        <div class="card-cost">{{ card.cost === -1 ? 'X' : card.cost }}</div>
        <div class="card-name">{{ card.name }}</div>
        <div class="card-desc">{{ card.description }}</div>
        <div class="card-type-badge">{{ card.type === 'attack' ? '攻击' : card.type === 'skill' ? '技能' : '能力' }}</div>
      </div>
      <div v-if="!state.hand?.length" style="color:#555;padding:20px;">
        手中没有牌（可能已全部打出或未开始）
      </div>
    </div>

    <!-- End turn -->
    <div style="margin:12px 0;text-align:center;" v-if="!state.battleOver">
      <button class="btn btn-red btn-large" @click="$emit('end')" style="min-width:200px;">
        ⏩ 结束回合
      </button>
    </div>
    <div v-else style="text-align:center;padding:16px;color:var(--gold);">
      {{ state.playerWin ? '🎉 战斗胜利！' : '' }}
    </div>

    <!-- Battle log -->
    <div class="battle-log" ref="logRef">
      <div v-for="(msg, i) in state.battleLog" :key="i" class="entry system">{{ msg }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import HealthBar from './HealthBar.vue'

defineProps({ state: Object })
defineEmits(['play', 'end'])

const logRef = ref(null)
watch(() => state.battleLog?.length, async () => {
  await nextTick()
  if (logRef.value) logRef.value.scrollTop = logRef.value.scrollHeight
})
</script>
