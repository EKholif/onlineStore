//package com.onlineStore.admin.test.brandTest;
//
//
//import com.onlineStore.admin.brand.reposetry.BrandRepository;
//import com.onlineStoreCom.entity.brand.Brand;
//import com.onlineStoreCom.entity.prodact.Category;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.annotation.Rollback;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@DataJpaTest(showSql = false)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(value = false)
//public class BrandSRepositoryTests {
//
//    @Autowired
//    private BrandRepository repo;
//
//    @Test
//    public void testCreateBrand1() {
//        Category laptops = new Category(6L);
//        Brand acer = new Brand("Acer");
//        acer.getCategories().add(laptops);
//
//        Brand savedBrand = repo.save(acer);
//
//        assertThat(savedBrand).isNotNull();
//        assertThat(savedBrand.getId()).isGreaterThan(0);
//    }
//
//    @Test
//    public void testFindAllMethod(){
//      List<Brand> brand =repo.findAll();
//
//        for ( Brand b :brand  ) {
//            System.out.println(b.getName());
//        }
//
//    }
//
//
//    @Test
//    public void testCreateBrand2() {
//        Category cellphones = new Category(4L);
//        Category tablets = new Category(7L);
//
//        Brand apple = new Brand("Apple");
//        apple.getCategories().add(cellphones);
//        apple.getCategories().add(tablets);
//
//        Brand savedBrand = repo.save(apple);
//
//        assertThat(savedBrand).isNotNull();
//        assertThat(savedBrand.getId()).isGreaterThan(0);
//    }
//
//    @Test
//    public void testCreateBrand3() {
//        Brand samsung = new Brand("Samsung");
//
//        samsung.getCategories().add(new Category(29L));	// category memory
//        samsung.getCategories().add(new Category(24L));	// category internal hard drive
//
//        Brand savedBrand = repo.save(samsung);
//
//        assertThat(savedBrand).isNotNull();
//        assertThat(savedBrand.getId()).isGreaterThan(0);
//    }
//
//
//    @Test
//    public void testFindAll() {
//        Iterable<Brand> brands = repo.findAll();
//        brands.forEach(System.out::println);
//
//        Assertions.assertThat(brands).isNotEmpty();
//    }
//    @Test
//    public void testGetById() {
//        Brand brand = repo.findById(1L).get();
//
//        assertThat(brand.getName()).isEqualTo("Acer");
//    }
//
//
//    @Test
//    public void testUpdateName() {
//        String newName = "Samsung Electronics";
//        Brand samsung = repo.findById(3L).get();
//        samsung.setName(newName);
//
//        Brand savedBrand = repo.save(samsung);
//        assertThat(savedBrand.getName()).isEqualTo(newName);
//    }
//
//    @Test
//    public void testDelete() {
//        long id = 2L;
//        repo.deleteById(id);
//
//        Optional<Brand> result = repo.findById(id);
//
//        Assertions.assertThat(result.isEmpty());
//    }
//
//
//
//}
