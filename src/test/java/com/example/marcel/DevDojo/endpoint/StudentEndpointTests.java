package com.example.marcel.DevDojo.endpoint;

import com.example.marcel.DevDojo.model.Student;
import com.example.marcel.DevDojo.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.ResourceAccessException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StudentEndpointTests {
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private Integer port;
    @MockBean
    private StudentRepository studentRepository;
    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class Config {
        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder().basicAuthentication("admin", "123456");
        }
    }

    @BeforeEach
    public void setup() {
        Student student = new Student("Legolas", "legolas@lordor.com");
        student.setId(1L);
        BDDMockito.when(studentRepository.findById(student.getId())).thenReturn(Optional.ofNullable(student));
    }

    @Test
    public void whenListStudentsUsingIncorrectUsernameAndPassword_ShouldReturnStatusCode401() {
        System.out.println("Port Connected: " + port);
        restTemplate = restTemplate.withBasicAuth("test", "test");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/v1/protected/students/", String.class);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void whenGetStudentsByIdUsingIncorrectUsernameAndPassword_ShouldReturnStatusCode401() {
        System.out.println("Port Connected: " + port);
        restTemplate = restTemplate.withBasicAuth("test", "test");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/v1/protected/students/{id}", String.class, 1);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void whenGetStudentsByNameUsingIncorrectUsernameAndPassword_ShouldReturnStatusCode401() {
        System.out.println("Port Connected: " + port);
        restTemplate = restTemplate.withBasicAuth("test", "test");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/v1/protected/students/byName/{name}", String.class, "legolas");
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void whenSaveStudentUsingIncorrectUsernameAndPassword_ShouldReturnStatusCode401() throws Exception {
        System.out.println("Port Connected: " + port);
        Student student = new Student("Sam", "sam@lordor.com");
        student.setId(2L);
        restTemplate = restTemplate.withBasicAuth("test", "test");
        assertThrows(ResourceAccessException.class,
                () -> restTemplate.postForEntity("/v1/admin/students/", student, String.class));
    }

    @Test
    public void whenDeleteStudentUsingIncorrectUsernameAndPassword_ShouldReturnStatusCode401() {
        System.out.println("Port Connected: " + port);
        restTemplate = restTemplate.withBasicAuth("test", "test");
        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/admin/students/{id}", HttpMethod.DELETE, null, String.class, 1L);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void whenUpdateStudentUsingIncorrectUsernameAndPassword_ShouldReturnStatusCode401() {
        System.out.println("Port Connected: " + port);
        Student student = new Student("Sam", "sam@lordor.com");
        student.setId(2L);
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);
        BDDMockito.when(studentRepository.findById(student.getId())).thenReturn(Optional.ofNullable(student));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate = restTemplate.withBasicAuth("test", "test");

        assertThrows(
                ResourceAccessException.class,
                () -> restTemplate.exchange("/v1/admin/students", HttpMethod.PUT, new HttpEntity<>(student, headers), String.class, 2L));
    }


    @Test
    public void whenListStudentsUsingCorrectUsernameAndPassword_ShouldReturnStatusCode200() {
        System.out.println("Port Connected: " + port);
        Student student1 = new Student("Legolas", "legolas@lordor.com");
        student1.setId(1L);
        Student student2 = new Student("Aragorn", "aragorn@lordor.com");
        student2.setId(2L);
        List<Student> studentList = Arrays.asList(student1, student2);
        Page<Student> page = new PageImpl<>(studentList);
        BDDMockito.when(studentRepository.findAll(isA(Pageable.class))).thenReturn(page);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/v1/protected/students/", String.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenGetStudentByIdUsingCorrectUsernameAndPassword_ShouldReturnStatusCode200() {
        System.out.println("Port Connected: " + port);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/v1/protected/students/{id}", String.class, 1L);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenGetStudentByNameUsingCorrectUsernameAndPassword_ShouldReturnStatusCode200() {
        System.out.println("Port Connected: " + port);
        Student student1 = new Student("Legolas1", "legolas1@lordor.com");
        student1.setId(1L);
        Student student2 = new Student("legolas2", "legolas2@lordor.com");
        student2.setId(2L);
        List<Student> studentList = Arrays.asList(student1, student2);
        Page<Student> page = new PageImpl<>(studentList);
        BDDMockito.when(studentRepository.findByNameIgnoreCaseContaining(eq("legolas"), isA(Pageable.class))).thenReturn(page);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/v1/protected/students/byName/{name}", String.class, "legolas");
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenGetStudentByIdUsingCorrectUsernameAndPasswordButStudentNotExist_ShouldReturnStatusCode404() {
        System.out.println("Port Connected: " + port);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/v1/protected/students/{id}", String.class, -1L);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void whenDeleteAndUserHasRoleAdminButStudentExist_ShouldReturnStatusCode204() {
        System.out.println("Port Connected: " + port);
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", HttpMethod.DELETE, null, String.class, 1L);

        assertThat(exchange.getStatusCodeValue()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @WithMockUser(username = "XX", password = "XX", roles = {"USER", "ADMIN"})
    public void whenDeleteAndUserHasRoleAdminButStudentDoesNotExist_ShouldReturnStatusCode404() throws Exception {
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
//        ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", HttpMethod.DELETE, null, String.class, -1L);
//        assertThat(exchange.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/v1/admin/students/{id}", -1L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "XX", password = "XX", roles = {"USER"})
    public void whenDeleteAndUserDoesNotHaveRoleAdmin_ShouldReturnStatusCode404() throws Exception {
        BDDMockito.doNothing().when(studentRepository).deleteById(1L);
//        ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", HttpMethod.DELETE, null, String.class, -1L);
//        assertThat(exchange.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/v1/admin/students/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void whenSaveButNameIsNull_ShouldReturnStatusCode400() throws Exception {
        Student student = new Student("", "sam@lordor.com");
        student.setId(2L);
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/v1/admin/students/", student, String.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseEntity.getBody()).contains("field", "O campo nome do estudante é obrigatório");
    }

    @Test
    public void whenSave_ShouldReturnStatusCode201() throws Exception {
        Student student = new Student("Sam", "sam@lordor.com");
        student.setId(2L);
        BDDMockito.when(studentRepository.save(student)).thenReturn(student);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/v1/admin/students/", student, String.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(responseEntity.getBody()).contains("name", "Sam");
    }

    @Test
    @WithMockUser(username = "xxx", password = "xxx", roles = {"ADMIN"})
    public void whenSaveButNameIsNull_ShouldReturnStatusCode400_Mockito() throws Exception {

        Student student = new Student("", "legolas@lotr.com");
        student.setId(2L);

        BDDMockito.when(studentRepository.save(student)).thenReturn(student);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(student);


        mockMvc.perform(MockMvcRequestBuilders.post("/v1/admin/students/").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void whenUpdate_ShouldReturnStatusCode201() throws Exception {
        Student student = new Student("Sam", "sam@lordor.com");
        student.setId(2L);

        BDDMockito.when(studentRepository.save(student)).thenReturn(student);
        BDDMockito.when(studentRepository.findById(student.getId())).thenReturn(Optional.ofNullable(student));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        student.setName("Modified");
        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/admin/students/", HttpMethod.PUT,
                new HttpEntity<>(student, headers), String.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(responseEntity.getBody()).contains("name", "Modified");
    }


}
