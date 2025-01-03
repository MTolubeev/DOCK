<template>
  <div v-if="!showOrderConfirmed" class="cart">
    <div v-if="cartItems.length" class="cart__main">
      <h2>Ваша корзина</h2>
      <div class="cart__info">
        <div class="products__list">
          <BasketItem
            v-for="item in cartItems"
            :key="item.id"
            :item="item"
            @updateItem="updateCartItem"
          />
        </div>
        <div class="cart__controll">
          <span>Всего товаров: {{ totalItems }}</span>
          <span>Общая сумма: {{ totalPrice }} ₽</span>
          <n-button 
            color="#465a86" 
            class="cart__confirm" 
            @click="openConfirmation">
            Оформить заказ
          </n-button>
        </div>
      </div>
    </div>
    <div v-else class="cart__empty">
      <h2>Корзина пуста</h2>
      <img src="@/assets/corob.svg" alt="Пустая корзина" />
      <p>Войдите или зарегистрируйтесь, чтобы вы смогли добавлять товары в корзину</p>
      <router-link :to="{ name: 'SignIn', query: { redirect: $route.fullPath } }">
        <n-button color="#465a86">Авторизоваться</n-button>
      </router-link>
    </div>
  </div>

  <div v-if="showOrderConfirmed" class="order-confirmed">
    <h2>Ваш заказ оформлен</h2>
    <img src="@/assets/ready.png" alt="Заказ оформлен" />
    <p>Вся информация о заказе отправлена на ваш email.</p>
    <router-link to="/">
      <n-button color="#465a86">Вернуться на главную</n-button>
    </router-link>
  </div>

  <div v-if="showConfirmation" class="dialog-overlay">
    <n-dialog
      class="confirm-dialog"
      title="Подтверждение оформления"
      positive-text="Оформить"
      negative-text="Отмена"
      @positive-click="placeOrder"
      @negative-click="closeConfirmation"
      :closable="false">
      Оформить заказ?
    </n-dialog>
  </div>
  
  <div v-if="isLoading" class="loading-overlay">
    <n-spin 
      size="large" 
      stroke="#465a86"
      description="Оформляем ваш заказ..." />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useCartStore } from '@/store/cartStore';
import { useUserStore } from '@/store/userStore';
import BasketItem from '../components/BasketItem.vue';
import api from '@/services/api.js';
import { NButton, NDialog, NSpin } from 'naive-ui';

const cartStore = useCartStore();
const userStore = useUserStore();

const showConfirmation = ref(false);
const showOrderConfirmed = ref(false);
const isLoading = ref(false); 

const cartItems = computed(() => cartStore.cartItems);
const totalItems = computed(() => cartStore.cartItems.reduce((total, item) => total + item.count, 0));
const totalPrice = computed(() => cartStore.cartItems.reduce((total, item) => total + item.price * item.count, 0));

const updateCartItem = (updatedItem) => {
  if (updatedItem === null) {
    cartStore.cartItems = [];
  } else {
    const index = cartStore.cartItems.findIndex((item) => item.id === updatedItem.id);
    if (index !== -1) {
      cartStore.cartItems[index] = updatedItem;
    }
  }
};

const placeOrder = async () => {
  const token = localStorage.getItem('token');
  try {
    isLoading.value = true; 
    await api.post(`/email`, {}, {
      headers: {
        Authorization: token,
      },
    });
    showConfirmation.value = false;
    showOrderConfirmed.value = true;
  } catch (error) {
    console.error('Ошибка при оформлении заказа:', error.message);
  } finally {
    isLoading.value = false; 
  }
};

const openConfirmation = () => {
  showConfirmation.value = true;
};

const closeConfirmation = () => {
  showConfirmation.value = false;
};

onMounted(async () => {
  try {
    const token = localStorage.getItem("token");
    if(!userStore.value || !token){
      await userStore.fetchUser();
      await cartStore.fetchCart(token);
    }
  } catch (err) {
    console.error("Ошибка загрузки корзины пользователя", err);
  }
});
</script>

<style lang="scss" scoped>
.cart {
  margin-top: 100px;

  &__main{
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  &__confirm{
    margin-top: auto;
  }

  &__controll {
    background-color: #fff;
    width: 600px;
    height: 400px;
    padding: 20px;
    display: flex;
    flex-direction: column;

    & span {
      font-size: 30px;
    }
  }

  &__info {
    display: flex;
    justify-content: center;
    gap: 200px;
  }

  &__empty {
    display: flex;
    height: 100vh;
    align-items: center;
    flex-direction: column;
    justify-content: center;
    gap: 20px;
  }

  .order-confirmed {
    display: flex;
    height: 100vh;
    align-items: center;
    flex-direction: column;
    justify-content: center;
    gap: 20px;

    & img {
      width: 150px;
    }
  }
  .confirm-dialog {
    z-index: 100;
  }

  .modal-content {
    text-align: center;
  }

  .modal-actions {
    margin-top: 20px;
  }

  p {
    text-align: center;
    width: 350px;
    font-size: 16px;
  }

  h2 {
    text-align: center;
    color: #465a86;
    font-size: 28px;
  }

  .dialog-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 99;
  }
  .loading-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(255, 255, 255, 0.8);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
  }
}
.n-card {
  width: 600px;
}
</style>
