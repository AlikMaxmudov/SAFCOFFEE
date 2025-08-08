// CartViewModel.kt
package com.example.coffesaf

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.coffesaf.Database.CoffeeEntity


class CartViewModel : ViewModel() {
    // Доступ к глобальному состоянию
    val cartItems get() = CartState.cartItems
    val loyaltyCards get() = CartState.loyaltyCards
    val cartItemCount get() = cartItems.size

    // Прокси-методы к CartState
    fun addToCart(item: CoffeeEntity) = CartState.addToCart(item)
    fun removeFromCart(item: CoffeeEntity) = CartState.removeFromCart(item)
    fun clearCart() = CartState.clearCart()
    fun completePurchase() = CartState.completePurchase()
    fun updateLoyaltyCard() = CartState.updateLoyaltyCard()

    // Метод для инициализации корзины
    fun initCart(items: List<CoffeeEntity>) {
        CartState.clearCart()
        items.forEach { CartState.addToCart(it) }
    }
}
