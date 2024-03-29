package com.onlineStore.admin.test;

import com.onlineStore.admin.user.role.RoleRepository;
import com.onlineStoreCom.entity.users.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;




@DataJpaTest
@AutoConfigureTestDatabase(replace =Replace.NONE)
@Rollback(false)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository repo;

    @Test
    public void testListAllUser(){

        Iterable<Role> listUsers =repo.findAll();

        for (Role role:listUsers) {
            System.out.println(role.getId() + " --" + role.getName() );
        }
        
    }



    @Test
    public void testCreateFirstRole() {
        Role roleAdmin = new Role("Admin", "manage everything");
        Role savedRole = repo.saveAndFlush(roleAdmin);

        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateFirstRoles() {
        Role roleSalesperson = new Role("Salesperson", "manage product price, "
                + "customers, shipping, orders and sales report");

        Role roleEditor = new Role("Editor", "manage categories, brands, "
                + "products, articles and menus");

        Role roleShipper = new Role("Shipper", "view products, view orders "
                + "and update order status");

        Role roleAssistant = new Role("Assistant", "manage questions and reviews");
        repo.saveAllAndFlush(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));
    }


    @Test
    public void testDeleteRole() {
        repo.deleteById(1L);
        repo.deleteById(2L);
        repo.deleteById(3L);
        repo.deleteById(4L);
        repo.deleteById(5L);

       testListAllUser();
    }
}

