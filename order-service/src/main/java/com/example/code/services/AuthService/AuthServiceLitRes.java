package com.example.code.services.AuthService;

import com.example.code.model.dto.web.request.RequestRegister;
import com.example.code.model.entities.UserInfo;
import com.example.code.model.exceptions.UserAlreadyExistException;
import com.example.code.model.mappers.UserInfoMapper;
import com.example.code.model.modelUtils.Role;
import com.example.code.repositories.UserRepository;
import com.example.code.security.utils.JwtUtils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
public class AuthServiceLitRes implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    @Autowired
    public AuthServiceLitRes(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public void register(RequestRegister requestRegister) {
        UserInfo newUserInfo = UserInfoMapper.INSTANCE.toUserInfo(requestRegister);
        newUserInfo.setPassword(passwordEncoder.encode(newUserInfo.getPassword()));
        userRepository.save(newUserInfo);
    }

    @Override
    public Role findUserRole(String username) {
        UserInfo userInfo = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User wasn't found"));
        return userInfo.getRole();
    }

    @Override
    public void checkUserExist(String username) throws UserAlreadyExistException {
        if (userRepository.findByUsername(username).isPresent()) throw new UserAlreadyExistException();
    }
}
