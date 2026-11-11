package com.example.crudrapido.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.crudrapido.entity.Student;

@Repository 
public interface StudentRepository  extends JpaRepository<Student, Long>{

}

     
