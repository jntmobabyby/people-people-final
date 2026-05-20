package com.example.labreservation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.labreservation.common.BusinessException;
import com.example.labreservation.dto.LoginRequest;
import com.example.labreservation.dto.LoginResponse;
import com.example.labreservation.entity.User;
import com.example.labreservation.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {
    private final UserMapper userMapper;

    public AuthService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public LoginResponse login(LoginRequest request) {
        if (request == null || !StringUtils.hasText(request.username()) || !StringUtils.hasText(request.password())) {
            throw new BusinessException("用户名和密码不能为空");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.username()));
        if (user == null || !request.password().equals(user.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已停用");
        }
        String token = "demo-token-" + user.getId() + "-" + user.getUsername();
        return new LoginResponse(user.getId(), user.getUsername(), user.getName(), user.getRole(), token);
    }
}
