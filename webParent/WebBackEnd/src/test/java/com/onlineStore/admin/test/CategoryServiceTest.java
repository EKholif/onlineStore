package com.onlineStore.admin.test;


import com.onlineStore.admin.category.CategoryRepository;
import com.onlineStore.admin.category.services.CategoryService;
import com.onlineStoreCom.entity.prodact.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {

    @MockBean
    private CategoryRepository repository;

    @InjectMocks
    private CategoryService service;

    @Test
    public void checkExceptionNotFoundExpatiation()  {
        Long id =500L;
        String name= "Computers";
        String alias= "Computers";


        Category cat = new Category(id,name,alias);

        Mockito.when( repository.findByName(name)).thenReturn(cat);

        String result = service.checkUnique(id,name,alias);


        assertThat(result).isEqualTo("DuplicateName");

    }

    @Test
    public void checkUniqeTest()  {
        Long id =0L;
        String name= "cam";
        String alias= "cam1";


        Category cat = new Category(5L,name,alias);

        Mockito.when( repository.findByName(name)).thenReturn(cat);
        Mockito.when( repository.findByAlias(alias)).thenReturn(cat);

        String result = service.checkUnique(id,name,alias);

        System.out.println(cat.getId());
        System.out.println(result);

        assertThat(result).isEqualTo("DuplicateName");

    }




}






