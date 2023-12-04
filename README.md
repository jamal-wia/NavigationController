# **NavigationController**

**NavigationController** - это современная навигационная библиотека для Android, которая использует
другую навигационную библиотеку под названием ❤ [Alligator](https://github.com/aartikov/Alligator)
❤. Вместе они обеспечивают мощную и расширяемую систему навигации. Для лучшего понимания
рекомендуется ознакомиться с документацией **Alligator**

#### Посмотреть работу библиотеки в реальных проектах:

https://play.google.com/store/apps/details?id=com.ummalife.android

## **Подключение**

``` Gradle
repositories {
    .....
    maven { setUrl("https://jitpack.io") }
}
```

``` Gradle
implementation("com.github.jamal-wia:NavigationController:1.1.0")
```

## **Быстрый старт в 3 шага**

### Шаг 1

Так как библиотека при переключении экранов использует объекты **Screen** вам необходимо расписать
связи между объектами **Screen** и реальными (**Activity**, **Fragment** или **DialogFragment**)

#### Пример кода

``` Kotlin
class AppRegistryNavigationControllerFactory : NavigationControllerFactory() {
    init {
        registerFragment(
            Screens.ColorFragmentScreen::class.java,
            ColorFragment::class.java
        )
    }
}
```
Так же есть методы: `registerDialogFragment` `registerActivity`

### Шаг 2

Далее вам необходимо инициализировать библиотеку используя следующий код

#### Пример кода

``` Kotlin
NavigationControllerHolder.createNavigator(AppRegistryNavigationControllerFactory())
```

### Шаг 3

В той активти в которой вы хотите использовать навигацию на основе фрагментов необходимо
**один раз** запустить **NavigationContextChangerFragment**

#### Пример кода

``` Kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) { // Что бы код выполнился только один раз
            NavigationContextChangerFragment.show(
                supportFragmentManager,
                R.id.navigation_container // R.id.navigation_container - Предстовляет из себя FragmentContainerView в layout/activity_main.xml
            )
        }
    }
}
```


