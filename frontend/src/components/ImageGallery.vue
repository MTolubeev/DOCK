<template>
  <div v-if="show" class="image__gallery">
    <n-button 
      color="#465a86"
      class="prev__button" 
      :disabled="currentIndex === 0"
      @click="prevImage">
      &#9664;
    </n-button>
    
    <div class="gallery__content">
      <img 
        v-if="currentImage"
        :src="currentImage"
        class="gallery__image" 
        :key="currentIndex"
        alt="gallery image"/>
      <n-button
        size="large"
        color="#465a86" 
        class="close__button" 
        @click="emitClose">
        ×
      </n-button>

      <n-button 
        v-if="isAdmin"
        class="delete__button"
        size="large"
        color="#465a86"
        @click="deleteImage">
        Удалить
      </n-button>
    </div>

    <n-button 
      color="#465a86"
      class="next__button" 
      :disabled="currentIndex >= images.length - 1"
      @click="nextImage">
      &#9654;
    </n-button>
  </div>
</template>

<script setup>
import { computed, ref, defineProps, defineEmits } from 'vue';
import { useUserStore } from "@/store/userStore";
import { NButton } from 'naive-ui';

const props = defineProps({
  images: {
    type: Array,
    required: true
  },
  show: {
    type: Boolean,
    required: true
  },
  commentId: {
    type: [Number, null],
    required: true
  }
});


const emit = defineEmits(['close', 'delete-image']);

const userStore = useUserStore();
const currentIndex = ref(0);

const role = computed(() => userStore.role.value);
const isAdmin = computed(() => role.value === "ROLE_ADMIN");

const currentImage = computed(() => {
  return props.images[currentIndex.value] ? props.images[currentIndex.value].base64 : null;
});

const deleteImage = () => {
  if (currentImage.value) {
    const imageId = props.images[currentIndex.value].id;
    emit('delete-image', { imageId, commentId: props.commentId });
  }
};

const emitClose = () => {
  emit('close');
};

const nextImage = () => {
  if (currentIndex.value < props.images.length - 1) {
    currentIndex.value++;
  }
};

const prevImage = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--;
  }
};
</script>

<style lang="scss" scoped>
.image__gallery {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;

  .gallery__content {
    text-align: center;
    display: flex;
    width: 400px;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;

    .gallery__image {
      max-width: 90%;
      max-height: 90%;
      object-fit: contain;
    }

    .close__button,
    .delete__button {
      position: fixed;
      top: 10px;
      color: #fff;

      &.close__button {
        right: 20px;
        font-size: 30px;
      }

      &.delete__button {
        left: 20px;
      }
    }
  }

  .prev__button,
  .next__button {
    position: fixed;
    top: 50%;
    transform: translateY(-50%);
    z-index: 101;
    font-size: 36px;
    color: #fff;

    &.prev__button {
      left: 20px;
    }

    &.next__button {
      right: 20px;
    }
  }
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
}
</style>
