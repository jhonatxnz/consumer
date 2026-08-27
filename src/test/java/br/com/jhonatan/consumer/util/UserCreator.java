package br.com.jhonatan.consumer.util;

import br.com.jhonatan.consumer.model.Users;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserCreator {
    public static Users createUserToBeSaved(){
        return Users.builder()
                .name("User test")
                .phone("19987123232")
                .email("email.example@gmail.com")
                .document("53223472112")
                .status("1")
                .birthDate(LocalDate.parse("2005-01-21"))
                .gender("Male")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Users createValidUser(){
        return Users.builder()
                .id(1L)
                .name("User test")
                .phone("19987123232")
                .email("email.example@gmail.com")
                .document("53223472112")
                .status("1")
                .birthDate(LocalDate.parse("2005-01-21"))
                .gender("Male")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
