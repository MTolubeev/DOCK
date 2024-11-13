<template> 
  <n-space vertical> 
    <n-card title="Вид телефонов"> 
      <n-checkbox-group v-model:value="selectedPhones" @update:value="updateSelectedPhones"> 
        <n-checkbox label="Apple" value="Apple" /> 
        <n-checkbox label="Samsung" value="Samsung" /> 
        <n-checkbox label="Huawei" value="Huawei" /> 
      </n-checkbox-group> 
    </n-card> 
 
    <n-card title="Стоимость"> 
      <n-input-number v-model:value="priceRange[0]" :min="500" :max="1000000" /> 
      <n-input-number v-model:value="priceRange[1]" :min="0" :max="1000000" /> 
      <n-button color="#465a86" @click="applyPriceRange">Применить</n-button>
      <n-button style="margin-top: 10px;" color="#465a86" @click="resetFilters">Сбросить</n-button>
    </n-card> 
  </n-space> 
</template> 
 
<script setup> 
import { ref, defineEmits } from 'vue'; 
import { NCheckbox, NCheckboxGroup, NCard, NSpace, NInputNumber, NButton } from 'naive-ui'; 
 
const selectedPhones = ref([]); 
const priceRange = ref([500, 10000]); 
 
const emit = defineEmits(['update:selectedPhones', 'update:priceRange']); 
 
const updateSelectedPhones = (value) => { 
  emit('update:selectedPhones', value); 
}; 
 
const applyPriceRange = () => { 
  emit('update:priceRange', priceRange.value); 
}; 
const resetFilters = () => {
emit('resetFilters'); 
};
</script> 
 
<style scoped> 
.n-card { 
  width: 200px; 
} 
.n-input-number { 
  margin-bottom: 15px; 
} 
</style>