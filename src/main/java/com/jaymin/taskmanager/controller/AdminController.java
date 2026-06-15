package com.jaymin.taskmanager.controller;

import com.jaymin.taskmanager.entity.User;
import com.jaymin.taskmanager.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    AdminService adminService;
    @GetMapping("/dashboard")
    public String dashboard() {
        return "Welcome Admin";
    }
    //get all users
    @GetMapping("/all-users")
    public List<User> getAllUsers(){
        return adminService.getAllUsers();
    }
    //delete users
    @DeleteMapping("users/{id}")
    public String deleteUser(@PathVariable Long id){
        adminService.deleteUser(id);
        return "user deleted";
    }
    //make user admin
    @PatchMapping("users/{id}/make-admin")
    public String makeAdmin(@PathVariable Long id){
        adminService.makeAdmin(id);
        return "user promoted to admin";
    }

}
