package com.example.crudrapido.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.crudrapido.dto.PageResponse;
import com.example.crudrapido.dto.StudentRequest;
import com.example.crudrapido.dto.StudentResponse;
import com.example.crudrapido.entity.Student;
import com.example.crudrapido.exception.EmailAlreadyExistsException;
import com.example.crudrapido.exception.ResourceNotFoundException;
import com.example.crudrapido.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public PageResponse<StudentResponse> getStudents(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("studentId"));
        Page<StudentResponse> result = studentRepository.findAll(pageable).map(this::toResponse);

        return new PageResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    public StudentResponse getStudent(Long id) {
        return toResponse(findOrThrow(id));
    }

    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Ya existe un estudiante con ese email");
        }

        Student student = new Student();
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());

        return toResponse(studentRepository.save(student));
    }

    public StudentResponse update(Long id, StudentRequest request) {
        Student student = findOrThrow(id);

        if (studentRepository.existsByEmailAndStudentIdNot(request.email(), id)) {
            throw new EmailAlreadyExistsException("Ya existe otro estudiante con ese email");
        }

        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());

        return toResponse(studentRepository.save(student));
    }

    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Estudiante no encontrado con id " + id);
        }
        studentRepository.deleteById(id);
    }

    private Student findOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudiante no encontrado con id " + id));
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getStudentId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail());
    }
}