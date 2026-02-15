package com.projetTransversalIsi.profil.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="surveillant_profile")
public class JpaSurveillantProfileEntity extends JpaProfileEntity{

}
