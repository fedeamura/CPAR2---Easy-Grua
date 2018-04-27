# CPAR2 EasyGrua
Codigo fuente del proyecto para el desafio organizado por el Grupo San Cristobal para el CPAR2.
Implementado en Android Nativo.


Integrantes
 - Amura, Federico
 - Garay, Lucio Omar
 - Perona, Camilo
 - Soruco, Ezequiel
 
Video DEMO
https://www.youtube.com/watch?v=gt4pxJBr9L4

Aplicacion DEMO
https://drive.google.com/open?id=1pCyqT0-7R-bDT0vTsKfzcUnlsJolIjvr

Version mínima de android
 - Android 4.1 JellyBean (Api 16)


Permisos de usuario necesarios
 - Acceso a internet y consultar estado de red del dispositivo (Para comunicarse con la base de datos y cargar el mapa)
 - Ubicacion GPS exacta del dispositivo (Para ubicar al usuario y a las gruas)


Caractericticas implementadas
 - Posicionamiento a traves de GPS
 - Ver gruas cercanas y como se desplazan por el mapa
 - Solicitar grua y automaticamente se selecciona la mas cercana
 - Se puede elegir entre distintas gruas, no solo la mas cercana
 - El usuario puede cancelar en cualquier momento
 - La grua puede marcar como completado en el momento que llega al destino
 - Se ve la ruta recomendada a seguir por la grua
 - Ubicacion en vivo de la grua seleccionada
 - La ruta se recalcula si la grua decide tomar otro camino
 - Se calcula tiempo y duración aproximada de arribo
 - Chat integrado entre grua y usuario
 - Logueo como usuario o grua
 - Base de datos en tiempo real a traves de Firebase
 

Lenguajes de programacion utilizados
 - Java
 - XML
  

Librerias externas utilizadas
 - Google AppComat (com.android.support:appcompat)
 - Google Play Services Maps (com.google.android.gms:play-services-maps)
 - Picasso (com.squareup.picasso:picasso)
 - Cardview (com.android.support:cardview
 - RecyclerView (com.android.support:recyclerview)
 - Firebase Core (com.google.firebase:firebase-core)
 - Firebase Firestore (com.google.firebase:firebase-firestore)

