<template>
  <div id="app">
    <div>
      <input v-model="newItemName" placeholder="Назва товару" />
      <input v-model.number="newItemPrice" type="number" placeholder="Ціна" />
      <button @click="addItem">Додати</button>
    </div>

    <hr />

    <div>
      <button @click="filter = 'all'">Усі</button>
      <button @click="filter = 'bought'">Куплені</button>
      <button @click="filter = 'not-bought'">Не куплені</button>
    </div>

    <div v-if="filteredList.length === 0">
      <p v-if="items.length === 0">Список порожній</p>
      <p v-else>Немає покупок для відображення у цій категорії</p>
    </div>

    <ul v-else>
      <li v-for="(item, index) in filteredList" :key="index">
        <span :class="{ 'bought-style': item.bought }">
          {{ item.name }} ({{ item.price }} грн) — {{ item.date }}
        </span>

        <button @click="item.bought = !item.bought">
          {{ item.bought ? 'Не куплено' : 'Куплено' }}
        </button>

        <button @click="deleteItem(item)">Видалити</button>
      </li>
    </ul>

    <hr />

    <h3>Загальна сума куплених: {{ totalSum }} грн</h3>
  </div>
</template>

<script>
export default {
  data() {
    return {
      newItemName: '',
      newItemPrice: null,
      filter: 'all',
      items: []
    };
  },
  computed: {
    filteredList() {
      if (this.filter === 'bought') return this.items.filter(i => i.bought);
      if (this.filter === 'not-bought') return this.items.filter(i => !i.bought);
      return this.items;
    },
    totalSum() {
      return this.items
        .filter(item => item.bought)
        .reduce((sum, item) => sum + (item.price || 0), 0);
    }
  },
  methods: {
    addItem() {
      if (this.newItemName.trim() === '') return;

      const newItem = {
        name: this.newItemName,
        bought: false,
        price: this.newItemPrice || 0,
        date: new Date().toLocaleString()
      };

      this.items.push(newItem);

      this.newItemName = '';
      this.newItemPrice = null;
    },
    deleteItem(item) {
      const index = this.items.indexOf(item);
      if (index > -1) {
        this.items.splice(index, 1);
      }
    }
  }
};
</script>

<style>
.bought-style {
  text-decoration: line-through;
  color: gray;
}
</style>