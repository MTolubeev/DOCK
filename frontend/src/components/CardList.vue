<template>
  <h2>Все товары</h2>
  <div class="cards">
    <CardItem
      v-for="item in filteredItems"
      :key="item.id"
      :item="item"
      :category-options="categoryOptions"
      :subcategory-options="subcategoryOptions"
      :subsubcategory-options="subsubcategoryOptions"
      @delete="handleDelete"
      />
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits, computed, defineProps } from 'vue';
import axios from 'axios';
import CardItem from './CardItem.vue';

const emit = defineEmits(['products-loaded']);
const props = defineProps({
  searhQuery: String
})
const items = ref([]);
const categoryOptions = ref([]);
const subcategoryOptions = ref([]);
const subsubcategoryOptions = ref([]);

const fetchItems = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/product/getAll`);

    items.value = response.data.map((product) => {
      return {
        ...product,
        imageUrl: `data:image/png;base64,${product.base64Image}`,
      };
    });
    emit('products-loaded');
  } catch (err) {
    console.error(err);
  }
};
const filteredItems = computed(() => {
  console.log('Текущий запрос поиска:', props.searhQuery);
  console.log('Исходные элементы:', items.value);
  
  if (!props.searhQuery) {
    return items.value;
  }

  const result = items.value.filter(item => 
    item.title.toLowerCase().includes(props.searhQuery.toLowerCase())
  );

  console.log('Отфильтрованные элементы:', result);
  return result;
});
const fetchCategories = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/product/getAll`);
    const products = response.data;

    const categoriesSet = new Set();
    const subcategoriesSet = new Set();
    const subsubcategoriesSet = new Set();

    products.forEach(product => {
      product.categories.forEach(cat => {
        categoriesSet.add(cat.name);
        if (cat.subcategory) subcategoriesSet.add(cat.subcategory);
        if (cat.subsubcategory) subsubcategoriesSet.add(cat.subsubcategory);
      });
    });

    categoryOptions.value = Array.from(categoriesSet).map(cat => ({ label: cat, value: cat }));
    subcategoryOptions.value = Array.from(subcategoriesSet).map(subcat => ({ label: subcat, value: subcat }));
    subsubcategoryOptions.value = Array.from(subsubcategoriesSet).map(subsubcat => ({ label: subsubcat, value: subsubcat }));
  } catch (error) {
    console.error('Ошибка при загрузке категорий', error);
  }
};

const handleDelete = () => {
  window.location.reload();
};

onMounted(() => {
  fetchItems();
  fetchCategories();
});
</script>

<style scoped>
h2 {
  text-align: center;
  margin-top: 20px;
}
.cards {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  margin-top: 20px;
  gap: 20px;
}
</style>