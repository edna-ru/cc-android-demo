# Правила приложения-хоста: только то, что не приходит из других слоёв.
#
# Классы SDK удерживают consumer-rules.pro модулей chatcentercore и chatcenterui, AGP
# подключает их автоматически. Библиотеки везут свои правила внутри артефактов: gson,
# retrofit, okhttp, work-runtime, lifecycle, fragment, navigation, kotlin-reflect, firebase
# и play-services. Компоненты манифеста, view из разметки и фрагменты из графа навигации
# удерживают правила AAPT2. Дублировать их здесь не нужно.
#
# ServerConfig и UserInfo Gson хранит в SharedPreferences: на их полях стоит @SerializedName,
# класс, поля и конструктор удерживает правило из jar gson. Новая модель, которую Gson
# сохраняет или читает, размечается так же, а не правилом в этом файле.

# Тип элемента в списке примеров вычисляет ordinal() из Extensions.kt: через kotlin-reflect
# проверяет, что DemoSamplesListItem - sealed, и ищет класс элемента среди вложенных через
# getClasses(). Без правила R8 сольёт или удалит вложенные классы, и DemoSamplesAdapter
# выберет не ту разметку строки. Аннотацию Metadata и атрибут InnerClasses держат правила
# kotlin-reflect
-keep class edna.chatcenter.demo.appCode.models.DemoSamplesListItem { *; }
-keep class edna.chatcenter.demo.appCode.models.DemoSamplesListItem$** { *; }

# HMS Push: правило из документации Huawei. AAR HMS везут собственные адресные правила, но снять
# это можно только после проверки приёма push на устройстве с HMS. Правила той же документации
# на com.huawei.hianalytics и com.huawei.updatesdk не нужны: этих классов в сборке нет
-keep class com.huawei.hms.** { *; }

# HMS ссылается на классы прошивки Huawei и HiAnalytics, которых нет ни в android.jar,
# ни в зависимостях: без -dontwarn R8 останавливает сборку на Missing class. Список берётся
# из missing_rules.txt, который R8 создаёт при такой ошибке. -ignorewarnings не подходит:
# он скрывает и все остальные предупреждения
-dontwarn android.telephony.HwTelephonyManager
-dontwarn com.huawei.android.os.BuildEx$VERSION
-dontwarn com.huawei.android.telephony.ServiceStateEx
-dontwarn com.huawei.hianalytics.process.HiAnalyticsConfig$Builder
-dontwarn com.huawei.hianalytics.process.HiAnalyticsConfig
-dontwarn com.huawei.hianalytics.process.HiAnalyticsInstance$Builder
-dontwarn com.huawei.hianalytics.process.HiAnalyticsInstance
-dontwarn com.huawei.hianalytics.process.HiAnalyticsManager
-dontwarn com.huawei.hianalytics.util.HiAnalyticTools
-dontwarn com.huawei.libcore.io.ExternalStorageFile
-dontwarn com.huawei.libcore.io.ExternalStorageFileInputStream
-dontwarn com.huawei.libcore.io.ExternalStorageFileOutputStream
-dontwarn com.huawei.libcore.io.ExternalStorageRandomAccessFile

# Читаемые стектрейсы в отчётах Crashlytics и AppCenter
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
