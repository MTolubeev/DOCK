<template>
  <n-card
    v-if="!isEdited"
    class="card"
    size="huge"
    hoverable
    @click="navigateToproduct"
  >
    <div class="edit_container">
      <img
        v-if="isAdmin"
        src="@/assets/pencil.svg"
        alt="edit_product"
        @click.stop="editModel"
      />
    </div>
    <div class="card-content">
      <div class="image-container">
        <img :src="item.imageUrl" alt="png" />
      </div>
      <h3>{{ item.title }}</h3>
      <div class="info_card">
        <span v-if="isAuthenticated">
          Цена: <b>{{ item.discountPrice }} руб.</b>
          <del style="margin-left: 10px">{{ item.price }} руб.</del>
        </span>
        <span v-else>
          Цена: <b>{{ item.price }} руб.</b>
        </span>
        <span v-if="item.count > 0"
          >Количество товаров осталось: <b>{{ item.count }}</b></span
        >
        <span v-else><b>Товара нет на складе</b></span>
      </div>
      <div class="card__button">
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
import axios from "axios";
import EditProduct from "./EditProduct.vue";

const props = defineProps({
  item: {
    type: Object,
    required: true,
  },

  categoryOptions: {
    type: Array,
    required: true,
  },

  subcategoryOptions: {
    type: Array,
    required: true,
  },

  subsubcategoryOptions: {
    type: Array,
    required: true,
  },
});

const emit = defineEmits(["delete"]);

const userStore = useUserStore();
const router = useRouter();
const confirmDialogVisible = ref(false);
const isAuthenticated = ref(false);
const isEdited = ref(false);

const role = computed(() => userStore.role.value);
const isAdmin = computed(() => role.value === "ROLE_ADMIN");

const checkAuth = () => {
  const token = localStorage.getItem("token");
  if (token) {
    isAuthenticated.value = true;
  }
};

const editModel = () => {
  isEdited.value = true;
};
const openConfirmDialog = (event) => {
  event.stopPropagation();
  confirmDialogVisible.value = true;
};
const deleteProduct = async () => {
  try {
    await axios.post(`http://localhost:8080/product/delete/${props.item.id}`);
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

const navigateToproduct = () => {
  router.push({ path: `/product-view/${props.item.id}` });
};

const handleSave = async (updatedProduct) => {
  const newFrom = new FormData();

  newFrom.append("id", updatedProduct.id);
  newFrom.append("newTitle", updatedProduct.newTitle);
  newFrom.append("newDescription", updatedProduct.newDescription);
  newFrom.append("newCount", updatedProduct.newCount);
  newFrom.append("newPrice", updatedProduct.newPrice);
  newFrom.append("newDiscountPrice", updatedProduct.newDiscountPrice);
  newFrom.append("newCategory", updatedProduct.newCategory);
  newFrom.append("newSubCategory", updatedProduct.newSubCategory);
  newFrom.append("newSubSubCategory", updatedProduct.newSubSubCategory);
  newFrom.append("images", updatedProduct.images);

  for (let [key, value] of newFrom.entries()) {
    console.log(key, value);
  }
  try {
    await axios.put(`http://localhost:8080/product/change`, newFrom, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    window.location.reload();
  } catch (error) {
    console.error("Error saving product:", error);
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

<style scoped>
.card {
  width: 800px;
  padding: 50px 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  cursor: pointer;
}
.card-content {
  display: flex;
  flex-direction: column;
}
.image-container {
  width: 300px;
  height: 300px;
}

.image-container img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.n-button {
  width: 100%;
}
.card__button {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.card h3 {
  font-size: 24px;
  margin: 0 0 10px 0;
}
.card p {
  font-size: 16px;
  margin: 0 0 20px 0;
}
.info_card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}
.info_card span {
  font-size: 16px;
}
.info_card b {
  font-weight: bold;
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
.edit_container {
  position: absolute;
  top: 40px;
  right: 40px;
}
.edit_container img {
  width: 20px;
}
</style>
