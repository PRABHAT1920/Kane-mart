package com.kanemart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanemart.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByMobile(String mobile);

}