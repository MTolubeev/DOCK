<template>
  <Draggable
    v-model="internalCategories"
    :group="{ name: 'categories' }"
    :disabled="!editMode"
    item-key="name"
    @end="handleDragEnd">
    <template #item="{ element }">
      <li class="category-item">
        <div class="category-content">
          <template v-if="editMode && editingItem === element">
            <n-input
              v-model:value="editedName"
              @blur="saveEdit(element)"
              @keyup.enter="saveEdit(element)"/>
            <n-button class="action-button" size="small" round type="success" @click="saveEdit(element)">
              ✔️
            </n-button>
            <n-button class="action-button" size="small" round type="error" @click="cancelEdit">
              ✖️
            </n-button>
          </template>
          <template v-else>
            <router-link :to="{ name: 'CategoriesView', params: { categoryName: element.name } }" 
            class="category-link"
            @click="closeDrawerOnClick">
              <strong>{{ element.name }}</strong>
            </router-link>
          </template>

          <span v-if="editMode && editingItem !== element" class="edit-icon" @click="startEditing(element)">
            <img src="@/assets/pencil.svg" alt="pencil" />
          </span>
        </div>

        <Draggable
          v-model="element.subcategories"
          :group="{ name: 'subcategories' }"
          :disabled="!editMode"
          item-key="name">
          <template #item="{ element: subcategory }">
            <ul>
              <li class="category-item">
                <div class="category-content">
                  <template v-if="editMode && editingItem === subcategory">
                    <n-input
                      v-model:value="editedName"
                      @blur="saveEdit(subcategory)"
                      @keyup.enter="saveEdit(subcategory)"/>

                    <n-button class="action-button" size="small" round type="success" @click="saveEdit(subcategory)">
                      ✔️
                    </n-button>
                    <n-button class="action-button" size="small" round type="error" @click="cancelEdit">
                      ✖️
                    </n-button>
                  </template>
                  <template v-else>
                    <router-link 
                      :to="{ name: 'CategoriesView', params: { categoryName: element.name, subcategoryName: subcategory.name } }"
                      @click="closeDrawerOnClick">
                      <strong>{{ subcategory.name }}</strong>
                    </router-link>
                  </template>

                  <span v-if="editMode && editingItem !== subcategory" class="edit-icon" @click="startEditing(subcategory)">
                    <img src="@/assets/pencil.svg" alt="pencil" />
                  </span>
                </div>
                <Draggable
                  v-model="subcategory.subsubcategories"
                  :group="{ name: 'subsubcategories' }"
                  :disabled="!editMode"
                  item-key="name">
                  <template #item="{ element: subsubcategory }">
                    <ul>
                      <li class="category-item">
                        <div class="category-content">
                          <template v-if="editMode && editingItem === subsubcategory">
                            <n-input
                              v-model:value="editedName"
                              @blur="saveEdit(subsubcategory)"
                              @keyup.enter="saveEdit(subsubcategory)"/>
                            <n-button class="action-button" size="small" round type="success" @click="saveEdit(subsubcategory)">
                              ✔️
                            </n-button>
                            <n-button class="action-button" size="small" round type="error" @click="cancelEdit">
                              ✖️
                            </n-button>
                          </template>
                          <template v-else>
                            <router-link 
                              :to="{ name: 'CategoriesView', params: { categoryName: element.name, subcategoryName: subcategory.name, subsubcategoryName: subsubcategory.name } }"
                              @click="closeDrawerOnClick">
                              <strong>{{ subsubcategory.name }}</strong>
                            </router-link>
                          </template>

                          <span v-if="editMode && editingItem !== subsubcategory" class="edit-icon" @click="startEditing(subsubcategory)">
                            <img src="@/assets/pencil.svg" alt="pencil" />
                          </span>
                        </div>
                        <Draggable
                          v-model="subsubcategory.products"
                          :group="{ name: 'products' }"
                          :move="checkSameList"
                          :disabled="!editMode"
                          item-key="id">
                          <template #item="{ element: product }">
                            <ul>
                              <li>
                                <router-link 
                                  :to="{ name: 'ProductItem', params: { productId: product.id } }"
                                  @click="closeDrawerOnClick">
                                  {{ product.title }}
                                </router-link>
                              </li>
                            </ul>
                          </template>
                        </Draggable>
                      </li>
                    </ul>
                  </template>
                </Draggable>
                <Draggable
                  v-model="subcategory.products"
                  :group="{ name: 'products' }"
                  :move="checkSameList"
                  :disabled="!editMode"
                  item-key="id">
                  <template #item="{ element: product }">
                    <ul>
                      <li>
                        <router-link 
                          :to="{ name: 'ProductItem', params: { productId: product.id } }"
                          @click="closeDrawerOnClick">
                          {{ product.title }}
                        </router-link>
                      </li>
                    </ul>
                  </template>
                </Draggable>
              </li>
            </ul>
          </template>
        </Draggable>

        <Draggable
          v-model="element.productsWithoutSubcategory"
          :group="{ name: 'products' }"
          :move="checkSameList"
          :disabled="!editMode"
          item-key="id">
          <template #item="{ element: product }">
            <ul>
              <li>
                <router-link 
                  :to="{ name: 'ProductItem', params: { productId: product.id } }"
                  @click="closeDrawerOnClick">
                  {{ product.title }}
                </router-link>
              </li>
            </ul>
          </template>
        </Draggable>
      </li>
    </template>
  </Draggable>
</template>

<script setup>
import { defineProps, defineEmits, ref, watch } from 'vue';
import Draggable from 'vuedraggable';
import { NInput, NButton } from 'naive-ui';

const props = defineProps({
  categories: Array,
  editMode: Boolean
});

const emit = defineEmits(['drag-end']);

const internalCategories = ref([...props.categories]);
const editingItem = ref(null);
const editedName = ref('');
const originalName = ref('');

const startEditing = (item) => {
  editingItem.value = item;
  editedName.value = item.name;
  originalName.value = item.name;
};

const saveEdit = (item) => {
  if (editedName.value.trim()) {
    item.name = editedName.value;
    emit('drag-end', internalCategories.value);
  }
  editingItem.value = null;
};

const cancelEdit = () => {
  editingItem.value.name = originalName.value; 
  editingItem.value = null;
};

const handleDragEnd = () => {
  emit('drag-end', internalCategories.value);
};

const checkSameList = (evt) => {
  return evt.from === evt.to;
};
const closeDrawerOnClick = () => {
  emit('close-drawer'); 
};

watch(() => props.categories, (newCategories) => {
  internalCategories.value = [...newCategories];
});
</script>

<style scoped lang="scss">
a {
  text-decoration: none;
  color: white;

  &:focus {
    outline: none;
  }

  &:hover {
    color: rgb(177, 177, 177);
  }
}

li {
  margin-bottom: 8px;
  padding: 8px;
  border-radius: 4px;
  list-style-type: none;
}

ul {
  padding-left: 16px;
}

.category-item {
  position: relative;
}

.category-content {
  display: flex;
  align-items: center;

  .edit-icon {
    display: none;
    cursor: pointer;
    margin-left: 8px;

    img {
      width: 16px;
      height: 16px;
      stroke: #ccc;
    }
  }

  &:hover .edit-icon {
    display: inline-block;
  }
}

.action-button {
  margin-left: 4px;
  padding: 0;
}
</style>
