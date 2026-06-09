const CACHE_NAME= 'v2_cache';
const ASSETS_TO_CACHE= [
    '/',
    '/css/bootstrap.min.css',
    '/js/common/application/TokenService.js',
    '/images/logo-kemoschool.svg'
]

self.addEventListener('install',(event)=>{
    event.waitUntil(
        caches.open(CACHE_NAME).then((cache)=>cache.addAll(ASSETS_TO_CACHE))
    )
});

self.addEventListener('fetch',(event)=>{
    event.respondWith(
        fetch(event.request).catch(()=>caches.match(event.request))
    )
});