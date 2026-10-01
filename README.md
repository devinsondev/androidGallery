# androidGallery

Локальная галерея для Android 16: фото, GIF и видео из Android MediaStore.

## Возможности

- фото, GIF и видео с устройства;
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

Это уменьшает поверхность атаки, но не гарантирует защиту от неизвестной уязвимости
в системном медиастеке. Android и Google Play system updates должны быть актуальны.

## Сборка

JDK 17, compileSdk 37, targetSdk 36 (Android 16), minSdk 29.

```bash
gradle :app:testDebugUnitTest
gradle :app:assembleDebug
```
