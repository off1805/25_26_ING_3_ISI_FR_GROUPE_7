# DataTable - Guide des Callbacks Backend

Le DataTable supporte deux modes de fonctionnement:

1. **Mode local** (`isRemoteData: false`) - Filtrage/tri client-side
2. **Mode backend** (`isRemoteData: true`) - Appels API pour chaque opération

Ce guide couvre le **Mode Backend**.

---

## 🚀 Activation du mode Backend

```javascript
const table = new DataTable({
  containerId: 'my-table',
  isRemoteData: true,  // ← Active le mode backend
  
  // Les callbacks seront appelés automatiquement
  onSearch: async (query, filters, sort, page, itemsPerPage) => { ... },
  onFilter: async (filters, search, sort, page, itemsPerPage) => { ... },
  onSort: async (sort, search, filters, page, itemsPerPage) => { ... },
  onPageChange: async (page, itemsPerPage, search, filters, sort) => { ... },
  onItemsPerPageChange: async (itemsPerPage, page, search, filters, sort) => { ... },
});
```

---

## 📍 Quand chaque callback est appelé

| Événement | Callback | Déclencheur |
|-----------|----------|------------|
| Recherche change | `onSearch` | L'utilisateur tape dans le champ de recherche |
| Filtre change | `onFilter` | L'utilisateur sélectionne un filtre |
| Tri change | `onSort` | L'utilisateur clique sur un en-tête de colonne |
| Page change | `onPageChange` | L'utilisateur clique sur les boutons précédent/suivant |
| Items/page change | `onItemsPerPageChange` | L'utilisateur change le nombre d'items par page |

---

## 📋 Signature des Callbacks

### `onSearch(query, filters, sort, page, itemsPerPage)`

Appelé quand la recherche change.

**Paramètres:**
- `query` (string) - Texte de recherche saisi par l'utilisateur
- `filters` (object) - Filtres actuels: `{ role: 'TEACHER', status: 'ACTIVE' }`
- `sort` (object|null) - Tri actuel: `{ key: 'email', direction: 'asc' }` ou null
- `page` (number) - Page actuelle (1-indexed)
- `itemsPerPage` (number) - Nombre d'items par page

**Retour attendu:**
```javascript
{
  data: [],        // Résultats paginés
  totalItems: 123  // Nombre total d'items (avant pagination)
}
```

**Exemple:**
```javascript
onSearch: async (query, filters, sort, page, itemsPerPage) => {
  console.log('Recherche:', query);
  
  const response = await fetch('/api/users/search', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      query,
      filters,
      sort,
      page,
      limit: itemsPerPage,
    }),
  });
  
  return await response.json();
  // { "data": [...], "totalItems": 150 }
}
```

---

### `onFilter(filters, search, sort, page, itemsPerPage)`

Appelé quand un filtre change (select, dropdown, etc.).

**Paramètres:**
- `filters` (object) - Les nouveaux filtres: `{ role: 'TEACHER', status: 'ACTIVE' }`
- `search` (string) - Recherche actuelle
- `sort` (object|null) - Tri actuel
- `page` (number) - Page (réinitialisée à 1)
- `itemsPerPage` (number) - Nombre d'items par page

**Retour:**
```javascript
{ data: [...], totalItems: 45 }
```

**Exemple:**
```javascript
onFilter: async (filters, search, sort, page, itemsPerPage) => {
  console.log('Filtre appliqué:', filters);
  
  const response = await fetch('/api/users/filter', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      filters,
      search,
      sort,
      page,
      limit: itemsPerPage,
    }),
  });
  
  return await response.json();
}
```

---

### `onSort(sort, search, filters, page, itemsPerPage)`

Appelé quand l'utilisateur clique sur un en-tête pour trier.

**Paramètres:**
- `sort` (object) - Nouveau tri: `{ key: 'email', direction: 'asc' }` ou `{ key: 'email', direction: 'desc' }`
- `search` (string) - Recherche actuelle
- `filters` (object) - Filtres actuels
- `page` (number) - Page (réinitialisée à 1)
- `itemsPerPage` (number) - Nombre d'items par page

**Retour:**
```javascript
{ data: [...], totalItems: 150 }
```

**Exemple:**
```javascript
onSort: async (sort, search, filters, page, itemsPerPage) => {
  console.log('Tri:', sort.key, sort.direction);
  
  const response = await fetch('/api/users/sort', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      sort: { key: sort.key, direction: sort.direction },
      search,
      filters,
      page,
      limit: itemsPerPage,
    }),
  });
  
  return await response.json();
}
```

---

### `onPageChange(page, itemsPerPage, search, filters, sort)`

Appelé quand l'utilisateur change de page.

**Paramètres:**
- `page` (number) - Nouvelle page
- `itemsPerPage` (number) - Nombre d'items par page
- `search` (string) - Recherche actuelle
- `filters` (object) - Filtres actuels
- `sort` (object|null) - Tri actuel

**Retour:**
```javascript
{ data: [...], totalItems: 150 }
```

**Exemple:**
```javascript
onPageChange: async (page, itemsPerPage, search, filters, sort) => {
  console.log('Page:', page);
  
  const response = await fetch('/api/users/page', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      page,
      limit: itemsPerPage,
      search,
      filters,
      sort,
    }),
  });
  
  return await response.json();
}
```

---

### `onItemsPerPageChange(itemsPerPage, page, search, filters, sort)`

Appelé quand l'utilisateur change le nombre d'items par page.

**Paramètres:**
- `itemsPerPage` (number) - Nouvelle limite par page
- `page` (number) - Page (réinitialisée à 1)
- `search` (string) - Recherche actuelle
- `filters` (object) - Filtres actuels
- `sort` (object|null) - Tri actuel

**Retour:**
```javascript
{ data: [...], totalItems: 150 }
```

**Exemple:**
```javascript
onItemsPerPageChange: async (itemsPerPage, page, search, filters, sort) => {
  console.log('Items par page:', itemsPerPage);
  
  const response = await fetch('/api/users/items-per-page', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      limit: itemsPerPage,
      page,
      search,
      filters,
      sort,
    }),
  });
  
  return await response.json();
}
```

---

## 🖥️ Endpoints Backend Requis

### POST /api/users/search

Effectue une recherche globale avec tous les paramètres actuels.

**Request:**
```json
{
  "query": "alice",
  "filters": { "role": "TEACHER", "status": "ACTIVE" },
  "sort": { "key": "email", "direction": "asc" },
  "page": 1,
  "limit": 50
}
```

**Response:**
```json
{
  "data": [
    { "id": 1, "email": "alice@example.com", "roleName": "TEACHER", ... },
    ...
  ],
  "totalItems": 150
}
```

---

### POST /api/users/filter

Applique des filtres avec tous les paramètres actuels.

**Request:**
```json
{
  "filters": { "role": "TEACHER" },
  "search": "alice",
  "sort": { "key": "email", "direction": "asc" },
  "page": 1,
  "limit": 50
}
```

**Response:**
```json
{
  "data": [...],
  "totalItems": 45
}
```

---

### POST /api/users/sort

Effectue un tri avec tous les paramètres actuels.

**Request:**
```json
{
  "sort": { "key": "email", "direction": "desc" },
  "search": "",
  "filters": {},
  "page": 1,
  "limit": 50
}
```

**Response:**
```json
{
  "data": [...],
  "totalItems": 150
}
```

---

### POST /api/users/page

Change la page avec les paramètres actuels.

**Request:**
```json
{
  "page": 2,
  "limit": 50,
  "search": "alice",
  "filters": { "role": "TEACHER" },
  "sort": { "key": "email", "direction": "asc" }
}
```

**Response:**
```json
{
  "data": [...],
  "totalItems": 45
}
```

---

### POST /api/users/items-per-page

Change le nombre d'items par page.

**Request:**
```json
{
  "limit": 100,
  "page": 1,
  "search": "",
  "filters": {},
  "sort": null
}
```

**Response:**
```json
{
  "data": [...],
  "totalItems": 150
}
```

---

## 🔧 Implémentation côté Serveur (exemples)

### JavaScript/Node.js (Express)

```javascript
// GET /api/users/search
app.post('/api/users/search', async (req, res) => {
  const { query, filters, sort, page, limit } = req.body;

  // Construire la requête
  let dbQuery = User.find();

  // Appliquer la recherche
  if (query) {
    dbQuery = dbQuery.where('email').regex(new RegExp(query, 'i'));
  }

  // Appliquer les filtres
  if (filters?.role && filters.role !== 'ALL') {
    dbQuery = dbQuery.where('roleName').equals(filters.role);
  }
  if (filters?.status && filters.status !== 'ALL') {
    dbQuery = dbQuery.where('status').equals(filters.status);
  }

  // Compter le total
  const totalItems = await dbQuery.clone().countDocuments();

  // Appliquer le tri
  if (sort) {
    const sortObj = {};
    sortObj[sort.key] = sort.direction === 'asc' ? 1 : -1;
    dbQuery = dbQuery.sort(sortObj);
  }

  // Appliquer la pagination
  const offset = (page - 1) * limit;
  const data = await dbQuery.skip(offset).limit(limit).exec();

  res.json({ data, totalItems });
});
```

### Python/Django

```python
from rest_framework.decorators import api_view
from rest_framework.response import Response
from django.db.models import Q

@api_view(['POST'])
def search_users(request):
    query = request.data.get('query', '')
    filters = request.data.get('filters', {})
    sort = request.data.get('sort')
    page = request.data.get('page', 1)
    limit = request.data.get('limit', 50)

    # Construire la requête
    queryset = User.objects.all()

    # Appliquer la recherche
    if query:
        queryset = queryset.filter(
            Q(email__icontains=query) | Q(name__icontains=query)
        )

    # Appliquer les filtres
    if filters.get('role') and filters['role'] != 'ALL':
        queryset = queryset.filter(roleName=filters['role'])
    if filters.get('status') and filters['status'] != 'ALL':
        queryset = queryset.filter(status=filters['status'])

    # Compter le total
    total_items = queryset.count()

    # Appliquer le tri
    if sort:
        field = sort['key']
        if sort['direction'] == 'desc':
            field = f'-{field}'
        queryset = queryset.order_by(field)

    # Appliquer la pagination
    offset = (page - 1) * limit
    data = list(queryset[offset:offset + limit].values())

    return Response({'data': data, 'totalItems': total_items})
```

### PHP/Laravel

```php
Route::post('/api/users/search', function (Request $request) {
    $query = $request->input('query', '');
    $filters = $request->input('filters', []);
    $sort = $request->input('sort');
    $page = $request->input('page', 1);
    $limit = $request->input('limit', 50);

    // Construire la requête
    $dbQuery = User::query();

    // Appliquer la recherche
    if ($query) {
        $dbQuery->where('email', 'like', "%$query%");
    }

    // Appliquer les filtres
    if (isset($filters['role']) && $filters['role'] !== 'ALL') {
        $dbQuery->where('roleName', $filters['role']);
    }
    if (isset($filters['status']) && $filters['status'] !== 'ALL') {
        $dbQuery->where('status', $filters['status']);
    }

    // Compter le total
    $totalItems = $dbQuery->count();

    // Appliquer le tri
    if ($sort) {
        $direction = $sort['direction'] === 'asc' ? 'asc' : 'desc';
        $dbQuery->orderBy($sort['key'], $direction);
    }

    // Appliquer la pagination
    $data = $dbQuery->skip(($page - 1) * $limit)
                    ->take($limit)
                    ->get();

    return response()->json(['data' => $data, 'totalItems' => $totalItems]);
});
```

---

## 🎯 Bonnes Pratiques

### 1. Gestion des erreurs

```javascript
onSearch: async (query, filters, sort, page, itemsPerPage) => {
  try {
    const response = await fetch('/api/users/search', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ query, filters, sort, page, limit: itemsPerPage }),
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Erreur lors de la recherche:', error);
    // Le DataTable affichera automatiquement un message d'erreur
    throw error;
  }
}
```

### 2. Logging/Debugging

```javascript
onSearch: async (query, filters, sort, page, itemsPerPage) => {
  console.log('Recherche:', { query, filters, sort, page, itemsPerPage });
  
  const response = await fetch('/api/users/search', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query, filters, sort, page, limit: itemsPerPage }),
  });
  
  const result = await response.json();
  console.log('Résultat:', result);
  
  return result;
}
```

### 3. Caching (optionnel)

```javascript
const cache = new Map();

onSearch: async (query, filters, sort, page, itemsPerPage) => {
  const cacheKey = JSON.stringify({ query, filters, sort, page, itemsPerPage });
  
  if (cache.has(cacheKey)) {
    console.log('Résultat du cache');
    return cache.get(cacheKey);
  }

  const response = await fetch('/api/users/search', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query, filters, sort, page, limit: itemsPerPage }),
  });

  const result = await response.json();
  cache.set(cacheKey, result);
  
  return result;
}
```

### 4. Timeouts

```javascript
async function fetchWithTimeout(url, options = {}, timeout = 5000) {
  const controller = new AbortController();
  const id = setTimeout(() => controller.abort(), timeout);

  try {
    const response = await fetch(url, {
      ...options,
      signal: controller.signal,
    });
    return response;
  } finally {
    clearTimeout(id);
  }
}

onSearch: async (query, filters, sort, page, itemsPerPage) => {
  const response = await fetchWithTimeout('/api/users/search', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query, filters, sort, page, limit: itemsPerPage }),
  }, 5000); // 5s timeout

  return await response.json();
}
```

---

## 💡 Conseils de Performance

1. **Utilisez les index DB** sur les colonnes filtrées/triées
2. **Paginez les résultats** - Ne chargez jamais 1 million d'enregistrements
3. **Limitez la profondeur des relations** - Jointure seulement si nécessaire
4. **Cachez les résultats** si les données changent rarement
5. **Optimisez les requêtes DB** avec un profiler
6. **Compressez les réponses** - Gzip pour les gros payloads

