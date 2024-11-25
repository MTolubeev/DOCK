<template>
  <n-drawer 
    v-model:show="localVisible" 
    :width="384" 
    :placement="'right'" 
    @update:show="handleClose">
    <n-drawer-content>
      <div class="catalog">
        <div class="catalog__basket">
          <h2>Каталог товаров</h2>
          <svg @click="closeDrawer" width="16" height="14" fill="#fff">
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
          @close-drawer="closeDrawer"
        />
        <div class="catalog__btns">
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
    </n-drawer-content>
  </n-drawer>
</template>

<script setup>
import { ref, watch, computed, onMounted, defineProps, defineEmits } from "vue";
import DraggableCatalog from './DraggableCatalog.vue';
import { NButton, NDrawer, NDrawerContent } from "naive-ui";
import { useUserStore } from "@/store/userStore";
import { useOrganizeProducts } from "@/composables/useOrganizeProducts";
import api from '@/services/api.js';

const { organizeProductsByCategories } = useOrganizeProducts();

const props = defineProps({
  isVisible: Boolean,
});

const emit = defineEmits(['close-drawer']);

const localVisible = ref(false);
const userStore = useUserStore();
const categories = ref([]);
const editMode = ref(false);

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
    const response = await api.get("/product/getAll");
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

  const processProduct = (params) => {
    const { product, category, subcategory = null, subsubcategory = null, categoryIndex, subcategoryIndex = null, subsubcategoryIndex = null, productIndex } = params;

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
      const params = { product, category, categoryIndex, productIndex };
      processProduct(params);
    });

    category.subcategories.forEach((subcategory, subcategoryIndex) => {
      subcategory.products.forEach((product, productIndex) => {
        const params = { product, category, subcategory, categoryIndex, subcategoryIndex, productIndex };
        processProduct(params);
      });

      subcategory.subsubcategories.forEach((subsubcategory, subsubcategoryIndex) => {
        subsubcategory.products.forEach((product, productIndex) => {
          const params = { product, category, subcategory, subsubcategory, categoryIndex, subcategoryIndex, subsubcategoryIndex, productIndex };
          processProduct(params);
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
      api.put("/product/categories/reorder", {
        [productId]: changes[productId],
      })
    );
    await Promise.all(requests);

    editMode.value = false;
  } catch (error) {
    console.error("Ошибка при сохранении порядка:", error);
  }
};

const closeDrawer = () => {
  emit('close-drawer');
};

const handleClose = (value) => {
  if (!value) {
    closeDrawer();
  }
};

watch(
  () => props.isVisible,
  (newVal) => {
    localVisible.value = newVal;
  },
  { immediate: true }
);

onMounted(() => {
  userStore.fetchUser();
  fetchData();
});
</script>


<style lang="scss" scoped>
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

  & h2{
    color: white;
  }
  &__btns{
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
    &__basket {
    display: flex;
    align-items: center;
    justify-content: space-between;
    svg {
      cursor: pointer;
    }
  }
}

</style>