package com.onlineStore.admin.usersAndCustomers.users.servcies;


import com.onlineStore.admin.UsernameNotFoundException;
import com.onlineStore.admin.category.services.PageInfo;
import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStore.admin.usersAndCustomers.users.role.RoleRepository;
import com.onlineStoreCom.entity.users.Role;
import com.onlineStoreCom.entity.users.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional

public class UserService {

    public static final int USERS_PER_PAGE = 5;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<User> listAllUsers() {
        return userRepo.findAll();
    }


    public List<Role> listAllRoles() {
        return roleRepo.findAll();
    }

    public User saveUser(User user) {
        encodePassword(user);
        return userRepo.saveAndFlush(user);
    }

    public User saveUpdatededUser(User user) {
        return userRepo.saveAndFlush(user);
    }


    public List<User> listByPage(PageInfo pageInfo, int pageNum, String sortField, String sortDir, String keyword) {
        Pageable pageable = createPageable(pageNum, sortField, sortDir);

        Page<User> pageUsers = (keyword != null) ? userRepo.findAll(keyword, pageable) : userRepo.findAll(pageable);

        setPageInfo(pageInfo, pageUsers);

        return pageUsers.getContent();
    }

    private Pageable createPageable(int pageNum, String sortField, String sortDir) {
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        return PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
    }

    private void setPageInfo(PageInfo pageInfo, Page<?> page) {
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPages(page.getTotalPages());
    }


    private void encodePassword(User user) {

        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
    }


    public boolean isEmailUnique(Long id, String email) {
        User userByEmail = userRepo.findByEmail(email);

        return userByEmail == null || userByEmail.getId().equals(id);
    }


    public User getUser(Long id) throws UsernameNotFoundException {
        try {
            return userRepo.findById(id).get();

        } catch (NoSuchElementException ex) {

            throw new UsernameNotFoundException("Could not find any user with ID " + id);
        }
    }

    public Boolean existsById(Long id) {
        return userRepo.findById(id).isPresent();
    }


    public void deleteUser(Long id) throws UsernameNotFoundException {
        try {
            userRepo.deleteById(id);

        } catch (NoSuchElementException ex) {

            throw new UsernameNotFoundException("Could not find any user with ID " + id);
        }
    }

    public void UdpateUserEnableStatus(Long id, Boolean enable) {
        userRepo.enableUser(id, enable);

    }
}


