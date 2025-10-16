package com.aaro.securemanagementsystem.controller;


import com.aaro.securemanagementsystem.repo.OtherUserRepo;
import com.aaro.securemanagementsystem.services.UserManagementService;
import com.aaro.securemanagementsystem.services.ManagerManagementService;
import com.aaro.securemanagementsystem.services.DeveloperManagementService;
import com.aaro.securemanagementsystem.services.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class ManagementController {
    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private ManagerManagementService managerManagementService;
    @Autowired
    private DeveloperManagementService developerManagementService;
    @Autowired
    private BusinessService businessService;

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

    @GetMapping("/manager/get-all-manager-users")
    public ResponseEntity<JSONRequestResponse> managerGetAllUsers(){
        return ResponseEntity.ok(managerManagementService.getAllManagerUsers());

    }

    @PutMapping("/manager/update/{userId}")
    public ResponseEntity<JSONRequestResponse> updateManagerUser(@PathVariable Integer userId, @RequestBody OtherUserRepo reqres){
        return ResponseEntity.ok(managerManagementService.updateManagerUser(userId, reqres));
    }

    @GetMapping("/developer/get-all-developer-users")
    public ResponseEntity<JSONRequestResponse> developerGetAllUsers(){
        return ResponseEntity.ok(developerManagementService.getAllDevelopers());

    }

    @PutMapping("/developer/update/{userId}")
    public ResponseEntity<JSONRequestResponse> updateDeveloperUser(@PathVariable Integer userId, @RequestBody OtherUserRepo reqres){
        return ResponseEntity.ok(developerManagementService.updateDeveloperUser(userId, reqres));
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

    @PostMapping("/admin/create")
    public ResponseEntity<JSONRequestResponse> adminCreate(@RequestBody JSONRequestResponse req){
        return ResponseEntity.ok(userManagementService.adminCreateUser(req));
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

    @PostMapping("/business/create")
    public ResponseEntity<JSONRequestResponse> createBusiness(@RequestBody JSONRequestResponse req){
        return ResponseEntity.ok(businessService.createBusiness(req));
    }

    @GetMapping("/business/my")
    public ResponseEntity<JSONRequestResponse> getMyBusiness(){
        return ResponseEntity.ok(businessService.getMyBusiness());
    }

    @GetMapping("/business")
    public ResponseEntity<JSONRequestResponse> listBusinesses(){
        return ResponseEntity.ok(businessService.listMyBusinesses());
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<JSONRequestResponse> businessDetails(@PathVariable Integer businessId){
        return ResponseEntity.ok(businessService.businessDetails(businessId));
    }

    @GetMapping("/business/{businessId}/users")
    public ResponseEntity<JSONRequestResponse> usersInBusiness(@PathVariable Integer businessId){
        return ResponseEntity.ok(businessService.usersInBusiness(businessId));
    }

    @GetMapping("/business/my/users")
    public ResponseEntity<JSONRequestResponse> usersInMyBusiness(){
        return ResponseEntity.ok(businessService.usersInMyBusiness());
    }



}