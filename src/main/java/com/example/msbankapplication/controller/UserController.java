package com.example.msbankapplication.controller;

import com.example.msbankapplication.model.UserRequestDto;
import com.example.msbankapplication.model.UserResponseDto;
import com.example.msbankapplication.service.UserService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable Long userId){
        return userService.getUserById(userId);
    }

    @PostMapping
    public void saveUser(@RequestBody @Valid UserRequestDto userRequestDto){
        userService.saveUser(userRequestDto);
    }

    @PutMapping("/{userId}")
    public void editUser(@PathVariable Long userId,@RequestBody UserRequestDto userRequestDto){
        userService.editUser(userId,userRequestDto);
    }

    @DeleteMapping("/{userId}")
    public UserResponseDto deleteUser(@PathVariable Long userId){
        return userService.deleteUser(userId);
    }

    @PatchMapping("/{userId}/accounts/{accountId}/assign")
    public void assignAccountToUser(@PathVariable Long userId,@PathVariable Long accountId){
        userService.assignAccountToUser(userId,accountId);
    }

}
