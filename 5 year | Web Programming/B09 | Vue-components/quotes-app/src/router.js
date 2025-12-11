import { createRouter, createWebHistory } from 'vue-router';

import QuotesList from './components/QuotesList.vue';
import RandomQuote from './components/RandomQuote.vue';

const routes = [
  { path: '/', component: QuotesList, name: 'List' }, // Головна - список
  { path: '/random', component: RandomQuote, name: 'Random' } // Випадкова
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
