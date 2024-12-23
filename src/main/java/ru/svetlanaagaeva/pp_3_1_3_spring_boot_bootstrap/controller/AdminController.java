package ru.svetlanaagaeva.pp_3_1_3_spring_boot_bootstrap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.svetlanaagaeva.pp_3_1_3_spring_boot_bootstrap.model.Role;
import ru.svetlanaagaeva.pp_3_1_3_spring_boot_bootstrap.model.User;
import ru.svetlanaagaeva.pp_3_1_3_spring_boot_bootstrap.service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping

    public String ListOfPeople(Model model) {
        List<User> allUsers = userService.getAllUsers();
        User userAuth = userService.getAuthUser();
        List<Role> allRoles = userService.getAllRoles();
        model.addAttribute("users", allUsers);
        model.addAttribute("user", userAuth);
        model.addAttribute("allRoles", userService.getAllRoles());
        model.addAttribute("newUser", new User());
        return "admin";
    }

//    @GetMapping("/new")
//
//    public String showAddUserForm(Model model) {
//        User newUser = new User();
//        List<Role> allRoles = userService.getAllRoles();
//        model.addAttribute("newUser", newUser);
//        model.addAttribute("allRoles", allRoles);
//
//        return "new";
//    }

    @PostMapping("/new")

    public String create(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/admin";
    }

// его код
//    @RequestMapping(method = RequestMethod.POST, value = "/edit/{id}")
//    public String editUser(@ModelAttribute("usEdit") User user) {
//        userService.updateUser(user);
//        return "redirect:/admin";
//    }

    @PostMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, @ModelAttribute("user") User user) {
        userService.updateUser(user);
        return "redirect:/admin";
    }



//    @GetMapping("/edit/{id}")
//
//    public String showEditForm(@PathVariable("id") Long id, Model model) {
//        try {
//            User user = userService.getUserById(id);
//            List<Role> allRoles = userService.getAllRoles();
//            model.addAttribute("allRoles", allRoles);
//            model.addAttribute("user", user);
//        } catch (Exception e) {
//            model.addAttribute("errorMessage", e.getMessage());
//            return "error";
//        }
//        return "admin";
//    }
//
//    @PostMapping("/edit/{id}")
//
//    public String editUser( @PathVariable("id") Long id,@ModelAttribute("user") User user) {
//        User existingUser = userService.getUserById(id);
//        if (user.getRoles() == null || user.getRoles().isEmpty()) {
//            user.setRoles(existingUser.getRoles());
//        }
//        userService.updateUser(user);
//        return "redirect:/admin";
//    }

    @RequestMapping(value = "/delete/{id}")

    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }


}