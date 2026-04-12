package com.projetTransversalIsi.emploi_temps.application.use_cases;

public record MarkStudentPresentCommand(Long idStudent, Long idCode, String codeValeur) {
    // idCode : utilisé pour le PIN (recherche par id)
    // codeValeur : utilisé pour le QR (recherche par token dans l'URL)
}
