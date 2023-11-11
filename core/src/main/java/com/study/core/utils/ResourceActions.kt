package com.study.core.utils


typealias ResourceAction<T> = (T) -> Unit

/**
 * Черга дій, де дії виконуються лише за наявності ресурсу(activity).
 * Якщо його немає, то дія додається до черги і чекає,
 * поки ресурс стане доступним.
 */
class ResourceActions<T> {

    var resource: T? = null
        set(newValue) {
            field = newValue
            if (newValue != null) {
                actions.forEach { it(newValue) }
                actions.clear()
            }
        }

    private val actions = mutableListOf<ResourceAction<T>>()

    /**
     * Викликати дію тільки тоді, коли ресурс(activity) існує (не є null).
     * В іншому випадку дія відкладається до тих пір,
     * поки ресурсу(activity) не буде присвоєно деяке non-null значення
     */
    operator fun invoke(action: ResourceAction<T>) {
        val resource = this.resource
        if (resource == null) {
            actions += action
        } else {
            action(resource)
        }
    }

    fun clear() {
        actions.clear()
    }
}