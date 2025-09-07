# Offline Photo Uploader (Android – Kotlin, Compose, Room, Firebase)

A simple, **offline‑first** Android app that lets users select photos and upload them to **Firebase Storage**. Photos are placed in a **persistent Room queue** and upload automatically when connectivity is available. The upload logic ships as a **reusable module** (`uploadkit`).

## Architecture
- **Clean-ish MVVM** in `app` (Compose UI + ViewModel)
- **uploadkit** module encapsulates:
  - Room DB (`UploadDatabase` / `UploadDao` / `UploadEntity`)
  - Domain (`UploadStatus`, `UploadItem`, `UploadRepository`)
  - Use cases (`ObserveUploads`, `EnqueuePhotos`, `RetryFailed`)
  - Infra (`AndroidFileStore`, `FirebaseUploader`, `NetworkMonitor`, `QueueProcessor`)
- **Offline-first**: selected URIs are copied into app-private storage, queued with `QUEUED` status.
- **Automatic upload**: `QueueProcessor` listens to `NetworkMonitor.online` and pushes pending items.
- **Persistence**: queue survives restarts via Room.
- **Concurrency**: Kotlin Coroutines + Flow throughout.

## Build & Run
1. Open in **Android Studio** (Giraffe/Koala+ recommended).
2. Add your Firebase project (Storage enabled) and optional **google-services.json** if you want uploads to actually hit Firebase.
   - The project compiles without the google-services Gradle plugin; uploads will no-op unless Firebase connects.
3. Run on API 24+ device/emulator.
4. Tap **Select Photos** to add images; they appear in the grid with statuses. Go offline → items remain queued; come back online → uploads start.

> If you prefer the Gradle plugin path, add `id("com.google.gms.google-services")` to the root and app modules and place **app/google-services.json**. Then rebuild.

## Testing
- Unit test example: `uploadkit/src/test/.../EnqueuePhotosUseCaseTest.kt`
- UI test example: `app/src/androidTest/.../MainScreenTest.kt`

## Firebase Storage Rules (basic example)
```
// storage.rules
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /uploads/{file} {
      allow write: if request.auth != null; // tighten as needed
      allow read: if false;
    }
  }
}
```

## Accessibility
- Content descriptions on images are provided; extend as needed.

## Notes / Trade-offs
- No external DI; a tiny service-locator (`UploadKit`) keeps the sample small.
- Uploads are sequential; parallelism can be added in `QueueProcessor` if desired.
- Photo Picker uses **PickMultipleVisualMedia**, which needs no runtime permission. URIs are copied to internal storage for persistence.
- Error handling kept simple for brevity.

## Using AI
Boilerplate scaffolding and file generation were assisted by an AI code assistant. Made use of Github Copilot and Gemini Agent Preview mode. All design decisions and structure were curated for clarity and modularity. The AI tools were particularly useful in generating the UI test cases and Unit Test cases. 
