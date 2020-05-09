package com.example.marcel.DevDojo.javaclient;

import com.example.marcel.DevDojo.error.RestResponseExceptionHandler;
import com.example.marcel.DevDojo.model.Student;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class JavaSpringDAO {
    private RestTemplate restTemplateUser = new RestTemplateBuilder()
            .rootUri("http://localhost:8080/v1/protected/students")
            .basicAuthentication("meimarcel", "123456")
            .errorHandler(new RestResponseExceptionHandler())
            .build();
    private RestTemplate restTemplateAdmin = new RestTemplateBuilder()
            .rootUri("http://localhost:8080/v1/admin/students")
            .basicAuthentication("admin", "123456")
            .errorHandler(new RestResponseExceptionHandler())
            .build();

    public Student getById(Long id) {
        return restTemplateUser.getForObject("/{id}", Student.class, 1);
    }

    public ResponseEntity<Student> getByIdR(Long id) {
        return restTemplateUser.getForEntity("/{id}", Student.class, 1);
    }

    // Nao funciona com pageable
    public Student[] getAll() {
        return restTemplateUser.getForObject("/", Student[].class);
    }

    // Nao funciona com pageable
    public ResponseEntity<PagedModel<EntityModel<Student>>> getAllE() {
        return restTemplateUser.exchange("/", HttpMethod.GET, null, new ParameterizedTypeReference<PagedModel<EntityModel<Student>>>() {
        });
    }

    public ResponseEntity<Student> saveE(Student student) {
        return restTemplateAdmin.exchange("/", HttpMethod.POST, new HttpEntity<>(student, createJsonHeader()), Student.class);
    }

    public Student save(Student student) {
        return restTemplateAdmin.postForObject("/", student, Student.class);
    }

    public ResponseEntity<Student> saveR(Student student) {
        return restTemplateAdmin.postForEntity("/", student, Student.class);
    }

    public void update(Student student) {
        restTemplateAdmin.put("/", student);
    }

    public void delete(Long id) {
        restTemplateAdmin.delete("/{id}", id);
    }

    private static HttpHeaders createJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


}
