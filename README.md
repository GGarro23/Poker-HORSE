# Poker HORSE

A mobile poker game developed in Kotlin using Android
and Jetpack Compose, with a retro pixel art aesthetic.

## Game modes

- H — Texas Hold'em
- O — Omaha Hi-Lo
- R — Razz
- S — Seven Card Stud
- E — Stud Eight-or-Better

## Architecture

The project consists of two independent responsibilities:

- `core`: Pure Kotlin/JVM domain and game engine.
- `app`: Android application built with Jetpack Compose.

The Android application depends on core, never the reverse.

## Technology stack

- Kotlin 2.4.21
- Gradle 9.6.0
- Android Gradle Plugin 9.5.2
- Jetpack Compose
- JUnit 4

## Requirements

- Android Studio
- JDK 21 for the core toolchain
- JDK 25 for the Gradle daemon
- Android SDK API 37
- Android 8.0 or higher for the device

## Build

```bash
./gradlew :core:test :app:assembleDebug
```

## Install

Connect an Android device with USB debugging enabled.

```bash
adb devices
./gradlew :app:installDebug
```

## Development status

Infrastructure established.
Domain model and game engine pending implementation.

## Documentation

See the `docs/` directory for architecture,
technical decisions, development status and test records.

## License

Pending agreement between the project authors.
