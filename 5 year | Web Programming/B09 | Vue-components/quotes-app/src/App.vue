<template>
  <div class="app-wrapper">
    <nav>
      <router-link to="/">Список цитат</router-link> |
      <router-link to="/random">Випадкова цитата</router-link>
    </nav>

    <main>
      <router-view></router-view>
    </main>
  </div>
</template>

<style>
.app-wrapper {
  font-family: Arial, sans-serif;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
nav {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #ccc;
  margin-bottom: 20px;
}
nav a {
  font-weight: bold;
  color: #2c3e50;
  text-decoration: none;
  margin: 0 10px;
}
nav a.router-link-exact-active {
  color: #42b983;
}
</style>