<template>
  <div class="welcome">
    <h1>🗡️ 尖塔卡牌</h1>
    <p>一款卡牌肉鸽游戏，灵感来自《杀戮尖塔》<br>
    构建你的卡组，击败敌人，攀登高塔！</p>
    <input v-model="name" placeholder="输入你的名字（可选）" maxlength="10" @keyup.enter="start" />
    <br>
    <button class="btn btn-gold btn-large" @click="start">⚔️ 开始攀登</button>
    <div style="margin-top:24px;font-size:12px;color:#666;">
      开局拥有 4×打击 4×防御 1×猛击 | 遗物：燃烧之血
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const emit = defineEmits(['start'])
const name = ref('')
function start() { emit('start', name.value.trim() || '冒险者') }
</script>
