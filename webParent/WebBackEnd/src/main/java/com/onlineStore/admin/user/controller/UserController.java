package com.onlineStore.admin.user.controller;


import com.onlineStore.admin.FileUploadUtil;
import com.onlineStore.admin.UsernameNotFoundException;
import com.onlineStore.admin.role.RoleRepository;
import com.onlineStore.admin.user.UserRepository;
import com.onlineStore.admin.user.servcies.UserService;
import com.onlineStoreCom.entity.User;
import com.onlineStoreCom.entity.Role;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
                                    @RequestParam("image")MultipartFile multipartFile) throws UsernameNotFoundException, IOException {
        redirectAttributes.addFlashAttribute("message", "the user   has been saved successfully.  ");


        if (!multipartFile.isEmpty()) {
              String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

              user.setUser_bio(fileName);
              User savedUser = userService.saveUser(user);

              String dirName = "user-photos/";

              String uploadDir = dirName  +savedUser.getId();

              FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);}

         userService.saveUser(user);

        return new ModelAndView("redirect:/users");
    }



    @GetMapping("/edit/{id}")
    public ModelAndView editUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {


        ModelAndView model = new ModelAndView("new-user-form");
        try {
            User user = userService.getUser(id);
            List<Role> listAllRoles = userService.listAllRoles();
            model.addObject("user", user);
            model.addObject("listAllRoles",  user.getRoles()  ) ;
            model.addObject("pageTitle","Edit User (ID: " + id + ")" );
            model.addObject("listAllRoles", listAllRoles);
            model.addObject("saveChanges", "/save-edit-user/");
            model.addObject("UserId", id);
            return model;

        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message  Ehab", ex.getMessage());
            return new ModelAndView("redirect:/users");

        }
    }

    @PostMapping( "/save-edit-user/")
    public ModelAndView saveUpdaterUser(@RequestParam ( name="id") Long id,@ModelAttribute  User user, RedirectAttributes redirectAttributes,
                                       @RequestParam("image") MultipartFile multipartFile ) throws UsernameNotFoundException, IOException {
        redirectAttributes.addFlashAttribute("message", "the user Id : " + id+  " has been updated successfully. ");

        User updateUser =userService.getUser(id);

        if (user.getPassword().isEmpty()) {

            if (multipartFile.isEmpty()) {

                BeanUtils.copyProperties(  user,updateUser,"id", "password", "user_bio");
                userService.saveUser(updateUser);

            } else if (!multipartFile.isEmpty()) {

                FileUploadUtil.cleanDir(updateUser.getImageDir());

                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                String uploadDir = "user-photos/" + updateUser.getId();
                user.setUser_bio(fileName);
                BeanUtils.copyProperties( user,updateUser,"id" ,"password" );

                userService.saveUser(updateUser);
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            }

        } else {

            if (multipartFile.isEmpty()) {

                BeanUtils.copyProperties(  user,updateUser,"id", "user_bio");
                userService.saveUser(updateUser);

            } else if (!multipartFile.isEmpty()) {

                FileUploadUtil.cleanDir(updateUser.getImageDir());

                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                String uploadDir = "user-photos/" + updateUser.getId();
                user.setUser_bio(fileName);
                BeanUtils.copyProperties( user,updateUser,"id");

                userService.saveUser(updateUser);
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            }

        }
        return new ModelAndView("redirect:/users");
    }


    @PostMapping("/searchID")
    public ModelAndView searchUser(@RequestParam ("uid") Long id,@ModelAttribute  User user,  RedirectAttributes redirectAttributes)  {


        ModelAndView model = new ModelAndView("users");
        try {
            user = userService.getUser(id);
            model.addObject("users", user);
            return model;

        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message  Ehab", ex.getMessage());
            return new ModelAndView("redirect:/users");
        }
    }

    @GetMapping ("/delete-user/{id}")
        public ModelAndView deleteUser(@PathVariable (name = "id")Long id, RedirectAttributes redirectAttributes) {

        try {
            if(userRepository.existsById(id)){
                FileUploadUtil.cleanDir( userService.getUser(id).getImageDir());

            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "the user ID: "+ id+" has been Deleted");
            } else {
                redirectAttributes.addFlashAttribute("message", "the user ID: "+ id+" user Not Found");

            }return new ModelAndView("redirect:/users");
        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", " user Not Found");
            return new ModelAndView("redirect:/users");
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    @PostMapping("/deleteUsers")
    public ModelAndView deleteUsers(@RequestParam(name = "selectedUsers", required = false) List<Long> selectedUsers,
                              RedirectAttributes redirectAttributes) throws UsernameNotFoundException, IOException {
        if (selectedUsers != null && !selectedUsers.isEmpty()) {
            for (Long userId : selectedUsers) {
                FileUploadUtil.cleanDir( userService.getUser(userId).getImageDir());

                userService.deleteUser(userId);
            }
        }
        return new ModelAndView( "redirect:/users");
    }


}
