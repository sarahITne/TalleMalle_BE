package com.tallemalle.api.user;

import com.tallemalle.api.user.model.UserDto;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto.SignupRes signup(UserDto.SignupReq dto) {
        UserDto.SignupRes returnDto = userRepository.save(dto);
        return returnDto;
    }

    public UserDto.LoginRes login(UserDto.LoginReq dto) {
        UserDto.LoginRes returnDto = userRepository.findByEmailAndPasssword(dto);
        return returnDto;
    }
}
