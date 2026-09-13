# Platform Reset

A Jetpack Compose app demonstrating five real Android 17 (API 37) platform changes with actual working code, not just a write-up. Companion to ["Android 17 Is a Platform Reset"](https://medium.com/@promode7/android-17-is-a-platform-reset-here-are-the-5-changes-every-developer-needs-to-act-on-now-6e9238839750).

## What's in it

- **AppFunctions** — a notes app whose create/list/delete actions are exposed as callable tools for on-device AI agents via `androidx.appfunctions`.
- **Adaptive layouts** — an articles reader that switches between single-pane and two-pane list/detail depending on window size. The app's own home screen uses the same logic to adapt itself on tablets and foldables.
- **Handoff API** — resumes a reading session across devices using the real `Activity.setHandoffEnabled` / `onHandoffActivityDataRequested` platform hooks.
- **On-device inference** — image classification via LiteRT, with NPU → GPU → CPU fallback.
- **Security hardening** — SMS Retriever, the new `ACCESS_LOCAL_NETWORK` permission, Certificate Transparency, and correct handling of Bluetooth's `-1` disconnect sentinel.

## Design

Dark, warm palette, IBM Plex Sans and Mono, flat hairline-separated rows instead of cards. The header caption is a small easter egg — Android 17's actual internal codename is `CINNAMON_BUN`.

## Structure

`app` — home dashboard and navigation.
`core-design` — shared theme, typography, and list/detail components.
`feature-appfunctions`, `feature-adaptive`, `feature-handoff`, `feature-npu`, `feature-security` — one module per feature above.

## Build & run

Point `JAVA_HOME` at Android Studio's bundled JDK and `ANDROID_HOME` at your SDK, then:

```
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

compileSdk/targetSdk 37, minSdk 26.

To try the AppFunctions integration directly:

```
adb shell cmd app_function execute-app-function \
  --package com.pramodpatel.platformreset \
  --function 'com.pramodpatel.notesfunctions.appfunctions.BaseNotesAppFunctionService#createNote' \
  --parameters '{"createNoteParams": {"title": ["Buy milk"], "content": ["From cmd app_function"]}}'
```

## License

MIT — see `LICENSE`. Fonts (IBM Plex) are SIL OFL 1.1, see `licenses/OFL-IBM-Plex.txt`. The bundled classification model and sample image in `feature-npu` keep their original licenses.
