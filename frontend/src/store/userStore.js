import { ref } from 'vue';
import api from '@/services/api.js';

export const useUserStore = () => {
  const user = ref(null);
  const role = ref(null);

  const decodeToken = (token) => {
    try {
      const base64Url = token.split(".")[1];
      const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => "%" + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      const decoded = JSON.parse(jsonPayload);
      user.value = {
        id: decoded.id,
        email: decoded.email,
        ...decoded,
      };

      if (Array.isArray(decoded.roles)) {
        role.value = decoded.roles[0] || null;
      } else {
        console.error('Roles is not an array');
        role.value = null;
      }

      return decoded;
    } catch (error) {
      console.error('Error decoding token:', error);
      user.value = null;
      role.value = null;
      return {};
    }
  };

  const fetchUser = () => {
    const token = localStorage.getItem("token");
    if (token) {
      decodeToken(token);
    } else {
      user.value = null;
      role.value = null;
    }
  };

  const login = async (email, password) => {
    try {
      const response = await api.post(`/user/login`, {email, password});
      const token = response.data.token;
      localStorage.setItem("token", token);
      fetchUser();
    } catch (error) {
      console.error(error);
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    user.value = null;
    role.value = null;

  };

  return { user, fetchUser, login, logout, role };
};
