<template>
  <n-modal 
    v-model:show="visible" 
    title="Написать отзыв"
    content-style="padding:20px; width:600px;" 
    @close="handleClose">
    <div class="review-form">
      <label for="reviewText">Ваш отзыв:</label>
      <n-input
        v-model:value="reviewText"
        type="textarea"
        placeholder="Введите ваш отзыв"
        rows="4"
        />
      <label>Оценка:</label>
      <div class="stars">
        <svg
          v-for="star in maxStars"
          :key="star"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
          :fill="star <= rating ? 'gold' : 'lightgray'"
          width="30"
          height="30"
          @click="setRating(star)">
          <path d="M12 2l3.09 6.26L22 9.27l-5 4.87L18.18 21 12 17.27 5.82 21 7 14.14 2 9.27l6.91-1.01L12 2z"/>
        </svg>
      </div>
      <label>Добавить изображения:</label>
      <n-upload 
        :multiple="true" 
        :before-upload="beforeUpload"
        @change="handleChange">
        <n-button color="#465a86">Выберите файлы</n-button>
      </n-upload>

      <div v-if="images.length">
        <h3>Загруженные изображения:</h3>
        <div class="images-wrapper">
          <div 
            v-for="img in images" 
            :key="img.id" 
            class="image-item">
            <img 
              :src="img.url" 
              :alt="image" 
              />
          </div>
        </div>
      </div>
      <n-button color="#465a86" @click="submitReview">Отправить отзыв</n-button>
    </div>
  </n-modal>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch, onMounted } from 'vue';
import { NModal, NUpload, NButton, NInput } from 'naive-ui';
import { useNotificationService } from '@/composables/useNotifications.js'; 
import api from '@/services/api.js';

const props = defineProps({
  modelValue: Boolean,
  productId: {
    type: Number,
    required: true
  }
});

const emits = defineEmits(['update:modelValue']);

const { showNotificationMessage } = useNotificationService();
const visible = ref(props.modelValue);
const reviewText = ref('');
const maxStars = 5;
const rating = ref(0);

const setRating = (star) => {
  rating.value = star;
};

const beforeUpload = () => {
  return true;
};

const files = ref([]);
const images = ref([]);

const handleChange = (event) => {
  images.value = event.fileList.map(file => {
    const fileObject = file.file;
    return {
      uid: file.uid,
      name: file.name,
      url: URL.createObjectURL(fileObject)
    };
  });
  files.value = event.fileList.map(file => file.file);
};

const handleClose = () => {
  emits('update:modelValue', false);
};

const submitReview = async () => {
  if (!reviewText.value || rating.value === 0) {
    showNotificationMessage('error', 'Ошибка', 'Заполните все поля!');
    return;
  }
  const formData = new FormData();
  formData.append('productId', props.productId);
  formData.append('text', reviewText.value);
  formData.append('score', rating.value.toString());
  files.value.forEach((file) => {
    formData.append('images', file); 
  });
  const token = localStorage.getItem('token');
  try {
    const response = await api.post(`/comments/add`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': token,
      },
    });

    if (response.status === 200) {
      localStorage.setItem('showReviewSuccessNotification', 'true');
      setTimeout(() => {
        window.location.reload();
      }, 1000);
    }
  } catch (error) {
    console.error('Ошибка при отправке комментария:', error.response ? error.response.data : error.message);
    showNotificationMessage('error', 'Ошибка', 'Произошла ошибка при отправке комментария.');
  }
};

watch(() => props.modelValue, (newVal) => {
  visible.value = newVal;
});

onMounted(() => {
  const notificationFlag = localStorage.getItem('showReviewSuccessNotification');
  
  if (notificationFlag === 'true') {
    showNotificationMessage('success', 'Успешно!', 'Ваш отзыв был успешно отправлен.');
    localStorage.removeItem('showReviewSuccessNotification');
  }
});
</script>

<style lang="scss" scoped>
.n-modal  {
  padding: 20px;
  box-sizing: border-box; 
  width: 600px; 
  background-color: #fff;
}

.review-form {
  display: flex;
  flex-direction: column;
  gap: 10px;

  .stars {
    display: flex;
    gap: 5px;
    cursor: pointer;
  }
}

.images-wrapper {
  display: flex;
  flex-direction: row;
  overflow-x: auto;
  gap: 10px;
  max-width: 100%; 
  padding: 10px 0;

  .image-item img {
    max-width: 100px;
    max-height: 100px;
    margin: 5px;
  }
}

.n-upload-file-list :deep(.n-upload-file) {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 10px;
}

.n-upload-file :deep(.n-upload-file) {
  max-width: 120px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>