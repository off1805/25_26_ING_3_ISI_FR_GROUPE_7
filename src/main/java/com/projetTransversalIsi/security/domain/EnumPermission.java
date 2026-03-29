package com.projetTransversalIsi.security.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumPermission {

    MANAGE_STAFF(EnumRole.SURVEILLANT, "Creer, modifier ou supprimer un membre du personnel"),




    // ================= STUDENTS =================

    VIEW_STUDENT(EnumRole.AP, "Voir les informations d'un étudiant"),
    UPDATE_STUDENT(EnumRole.AP, "Modifier les informations d'un étudiant"),
    DELETE_STUDENT(EnumRole.AP, "Supprimer un étudiant"),
    IMPORT_STUDENTS_TO_CLASS(EnumRole.AP, "Importer des étudiants dans une classe"),
    EXPORT_STUDENTS_FROM_CLASS(EnumRole.AP, "Exporter les étudiants d'une classe"),
    VIEW_STUDENT_STATISTICS(EnumRole.AP, "Voir les statistiques d'un étudiant"),


    // ================= CLASSES =================
    CREATE_CLASS(EnumRole.AP, "Créer une classe"),
    VIEW_CLASS(EnumRole.AP, "Voir une classe"),
    UPDATE_CLASS(EnumRole.AP, "Modifier une classe"),
    DELETE_CLASS(EnumRole.AP, "Supprimer une classe"),
    VIEW_CLASS_STATISTICS(EnumRole.AP, "Voir les statistiques d'une classe"),


    // ================= TIMETABLE =================
    CREATE_TIMETABLE(EnumRole.AP, "Créer un emploi du temps"),
    VIEW_TIMETABLE(EnumRole.AP, "Voir un emploi du temps"),
    UPDATE_TIMETABLE(EnumRole.AP, "Modifier un emploi du temps"),
    DELETE_TIMETABLE(EnumRole.AP, "Supprimer un emploi du temps"),


    // ================= JUSTIFICATION REQUEST =================

    VIEW_JUSTIFICATION_REQUEST(EnumRole.AP, "Voir les requêtes de justificatif"),
    UPDATE_JUSTIFICATION_REQUEST(EnumRole.AP, "Modifier une requête de justificatif"),
    DELETE_JUSTIFICATION_REQUEST(EnumRole.AP, "Supprimer une requête de justificatif"),
    APPROVE_JUSTIFICATION_REQUEST(EnumRole.AP, "Valider une requête de justificatif"),
    REJECT_JUSTIFICATION_REQUEST(EnumRole.AP, "Refuser une requête de justificatif"),


    // ================= UE =================
    CREATE_UE(EnumRole.AP, "Créer une UE"),
    VIEW_UE(EnumRole.AP, "Voir une UE"),
    UPDATE_UE(EnumRole.AP, "Modifier une UE"),
    DELETE_UE(EnumRole.AP, "Supprimer une UE"),
    VIEW_UE_STATISTICS(EnumRole.AP, "Voir les statistiques des UE"),


    // ================= TEACHERS =================

    VIEW_TEACHER(EnumRole.AP, "Voir un professeur"),
    UPDATE_TEACHER(EnumRole.AP, "Modifier un professeur"),
    DELETE_TEACHER(EnumRole.AP, "Supprimer un professeur"),
    VIEW_TEACHER_STATISTICS(EnumRole.AP, "Voir les statistiques des professeurs"),


    DEFAULT_STUDENT(EnumRole.STUDENT, "Accéder aux cours et aux annonces"),
    MAKE_CALL(EnumRole.TEACHER, "Faire un appel");

    private final EnumRole roleId;
    private final String label;

    public Permission toPermission() {
        return new Permission(this.name(), this.label);
    }
}
