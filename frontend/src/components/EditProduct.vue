<template>
  <n-card class="card">
    <div class="correct_img">
      <div v-if="!imageDeleted" class="image-container">
        <img
          :src="item.imageUrl"
          class="image__delete"
          alt="png"
          />
        <div class="overlay">
          <n-button color="#fff" style="color: #000;" size="large" @click.stop="deleteImage">Удалить</n-button>
        </div>
      </div>
      <n-upload
        v-else
        :default-file-list="fileList"
        :create-thumbnail-url="createThumbnailUrl"
        list-type="image"
        max="1"
        @change="handleChange">
        <n-button>Загрузить</n-button>
      </n-upload>
    </div>
      <label>Название товара</label>
      <n-input 
        v-model:value="editProduct.title"
        type="text" 
        placeholder="Название товара" 
        clearable
        />
      <label>Описание товара</label>
      <n-input
        v-model:value="editProduct.description"
        type="textarea"
        placeholder="Введите описание товара"
        clearable
        />
      <label>Цена товара</label>
      <n-input-number
        v-model:value="editProduct.price"
        :show-button="false"
        type="number"
        placeholder="Введите цену товара"
        clearable
        />
      <label>Скидочная цена товара</label>
      <n-input-number
        v-model:value="editProduct.discountPrice"
        :show-button="false"
        type="number"
        placeholder="Введите скидку товара"
        clearable
        />
      <label>Количество товаров</label>
      <n-input-number
        v-model:value="editProduct.count"
        :show-button="false"
        type="number"
        placeholder="Введите количество товаров"
        clearable
        />
  
      <label>Категория</label>
      <n-select
        v-model:value="selectedCategory"
        :options="categoryOptions"
        placeholder="Выберите категорию"
        @update:value="handleCategoryChange"
        />
  
      <label>Подкатегория</label>
      <n-select
        v-model:value="selectedSubcategory"
        :options="subcategoryOptions"
        placeholder="Выберите подкатегорию"
        @update:value="handleSubcategoryChange"
        />
  
      <label>Подподкатегория</label>
      <n-select
        v-model:value="selectedSubsubcategory"
        :options="subsubcategoryOptions"
        placeholder="Выберите субсубкатегорию"
        />
  
      <div class="edit__button">
        <n-button color="#465a86" @click.stop="saveChanges">
          Сохранить изменения
        </n-button>
        <n-button type="error" @click.stop="cancelEdit">
          Отменить изменения
        </n-button>
      </div>
    </n-card>
  </template>
  
<script setup>
import { ref, defineProps, defineEmits } from 'vue';
import { NCard, NButton, NInput, NUpload, NSelect, NInputNumber } from 'naive-ui';

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

const emit = defineEmits(['save', 'cancel']);
const selectedCategory = ref(props.item.categories[0]?.name || '');
const selectedSubcategory = ref(props.item.categories[0]?.subcategory || '');
const selectedSubsubcategory = ref(props.item.categories[0]?.subsubcategory || '');

const imageDeleted = ref(false);
const editProduct = ref({ ...props.item });

const deleteImage = () => {
  imageDeleted.value = true;
  editProduct.value.imageUrl = null;
};

const fileList = ref([]);

const handleChange = (event) => {
  if (event.fileList && event.fileList.length > 0) {
    fileList.value = event.fileList; 
  }
};
  
const cancelEdit = () => {
  emit('cancel');
};
  
const saveChanges = () => {
  let imageToSend = null;
    if (imageDeleted.value && fileList.value.length > 0) {
        imageToSend = fileList.value[0].file; 
    }
    const updatedProduct = {
      id: editProduct.value.id,
      newTitle: editProduct.value.title,
      newDescription: editProduct.value.description,
      newPrice: editProduct.value.price,
      newDiscountPrice: editProduct.value.discountPrice,
      newCount: editProduct.value.count,
      newCategory: selectedCategory.value,
      newSubCategory: selectedSubcategory.value,
      newSubSubCategory: selectedSubsubcategory.value,
      images: imageToSend
    }
    emit('save', updatedProduct);
};
  
const handleCategoryChange = (value) => {
  selectedCategory.value = value;
  selectedSubcategory.value = '';
  selectedSubsubcategory.value = '';
};
  
const handleSubcategoryChange = (value) => {
  selectedSubcategory.value = value;
  selectedSubsubcategory.value = '';
};
</script>

<style lang="scss" scoped>
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

.edit__button {
  margin-top: 20px;
  display: flex;
  gap: 20px;
}

.image__delete {
  width: 150px;
  transition: opacity 0.3s ease;
}

.image-container {
  position: relative;
  display: inline-block;

  &:hover .image__delete {
    opacity: 0.5;
  }

  &:hover .overlay {
    opacity: 1;
  }
}

.btn__delete {
  color: #fff;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}
</style>