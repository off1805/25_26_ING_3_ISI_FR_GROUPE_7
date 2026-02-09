package com.projetTransversalIsi.profil.infrastructure;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.profil.domain.StudentProfile;
import com.projetTransversalIsi.profil.domain.TeacherProfile;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.SubclassMapping;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @SubclassMapping(source = StudentProfile.class, target = JpaStudentProfileEntity.class)
    @SubclassMapping(source = TeacherProfile.class, target = JpaTeacherProfileEntity.class)
    JpaProfileEntity profiletoJpaProfileEntity(Profile profile);

    @SubclassMapping(source = JpaStudentProfileEntity.class, target = StudentProfile.class)
    @SubclassMapping(source = JpaTeacherProfileEntity.class, target = TeacherProfile.class)
    Profile jpaProfileEntitytoDomain(JpaProfileEntity entity);

    JpaStudentProfileEntity studentProfilToStudentEntity(StudentProfile student);
    JpaTeacherProfileEntity teacherProfileToTeacherEntity(TeacherProfile teacher);

    StudentProfile jpaStudentProfileToStudentDomain(JpaStudentProfileEntity entity);
    TeacherProfile jpaTeacherProfileToTeacherDomain(JpaTeacherProfileEntity entity);

    // ⭐ LA PIÈCE MANQUANTE
    @ObjectFactory
    default JpaProfileEntity createJpaProfileEntity(Profile profile) {
        if (profile instanceof StudentProfile) {
            return new JpaStudentProfileEntity();
        }
        if (profile instanceof TeacherProfile) {
            return new JpaTeacherProfileEntity();
        }
        throw new IllegalArgumentException("Type de Profile non supporté: " + profile.getClass());
    }
    @ObjectFactory
    default Profile createJpaProfileEntity(JpaProfileEntity jpaProfile) {
        if (jpaProfile instanceof JpaStudentProfileEntity) {
            return new StudentProfile();
        }
        if (jpaProfile instanceof JpaTeacherProfileEntity) {
            return new TeacherProfile();
        }
        throw new IllegalArgumentException("Type de Profile non supporté: " + jpaProfile.getClass());
    }
}
