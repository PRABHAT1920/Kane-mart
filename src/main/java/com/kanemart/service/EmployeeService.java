package com.kanemart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kanemart.model.Employee;
import com.kanemart.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found with ID: " + id));
    }

    public Employee getEmployeeByMobile(String mobile) {
        return employeeRepository.findByMobile(mobile)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found with mobile: " + mobile));
    }

    public Employee addEmployee(Employee employee) {

        if (employeeRepository.findByMobile(employee.getMobile()).isPresent()) {
            throw new RuntimeException(
                    "Employee already exists with mobile: " + employee.getMobile());
        }

        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {

        Employee employee = getEmployeeById(id);

        employee.setName(updatedEmployee.getName());
        employee.setMobile(updatedEmployee.getMobile());
        employee.setRole(updatedEmployee.getRole());
        employee.setActive(updatedEmployee.isActive());

        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {

        Employee employee = getEmployeeById(id);

        employeeRepository.delete(employee);
    }

    public List<Employee> getActiveEmployees() {
        return employeeRepository.findAll()
                .stream()
                .filter(Employee::isActive)
                .toList();
    }
}