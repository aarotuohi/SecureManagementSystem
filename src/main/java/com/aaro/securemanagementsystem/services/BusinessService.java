package com.aaro.securemanagementsystem.services;

import com.aaro.securemanagementsystem.controller.JSONRequestResponse;
import com.aaro.securemanagementsystem.models.Business;
import com.aaro.securemanagementsystem.repo.BusinessRepo;
import com.aaro.securemanagementsystem.repo.OtherUserRepo;
import com.aaro.securemanagementsystem.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BusinessService {
    @Autowired
    private BusinessRepo businessRepo;
    @Autowired
    private UsersRepo usersRepo;

    public JSONRequestResponse createBusiness(JSONRequestResponse req) {
        JSONRequestResponse res = new JSONRequestResponse();
        try {
            Authentication authCheck = SecurityContextHolder.getContext().getAuthentication();
            if (authCheck == null || authCheck.getName() == null) {
                res.setStatusCode(401);
                res.setMessage("Unauthorized");
                return res;
            }
            Optional<OtherUserRepo> currentOpt = usersRepo.findByEmail(authCheck.getName());
            if (currentOpt.isEmpty() || currentOpt.get().getRole() == null || !"ADMIN".equalsIgnoreCase(currentOpt.get().getRole())) {
                res.setStatusCode(403);
                res.setMessage("Forbidden: Only admins can create a business.");
                return res;
            }
            if (req.getName() == null || req.getName().isEmpty()) {
                res.setStatusCode(400);
                res.setMessage("Business name is required");
                return res;
            }
            Business b = new Business();
            b.setName(req.getName());
            b.setDescription(req.getMessage());
            Business saved = businessRepo.save(b);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<OtherUserRepo> current = usersRepo.findByEmail(auth.getName());
            current.ifPresent(u -> { u.setBusiness(saved); usersRepo.save(u); });

            res.setStatusCode(200);
            res.setMessage("Business created");
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error: " + e.getMessage());
        }
        return res;
    }

    public JSONRequestResponse getMyBusiness() {
        JSONRequestResponse res = new JSONRequestResponse();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<OtherUserRepo> current = usersRepo.findByEmail(auth.getName());
            if (current.isPresent() && current.get().getBusiness() != null) {
                res.setStatusCode(200);
                res.setMessage(current.get().getBusiness().getName());
            } else {
                res.setStatusCode(404);
                res.setMessage("No business set for user");
            }
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error: " + e.getMessage());
        }
        return res;
    }

    public JSONRequestResponse listMyBusinesses() {
        // In current model, user has one business; return it if present.
        JSONRequestResponse res = new JSONRequestResponse();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<OtherUserRepo> current = usersRepo.findByEmail(auth.getName());
            if (current.isPresent() && current.get().getBusiness() != null) {
                JSONRequestResponse r = new JSONRequestResponse();
                r.setStatusCode(200);
                r.setMessage(current.get().getBusiness().getName());
                res.setStatusCode(200);
                res.setMessage("ok");
                res.setOurUsersList(List.of());
                // overload message to carry single business name
                res.setMessage(current.get().getBusiness().getName());
            } else {
                res.setStatusCode(200);
                res.setMessage("");
            }
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error: " + e.getMessage());
        }
        return res;
    }

    public JSONRequestResponse businessDetails(Integer businessId) {
        JSONRequestResponse res = new JSONRequestResponse();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            OtherUserRepo me = usersRepo.findByEmail(auth.getName()).orElse(null);
            if (me == null || me.getBusiness() == null || !me.getBusiness().getId().equals(businessId)) {
                res.setStatusCode(403);
                res.setMessage("Forbidden");
                return res;
            }
            Optional<Business> b = businessRepo.findById(businessId);
            if (b.isEmpty()) {
                res.setStatusCode(404);
                res.setMessage("Not found");
                return res;
            }
            res.setStatusCode(200);
            res.setMessage(b.get().getName());
            return res;
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error: " + e.getMessage());
            return res;
        }
    }

    public JSONRequestResponse usersInBusiness(Integer businessId) {
        JSONRequestResponse res = new JSONRequestResponse();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            OtherUserRepo me = usersRepo.findByEmail(auth.getName()).orElse(null);
            if (me == null || me.getBusiness() == null || !me.getBusiness().getId().equals(businessId)) {
                res.setStatusCode(403);
                res.setMessage("Forbidden");
                return res;
            }
            List<OtherUserRepo> users = usersRepo.findAllByBusiness_Id(businessId);
            res.setStatusCode(200);
            res.setOurUsersList(users);
            return res;
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error: " + e.getMessage());
            return res;
        }
    }

    public JSONRequestResponse usersInMyBusiness() {
        JSONRequestResponse res = new JSONRequestResponse();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            OtherUserRepo me = usersRepo.findByEmail(auth.getName()).orElse(null);
            if (me == null || me.getBusiness() == null) {
                res.setStatusCode(404);
                res.setMessage("No business set for user");
                return res;
            }
            List<OtherUserRepo> users = usersRepo.findAllByBusiness_Id(me.getBusiness().getId());
            res.setStatusCode(200);
            res.setOurUsersList(users);
            return res;
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error: " + e.getMessage());
            return res;
        }
    }
}
