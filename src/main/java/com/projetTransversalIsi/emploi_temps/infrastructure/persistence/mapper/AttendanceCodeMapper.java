package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.mapper;

import com.projetTransversalIsi.emploi_temps.domain.model.AttendanceCode;
import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaAttendanceCodeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttendanceCodeMapper {
    JpaAttendanceCodeEntity toEntity(AttendanceCode domain);
    AttendanceCode toDomain(JpaAttendanceCodeEntity entity);
}
