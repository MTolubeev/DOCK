<template>
  <NNotificationProvider>
    <AppHeader
      v-if="shouldShowComponent"
      @toggle-drawer="toggleDrawer"
    />
    <AppDrawer
      :is-visible="isDrawerVisible" 
      @close-drawer="closeDrawer"
    />
    <router-view />
  </NNotificationProvider>
</template>

<script setup>
import { ref, watchEffect } from 'vue'; 
import { NNotificationProvider } from 'naive-ui';
import AppHeader from '@/components/AppHeader.vue';
import AppDrawer from '@/components/AppDrawer.vue';
import { useRoute } from 'vue-router'


const route = useRoute();

const shouldShowComponent = ref(!['SignIn', 'Registrantion'].includes(route.name));

watchEffect(() => {
  shouldShowComponent.value = !['SignIn', 'Registrantion'].includes(route.name);
});

const isDrawerVisible = ref(false);

const toggleDrawer = () => {
  isDrawerVisible.value = !isDrawerVisible.value;
};

const closeDrawer = () => {
  isDrawerVisible.value = false;
};
</script>
