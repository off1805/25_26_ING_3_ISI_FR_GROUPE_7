# 📊 DataTable - Solution Complète avec Backend

## 📦 Fichiers Fournis

| Fichier | Description |
|---------|------------|
| **DataTable.js** | Composant principal (réutilisable partout) |
| **DataTable-Backend-Example.html** | Démonstration complète avec API mock |
| **BACKEND-CALLBACKS-GUIDE.md** | Guide détaillé des callbacks backend |
| **KemOSchool-UserController-Backend.js** | Implémentation prête pour KemOSchool |

---

## 🚀 Démarrage Rapide

### 1. Activez le mode backend

```javascript
const table = new DataTable({
  isRemoteData: true,  // ← Active les appels API
  
  onSearch: async (query, filters, sort, page, itemsPerPage) => { /* ... */ },
  onFilter: async (filters, search, sort, page, itemsPerPage) => { /* ... */ },
  onSort: async (sort, search, filters, page, itemsPerPage) => { /* ... */ },
  onPageChange: async (page, itemsPerPage, search, filters, sort) => { /* ... */ },
  onItemsPerPageChange: async (itemsPerPage, page, search, filters, sort) => { /* ... */ },
});
```

### 2. Créez les endpoints backend

```
POST /api/users/search         → Recherche + filtre + tri + pagination
POST /api/users/filter         → Applique les filtres
POST /api/users/sort           → Effectue le tri
POST /api/users/page           → Change la page
POST /api/users/items-per-page → Change la limite par page
```

### 3. Retournez le format attendu

Tous les endpoints retournent:
```json
{
  "data": [ { id, email, roleName, status, ... } ],
  "totalItems": 150
}
```

---

## 🔄 Flux de Communication

```
FRONTEND                          BACKEND
═════════════════════════════════════════════════════

Utilisateur tape "alice"
  │
  ├─→ onSearch callback
  │   │
  │   └─→ POST /api/users/search
  │       { query: "alice", filters: {}, sort: null, page: 1, limit: 50 }
  │
  └─← Backend répond
      { data: [...], totalItems: 3 }
      │
      └─→ Affiche les résultats
          avec indicateur de chargement
```

---

## 💡 Cas d'Usage

### Mode Local (isRemoteData: false)
✅ Données petites/moyennes (<1000 items)
✅ Filtrage instant requis
✅ Application hors-ligne
❌ Pas d'efficacité pour gros volumes

### Mode Backend (isRemoteData: true)
✅ Données volumineuses (>10000 items)
✅ Données en temps réel
✅ Permissions complexes côté serveur
✅ Calculs serveur pour résultats
✅ Meilleure performance

---

## 📋 Exemple Complet: KemOSchool

### HTML Template (Thymeleaf)

```html
<!-- Dans votre page de gestion des utilisateurs -->

<div id="data-table-users"></div>

<!-- Modale d'édition (votre HTML existant) -->
<div id="hs-modal-edit-user">
  <!-- ... votre contenu ... -->
</div>

<!-- Scripts -->
<script th:src="@{/js/components/DataTable.js}"></script>
<script th:src="@{/js/components/KemOSchool-UserController-Backend.js}"></script>
```

### Backend - Endpoint Exemple (Spring Boot)

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

  @PostMapping("/search")
  public ResponseEntity<?> search(@RequestBody SearchRequest request) {
    // Construire la requête avec search, filters, sort
    List<User> users = userService.search(
      request.getQuery(),
      request.getFilters(),
      request.getSort(),
      request.getPage(),
      request.getLimit()
    );
    
    long totalItems = userService.count(request.getQuery(), request.getFilters());
    
    return ResponseEntity.ok(new DataTableResponse(users, totalItems));
  }

  @PostMapping("/filter")
  public ResponseEntity<?> filter(@RequestBody FilterRequest request) {
    List<User> users = userService.filter(
      request.getFilters(),
      request.getSearch(),
      request.getSort(),
      request.getPage(),
      request.getLimit()
    );
    
    long totalItems = userService.count(request.getSearch(), request.getFilters());
    
    return ResponseEntity.ok(new DataTableResponse(users, totalItems));
  }

  @PostMapping("/sort")
  public ResponseEntity<?> sort(@RequestBody SortRequest request) {
    List<User> users = userService.sort(
      request.getSort(),
      request.getSearch(),
      request.getFilters(),
      request.getPage(),
      request.getLimit()
    );
    
    long totalItems = userService.count(request.getSearch(), request.getFilters());
    
    return ResponseEntity.ok(new DataTableResponse(users, totalItems));
  }

  @PostMapping("/page")
  public ResponseEntity<?> changePage(@RequestBody PageRequest request) {
    List<User> users = userService.getPage(
      request.getPage(),
      request.getLimit(),
      request.getSearch(),
      request.getFilters(),
      request.getSort()
    );
    
    long totalItems = userService.count(request.getSearch(), request.getFilters());
    
    return ResponseEntity.ok(new DataTableResponse(users, totalItems));
  }
}

// DTO Response
public class DataTableResponse {
  public List<User> data;
  public long totalItems;
  
  public DataTableResponse(List<User> data, long totalItems) {
    this.data = data;
    this.totalItems = totalItems;
  }
}
```

---

## 🎯 Avantages vs Inconvénients

### Mode Backend: Avantages ✅

| Avantage | Explication |
|----------|------------|
| **Performance** | Ne charge que les données visibles |
| **Scalabilité** | Gère 1M+ enregistrements facilement |
| **Fraîcheur** | Toujours les données à jour |
| **Sécurité** | Permissions vérifiées côté serveur |
| **Flexibilité** | Logique métier complexe au serveur |

### Mode Backend: Inconvénients ❌

| Inconvénient | Solution |
|-------------|----------|
| **Latence réseau** | Débouncer les entrées, indiquer le chargement |
| **Offline** | Implémenter un cache/fallback |
| **Bande passante** | Compresser, paginer, limiter les champs |

---

## 🔧 Checklist d'Implémentation

### Frontend
- [ ] Copier `DataTable.js` dans `/js/components/`
- [ ] Inclure le script dans votre template
- [ ] Remplacer le tableau HTML par `<div id="data-table-users"></div>`
- [ ] Copier `KemOSchool-UserController-Backend.js` ou adapter la logique
- [ ] Tester avec la page exemple HTML

### Backend
- [ ] Créer endpoint `POST /api/users/search`
- [ ] Créer endpoint `POST /api/users/filter`
- [ ] Créer endpoint `POST /api/users/sort`
- [ ] Créer endpoint `POST /api/users/page`
- [ ] Créer endpoint `POST /api/users/items-per-page`
- [ ] Tous retournent `{ data: [...], totalItems: N }`
- [ ] Tester avec Postman/Thunder Client
- [ ] Vérifier les permissions de l'utilisateur
- [ ] Optimiser les requêtes DB (index, lazy loading, etc.)

### Testing
- [ ] Recherche fonctionne
- [ ] Filtres fonctionnent
- [ ] Tri fonctionne
- [ ] Pagination fonctionne
- [ ] Changer items/page fonctionne
- [ ] Combinaisons (recherche + filtre + tri) fonctionnent
- [ ] Gestion d'erreur (timeout, 500)
- [ ] Indicateur de chargement s'affiche

---

## 📊 Architecture Globale

```
┌─────────────────────────────────────────────────────────┐
│                   KemOSchool Frontend                    │
│                                                           │
│  ┌─────────────────────────────────────────────────────┐ │
│  │               DataTable Component                    │ │
│  │                                                       │ │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐          │ │
│  │  │ Recherche│  │ Filtres  │  │   Tri    │          │ │
│  │  └─────┬────┘  └─────┬────┘  └─────┬────┘          │ │
│  │        │             │              │               │ │
│  │        └─────────────┴──────────────┘               │ │
│  │                      │                              │ │
│  │                  Callbacks API                      │ │
│  │                      │                              │ │
│  │        ┌─────────────┴──────────────┐              │ │
│  │        │      Pagination            │              │ │
│  │        └─────────────────────────────┘             │ │
│  └─────────────────────────────────────────────────────┘ │
│                                                           │
└───────────────────────────┬───────────────────────────────┘
                            │
                      HTTP Requests
                      (POST /api/users/*)
                            │
         ┌──────────────────┴──────────────────┐
         │                                      │
┌─────────▼──────────────────────────────────┐ │
│         KemOSchool Backend (Spring Boot)   │ │
│                                            │ │
│  ┌────────────────────────────────────┐   │ │
│  │      UserController                │   │ │
│  │                                    │   │ │
│  │  - search()                        │   │ │
│  │  - filter()                        │   │ │
│  │  - sort()                          │   │ │
│  │  - page()                          │   │ │
│  │  - itemsPerPage()                  │   │ │
│  └────────────┬───────────────────────┘   │ │
│               │                            │ │
│  ┌────────────▼───────────────────────┐   │ │
│  │      UserService                   │   │ │
│  │                                    │   │ │
│  │  - Filtrage                        │   │ │
│  │  - Tri (optimisé DB)               │   │ │
│  │  - Permissions                     │   │ │
│  │  - Pagination                      │   │ │
│  └────────────┬───────────────────────┘   │ │
│               │                            │ │
│  ┌────────────▼───────────────────────┐   │ │
│  │   User Repository (JPA)            │   │ │
│  │                                    │   │ │
│  │   (Specification, Pageable)        │   │ │
│  └────────────┬───────────────────────┘   │ │
│               │                            │ │
│  ┌────────────▼───────────────────────┐   │ │
│  │      PostgreSQL Database           │   │ │
│  │   (avec indexes sur role, status)  │   │ │
│  └────────────────────────────────────┘   │ │
│                                            │ │
└──────────────┬───────────────────────────┬─┘
               │                           │
         JSON Response              HTTP 200
         { data, totalItems }
```

---

## 🎓 Ressources

1. **DataTable-Backend-Example.html** - Testez l'exemple avec API mock
2. **BACKEND-CALLBACKS-GUIDE.md** - Guide détaillé des callbacks
3. **KemOSchool-UserController-Backend.js** - Code prêt à copier

---

## ❓ Questions Fréquentes

**Q: Comment gérer les erreurs réseau?**
A: Les callbacks lèvent une exception, qui est automatiquement affichée

**Q: Puis-je combiner recherche + filtre + tri + pagination?**
A: Oui, tous les callbacks reçoivent tous les paramètres

**Q: Comment mettre en cache les résultats?**
A: Implémentez un cache avec clé = JSON.stringify(params)

**Q: Puis-je avoir un timeout sur les appels?**
A: Enveloppez fetch avec AbortController

**Q: Le tri peut-il être multicolonne?**
A: Oui, modifiez la structure de `sort` pour `[{ key, direction }]`

---

## 📞 Support & Aide

Consultez les fichiers fournis:
1. Ouvrez `DataTable-Backend-Example.html` dans le navigateur
2. Inspectez la console pour voir les logs d'appel API
3. Lisez `BACKEND-CALLBACKS-GUIDE.md` pour les détails
4. Adaptez `KemOSchool-UserController-Backend.js` à vos besoins

---

**Version:** 1.0  
**Dernière mise à jour:** 2024  
**Licence:** MIT
