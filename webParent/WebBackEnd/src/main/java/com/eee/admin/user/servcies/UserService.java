package com.eee.admin.user.servcies;


import com.eee.admin.role.RoleRepository;
import com.eee.admin.user.UserRepository;
import com.eee.common.entity.Role;
import com.eee.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<User> listAllUsers(){
        return userRepo.findAll();
    }

    public List<Role> listAllRoles (){
        return roleRepo.findAll();
    }

    public void saveUser (User user){
        encodePassword(user);
        userRepo.saveAndFlush(user);
    }

    private void encodePassword(User user){

        String encodePassword= passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
    }

    public boolean isEmailUnique(String email){
        return userRepo.existsByEmail(email);

    }

   }

