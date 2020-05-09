package com.example.marcel.DevDojo.repository;

import com.example.marcel.DevDojo.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface StudentRepository extends PagingAndSortingRepository<Student, Long> {
    Page<Student> findByNameIgnoreCaseContaining(String name, Pageable pageable);
}
