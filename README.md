Setup
--------
The TMDB key is intentionally excluded from version control.
Add it to local.properties:
TMDB_API_KEY=your_api_key

Overview
--------
MovieRank is an Android app for browsing trending movies using the TMDB API. 
The UI is built with Jetpack Compose and follows a modular architecture designed to 
keep application composition, networking, shared UI, and feature logic separated.

Modules
-------
:app
The application module is the composition root. 
It owns application startup and creates the dependency graph, but does not contain any logic.

:core:network
Contains shared networking infrastructure: Retrofit, OkHttp, TMDB authentication, 
guest session management, encrypted session persistence and the NetworkComponent. 
The module exposes only the dependencies required by features through NetworkDependencies.

:core:design
Contains the shared design system: for the assessment scope it is only theme and colors.

:feature:trending
Contains the movie feature: TMDB movie API, repository, use cases, ViewModels, 
movie list/detail screens, filtering and sorting.

Dependency direction is one-way:
app → feature/core
feature → core
core modules do not depend on app or feature modules.

Dependency Injection
--------------------
Dagger was chosen because it scales well with a modular architecture: 
dependencies and module boundaries are explicit, while the dependency graph is validated at compile time. 
Having separate components makes dependency lifetimes straightforward, without the need to manually open and close scopes. 

Data Models
-----------
The project intentionally does not introduce separate DTO and domain models for every API entity. 
For the assessment scope, the TMDB response is close to the data required by the feature, 
so an additional Dto → data class mapping layer would add boilerplate without solving any problem.

Gson also allows the model to contain only the fields needed by the app; unknown fields returned by TMDB are ignored. 
Feature-specific information such as resolved genre names is added after the API response is loaded.
This is a deliberate trade-off. 
In a larger application, I would consider separate DTO/domain models if API and domain models diverged, 
multiple data sources were introduced or API changes needed to be isolated from the domain layer.

Libraries
---------
• Kotlin + Coroutines 
• Jetpack Compose + Material 3
• Retrofit + OkHttp + Gson
• Dagger
• Coil — movie poster loading
• AndroidX Security Crypto — encrypted guest-session persistence
• Crashlytics - logs

Other Notes
-----------
Movie lists use LazyColumn because the list can contain many items while keeping composition lazy.
Sorting and genre filtering are performed locally on the loaded movie data.

R8 is enabled for release builds to perform code shrinking, optimization, and obfuscation. 
Rules are configured in proguard-rules file in app module.