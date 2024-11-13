<template>
  <n-dialog class="modal__overlay">
    <div class="modal__overlay__content">
      <n-button color="#465a86" class="modal__overlay__close" @click="closeModal">✖</n-button>
      <h2>Вход в личный аккаунт</h2>
      <p>Нет аккаунта? создайте!</p>
      <router-link to="/registration">
        <n-button type="warning">Создать аккаунт</n-button>
      </router-link>
      <n-input
        v-model:value="loginEmail"
        type="email"
        placeholder="Email"
        required
        autocomplete="email"
        />
      <n-input
        v-model:value="loginPassword"
        type="password" 
        placeholder="Пароль"
        required
        autocomplete="current-password"
        />
        <n-button color="#465a86" class="modal__overlay__registrantion" @click="login">Войти</n-button>
    </div>
  </n-dialog>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/store/userStore';
import { useNotificationService } from '@/composables/useNotifications.js';
import { NButton, NInput, NDialog } from 'naive-ui';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const { showNotificationMessage } = useNotificationService();

const loginEmail = ref('');
const loginPassword = ref('');
let cameFromRegistration = ref(false);

const login = async () => {
  try {
    await userStore.login(loginEmail.value, loginPassword.value);
    showNotificationMessage('success', 'Успех', 'Вы успешно вошли в аккаунт');

    if (cameFromRegistration.value) {
      router.push("/"); 
    } else {
      window.history.back(); 
    }
  } catch (error) {
    showNotificationMessage('error', 'Ошибка', 'Неправильный логин или пароль');
  }
};

const closeModal = () => {
  router.push("/").then(() => window.location.reload());
};

onBeforeMount(() => {
  if (
    route?.redirectedFrom?.fullPath === "/registration" ||
    route?.query?.from === 'registration'
  ) {
    cameFromRegistration.value = true;
  }
});
</script>

<style lang="scss" scoped>
.modal__overlay {
  position: fixed;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;

  &__content {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #fff;
    padding: 20px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    width: 500px;
    gap: 10px;

    & h2 {
        margin-top: 0;
      }
  }
  input {
    width: 100%;
    padding: 10px;
    margin: 10px 0;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
  &__close {
    font-size: 24px;
    cursor: pointer;
    position: absolute;
    top: 10px;
    right: 20px;
  }
  &__registrantion {
    width: 100%;
    display: flex;
    justify-content: center;
    padding: 10px;
    font-size: 16px;
    cursor: pointer;
  }
}
.n-dialog :deep(.n-dialog__title) {
  display: none;
}
.n-dialog :deep(.n-dialog__close) {
  display: none;
}

</style>
