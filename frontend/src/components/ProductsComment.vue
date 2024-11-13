<template>
  <div class="title__comments">
    <h2>Отзывы о товаре</h2>
    <div class="controls">
      <h3
        v-if="comments.length > 3"
        @click="toggleComments">
        {{ showAll ? "Скрыть отзывы" : "Показать все отзывы" }}
      </h3>
      <n-button
        color="#465a86"
        @click="handleWriteReview">
        Написать отзыв
      </n-button>
      <n-select
        v-if="comments.length > 0"
        v-model:value="sortOrder"
        :options="sortOptions"
        placeholder="Сортировать по"
        style="width: 200px; margin-left: 20px"/>
    </div>
  </div>

  <div class="comments__wrapper">
    <div v-if="comments.length > 0" class="comments__items">
      <n-card v-for="comment in sortedComments" :key="comment.id">
        <div class="profile">
          <img src="@/assets/Union.svg" alt="user" />
          <strong>{{ comment.username }}</strong>
          <div class="stars">
            <svg
              v-for="star in maxStars"
              :key="star"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              :fill="star <= comment.score ? 'gold' : 'lightgray'"
              width="20"
              height="20">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87L18.18 21 12 17.27 5.82 21 7 14.14 2 9.27l6.91-1.01L12 2z"/>
            </svg>
          </div>
        </div>
        <div>{{ comment.text }}</div>
        <div v-if="comment.images.length > 0" class="comment-images">
          <div class="images-container">
            <div class="images-wrapper">
              <img
                v-for="image in comment.images.slice(0, 3)"
                :key="image.id"
                :src="convertBase64(image.bytes)"
                class="comment-image"
                alt="изображение комментария"
                @click="openImageGallery(comment.images, comment.id)"/>
            </div>
            <n-button
              v-if="comment.images.length > 3"
              color="#465a86"
              quaternary
              circle
              size="large"
              @click="openImageGallery(comment.images)">
              +
            </n-button>
          </div>
        </div>
        <ImageGallery
          :images="currentImages"
          :show="showImageGallery"
          :comment-id="currentImageGalleryId"
          @close="closeImageGallery"
          @delete-image="handleDeleteImage"
        />
        <img
          v-if="isAdmin || isOwner(comment)"
          class="admin-icon"
          src="@/assets/bin.webp"
          alt="admin badge"
          @click="openDeleteDialog(comment.id)"
          />
      </n-card>
    </div>
    <div v-else>
      <h3>Комментариев пока что нет</h3>
    </div>
  </div>

  <div v-if="confirmDialogVisible" class="modal-overlay">
    <n-dialog
      class="modal-content"
      title="Удаление комментария"
      positive-text="Удалить"
      negative-text="Отмена"
      @positive-click="confirmDeleteComment"
      @negative-click="closeConfirmDialog"
      :closable="false">
      Оформить заказ?
    </n-dialog>
  </div>
  <WriteReviewModal v-model:show="showReviewModal" :product-id="productId" />
</template>

<script setup>
import { defineProps, ref, computed, onMounted } from 'vue';
import { NCard, NButton, NSelect, NDialog } from 'naive-ui';
import WriteReviewModal from './WriteReviewModal.vue';
import { useUserStore } from '@/store/userStore';
// import axios from 'axios';
import ImageGallery from './ImageGallery.vue';
import { useNotificationService } from '@/composables/useNotifications';
import api from '@/services/api.js';

const props = defineProps({
  comments: {
    type: Array,
    required: true,
  },
  productId: {
    type: Number,
    required: true,
  },
});

const { showNotificationMessage } = useNotificationService();
const userStore = useUserStore();

const showAll = ref(false);
const showReviewModal = ref(false);
const confirmDialogVisible = ref(false);
const commentIdToDelete = ref(null);
const sortOrder = ref('desc');
const currentImages = ref([]);
const showImageGallery = ref(false);
const currentImageGalleryId = ref(null);
const maxStars = 5;

const sortOptions = [
  { label: "От высоких к низким", value: 'desc' },
  { label: "От низких к высоким", value: 'asc' },
];

const user = computed(() => userStore.user.value);
const role = computed(() => userStore.role.value);
const isAdmin = computed(() => role.value === "ROLE_ADMIN");

const toggleComments = () => {
  showAll.value = !showAll.value;
};
const closeImageGallery = () => {
  showImageGallery.value = false;
  currentImages.value = [];
};

const convertBase64 = (base64String) => {
  return base64String ? `data:image/jpeg;base64,${base64String}` : "";
};

const isOwner = (comment) => {
  return user.value && user.value.id === comment.userId;
};

const sortedComments = computed(() => {
  const sorted = [...props.comments];
  sorted.sort((a, b) => {
    return sortOrder.value === 'asc' ? a.score - b.score : b.score - a.score;
  });
  return showAll.value ? sorted : sorted.slice(0, 3);
});

const handleWriteReview = () => {
  if (user.value) {
    showReviewModal.value = true;
  } else {
    showNotificationMessage('error', 'Ошибка добавления комментария','Авторизуйтесь для создания комментария');
  }
};

const openDeleteDialog = (commentId) => {
  commentIdToDelete.value = commentId;
  confirmDialogVisible.value = true;
};

const closeConfirmDialog = () => {
  confirmDialogVisible.value = false;
  commentIdToDelete.value = null;
};
const openImageGallery = (images, commentId) => {
  currentImages.value = images.map((image) => ({
    base64: convertBase64(image.bytes),
    ...image,
  }));
  currentImageGalleryId.value = commentId;
  showImageGallery.value = true;
};

const confirmDeleteComment = async () => {
  if (!commentIdToDelete.value) return;

  try {
    const url = isAdmin.value
      ? `/comments/${commentIdToDelete.value}`
      : `/comments/usersComment/${commentIdToDelete.value}`;

    const token = localStorage.getItem("token");
    const response = await api.delete(url, {
      headers: {
        Authorization: token,
      },
    });

    if (response.status === 200) {
      localStorage.setItem('showDeleteSuccessNotification', 'true');
      window.location.reload();
    } else {
      showNotificationMessage('error', 'Ошибка','Произошла ошибка при удалении комментария.');
    }
  } catch (error) {
    if (error.response) {
      showNotificationMessage('error', 'Ошибка',`Ошибка: ${error.response.data.message || error.response.data}`);
    } else {
      showNotificationMessage('error', 'Ошибка', 'Произошла ошибка при удалении комментария.');
    }
  } finally {
    closeConfirmDialog();
  }
};
const handleDeleteImage = async ({ commentId, imageId }) => {
  try {
    const token = localStorage.getItem("token");

    const response = await api.delete(`/comments/deleteImage/${commentId}`,
      {
        headers: {
          Authorization: token,
        },
        params: {
          commentImageId: imageId,
        },
      }
    );

    if (response.status === 200) {
      localStorage.setItem('showDeleteImageSuccessNotification', 'true');
      window.location.reload();
    } else {
      showNotificationMessage('error', 'Ошибка при удалении изображения.');
    }
  } catch (error) {
    console.error(error);
    showNotificationMessage('error','Произошла ошибка при удалении изображения.');
  }
};

onMounted(async () => {
await userStore.fetchUser();
const deleteCommentNotification = localStorage.getItem('showDeleteSuccessNotification');
  if (deleteCommentNotification === 'true') {
    showNotificationMessage('success', 'Комментарий успешно удалён.');
    localStorage.removeItem('showDeleteSuccessNotification');
  }

const deleteImageNotification = localStorage.getItem('showDeleteImageSuccessNotification');
  if (deleteImageNotification === 'true') {
    showNotificationMessage('success', 'Изображение успешно удалено.');
    localStorage.removeItem('showDeleteImageSuccessNotification');
  }
});
</script>

<style lang="scss" scoped>
.comments__wrapper {
  display: flex;
  align-items: center;
  justify-content: center;

  .comments__items {
    margin-top: 30px;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 30px;

    .profile {
      display: flex;
      gap: 10px;
    }
    
    .n-card {
      width: 300px;
      border-radius: 20px;

      .admin-icon {
        cursor: pointer;
        position: absolute;
        top: 10px;
        right: 10px;
        width: 25px;
        height: 25px;
      }
    }

    .comment-image {
      width: 60px;
      height: 60px;
      object-fit: contain;
      border-radius: 10px;
      cursor: pointer;
    }
  }

  .images-container {
    display: flex;
    align-items: center;
    gap: 10px;

    .images-wrapper {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
    }
  }
}

.title__comments {
  display: flex;
  margin-top: 20px;
  align-items: center;
  flex-direction: column;
  justify-content: center;

  h2, h3 {
    margin: 0;
    cursor: pointer;
  }
}

.controls {
  display: flex;
  align-items: center;
  gap: 15px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;

  .modal-actions {
    margin-top: 20px;
    display: flex;
    justify-content: center;
    gap: 15px;
  }
}
</style>
