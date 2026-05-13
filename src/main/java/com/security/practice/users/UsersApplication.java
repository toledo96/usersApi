package com.security.practice.users;

import com.security.practice.users.model.Rol;
import com.security.practice.users.model.User;
import com.security.practice.users.model.UserRole;
import com.security.practice.users.repository.RolRepository;
import com.security.practice.users.repository.UserRepository;
import com.security.practice.users.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
public class UsersApplication {


	public static void main(String[] args) {
		SpringApplication.run(UsersApplication.class, args);
	}

}
