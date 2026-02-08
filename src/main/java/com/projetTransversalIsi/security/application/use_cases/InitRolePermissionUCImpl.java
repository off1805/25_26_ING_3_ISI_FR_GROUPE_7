package com.projetTransversalIsi.security.application.use_cases;

import com.projetTransversalIsi.security.application.services.DefaultPermissionOperation;
import com.projetTransversalIsi.security.application.services.DefaultRoleOperation;
import com.projetTransversalIsi.security.domain.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class InitRolePermissionUCImpl implements InitRolePermissionUC{

    private final DefaultRoleOperation defaultRoleOp;
    private final DefaultPermissionOperation defaultPermissionOp;


    @Override
    @Transactional
    public void execute(){

            log.info("Vérification et initialisation de la base de données...");

            // 1. Création des Permissions par défaut
            if (defaultPermissionOp.count() == 0) {
                for(EnumPermission enumPerm: EnumPermission.values()){
                    Permission perm= enumPerm.toPermission();
                    defaultPermissionOp.registerNewPermission(perm);
                }
                log.info("Permissions par défaut créées.");
            }

            // 2. Création des Rôles par défaut
            if (defaultRoleOp.count() == 0) {

                for(EnumRole enumRole: EnumRole.values()){
                    log.info(enumRole.name());
                    Set<String> idPermissions = Arrays.stream(EnumPermission.values())
                                                        .filter(p->p.getRoleId()==enumRole)
                                                        .map(EnumPermission::name)
                                                        .collect(Collectors.toSet());
                    Role role= new Role(enumRole.name());
                    role.setIdPermissions(idPermissions);
                    log.info("0");
                   for(int i=0;i<role.getIdPermissions().size();i++){
                       log.info(i+ "- "+ idPermissions.stream().toList().get(i));
                   }

                    defaultRoleOp.registerNewRole(role);
                }

                log.info("Rôles par défaut créés.");
            }

            log.info("Initialisation terminée.");


    }


}
