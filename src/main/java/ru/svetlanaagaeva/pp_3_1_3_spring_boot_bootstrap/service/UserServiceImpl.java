package ru.svetlanaagaeva.pp_3_1_3_spring_boot_bootstrap.service;


import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.svetlanaagaeva.pp_3_1_3_spring_boot_bootstrap.model.Role;
import ru.svetlanaagaeva.pp_3_1_3_spring_boot_bootstrap.model.User;
import ru.svetlanaagaeva.pp_3_1_3_spring_boot_bootstrap.repository.RoleRepository;
import ru.svetlanaagaeva.pp_3_1_3_spring_boot_bootstrap.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email);
        if (user == null) {
            throw new UsernameNotFoundException("Incorrect email or password");

        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), user.getAuthorities());
    }


    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> Hibernate.initialize(user.getRoles()));
        return users;
    }

    @Override
    public List<Role> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        roles.forEach(role -> System.out.println("Role name: " + role.getName()));
        return roles;
    }


    @Override
    public User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with such ID doesn't exist."));

        Hibernate.initialize(user.getRoles());

        return user;
    }

    @Override
    public void saveUser(User user) {

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Set<Role> managedRoles = user.getRoles().stream()
                .map(role -> roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + role.getName())))
                .collect(Collectors.toSet());

        user.setRoles(managedRoles);
        userRepository.save(user);
    }
//    @Override
//    public void saveUser(User user) {
//        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//        if (user.getRoles() == null || user.getRoles().isEmpty()) {
//            Optional<Role> userRoleOptional = roleRepository.findByName("ROLE_USER");
//            if (userRoleOptional.isPresent()) {
//                Set<Role> roles = new HashSet<>();
//                roles.add(userRoleOptional.get());
//                user.setRoles(roles);
//                //user.setRoles(Collections.singleton(userRoleOptional.get()));
//            } else {
//                throw new RuntimeException("Role USER not found");
//            }
//        }
//        userRepository.save(user);
//    }


    @Override
    public void updateUser(User user) {

        User existingUser = getUserById(user.getId());
        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(existingUser.getRoles());
        }
        if (!existingUser.getPassword().isEmpty()) {
            if (!bCryptPasswordEncoder.matches(existingUser.getPassword(), user.getPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(existingUser.getPassword()));
            }
            existingUser.setRoles(user.getRoles());
            userRepository.save(existingUser);

        }
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with such ID doesn't exist."));
        user.setRoles(null);
        userRepository.save(user);
        userRepository.deleteById(id);

    }
//    public User getAuthUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return (User) authentication.getPrincipal();  // Получаем пользователя из контекста безопасности
//    }


    @Override
    public User getAuthUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmailIgnoreCase(auth.getName());
    }

//    @Autowired
//    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//    }
//
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByName(username);  // Вместо findByUsername, используем findByEmail
//        if (user == null) {
//            throw new UsernameNotFoundException("Incorrect username or password");
//        }
//        return new org.springframework.security.core.userdetails.User(user.getUsername(),
//                user.getPassword(), user.getAuthorities());
//    }
//
//
//    @Override
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    @Override
//    public List<Role> getAllRoles() {
//        return roleRepository.findAll();
//    }
//
//
//    @Override
//    public User getById(Long id) {
//        return userRepository.getById( id);
//               //
//
//    }
//
//
////    @Override
////    public void saveUser(User user) {
////        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
////        if (user.getRoles() == null || user.getRoles().isEmpty()) {
////            Optional<Role> userRoleOptional = roleRepository.findByName("USER");
////            if (userRoleOptional.isPresent()) {
////                user.setRoles(Collections.singleton(userRoleOptional.get()));
////            } else {
////                throw new RuntimeException("Role USER not found");
////            }
////        }
////        userRepository.save(user);
////    }
//
//    @Override
//    public void saveUser(User user) {
//        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//        userRepository.save(user);
//    }
////    @Override
////    public void updateUser(User user) {
////
////        User updatedUser = getUserById(user.getId());
////        updatedUser.setName(user.getName());
////        updatedUser.setSurname(user.getSurname());
////        updatedUser.setEmail(user.getEmail());
////        updatedUser.setPassword((bCryptPasswordEncoder.encode(user.getPassword())));
////        updatedUser.setRoles(user.getRoles());
////        userRepository.save(updatedUser);
////    }
//@Override
//public void updateUser(User user) {
//    userRepository.save(user);
//}
//
//    @Override
//    public void deleteById(Long id) {
//        User user = userRepository.findById(id).get();
//        user.setRoles(null);
//       // userRepository.save(user);
//        userRepository.deleteById(id);
//
//    }
//
//    @Override
//    public User getAuthUser() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        return userRepository.findByName(auth.getName());
//    }
//    @Override
//    public Role getName(){ return getName();}
}
