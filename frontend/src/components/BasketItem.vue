<template>
  <n-card content-style="display: flex; flex-direction:column; gap:20px">
    <h2>{{ item.title }}</h2>
    <img
      v-if="item.imageUrl"
      :src="item.imageUrl"
      width="200px"
      class="product-image"
      alt="Product Image"
      />
    <div class="controls">
      <n-button @click="decrementCount">-</n-button>
      <span>{{ localCount }}</span>
      <n-button @click="incrementCount">+</n-button>
    </div>
    <span class="item__price">
      <strong>{{ item.price }} руб.</strong>
    </span>
    <n-button type="error" @click="removeItem">Удалить</n-button>
  </n-card>
</template>

<script setup>
import { defineProps, ref, defineEmits, watch, computed, onMounted } from 'vue';
import { useCartStore } from '@/store/cartStore';
import { useUserStore } from '@/store/userStore';
import { NCard, NButton } from 'naive-ui';

const props = defineProps({
  item: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['updateItem']);

const cartStore = useCartStore();
const userStore = useUserStore();
const token = localStorage.getItem("token");
const localCount = ref(props.item.count);

const user = computed(() => userStore.user.value);

const incrementCount = async () => {
  if (localCount.value >= 0) {
    localCount.value++;
    await cartStore.addToCart(props.item.id, token);
    emit('updateItem', { ...props.item, count: localCount.value });
  }
};

const decrementCount = async () => {
  if (localCount.value > 0) {
    localCount.value--;
    await cartStore.removeFromCart(props.item.id, token);
    emit('updateItem', { ...props.item, count: localCount.value });
  }
};

const removeItem = async () => {
  if (user.value && token) {
    await cartStore.removeFromCartAll(props.item.id, token);
    await cartStore.fetchCart(user.value.id, token);
  }
};

watch(() => props.item,
  (newItem) => {
    localCount.value = newItem.count;
  },
  { immediate: true }
);

onMounted(async () => {
  if (!user.value) {
    await userStore.fetchUser();
  }
  await cartStore.fetchCart(user.value.id, token);
});
</script>

<style scoped>
.controls {
  display: flex;
  align-items: center;
}
.controls button {
  width: 40px;
  height: 40px;
  margin: 0 5px;
  text-align: center;
  font-size: 18px;
  cursor: pointer;
}

.controls span {
  font-size: 18px;
  width: 40px;
  text-align: center;
  font-family: "Arial", sans-serif;
}
.item__price {
  font-size: 20px;
}
</style>
