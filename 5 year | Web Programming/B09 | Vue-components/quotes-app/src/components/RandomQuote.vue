<template>
  <div class="container center">
    <h2>Випадкова цитата дня</h2>

    <div v-if="quote" class="featured-quote">
      <p class="text">"{{ quote.body }}"</p>
      <p class="author">— {{ quote.author }}</p>
    </div>
    <div v-else>Завантаження...</div>

    <button @click="getRandomQuote" class="btn-random">Random Quote</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import apiClient from '../api';

const quote = ref(null);

const getRandomQuote = async () => {
  quote.value = null;
  try {
    // Запит на отримання цитати дня
    const response = await apiClient.get('/qotd');
    quote.value = response.data.quote;
  } catch (error) {
    console.error("Помилка:", error);
  }
};

onMounted(getRandomQuote);
</script>

<style scoped>
.center { text-align: center; }
.featured-quote {
  font-size: 1.2em;
  margin: 20px 0;
  padding: 20px;
  border-left: 5px solid #42b983;
  background: #f4f4f4;
}
.btn-random {
  padding: 10px 20px;
  background-color: #42b983;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 16px;
  border-radius: 4px;
}
.btn-random:hover { background-color: #3aa876; }
</style>
