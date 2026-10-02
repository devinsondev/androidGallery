# androidGallery

Локальная галерея для Android 16: фото, GIF и видео из Android MediaStore.

## Возможности

- фото, GIF и видео с устройства;
- группировка медиатеки по локальной дате;
- видео-плеер внутри приложения;
- полноэкранный просмотр изображений с масштабированием;
- полный и ограниченный доступ к медиатеке Android 14+;
- Material 3, Dynamic Color, edge-to-edge и нормальный Android Back.

## Безопасность

Приложение не запрашивает `INTERNET` и запрещает cleartext-трафик.

Перед декодированием проверяются MIME, размер файла, геометрия кадра и длительность.
Неподдерживаемые или аномально большие файлы не передаются декодеру. URI принимаются
только от системного MediaStore.

Изображения и GIF декодируются системным ImageDecoder сразу в размер экрана, превью —
через ContentResolver.loadThumbnail(). Видео проигрывается Media3/ExoPlayer через
локальный `content://` URI.

Сборка защищена Gradle Wrapper checksum, проверкой SHA-256 самого wrapper JAR и
Gradle dependency verification в strict-режиме.

## READY FOR USER PULL

Обычный workflow на рабочем Windows ПК:

```powershell
cd D:\ORDERED_CODE\PHONE\APPS\androidGallery
git pull --ff-only
.\build-debug.bat
```

`build-debug.bat` fail-closed проверяет wrapper, точные Gradle URL/SHA-256,
`gradle/verification-metadata.xml`, JDK и Android SDK. Затем запускает unit-тесты
и собирает debug APK со строгой проверкой зависимостей.

APK после успешной сборки:

```text
app\build\outputs\apk\debug\app-debug.apk
```

Ручной эквивалент Gradle-команды:

```powershell
.\gradlew.bat --dependency-verification strict :app:testDebugUnitTest :app:assembleDebug
```

Toolchain: JDK 17, compileSdk 36, targetSdk 36 (Android 16), minSdk 29.


## Оптимизированная локальная сборка для телефона

Для проверки производительности без debug-overhead используется отдельный `localRelease`:
он не debuggable, включает R8/minify и resource shrinking, но подписывается локальным
debug-ключом, чтобы APK можно было ставить через ADB без отдельного release keystore.

Собрать APK:

```powershell
.\build-local-release.bat
```

Собрать, установить на подключённый по ADB телефон и запустить:

```powershell
.\install-local-release.bat
```

APK:

```text
app\build\outputs\apk\localRelease\app-localRelease.apk
```
