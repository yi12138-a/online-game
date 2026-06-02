import axios from 'axios'

const api = axios.create({ baseURL: '/api/game', timeout: 15000 })

export function startGame(name) { return api.post('/start', { name }) }
export function getState() { return api.get('/state') }
export function chooseRoom(choice) { return api.post('/choose-room', { choice }) }
export function playCard(index) { return api.post('/play-card', { index }) }
export function endTurn() { return api.post('/end-turn') }
export function chooseReward(index) { return api.post('/choose-reward', { index }) }
export function rest() { return api.post('/rest') }
export function buyCard(index) { return api.post('/buy-card', { index }) }
export function buyRelic() { return api.post('/buy-relic') }
export function leaveShop() { return api.post('/leave-shop') }
