<template>
  <n-dialog
     v-model:show="showModal" 
     class="modal__overlay">
      <div class="modal__overlay__content"> 
       <n-button 
         class="modal__overlay__close"
         color="#465a86" 
         @click="emitClose">
         ✖
       </n-button>
       <div class="form-container">
         <div>
           <label>Изображение к товару:</label>
           <n-upload
             :default-file-list="fileList"
             :max="1"
             list-type="image"
             @change="handleFileChange">
             <n-button color="#465a86">Выбрать файл</n-button>
           </n-upload>
         </div>

         <div>
           <label>Название товара:</label>
           <n-input
             v-model:value="product.title"
             type="text"
             placeholder="Название"
             required 
            />
         </div>

         <div>
           <label>Цена:</label>
           <n-input
             v-model:value="product.price"
             type="number"
             placeholder="Цена"
             required
            />
         </div>
 
         <div>
           <label>Скидочная цена:</label>
           <n-input
             v-model:value="product.discountPrice"
             type="number"
             placeholder="Скидочная цена"
             />
         </div>
 
         <div>
           <label>Описание товара:</label>
           <n-input 
            v-model:value="product.description"
            type="textarea" 
            placeholder="Описание товара"
            required 
           />
         </div>
 
         <div>
           <SelectCategory
             :options="categoryOptions"
             label="Категория"
             @data-changed="(value) => handleDataChange('category')(value)"
           />
         </div>

         <div>
           <SelectCategory
             :options="subcategoryOptions"
             label="Подкатегория (Необязательно)"
             @data-changed="(value) => handleDataChange('subcategory')(value)"
           />
         </div>

         <div>
           <SelectCategory
             :options="subsubcategoryOptions"
             label="Подподкатегория (Необязательно)"
             @data-changed="(value) => handleDataChange('subsubcategory')(value)"
           />
         </div>

         <div>
           <label>Количество товаров:</label>
           <n-input
             v-model:value="product.count"
             :show-button="false"
             type="number"
             placeholder="Количество"
             />
         </div>

         <n-button 
           color="#465a86" 
           class="modal__overlay__create" 
           @click="uploadFile">
           Создать товар
         </n-button>
        </div>
     </div>
  </n-dialog>
 </template>
 
<script setup>
import { ref, defineEmits, onMounted } from 'vue';
import { NInput, NButton, NDialog, NUpload } from 'naive-ui';
import SelectCategory from './SelectCategory.vue';
import { useNotificationService } from '@/composables/useNotifications';
import api from '@/services/api.js';
 
 const { showNotificationMessage } = useNotificationService();
 
 const showModal = ref(false);
 const categoryOptions = ref([]);
 const subcategoryOptions = ref([]);
 const subsubcategoryOptions = ref([]);
 
 const emit = defineEmits(["close"]);

 const emitClose = () => {
   showModal.value = false;
   emit("close");
 };
 
 const getUniqueValues = (items, key) => {
   return Array.from(new Set(items.map((item) => item[key])))
     .filter((value) => value !== null)
     .map((value) => ({
       label: value,
       value: value,
     }));
 };

 const product = ref({
   title: '',
   price: '',
   discountPrice: '',
   description: '',
   category: '',
   count: '',
 });

 const fetchData = async () => {
   try {
     const response = await api.get(`/product/getAll`);
     const allCategories = response.data.flatMap((product) => product.categories);
     categoryOptions.value = getUniqueValues(allCategories, "name");
     subcategoryOptions.value = getUniqueValues(allCategories.filter((cat) => cat.subcategory !== null),"subcategory");
     subsubcategoryOptions.value = getUniqueValues(allCategories.filter((cat) => cat.subsubcategory !== null),"subsubcategory");
   } catch (error) {
     console.error("Ошибка при загрузке данных:", error);
   }
 };

 const selectedData = ref({
   category: null,
   subcategory: null,
   subsubcategory: null,
 });
 
 const handleDataChange = (field) => (value) => {
   selectedData.value[field] = value;
 };
 
 const fileList = ref([]);

 const handleFileChange = (event) => {
   if (event.fileList && event.fileList.length > 0) {
     fileList.value = event.fileList;
   }
 };

 const isFormValid = () => {
   return (
     fileList.value.length > 0 &&
     product.value.title &&
     product.value.price &&
     product.value.description &&
     selectedData.value.category &&
     product.value.count !== ""
   );
 };
 
 const uploadFile =  async() => {
   if (isFormValid()) {
     const formData = new FormData();
     formData.append("file1", fileList.value[0].file);
     formData.append("title", product.value.title);
     formData.append("price", product.value.price);
     formData.append("discountPrice", product.value.discountPrice);
     formData.append("description", product.value.description);
     formData.append("category", selectedData.value.category);
     formData.append("subcategory", selectedData.value.subcategory || "");
     formData.append("subsubcategory", selectedData.value.subsubcategory || "");
     formData.append("count", product.value.count);
     try {
       const token = localStorage.getItem("token");
        const response = await api.post(`/product/create`, formData,
         {
           headers: {
             "Content-Type": "multipart/form-data",
             Authorization: token,
           },
         });
         if(response.status === 201){
           window.location.reload();
         } else{
           showNotificationMessage('error', 'Товар не удалось создать');
         }
     } catch (error) {
       console.error("Error uploading file:", error.response?.data || error);
     }
   } else{
     showNotificationMessage('error', 'Не все поля заполнены');
   }
 }
 
 onMounted(() => {
   fetchData();
 })
 </script>
 
 <style lang="scss" scoped>
 .modal__overlay {
   position: absolute;
   top: 0;
   left: 0;
   width: 100%;
   overflow-y: auto;
   height: 100%;
   background: rgba(0, 0, 0, 0.7);
   display: flex;
   align-items: center;
   justify-content: center;
   z-index: 130;

  &__content {
    background: white;
    padding: 20px;
    border-radius: 8px;
    width: 700px;
    position: relative; 
    z-index: 100;
  }
  &__close {
  font-size: 24px;
  position: absolute;
  top: 10px;
  right: 20px;

    &:hover {
      color: #f0f0f0;
    }
  }
  &__create {
   width: 100%;
   padding: 10px;
   cursor: pointer;
  }
  label {
   display: block;
   margin-bottom: 5px;
   font-weight: bold;
  }
  .form-container > div {
   margin-bottom: 15px;
  }
}
.n-dialog :deep(.n-dialog__title) {
  display: none;
}
.n-dialog :deep(.n-dialog__close) {
  display: none;
}
</style>
 