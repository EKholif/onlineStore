package com.onlineStore.admin.user.servcies;


import com.onlineStore.admin.UsernameNotFoundException;
import com.onlineStore.admin.role.RoleRepository;
import com.onlineStore.admin.user.UserRepository;
import com.onlineStoreCom.entity.Role;
import com.onlineStoreCom.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional

public class UserService {

    public static final int USERS_PER_PAGE=4;

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

    public User saveUser (User user)   {
        encodePassword(user);
       return userRepo.saveAndFlush(user);

    }

    public Page<User> listByPage(int pageNum){

        Pageable pageable = PageRequest.of(pageNum-1 ,USERS_PER_PAGE);
        System.out.println(pageNum);
        return userRepo.findAll(pageable);


    }



    private void encodePassword(User user){

        String encodePassword= passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
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

public void UdpateUserEnableStatus (Long id, Boolean enable){
        userRepo.enableUser(id, enable);

}




}


