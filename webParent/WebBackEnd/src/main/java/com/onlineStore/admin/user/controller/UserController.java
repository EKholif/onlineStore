package com.onlineStore.admin.user.controller;


import com.onlineStore.admin.UsernameNotFoundException;
import com.onlineStore.admin.role.RoleRepository;
import com.onlineStore.admin.user.UserRepository;
import com.onlineStore.admin.user.servcies.UserService;
import com.onlineStore.entity.Role;
import com.onlineStore.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    @GetMapping("/users")
    public ModelAndView listAllUsers() {
        ModelAndView model = new ModelAndView("users");
        List<User> list = userService.listAllUsers();
        model.addObject("users", list);
        return model;
    }

    @GetMapping("/new-user-form")
    public ModelAndView newUserForm() {
        ModelAndView model = new ModelAndView("new-user-form");
        List<Role> listAllRoles = userService.listAllRoles();
        User newUser = new User();
        newUser.setEnable(true);
        model.addObject("user", newUser);
        model.addObject("listAllRoles", listAllRoles);
        model.addObject("pageTitle","Creat new User" );
        model.addObject("saveChanges", "/save-user");
        model.addObject("UserId", 0L);
        return model;

    }

    @PostMapping("/save-user")
    public ModelAndView saveNewUser(@ModelAttribute  User user,
                                    RedirectAttributes redirectAttributes,
                                    @RequestParam("image")MultipartFile multipartFile) throws UsernameNotFoundException {
        redirectAttributes.addFlashAttribute("message", "the user has been saved");
        System.out.println(user);
        System.out.println(multipartFile.getOriginalFilename());

        userService.saveUser(user);
        return new ModelAndView("redirect:/users");
    }



    @GetMapping("/edit/{id}")
    public ModelAndView editUser(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {


        ModelAndView model = new ModelAndView("new-user-form");
        try {
            User user = userService.getUser(id);
            List<Role> listAllRoles = userService.listAllRoles();
            model.addObject("user", user);
            model.addObject("listAllRoles",  user.getRoles()  ) ;
            model.addObject("pageTitle","Edit User (ID: " + id + ")" );
            model.addObject("listAllRoles", listAllRoles);
            model.addObject("saveChanges", "/save-edit-user/"+id);
            model.addObject("UserId", id);
            return model;

        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message  Ehab", ex.getMessage());
            return new ModelAndView("redirect:/users");

        }
    }



    @GetMapping("/searchID/{userId}")
    public ModelAndView searchUser(@PathVariable  (name = "userId") Long id, RedirectAttributes redirectAttributes)  {


        ModelAndView model = new ModelAndView("users");
        try {
            User user = userService.getUser(id);
            model.addObject("users", user);
//            model.addObject("search", "/search/"+id);
//            model.addObject("UserId", id);

            return model;

        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message  Ehab", ex.getMessage());
            return new ModelAndView("redirect:/users");

        }
    }

        @PostMapping( "/save-edit-user/{id}")
    public ModelAndView saveUpdaterUser(@PathVariable Long id,@ModelAttribute  User user, RedirectAttributes redirectAttributes) throws UsernameNotFoundException {
        redirectAttributes.addFlashAttribute("message", "the user has been updated");
        User updateUser =userService.getUser(id);
            if (user.getPassword().isEmpty()) {
                // Copy properties excluding "id" and "password"
                BeanUtils.copyProperties(user, updateUser, "id", "password");
                System.out.println(user.getPassword());
            } else {
                // Copy properties excluding "id"
                BeanUtils.copyProperties(user, updateUser, "id");
            }

            userService.saveUser(updateUser);
        return new ModelAndView("redirect:/users");
    }


    @GetMapping ("/delete-user/{id}")
        public ModelAndView deleteUser(@PathVariable (name = "id")Long id, RedirectAttributes redirectAttributes) {

        try {
            if(userRepository.existsById(id)){
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "the user ID: "+ id+" has been Deleted");
            } else {
                redirectAttributes.addFlashAttribute("message", "the user ID: "+ id+" user Not Found");

            }return new ModelAndView("redirect:/users");
        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", " user Not Found");
            return new ModelAndView("redirect:/users");
        }
       }

       @GetMapping("/user/{id}/enable/{status}")
    public ModelAndView UpdateUserStatus (@PathVariable("id")Long id, @PathVariable("status") boolean enable,
                                    RedirectAttributes redirectAttributes){
        userService.UdpateUserEnableStatus(id,enable);
        String status = enable ? "enable" :" disable";
        String message = " the user Id  " + id +"has bean  " +status ;
        redirectAttributes.addFlashAttribute("message", message);
        return new ModelAndView( "redirect:/users");

       }
}
