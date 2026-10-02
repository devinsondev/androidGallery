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

Приложение намеренно не запрашивает разрешение INTERNET.

Перед декодированием проверяются MIME, размер файла, геометрия кадра и длительность.
Неподдерживаемые или аномально большие файлы не передаются декодеру. URI принимаются
только от системного MediaStore.

Изображения и GIF декодируются системным ImageDecoder сразу в размер экрана, превью —
через ContentResolver.loadThumbnail(). Видео проигрывается Media3/ExoPlayer через
локальный content:// URI.

Сборка также защищена Gradle Wrapper checksum и Gradle dependency verification metadata.

Это уменьшает поверхность атаки, но не гарантирует защиту от неизвестной уязвимости
в системном медиастеке. Android и Google Play system updates должны быть актуальны.

## Сборка на рабочем Windows ПК

Проект рассчитан на JDK 17, compileSdk 36, targetSdk 36 (Android 16), minSdk 29.

```powershell
cd D:\ORDERED_CODE\PHONE\APPS\androidGallery
git pull
.\build-debug.bat
```

`build-debug.bat` использует `D:\TOOLS\andrstdio\jbr`, Android SDK из
`%LOCALAPPDATA%\Android\Sdk`, проверяет наличие checksum у Gradle Wrapper,
запускает unit-тесты и собирает debug APK.

Ручной запуск через wrapper:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
```
