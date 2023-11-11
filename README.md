# Core - модуль документація

***Вимагає реалізувати 2 контракти для роботи:***
- ModelsProvider - провайдер залежностей для ViewModelFactory
- NotifyAdapter
  - _notifyScreenUpdates_ - контракт з Activity для апдейту компонентів Activity
  - _containerId_ -  доставка containerId у ViewModelFactory.


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

**NotifyAdapter**

1. Додаемо CoreViewModel
2. Додаемо fragmentCallbacks
3. Рееструемо fragmentCallbacks в onCreate, також відписуемось в onDestroy
4. Передаемо у вьюмодель посилання на Activity в onResume, та відпис. у onPause
5. Прокидуємо далі з фрагменту результат у вьюмодель
6. Прокидуемо id на fragmentContainer

_MainActivity.kt_
```
/**
 * Приклад реалізації Activity
 */
class MainActivity : AppCompatActivity(), NotifyAdapter {

    /**
     * 1. Додається [CoreViewModel] та далі рееструється callback
     * на життевий цикл
     */
    private val activityViewModel by viewModels<CoreViewModel> {
        ViewModelProvider.AndroidViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            // початковий єкран якщо додаток відкрито вперше
            activityViewModel.launchFragment(
                activity = this,
                screen = CurrentColorFragment.Screen(),
                addToBackStack = false
            )
        }

        // 3. реєстрація життевого циклу фрагменту
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallbacks, false)
    }

    override fun onDestroy() {
        // 3. відписка від життевого циклу фрагменту
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallbacks)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        // 4. стає можливою навігація лише якщо acivity існує
        activityViewModel.whenActivityActive.resource = this
    }

    override fun onPause() {
        super.onPause()
        // 4. стираємо посилання на acivity
        activityViewModel.whenActivityActive.resource = null
    }
    
    /**
     * 6. Прокидуемо id на fragmentContainer
     */
    override val containerId: Int
        get() = R.id.fragmentContainer

    override fun notifyScreenUpdates() {
        val f = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        
        val result = activityViewModel.result.value?.getValue() ?: return
        if (f is BaseFragment) {
            // 5. Якщо з єкрану повертається результат, передаємо до вьюмоделі
            f.viewModel.onResult(result)
        }
    }

    /**
     * 2. У [onFragmentViewCreated] сповіщуємо які зміни треба оновити
     */
    private val fragmentCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            notifyScreenUpdates()
        }
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
