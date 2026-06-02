<template>
  <div class="battle-log" ref="logRef">
    <div
      v-for="(entry, idx) in logs"
      :key="idx"
      :class="['log-entry', entry.type, 'fade-in']"
    >
      {{ entry.text }}
    </div>
    <div v-if="logs.length === 0" style="color: #555; text-align: center;">
      战斗日志将在这里显示...
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'

const props = defineProps({
  logs: {
    type: Array,
    default: () => []
  }
})

const logRef = ref(null)

watch(
  () => props.logs.length,
  async () => {
    await nextTick()
    if (logRef.value) {
      logRef.value.scrollTop = logRef.value.scrollHeight
    }
  }
)
</script>
