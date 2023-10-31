package com.eee.admin.user.servcies;


import com.eee.admin.UsernameNotFoundException;
import com.eee.admin.role.RoleRepository;
import com.eee.admin.user.UserRepository;
import com.eee.common.entity.Role;
import com.eee.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

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

     public User findUserById (Long id) {
        try {
            User userFindById = userRepo.findById(id).get();
            return userFindById;
        } catch (NoSuchElementException ex){
            return null;
        }

     }



    public boolean isEmailUnique(Long id, String email) {
        User userByEmail = userRepo.findByEmail(email);

        if (userByEmail == null) return true;
        boolean isCreatingNew = (id == 0);

        if (isCreatingNew) {
            if (userByEmail != null) return false;
        } else {
            if (userByEmail.getId() == id) {
                return true;
            }
        }
return true;
    }

    public User getUser( Long userId) throws UsernameNotFoundException{
        try {
        return userRepo.findById(userId).get();

        }catch (NoSuchElementException ex){

            throw new UsernameNotFoundException("Could not find any user with ID " + userId);
    }
}


public void deleteUser(Long userId) throws UsernameNotFoundException{
    try {
        userRepo.deleteById(userId);
    }catch (NoSuchElementException ex){

        throw new UsernameNotFoundException("Could not find any user with ID " + userId);
    }
}


 }
