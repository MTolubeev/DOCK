<template>
  <div v-if="isVisible" class="catalog">
    <div class="basket">
      <h2>Каталог товаров</h2>
      <svg @click="$emit('close-drawer')" width="16" height="14" fill="#fff">
        <path d="M1 7H14.7143" stroke="#fff" stroke-width="2" />
        <path d="M8.71436 1L14.7144 7L8.71436 13" stroke="#fff" stroke-width="2" />
      </svg>
    </div>
    <n-button 
      v-if="!editMode && isAdmin" 
      type="warning" 
      size="small" 
      @click="enableEditMode">
      Включить режим редактирования
    </n-button>
    <DraggableCatalog
      :categories="categories"
      :edit-mode="editMode"
      @drag-end="onDragEnd"
    />
    <div class="change_order_btns">
      <n-button 
        v-if="editMode" 
        type="warning" 
        @click="saveOrder">
        Сохранить изменения
      </n-button>
      <n-button 
        v-if="editMode" 
        type="error" 
        @click="cancelEditMode">
        Отменить изменения
      </n-button>
  </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, defineProps } from "vue";
import axios from "axios";
import DraggableCatalog from './DraggableCatalog.vue';
import { NButton } from "naive-ui";
import { useUserStore } from "@/store/userStore";
import { useOrganizeProducts } from "@/composables/useOrganizeProducts";

defineProps({
  isVisible: Boolean,
});

const userStore = useUserStore();
const categories = ref([]);
const editMode = ref(false);
const { organizeProductsByCategories } = useOrganizeProducts();

const role = computed(() => userStore.role.value);
const isAdmin = computed(() => role.value === "ROLE_ADMIN");

const enableEditMode = () => {
  editMode.value = true;
};

const cancelEditMode = () => {
  editMode.value = false;
};

const fetchData = async () => {
  try {
    const response = await axios.get("http://localhost:8080/product/getAll");
    const products = response.data;
    if (Array.isArray(products)) {
      categories.value = organizeProductsByCategories(products);
    } else {
      console.error("Неправильный формат данных:", products);
    }
  } catch (error) {
    console.error("Ошибка при получении данных:", error);
  }
};

const onDragEnd = (newCategories) => {
  categories.value = newCategories;
};

const collectChanges = () => {
  let changes = {};

  const processProduct = (product, category, subcategory = null, subsubcategory = null, categoryIndex, subcategoryIndex = null, subsubcategoryIndex = null, productIndex) => {
    if (!changes[product.id]) {
      changes[product.id] = [];
    }
    changes[product.id].push({
      name: category.name,
      subcategory: subcategory ? subcategory.name : null,
      subsubcategory: subsubcategory ? subsubcategory.name : null,
      order: categoryIndex + 1,
      subcategoryOrder: subcategoryIndex !== null ? subcategoryIndex + 1 : null,
      subsubcategoryOrder: subsubcategoryIndex !== null ? subsubcategoryIndex + 1 : null,
      productOrder: productIndex + 1,
    });
  };

  categories.value.forEach((category, categoryIndex) => {
    category.productsWithoutSubcategory.forEach((product, productIndex) => {
      processProduct(product, category, null, null, categoryIndex, null, null, productIndex);
    });

    category.subcategories.forEach((subcategory, subcategoryIndex) => {
      subcategory.products.forEach((product, productIndex) => {
        processProduct(product, category, subcategory, null, categoryIndex, subcategoryIndex, null, productIndex);
      });

      subcategory.subsubcategories.forEach((subsubcategory, subsubcategoryIndex) => {
        subsubcategory.products.forEach((product, productIndex) => {
          processProduct(product, category, subcategory, subsubcategory, categoryIndex, subcategoryIndex, subsubcategoryIndex, productIndex);
        });
      });
    });
  });
  return changes;
};
const saveOrder = async () => {
  try {
    const changes = collectChanges();
    const requests = Object.keys(changes).map((productId) =>
      axios.put("http://localhost:8080/product/categories/reorder", {
        [productId]: changes[productId],
      })
    );
    await Promise.all(requests);

    editMode.value = false;
  } catch (error) {
    console.error("Ошибка при сохранении порядка:", error);
  }
};

onMounted(() => {
  userStore.fetchUser();
  fetchData();
});
</script>

<style scoped>
.catalog {
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100%;
  width: 384px;
  background-color: #465a86;
  padding: 28px 40px;
  top: 0;
  right: 0;
  z-index: 120;
  overflow-y: scroll;
}
.catalog a {
  color: #fff;
}

h2,
span {
  font-family: Arial, Helvetica, sans-serif;
  color: #fff;
}

.basket {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

svg {
  cursor: pointer;
}
.change_order_btns{
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>