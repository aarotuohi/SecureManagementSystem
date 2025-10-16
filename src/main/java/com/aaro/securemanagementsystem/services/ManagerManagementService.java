package com.aaro.securemanagementsystem.services;

import com.aaro.securemanagementsystem.controller.JSONRequestResponse;
import com.aaro.securemanagementsystem.repo.OtherUserRepo;
import com.aaro.securemanagementsystem.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerManagementService {

    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ROLE = "MANAGER";

    public JSONRequestResponse getAllManagers() {
        JSONRequestResponse res = new JSONRequestResponse();
        try {
            List<OtherUserRepo> managers = usersRepo.findByRole(ROLE);
            if (managers.isEmpty()) {
                res.setStatusCode(404);
                res.setMessage("No managers found");
            } else {
                res.setOurUsersList(managers);
                res.setStatusCode(200);
                res.setMessage("Successful");
            }
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error occurred: " + e.getMessage());
        }
        return res;
    }

    public JSONRequestResponse getManagerById(Integer id) {
        JSONRequestResponse res = new JSONRequestResponse();
        try {
            Optional<OtherUserRepo> user = usersRepo.findById(id);
            if (user.isPresent() && ROLE.equals(user.get().getRole())) {
                res.setOurUsers(user.get());
                res.setStatusCode(200);
                res.setMessage("Manager found");
            } else {
                res.setStatusCode(404);
                res.setMessage("Manager not found");
            }
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error occurred: " + e.getMessage());
        }
        return res;
    }

    public JSONRequestResponse getAllManagerUsers() {
        JSONRequestResponse reqRes = new JSONRequestResponse();

        try {
            List<OtherUserRepo> result = usersRepo.findByRole(ROLE);
            if (!result.isEmpty()) {
                reqRes.setOurUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No manager users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }
    
    public JSONRequestResponse updateManagerUser(Integer userId, OtherUserRepo updatedUser) {
        JSONRequestResponse reqRes = new JSONRequestResponse();
        try {
            Optional<OtherUserRepo> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                OtherUserRepo existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());
                existingUser.setOrganization(updatedUser.getOrganization());

                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                OtherUserRepo savedUser = usersRepo.save(existingUser);
                reqRes.setOurUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Manager user updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("Manager user not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating manager user: " + e.getMessage());
        }
        return reqRes;
    }
}
