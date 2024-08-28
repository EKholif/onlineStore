//package com.onlineStore.admin.test.brandTest;
//
//
//import com.onlineStore.admin.brand.reposetry.BrandRepository;
//import com.onlineStore.admin.brand.BrandService;
//import com.onlineStoreCom.entity.brand.Brand;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ExtendWith(MockitoExtension.class)
//@ExtendWith(SpringExtension.class)
//public class BeandServiceTest {
//
//    @MockBean
//    private BrandRepository repository;
//
//    @InjectMocks
//    private BrandService service;
//
//    @Test
//    public void checkExceptionNotFoundExpatiation()  {
//        Long id =500L;
//        String name= "Computers";
//        String alias= "Computers";
//
//
//        Brand cat = new Brand(id,name,alias);
//
//        Mockito.when( repository.findByName(name)).thenReturn(cat);
//
//        String result = service.checkUnique(id,name);
//
//
//        assertThat(result).isEqualTo("Duplicate");
//
//    }
//
//    @Test
//    public void checkUniqeTest()  {
//        Long id =5L;
//        String name= "cam";
//        String alias= "cam1";
//
//
//        Brand cat = new Brand(5L,name,alias);
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
//
//
//
//}
//
//
//
//
//
//
