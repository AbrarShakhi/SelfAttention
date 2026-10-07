# Self Attention

An Android app for tracking class attendance. Add your courses once, get reminded before each
class and prompted afterwards to record whether you went, and see at a glance how close you are to
the attendance you need.

Built entirely with Jetpack Compose and Material 3.

---

## Download

**[Download the latest APK](https://github.com/AbrarShakhi/SelfAttention/releases/latest/download/app-release.apk)**
 · [All releases](https://github.com/AbrarShakhi/SelfAttention/releases)

Requires **Android 11 (API 30)** or newer.

The app is distributed as an APK rather than through an app store, so Android will ask you to
allow installs from this source the first time. On most devices that prompt appears during
installation; otherwise it is under *Settings → Apps → Special app access → Install unknown apps*.

---

## Contents

- [Download](#download)
- [Features](#features)
- [Requirements](#requirements)
- [Building and running](#building-and-running)
- [Testing](#testing)
- [Architecture](#architecture)
- [Backup format](#backup-format)
- [Permissions](#permissions)
- [Known limitations](#known-limitations)
- [License](#license)
- [Credits](#credits)

---

## Features

**Courses**
- Create a course with its name, code, class days, and start time using a plain-language form —
  *"I have [Databases] class with code [CS-201] on every [Mon, Wed] at [09:30]."*
- Edit or delete a course at any time; editing re-arms its reminders automatically.

**Attendance**
- Mark each class Present, Absent, or Holiday, from a notification or from the app.
- Per-course attendance percentage, with an at-risk threshold at 75%.
- Month calendar showing every class day and what was recorded.

**Reminders**
- An optional reminder a configurable number of minutes before class.
- A prompt after class asking whether you attended, so nothing goes unrecorded.
- Reminders survive a reboot, an app update, and time or timezone changes.

**Timeline**
- A calendar that expands from a single week to a full month, and collapses again as you scroll
  the day's classes.

**Home-screen widgets**
- *Next class* — the upcoming course and when it starts.
- *Today* — today's classes with Present / Absent / Holiday buttons right on the widget.
- *Attendance* — overall ring plus every course's percentage, at-risk courses highlighted.

**Personalisation**
- Material 3 Expressive design with shape-morphing components and motion.
- Colour scheme generated from a seed colour you pick (eight seeds, six palette styles), or from
  your wallpaper on Android 12+; optional pure-black dark theme.
- Light, dark, or system theme.
- Eight typefaces, including a system option that needs no download.
- Configurable first day of the week and weekly holidays; holiday weekdays never count as class
  days.

**Backup**
- Export every course and attendance record to a JSON file.
- Import a backup additively — it never overwrites what you already have.
- Guided first-run setup, which can be skipped.

---

## Requirements

| | |
|---|---|
| JDK | 25 (the Gradle daemon toolchain, provided by `mise.toml`; bytecode targets 11) |
| Android SDK | API 37.1 (`compileSdk`), build-tools 37.0.0 |
| Minimum device | Android 11 (API 30) |
| Target | API 37 |

---

## Building and running

```bash
git clone https://github.com/AbrarShakhi/SelfAttention.git
cd SelfAttention
./gradlew assembleDebug
```

> **A JDK 25 must be on the path.** The wrapper otherwise fails with
> `JAVA_HOME is not set and no 'java' command could be found in your PATH`. This matches
> `gradle/gradle-daemon-jvm.properties` (`toolchainVersion=25`). The repository's `mise.toml`
> provides it — run `mise install` once, then build from a mise-activated shell or with
> `mise exec -- ./gradlew …`. In Android Studio, set the Gradle JDK to any JDK 25.

### Common tasks

| Command | Purpose |
|---|---|
| `./gradlew assembleDebug` | Build the debug APK |
| `./gradlew installDebug` | Build and install on a connected device |
| `./gradlew testDebugUnitTest` | Run the JVM unit tests |
| `./gradlew lintDebug` | Run Android Lint |
| `./gradlew assembleRelease` | Build the release APK (R8 + resource shrinking) |

---

## Testing

```bash
./gradlew testDebugUnitTest
```

53 JVM unit tests cover the parts where a mistake is expensive and invisible:

| Area | What is verified |
|---|---|
| `JsonBackupCodecTest` | Every malformed backup shape produces a readable message, never an exception |
| `UpdateCourseUseCaseTest` | Editing a course cancels alarms against the **stored** schedule before re-arming |
| `AppRouteBackStackSaverTest` | Navigation survives rotation and process death, arguments included |
| `CourseScheduleTest` | A weekly holiday overrides a course's schedule |
| `CalendarMathTest` | Calendar maths honours the configured first day of the week |
| `CourseStatsTest` | "Classes you can miss" and "classes needed" against the 75% target |
| `CourseFormStateTest` | The add/edit form validates, trims, and keeps a course's identity when editing |
| `GetScheduleForDateUseCaseTest` | A day's classes come back in start order, and weekly holidays have none |
| `GetCourseStatsUseCaseTest`, `GetNextClassUseCaseTest`, `MarkAttendanceUseCaseTest`, `HomeViewModelTest` | Core attendance logic |

Run a single class or method:

```bash
./gradlew testDebugUnitTest --tests "*UpdateCourseUseCaseTest"
./gradlew testDebugUnitTest --tests "*CourseScheduleTest.a weekly holiday overrides the schedule*"
```

Test names are backtick sentences, so quote the whole pattern and wrap the method name in `*`.

---

## Architecture

A single module organised by package, following Google's app-architecture guide — UI layer, an
optional domain layer of use cases, and a data layer.

```
app/src/main/java/com/abrarshakhi/selfattention/
├── feature/         One package per feature: screen, ViewModel and UI state.
├── navigation/      Routes, the Navigation 3 graph and top-level destinations.
├── ui/              The app shell: outer Scaffold and navigation bar.
└── core/
    ├── model/         Plain Kotlin models.
    ├── domain/        Use cases.
    ├── data/          Repositories, backup codec and file store.
    ├── database/      Room database, DAOs and entities.
    ├── datastore/     Preferences DataStore.
    ├── alarm/         Exact-alarm scheduling and its receivers.
    ├── notification/  Notification channels and actions.
    ├── designsystem/  Theme and generic components.
    └── ui/            Shared app-specific UI.
```

**Stack**

| | |
|---|---|
| UI | Jetpack Compose, Material 3 Expressive (`material3` 1.5.0-alpha29), MaterialKolor, Lottie |
| Widgets | Jetpack Glance 1.2 |
| Navigation | Navigation 3 (`androidx.navigation3` 1.1.7) |
| DI | Hilt 2.60.1 |
| Database | Room 2.8.5 |
| Preferences | DataStore 1.2.1 |
| Serialisation | kotlinx.serialization |
| Build | AGP 9.4.0, Kotlin 2.4.20, KSP |

A few conventions worth knowing before contributing:

- **Navigation 3, not `NavController`.** The back stack is a `SnapshotStateList<AppRoute>` mutated
  only through the extensions in `navigation/BackStackController.kt`.
- **Two-layer `Scaffold`.** The outer `Scaffold` in `AppRoot` owns only the bottom navigation bar
  and consumes its padding; every screen owns an inner `Scaffold` with its own top app bar, FAB,
  and scroll behaviour.
- **No `java.time` type crosses the Room boundary.** Clock times are `Int` columns, dates are
  `epochDay` longs, and enums persist as strings.


---

## Backup format

Export produces a single JSON file. `version` allows the format to change without breaking older
files; unknown fields are ignored on import, and every field has a default, so a hand-written file
with only the essentials will load.

```json
{
  "version": 1,
  "exportedAt": "2026-09-18",
  "courses": [
    {
      "id": 1,
      "name": "Databases",
      "code": "CS-201",
      "scheduleDays": [1, 3, 5],
      "classHour": 9,
      "classMinute": 30,
      "classDurationMinutes": 60,
      "hasReminder": true,
      "reminderMinutesBefore": 15,
      "createdOn": "2026-09-01"
    }
  ],
  "attendance": [
    { "courseId": 1, "date": "2026-09-16", "status": "PRESENT" }
  ]
}
```

| Field | Notes |
|---|---|
| `scheduleDays` | ISO weekday numbers, Monday = 1 … Sunday = 7 |
| `classHour` / `classMinute` | 24-hour clock |
| `date` | ISO `yyyy-MM-dd` |
| `createdOn` | ISO date the course was added. Older backups without it start at the course's first recorded class. |
| `status` | `PRESENT`, `ABSENT`, or `HOLIDAY` |

**Import is additive.** Courses are inserted alongside whatever already exists and are given fresh
ids, with attendance remapped onto them. Importing the same file twice therefore duplicates it —
a deliberate trade, chosen over any behaviour that could silently erase existing data.

Invalid files are rejected with a specific, readable message (`"Course 2 has no name."`,
`"…has an invalid class day (9). Days run 1 (Monday) to 7 (Sunday)."`) rather than a crash.
Individual unreadable attendance rows are skipped so the courses still import.

---

## Permissions

| Permission | Why |
|---|---|
| `POST_NOTIFICATIONS` | Class reminders and the after-class prompt. Requested at runtime. |
| `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` | Reminders must fire at the class time, not whenever the system next wakes. |
| `RECEIVE_BOOT_COMPLETED` | Re-arms reminders after a restart. |
| `VIBRATE` | Notification vibration. |

If notifications are denied, the app says so in Settings and offers a way to fix it rather than
failing silently.

---

## Known limitations

Documented here rather than left to be discovered later.

- **Database migrations have not been exercised on a device.** The v1 → v2 rename was validated
  against SQLite directly, but Room's own schema check only runs on a real upgrade.

---

## License

Licensed under the Apache License, Version 2.0. See [`LICENSE`](LICENSE) for the full text.

---

## Credits

- <a href="https://www.flaticon.com/free-icons/awareness" title="awareness icons">Awareness icons created by Flat Icons - Flaticon</a>
