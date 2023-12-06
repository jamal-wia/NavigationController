# **NavigationController**
[![Release](https://jitpack.io/v/jamal-wia/NavigationController.svg)](https://jitpack.io/#jamal-wia/NavigationController) [![license](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)

**NavigationController** - это современная навигационная библиотека для Android, которая использует
другую навигационную библиотеку под названием ❤ [Alligator](https://github.com/aartikov/Alligator)
❤. Вместе они обеспечивают мощную и расширяемую систему навигации. Для лучшего понимания
рекомендуется ознакомиться с документацией **Alligator**

#### Посмотреть работу библиотеки в реальных проектах:

https://play.google.com/store/apps/details?id=com.ummalife.android

## **Подключение**

``` Gradle
repositories {
    ....
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
            NavigationControllerFragment.Builder()
                .setRootScreen(LineNavigationControllerFragmentScreen())
                .show(supportFragmentManager, R.id.navigation_container) //  R.id.navigation_container - Предстовляет из себя FragmentContainerView в layout/activity_main.xml
        }
    }
}
```

## License

```
The MIT License (MIT)

Copyright (c) 2023 Jamal Aliev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
