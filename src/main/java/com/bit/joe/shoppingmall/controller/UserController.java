package com.bit.joe.shoppingmall.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.response.Response;
import com.bit.joe.shoppingmall.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
        // return success response with status code 200 (OK)
    }

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(
            HttpSession session, @RequestBody UserDto userDto) {

        // Check is requset body is empty
        if (userDto.getEmail() == null
                || userDto.getPassword() == null
                || userDto.getName() == null
                || userDto.getRole() == null
                || userDto.getGender() == null
                || userDto.getBirth() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.builder().status(400).message("Bad Request").build());
            // return bad request response with status code 400
        }

        Response resp = userService.createUser(userDto);
        // Create user with userDto and save it to database, then get response

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        // return success response with status code 201 (CREATED)
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<Response> updateUser(
            HttpSession session, @PathVariable Long userId, @RequestBody UserDto userDto) {

        UserDto user = (UserDto) session.getAttribute("user");
        // Get user from session
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Response.builder()
                                    .status(401)
                                    .message("Unauthorized(in controller): User is not logged in")
                                    .build());
            // return unauthorized response with status code 401 -> user is not logged in
        }

        Long sessionUserId = user.getId();
        // Get user's id
        if (!sessionUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Response.builder().status(403).message("Forbidden").build());
            // return forbidden response with status code 403 -> user is not allowed to update other
            // user's data
        }

        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userId, userDto));
        // return success response with status code 200 (OK)
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Response> deleteUser(HttpSession session, @PathVariable Long userId) {

        UserDto sessionUser = (UserDto) session.getAttribute("user");
        // Get user from session

        // Check if user is logged in
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Response.builder().status(401).message("Unauthorized").build());
            // return unauthorized response with status code 401 -> user is not logged in
        }

        // Check if user is trying to delete his/her own account
        if (!sessionUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Response.builder().status(403).message("Forbidden").build());
            // return forbidden response with status code 403 -> user is not allowed to delete other
            // user's account
        }

        Response resp = userService.deleteUser(userId);
        // Delete user by userId and get response

        session.invalidate();
        // Invalidate session -> logout user

        return ResponseEntity.status(HttpStatus.OK).body(resp);
        // return success response with status code 200 (OK)
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(HttpSession session, @RequestBody UserDto userDto) {
        session.removeAttribute("user");
        // Set user to null -> logout user

        System.out.println("User email :" + userDto.getEmail());
        // Print user email to console

        UserDto user = userService.getUserByEmail(userDto.getEmail()).getUser();
        // Get user by email from database

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder().status(404).message("Not Found").build());
            // return not found response with status code 404 -> user not found
        }

        // check password
        if (!user.getPassword().equals(userDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Response.builder().status(401).message("Unauthorized").build());
            // return unauthorized response with status code 401 -> password is incorrect
        }

        session.setAttribute("user", user);
        // Set user to session

        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.builder().status(200).message("Login Success").build());
        // return success response with status code 200 (OK)
    }
}
