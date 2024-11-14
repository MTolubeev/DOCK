<template>
  <n-dialog class="modal__overlay">
    <div class="modal__overlay__content">
      <n-button color="#465a86" class="modal__overlay__close" @click="closeModal">✖</n-button>
      <h2>Регистрация нового пользователя</h2>
      <p>Если аккаунт уже существует, то войдите</p>
      <router-link :to="{ name: 'SignIn', query: { from: 'registration' } }">
         <n-button type="warning">Войти</n-button> 
      </router-link>
      <n-input 
        v-model:value="username" 
        type="text" 
        placeholder="Имя" 
        required  
        />
      <n-input 
        v-model:value="surname" 
        type="text" 
        placeholder="Фамилия" 
        required 
        />
      <n-input 
        v-model:value="email" 
        type="email" 
        placeholder="Email" 
        required 
        />
      <n-input
        v-model:value="password" 
        type="password" 
        placeholder="Пароль" 
        required 
        />
        <div class="modal__overlay__buttons">
          <n-button color="#465a86" @click="register">Зарегистрироваться</n-button>
        </div>
    </div>
  </n-dialog>
</template>

<script setup>
import { ref } from 'vue';
import api from '../services/api';
import { NButton, NDialog, NInput } from 'naive-ui';
import router from '@/router';
import { useNotificationService } from '@/composables/useNotifications.js';
import { useUserStore } from "@/store/userStore";

const userStore = useUserStore();
const { showNotificationMessage } = useNotificationService(); 
const username = ref('');
const surname = ref('');
const email = ref('');
const password = ref('');

const register = async () => {
  try {
    await api.post('/user/registration', null, {
      params: {
        username: username.value,
        surname: surname.value,
        email: email.value,
        password: password.value,
      },
   });
    await userStore.login(email.value, password.value);
    console.log(email.value, password.value)
    router.push({name: 'MainPage'});
    showNotificationMessage('success', 'Успех', 'Вы успешно зарегестрировались.'); 
  } catch (error) {
    showNotificationMessage('error', 'Ошибка', 'Регистрация не прошла'); 
  }
};
const closeModal = () => {
  router.push({name: 'MainPage'}).then(() => window.location.reload());
};
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
  &__close {
    font-size: 24px;
    cursor: pointer;
    position: absolute;
    top: 10px;
    right: 10px;
  }

  &__buttons {
    display: flex;
    flex-direction: column;
    gap: 10px;
    width: 100%;
  }
  input {
    width: 100%;
    padding: 10px;
    margin: 10px 0;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
  
}
.n-dialog :deep(.n-dialog__title) {
  display: none;
}
.n-dialog :deep(.n-dialog__close) {
  display: none;
}
</style>