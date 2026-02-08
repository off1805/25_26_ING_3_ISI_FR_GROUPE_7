package com.projetTransversalIsi.profil.application.services;

import com.projetTransversalIsi.profil.domain.Profile;
import com.projetTransversalIsi.profil.domain.ProfileProvider;
import com.projetTransversalIsi.security.domain.EnumRole;
import com.projetTransversalIsi.security.domain.Role;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DefaultProfileSelectionStrategy implements ProfileSelectionStrategy {
    private final Map<String, ProfileProvider> providerByRole;

    public DefaultProfileSelectionStrategy(List<ProfileProvider> profileProviders) {
        Map<String, ProfileProvider> registry = new HashMap<>();
        for (EnumRole enumRole : EnumRole.values()) {
            String roleName = enumRole.name();
            ProfileProvider provider = findProvider(profileProviders, roleName);
            if (provider != null) {
                if (registry.put(roleName, provider) != null) {
                    throw new IllegalStateException("Provider duplicate pour le rôle : " + roleName);
                }
            }
        }
        this.providerByRole = registry;
    }

    @Override
    public Profile selectProfileFor(Role role) {
        ProfileProvider provider = providerByRole.get(role.getName());
        if (provider == null) {
            throw new RuntimeException("Aucun profil pour le rôle : " + role.getName());
        }
        return provider.create();
    }

    private ProfileProvider findProvider(List<ProfileProvider> profileProviders, String roleName) {
        ProfileProvider match = null;
        for (ProfileProvider provider : profileProviders) {
            if (!provider.supports(roleName)) {
                continue;
            }
            if (match != null) {
                throw new IllegalStateException("Providers multiples pour le rôle : " + roleName);
            }
            match = provider;
        }
        return match;
    }
}
