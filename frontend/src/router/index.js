import { createRouter, createWebHashHistory } from 'vue-router';
const routes = [
  {
    path: "/",
    name: "MainPage",
    component: () => import('@/pages/MainPage.vue')
  },
  {
    path: "/cart",
    name: "BasketPage",
    component: () => import('@/pages/AppBasket.vue')
  },
  {
    path: "/registration",
    name: "Registrantion",
    component: () => import('@/pages/RegistrantionPage.vue')
  },
  {
    path: "/signin",
    name: "SignIn",
    component: () => import('@/pages/SignPage.vue')
  },
  {
    path: "/category/:categoryName/:subcategoryName?/:subsubcategoryName?",
    name: "CategoriesView",
    component: () => import('@/pages/CategoriesView.vue'),
    props: true,
  },
  {
    path: "/product-view/:productId",
    name: "ProductItem",
    component: () => import('@/pages/ProductItem.vue'),
    props: true,
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
