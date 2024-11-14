<template>
  <n-button 
    color="#465a86" 
    :style="buttonStyle" 
    @click="toggleCart">
    {{ buttonText }}
  </n-button>
</template>

<script setup>
import { defineProps, ref, computed, onMounted } from 'vue';
import { useCartStore } from '@/store/cartStore';
import { useUserStore } from "@/store/userStore";
import { NButton } from 'naive-ui';
import { useRouter } from 'vue-router';
import { useNotificationService } from '@/composables/useNotifications.js'; 

const { showNotificationMessage } = useNotificationService();

const cartStore = useCartStore();
const userStore = useUserStore();
const inCart = ref(false);

const props = defineProps({
  productId: {
    type: Number,
    required: true,
  },
  product: {
    type: Object,
    required: true,
  },
});

const user = computed(() => userStore.user.value);

const buttonText = computed(() =>
  inCart.value ? "Перейти в корзину" : "Добавить в корзину"
);

const buttonStyle = computed(() => ({
  backgroundColor: inCart.value ? "#f0f0f0" : "#3B5998",
  color: inCart.value ? "#000" : "#fff",
}));

const updateInCartStatus = async () => {
  const token = localStorage.getItem("token");
    if(token === null || user.value === null){
      inCart.value = false;
      return;
    }
    try {
      await userStore.fetchUser();
      const userId = user.value?.id;
      if (userId) {
        const cart = await cartStore.fetchCart(token);
        inCart.value = cart.some((item) => item.id === props.productId);
      } 
    } catch (error) {
      console.error("Ошибка получения корзины", error);
  }
};

const router = useRouter();

const toggleCart = async () => {
  try {
    const token = localStorage.getItem("token");

    if (!token || !user.value) {
      return router.push("/signin");
    }
    if (props.product.count <= 0) {
      showNotificationMessage('error', 'Ошибка', 'Товара нет на складе!');
      return;
    }
    if (!inCart.value) {
      await cartStore.addToCart(props.productId, token);
      inCart.value = true;
    } else {
      router.push("/cart");
    }
    await updateInCartStatus();
  } catch (error) {
    console.error(error);
  }
};

onMounted(async () => {
  await updateInCartStatus();
});
</script>
