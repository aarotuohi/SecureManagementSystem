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
public class DeveloperManagementService {

    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ROLE = "DEVELOPER";

    public JSONRequestResponse getAllDevelopers() {
        JSONRequestResponse res = new JSONRequestResponse();
        try {
            List<OtherUserRepo> devs = usersRepo.findByRole(ROLE);
            if (devs.isEmpty()) {
                res.setStatusCode(404);
                res.setMessage("No developers found");
            } else {
                res.setOurUsersList(devs);
                res.setStatusCode(200);
                res.setMessage("Successful");
            }
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error occurred: " + e.getMessage());
        }
        return res;
    }

    public JSONRequestResponse getDeveloperById(Integer id) {
        JSONRequestResponse res = new JSONRequestResponse();
        try {
            Optional<OtherUserRepo> user = usersRepo.findById(id);
            if (user.isPresent() && ROLE.equals(user.get().getRole())) {
                res.setOurUsers(user.get());
                res.setStatusCode(200);
                res.setMessage("Developer found");
            } else {
                res.setStatusCode(404);
                res.setMessage("Developer not found");
            }
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error occurred: " + e.getMessage());
        }
        return res;
    }

    public JSONRequestResponse updateDeveloperUser(Integer userId, OtherUserRepo updatedUser) {
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
                reqRes.setMessage("Developer user updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("Developer user not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating developer user: " + e.getMessage());
        }
        return reqRes;
    }
}
