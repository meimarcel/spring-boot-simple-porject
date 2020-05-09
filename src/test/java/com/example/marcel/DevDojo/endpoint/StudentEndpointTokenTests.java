package com.example.marcel.DevDojo.endpoint;

import com.example.marcel.DevDojo.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.ResourceAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StudentEndpointTokenTests {
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private Integer port;
    @Autowired
    private MockMvc mockMvc;

    private HttpEntity<Void> protectedHeader;
    private HttpEntity<Void> adminHeader;
    private HttpEntity<Void> wrongHeader;

    @BeforeEach
    public void configProtectedHeaders() {
        String str = "{\"username\":\"meimarcel\", \"password\":\"123456\"}";
        HttpHeaders headers = restTemplate.postForEntity("/login", str, String.class).getHeaders();
        this.protectedHeader = new HttpEntity<>(headers);
    }

    @BeforeEach
    public void configAdminHeaders() {
        String str = "{\"username\":\"admin\", \"password\":\"123456\"}";
        HttpHeaders headers = restTemplate.postForEntity("/login", str, String.class).getHeaders();
        this.adminHeader = new HttpEntity<>(headers);
    }

    @BeforeEach
    public void configWrongHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "1231231312321");
        this.wrongHeader = new HttpEntity<>(headers);
    }

    @Test
    public void whenListStudentsUsingIncorrectToken_ShouldReturnStatusCode403() {
        System.out.println("Port Connected: " + port);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/protected/students/", HttpMethod.GET, this.wrongHeader, String.class);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void whenGetStudentsByIdUsingIncorrectToken_ShouldReturnStatusCode403() {
        System.out.println("Port Connected: " + port);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/protected/students/{id}", HttpMethod.GET, this.wrongHeader, String.class, 1);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void whenGetStudentsByNameUsingIncorrectToken_ShouldReturnStatusCode403() {
        System.out.println("Port Connected: " + port);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/protected/students/byName/{name}", HttpMethod.GET, this.wrongHeader, String.class, "legolas");
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void whenSaveStudentUsingIncorrectToken_ShouldReturnStatusCode403() throws Exception {
        System.out.println("Port Connected: " + port);
        Student student = new Student("Sam", "sam@lordor.com");
        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/admin/students/", HttpMethod.POST, new HttpEntity<>(student, this.wrongHeader.getHeaders()), String.class);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void whenDeleteStudentUsingIncorrectToken_ShouldReturnStatusCode403() {
        System.out.println("Port Connected: " + port);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/admin/students/{id}", HttpMethod.DELETE, this.wrongHeader, String.class, 3L);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void whenUpdateStudentUsingIncorrectToken_ShouldReturnStatusCode401() {
        System.out.println("Port Connected: " + port);
        Student student = new Student("Sam", "sam@lordor.com");
        student.setId(2L);

        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/admin/students", HttpMethod.PUT, new HttpEntity<>(student, this.wrongHeader.getHeaders()), String.class, 2L);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }


    @Test
    public void whenListStudentsUsingCorrectToken_ShouldReturnStatusCode200() {
        System.out.println("Port Connected: " + port);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/protected/students/", HttpMethod.GET, this.protectedHeader, String.class, PageRequest.of(0, 7));

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenGetStudentByIdUsingCorrectToken_ShouldReturnStatusCode200() {
        System.out.println("Port Connected: " + port);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/protected/students/{id}", HttpMethod.GET, this.protectedHeader, String.class, 1L);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenGetStudentByNameUsingCorrectToken_ShouldReturnStatusCode200() {
        System.out.println("Port Connected: " + port);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/protected/students/byName/{name}", HttpMethod.GET, this.protectedHeader, String.class, "legolas");
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenGetStudentByIdUsingCorrectTokenButStudentNotExist_ShouldReturnStatusCode404() {
        System.out.println("Port Connected: " + port);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/protected/students/{id}", HttpMethod.GET, this.protectedHeader, String.class, -1L);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void whenDeleteAndUserHasRoleAdminAndStudentExist_ShouldReturnStatusCode204() {
        System.out.println("Port Connected: " + port);
        ResponseEntity<String> exchange = restTemplate.exchange("/v1/admin/students/{id}", HttpMethod.DELETE, this.adminHeader, String.class, 8L);

        assertThat(exchange.getStatusCodeValue()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void whenDeleteAndUserHasRoleAdminButStudentDoesNotExist_ShouldReturnStatusCode404() throws Exception {
        String token = this.adminHeader.getHeaders().get("Authorization").get(0);
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/v1/admin/students/{id}", -1L).header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void whenDeleteAndUserDoesNotHaveRoleAdmin_ShouldReturnStatusCode404() throws Exception {
        String token = this.protectedHeader.getHeaders().get("Authorization").get(0);
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/v1/admin/students/{id}", 1L).header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void whenSaveButNameIsNull_ShouldReturnStatusCode400() throws Exception {
        Student student = new Student("", "sam@lordor.com");
        student.setId(2L);

        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/admin/students/", HttpMethod.POST, new HttpEntity<>(student, this.adminHeader.getHeaders()), String.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseEntity.getBody()).contains("field", "O campo nome do estudante é obrigatório");
    }

    @Test
    public void whenSave_ShouldReturnStatusCode201() throws Exception {
        Student student = new Student("Sam", "sam@lordor.com");

        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/admin/students/", HttpMethod.POST, new HttpEntity<>(student, this.adminHeader.getHeaders()), String.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(responseEntity.getBody()).contains("name", "Sam");
    }

    @Test
    public void whenSaveButNameIsNull_ShouldReturnStatusCode400_Mockito() throws Exception {

        Student student = new Student("", "legolas@lotr.com");

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(student);

        String token = this.adminHeader.getHeaders().get("Authorization").get(0);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/admin/students/")
                .contentType(MediaType.APPLICATION_JSON).content(jsonString).header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void whenUpdate_ShouldReturnStatusCode201() throws Exception {
        Student student = new Student("Sam", "sam@lordor.com");
        student.setId(10L);

        ResponseEntity<String> responseEntity = restTemplate.exchange("/v1/admin/students/", HttpMethod.PUT,
                new HttpEntity<>(student, this.adminHeader.getHeaders()), String.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(responseEntity.getBody()).contains("name", "Sam");
    }


}
