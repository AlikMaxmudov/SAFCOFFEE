// CartState.kt
package com.example.coffesaf

import androidx.compose.runtime.mutableStateListOf
import com.example.coffesaf.Database.CoffeeEntity

object CartState {
    val cartItems = mutableStateListOf<CoffeeEntity>()
    val loyaltyCards = mutableStateListOf<LoyaltyCard>()

    init {
        // Инициализация карты лояльности
        repeat(8) { index ->
            loyaltyCards.add(LoyaltyCard(index))
        }
    }

    fun addToCart(item: CoffeeEntity) {
        cartItems.add(item)
    }

    fun removeFromCart(item: CoffeeEntity) {
        cartItems.remove(item)
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun completePurchase() {
        // Обновляем карту лояльности
        cartItems.forEach { _ ->
            updateLoyaltyCard()
        }
        clearCart()
    }

    fun updateLoyaltyCard() {
        val index = loyaltyCards.indexOfFirst { !it.isFilled }
        if (index != -1) {
            loyaltyCards[index] = loyaltyCards[index].copy(isFilled = true)
        }

        // Сброс карты при заполнении
        if (loyaltyCards.all { it.isFilled }) {
            loyaltyCards.clear()
            repeat(8) { i -> loyaltyCards.add(LoyaltyCard(i)) }
        }
    }
}