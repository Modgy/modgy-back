package ru.modgy.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.modgy.user.dto.NewUserDto;
import ru.modgy.user.dto.UpdateUserDto;
import ru.modgy.user.dto.UserDto;
import ru.modgy.user.model.Roles;
import ru.modgy.user.service.UserService;
import ru.modgy.utility.UtilityService;

import java.util.Collection;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UtilityService utilityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestHeader(UtilityService.REQUESTER_ID_HEADER) Long requesterId,
                           @RequestBody @Valid NewUserDto userDto

    ) {
        utilityService.checkHigherOrdinalRoleAccessForUsers(requesterId, userDto.getRole());
        log.info("UserController: POST/addUser, requesterId={}, user={}", requesterId, userDto);
        return userService.addUser(requesterId, userDto);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers(@RequestHeader(UtilityService.REQUESTER_ID_HEADER) Long requesterId,
                                           @RequestParam(value = "isActive", required = false) Boolean isActive
    ) {
        utilityService.checkHigherOrEqualOrdinalRoleAccessForUsers(requesterId, Roles.ROLE_ADMIN);
        log.info("UserController: GET/getAllUsers, requesterId={}, isActive={}", requesterId, isActive);
        return userService.getAllUsers(requesterId, isActive);
    }


    @GetMapping("/{id}")
    public UserDto getUserById(@RequestHeader(UtilityService.REQUESTER_ID_HEADER) Long requesterId,
                               @PathVariable(value = "id") long userId
    ) {
        if (!utilityService.checkRequesterRequestsHimself(requesterId, userId)) {
            utilityService.checkHigherOrEqualOrdinalRoleAccessForUsers(requesterId, userId);
        }
        log.info("UserController: GET/getUserById, requesterId={}, userId={}", requesterId, userId);
        return userService.getUserById(requesterId, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@RequestHeader(UtilityService.REQUESTER_ID_HEADER) Long requesterId,
                               @PathVariable("id") Long userId
    ) {
        utilityService.checkHigherOrdinalRoleAccessForUsers(requesterId, userId);
        log.info("UserController: DELETE/deleteUserById, requesterId={}, userId={}", requesterId, userId);
        userService.deleteUserById(requesterId, userId);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestHeader(UtilityService.REQUESTER_ID_HEADER) Long requesterId,
                              @PathVariable(value = "id") Long userId,
                              @RequestBody @Valid UpdateUserDto updateUserDto
    ) {
        if (!utilityService.checkRequesterRequestsHimself(requesterId, userId)) {
            utilityService.checkHigherOrEqualOrdinalRoleAccessForUsers(requesterId, userId);
        }
        log.info("UserController: PATCH/updateUser, requesterId={}, userId={}, requestBody={}",
                requesterId, userId, updateUserDto);
        return userService.updateUser(requesterId, userId, updateUserDto);
    }

    @PatchMapping("/{id}/state")
    public UserDto setUserState(@RequestHeader(UtilityService.REQUESTER_ID_HEADER) Long requesterId,
                                @PathVariable(value = "id") Long userId,
                                @RequestParam(value = "isActive", defaultValue = "true") Boolean isActive
    ) {
        utilityService.checkHigherOrdinalRoleAccessForUsers(requesterId, userId);
        log.info("UserController: PATCH/setUserState, requesterId={}, userId={}, isActive={}",
                requesterId, userId, isActive);
        return userService.setUserState(requesterId, userId, isActive);
    }
}
