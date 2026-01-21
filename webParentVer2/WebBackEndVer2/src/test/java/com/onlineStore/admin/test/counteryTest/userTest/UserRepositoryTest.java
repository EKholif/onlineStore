package com.onlineStore.admin.test.counteryTest.userTest;

import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStoreCom.entity.users.User;
import com.onlineStoreCom.tenant.TenantContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)
class UserRepositoryTest {

    @Autowired
    private UserRepository repo;
    @Autowired
    private org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager entityManager;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        TenantContext.setTenantId(1L);
    }

    private com.onlineStoreCom.entity.users.Role getOrCreateRole(Integer id, String name, String desc) {
        // Since id is manual, we might need to check if exists or just persist new one
        // if possible
        // But Role entities might have manually assigned IDs.
        // If we use entityManager to find, we can check.
        com.onlineStoreCom.entity.users.Role role = entityManager.find(com.onlineStoreCom.entity.users.Role.class, id);
        if (role == null) {
            role = new com.onlineStoreCom.entity.users.Role();
            role.setId(id);
            role.setName(name);
            role.setDescrption(desc);
            entityManager.persist(role);
        }
        return role;
    }

    private User createAndSaveUser(String email) {
        // Ensure role exists
        com.onlineStoreCom.entity.users.Role role = getOrCreateRole(1, "Admin", "Admin Role");
        User user = new User(email, "password", "First", "Last");
        user.addRole(role);
        user.setEnabled(true);
        return repo.saveAndFlush(user);
    }

    @Test
    public void testCreateFirstUser() {
        com.onlineStoreCom.entity.users.Role role = getOrCreateRole(1, "Admin", "Admin Role");
        User userAdmin = new User("test1_" + System.currentTimeMillis() + "@gmail.com", "password", "ehab", "kholif");
        userAdmin.addRole(role);
        userAdmin.setEnabled(true);

        User savedUser = repo.saveAndFlush(userAdmin);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testUpdateUserPassword() {
        String email = "findme_" + System.currentTimeMillis() + "@gmail.com";
        createAndSaveUser(email);

        User user = repo.findByEmail(email);
        assertThat(user).isNotNull();
        System.out.println(user.getId());
        user.setPassword("newpass");
        System.out.println(user.getPassword());
    }

    @Test
    public void testCreateUsers() {
        com.onlineStoreCom.entity.users.Role r2 = getOrCreateRole(2, "Salesperson", "Sales");
        com.onlineStoreCom.entity.users.Role r3 = getOrCreateRole(3, "Editor", "Editor");
        com.onlineStoreCom.entity.users.Role r4 = getOrCreateRole(4, "Shipper", "Shipper");
        com.onlineStoreCom.entity.users.Role r5 = getOrCreateRole(5, "Assistant", "Assistant");

        User userSalesperson = new User("sales_" + System.currentTimeMillis() + "@gmail.com", "pass", "Name1", "Last");
        userSalesperson.addRole(r2);

        User userEditor = new User("editor_" + System.currentTimeMillis() + "@gmail.com", "pass", "Name2", "Last");
        userEditor.addRole(r3);

        User userShipper = new User("shipper_" + System.currentTimeMillis() + "@gmail.com", "pass", "Name3", "Last");
        userShipper.addRole(r4);

        User userAssistant = new User("assist_" + System.currentTimeMillis() + "@gmail.com", "pass", "Name4", "Last");
        userAssistant.addRole(r5);

        repo.saveAllAndFlush(List.of(userSalesperson, userEditor, userShipper, userAssistant));

        assertThat(repo.findByEmail(userSalesperson.getEmail())).isNotNull();
    }

    @Test
    public void testListAllUser() {
        createAndSaveUser("listuser_" + System.currentTimeMillis() + "@gmail.com");
        Iterable<User> listUsers = repo.findAll();
        assertThat(listUsers.iterator().hasNext()).isTrue();
    }

    @Test
    public void testUpdateUser() {
        User user = createAndSaveUser("updateuser_" + System.currentTimeMillis() + "@gmail.com");
        Integer id = user.getId();

        User retrieved = repo.findById(id).get();
        retrieved.setEnabled(false);
        retrieved.setPassword("updatedPass");

        User updated = repo.saveAndFlush(retrieved);
        assertThat(updated.isEnabled()).isFalse();
    }

    @Test
    public void testCountById() {
        User user = createAndSaveUser("countuser_" + System.currentTimeMillis() + "@gmail.com");
        Integer count = repo.countById(user.getId());
        assertThat(count).isGreaterThan(0);
    }

    @Test
    public void testDeleteUser() {
        User user = createAndSaveUser("deleteuser_" + System.currentTimeMillis() + "@gmail.com");
        Integer id = user.getId();
        repo.deleteById(id);
        assertThat(repo.findById(id)).isEmpty();
    }

    @Test
    public void testUpdateRoleUser() {
        com.onlineStoreCom.entity.users.Role r1 = getOrCreateRole(1, "Admin", "Admin");
        com.onlineStoreCom.entity.users.Role r3 = getOrCreateRole(3, "Editor", "Editor");
        com.onlineStoreCom.entity.users.Role r4 = getOrCreateRole(4, "Shipper", "Shipper");

        User user = new User("roleuser_" + System.currentTimeMillis() + "@gmail.com", "pass", "N", "L");
        user = repo.saveAndFlush(user);

        user.addRole(r1);
        repo.saveAndFlush(user);

        user = repo.findById(user.getId()).get();
        user.addRole(r3);
        repo.saveAndFlush(user);

        user = repo.findById(user.getId()).get();
        user.addRole(r4);
        repo.saveAndFlush(user);

        assertThat(user.getRoles().size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    public void testRemoveRoleUser() {
        com.onlineStoreCom.entity.users.Role r1 = getOrCreateRole(1, "Admin", "Admin");
        User user = new User("removerole_" + System.currentTimeMillis() + "@gmail.com", "pass", "N", "L");
        user.addRole(r1);
        user = repo.saveAndFlush(user);

        user.getRoles().remove(r1);
        user = repo.saveAndFlush(user);

        assertThat(user.getRoles().size()).isEqualTo(0);
    }

    @Test
    public void testFindUserByEmail() {
        String email = "findemail_" + System.currentTimeMillis() + "@gmail.com";
        createAndSaveUser(email);
        User user = repo.findByEmail(email);
        assertThat(user).isNotNull();
    }

    @Test
    public void testUserExistsByEmail() {
        String email = "existemail_" + System.currentTimeMillis() + "@gmail.com";
        createAndSaveUser(email);
        boolean exists = repo.existsByEmail(email);
        assertThat(exists).isTrue();
    }

    @Test
    public void testGetUserImagePath() {
        User user = createAndSaveUser("imagepath_" + System.currentTimeMillis() + "@gmail.com");
        user.setUser_bio("test.jpg");
        repo.saveAndFlush(user);

        user = repo.getReferenceById(user.getId());
        assertThat(user.getImagePath()).isNotNull();
    }

    @Test
    public void testUnitBean() {
        // Just testing bean copy, no DB needed really, but using objects
        com.onlineStoreCom.entity.users.Role r1 = getOrCreateRole(1, "Admin", "Admin");
        User source = new User();
        source.addRole(r1);

        User target = new User();
        // Simulate copy except id
        BeanUtils.copyProperties(source, target, "id");

        assertThat(target.getRoles().contains(r1)).isTrue();
    }

    @Test
    public void testPaging() {
        createAndSaveUser("page1_" + System.currentTimeMillis() + "@gmail.com");
        createAndSaveUser("page2_" + System.currentTimeMillis() + "@gmail.com");

        Pageable pageable = PageRequest.of(0, 2);
        Page<User> page = repo.findAll(pageable);
        assertThat(page.getContent().size()).isGreaterThan(0);
    }
}