package com.onlineStore.admin.test.productTest;//package com.onlineStore.admin.test.brandTest;
//

import com.onlineStore.admin.product.repository.ProductRepository;
import com.onlineStore.admin.product.service.ProductService;
import com.onlineStoreCom.entity.product.Product;
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
public class ProductServiceTest {

    @MockBean
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Test
    public void checkExceptionNotFoundExpatiation()  {
        int id =500;
        String name= "Computers";
        String alias= "Computers";


        Product cat = new Product(400,name,alias);

        Mockito.when( repository.findByName(name)).thenReturn(cat);

        String result = service.checkUnique(id,name,alias);


        assertThat(result).isEqualTo("DuplicateName");

    }

//    @Test
//    public void checkUniqeTest()  {
//        int id =5;
//        String name= "cam";
//        String alias= "cam1";
//
//
//        Product cat = new Product(5,name,alias);
//
//        Mockito.when( repository.findByName(name)).thenReturn(cat);
//
//        String result = service.checkUnique(id,name);
//
//        System.out.println(cat.getId());
//        System.out.println(result);
//
//        assertThat(result).isEqualTo("Duplicate");
//
//    }
//



}






