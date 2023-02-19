package com.thullo.data.repository;

import com.thullo.data.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findByName(String name);

    List<Privilege> findAllByNameIn(List<String> privileges);
}
