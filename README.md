# MVI-Sample

An Android sample app that places **MVI** and **MVVM** side by side on the same screen, so you can
compare both patterns on an identical feature.

---

## What it does

The app displays a simple item list with two tabs — one backed by an MVI ViewModel, the other by a
MVVM ViewModel. Both tabs expose the same UI (add an item by name, delete an item with a trash icon)
and share the same stateless `ItemsScreen` composable.

---

## Architecture overview

```
MainActivity
├── Tab 0 → ItemsMviRoute  ──► ItemsMviViewModel  (MVI)
└── Tab 1 → ItemsMvvmRoute ──► ItemsMvvmViewModel (MVVM)
                                        │
                              shared ItemsScreen composable
```

### MVI pattern (`feature/items/mvi`)

| Concept    | Class                                                                                                |
|------------|------------------------------------------------------------------------------------------------------|
| State      | `ItemsUiState` — immutable data class (`items`, `errorMessage`, `deletionSuccess`)                   |
| Action     | `ItemsAction` — sealed interface (`AddItem`, `DeleteItem`, `ConsumeError`, `ConsumeDeletionSuccess`) |
| ViewModel  | `ItemsMviViewModel` — extends `MviViewModel<State, Action>`                                          |
| Base class | `core/MviViewModel` — mutex-protected `updateState {}` reducer, single `handle()` entry point        |

Errors and deletion feedback are modelled as **one-shot state fields** consumed by `LaunchedEffect`
in the route composable via `ConsumeError` / `ConsumeDeletionSuccess` actions.

### MVVM pattern (`feature/items/mvvm`)

| Concept   | Class                                                                         |
|-----------|-------------------------------------------------------------------------------|
| State     | `StateFlow<ImmutableList<Item>>` exposed directly from the ViewModel          |
| ViewModel | `ItemsMvvmViewModel` — plain `ViewModel`, methods return `Result<T>` |

Add/delete results are returned as `Result<Item>` / `Result<String>` and handled imperatively by the
caller.

---

## Project structure

```
app/src/main/…
├── core/
│   └── MviViewModel.kt          # Generic MVI base ViewModel
├── factory/
│   └── ItemFactory.kt           # Creates Item instances with a UUID
├── model/
│   └── Item.kt                  # Data class: id + name
├── feature/items/
│   ├── ItemsScreen.kt           # Shared stateless Compose UI
│   ├── mvi/
│   │   ├── ItemsAction.kt
│   │   ├── ItemsUiState.kt
│   │   ├── ItemsMviViewModel.kt
│   │   └── ItemsMviScreen.kt    # Route: collects state, dispatches actions
│   └── mvvm/
│       ├── ItemsMvvmViewModel.kt
│       └── ItemsMvvmScreen.kt   # Route: calls VM methods, handles Result
└── ui/theme/
```

---

## Testing

Unit tests live in `app/src/test/` and follow the same structure as the source.

Both test classes inject the initial `State` / persistent item list directly into the ViewModel
constructor instead of calling methods to build up state, keeping each test focused on a
**single action**.

```kotlin
import kotlinx.collections.immutable.persistentListOf

// MVI — inject ItemsUiState
viewModel = ItemsMviViewModel(
    initialState = ItemsUiState(
        items = persistentListOf(Item(id = "id-1", name = "ToDelete"))
    )
)

// MVVM — inject initial items list
viewModel = ItemsMvvmViewModel(
    initialItems = persistentListOf(Item(id = "id-1", name = "ToDelete"))
)
```

---

## Tech stack

| Layer        | Library / Version                             |
|--------------|-----------------------------------------------|
| Language     | Kotlin 2.3.21                                 |
| UI           | Jetpack Compose BOM 2026.04.01 · Material 3   |
| Architecture | AndroidX ViewModel 2.10.0 · `StateFlow`       |
| Coroutines   | kotlinx.coroutines 1.10.2                     |
| Testing      | JUnit 4.13.2 · kotlinx-coroutines-test 1.10.2 |
| Build        | AGP 8.13.2 · Gradle version catalog           |
