package com.eee.admin.test;

import com.eee.admin.role.RoleRepository;
import com.eee.admin.user.UserRepository;
import com.eee.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class UserRepositoryTest {

    @Autowired
    private UserRepository repo;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateFirstUser() {
        User userAdmin = new User("ehabkholif@gmail.com", "0000", "ehab", "kholif");
        userAdmin.addRole(roleRepository.getReferenceById(1L));
        User savedRole = repo.saveAndFlush(userAdmin);
        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateUsers() {
        User userSalesperson = new User("ehab.bts@gmail.com", "a0000", "ehab", "kholif");
        userSalesperson.addRole(roleRepository.getReferenceById(2L));

        User userEditor = new User("ehab@gmail.com", "0a000", "ekholif", "kholif");
        userSalesperson.addRole(roleRepository.getReferenceById(3L));

        User userShipper = new User("bts@gmail.com", "00a00", "testUserOne", "kholif");
        userSalesperson.addRole(roleRepository.getReferenceById(4L));

        User userAssistant = new User("eh.bts@gmail.com", "000a0", "testUserTwo", "kholif");
        userSalesperson.addRole(roleRepository.getReferenceById(5L));

        repo.saveAllAndFlush(List.of(userSalesperson, userEditor, userShipper, userAssistant));

    }

    @Test
    public void testListAllUser(){

        Iterable<User> listUsers =repo.findAll();
        listUsers.forEach(System.out::println);
    }

    @Test
    public void testGetUserById(){

     User userAdmin = repo.findById(1L).get();
        System.out.println(userAdmin);
    }
    @Test
    public void testUpdateUser(){
        User userAdmin = repo.findById(1L).get();
        userAdmin.setEnable(true);
        userAdmin.setPassword("1111L");
        repo.saveAndFlush(userAdmin);
        System.out.println(userAdmin);
    }

    @Test
    public void testCreateTestUser() {
        User userAdmin = new User("testDeleteUser@gmail.com", "0000", "ehab", "kholif");
        userAdmin.addRole(roleRepository.getReferenceById(1L));
        User savedRole = repo.saveAndFlush(userAdmin);
        assertThat(savedRole.getId()).isGreaterThan(0);
    }
    @Test
    public void testDeleteUser() {

        Long userId = 10L;
        repo.deleteById(userId);
    }

    @Test
    public void testUpdateRoleUser() {
        User userAdmin = repo.findById(9L).get();
        userAdmin.addRole(roleRepository.getReferenceById(1L));
        repo.saveAndFlush(userAdmin);
        userAdmin.addRole(roleRepository.getReferenceById(3L));
        repo.saveAndFlush(userAdmin);
        userAdmin.addRole(roleRepository.getReferenceById(4L));
        repo.saveAndFlush(userAdmin);

    }

    @Test
    public void testRemoveRoleUser() {
        User userAdmin = repo.findById(9L).get();
        userAdmin.getRoles().remove(roleRepository.getReferenceById(3L));
        userAdmin.getRoles().remove(roleRepository.getReferenceById(1L));
        repo.saveAndFlush(userAdmin);

    }

    @Test
    public void testFindUserByEmail() {
        User UserByEmail = repo.findByEmail("ehabkholif@gmail.com");
        System.out.println(UserByEmail.getId());
    }

    @Test
    public void testUserExistsByEmail() {
        boolean UserByEmail = repo.existsByEmail("ehabkholif@2gmail.com");
        System.out.println(UserByEmail);
    }


}