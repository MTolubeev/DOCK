<template>
  <n-card
    v-if="!isEdited"
    class="product-card"
    size="huge"
    hoverable
    @click="navigateToproduct"
  >
    <div class="edit-icon-container">
      <img
        v-if="isAdmin"
        src="@/assets/pencil.svg"
        alt="edit_product"
        @click.stop="editModel"
      />
    </div>
    <div class="product-card__content">
      <div class="product-card__image">
        <img :src="item.imageUrl" alt="Изображение продукта" />
      </div>
      <h3 class="product-card__title">{{ item.title }}</h3>
      <div class="product-card__info">
        <span v-if="isAuthenticated">
          Цена: <b>{{ item.discountPrice }} руб.</b>
          <del style="margin-left: 10px">{{ item.price }} руб.</del>
        </span>
        <span v-else>
          Цена: <b>{{ item.price }} руб.</b>
        </span>
        <span v-if="item.count > 0">
          Количество товаров осталось: <b>{{ item.count }}</b>
        </span>
        <span v-else><b>Товара нет на складе</b></span>
      </div>
      <div class="product-card__buttons">
        <BasketButton :product-id="item.id" :product="item" @click.stop />
        <n-button v-if="isAdmin" type="error" @click.stop="openConfirmDialog">
          Удалить товар из списка
        </n-button>
      </div>
    </div>
  </n-card>

  <EditProduct
    v-else
    :item="item"
    :category-options="categoryOptions"
    :subcategory-options="subcategoryOptions"
    :subsubcategory-options="subsubcategoryOptions"
    @save="handleSave"
    @cancel="handleCancel"
  />

  <div v-if="confirmDialogVisible" class="dialog-overlay">
    <n-dialog
      class="confirm-dialog"
      title="Подтверждение удаления"
      positive-text="Удалить"
      negative-text="Отмена"
      :closable="false"
      @positive-click="deleteProduct"
      @negative-click="closeConfirmDialog"
    >
      Вы уверены, что хотите удалить этот продукт?
    </n-dialog>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { NCard, NButton, NDialog } from "naive-ui";
import { useUserStore } from "@/store/userStore";
import BasketButton from "./BasketButton.vue";
import EditProduct from "./EditProduct.vue";
import api from '@/services/api.js';

const props = defineProps({
  item: 
  { 
    type: Object, 
    required: true 
  },
  categoryOptions: { 
    type: Array 
  },
  subcategoryOptions: { 
    type: Array 
  },
  subsubcategoryOptions: { 
    type: Array 
  },
});

const userStore = useUserStore();
const confirmDialogVisible = ref(false);
const role = computed(() => userStore.role.value);
const isAdmin = computed(() => role.value === "ROLE_ADMIN");
const isAuthenticated = ref(false);

const checkAuth = () => {
  const token = localStorage.getItem("token");
  if (token) isAuthenticated.value = true;
};

const isEdited = ref(false);

const editModel = () => {
  isEdited.value = true;
};

const openConfirmDialog = (event) => {
  event.stopPropagation();
  confirmDialogVisible.value = true;
};

const emit = defineEmits(["delete"]);

const deleteProduct = async () => {
  try {
    await api.post(`/product/delete/${props.item.id}`);
    closeConfirmDialog();
    emit("delete", props.item.id);
  } catch (error) {
    console.error("Ошибка при удалении продукта", error);
    closeConfirmDialog();
  }
};

const closeConfirmDialog = () => {
  confirmDialogVisible.value = false;
};

const router = useRouter();

const navigateToproduct = () => {
  router.push({ path: `/product-view/${props.item.id}` });
};

const handleSave = async (updatedProduct) => {
  const formData = new FormData();
  formData.append("productData", JSON.stringify({
      productId: updatedProduct.id,
      newTitle: updatedProduct.newTitle,
      newDescription: updatedProduct.newDescription,
      newCount: updatedProduct.newCount,
      newPrice: updatedProduct.newPrice,
      newDiscountPrice: updatedProduct.newDiscountPrice,
      newCategory: updatedProduct.newCategory,
      newSubCategory: updatedProduct.newSubCategory,
      newSubSubCategory: updatedProduct.newSubSubCategory,
    })
  );

  if (updatedProduct.images) formData.append("images", updatedProduct.images);
  try {
    await api.put(`/product/change`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    window.location.reload();
  } catch (error) {
    console.error("Ошибка при сохранении продукта:", error);
  }
  isEdited.value = false;
};

const handleCancel = () => {
  isEdited.value = false;
};

onMounted(() => {
  userStore.fetchUser();
  checkAuth();
});
</script>

<style lang="scss" scoped>
.product-card {
  width: 800px;
  padding: 50px 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  display: flex;
  flex-direction: column;
  position: relative;
  cursor: pointer;

  &__content {
    display: flex;
    flex-direction: column;
  }

  &__title {
    font-size: 24px;
    margin: 0 0 10px 0;
  }

  &__info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: auto;

    span {
      font-size: 16px;
    }

    b {
      font-weight: bold;
    }
  }

  &__image {
    width: 300px;
    height: 300px;

    img {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
  }

  &__buttons {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    gap: 10px;

    .n-button {
      width: 100%;
    }
  }
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
  z-index: 9998;
}

.confirm-dialog {
  z-index: 9999;
}

.edit-icon-container {
  position: absolute;
  top: 40px;
  right: 40px;

  img {
    width: 20px;
  }
}
</style>
