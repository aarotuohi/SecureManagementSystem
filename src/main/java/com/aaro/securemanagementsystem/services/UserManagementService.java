package com.aaro.securemanagementsystem.services;


import com.aaro.securemanagementsystem.controller.JSONRequestResponse;
import com.aaro.securemanagementsystem.repo.OtherUserRepo;
import com.aaro.securemanagementsystem.repo.UsersRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserManagementService {
    private static final Logger log = LoggerFactory.getLogger(UserManagementService.class);

    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private OtherUserRepo currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usersRepo.findByEmail(auth.getName()).orElse(null);
    }
    public JSONRequestResponse adminCreateUser(JSONRequestResponse req) {
        JSONRequestResponse resp = new JSONRequestResponse();
        try {
            OtherUserRepo me = currentUser();
            OtherUserRepo user = new OtherUserRepo();
            user.setEmail(req.getEmail());
            user.setName(req.getName());
            user.setCity(req.getCity());
            user.setOrganization(req.getOrganization());
            // No role model: ignore incoming role and leave null
            String rawPassword = (req.getPassword() == null || req.getPassword().isBlank())
                    ? UUID.randomUUID().toString().replace("-", "").substring(0, 12)
                    : req.getPassword();
            user.setPassword(passwordEncoder.encode(rawPassword));
            if (me != null && me.getBusiness() != null) {
                user.setBusiness(me.getBusiness());
            }
            OtherUserRepo saved = usersRepo.save(user);
            resp.setOurUsers(saved);
            resp.setStatusCode(200);
            resp.setMessage("User created successfully");
            if (me != null) {
                log.info("User {} created member {} in business {}", me.getEmail(), user.getEmail(), me.getBusiness() != null ? me.getBusiness().getId() : null);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage("Error creating user: " + e.getMessage());
        }
        return resp;
    }


    public JSONRequestResponse register(JSONRequestResponse registrationRequest){
        JSONRequestResponse resp = new JSONRequestResponse();

        try {
            OtherUserRepo ourUser = new OtherUserRepo();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setCity(registrationRequest.getCity());
            // No roles in the system
            ourUser.setOrganization(registrationRequest.getOrganization());
            ourUser.setName(registrationRequest.getName());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            OtherUserRepo ourUsersResult = usersRepo.save(ourUser);
            if (ourUsersResult.getId()>0) {
                resp.setOurUsers((ourUsersResult));
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
                log.info("User registered: {}", ourUser.getEmail());
            }

        }catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }


    public JSONRequestResponse login(JSONRequestResponse loginRequest){
        JSONRequestResponse response = new JSONRequestResponse();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }





    public JSONRequestResponse refreshToken(JSONRequestResponse refreshTokenReqiest){
        JSONRequestResponse response = new JSONRequestResponse();
        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
            OtherUserRepo users = usersRepo.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenReqiest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }


    public JSONRequestResponse getAllUsers() {
        JSONRequestResponse reqRes = new JSONRequestResponse();

        try {
            OtherUserRepo me = currentUser();
            List<OtherUserRepo> result = (me != null && me.getBusiness() != null)
                ? usersRepo.findAllByBusiness_Id(me.getBusiness().getId())
                : usersRepo.findAll();
            if (!result.isEmpty()) {
                reqRes.setOurUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }


    public JSONRequestResponse getUsersById(Integer id) {
        JSONRequestResponse reqRes = new JSONRequestResponse();
        try {
            OtherUserRepo me = currentUser();
            OtherUserRepo usersById = (me != null && me.getBusiness() != null)
                ? usersRepo.findByIdAndBusiness_Id(id, me.getBusiness().getId()).orElseThrow(() -> new RuntimeException("User Not found"))
                : usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setOurUsers(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }


    public JSONRequestResponse deleteUser(Integer userId) {
        JSONRequestResponse reqRes = new JSONRequestResponse();
        try {
            OtherUserRepo me = currentUser();
            Optional<OtherUserRepo> userOptional = (me != null && me.getBusiness() != null)
                ? usersRepo.findByIdAndBusiness_Id(userId, me.getBusiness().getId())
                : usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                usersRepo.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }

    public JSONRequestResponse updateUser(Integer userId, OtherUserRepo updatedUser) {
        JSONRequestResponse reqRes = new JSONRequestResponse();
        try {
            OtherUserRepo me = currentUser();
            Optional<OtherUserRepo> userOptional = (me != null && me.getBusiness() != null)
                ? usersRepo.findByIdAndBusiness_Id(userId, me.getBusiness().getId())
                : usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                OtherUserRepo existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                // No roles in the system
                existingUser.setOrganization(updatedUser.getOrganization());

                // Check if password is present in the request
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    // Encode the password and update it
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                OtherUserRepo savedUser = usersRepo.save(existingUser);
                reqRes.setOurUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }


    public JSONRequestResponse getMyInfo(String email){
        JSONRequestResponse reqRes = new JSONRequestResponse();
        try {
            Optional<OtherUserRepo> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setOurUsers(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        }catch (Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;

    }
}