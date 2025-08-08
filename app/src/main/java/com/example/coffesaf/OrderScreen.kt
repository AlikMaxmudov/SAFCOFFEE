package com.example.coffesaf

import YandexMapView
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffesaf.Database.CoffeeEntity
import com.example.coffesaf.ui.theme.CoffeSafTheme
import java.net.URLEncoder

class OrderActivity : ComponentActivity() {
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  val cartItems = intent.getParcelableArrayListExtra<CoffeeEntity>("cartItems") ?: emptyList()

        val cartItems = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("cartItems", CoffeeEntity::class.java) ?: emptyList()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<CoffeeEntity>("cartItems") ?: emptyList()
        }

        val phone = intent.getStringExtra("phone") ?: ""
        val firstName = intent.getStringExtra("firstName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val deliveryType = intent.getStringExtra("deliveryType") ?: "delivery"
        val deliveryTime = intent.getStringExtra("deliveryTime") ?: ""

        cartViewModel.initCart(cartItems)

        setContent {
            CoffeSafTheme {
                OrderScreen(
                    cartItems = cartItems,
                    phone = phone,
                    firstName = firstName,
                    lastName = lastName,
                    address = address,
                    deliveryType = deliveryType,
                    deliveryTime = deliveryTime,
                    onBackClick = {
                        cartViewModel.clearCart()
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    cartItems: List<CoffeeEntity>,
    phone: String,
    firstName: String,
    lastName: String,
    address: String,
    deliveryType: String,
    deliveryTime: String,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Заказ оформлен",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003325)
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF003325)
                )
            ) {
                Text("Вернуться обратно", fontSize = 18.sp)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Иконка успеха
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Успех",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Заголовок
            Text(
                text = "Заказ успешно оформлен!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF003325),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Карточка с информацией о заказе
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Заголовок информации
                    Text(
                        text = "Информация о заказе",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF003325),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Список товаров
                    if (cartItems.isNotEmpty()) {
                        Text(
                            text = "Ваши товары (${cartItems.size}):",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Log.d("OrderDebug", "Received cart items: ${cartItems.size}")
                        cartItems.forEach { item ->
                            Log.d("OrderDebug", "Item: ${item.name}, ${item.priceM}")
                        }
                        cartItems.forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                AsyncImage(
                                    model = item.imageUri,
                                    contentDescription = item.name,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = item.type,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = item.priceM,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF003325)
                                    )
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Способ получения
                    Text(
                        text = "Способ получения: ${if (deliveryType == "delivery") "Доставка" else "Самовывоз"}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (deliveryTime.isNotEmpty()) {
                        Text(
                            text = "Время: $deliveryTime",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Информация о клиенте
                    Text(
                        text = "Контактные данные:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$firstName $lastName",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (deliveryType == "delivery") {
                        Text(
                            text = address,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Итого
                    val totalPrice = cartItems.sumOf {
                        it.priceM.replace(Regex("[^\\d]"), "").toIntOrNull() ?: 0
                    }
                    Text(
                        text = "Итого: $totalPrice ₽",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003325),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Сообщение о приготовлении
            Text(
                text = "Ваш кофе уже готовится. Скоро сможете насладиться его вкусом!",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 24.sp
            )

            if (deliveryType == "delivery" && address.isNotEmpty()) {
                YandexMapView(
                    startAddress = "Авиамоторная улица, 12, Москва",  // Ваш адрес кафе
                    endAddress = address,                             // Адрес клиента
                    showRoute = true                                  // Показывать маршрут
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OrderScreenPreview() {
    CoffeSafTheme {
        OrderScreen(
            cartItems = listOf(
                CoffeeEntity(
                    id = 1,
                    name = "Капучино",
                    type = "Классический",
                    description = "Описание",
                    ingredients = "Ингредиенты",
                    imageUri = "",
                    priceS = "150 ₽",
                    priceM = "200 ₽",
                    priceL = "250 ₽",
                    rating = 4.5f,
                    category = "Капучино"
                )
            ),
            phone = "+79001234567",
            firstName = "Иван",
            lastName = "Иванов",
            address = "ул. Примерная, д. 1",
            deliveryTime = "",
            deliveryType = "",
            onBackClick = {}
        )
    }
}