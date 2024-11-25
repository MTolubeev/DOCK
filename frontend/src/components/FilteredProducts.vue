<template> 
  <n-space vertical> 
    <n-card title="Вид телефонов"> 
      <n-checkbox-group v-model:value="localSelectedPhones"> 
        <n-checkbox label="Apple" value="Apple" /> 
        <n-checkbox label="Samsung" value="Samsung" /> 
        <n-checkbox label="Huawei" value="Huawei" /> 
      </n-checkbox-group> 
    </n-card> 
 
    <n-card title="Стоимость"> 
      <n-input-number 
        v-model:value="localPriceRange[0]"
        :show-button="false" 
        :min="0" 
        :max="1000000" 
        placeholder="Минимум"
      /> 
      <n-input-number 
        v-model:value="localPriceRange[1]"
        :show-button="false" 
        :min="0" 
        :max="1000000" 
        placeholder="Максимум"
      /> 
      <n-button color="#465a86" @click="applyFilters">Применить</n-button>
      <n-button style="margin-top: 10px;" color="#465a86" @click="resetFilters">Сбросить</n-button>
    </n-card> 
  </n-space> 
</template> 
 
<script setup> 
import { ref, defineEmits } from 'vue'; 
import { NCheckbox, NCheckboxGroup, NCard, NSpace, NInputNumber, NButton } from 'naive-ui'; 

const emit = defineEmits(['update:selectedPhones', 'update:priceRange', 'resetFilters']); 

const localSelectedPhones = ref([]);
const localPriceRange = ref([0, null]); 

const applyFilters = () => {
  const minPrice = localPriceRange.value[0] || 0; 
  const maxPrice = localPriceRange.value[1] === null ? Infinity : localPriceRange.value[1]; 
  
  emit('update:priceRange', [minPrice, maxPrice]);
  emit('update:selectedPhones', localSelectedPhones.value);
};

const resetFilters = () => {
  localSelectedPhones.value = [];
  localPriceRange.value = [0, '']; 
  applyFilters();
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
