<template>
  <n-spin
    content-style="--n-opacity-spinning:0; height: 100vh;"
    stroke="#465a86"
    :show="loading"
  >
    <AppHeader @toggle-drawer="toggleDrawer" />
    <AppDrawer :is-visible="isDrawerVisible" @close-drawer="closeDrawer" />
    <n-button
      v-if="isAdmin"
      class="button__add"
      color="#465a86"
      @click="openModal"
    >
      Добавить новый товар
    </n-button>
    <AddProduct v-if="showModal" @close="closeModal" />
    <n-input
      content-style=""
      v-model:value="searhQuery"
      placeholder="Поиск товаров"
    />
    <CardList :searh-query="searhQuery" @products-loaded="onProductsLoaded" />
    <div class="all_comments">
      <h2>Отзывы наших пользователей</h2>
      <n-card
        v-for="comment in limitedComments"
        :key="comment.id"
        content-style="display:flex; justify-content:space-around;"
      >
        <div>
          <h3>{{ comment.username }}</h3>
          <span>Оценка за товар: {{ comment.score }}</span>
          <p>Отзыв: {{ comment.text }}</p>
        </div>
        <div>
          <router-link
            :to="{
              name: 'ProductItem',
              params: { productId: comment.productId },
            }"
            class="name_product"
          >
            {{ comment.productTitle }}
          </router-link>
        </div>
      </n-card>
    </div>
  </n-spin>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { NButton, NCard, NSpin, NInput } from "naive-ui";
import axios from "axios";
import AppHeader from "@/components/AppHeader.vue";
import AppDrawer from "@/components/AppDrawer.vue";
import AddProduct from "@/components/AddProduct.vue";
import CardList from "@/components/CardList.vue";
import { useUserStore } from "@/store/userStore";
import { useDrawer } from "@/composables/useHeader.js";

const userStore = useUserStore();
const { isDrawerVisible, toggleDrawer, closeDrawer } = useDrawer();

const productsLoaded = ref(false);
const commentsLoaded = ref(false);
const showModal = ref(false);
const comments = ref([]);
const searhQuery = ref("");

const loading = computed(() => !productsLoaded.value || !commentsLoaded.value);
const role = computed(() => userStore.role.value);
const isAdmin = computed(() => role.value === "ROLE_ADMIN");
const limitedComments = computed(() => {
  return comments.value.filter((comment) => comment.score >= 4).slice(0, 5);
});

const openModal = () => {
  showModal.value = true;
  document.body.style.overflow = "hidden";
};

const closeModal = () => {
  showModal.value = false;
  document.body.style.overflow = "";
};

const getAllComments = async () => {
  try {
    const response = await axios.get(
      `http://localhost:8080/comments/getAllComments`
    );
    comments.value = response.data;
    commentsLoaded.value = true;
  } catch (error) {
    console.error(error);
  }
};

const onProductsLoaded = () => {
  productsLoaded.value = true;
  if (commentsLoaded.value) {
    loading.value = false;
  }
};

onMounted(async () => {
  await userStore.fetchUser();
  await getAllComments();
});
</script>

<style scoped>
.button__add {
  position: relative;
  left: 10%;
  top: 60px;
}
.all_comments {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background-color: #465a86;
}
.n-card {
  width: 700px;
  display: flex;
  flex-direction: column;
}
h2 {
  color: #fff;
}
.name_product {
  color: #465a86;
  cursor: pointer;
  text-decoration: none;
  position: relative;
  display: inline-block;
}

.name_product::after {
  content: "";
  position: absolute;
  left: 0;
  bottom: -2px;
  width: 0;
  height: 2px;
  background-color: #465a86;
  transition: width 0.3s ease;
}

.name_product:hover::after {
  width: 100%;
}
.n-input {
  width: 300px;
  position: relative;
  top: 60px;
  left: 60%;
}
</style>
