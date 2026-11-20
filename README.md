# TP Android V3 - Gestion des contacts

Ce projet représente l'implémentation complète de la version 3 de l'application de gestion des contacts, développée avec Android Studio, Kotlin, AndroidX Fragments, RecyclerView et Google Maps SDK, conformément aux exigences du TP.

## Architecture mise en place
* **1 seule Activity** : `MainActivity.kt` héberge désormais les fragments dans un `FragmentContainerView`.
* **3 Fragments** :
    * `ListeContactsFragment` : Remplace la précédente ListView par une RecyclerView et intègre une barre de recherche.
    * `DetailContactFragment` : Gère l'affichage, la modification et les actions externes d'un contact (appels, e-mail, galerie, caméra).
    * `CarteContactFragment` : Affiche les coordonnées du contact sur une carte interactive via le SDK Google Maps.
* **ViewModel Partagé** : Un `ContactViewModel` (via `activityViewModels`) permet de stocker la liste des contacts en mémoire et de partager les données facilement entre les fragments. Ainsi, chaque ajout ou modification d'un contact est instantanément répercuté dans la RecyclerView de la liste.
* **Navigation** : Utilisation du `FragmentManager` (via `parentFragmentManager.beginTransaction()`) pour remplacer les fragments et l'utilisation de `addToBackStack(null)` permet de gérer de manière native et fluide la navigation au clic sur le bouton "Retour" d'Android.

## Fonctionnalités implémentées
1. **RecyclerView et Liste des contacts**
   * Migration vers `RecyclerView` en réutilisant un adapter optimisé (`ContactAdapter.kt`).
   * Filtrage des contacts (Nom, Prénom, Téléphone) en temps réel, indifférent à la casse, et gestion de l'état "Aucun contact trouvé".

2. **Détail et Edition (DetailContactFragment)**
   * Conservation d'une copie du contact sélectionné : Les modifications ne sont appliquées au ViewModel qu'au moment du clic sur le bouton "Enregistrer". Le bouton "Annuler" ou "Retour" discard proprement les informations sans polluer la source de vérité.
   * Ajout de la validation des champs obligatoires (nom, prénom, téléphone).

3. **Intégration et API Externes**
   * Action **Appeler** et **Email** par *Intents* implicites avec gestion de l'indisponibilité de l'application cliente pour éviter les crashs.
   * Récupération de photos depuis la **Caméra** et la **Galerie** via la nouvelle `ActivityResultContracts` API.
   * Sécurisation par copie de cache pour la caméra et content resolver persist URI flag pour la galerie.

4. **Coordonnées GPS et Google Maps**
   * Ajout des champs `Latitude` et `Longitude` dans la classe Modèle `Contact`.
   * Validation strict des coordonnées (`-90` à `90` pour lat, `-180` à `180` pour lng).
   * Lancement d'un Marker centré sur Google Maps depuis le fragment `CarteContactFragment` si les coordonnées sont valides.

## Configuration requise (Google Maps)
La clé Google Maps API a été configurée de manière sécurisée sans être codée en dur, grâce au plugin **Secrets Gradle Plugin for Android** (`com.google.android.libraries.mapsplatform.secrets-gradle-plugin`).

**Étapes pour activer l'affichage des cartes sur votre environnement local :**
1. Générez une clé API Google Maps (via la Google Cloud Console en activant le "Maps SDK for Android").
2. Dans le répertoire racine du projet, ouvrez le fichier `local.properties`.
3. Ajoutez la ligne suivante :
   ```properties
   MAPS_API_KEY=VOTRE_CLE_API_GOOGLE_MAPS
   ```
4. Lancez un Build et exécutez le projet.

*Le fichier `local.properties` est déjà ignoré par Git (voir le `.gitignore`), ce qui empêche votre clé d'être exposée publiquement.*

## Fichiers créés ou modifiés :
* **Nouveaux** : `ListeContactsFragment.kt`, `DetailContactFragment.kt`, `CarteContactFragment.kt`, `ContactViewModel.kt`, `fragment_liste_contacts.xml`, `fragment_detail_contact.xml`, `fragment_carte_contact.xml`.
* **Modifiés** : `MainActivity.kt`, `activity_main.xml`, `Contact.kt`, `ContactAdapter.kt`, `AndroidManifest.xml`, `libs.versions.toml`, `build.gradle.kts` (app et project).
* **Supprimés** : `DetailActivity.kt`, `activity_detail.xml` (Remplacés par les versions Fragment).
