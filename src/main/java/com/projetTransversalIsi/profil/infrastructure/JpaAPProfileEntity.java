package com.projetTransversalIsi.profil.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="ap_profile")
public class JpaAPProfileEntity extends JpaProfileEntity{
}
