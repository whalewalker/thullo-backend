package com.thullo.config;

import com.thullo.data.repository.UserRepository;
import com.thullo.security.CustomUserDetailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;

@Configuration
public class AppBeans {

//    @Autowired
//    private UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        return mapper;
    }

//    @Bean
//    public CustomUserDetailService customUserDetailService(){
//        return new CustomUserDetailService();
//    }

//    @Bean
//    public UserRepository userRepository(EntityManager entityManager){
//        return new JpaRepositoryFactory(entityManager).getRepository(UserRepository.class);
//    }
}
