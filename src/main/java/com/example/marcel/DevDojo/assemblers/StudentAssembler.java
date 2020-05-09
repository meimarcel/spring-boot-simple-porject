package com.example.marcel.DevDojo.assemblers;

import com.example.marcel.DevDojo.endpoint.StudentEndpoint;
import com.example.marcel.DevDojo.model.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class StudentAssembler implements RepresentationModelAssembler<Student, EntityModel<Student>> {
    @Override
    public EntityModel<Student> toModel(Student student) {
        return new EntityModel<>(student,
                linkTo(methodOn(StudentEndpoint.class).getStudentById(student.getId())).withSelfRel(),
                linkTo(methodOn(StudentEndpoint.class).update(student)).withRel("update"),
                linkTo(methodOn(StudentEndpoint.class).delete(student.getId())).withRel("delete"));
    }
}
