<template>
  <div>
    <label>{{ label }}</label>
    <div class="select__control">
      <NSelect 
      v-model:value="selectedValue" 
      :options="options" 
      />
      <NButton @click="toggleInput" size="small" style="margin-left: 8px">
        {{ showInput ? "-" : "+" }}
      </NButton>
    </div>
    <n-input
      v-if="showInput"
      :value="inputValue"
      placeholder="Введите новое значение"
      style="margin-top: 8px"
      @update:value="updateInputValue"/>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue';
import { NSelect, NButton, NInput } from 'naive-ui';

defineProps({
  options: {
    type: Array,
    default: () => [],
  },
  label: String,
});

const emit = defineEmits(['data-changed']);

const selectedValue = ref(null);
const inputValue = ref('');
const showInput = ref(false);

const updateInputValue = (value) => {
  inputValue.value = value;
};
const toggleInput = () => {
  showInput.value = !showInput.value;
  if (showInput.value) {
    selectedValue.value = null;
    inputValue.value = ''; 
  }
};

watch(selectedValue, (newValue) => {
  if (newValue !== null) {
    showInput.value = false;
    emit('data-changed', newValue);
    inputValue.value = '';
  }
});

watch(inputValue, (newValue) => {
  if (showInput.value && newValue !== '') {
    emit('data-changed', newValue);
  }
});
</script>

<style lang="scss" scoped>
.select__control{
  display: flex;
  align-items: center;
}
</style>