package com.projetTransversalIsi.emploi_temps.application.use_cases;

// Objet commande transportant le contexte d'un marquage de présence.
// Trois stratégies selon le type d'Appel :
//   - QR     : codeValeur non-null (UUID depuis l'URL de scan)
//   - PIN    : idCode non-null (id de l'AttendanceCode PIN)
//   - MANUEL : appelId non-null (id de l'Appel MANUEL, marqué directement par l'enseignant)
// MarkStudentPresentUCImpl choisit la stratégie selon lequel est non-null.
public record MarkStudentPresentCommand(Long idStudent, Long idCode, String codeValeur, Long appelId) {
}
