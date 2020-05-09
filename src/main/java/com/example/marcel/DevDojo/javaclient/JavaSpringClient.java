package com.example.marcel.DevDojo.javaclient;

import com.example.marcel.DevDojo.model.Student;

import java.util.Arrays;

public class JavaSpringClient {
    public static void main(String[] args) {
        JavaSpringDAO javaSpringDAO = new JavaSpringDAO();

//        System.out.println(javaSpringDAO.getById(16L));
//        System.out.println(javaSpringDAO.getByIdR(16L));
//        System.out.println(Arrays.toString(javaSpringDAO.getAll()));
        System.out.println(javaSpringDAO.getAllE());

//        Student student = new Student("Wood1", "wood@gmail.com");
//
//        System.out.println(javaSpringDAO.save(student));
//        student.setName("Wood2");
//        System.out.println(javaSpringDAO.saveE(student));
//        student.setName("Wood3");
//        System.out.println(javaSpringDAO.saveR(student));
    }


}
