package com.example.coffesaf

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffesaf.Database.CoffeeEntity
import com.example.coffesaf.ui.theme.CoffeSafTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class PersonalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cartItems = intent.getParcelableArrayListExtra<CoffeeEntity>("cartItems") ?: emptyList()
        val phone = intent.getStringExtra("phone") ?: ""
        val cartViewModel: CartViewModel by viewModels()

        setContent {
            CoffeSafTheme {
                PersonalOrderScreen(
                    cartItems = cartItems,
                    initialPhone = phone,
                    onBackClick = { finish() },
                    onOrderSubmit = { firstName, lastName, address, deliveryType, deliveryTime ->
                        // Отправляем данные в Telegram (в фоновом потоке)
                        CoroutineScope(Dispatchers.IO).launch {
                            sendOrderToTelegram(
                                cartItems = cartItems,
                                firstName = firstName,
                                lastName = lastName,
                                phone = phone,
                                address = address,
                                deliveryType = deliveryType,
                                deliveryTime = deliveryTime
                            )
                        }

                        // Переходим в OrderActivity
                        val intent = Intent(this, OrderActivity::class.java).apply {
                            putParcelableArrayListExtra("cartItems", ArrayList(cartViewModel.cartItems))
                            putExtra("phone", phone)
                            putExtra("firstName", firstName)
                            putExtra("lastName", lastName)
                            putExtra("address", address)
                            putExtra("deliveryType", deliveryType)
                            putExtra("deliveryTime", deliveryTime)
                        }
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun CartItem(
    item: CoffeeEntity,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
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
                style = MaterialTheme.typography.bodyLarge,
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
                color = Color(0xFF003325)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalOrderScreen(
    cartItems: List<CoffeeEntity>,
    initialPhone: String,
    onBackClick: () -> Unit,
    onOrderSubmit: (String, String, String, String, String) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var deliveryType by remember { mutableStateOf("delivery") } // "delivery" или "pickup"
    var deliveryTime by remember { mutableStateOf("") }
    val deliveryTimes = listOf("Как можно скорее", "10:00-12:00", "12:00-14:00", "14:00-16:00", "16:00-18:00", "18:00-20:00")

    // Состояния для автодополнения адресов
    var isAddressDropdownExpanded by remember { mutableStateOf(false) }
    val moscowAddresses = remember {
        listOf(
            "ул. Авиамоторная, 12",
            "ул. Тверская, 7",
            "ул. Арбат, 25",
            "пр. Ленинский, 32",
            "ул. Новый Арбат, 15",
            "ул. Пушкина, 10",
            "пр. Мира, 20",
            "ул. Кутузовский проспект, 30"
        )
    }
    val filteredAddresses = remember(address) {
        if (address.length > 2) {
            moscowAddresses.filter { it.contains(address, ignoreCase = true) }
        } else {
            emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Оформление заказа",
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
                onClick = {
                    onOrderSubmit(
                        firstName,
                        lastName,
                        if (deliveryType == "delivery") address else "",
                        deliveryType,
                        deliveryTime
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF003325)
                ),
                enabled = firstName.isNotBlank() && lastName.isNotBlank() &&
                        (deliveryType == "pickup" || address.isNotBlank()) &&
                        deliveryTime.isNotBlank()
            ) {
                Text("Подтвердить заказ", fontSize = 18.sp)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Список товаров в корзине
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(cartItems) { item ->
                    CartItem(item = item, onRemoveClick = {})
                }
            }

            // Выбор способа получения
            Text(
                text = "Способ получения",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF003325)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                this@Column.DeliveryTypeButton(
                    text = "Самовывоз",
                    isSelected = deliveryType == "pickup",
                    onClick = {
                        deliveryType = "pickup"
                        deliveryTime = ""
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                this@Column.DeliveryTypeButton(
                    text = "Доставка",
                    isSelected = deliveryType == "delivery",
                    onClick = { deliveryType = "delivery" }
                )
            }

            // Контактная информация
            Text(
                text = "Контактные данные",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF003325)
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Фамилия") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = initialPhone,
                onValueChange = { },
                label = { Text("Номер телефона") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            // Адрес доставки (только для доставки)
            if (deliveryType == "delivery") {
                Text(
                    text = "Адрес доставки",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF003325)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = address,
                        onValueChange = {
                            address = it
                            isAddressDropdownExpanded = it.isNotBlank()
                        },
                        label = { Text("Улица, дом, квартира") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    DropdownMenu(
                        expanded = isAddressDropdownExpanded && filteredAddresses.isNotEmpty(),
                        onDismissRequest = { isAddressDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        filteredAddresses.forEach { suggestion ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = suggestion,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                onClick = {
                                    address = suggestion
                                    isAddressDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Выбор времени доставки
            Text(
                text = if (deliveryType == "delivery") "Время доставки" else "Время самовывоза",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF003325)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(deliveryTimes) { time ->
                    FilterChip(
                        selected = deliveryTime == time,
                        onClick = { deliveryTime = time },
                        label = { Text(time) },
                        modifier = Modifier.padding(4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF003325),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

private suspend fun sendOrderToTelegram(
    cartItems: List<CoffeeEntity>,
    firstName: String,
    lastName: String,
    phone: String,
    address: String,
    deliveryType: String,
    deliveryTime: String
) {
    val botToken = "8449218413:AAFdlg7YHR2syLI2fhH6l-s0JqyO4ot4VNA"  // Замените на токен вашего бота
    val chatId = "1079373879"

    // Формируем сообщение
    val itemsText = cartItems.joinToString("\n") { item ->
        "☕ ${item.name} (${item.type}) - ${item.priceM}"
    }

    val message = """
        🚀 *Новый заказ!*
        
        *Клиент:* $firstName $lastName
        *Телефон:* $phone
        *Способ получения:* ${if (deliveryType == "delivery") "Доставка" else "Самовывоз"}
        ${if (deliveryType == "delivery") "*Адрес:* $address" else ""}
        *Время:* $deliveryTime
        
        *Заказ:*
        $itemsText
        
        *Итого:* ${cartItems.sumOf { it.priceM.replace("₽", "").trim().toIntOrNull() ?: 0 }} ₽
    """.trimIndent()

    try {
        val url = URL("https://api.telegram.org/bot$botToken/sendMessage")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

        val postData = "chat_id=$chatId&text=${URLEncoder.encode(message, "UTF-8")}&parse_mode=Markdown"

        withContext(Dispatchers.IO) {
            val outputStream = connection.outputStream
            OutputStreamWriter(outputStream).use { writer ->
                writer.write(postData)
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("Telegram", "Заказ отправлен в Telegram")
            } else {
                Log.e("Telegram", "Ошибка: $responseCode")
            }
        }
    } catch (e: Exception) {
        Log.e("Telegram", "Ошибка отправки: ${e.message}")
    }
}


@Composable
fun ColumnScope.DeliveryTypeButton(  // Добавляем ColumnScope как приемник
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF003325) else Color.LightGray,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text)
    }
}
