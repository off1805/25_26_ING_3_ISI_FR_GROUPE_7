package com.projetTransversalIsi.security.infrastructure;



import com.projetTransversalIsi.security.application.use_cases.InitRolePermissionUC;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.security.domain.PermissionRepository; // À adapter selon tes noms
import com.projetTransversalIsi.security.domain.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class Config {

    private final InitRolePermissionUC initRolePerm;

    @Bean
    CommandLineRunner initDataBase(){
        return args -> {
            initRolePerm.execute();
        };
    }


}
