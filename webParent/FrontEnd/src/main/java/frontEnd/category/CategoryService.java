package frontEnd.category;


import com.onlineStoreCom.entity.category.Category;
import frontEnd.product.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService {


    public static final Long USERS_PER_PAGE = 5L;
    @Autowired
    private CategoryRepository repo;

    public List<Category> listNoChildrenCategories() {
        List<Category> listNoChildrenCategories = new ArrayList<>();

        List<Category> listEnabledCategories = repo.findAllEnabled();

        listEnabledCategories.forEach(category -> {
            Set<Category> children = repo.getChildren(category);

            if (children == null || children.isEmpty()) {
                listNoChildrenCategories.add(category);
            }
        });

        return listNoChildrenCategories;
    }


    public List<Category> listNoParentCategories() {

//        List<Category> listNoParentCategories = new ArrayList<>();

        List<Category> listEnabledCategories = repo.findAllEnabled();

        List<Category> listNoParentCategories =  repo.notParentCategory();


        return listNoParentCategories;
    }





    public Category getCategory(String alias) throws CategoryNotFoundException {

        Category category = repo.findByAliasParent(alias);

        // If it's null, then try the 'findByAliasEnabled' method
        if (category == null) {
            category = repo.findByAliasEnabled(alias);
        }

        // If still null, throw the exception
        if (category == null) {
            throw new CategoryNotFoundException("Could not find any categories with alias " + alias);
        }

        return category;
    }

    public List<Category> getCategoryParents(Category child) {

        List<Category> listParents = new ArrayList<>();

        Category parent = repo.getParent(child);

        while (parent != null) {
            listParents.add(0, parent);
            parent = repo.getParent(parent);
        }
//        listParents.add(child);
        return listParents;
    }


    public Set<Category> listCategoryChildren(Category category) {

        Set<Category> listCategoryChildren = repo.getChildren(category);

        return listCategoryChildren;
    }
}

