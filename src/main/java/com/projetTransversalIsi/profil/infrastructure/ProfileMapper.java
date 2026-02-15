package com.projetTransversalIsi.profil.infrastructure;

import com.projetTransversalIsi.profil.domain.*;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.SubclassMapping;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @SubclassMapping(source = StudentProfile.class, target = JpaStudentProfileEntity.class)
    @SubclassMapping(source = TeacherProfile.class, target = JpaTeacherProfileEntity.class)
    @SubclassMapping(source = AdminProfile.class, target = JpaAdminProfileEntity.class)
    @SubclassMapping(source = SurveillantProfile.class, target = JpaSurveillantProfileEntity.class)
    @SubclassMapping(source = APProfile.class , target = JpaAPProfileEntity.class)
    JpaProfileEntity profiletoJpaProfileEntity(Profile profile);

    @SubclassMapping(source = JpaStudentProfileEntity.class, target = StudentProfile.class)
    @SubclassMapping(source = JpaTeacherProfileEntity.class, target = TeacherProfile.class)
    @SubclassMapping(source = JpaAdminProfileEntity.class, target = AdminProfile.class)
    @SubclassMapping(source = JpaSurveillantProfileEntity.class, target = SurveillantProfile.class)
    @SubclassMapping(source = JpaAPProfileEntity.class, target = APProfile.class)
    Profile jpaProfileEntitytoDomain(JpaProfileEntity entity);



    JpaStudentProfileEntity studentProfilToStudentEntity(StudentProfile student);
    JpaTeacherProfileEntity teacherProfileToTeacherEntity(TeacherProfile teacher);

    StudentProfile jpaStudentProfileToStudentDomain(JpaStudentProfileEntity entity);
    TeacherProfile jpaTeacherProfileToTeacherDomain(JpaTeacherProfileEntity entity);


    @ObjectFactory
    default JpaProfileEntity createJpaProfileEntity(Profile profile) {
        if (profile instanceof StudentProfile) {
            return new JpaStudentProfileEntity();
        }
        if (profile instanceof TeacherProfile) {
            return new JpaTeacherProfileEntity();
        }
        if(profile instanceof AdminProfile){
            return new JpaAdminProfileEntity();
        }
        if(profile instanceof SurveillantProfile){
            return new JpaSurveillantProfileEntity();
        }
        if(profile instanceof APProfile){
            return new JpaAPProfileEntity();
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
        if (jpaProfile instanceof JpaAdminProfileEntity) {
            return new AdminProfile();
        }
        if (jpaProfile instanceof JpaSurveillantProfileEntity) {
            return new SurveillantProfile();
        }
        if (jpaProfile instanceof JpaAPProfileEntity) {
            return new APProfile();
        }
        throw new IllegalArgumentException("Type de JPAProfile non supporté: " + jpaProfile.getClass());
    }
}
