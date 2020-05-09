package com.example.marcel.DevDojo.endpoint;

import com.example.marcel.DevDojo.assemblers.StudentAssembler;
import com.example.marcel.DevDojo.error.ResourceNotFoundException;
import com.example.marcel.DevDojo.model.Student;
import com.example.marcel.DevDojo.repository.StudentRepository;
import com.example.marcel.DevDojo.util.DateUtil;
import com.example.marcel.DevDojo.util.PasswordEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("v1")
public class StudentEndpoint {


    private final DateUtil dateUtil;
    private final StudentRepository studentDAO;
    private final StudentAssembler studentAssembler;
    private final PagedResourcesAssembler<Student> pagedResourcesAssembler;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StudentEndpoint(DateUtil dateUtil,
                           StudentRepository studentDAO,
                           StudentAssembler studentAssembler,
                           PagedResourcesAssembler<Student> pagedResourcesAssembler,
                           PasswordEncoder passwordEncoder) {
        this.dateUtil = dateUtil;
        this.studentDAO = studentDAO;
        this.studentAssembler = studentAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("protected/students")
    public ResponseEntity<?> listAll(@PageableDefault(size = 7) Pageable pageable) {
        System.out.println(dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
        Page<Student> studentPage = studentDAO.findAll(pageable);
        PagedModel<EntityModel<Student>> studentPagedModel = pagedResourcesAssembler.toModel(studentPage, studentAssembler);
        studentPagedModel.forEach((studentEntityModel) -> {
            studentEntityModel.add(studentPagedModel.getRequiredLink(IanaLinkRelations.SELF).withRel("students"));
        });
        return ResponseEntity.ok(studentPagedModel);
    }

    @GetMapping("protected/students/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable("id") Long id) {
        this.verifyIfStudentExist(id);
        Student student = studentDAO.findById(id).get();
        return ResponseEntity.ok(studentAssembler.toModel(student));
    }

    @GetMapping("protected/students/byName/{name}")
    public ResponseEntity<?> getStudentByName(@PathVariable("name") String name, Pageable pageable) {
        Page<Student> studentPage = studentDAO.findByNameIgnoreCaseContaining(name, pageable);
        PagedModel<EntityModel<Student>> studentPagedModel = pagedResourcesAssembler.toModel(studentPage, studentAssembler);
        studentPagedModel.forEach((studentEntityModel) -> {
            studentEntityModel.add(studentPagedModel.getRequiredLink(IanaLinkRelations.SELF).withRel("students"));
        });
        return ResponseEntity.ok(studentPagedModel);
    }

    @PostMapping("admin/students")
    @Transactional
    public ResponseEntity<?> save(@Valid @RequestBody Student student) {
        EntityModel<Student> entityModel = studentAssembler.toModel(studentDAO.save(student));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("admin/students")
    @Transactional
    public ResponseEntity<?> update(@Valid @RequestBody Student student) {
        this.verifyIfStudentExist(student.getId());
        Student updatedStudent = studentDAO.save(student);
        EntityModel<Student> entityModel = studentAssembler.toModel(updatedStudent);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("admin/students/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        this.verifyIfStudentExist(id);
        studentDAO.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/teste")
    public ResponseEntity<?> teste(@RequestBody JsonNode requestEntity, Authentication authentication) {
        System.out.println(requestEntity);
        System.out.println(authentication.getPrincipal());
        return ResponseEntity.ok(requestEntity);
    }

    @GetMapping("admin/generatePassword/{password}")
    public ResponseEntity<?> gerenatePassword(@PathVariable("password") String password) {
        return ResponseEntity.ok(passwordEncoder.encode(password));
    }

    private void verifyIfStudentExist(Long id) {
        Student student = studentDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found for Id: " + id));
    }
}
