package com.onlineStore.admin.user.controller;


import com.onlineStore.admin.UsernameNotFoundException;
import com.onlineStore.admin.role.RoleRepository;
import com.onlineStore.admin.user.UserRepository;
import com.onlineStore.admin.user.servcies.UserService;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStore.admin.utility.UserCsvExporter;
import com.onlineStore.admin.utility.UserExcelExporter;
import com.onlineStore.admin.utility.UserPdfExporter;
import com.onlineStoreCom.entity.users.Role;
import com.onlineStoreCom.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
    public ModelAndView listAllUsers( ) {
        ModelAndView model = new ModelAndView("users");
        return listByPage(1,"firstName","dsc",null);
    }

    @GetMapping("/users/page/{pageNum}")
    public ModelAndView listByPage(@PathVariable(name = "pageNum") int pageNum,
    @Param("sortFiled") String sortFiled, @Param("sortDir") String sortDir,
           @Param("keyWord") String keyWord
    ) {

        ModelAndView model = new ModelAndView("users");
        Page<User> userPage = userService.listByPage(pageNum, sortFiled, sortDir, keyWord);
        List<User>listUsers= userPage.getContent();

        long startCont = (long) ((long) (pageNum - 1) * UserService.USERS_PER_PAGE +1);
        long endCount = startCont+ UserService.USERS_PER_PAGE -1;


         if(endCount> userPage.getTotalElements()){
             endCount=userPage.getTotalElements();
         }
        String reverseSortDir= sortDir.equals("asc")?"dsc":"asc";

        model.addObject("totalItems", userPage.getTotalElements());
        model.addObject("totalPages", userPage.getTotalPages());
        model.addObject("sortFiled" , sortFiled);
        model.addObject("sortDir" , sortDir);
        model.addObject("currentPage", pageNum);
        model.addObject("userPage", userPage);
        model.addObject("endCount", endCount);
        model.addObject("startCont", startCont);
        model.addObject("users", listUsers);
        model.addObject("reverseSortDir", reverseSortDir);
        model.addObject("keyWord", keyWord);

         return  model;
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

            System.out.println(user.getfirstName());

              User savedUser = userService.saveUser(user);

              String dirName = "user-photos/";

              String uploadDir = dirName + savedUser.getId();

              FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }


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
            model.addObject("pageTitle","Edit "+user.getfirstName()   + " (ID: " + id + ")" );
            model.addObject("listAllRoles", listAllRoles);
            model.addObject("saveChanges", "/save-edit-user/");
            model.addObject("UserId", id);


            return model;

        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
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
                BeanUtils.copyProperties(  user,updateUser,"id",  "user_bio" ,"password");
                userService.saveUpdatededUser(updateUser);


               } else if (!multipartFile.isEmpty()) {

                FileUploadUtil.cleanDir(updateUser.getImageDir());
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                String uploadDir = "user-photos/" + updateUser.getId();
                user.setUser_bio(fileName);
                BeanUtils.copyProperties( user,updateUser,"id" ,"password" );
                userService.saveUpdatededUser(updateUser);
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
        String fristPartEmail= user.getEmail().split("@")[0];
        return new ModelAndView("redirect:/users/page/1?sortFiled=id&sortDir=asc&keyWord="+fristPartEmail);
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
        String message = " the user Id  " + id +"  has bean  " +status ;
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


@GetMapping("/users/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAllUsers();
        UserCsvExporter userCsvExporter = new UserCsvExporter();
        userCsvExporter.export(listUsers,response);

}
    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAllUsers();
        UserExcelExporter userExcelExporter = new  UserExcelExporter();
        userExcelExporter.export(listUsers,response);

    }
    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAllUsers();
        UserPdfExporter UserPdfExporter = new  UserPdfExporter();
        UserPdfExporter.export(listUsers,response);

    }

}
