<template>
  <n-spin 
    content-style="--n-opacity-spinning:0; height: 100vh;" 
    stroke="blue" 
    :show="isLoading">
    <AppHeader @toggle-drawer="toggleDrawer" />
    <AppDrawer :is-visible="isDrawerVisible" @close-drawer="closeDrawer" />
    <div class="categories__page">
    <div class="path">
      <h1>
        <router-link
          class="path__link"
          :to="{ name: 'CategoriesView', params: { categoryName: categoryName } }">
          {{ categoryName }}
        </router-link>
        /
        <router-link
          v-if="subcategoryName"
          class="path__link"
          :to="{ name: 'CategoriesView', params: { categoryName: categoryName, subcategoryName: subcategoryName } }">
          {{ subcategoryName }}
        </router-link>
        <span v-if="subsubcategoryName">
          /
          <router-link
            class="path__link"
            :to="{ name: 'CategoriesView', params: { categoryName: categoryName, subcategoryName: subcategoryName, subsubcategoryName: subsubcategoryName } }">
            {{ subsubcategoryName }}
          </router-link>
        </span>
      </h1>
    </div>
    <div class="cards">
      <CardItem
        v-for="product in filteredProducts"
        :key="product.id"
        :item="product"
        :is-editable="false"
        @delete ="handleDelete"
        />
    </div>
  </div>
  </n-spin>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useOrganizeProducts } from '@/composables/useOrganizeProducts.js';
import CardItem from '@/components/CardItem.vue';
import { useDrawer } from '@/composables/useHeader.js';
import AppHeader from '@/components/AppHeader.vue';
import AppDrawer from '@/components/AppDrawer.vue';
import api from '@/services/api.js';
import { NSpin } from 'naive-ui'; 

const { isDrawerVisible, toggleDrawer, closeDrawer } = useDrawer();
const route = useRoute();
const categories = ref([]);
const isLoading = ref(true); 
const categoryName = ref(route.params.categoryName);
const subcategoryName = ref(route.params.subcategoryName || '');
const subsubcategoryName = ref(route.params.subsubcategoryName || '');
const { organizeProductsByCategories } = useOrganizeProducts();

const filteredProducts = computed(() => {
  const category = categories.value.find(cat => cat.name === categoryName.value);
  if (!category) return [];
  
  if (!subcategoryName.value) {
    return [
      ...category.productsWithoutSubcategory,
      ...category.subcategories.flatMap(sub => [
        ...sub.products,
        ...sub.subsubcategories.flatMap(subsub => subsub.products),
      ]),
    ];
  }

  const subcategory = category.subcategories.find(sub => sub.name === subcategoryName.value);
  if (!subcategory) return [];
  if (!subsubcategoryName.value) {
    return [
      ...subcategory.products,
      ...subcategory.subsubcategories.flatMap(subsub => subsub.products),
    ];
  }

  const subsubcategory = subcategory.subsubcategories.find(subsub => subsub.name === subsubcategoryName.value);
  return subsubcategory ? subsubcategory.products : [];
});

const fetchData = async () => {
  isLoading.value = true; 
  try {
    const response = await api.get(`/product/getAll`);
    const products = response.data;

    if (Array.isArray(products)) {
      products.forEach((product) => {
        product.imageUrl = `data:image/png;base64,${product.base64Image}`;
      });
      categories.value = organizeProductsByCategories(products);
    } else {
      console.error('Неправильный формат данных:', products);
    }
  } catch (error) {
    console.error('Ошибка при получении данных:', error);
  } finally {
    isLoading.value = false; 
  }
};
const handleDelete = () => {
  window.location.reload();
}
watch(
  () => route.params,
  (newParams) => {
    categoryName.value = newParams.categoryName;
    subcategoryName.value = newParams.subcategoryName || '';
    subsubcategoryName.value = newParams.subsubcategoryName || '';
    fetchData();
    closeDrawer();
  },
  { immediate: true }
);

onMounted(() => {
  fetchData();
});
</script>

<style lang="scss" scoped>
.categories__page {
  margin-top: 5%;

  .path {
    margin: 10px 20px;

    &__link {
      text-decoration: none;
      color: inherit;

      &:hover {
        color: gray;
      }
    }
  }

  .cards {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 20px;
  }
}
</style>
