<template>
  <n-spin 
  content-style="--n-opacity-spinning:0; height: 100vh;" 
  stroke="blue" 
  :show="loader">
    <div v-if="product">
      <AppHeader @toggle-drawer="toggleDrawer" />
      <AppDrawer 
      :is-visible="isDrawerVisible" 
      @close-drawer="closeDrawer" 
      />
      <h2 class="breadcrumb">
        <router-link
            class="product__link"
            :to="{ name: 'CategoriesView', params: { categoryName: productCategory } }">
              {{ productCategory }}
        </router-link>
        /
        <span v-if="productSubcategory !== 'None'">
          <router-link
              class="product__link"
              :to="{ name: 'CategoriesView', params: { categoryName: productCategory, subcategoryName: productSubcategory } }">
                {{ productSubcategory }}
          </router-link>
          /
        </span>
        <span v-if="productSubsubcategory !== 'None'">
          <router-link
              class="product__link"
              :to="{ name: 'CategoriesView', params: { categoryName: productCategory, subcategoryName: productSubcategory, subsubcategoryName: productSubsubcategory } }">
                {{ productSubsubcategory }}
          </router-link>
          /
        </span>
        {{ product.title }}
      </h2>
      <div class="card__item">
        <n-card class="product__card" content-style="display: flex; flex-direction: row !important;">
          <div class="img__wrapper">
            <img
                v-if="product.imageUrl"
                :src="product.imageUrl"
                class="product__img"
                alt="Product Image"
                />
          </div>
          <div class="card__info">
            <h1 class="product__title">{{ product.title }}</h1>
            <p class="product__description">{{ product.description }}</p>
            <div class="card__pay">
              <span v-if="isAuthenicated">Цена: <b>{{ product.discountPrice }} руб.</b>
                  <del style="margin-left: 10px">{{ product.price }} руб.</del>
              </span>
              <span v-else>Цена: {{ product.price }}</span>
              <span v-if="product.count > 0">Количество товаров осталось: <b>{{ product.count }}</b></span>
              <span v-else><b>Товара нет на складе</b></span>
              <BasketButton 
                v-if="product" 
                :product-id="product.id" 
                :product="product" 
              />
            </div>
          </div>
        </n-card>
      </div>
      <ProductsComment 
      :comments="product.comments" 
      :product-id="product.id" 
      />
    </div>
  </n-spin>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';
import { NCard, NSpin } from 'naive-ui';
import AppHeader from '@/components/AppHeader.vue';
import AppDrawer from '@/components/AppDrawer.vue';
import { useDrawer } from '@/composables/useHeader.js';
import ProductsComment from '@/components/ProductsComment.vue';
import BasketButton from '@/components/BasketButton.vue';


const { isDrawerVisible, toggleDrawer, closeDrawer } = useDrawer();
const route = useRoute();
const product = ref(null);

const isAuthenicated = ref(false);

const loader = computed(() => !product.value);
const productCategory = computed(() => {
  return product.value?.categories?.[0]?.name || 'Unknown';
});

const productSubcategory = computed(() => {
  return product.value?.categories?.[0]?.subcategory || 'None';
});

const productSubsubcategory = computed(() => {
  return product.value?.categories?.[0]?.subsubcategory || 'None';
});

const fetchProduct = async (id) => {
  try {
    const response = await axios.get(`http://localhost:8080/product/getAll/${id}`);
    const productData = response.data;

    if (productData.base64Image) {
      productData.imageUrl = `data:image/png;base64,${productData.base64Image}`;
    }

    product.value = productData;
  } catch (error) {
    console.error('продукт не получили:', error);
  }
};

const checkAuth = () =>{
  const token = localStorage.getItem("token");
  if(token){
    isAuthenicated.value = true;
  }
}

watch(() => route.params.productId, (newId) => {
  if (newId) {
    fetchProduct(newId);
    closeDrawer();
  }
});

onMounted(() => {
  checkAuth();
  const productId = route.params.productId;
  fetchProduct(productId);
});
</script>

<style scoped>
.breadcrumb {
  margin-bottom: 20px;
}
.card__item {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}

.product__card {
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.img__wrapper {
  flex: 1;
  margin-right: 30px;
  margin-top: 10px;
}
.product__img {
  width: 350px;
  object-fit: cover;
}
.card__info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.card__pay{
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.product__title {
  font-size: 28px;
  margin-bottom: 15px;
  color: #333;
}

.product__description {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.n-button {
  align-self: flex-start;
  width: 100%;
}
.n-card {
  width: 1000px;
}
.product__link{
  text-decoration: none;
  color: inherit;
}
.product__link:hover{
  color: gray;
}

h2{
  margin: 10px 20px;
}
</style>
