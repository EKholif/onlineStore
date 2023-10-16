package com.eee.admin.user.servcies;


import com.eee.admin.user.UserRepository;
import com.eee.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;


    public List<User> listAll (){
        return repo.findAll();
    }
}
