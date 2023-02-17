package com.thullo.event;


import com.thullo.data.model.AuthProvider;
import com.thullo.data.model.Privilege;
import com.thullo.data.model.Role;
import com.thullo.data.model.User;
import com.thullo.data.repository.PrivilegeRepository;
import com.thullo.data.repository.RoleRepository;
import com.thullo.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Configuration
@RequiredArgsConstructor
public class SetupRoleAndPrivileges implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PrivilegeRepository privilegeRepository;


    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        createDefaultRoles();
        createAdminUserIfNotFound();
    }


    @Transactional
    public void createDefaultRoles() {
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_OWNER");
        createRoleIfNotFound("ROLE_COLLABORATOR");
        createRoleIfNotFound("ROLE_CONTRIBUTOR");
    }

    @Transactional
    public void createAdminUserIfNotFound() {
        Optional<Role> adminRole = roleRepository.findByName("ROLE_ADMIN");
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege);


        if (adminRole.isPresent() && userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            adminRole.get().setPrivileges(adminPrivileges);
            User user = new User();
            user.setName("admin");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setEmail("admin@gmail.com");
            user.setRoles(List.of(adminRole.get()));
            user.setEmailVerified(true);
            user.setProvider(AuthProvider.LOCAL);
            userRepository.save(user);
        }
    }

    @Transactional
    public void createRoleIfNotFound(String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            roleRepository.save(new Role(roleName));
        }
    }


    @Transactional
    public Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }


}
