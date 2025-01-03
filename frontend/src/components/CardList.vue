<template>
  <h2>Все товары</h2>
  <div class="card_and_filters">
    <div class="filters">
      <FilteredProducts
        @update:selectedPhones="updateSelectedPhones"
        @update:priceRange="updatePriceRange"
        @resetFilters="resetAllFilters"
      />
    </div>
    <div class="cards" :class="{ 'single-item': filteredItems.length === 1 }">
      <template v-if="filteredItems.length > 0">
        <CardItem
          v-for="item in filteredItems"
          :key="item.id"
          :item="item"
          :category-options="categoryOptions"
          :subcategory-options="subcategoryOptions"
          :subsubcategory-options="subsubcategoryOptions"
          :is-editable="true"
          @delete="handleDelete"
          class="card-item"
        />
      </template>
      <template v-else>
        <div class="no-items">Нет таких товаров</div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits, computed, defineProps } from 'vue';
import CardItem from './CardItem.vue';
import FilteredProducts from '@/components/FilteredProducts.vue';
import api from '@/services/api.js';

const emit = defineEmits(["products-loaded"]);
const props = defineProps({
  searhQuery: String,
});

const categoryOptions = ref([]);
const subcategoryOptions = ref([]);
const subsubcategoryOptions = ref([]);

const selectedPhones = ref([]);
const priceRange = ref([0, Infinity]);

const updateSelectedPhones = (phones) => {
  selectedPhones.value = phones;
};

const resetAllFilters = () => {
  selectedPhones.value = [];
};

const items = ref([]);

const updatePriceRange = (range) => {
  priceRange.value = range;
};

const fetchItems = async () => {
  try {
    const response = await api.get(`/product/getAll`);

    items.value = response.data.map((product) => {
      return {
        ...product,
        imageUrl: `data:image/png;base64,${product.base64Image}`,
      };
    });
    emit("products-loaded");
  } catch (err) {
    console.error(err);
  }
};
const filteredItems = computed(() => {
  return items.value.filter((item) => {
    const matchesSearch = !props.searhQuery || item.title.toLowerCase().includes(props.searhQuery.toLowerCase());
    const matchesPhones =
      selectedPhones.value.length === 0 ||
      selectedPhones.value.some((phone) =>
        item.title.toLowerCase().includes(phone.toLowerCase())
      );
    const minPrice = priceRange.value[0] || 0;
    const maxPrice = priceRange.value[1] || Infinity;
    const matchesPriceRange = item.price >= minPrice && item.price <= maxPrice;
    return matchesSearch && matchesPhones && matchesPriceRange;
  });
});

const fetchCategories = async () => {
  try {
    const response = await api.get(`/product/getAll`);
    const products = response.data;

    const categoriesSet = new Set();
    const subcategoriesSet = new Set();
    const subsubcategoriesSet = new Set();

    products.forEach((product) => {
      product.categories.forEach((cat) => {
        categoriesSet.add(cat.name);
        if (cat.subcategory) subcategoriesSet.add(cat.subcategory);
        if (cat.subsubcategory) subsubcategoriesSet.add(cat.subsubcategory);
      });
    });

    categoryOptions.value = Array.from(categoriesSet).map((cat) => ({
      label: cat,
      value: cat,
    }));
    subcategoryOptions.value = Array.from(subcategoriesSet).map((subcat) => ({
      label: subcat,
      value: subcat,
    }));
    subsubcategoryOptions.value = Array.from(subsubcategoriesSet).map(
      (subsubcat) => ({ label: subsubcat, value: subsubcat })
    );
  } catch (error) {
    console.error("Ошибка при загрузке категорий", error);
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

<style lang="scss" scoped>
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
  

  &.single-item {
    position: relative;
    left: 16%;

    .card-item {
      max-width: 300px;
    }
  }
}
.card_and_filters {
  display: flex;
  gap: 40px;

  .filters {
    margin: 20px 0px 0px 20px;
    position: sticky;
    top: 80px;
    align-self: flex-start;
  }
}
.no-items {
  text-align: center;
  font-size: 1.5em;
  color: #777;
  margin-top: 20px;
}
</style>
