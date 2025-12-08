# Edna ChatCenter Demo (Android app)

Демо-приложение Android, показывающее, как интегрировать модули `chatcentercore` и `chatcenterui` (Edna ChatCenter SDK) в настоящее приложение.  
Приложение позволяет:

- выбрать сервер и пользователя;
- настроить базовые параметры и тему;
- открыть полнофункциональный чат в модальном экране;
- запускать UI- и screenshot-тесты для проверки внешнего вида чата.

---

## Подключение сабмодулей `chatcentercore` и `chatcenterui` к модулю `app`

### 1. Локальное подключение

Убедитесь, что оба SDK-модуля объявлены в `settings.gradle` корневого проекта.

Если все три модуля (`app`, `chatcentercore`, `chatcenterui`) лежат в одном репозитории рядом:

```groovy
include ':app', ':chatcenterui', ':chatcentercore'
project(':chatcenterui').projectDir = new File('chatcenterui/chatcenterui')
```

В app/build.gradle должны быть указаны:

```groovy
dependencies {
    // ядро SDK
    implementation(project(":chatcentercore"))

    // UI-слой SDK
    implementation(project(":chatcenterui"))

    // ваши остальные зависимости…
}
```
После этого app сможет использовать классы из обоих модулей, в первую очередь:

ChatCenterCore / ChatConfigCore (ядро),

ChatCenterUI / ChatConfig / ChatFragment / ChatActivity (UI).

В демо-проекте инициализация уже реализована в EdnaChatCenterApplication
(класс Application, указанный в AndroidManifest.xml), но для своего приложения
минимальная схема выглядит так:

```kotlin
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Конфиг ядра
        val chatConf = ChatConfig(
            transportConfig = ChatTransportConfig(
                // базовые URL, настройки SSL, таймауты и т.п.
            ),
            networkConfig = ChatNetworkConfig(
                // опции сети
            ),
            // остальные поля конфигурации…
        )
        
        // 2. Конфиг логгера
        val loggerConfig = ChatLoggerConfig(
            applicationContext,
            // ... и т.д.
        )

        // 3. Инициализация sdk
        ChatCenterUI(applicationContext, loggerConfig).apply {
            theme = chatLightTheme
            darkTheme = chatDarkTheme
            init(server.threadsGateProviderUid ?: "", server.appMarker ?: "", chatConf)
        }
    }
}
```

И не забудьте указать этот класс в AndroidManifest.xml:

```xml
<application
    android:name=".MyApplication"
    ... >
    ...
</application>
```

В демо-проекте роль MyApplication уже выполняет EdnaChatCenterApplication,
поэтому для запуска примера ничего дополнительно делать не нужно.

### 2. Подключение и работа с сабмодулями через git:

Клонируйте репозиторий (при использовании git-submodules не забудьте инициализировать их):

```shell
git clone <url>
cd <папка_проекта>
# если есть сабмодули:
git submodule update --init --recursive
```

1. Откройте корень проекта в Android Studio
(ту директорию, где лежат build.gradle верхнего уровня, settings.gradle, папка app/ и папки SDK).

2. Дождитесь завершения Gradle Sync.
    - Убедитесь, что:
        - установлена JDK 17;
        - скачаны нужные Android SDK (API уровня, на который настроен проект).
        - В панели Build Variants выберите для модуля app вариант debug.

    Выберите устройство (эмулятор или реальный телефон) и нажмите Run ▶ (Shift+F10).

3. После запуска приложения:
    - выберите сервер и пользователя на стартовом экране;
    - нажмите кнопку запуска чата — откроется модальное окно с ChatFragment.

## Структура модуля

Основные элементы в корне модуля:

- `build.gradle` — Gradle-скрипт модуля (плагины, зависимости, настройки Android, кастомные таски).
- `proguard-rules.pro` — правила для shrinker/обфускатора в release-сборке.
- `google-services.json` — конфигурация Firebase (Analytics, Crashlytics, Messaging, Performance).
- `agconnect-services.json` — конфигурация Huawei AGConnect / HMS Push.
- `debug.keystore` — дебажный ключ для подписи сборок debug (используется в `signingConfigs.debug`).
- `mainApp/` — вспомогательные артефакты (например, релизные сборки/доп. ресурсы, если используются).
- `src/` — исходники и ресурсы.

Внутри `src/`:

- `src/main/java/edna/chatcenter/demo/appCode/...`  
  Код демо-приложения:
    - `activity/` — `MainActivity`, `ModalChatActivity`, `SplashScreenActivity` и т.д.;
    - `fragments/` — стартовый экран, список серверов/пользователей, настройки, лог и т.п.;
    - `business/` — провайдеры (`PreferencesProvider`, `ServersProvider`, `StringsProvider`, `UiThemeProvider` и пр.), утилиты.
- `src/main/java/edna/chatcenter/demo/integrationCode/...`  
  Обвязка для подключения ChatCenter:
    - `EdnaChatCenterApplication` — инициализация SDK, транспорта, тем, подписка на пуш-токены;
    - `fragments/chatFragment/ChatAppFragment` — контейнер вокруг `ChatFragment` из `chatcenterui`;
    - `fragments/launch/LaunchFragment`, `LaunchViewModel` — вспомогательный код инициализации Threads-библиотеки.
- `src/androidTest/java/edna/chatcenter/demo/...`  
  UI- и screenshot-тесты:
    - базовые классы тестов, Kaspresso-screen’ы;
    - `screenshot/*ScreenshotTest.kt` — набор скриншот-тестов (Dropshots).
- `src/androidTest/assets/screenshots/`  
  Эталонные скриншоты для Dropshots.
- `src/main/assets/servers_config.json`  
  Конфигурация серверов (адреса API, WebSocket и т.п.), которые отображаются в UI.
- `src/main/res/`  
  Разметка, цвета, стили, drawable-ресурсы, темы и прочее.
- `src/main/AndroidManifest.xml`  
  Описание `Application`, активити, сервисов (FCM, HMS), разрешений и сетевой конфигурации.

---

## Как работает модуль

### Жизненный цикл и навигация

1. **Точка входа** — `SplashScreenActivity`  
   Простой splash: сразу запускает `MainActivity` и завершает себя.

2. **`MainActivity`**  
   Содержит `NavHostFragment` (Navigation Component) и управляет навигацией между фрагментами:
    - стартовый экран (`StartChatFragment`);
    - списки демо-примеров, серверов и пользователей;
    - настройки;
    - экран логов.

3. **Стартовый экран — `StartChatFragment`**  
   Показывает текущие настройки:
    - выбранный сервер (через `ServersProvider`);
    - выбранного пользователя и версию API (через `PreferencesProvider`);
    - количество непрочитанных сообщений (слушатель из `EdnaChatCenterApplication`).

   По кнопке «Go to chat» открывается модальное окно чата (`ModalChatActivity`).

4. **Модальный чат — `ModalChatActivity`**
    - Разворачивает layout `activity_modal_chat`.
    - Получает ссылку на `ChatCenterUI` из `EdnaChatCenterApplication`.
    - Вставляет в контейнер `ChatFragment`:
        - либо созданный SDK (`chatCenterUI.getChatFragment()`),
        - либо новый фрагмент через `ChatFragment.newInstance(...)`.
    - Пробрасывает событие `onBackPressed()` во фрагмент (чтобы корректно закрывать вложенные экраны чата).

5. **`EdnaChatCenterApplication` (Application)**  
   Указан в манифесте как `android:name`. Отвечает за:

    - **Инициализацию транспорта** (`ChatTransportConfig`, `ChatNetworkConfig`, `HTTPConfig`, `SSLPinningConfig`):
        - задание базовых URL для HTTP и WebSocket;
        - настройку SSL-пиннинга, при необходимости — флагов доверия к сертификатам;
        - поддержку mock-сервера (`ednaMockUrl`, `ednaMockThreadsGateUrl` и т.п.).
    - **Инициализацию UI-библиотеки** (`chatcenterui`):
        - настройка тем и цветов (светлая/тёмная, кастомные палитры чата);
        - конфигурация поведения (режимы открытия, стартовый экран, иконки, статусы сообщений и т.п.);
        - передача слушателя `ChatCenterUIListener` для получения количества непрочитанных сообщений и др.
    - **Интеграцию с внешними сервисами**:
        - Firebase Messaging — получение/обновление FCM-токена;
        - Huawei HMS Push — поддержка Huawei-устройств;
        - Microsoft AppCenter (Analytics, Crashes) — отправка логов и отчётов о падениях.
    - **Подготовку настроек демо**:
        - очистка/инициализация демо-режима (`resetDemoMode`);
        - работа с темами/ресурсами.

6. **Бизнес-логика и настройки**

    - `PreferencesProvider`:
        - хранит выбранного пользователя (`UserInfo`) и сервер (`ServerConfig`) в SharedPreferences (через Gson),
        - сохраняет/читаем выбранную версию API (`ChatApiVersion`),
        - хранит вспомогательные флаги демо-режима.
    - `ServersProvider`:
        - читает JSON из `assets/servers_config.json`,
        - возвращает список серверов и выбранный сервер, с fallback’ом на первый в списке.
    - `UiThemeProvider`:
        - отвечает за переключение светлой/тёмной темы и хранит текущее состояние.
    - `KoinModules.kt`:
        - модуль `appModule` регистрирует все провайдеры и ViewModel’и (`ServerListViewModel`, `UserListViewModel`, `DemoSamplesViewModel`, `LogViewModel` и др.).

---

## Основные зависимости и их назначение

### Внутренние модули

- `implementation project(path: ':chatcentercore')`  
  Ядро ChatCenter:
    - модели, конфигурации транспорта;
    - взаимодействие с Threads API;
    - обработка push-уведомлений и статусов сообщений.

- `implementation project(path: ':chatcenterui')`  
  UI-библиотека:
    - `ChatFragment` и вся логика отображения;
    - разные типы сообщений (текст, файлы, картинки, голосовые, системные и т.д.);
    - быстрые ответы, рейтинги, кнопки, состояние «печатает» и пр.

### Android и UI

- `androidx.core:core-ktx` — Kotlin-расширения для базовых Android API.
- `androidx.appcompat:appcompat` — обратная совместимость и базовые UI-компоненты.
- `com.google.android.material:material` — Material-компоненты (кнопки, диалоги, bottom sheet, табы и др.).
- `androidx.constraintlayout:constraintlayout` — основная библиотека для верстки экранов демо-приложения.
- `androidx.navigation:navigation-fragment-ktx`  
  `androidx.navigation:navigation-ui-ktx` — навигация между фрагментами (Graph + утилиты для ActionBar/BottomNavigation).

### DI, сеть, модели

- `io.insert-koin:koin-android` — DI-контейнер:
    - регистрация `PreferencesProvider`, `ServersProvider`, `StringsProvider`, `UiThemeProvider`;
    - создание ViewModel’ей.
- `com.squareup.retrofit2:converter-gson` — JSON-конвертер Retrofit (используется для вспомогательных HTTP-запросов при необходимости).
- `org.parceler:parceler-api` — удобная сериализация объектов в Parcelable.
- `androidx.lifecycle:lifecycle-viewmodel-ktx`  
  `androidx.lifecycle:lifecycle-livedata-ktx` — базовые классы и расширения жизненного цикла и реактивного UI.
- `org.jetbrains.kotlin:kotlin-reflect` — рефлексия Kotlin, используется, в частности, DI и для некоторых динамических операций.

### Аналитика, краши и перфоманс

- `com.google.firebase:firebase-analytics` — сбор событий аналитики.
- `com.google.firebase:firebase-crashlytics` — сбор и отправка крашей.
- `com.google.firebase:firebase-perf` — мониторинг производительности.
- `com.microsoft.appcenter:appcenter-analytics`  
  `com.microsoft.appcenter:appcenter-crashes` — дополнительный канал аналитики и отчётов о сбоях (AppCenter).

### Push-уведомления

- `com.google.firebase:firebase-messaging-ktx` — Firebase Cloud Messaging, обработка push-уведомлений для устройств с Google Play Services.
- `com.huawei.hms:push` — HMS Push для Huawei-устройств.

В манифесте объявлены сервисы и ресиверы, которые пробрасывают события push в SDK и демо-приложение.

### Инструментальные зависимости

- `coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:2.0.4'` — backport Java-API для старых версий Android (включена через `compileOptions.coreLibraryDesugaringEnabled = true`).
- Плагины:
    - `com.google.gms.google-services` — инициализация Firebase по `google-services.json`;
    - `com.huawei.agconnect` — интеграция с AGConnect;
    - `com.google.firebase.firebase-perf`, `com.google.firebase.crashlytics` — подключение перфоманс- и crash-SDK;
    - `com.dropbox.dropshots` — плагин для screenshot-тестов.

### Тестовые зависимости

- Юнит-тесты:
    - `junit:junit:4.13.2`.
- Инструментальные тесты:
    - `androidx.test:runner`, `rules`, `orchestrator` — инфраструктура Android-тестов;
    - `androidx.test.ext:junit-ktx` — обёртка JUnit для Android.
- UI-тесты:
    - `androidx.test.espresso:espresso-intents`, `espresso-contrib` — матчинги, взаимодействия и проверки UI;
    - `com.kaspersky.android-components:kaspresso` — надстройка над Espresso с удобным DSL и стабильными ожиданиями.
- Моки/сети:
    - `org.mockito:mockito-core`, `mockito-android`, `org.mockito.kotlin:mockito-kotlin` — мок-объекты;
    - `com.squareup.okhttp3:mockwebserver` — лёгкий тестовый HTTP/WebSocket-сервер;
    - `com.github.tomakehurst:wiremock-standalone` — более мощный HTTP-mock.
- Screenshot-тесты:
    - `com.dropbox.dropshots:Dropshots` — делает и сравнивает снимки экрана с эталоном.
- Диагностика:
    - `com.squareup.leakcanary:leakcanary-android` (только debug) — поиск утечек памяти.

---

## Сборка и запуск локально

Основные команды Gradle (запускаются из корня репозитория):

```bash
# Сборка debug-сборки
./gradlew :app:assembleDebug

# Юнит-тесты
./gradlew :app:testDebugUnitTest

# Обычные UI-тесты (нужен запущенный эмулятор/устройство)
./gradlew :app:connectedDebugAndroidTest

# Записать эталонные скриншоты (Dropshots)
./gradlew :app:recordScreenshots

# Проверить скриншоты (Dropshots)
./gradlew :app:verifyScreenshots
