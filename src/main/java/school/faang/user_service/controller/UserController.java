package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    public final UserService userService;
    public final UserMapper userMapper;

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return userService
                .getPremiumUsers(userFilterDto)
                .map(userMapper::toDto)
                .toList();
    }
}
