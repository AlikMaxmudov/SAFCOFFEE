package com.example.coffesaf.Database

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffesaf.R
import kotlinx.coroutines.launch

class CoffeeViewModel(private val repository: CoffeeRepository) : ViewModel() {
    private val _categories = mutableStateOf<List<String>>(emptyList())
    val categories: State<List<String>> = _categories

    private val _coffeeItems = mutableStateOf<List<CoffeeEntity>>(emptyList())
    val coffeeItems: State<List<CoffeeEntity>> = _coffeeItems

    init {
        viewModelScope.launch {
            loadInitialData()
        }
    }

    suspend fun loadInitialData() {
        if (repository.getAllCategories().isEmpty()) {
            repository.insertList(getInitialCoffeeData())
        }
        _categories.value = repository.getAllCategories()
        Log.e("ara", "DELETE ME: all categories: ${_categories.value}")
        Log.e("ara", "DELETE ME: all coffee: ${repository.getAll()}")
        if (_categories.value.isNotEmpty()) {
            selectCategory(_categories.value.first())
        }
    }

    fun selectCategory(category: String) {
        viewModelScope.launch {
            _coffeeItems.value = repository.getCoffeeByCategory(category)
            Log.e("ara", "DELETE ME: all categories: ${_coffeeItems.value}")
        }
    }

    fun getInitialCoffeeData(): List<CoffeeEntity> {
        return listOf(
            // Эспрессо
            CoffeeEntity(
                name = "Эспрессо",
                type = "Классический",
                description = "Насыщенный кофейный напиток объемом 30 мл, приготовленный под высоким давлением. Имеет густую золотистую пенку (крема) и интенсивный вкус с фруктовыми нотами.",
                ingredients = "Кофе, вода",
                imageUri = "android.resource://com.example.coffesaf/drawable/espresso_classic",
                priceS = "90 ₽",
                priceM = "120 ₽",
                priceL = "150 ₽",
                rating = 4.2f,
                category = "Эспрессо"
            ),
            CoffeeEntity(
                name = "Эспрессо",
                type = "Двойной",
                description = "Две порции классического эспрессо (60 мл) для истинных ценителей крепкого кофе. Сохраняет все характеристики эспрессо, но с более выраженным вкусом и ароматом.",
                ingredients = "Кофе, вода",
                imageUri = "android.resource://com.example.coffesaf/drawable/espresso_double",
                priceS = "130 ₽",
                priceM = "160 ₽",
                priceL = "190 ₽",
                rating = 4.4f,
                category = "Эспрессо"
            ),
            CoffeeEntity(
                name = "Эспрессо",
                type = "Ристретто",
                description = "Концентрированная версия эспрессо (15-20 мл) с меньшим количеством воды. Обладает более сладким и насыщенным вкусом без горечи. В переводе с итальянского означает 'ограниченный'.",
                ingredients = "Кофе, вода",
                imageUri = "android.resource://com.example.coffesaf/drawable/espresso_ristretto",
                priceS = "120 ₽",
                priceM = "150 ₽",
                priceL = "180 ₽",
                rating = 4.9f,
                category = "Эспрессо"
            ),
            CoffeeEntity(
                name = "Эспрессо",
                type = "Лунго",
                description = "'Продолженный' эспрессо объемом 50-60 мл, где через кофе пропускают больше воды. Имеет менее интенсивный, но более продолжительный вкус с тонкими оттенками.",
                ingredients = "Кофе, вода",
                imageUri = "android.resource://com.example.coffesaf/drawable/espresso_lungo",
                priceS = "140 ₽",
                priceM = "170 ₽",
                priceL = "200 ₽",
                rating = 4.6f,
                category = "Эспрессо"
            ),
            CoffeeEntity(
                name = "Эспрессо",
                type = "Допио",
                description = "Двойной эспрессо, приготовленный как одна порция (60 мл) с увеличенной дозой молотого кофе. Отличается особенно густой крема и насыщенным вкусом.",
                ingredients = "Кофе, вода",
                imageUri = "android.resource://com.example.coffesaf/drawable/esrpesso_dopio",
                priceS = "150 ₽",
                priceM = "180 ₽",
                priceL = "210 ₽",
                rating = 4.9f,
                category = "Эспрессо"
            ),
            CoffeeEntity(
                name = "Эспрессо",
                type = "Романо",
                description = "Эспрессо с долькой лимона, которая подчеркивает фруктовые ноты кофе. Традиционный итальянский способ подачи, где кислинка лимона балансирует горчинку кофе.",
                ingredients = "Кофе, вода, лимон",
                imageUri = "android.resource://com.example.coffesaf/drawable/esrpesso_romano",
                priceS = "150 ₽",
                priceM = "180 ₽",
                priceL = "210 ₽",
                rating = 5.0f,
                category = "Эспрессо"
            ),

            // Капучино
            CoffeeEntity(
                name = "Капучино",
                type = "Стандарт",
                description = "Классическое сочетание эспрессо, горячего молока и молочной пены в равных пропорциях (1:1:1). Имеет нежную текстуру и сбалансированный вкус. Идеальная температура подачи - 60-65°C.",
                ingredients = "Кофе, молоко",
                imageUri = "android.resource://com.example.coffesaf/drawable/cappucino_classic",
                priceS = "180 ₽",
                priceM = "210 ₽",
                priceL = "240 ₽",
                rating = 4.5f,
                category = "Капучино"
            ),
            CoffeeEntity(
                name = "Капучино",
                type = "Чигаро",
                description = "Капучино с особой техникой взбивания молока, создающей шелковистую микропену. Подается в прозрачном стакане для демонстрации идеальных слоев. Название происходит от испанского 'чигаро' - сигара.",
                ingredients = "Кофе, молоко",
                imageUri = "android.resource://com.example.coffesaf/drawable/chicaro",
                priceS = "200 ₽",
                priceM = "230 ₽",
                priceL = "260 ₽",
                rating = 4.2f,
                category = "Капучино"
            ),
            CoffeeEntity(
                name = "Капучино",
                type = "Скуро",
                description = "'Темный' капучино с увеличенной порцией эспрессо. Имеет более выраженный кофейный вкус и менее сладкий, чем классический вариант. Для тех, кто любит ощущать настоящий вкус кофе.",
                ingredients = "Кофе, молоко",
                imageUri = "android.resource://com.example.coffesaf/drawable/capuputchino_scuro",
                priceS = "190 ₽",
                priceM = "220 ₽",
                priceL = "250 ₽",
                rating = 4.9f,
                category = "Капучино"
            ),
            CoffeeEntity(
                name = "Капучино",
                type = "Шоколадный",
                description = "Гармоничное сочетание эспрессо, молока и шоколадного сиропа с декоративным рисунком на пене. Напоминает горячий шоколад с кофейными нотками.",
                ingredients = "Кофе, молоко, шоколад",
                imageUri = "android.resource://com.example.coffesaf/drawable/capputchono_chocolate",
                priceS = "210 ₽",
                priceM = "240 ₽",
                priceL = "270 ₽",
                rating = 5.0f,
                category = "Капучино"
            ),
            CoffeeEntity(
                name = "Капучино",
                type = "Ванильный",
                description = "Нежный вариант с натуральным ванильным сиропом. Сладковатый аромат ванили идеально сочетается с горьковатыми нотами эспрессо. Идеальный выбор для сладкоежек.",
                ingredients = "Кофе, молоко, ваниль",
                imageUri = "android.resource://com.example.coffesaf/drawable/capputchino_vanilla",
                priceS = "210 ₽",
                priceM = "240 ₽",
                priceL = "270 ₽",
                rating = 4.8f,
                category = "Капучино"
            ),
            CoffeeEntity(
                name = "Капучино",
                type = "Карамельный",
                description = "Сочетание эспрессо с карамельным сиропом и молочной пеной. Карамель добавляет напитку мягкую сладость и золотистый оттенок. Часто украшается карамельным сетчатым рисунком.",
                ingredients = "Кофе, молоко, карамель",
                imageUri = "android.resource://com.example.coffesaf/drawable/capputchino_caramele",
                priceS = "210 ₽",
                priceM = "240 ₽",
                priceL = "270 ₽",
                rating = 4.7f,
                category = "Капучино"
            ),

            // Раф
            CoffeeEntity(
                name = "Раф",
                type = "Классический",
                description = "Готовится из эспрессо, сливок и ванильного сахара, взбитых вместе паром. Имеет нежную воздушную текстуру и мягкий сливочный вкус. Был изобретен в Москве в 1990-х и назван в честь клиента Рафаэля.",
                ingredients = "Кофе, сливки, ванильный сахар",
                imageUri = "android.resource://com.example.coffesaf/drawable/raf_classic",
                priceS = "220 ₽",
                priceM = "250 ₽",
                priceL = "280 ₽",
                rating = 4.9f,
                category = "Раф"
            ),
            CoffeeEntity(
                name = "Раф",
                type = "Медовый",
                description = "Вариация классического рафа с добавлением натурального меда вместо сахара. Мед придает напитку особую сладость, полезные свойства и неповторимый цветочный аромат.",
                ingredients = "Кофе, сливки, мед",
                imageUri = "android.resource://com.example.coffesaf/drawable/raf_med",
                priceS = "240 ₽",
                priceM = "270 ₽",
                priceL = "300 ₽",
                rating = 4.4f,
                category = "Раф"
            ),
            CoffeeEntity(
                name = "Раф",
                type = "Ореховый",
                description = "Десертный вариант с ореховым сиропом (обычно фундука). Создает впечатление десерта в чашке с богатым ореховым послевкусием. Часто посыпается дроблеными орехами.",
                ingredients = "Кофе, сливки, ореховый сироп",
                imageUri = "android.resource://com.example.coffesaf/drawable/raf_orex",
                priceS = "230 ₽",
                priceM = "260 ₽",
                priceL = "290 ₽",
                rating = 4.7f,
                category = "Раф"
            ),
            CoffeeEntity(
                name = "Раф",
                type = "Сникерс",
                description = "Вдохновленный популярным батончиком, сочетает шоколадный и карамельный сиропы с дроблеными орехами. Настоящий десертный кофе с карамельно-ореховым вкусом.",
                ingredients = "Кофе, сливки, шоколад, карамель",
                imageUri = "android.resource://com.example.coffesaf/drawable/raf_snickers",
                priceS = "290 ₽",
                priceM = "320 ₽",
                priceL = "350 ₽",
                rating = 5.0f,
                category = "Раф"
            ),
            CoffeeEntity(
                name = "Раф",
                type = "Соленая карамель",
                description = "Идеальный баланс сладкой карамели и морской соли. Контраст вкусов делает этот напиток особенно запоминающимся. Соль подчеркивает сладость карамели и смягчает горечь кофе.",
                ingredients = "Кофе, сливки, соленая карамель",
                imageUri = "android.resource://com.example.coffesaf/drawable/raf_solenaya",
                priceS = "280 ₽",
                priceM = "310 ₽",
                priceL = "340 ₽",
                rating = 5.0f,
                category = "Раф"
            ),

            // Авторские
            CoffeeEntity(
                name = "Фирменный кофе",
                type = "Авторская рецептура",
                description = "Уникальная рецептура нашего шеф-бариста, включающая специально подобранную смесь зерен и секретные ингредиенты. Меняется сезонно в зависимости от доступности лучших сортов кофе.",
                ingredients = "Кофе, секретные ингредиенты",
                imageUri = "android.resource://com.example.coffesaf/drawable/firmen_coffe",
                priceS = "290 ₽",
                priceM = "320 ₽",
                priceL = "350 ₽",
                rating = 4.9f,
                category = "Авторские"
            ),
            CoffeeEntity(
                name = "Сезонный напиток",
                type = "Специальный рецепт",
                description = "Ограниченное предложение по сезону. Летом может содержать ягодные ноты, зимой - пряные специи. Всегда свежий и неожиданный вкусовой опыт.",
                ingredients = "Кофе, сезонные ингредиенты",
                imageUri = "android.resource://com.example.coffesaf/drawable/seasen_coffe",
                priceS = "300 ₽",
                priceM = "330 ₽",
                priceL = "360 ₽",
                rating = 5.0f,
                category = "Авторские"
            ),
            CoffeeEntity(
                name = "Coffee SAF",
                type = "Авторская рецептура",
                description = "Фирменный напиток кофейни SAF, сочетающий эспрессо, фирменный сироп на основе тропических фруктов и особый способ подачи с дымком. Настоящая визитная карточка нашего заведения.",
                ingredients = "Кофе, фирменный сироп",
                imageUri = "android.resource://com.example.coffesaf/drawable/coffe_avtor_theree",
                priceS = "320 ₽",
                priceM = "350 ₽",
                priceL = "380 ₽",
                rating = 5.0f,
                category = "Авторские"
            ),
            CoffeeEntity(
                name = "Хлопай и взлетай",
                type = "Авторская рецептура",
                description = "Энергичный напиток для бодрости /n на основе двойного эспрессо с добавлением тонизирующих трав и натуральных энергетиков. Рекомендуем для утреннего пробуждения или перед важными делами.",
                ingredients = "Кофе, энергетические компоненты",
                imageUri = "android.resource://com.example.coffesaf/drawable/coffe_avtor_four",
                priceS = "390 ₽",
                priceM = "420 ₽",
                priceL = "450 ₽",
                rating = 4.8f,
                category = "Авторские"
            )
        )
    }
}
