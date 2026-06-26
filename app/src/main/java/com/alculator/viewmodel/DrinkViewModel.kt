package com.alculator.viewmodel

import androidx.lifecycle.ViewModel
import com.alculator.data.Drink
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DrinkViewModel : ViewModel() {
    private val _drinks = MutableStateFlow<List<Drink>>(emptyList())
    val drinks: StateFlow<List<Drink>> = _drinks.asStateFlow()

    fun add(drink: Drink) {
        _drinks.value = _drinks.value + drink
    }

    fun update(drink: Drink) {
        _drinks.value = _drinks.value.map { if (it.id == drink.id) drink else it }
    }

    fun remove(id: String) {
        _drinks.value = _drinks.value.filter { it.id != id }
    }

    fun clear() {
        _drinks.value = emptyList()
    }
}
