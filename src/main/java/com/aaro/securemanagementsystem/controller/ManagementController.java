package com.aaro.securemanagementsystem.controller;


import com.aaro.securemanagementsystem.repo.OtherUserRepo;
import com.aaro.securemanagementsystem.services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class ManagementController {
    @Autowired
    private UserManagementService userManagementService;

    @PostMapping("/auth/register")
    public ResponseEntity<JSONRequestResponse> register(@RequestBody JSONRequestResponse reg){
        return ResponseEntity.ok(userManagementService.register(reg));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<JSONRequestResponse> login(@RequestBody JSONRequestResponse req){
        return ResponseEntity.ok(userManagementService.login(req));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<JSONRequestResponse> refreshToken(@RequestBody JSONRequestResponse req){
        return ResponseEntity.ok(userManagementService.refreshToken(req));
    }

    @GetMapping("/manager/get-all-users")
    public ResponseEntity<JSONRequestResponse> managerGetAllUsers(){
        return ResponseEntity.ok(userManagementService.getAllUsers());

    }

    @PutMapping("/manager/update/{userId}")
    public ResponseEntity<JSONRequestResponse> updateManagerUser(@PathVariable Integer userId, @RequestBody OtherUserRepo reqres){
        return ResponseEntity.ok(userManagementService.updateManagerUser(userId, reqres));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<JSONRequestResponse> getAllUsers(){
        return ResponseEntity.ok(userManagementService.getAllUsers());

    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<JSONRequestResponse> getUSerByID(@PathVariable Integer userId){
        return ResponseEntity.ok(userManagementService.getUsersById(userId));

    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<JSONRequestResponse> updateUser(@PathVariable Integer userId, @RequestBody OtherUserRepo reqres){
        return ResponseEntity.ok(userManagementService.updateUser(userId, reqres));
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<JSONRequestResponse> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        JSONRequestResponse response = userManagementService.getMyInfo(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<JSONRequestResponse> deleteUSer(@PathVariable Integer userId){
        return ResponseEntity.ok(userManagementService.deleteUser(userId));
    }



}