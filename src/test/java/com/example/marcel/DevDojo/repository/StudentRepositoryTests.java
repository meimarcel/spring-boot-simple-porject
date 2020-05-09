package com.example.marcel.DevDojo.repository;


import com.example.marcel.DevDojo.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)   <- ativa testes usando o próprio bd
public class StudentRepositoryTests {

    @Autowired
    private StudentRepository studentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void whenCreateShouldPersistData() {
        Student student = new Student("Marcel", "marcel@gmail.com");
        this.studentRepository.save(student);
        assertThat(student.getId()).isNotNull();
        assertThat(student.getName()).isEqualTo("Marcel");
        assertThat(student.getEmail()).isEqualTo("marcel@gmail.com");
    }

    @Test
    public void whenDeleteShouldRemoveData() {
        Student student = new Student("Marcel", "marcel@gmail.com");
        student = this.studentRepository.save(student);
        this.studentRepository.delete(student);
        assertThat(this.studentRepository.findById(student.getId())).isEmpty();
    }

    @Test
    public void whenUpdateShouldChangeAndPersistData() {
        Student student = new Student("Marcel1", "marcel1@gmail.com");
        student = this.studentRepository.save(student);
        student.setName("Marcel2");
        student.setEmail("marcel2@gmail.com");
        this.studentRepository.save(student);

        student = this.studentRepository.findById(student.getId()).orElse(null);

        assertThat(student.getName()).isEqualTo("Marcel2");
        assertThat(student.getEmail()).isEqualTo("marcel2@gmail.com");
    }

    @Test
    public void findByNameIgnoreCaseContaining() {
        Student student1 = new Student("Marcel", "marcel@gmail.com");
        Student student2 = new Student("marcel", "marcel@gmail.com");
        this.studentRepository.save(student1);
        this.studentRepository.save(student2);
        Page<Student> studentPage = this.studentRepository.findByNameIgnoreCaseContaining("marcel", PageRequest.of(0, 10));
        assertThat(studentPage.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void whenCreateAndNameIsNullShouldThrowConstraintViolationException() {
        Student student = new Student("", "marcel@gmail.com");
        Exception exception = assertThrows(ConstraintViolationException.class,
                () -> {
                    this.studentRepository.save(student);
                    entityManager.flush();
                });
        assertTrue(exception.getMessage().contains("O campo nome do estudante é obrigatório"));
    }

    @Test
    public void whenCreateAndEmailIsNullShouldThrowConstraintViolationException() {
        Student student = new Student("Marcel", "");
        Exception exception = assertThrows(ConstraintViolationException.class,
                () -> {
                    this.studentRepository.save(student);
                    entityManager.flush();
                });
        assertTrue(exception.getMessage().contains("O campo email do estudante é obrigatório"));
    }

    @Test
    public void whenCreateAndEmailIsNotValidShouldThrowConstraintViolationException() {
        Student student = new Student("Marcel", "marcel.com");
        Exception exception = assertThrows(ConstraintViolationException.class,
                () -> {
                    this.studentRepository.save(student);
                    entityManager.flush();
                });
        assertTrue(exception.getMessage().contains("O email deve ser válido"));
    }


}
