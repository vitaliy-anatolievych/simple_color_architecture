package com.study.core.navigator

import com.study.core.contracts.Navigator
import com.study.core.utils.ResourceActions
import com.study.core.views.BaseScreen

/**
 * Проміжний навігатор (Кєш викликів), який працює на стороні CoreViewModel
 *
 * Його завдання: брати усі виклики які будуть поступати по навігатору,
 * та якщо не буде ресурсу який зможе ці виклики обробити, буде відкладати в кєш,
 * у протилежному випадку - виконає ці виклики за наявності ресурсу.
 */
class NavigatorManager: Navigator {

    private var targetNavigator = ResourceActions<Navigator>()

    override fun launch(screen: BaseScreen) = targetNavigator {
        it.launch(screen)
    }

    override fun goBack(result: Any?) = targetNavigator {
        it.goBack(result)
    }

    /**
     * @param navigator може слугувати Activity,
     * яки потрібно підписати у onResume
     */
    fun setTarget(navigator: Navigator?) {
        targetNavigator.resource = navigator
    }

    fun clear() {
        targetNavigator.clear()
    }

}