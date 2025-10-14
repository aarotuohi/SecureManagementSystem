package com.aaro.securemanagementsystem.services;

import com.aaro.securemanagementsystem.controller.JSONRequestResponse;
import com.aaro.securemanagementsystem.repo.OtherUserRepo;
import com.aaro.securemanagementsystem.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerManagementService {

    @Autowired
    private UsersRepo usersRepo;

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
}
