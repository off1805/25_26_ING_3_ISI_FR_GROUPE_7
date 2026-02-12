package com.projetTransversalIsi.authentification.infrastructure;

import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.authentification.domain.RefreshTokenRepository;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaRefreshTokenRepository implements RefreshTokenRepository {

    private  final EntityManager entityManager;
    private final SpringDataRefreshTokenRepository sprgDataReTk;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public Optional<RefreshToken> getRefreshTokenById(String id){
        return sprgDataReTk.findById(id).map(refreshTokenMapper::JpaRefreshTokenToRefreshToken);
    }
    @Override
    public RefreshToken saveRefreshToken(RefreshToken rToken){
        JpaUserEntity jpaUser= entityManager.getReference(JpaUserEntity.class,rToken.getUserId());
        JpaRefreshTokenEntity rTokenEntity= refreshTokenMapper.RefreshTokenToJpaRefreshToken(rToken,jpaUser);
         return refreshTokenMapper.JpaRefreshTokenToRefreshToken( sprgDataReTk.save(rTokenEntity));
    }
}
