package com.projetTransversalIsi.cycle.domain;

import com.projetTransversalIsi.Filiere.domain.Filiere;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Cycle {

    private Long id;
    private String name;
    private String code;
    private int durationYears;
    private String description;
    private CycleStatus status;
    private boolean deleted = false;
    private LocalDateTime deletedAt;

    public Cycle(String name, String code, int durationYears, String description) {
        this.name = name;
        this.code = code;
        this.durationYears = durationYears;
        this.description = description;
        this.status = CycleStatus.ACTIVE;
        this.deleted = false;
    }

    public Cycle(Long id, String name, String code, int durationYears, String description,
                 CycleStatus status, boolean deleted, LocalDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.durationYears = durationYears;
        this.description = description;
        this.status = status;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public boolean isActive() {
        return this.status == CycleStatus.ACTIVE;
    }

    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
    }

    public void deactivate() {
        this.status = CycleStatus.INACTIVE;
    }

    public void activate() {
        this.status = CycleStatus.ACTIVE;
    }

}
