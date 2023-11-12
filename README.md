# Core - модуль документація

***Вимагає реалізувати 2 контракти для роботи:***
Вимагає реалізувати 2 контракти для роботи:
- ModelsProvider - провайдер залежностей для ViewModelFactory
- FragmentsHolder - контракт для Activity який підключає навігацію.


> Також має наступні контракти для роботи через _CoreViewModel_
> + _Navigator_ - контракт навігації
> + _UIActions_
>   + _toast_ - показ повідомлень
>   + _getString_ - доставка з ресурсів тексту

# Порядок та приклад реалізації контрактів

**ModelsProvider**
- Необхідно передати залежності які будуть передаватись ViewModelFactory до конструкторів ViewModel
```
class App: Application(), ModelsProvider {

    private val dependency = listOf<Any>(
        InMemoryColorsRepository()
    )

// Передати залежності які будуть використовувати 
// скоуп Application
    override val models: List<Any>
        get() = dependency
}
```

**FragmentsHolder**

1. Створити [CoreViewModel] так передати UIActions та NavigatorManager
2. Оголосити сам навігатор, типу [StackFragmentNavigator]
3. Створити StackFragmentNavigator в onCreate
4.  У notifyScreenUpdates прокинути navigator.notifyScreenUpdates()
5. Повернути єкземпляр класу [CoreViewModel] до Core модулю

_MainActivity.kt_
```
/**
 * Приклад реалізації Activity
 */
class MainActivity : AppCompatActivity(), FragmentsHolder {

/**
* 2. Оголосити сам навігатор, типу [StackFragmentNavigator]
*/
    private lateinit var navigator: StackFragmentNavigator

/**
* 1. Створити [CoreViewModel] так передати UIActions та 
* NavigatorManager
*/
    private val activityViewModel by viewModelCreator<CoreViewModel> {
        CoreViewModel(
            uiActions = AndroidUIActions(applicationContext),
            navigatorManager = NavigatorManager(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 3. Створити StackFragmentNavigator в onCreate
        navigator = StackFragmentNavigator(
            activity = this,
            navigatorManager = activityViewModel.navigatorManager,
            savedInstanceState = savedInstanceState,
            containerId = R.id.fragmentContainer,
            animations = Animations(
                enterAnim = R.anim.enter,
                exitAnim = R.anim.exit,
                popEnterAnim = R.anim.pop_enter,
                popExitAnim = R.anim.pop_exit
            ),
            initialScreenCreator = { CurrentColorFragment.Screen() }
        )

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

/**
* 4. У notifyScreenUpdates прокинути navigator.notifyScreenUpdates()
*/
    override fun notifyScreenUpdates() {
        navigator.notifyScreenUpdates()
        val f = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (f is HasScreenTitle && f.getScreenTitle() != null) {
            // fragment has custom screen title -> display it
            supportActionBar?.title = f.getScreenTitle()
        } else {
            supportActionBar?.title = getString(R.string.app_name)
        }
    }

/**
* 5. Повернути єкземпляр класу [CoreViewModel] до Core модулю
*/
    override fun getActivityScopeViewModel(): CoreViewModel {
        return activityViewModel
    }

}
```

# Приклад використання

### _Fragment_

```
class ExampleFragment : BaseFragment() {

// Якщо потрібно передати аргументи до єкрану,
// використовуемо конструктор з даними які серіалізуються
// class Screen(value: String) : BaseScreen
    class Screen : BaseScreen

    override val viewModel by screenViewModel<ExampleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        TODO("Основна робота")
    }
}
```

### _ViewModel_

```
class ExampleViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
) : BaseViewModel() {
		
// Прийде результат з інших єкранів
    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is TestClass) {
            val message = uiActions.getString(R.string.test, result.name)
            uiActions.toast(message)
        }
    }
		
// Приклад навігації на наступний єкран
    fun navigateToNextScreen(value: Any) {
        val screen = ExampleSecondFragment.Screen(value)
        navigator.launch(screen)
    }
}
```
