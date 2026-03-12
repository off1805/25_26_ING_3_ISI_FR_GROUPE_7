package com.projetTransversalIsi.school.infrastructure;

import com.projetTransversalIsi.school.domain.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor

public class JpaSchoolRepository implements SchoolRepository {
    private final SpringDataSchoolRepository schoolRepo;
}
