export const useOrganizeProducts = () => {
  const organizeProductsByCategories = (products) => {
    const categoryMap = new Map();

    products.forEach((product) => {
      product.categories.forEach((cat) => {
        let category = categoryMap.get(cat.name);
        if (!category) {
          category = {
            name: cat.name,
            order: cat.order || 0, 
            subcategories: [],
            productsWithoutSubcategory: [],
          };
          categoryMap.set(cat.name, category);
        }

        if (!cat.subcategory || cat.subcategory === "") {
          category.productsWithoutSubcategory.push(product);
        } else {
          let subcategory = category.subcategories.find(
            (sub) => sub.name === cat.subcategory
          );
          if (!subcategory) {
            subcategory = {
              name: cat.subcategory,
              order: cat.subcategoryOrder || 0, 
              products: [],
              subsubcategories: [],
            };
            category.subcategories.push(subcategory);
          }

          if (cat.subsubcategory) {
            let subsubcategory = subcategory.subsubcategories.find(
              (subsub) => subsub.name === cat.subsubcategory
            );
            if (!subsubcategory) {
              subsubcategory = {
                name: cat.subsubcategory,
                order: cat.subsubcategoryOrder || 0, 
                products: [],
              };
              subcategory.subsubcategories.push(subsubcategory);
            }
            subsubcategory.products.push(product);
          } else {
            subcategory.products.push(product);
          }
        }
      });
    });

    const sortedCategories = Array.from(categoryMap.values()).sort(
      (a, b) => a.order - b.order
    );

    sortedCategories.forEach((category) => {
      category.subcategories.sort((a, b) => a.order - b.order);
      category.subcategories.forEach((subcategory) => {
        subcategory.subsubcategories.sort(
          (a, b) => a.order - b.order
        );
      });
    });

    return sortedCategories;
  };

  return {
    organizeProductsByCategories,
  };
};
