package com.project.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import com.project.dto.Pagination;
import com.project.dto.UserDTO;
import com.project.enums.Role;
import com.project.exception.ExistException;
import com.project.exception.NotFoundException;
import com.project.model.User;
import com.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.project.exception.AlreadyEmailExistException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO convertToUserDTO(User user) {
        UserDTO res = new UserDTO();
        res.setId(user.getId());
        res.setFullname(user.getFullname());
        res.setEmail(user.getEmail());
        res.setPhoneNumber(user.getPhoneNumber());
        res.setAddress(user.getAddress());
        res.setRoles(user.getRoles());
        res.setGender(user.getGender());
        res.setBirthday(user.getBirthday());
        return res;
    }

    public Pagination<P> getAllUsers(Specification<User> specification, Pageable pageable) {
        Page<User> pageUser = userRepository.findAll(specification, pageable);
        Pagination<P> pagination = new Pagination<P>();
        Pagination.Meta meta = new Pagination.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        pagination.setMeta(meta);

        List<UserDTO> listUsers = pageUser.getContent().stream()
                .map(this::convertToUserDTO).collect(Collectors.toList());

        pagination.setResult(listUsers);

        return pagination;
    }

    public UserDTO createUser(User user) throws ExistException {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ExistException("Email đã tồn tài : " + user.getEmail());
        }
        String hashPassword = this.passwordEncoder.encode("123456");
        user.setPassword(hashPassword);
        user.setRoles(Collections.singleton(Role.CUSTOMER));
        User savedUser = userRepository.save(user);

        return convertToUserDTO(savedUser);
    }

    public UserDTO updateUser(Integer id, User user) throws NotFoundException {
        Optional<User> currentUser = userRepository.findById(id);
        if (currentUser.isEmpty()) {
            throw new NotFoundException("Không tìm thấy người dùng : " + id);
        }
        currentUser.get().setFullname(user.getFullname());
        currentUser.get().setEmail(user.getEmail());
        currentUser.get().setPhoneNumber(user.getPhoneNumber());
        currentUser.get().setAddress(user.getAddress());
        currentUser.get().setGender(user.getGender());

        return convertToUserDTO(userRepository.save(currentUser.get()));

    }

    public void deleteUser(Integer id) throws NotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("Không tìm thấy người dùng : " + id);
        }
        user.get().getRoles().clear();
        userRepository.save(user.get());
        userRepository.deleteById(id);
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AlreadyEmailExistException("Email đã tồn tại: " + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Collections.singleton(Role.CUSTOMER));
        }
        return userRepository.save(user);
    }
}
