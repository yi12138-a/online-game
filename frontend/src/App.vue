<template>
  <div>
    <!-- Top bar -->
    <div class="topbar" v-if="state.phase && state.phase !== 'welcome'">
      <div>
        <span class="stat">❤️ <span>{{ state.hp }}</span>/{{ state.maxHP }}</span>
        <span class="stat">🪙 <span>{{ state.gold }}</span></span>
        <span class="stat">💪 <span>{{ state.strength || 0 }}</span></span>
        <span class="stat">🛡️ <span>{{ state.block || 0 }}</span></span>
      </div>
      <div>
        <span class="stat">第 <span>{{ state.act }}</span> 幕 第 <span>{{ state.floor }}</span> 层</span>
      </div>
    </div>

    <!-- Screens -->
    <WelcomeScreen v-if="state.phase === 'welcome' || !state.phase" @start="onStart" />
    <MapScreen v-else-if="state.phase === 'map'" :state="state" @choose="onChooseRoom" />
    <BattleScreen v-else-if="state.phase === 'battle'" :state="state" @play="onPlayCard" @end="onEndTurn" />
    <RewardScreen v-else-if="state.phase === 'reward'" :state="state" @choose="onChooseReward" />
    <RestScreen v-else-if="state.phase === 'rest'" :state="state" @rest="onRest" />
    <ShopScreen v-else-if="state.phase === 'shop'" :state="state" @buy="onBuyCard" @buy-relic="onBuyRelic" @leave="onLeaveShop" />
    <GameOverScreen v-else-if="state.phase === 'gameover' || state.phase === 'victory'"
      :state="state" @restart="onRestart" />
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import * as api from './api/game.js'
import WelcomeScreen from './components/WelcomeScreen.vue'
import MapScreen from './components/MapScreen.vue'
import BattleScreen from './components/BattleScreen.vue'
import RewardScreen from './components/RewardScreen.vue'
import RestScreen from './components/RestScreen.vue'
import ShopScreen from './components/ShopScreen.vue'
import GameOverScreen from './components/GameOverScreen.vue'

const state = reactive({
  phase: 'welcome',
  hp: 80, maxHP: 80, gold: 0, floor: 1, act: 1,
  energy: 3, maxEnergy: 3, block: 0,
  strength: 0, dexterity: 0, vulnerable: 0, weak: 0,
  hand: [], drawSize: 0, discardSize: 0, deckSize: 0,
  enemy: null, enemyBlock: 0, enemyVulnerable: 0, enemyWeak: 0, enemyIntent: '',
  battleLog: [], battleOver: false, playerWin: false,
  mapOptions: [], rewardCards: [],
  shopCards: [], shopRelicName: '', shopRelicDesc: '',
  shopCardPrice: 50, shopRelicPrice: 150,
  relicNames: [], relicDescs: [],
  message: ''
})

function apply(d) { Object.assign(state, d) }

async function onStart(name) {
  const { data } = await api.startGame(name)
  apply(data)
}

async function onChooseRoom(choice) {
  const { data } = await api.chooseRoom(choice)
  apply(data)
}

async function onPlayCard(index) {
  const { data } = await api.playCard(index)
  apply(data)
}

async function onEndTurn() {
  const { data } = await api.endTurn()
  apply(data)
}

async function onChooseReward(index) {
  const { data } = await api.chooseReward(index)
  apply(data)
}

async function onRest() {
  const { data } = await api.rest()
  apply(data)
}

async function onBuyCard(index) {
  const { data } = await api.buyCard(index)
  apply(data)
}

async function onBuyRelic() {
  const { data } = await api.buyRelic()
  apply(data)
}

async function onLeaveShop() {
  const { data } = await api.leaveShop()
  apply(data)
}

function onRestart() {
  state.phase = 'welcome'
}
</script>
