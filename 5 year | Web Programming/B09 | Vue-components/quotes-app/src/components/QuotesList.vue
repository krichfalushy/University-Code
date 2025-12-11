<template>
  <div class="container">
    <h2>Список цитат</h2>

    <div v-if="loading">Завантаження...</div>

    <div v-else class="quotes-grid">
      <div v-for="quote in quotes" :key="quote.id" class="quote-card">
        <p class="quote-text">"{{ quote.body }}"</p>
        <p class="quote-author">— {{ quote.author }}</p>
      </div>
    </div>

    <div class="pagination">
      <button @click="page--" :disabled="page <= 1">Назад</button>
      <span>Сторінка {{ page }}</span>
      <button @click="page++">Вперед</button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import apiClient from '../api';

const quotes = ref([]);
const page = ref(1);
const loading = ref(false);

const fetchQuotes = async () => {
  loading.value = true;
  try {
    const response = await apiClient.get('/quotes', {
      params: { page: page.value }
    });
    quotes.value = response.data.quotes;
  } catch (error) {
    console.error("Помилка:", error);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchQuotes);

// Слідкуємо за зміною сторінки, щоб оновити дані
watch(page, fetchQuotes);
</script>

<style scoped>
.quote-card {
  border: 1px solid #ddd;
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 8px;
  background-color: #f9f9f9;
}
.quote-author {
  font-weight: bold;
  text-align: right;
  color: #555;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 15px;
}
</style>
