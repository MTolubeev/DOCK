<template>
  <div class="header">
    <AppHeader @toggle-drawer="toggleDrawer" />
    <AppDrawer 
      :is-visible="isDrawerVisible" 
      @close-drawer="closeDrawer" 
    />
  </div>
  <AppCart />
</template>

<script setup>
import AppCart from '@/components/AppBasket.vue';
import AppHeader from '@/components/AppHeader.vue';
import AppDrawer from '@/components/AppDrawer.vue';
import { useDrawer } from '@/composables/useHeader.js';

const { isDrawerVisible, toggleDrawer, closeDrawer } = useDrawer();
</script>
