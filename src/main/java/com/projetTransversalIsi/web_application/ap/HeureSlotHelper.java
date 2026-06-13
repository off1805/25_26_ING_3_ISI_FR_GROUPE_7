package com.projetTransversalIsi.web_application.ap;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Découpe une plage horaire de séance en créneaux d'une heure (le dernier
 * créneau pouvant être plus court) afin d'afficher une colonne par heure de
 * cours dans la liste de présence.
 */
public final class HeureSlotHelper {

    private HeureSlotHelper() {}

    public static List<String> computeCreneaux(LocalTime heureDebut, LocalTime heureFin) {
        List<String> creneaux = new ArrayList<>();
        if (heureDebut == null || heureFin == null || !heureDebut.isBefore(heureFin)) {
            return creneaux;
        }

        LocalTime cur = heureDebut;
        while (cur.isBefore(heureFin)) {
            LocalTime next = cur.plusHours(1);
            if (next.isAfter(heureFin)) next = heureFin;
            creneaux.add(format(cur) + "-" + format(next));
            cur = next;
        }
        return creneaux;
    }

    private static String format(LocalTime time) {
        return String.format("%02dh%02d", time.getHour(), time.getMinute());
    }
}
