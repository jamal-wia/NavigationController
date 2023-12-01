# **NavigationController**

### NavigationController
** - это современная навигационная библиотека для Android, которая использует другую навигационную библиотеку под названием [Alligator](https://github.com/aartikov/Alligator). Вместе они обеспечивают мощную и расширяемую систему навигации.

## **Подключение**

`
repositories {
google()
mavenCentral()
maven { setUrl("https://jitpack.io") }
}
`

`implementation("com.github.jamal-wia:NavigationController:1.0.6")`

## **Быстрый старт**

Так как библиотека при переключении экранов использует объекты **Screen** вам необходимо расписать
связи между объектами **Screen** и реальными (**Activity**, **Fragment** или **DialogFragment**)

### Шаг 1

#### Пример кода

`
class AppRegistryNavigationControllerFactory : NavigationControllerFactory() {
init {
registerFragment(
Screens.ColorFragmentScreen::class.java,
ColorFragment::class.java
)
}
}
`
Так же есть методы: `registerDialogFragment` `registerActivity`

### Шаг 2

Далее вам необходимо инициализировать библиотеку используя следующий код

#### Пример кода

`NavigationControllerHolder.createNavigator(AppRegistryNavigationControllerFactory())`

### Шаг 3

В той активти в которой вы хотите использовать навигацию на основе фрагментов необходимо **один раз
** запустить **NavigationContextChangerFragment**

#### Пример кода

`
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
`


