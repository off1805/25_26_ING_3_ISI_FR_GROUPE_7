package com.projetTransversalIsi.config;

import com.projetTransversalIsi.security.application.use_cases.InitRolePermissionUC;
import com.projetTransversalIsi.structure_academique.domain.model.CycleStatus;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaCycleEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaFiliereEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaNiveauEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaSpecialiteEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaSchoolEntity;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataClasseRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataCycleRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataFiliereRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataNiveauRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataSchoolRepository;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataSpecialiteRepository;
import com.projetTransversalIsi.user.dto.CreateUserRequestDTO;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import com.projetTransversalIsi.user.profil.application.ProfilCreationDTO;
import com.projetTransversalIsi.user.services.CreateUserUC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final SpringDataCycleRepository cycleRepo;
    private final SpringDataFiliereRepository filiereRepo;
    private final SpringDataNiveauRepository niveauRepo;
    private final SpringDataSpecialiteRepository specialiteRepo;
    private final SpringDataClasseRepository classeRepo;
    private final SpringDataUserRepository userRepo;
    private final SpringDataSchoolRepository schoolRepo;
    private final InitRolePermissionUC initRolePermissionUC;
    private final CreateUserUC createUserUC;

    @Override
    public void run(ApplicationArguments args) {
        initRolePermissionUC.execute();
        if (schoolRepo.count() == 0) {
            log.info("Création de l'école par défaut...");
            JpaSchoolEntity school = new JpaSchoolEntity();
            school.setName("Saint Jean Ingénieur");
            school.setLatitude(0.0);
            school.setLongitude(0.0);
            school.setRayon(200.0);
            schoolRepo.save(school);
        }
        if (cycleRepo.count() == 0) {
            log.info("Initialisation de la structure académique...");
            seedStructure();
        }
        if (userRepo.count() == 0) {
            log.info("Initialisation des utilisateurs par défaut...");
            seedUsers();
        }
    }

    private void seedUsers() {
        Long isiFiliereId = filiereRepo.findByCode("ISI").map(JpaFiliereEntity::getId).orElse(null);

        seedDefaultUser("admin@saintjean.sn",           "ADMIN",       "ADM001", "Administrateur", "Système",   "770000000", null);
        seedDefaultUser("diallo.mamadou@saintjean.sn",  "TEACHER",     "ENS001", "Diallo",         "Mamadou",   "771111111", null);
        seedDefaultUser("sow.fatou@saintjean.sn",       "TEACHER",     "ENS002", "Sow",            "Fatou",     "772222222", null);
        seedDefaultUser("ndiaye.ibrahima@saintjean.sn", "TEACHER",     "ENS003", "Ndiaye",         "Ibrahima",  "773333333", null);
        seedDefaultUser("ap@saintjean.sn",              "AP",          "AP001",  "Ba",             "Aminata",   "774444444", isiFiliereId);
        seedDefaultUser("surveillant@saintjean.sn",     "SURVEILLANT", "SUR001", "Fall",           "Ousmane",   "775555555", null);
    }

    private void seedDefaultUser(String email, String role, String mat,
                                  String nom, String prenom, String tel, Long filiereId) {
        if (userRepo.existsByEmail(email)) return;
        ProfilCreationDTO profil = new ProfilCreationDTO();
        profil.setMatricule(mat);
        profil.setNom(nom);
        profil.setPrenom(prenom);
        profil.setNumeroTelephone(tel);
        profil.setFiliereId(filiereId);
        try {
            createUserUC.execute(new CreateUserRequestDTO(email, role, Set.of(), profil));
            log.info("Utilisateur créé : {}", email);
        } catch (Exception e) {
            log.warn("Échec création utilisateur {} : {}", email, e.getMessage());
        }
    }

    private void seedStructure() {
        JpaCycleEntity ing = createCycle("Ingénieur", "ING", 5, "Cycle Ingénieur de conception");
        JpaCycleEntity lic = createCycle("Licence",   "LIC", 3, "Cycle Licence professionnelle");

        JpaFiliereEntity isi    = createFiliere("ISI", "Informatique et Sciences de l'Ingénieur", ing);
        JpaFiliereEntity srt    = createFiliere("SRT", "Systèmes et Réseaux de Télécommunication", ing);
        JpaFiliereEntity gc     = createFiliere("GC",  "Génie Civil", ing);
        JpaFiliereEntity ge     = createFiliere("GE",  "Génie Électrique", ing);
        JpaFiliereEntity licIsi = createFiliere("LMG", "Licence en Management et Gestion", lic);

        JpaNiveauEntity isi1 = createNiveau(1, "ING1 - Tronc commun", isi);
        JpaNiveauEntity isi2 = createNiveau(2, "ING2 - Tronc commun", isi);
        JpaNiveauEntity isi3 = createNiveau(3, "ING3 - Spécialisation", isi);
        JpaNiveauEntity isi4 = createNiveau(4, "ING4 - Spécialisation", isi);
        JpaNiveauEntity isi5 = createNiveau(5, "ING5 - Spécialisation", isi);

        JpaNiveauEntity srt1 = createNiveau(1, "ING1 - Tronc commun", srt);
        JpaNiveauEntity srt2 = createNiveau(2, "ING2 - Tronc commun", srt);
        JpaNiveauEntity srt3 = createNiveau(3, "ING3 - Spécialisation", srt);
        JpaNiveauEntity srt4 = createNiveau(4, "ING4 - Spécialisation", srt);
        JpaNiveauEntity srt5 = createNiveau(5, "ING5 - Spécialisation", srt);

        JpaNiveauEntity gc1 = createNiveau(1, "ING1", gc);
        JpaNiveauEntity gc2 = createNiveau(2, "ING2", gc);
        JpaNiveauEntity gc3 = createNiveau(3, "ING3", gc);
        JpaNiveauEntity gc4 = createNiveau(4, "ING4", gc);
        JpaNiveauEntity gc5 = createNiveau(5, "ING5", gc);

        JpaNiveauEntity ge1 = createNiveau(1, "ING1", ge);
        JpaNiveauEntity ge2 = createNiveau(2, "ING2", ge);
        JpaNiveauEntity ge3 = createNiveau(3, "ING3", ge);
        JpaNiveauEntity ge4 = createNiveau(4, "ING4", ge);
        JpaNiveauEntity ge5 = createNiveau(5, "ING5", ge);

        JpaNiveauEntity l1 = createNiveau(1, "Licence 1", licIsi);
        JpaNiveauEntity l2 = createNiveau(2, "Licence 2", licIsi);
        JpaNiveauEntity l3 = createNiveau(3, "Licence 3", licIsi);

        JpaSpecialiteEntity isiTc1 = createSpecialite("ISI-TC1", "Tronc Commun ISI", isi1);
        JpaSpecialiteEntity isiTc2 = createSpecialite("ISI-TC2", "Tronc Commun ISI", isi2);
        JpaSpecialiteEntity il3    = createSpecialite("ISI-IL3",  "Ingénierie Logicielle", isi3);
        JpaSpecialiteEntity msi3   = createSpecialite("ISI-MSI3", "Management des Systèmes d'Information", isi3);
        JpaSpecialiteEntity il4    = createSpecialite("ISI-IL4",  "Ingénierie Logicielle", isi4);
        JpaSpecialiteEntity msi4   = createSpecialite("ISI-MSI4", "Management des Systèmes d'Information", isi4);
        JpaSpecialiteEntity il5    = createSpecialite("ISI-IL5",  "Ingénierie Logicielle", isi5);
        JpaSpecialiteEntity msi5   = createSpecialite("ISI-MSI5", "Management des Systèmes d'Information", isi5);

        JpaSpecialiteEntity srtTc1 = createSpecialite("SRT-TC1",  "Tronc Commun SRT", srt1);
        JpaSpecialiteEntity srtTc2 = createSpecialite("SRT-TC2",  "Tronc Commun SRT", srt2);
        JpaSpecialiteEntity tel3   = createSpecialite("SRT-TEL3", "Télécommunications", srt3);
        JpaSpecialiteEntity res3   = createSpecialite("SRT-RES3", "Réseaux", srt3);
        JpaSpecialiteEntity tel4   = createSpecialite("SRT-TEL4", "Télécommunications", srt4);
        JpaSpecialiteEntity res4   = createSpecialite("SRT-RES4", "Réseaux", srt4);
        JpaSpecialiteEntity tel5   = createSpecialite("SRT-TEL5", "Télécommunications", srt5);
        JpaSpecialiteEntity res5   = createSpecialite("SRT-RES5", "Réseaux", srt5);

        JpaSpecialiteEntity gcS1 = createSpecialite("GC1", "Génie Civil", gc1);
        JpaSpecialiteEntity gcS2 = createSpecialite("GC2", "Génie Civil", gc2);
        JpaSpecialiteEntity gcS3 = createSpecialite("GC3", "Génie Civil", gc3);
        JpaSpecialiteEntity gcS4 = createSpecialite("GC4", "Génie Civil", gc4);
        JpaSpecialiteEntity gcS5 = createSpecialite("GC5", "Génie Civil", gc5);

        JpaSpecialiteEntity geS1 = createSpecialite("GE1", "Génie Électrique", ge1);
        JpaSpecialiteEntity geS2 = createSpecialite("GE2", "Génie Électrique", ge2);
        JpaSpecialiteEntity geS3 = createSpecialite("GE3", "Génie Électrique", ge3);
        JpaSpecialiteEntity geS4 = createSpecialite("GE4", "Génie Électrique", ge4);
        JpaSpecialiteEntity geS5 = createSpecialite("GE5", "Génie Électrique", ge5);

        JpaSpecialiteEntity licS1 = createSpecialite("LIC1", "Management et Gestion L1", l1);
        JpaSpecialiteEntity licS2 = createSpecialite("LIC2", "Management et Gestion L2", l2);
        JpaSpecialiteEntity licS3 = createSpecialite("LIC3", "Management et Gestion L3", l3);

        createClasse("ING1-FR",      "ING1 Groupe Francophone",  isiTc1);
        createClasse("ING2-FR",      "ING2 Groupe Francophone",  isiTc2);
        createClasse("ISI3-IL-FR",   "ISI ING3 IL Francophone",  il3);
        createClasse("ISI3-MSI-FR",  "ISI ING3 MSI Francophone", msi3);
        createClasse("ISI4-IL-FR",   "ISI ING4 IL Francophone",  il4);
        createClasse("ISI4-MSI-FR",  "ISI ING4 MSI Francophone", msi4);
        createClasse("ISI5-IL-FR",   "ISI ING5 IL Francophone",  il5);
        createClasse("ISI5-MSI-FR",  "ISI ING5 MSI Francophone", msi5);
        createClasse("SRT1-FR",      "SRT ING1 Francophone",     srtTc1);
        createClasse("SRT2-FR",      "SRT ING2 Francophone",     srtTc2);
        createClasse("SRT3-TEL-FR",  "SRT ING3 Télécom",         tel3);
        createClasse("SRT3-RES-FR",  "SRT ING3 Réseaux",         res3);
        createClasse("SRT4-TEL-FR",  "SRT ING4 Télécom",         tel4);
        createClasse("SRT4-RES-FR",  "SRT ING4 Réseaux",         res4);
        createClasse("SRT5-TEL-FR",  "SRT ING5 Télécom",         tel5);
        createClasse("SRT5-RES-FR",  "SRT ING5 Réseaux",         res5);
        createClasse("GC1-FR",  "GC ING1", gcS1);
        createClasse("GC2-FR",  "GC ING2", gcS2);
        createClasse("GC3-FR",  "GC ING3", gcS3);
        createClasse("GC4-FR",  "GC ING4", gcS4);
        createClasse("GC5-FR",  "GC ING5", gcS5);
        createClasse("GE1-FR",  "GE ING1", geS1);
        createClasse("GE2-FR",  "GE ING2", geS2);
        createClasse("GE3-FR",  "GE ING3", geS3);
        createClasse("GE4-FR",  "GE ING4", geS4);
        createClasse("GE5-FR",  "GE ING5", geS5);
        createClasse("LIC1-FR", "Licence 1", licS1);
        createClasse("LIC2-FR", "Licence 2", licS2);
        createClasse("LIC3-FR", "Licence 3", licS3);
    }

    private JpaCycleEntity createCycle(String name, String code, int years, String desc) {
        if (cycleRepo.existsByCode(code)) return cycleRepo.findByCode(code).orElseThrow();
        JpaCycleEntity c = new JpaCycleEntity();
        c.setName(name); c.setCode(code); c.setDurationYears(years);
        c.setDescription(desc); c.setStatus(CycleStatus.ACTIVE);
        return cycleRepo.save(c);
    }

    private JpaFiliereEntity createFiliere(String code, String nom, JpaCycleEntity cycle) {
        if (filiereRepo.existsByCode(code)) return filiereRepo.findByCode(code).orElseThrow();
        JpaFiliereEntity f = new JpaFiliereEntity();
        f.setCode(code); f.setNom(nom); f.setCycle(cycle);
        return filiereRepo.save(f);
    }

    private JpaNiveauEntity createNiveau(int ordre, String desc, JpaFiliereEntity filiere) {
        JpaNiveauEntity n = new JpaNiveauEntity();
        n.setOrdre(ordre); n.setDescription(desc); n.setFiliere(filiere);
        return niveauRepo.save(n);
    }

    private JpaSpecialiteEntity createSpecialite(String code, String libelle, JpaNiveauEntity niveau) {
        if (specialiteRepo.existsByCode(code.toUpperCase())) return specialiteRepo.findByCode(code.toUpperCase()).orElseThrow();
        JpaSpecialiteEntity s = new JpaSpecialiteEntity();
        s.setCode(code); s.setLibelle(libelle); s.setNiveau(niveau);
        return specialiteRepo.save(s);
    }

    private void createClasse(String code, String desc, JpaSpecialiteEntity specialite) {
        if (classeRepo.existsByCode(code.toUpperCase())) return;
        JpaClasseEntity c = new JpaClasseEntity();
        c.setCode(code); c.setDescription(desc); c.setSpecialite(specialite);
        classeRepo.save(c);
    }
}
