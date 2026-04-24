# Viewer (Android PDF Opener)

A lightweight Android app that opens PDF files sent via `ACTION_VIEW` intent and renders the first page as a preview.

This project is built with Kotlin and Android Gradle Plugin using SDK 36.

## Features

- Open PDF from file manager or any app that shares a PDF intent.
- Render first page with `PdfRenderer`.
- Show clear status messages for success and failure.
- Guide user to set this app as default PDF opener.

## Screenshots

> Images are loaded from the existing `assits` folder.

![Viewer Screenshot 1](assits/pdf_viewer_01.png)
![Viewer Screenshot 2](assits/pdf_viewer_02.png)
![Viewer Screenshot 3](assits/pdf_viewer_03.png)
![Viewer Screenshot 4](assits/pdf_viewer_04.png)
![Viewer Screenshot 5](assits/pdf_viewer_05.png)

## Project Structure

- `app/` - Android application module
- `assits/` - screenshots and media used in README
- `gradle/` - Gradle wrapper and versions catalog

## Requirements

- Android Studio (latest stable recommended)
- JDK 11
- Android SDK with API 36

## Run Locally

1. Clone repository:
   ```bash
   git clone <your-repo-url>
   cd PDF-viewer-and-search-Readoc-Search-
   ```
2. Open project in Android Studio.
3. Sync Gradle.
4. Run app on emulator or physical device.

## Build APK (Debug)

```bash
./gradlew assembleDebug
```

On Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

Generated APK path:

- `app/build/outputs/apk/debug/`

## Notes

- `local.properties` is ignored in git, so no local SDK path leaks.
- Build output folders are not tracked.
- Keep screenshots inside `assits/` so README links remain valid.

## License

Add your preferred license (for example MIT) before sharing publicly.
