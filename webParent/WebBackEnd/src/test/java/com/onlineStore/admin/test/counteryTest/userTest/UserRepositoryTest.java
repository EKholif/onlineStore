package com.onlineStore.admin.test.counteryTest.userTest;

import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStore.admin.usersAndCustomers.users.role.RoleRepository;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStoreCom.entity.users.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class UserRepositoryTest {

    @Autowired
    private UserRepository repo;
    @Autowired
    private RoleRepository roleRepository;


    @Test
    public void testCreateFirstUser() {
        User userAdmin = new User("e@gmail.com", "0", "ehab", "kholif");
        userAdmin.addRole(roleRepository.getReferenceById(1L));
        userAdmin.setEnable(true);

        User savedRole = repo.saveAndFlush(userAdmin);
        System.out.println(userAdmin.getEmail());

        System.out.println(userAdmin);

        testUser();
        assertThat(savedRole.getId()).isGreaterThan(0);

    }
    @Test
    public void tateFirstUser() {
     User user = repo.findByEmail("aecllc.bnk@gmail.com");
        System.out.println(user.getId());
     user.setPassword("");
        System.out.println(user.getPassword());
    }

    @Test
    public void testCreateUsers()
     {
        User userSalesperson = new User("ehab.bts@gmail.com", "a0000", "ehab", "kholif");
        userSalesperson.addRole(roleRepository.getReferenceById(2L));

        User userEditor = new User("ehab@gmail.com", "0a000", "ekholif", "kholif");
        userEditor.addRole(roleRepository.getReferenceById(3L));

        User userShipper = new User("bts@gmail.com", "00a00", "testUserOne", "kholif");
        userShipper.addRole(roleRepository.getReferenceById(4L));

        User userAssistant = new User("eh.bts@gmail.com", "000a0", "testUserTwo", "kholif");
        userAssistant.addRole(roleRepository.getReferenceById(5L));

         testUser();

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
        userAdmin.setPassword("");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword= passwordEncoder.encode(userAdmin.getPassword());
        userAdmin.setPassword(encodePassword);
        repo.saveAndFlush(userAdmin);
        System.out.println(userAdmin);
    }



    @Test
    public void testCountById(){
        Long id =22L;
        Long countBYId = repo.countById(id);
        assertThat(countBYId).isNotNull().isGreaterThan(0);

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

        Long id = 1L;
        repo.deleteById(id);

        testUser();
    }

    @Test
    public void testUpdateRoleUser() {
        User userAdmin = repo.findById(3103L).get();
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
        User UserByEmail = repo.findByEmail("ehabffkholif@gmail.com");
        boolean user = (UserByEmail==null);
        System.out.println(user);
    }

    @Test
    public void  testNewid (){
        User newUser = new User();
        System.out.println(newUser.getId());

    }


    @Test
    public void testUserExistsByEmail() {
        boolean UserByEmail = repo.existsByEmail("ehab.bts@gmail.com");
        System.out.println(UserByEmail);
    }

    @Test
    public void testFindUserById () {
        try {
            User userFindById = repo.findById(2L).get();
            System.out.println(userFindById);

        }catch (NoSuchElementException ex){

            System.out.println("null");
        }
    }

    @Test
    public void testEnableUser(){
        Long id = 1152L;
        repo.enableUser(id,true);
    }

    @Test
    public void testGetUserImagePath(){

        User user = repo.getReferenceById(3L);

        System.out.println( user.getImagePath());
        System.out.println( user.getUser_bio());



    }

    @Test
    public void testUnitBean(){
        User user = repo.getReferenceById(3252L);
        User userAdmin =new User();
        userAdmin.addRole(roleRepository.getReferenceById(1L));


        BeanUtils.copyProperties( userAdmin,user, "id");
        System.out.println(userAdmin.getRoles());
        System.out.println(user.getRoles());
    }

    @Test
    public void testCleanDir() throws IOException {
        User user = repo.getReferenceById(3L);
        FileUploadUtil.cleanDir(user.getImageDir());
    }

    @Test
    public void testPaging(){
        int pageNumber = 1;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(pageable);
        List<User> contant= page.getContent();
        contant.forEach(System.out::println);


    }

    @Test
    public void testUserServPageing(){
   String keyword = "bruce";
   int pageNum = 0;
   int pagSize = 6;
   Pageable pageable =PageRequest.of(pageNum,pagSize);
   Page<User> page=repo.findAll(keyword, pageable);
   List<User>listUsers=page.getContent();
        System.out.println(listUsers);




    }
    @Test
    public void testUserSeachPageing(){

     Long id = 20L;
        User user = repo.getReferenceById(20L);
        String encodedPassword = "'$2a$10$1pu/o2S4kwAQ3aFWvIAW9eEnbVgOq0/SebsHLe7BsyZYJ9mSOdRpG'";

        user.setPassword(encodedPassword);

        System.out.println(user.getPassword());
    }

    @Test
    public void testUser(){

        Iterable<User> listUsers =repo.findAll();

        for (User user:listUsers) {
            System.out.println(user.getId() + " --" + user.getfirstName() + "-- "
                    + user.getEmail()+"--"+user.isEnable()+"--"+user.getPassword());
        }

}

}