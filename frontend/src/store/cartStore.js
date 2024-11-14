import { defineStore } from 'pinia';
import { ref } from 'vue';
import api from '@/services/api.js';

export const useCartStore = defineStore('cart', () => {
  const cartItems = ref([]);

  const fetchCart = async (token) => {
    try {
      const response = await api.get(`/basket/myBasket`,
        {
          headers: {
            Authorization: token,
          },
        }
      );
      cartItems.value = response.data.map((item) => {
        return {
          ...item,
          imageUrl: item.base64Image
            ? `data:image/png;base64,${item.base64Image}`
            : null,
        };
      });

      return cartItems.value;
    } catch (error) {
      console.error("Ошибка получения корзины:",error.response.data);
    }
  };

  const addToCart = async (productId, token) => {
    try {
      const response = await api.post(`/basket/addToBasket`, null,
        {
          params: { productId },
          headers: { Authorization: token },
        });
      return response.data;
    } catch (error) {
      console.error(error);
    }
  };
  const removeFromCart = async (productId, token) => {
    try {
      const existingItem = cartItems.value.find((i) => i.id === productId);
      if (existingItem) {
        if (existingItem.count > 1) {
          existingItem.count--;
        } else {
          cartItems.value = cartItems.value.filter((i) => i.id !== productId);
        }
        await api.post(`/basket/delete/${productId}`,null,
          {
            headers: {
              Authorization: token,
            },
          }
        );
      }
      return existingItem;
    } catch (error) {
      console.error('Ошибка удаления товара из корзины:', error.response.data);
    }
  };

  const removeFromCartAll = async (productId, token) => {
    try {
      await api.post(`/basket/deleteFull/${productId}`, null,
        {
          params: {
            productId,
          },
          headers: {
            Authorization: token,
          },
        }
      );
    } catch (error) {
      console.error('товар не крякнул' + error);
    }
  };

  const getTotalCount = () => {
    return cartItems.value.reduce((total, item) => total + item.count, 0);
  };
  return {
    cartItems,
    fetchCart,
    addToCart,
    removeFromCart,
    getTotalCount,
    removeFromCartAll,
  };
});
