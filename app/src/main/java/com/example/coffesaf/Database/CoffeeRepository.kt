package com.example.coffesaf.Database

class CoffeeRepository(private val coffeeDao: CoffeeDao) {
    suspend fun getCoffeeByCategory(category: String): List<CoffeeEntity> {
        return coffeeDao.getCoffeeByCategory(category)
    }

    suspend fun getCoffeeById(id: Int): CoffeeEntity? {
        return coffeeDao.getCoffeeById(id)
    }

    suspend fun getAllCategories(): List<String> {
        return coffeeDao.getAllCategories()
    }

    suspend fun getAll(): List<CoffeeEntity> {
        return coffeeDao.getAll()
    }

    suspend fun insertList(coffee: List<CoffeeEntity>) {
        coffeeDao.insertAll(coffee)
    }
}