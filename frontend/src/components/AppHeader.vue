<template>
  <header>
    <div class="header__info">
      <div class="header__profile">
        <img src="@/assets/Union.svg" alt="profile" />
        <div v-if="!user" class="header__info__btns">
          <router-link :to="{ name: 'SignIn', query: { redirect: $route.fullPath } }">
            <n-button color="#fff">Войти</n-button>
          </router-link>
          <router-link :to="{ name: 'Registrantion', query: { redirect: $route.fullPath } }">
            <n-button color="#fff">Создать аккаунт</n-button>
          </router-link>
        </div>
        <div v-else>
          <span>{{ user.sub }}</span>
          <n-button color="#fff" @click="logout">
            Выйти
          </n-button>
        </div>
      </div>
      <div class="header__basket">
        <img src="@/assets/cart.svg" alt="basket" />
        <router-link :to="{name: 'BasketPage'}">
          <n-button color="#fff">Корзина</n-button>
        </router-link>
        <span>{{ cartItemCount }}</span>
      </div>
      <div class="header__main">
        <router-link :to="{name: 'MainPage'}">
          <n-button color="#fff">Главная</n-button>
        </router-link>
      </div>
    </div>
    <div class="header__menu">
      <n-button
        color="#fff"
        @click="$emit('toggle-drawer')">
        Каталог
      </n-button>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useCartStore } from '@/store/cartStore';
import { useUserStore } from '@/store/userStore';
import { useRouter } from 'vue-router';
import { NButton } from 'naive-ui';

const router = useRouter();
const cartStore = useCartStore();
const userStore = useUserStore();
const cartItemCount = ref(0);

const user = computed(() => userStore.user.value);

const logout = () => {
  userStore.logout();
  router.push("/signin");
};
watch(
  () => cartStore.cartItems,
  (newItems) => {
    cartItemCount.value = newItems.reduce(
      (total, item) => total + item.count, 0);
  },
  { immediate: true, deep: true }
);

onMounted(async () => {
  await userStore.fetchUser();
  if (user.value) {
    const token = localStorage.getItem("token");
    await cartStore.fetchCart(token);
  }
});
</script>

<style lang="scss" scoped>
.header__info{
  display: flex;
  gap: 30px;

  &__btns{
    display: flex;
    gap: 10px;
  }
}
.header__profile,
.header__basket,
.header__menu {
  display: flex;
  align-items: center;
  gap: 10px;
}
.header__profile span {
    color: #fff;
    margin-right: 10px;
}
.header__basket span {
    width: 40px;
    color: #fff;
}
.n-button {
  color: #000;
}

.n-button:hover{
  color: #465a86;
}
</style>
