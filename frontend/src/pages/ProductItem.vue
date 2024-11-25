<template>
  <n-spin
    content-style="--n-opacity-spinning:0; height: 100vh;"
    stroke="blue"
    :show="loader">
    <div v-if="product">
      <div class="product">
        <h2 class="product__breadcrumb">
          <router-link
            class="product__breadcrumb__link"
            :to="{
              name: 'CategoriesView',
              params: { categoryName: productCategory },
            }"
          >
            {{ productCategory }}
          </router-link>
          /
          <span v-if="productSubcategory !== 'None'">
            <router-link
              class="product__breadcrumb__link"
              :to="{
                name: 'CategoriesView',
                params: {
                  categoryName: productCategory,
                  subcategoryName: productSubcategory,
                },
              }"
            >
              {{ productSubcategory }}
            </router-link>
            /
          </span>
          <span v-if="productSubsubcategory !== 'None'">
            <router-link
              class="product__breadcrumb__link"
              :to="{
                name: 'CategoriesView',
                params: {
                  categoryName: productCategory,
                  subcategoryName: productSubcategory,
                  subsubcategoryName: productSubsubcategory,
                },
              }"
            >
              {{ productSubsubcategory }}
            </router-link>
            /
          </span>
          {{ product.title }}
        </h2>
        <div class="product__item">
          <n-card
            class="product__item__card"
            content-style="display: flex; flex-direction: row;">
            <div class="product__item__wrapper">
              <img
                v-if="product.imageUrl"
                :src="product.imageUrl"
                class="product__item__img"
                alt="Product Image"
              />
            </div>
            <div class="product__info">
              <h1 class="product__title">{{ product.title }}</h1>
              <p class="product__description">{{ product.description }}</p>
              <div class="product__pay">
                <span v-if="isAuthenicated">
                  Цена: <b>{{ product.discountPrice }} руб.</b>
                  <del style="margin-left: 10px">{{ product.price }} руб.</del>
                </span>
                <span v-else>Цена: {{ product.price }}</span>
                <span v-if="product.count > 0"
                  >Количество товаров осталось: <b>{{ product.count }}</b></span
                >
                <span v-else><b>Товара нет на складе</b></span>
                <BasketButton
                  v-if="product"
                  :product-id="product.id"
                  :product="product"
                />
              </div>
            </div>
          </n-card>
        </div>
        <ProductsComment
          :comments="product.comments"
          :product-id="product.id"
        />
      </div>
    </div>
  </n-spin>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import { NCard, NSpin } from 'naive-ui';
import ProductsComment from '@/components/ProductsComment.vue';
import BasketButton from '@/components/BasketButton.vue';
import api from '@/services/api.js';

const route = useRoute();
const product = ref(null);

const isAuthenicated = ref(false);

const loader = computed(() => !product.value);
const productCategory = computed(() => {
  return product.value?.categories?.[0]?.name || "Unknown";
});

const productSubcategory = computed(() => {
  return product.value?.categories?.[0]?.subcategory || "None";
});

const productSubsubcategory = computed(() => {
  return product.value?.categories?.[0]?.subsubcategory || "None";
});

const fetchProduct = async (id) => {
  try {
    const response = await api.get(`/product/getAll/${id}`);
    const productData = response.data;

    if (productData.base64Image) {
      productData.imageUrl = `data:image/png;base64,${productData.base64Image}`;
    }

    product.value = productData;
  } catch (error) {
    console.error("продукт не получили:", error);
  }
};

const checkAuth = () => {
  const token = localStorage.getItem("token");
  if (token) {
    isAuthenicated.value = true;
  }
};

watch(
  () => route.params.productId,
  (newId) => {
    if (newId) {
      fetchProduct(newId);
    }
  }
);

onMounted(() => {
  checkAuth();
  const productId = route.params.productId;
  fetchProduct(productId);
});
</script>

<style lang="scss" scoped>
.product{
  margin-top: 5%;
    h2 {
      margin: 10px 20px;
    }
    &__breadcrumb {
      margin-bottom: 20px;

        &__link {
        text-decoration: none;
        color: inherit;
          &:hover {
            color: gray;
          }
      }
    }
    
    &__item {
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-items: center; 

      &__card {
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      }

      &__wrapper {
        flex: 1;
        margin-right: 30px;
        margin-top: 10px;
      }
      &__img {
      width: 350px;
      object-fit: cover;
    }
  }

  &__info {
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  &__title {
    font-size: 28px;
    margin-bottom: 15px;
    color: #333;
  }
  &__pay {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  &__description {
    font-size: 16px;
    color: #666;
    margin-bottom: 20px;
  }
  .n-button {
    align-self: flex-start;
    width: 100%;
  }
  .n-card {
    width: 1000px;
  }
}
</style>
