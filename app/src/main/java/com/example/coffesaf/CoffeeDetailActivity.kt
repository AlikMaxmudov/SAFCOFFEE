package com.example.coffesaf

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffesaf.Database.CoffeeEntity
import com.example.coffesaf.ui.theme.CoffeSafTheme
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.coffesaf.ui.theme.CoffeSafTheme

class CoffeeDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val coffee = intent.getParcelableExtra<CoffeeEntity>("coffee")
        setContent {
            CoffeSafTheme {
                val context = LocalContext.current
                val cartViewModel: CartViewModel = viewModel()
                val phoneVerificationManager = remember { PhoneVerificationManager() }
                var selectedSize by remember { mutableStateOf("M") }

                // Обработчик успешного подтверждения
                val onOrderConfirmed = { phone: String ->
                    // Добавляем выбранный кофе в корзину с правильным размером
                    val coffeeToAdd = coffee?.copy()?.apply {
                        priceM = when (selectedSize) {
                            "S" -> coffee.priceS
                            "M" -> coffee.priceM
                            "L" -> coffee.priceL
                            else -> coffee.priceM
                        }
                    }

                    coffeeToAdd?.let { cartViewModel.addToCart(it) }

                    // Переходим к оформлению заказа
                    val intent = Intent(context, PersonalActivity::class.java).apply {
                        putExtra("phone", phone)
                        putParcelableArrayListExtra(
                            "cartItems",
                            ArrayList(cartViewModel.cartItems) as ArrayList<out Parcelable>
                        )
                    }
                    context.startActivity(intent)
                }

                phoneVerificationManager.VerificationDialog(
                    onSuccess = onOrderConfirmed
                )

                CoffeeDetailScreen(
                    coffee = coffee ?: return@CoffeSafTheme,
                    onBackClick = { finish() },
                    onFavoriteClick = {},
                    onBuyClick = {
                        // Сохраняем выбранный размер перед показом диалога
                        selectedSize = selectedSize
                        phoneVerificationManager.showVerificationDialog()
                    },
                    selectedSize = selectedSize,
                    onSizeSelected = { size -> selectedSize = size }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeDetailScreen(
    coffee: CoffeeEntity,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onBuyClick: () -> Unit,
    selectedSize: String,
    onSizeSelected: (String) -> Unit
) {
    var selectedSize by remember { mutableStateOf("M") }
    var isExpanded by remember { mutableStateOf(false) }
    val maxLines = if (isExpanded) Int.MAX_VALUE else 3
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Добавляем Spacer для создания пространства
                    Spacer(modifier = Modifier.weight(1.5f))
                    // Текст "Элементы" с улучшенным стилем
                    Text(
                        text = "Элементы",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFA7F3D0), // светлый оттенок зеленого
                                    Color(0xFF059669)  // темный оттенок зеленого
                                )
                            )
                        )
                    )
                    // Добавляем еще один Spacer для создания пространства
                    Spacer(modifier = Modifier.weight(1f))
                    // Кнопка "Favorite"
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.heart_menubar),
                            contentDescription = "Favorite",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003325),
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White
                )
            )
        },
                bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = when (selectedSize) {
                            "S" -> coffee.priceS
                            "M" -> coffee.priceM
                            "L" -> coffee.priceL
                            else -> coffee.priceM
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003325)
                    )

                    Button(
                        onClick = onBuyClick,
                        modifier = Modifier
                            .width(200.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF003325)
                        )
                    ) {
                        Text("Купить", fontSize = 18.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Coffee image
            AsyncImage(
                model = coffee.imageUri,
                contentDescription = coffee.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.coffe_icon)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Name and rating
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = coffee.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003325)
                    )
                    Text(
                        text = coffee.type,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.star_filled),
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%.1f".format(coffee.rating),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Описание",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003325)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = coffee.description,
                    fontSize = 16.sp,
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis
                )

                if (coffee.description.count { it == '\n' } > 2 || coffee.description.length > 150) {
                    OutlinedButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .height(36.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF388E3C) // Зеленый цвет
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp
                        )
                    ) {
                        Text(
                            text = if (isExpanded) "Свернуть" else "Читать далее",
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ингредиенты ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003325)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = coffee.ingredients,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Size selection
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Размер",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003325)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SizeButton(
                        size = "S",
                        isSelected = selectedSize == "S",
                        onClick = { onSizeSelected("S") }
                    )

                    SizeButton(
                        size = "M",
                        isSelected = selectedSize == "M",
                        onClick = { onSizeSelected("M") }
                    )

                    SizeButton(
                        size = "L",
                        isSelected = selectedSize == "L",
                        onClick = { onSizeSelected("L") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SizeButton(
    size: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(100.dp)
            .height(48.dp), // Увеличиваем высоту для лучшей кликабельности
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF003325) else Color(0xFFF5F5F5),
            contentColor = if (isSelected) Color.White else Color(0xFF003325)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Text(
            text = size,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewCoffeeDetailScreen() {
//    CoffeSafTheme {
//        CoffeeDetailScreen(
//            coffee = CoffeeEntity(
//                id = 1,
//                category = "",
//                name = "Капучино",
//                type = "Кофе с молоком",
//                description = "Капучино - это итальянский кофе, состоящий из равных частей эспрессо, горячего молока и молочной пены.",
//                ingredients = "Эспрессо, Молоко, Молочная пена",
//                priceS = "150 ₽",
//                priceM = "200 ₽",
//                priceL = "250 ₽",
//                rating = 4.5f,
//                imageUri = "" // Замените на ваш ресурс изображения
//            ),
//            onBackClick = {},
//            onFavoriteClick = {},
//            onBuyClick = {}
//        )
//    }
//}