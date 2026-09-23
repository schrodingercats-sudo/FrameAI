# FrameAI - AI Director & Smart Camera for Solo Creators

FrameAI is an Android camera app built with Kotlin and Jetpack Compose featuring an iOS-inspired **Liquid Glass** aesthetic. It solves the solo creator framing challenge when recording with the rear camera by providing live computer vision guidance, automated face and eye tracking, directional voice feedback, and smart camera adjustments.

## Features

- **AI Director Mode:** Real-time on-device ML Kit face, eye, and headroom tracking with perspective-aware voice instructions (`"Move to your right"`, `"Step closer"`, `"Raise your chin"`, `"PERFECT FRAME"`).
- **Multiple Voice Director Personas:** Hollywood Director, Friendly Coach, Studio Pro, and Audio Cues / Chimes.
- **Smart Auto-Framing & Zoom:** Smooth digital zoom compensation to keep you centered.
- **Dismissible Bottom Sheet & Navigation:** Cross (`✕`) buttons on all overlay panels and toggleable bottom navigation tabs.
- **Audio Metering & Teleprompter:** Live microphone capsule with level indicator and floating scrollable script.

## Building the APK

To build the APK locally or via GitHub Actions:
```bash
./gradlew assembleDebug
```
The resulting ready-to-install APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`
