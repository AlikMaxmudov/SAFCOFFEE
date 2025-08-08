package com.example.coffesaf

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.coffesaf.CartState.cartItems
import com.example.coffesaf.Database.CoffeeDatabase
import com.example.coffesaf.Database.CoffeeEntity
import com.example.coffesaf.Database.CoffeeRepository
import com.example.coffesaf.Database.CoffeeViewModel
import com.example.coffesaf.Database.CoffeeViewModelFactory
import com.example.coffesaf.ui.theme.CoffeSafTheme


class MainActivity : ComponentActivity() {
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoffeSafTheme {
                val viewModel: CoffeeViewModel = viewModel(
                    factory = CoffeeViewModelFactory(
                        CoffeeRepository(
                            CoffeeDatabase.getDatabase(this).coffeeDao()
                        )
                    )
                )
                MainScreen(viewModel, cartViewModel)
            }
        }
    }
}

sealed class BottomNavItem(
    val title: String,
    @DrawableRes val iconRes: Int,
    val route: String
) {
    object Home : BottomNavItem("", R.drawable.home_menubar, "home")
    object Cart : BottomNavItem("", R.drawable.coffee_icon_menubar, "cart")
    object Favorites : BottomNavItem("", R.drawable.heart_menubar, "favorites")
    object Profile : BottomNavItem("", R.drawable.bell_menubar, "profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: CoffeeViewModel,
    cartViewModel: CartViewModel,
) {
    val searchText = remember { mutableStateOf(TextFieldValue("")) }
    val screenHeightPercent = 0.20f
    var selectedItem by remember { mutableStateOf(0) }
    val cartItemCount by remember { derivedStateOf { cartViewModel.cartItems.size } }
    val loyaltyCards = CartState.loyaltyCards



    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Cart,
        BottomNavItem.Favorites,
        BottomNavItem.Profile
    )

    val categories by viewModel.categories
    val coffeeItems by viewModel.coffeeItems
    var selectedCoffeeType by remember { mutableStateOf("") }
    val context = LocalContext.current
    val intent = Intent(context, CartActivity::class.java)



    LaunchedEffect(Unit) {
        if (categories.isNotEmpty() && selectedCoffeeType.isEmpty()) {
            selectedCoffeeType = categories.first()
        }
    }

    LaunchedEffect(selectedCoffeeType) {
        if (selectedCoffeeType.isNotEmpty()) {
            viewModel.selectCategory(selectedCoffeeType)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top panel with search
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(screenHeightPercent)
                .background(Color(0xFF003325))
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menu_icon),
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )

                Column {
                    Text(
                        text = "SAF COFFEE",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Home",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }

            Icon(
                painter = painterResource(id = R.drawable.table_user_icon),
                contentDescription = "User",
                tint = Color.White,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .clickable {
                        val intent = Intent(context, PersonalActivity::class.java)
                        context.startActivity(intent)
                    }
            )

            TextField(
                value = searchText.value,
                onValueChange = { searchText.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 70.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.8f)),
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Поиск",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                },
                placeholder = {
                    Text(
                        "Поиск",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Заголовок с иконкой
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Карта SAF",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003325)
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, Color(0xFF003325).copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Прогресс-бар
                    LinearProgressIndicator(
                        progress = cartViewModel.loyaltyCards.count { it.isFilled } / 8f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF003325),
                        trackColor = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Лоялка
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val itemWidth = maxWidth / 8

                        Canvas(modifier = Modifier.fillMaxWidth()) {
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(0f, 20f),
                                end = Offset(size.width, 20f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            cartViewModel.loyaltyCards.forEachIndexed { index, card ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (index < 7) {
                                        Canvas(
                                            modifier = Modifier
                                                .width(itemWidth)
                                                .height(1.dp)
                                        ) {
                                            drawLine(
                                                color = if (card.isFilled) Color(0xFF4CAF50) else Color.LightGray,
                                                start = Offset(size.width / 2, 0f),
                                                end = Offset(size.width, 0f),
                                                strokeWidth = 2.dp.toPx()
                                            )
                                        }
                                    }

                                    LoyaltyCardItem(
                                        isFilled = card.isFilled,
                                        modifier = Modifier.size(40.dp)
                                    )

                                    Text(
                                        text = "${index + 1}",
                                        color = if (card.isFilled) Color(0xFF003325) else Color.Gray,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Собери 8 чашек кофе и получи бонус!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            LazyRow(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CoffeeTypeChip(
                        type = category,
                        isSelected = category == selectedCoffeeType,
                        onSelect = {
                            selectedCoffeeType = category
                            viewModel.selectCategory(category)
                        }
                    )
                }
            }

            if (coffeeItems.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(coffeeItems) { item ->
                        CoffeeGridItem(
                            item = item,
                            cartItems = cartViewModel.cartItems,
                            onAddToCart = { cartViewModel.addToCart(item) },
                            onClick = {
                                val intent = Intent(context, CoffeeDetailActivity::class.java)
                                intent.putExtra("coffee", item)
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Кофе не найден", color = Color.Gray)
                }
            }
        }

        // боттом навигейт
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            NavigationBar(
                modifier = Modifier.height(60.dp),
                containerColor = Color.White,
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Box {
                                Icon(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = item.title,
                                    tint = if (selectedItem == index) Color.White else Color(
                                        0xFF003325
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )
                                if (cartViewModel.cartItems.isNotEmpty() && item == BottomNavItem.Cart) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .align(Alignment.TopEnd)
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cartViewModel.cartItems.size.toString(),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        },
                        label = {
                            Text(
                                item.title,
                                color = if (selectedItem == index) Color.White else Color(0xFF003325)
                                )
                        },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            if (item == BottomNavItem.Cart) {
                                if (cartItems.isNotEmpty()) {
                                    val intent = Intent(context, CartActivity::class.java)
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "Корзина пуста", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color(0xFF003325),
                            unselectedTextColor = Color(0xFF003325),
                            indicatorColor = Color(0xFF003325)
                        ),
                        alwaysShowLabel = true
                    )
                }
            }
        }
    }
}



@Composable
fun CoffeeTypeChip(
    type: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() },
        color = if (isSelected) Color(0xFF003325) else Color.LightGray
    ) {
        Text(
            text = type,
            color = if (isSelected) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun CoffeeGridItem(
    item: CoffeeEntity,
    onAddToCart: () -> Unit,
    cartItems: List<CoffeeEntity>,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val countInCart = cartItems.count { it.id == item.id }


    if (countInCart > 0) {
        Text(
            text = "$countInCart",
            modifier = Modifier
                .background(Color(0xFF003325), CircleShape)
                .padding(4.dp),
            color = Color.White
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.imageUri,
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(1.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.coffe_icon),
                placeholder = painterResource(R.drawable.ic_launcher_foreground)
            )

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(
                        color = Color(0x80000000),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.star_filled),
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%.1f".format(item.rating),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = item.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.priceM,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            onAddToCart()
                            Toast.makeText(
                                context,
                                "${item.name} добавлен в корзину",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF003325), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Добавить в корзину",
                            tint = Color.White
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun LoyaltyCardItem(
    isFilled: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isFilled) Color(0xFF4CAF50) else Color.LightGray,
        animationSpec = tween(durationMillis = 500)
    )

    val borderColor by animateColorAsState(
        targetValue = if (isFilled) Color(0xFF388E3C) else Color.Gray,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.coffee_icon_menubar),
            contentDescription = "Coffee stamp",
            tint = if (isFilled) Color.White else Color.DarkGray,
            modifier = Modifier.size(20.dp)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun MainScreenDatabasePreview() {
    CoffeSafTheme {
        val context = LocalContext.current
        val viewModel: CoffeeViewModel = viewModel(
            factory = CoffeeViewModelFactory(
                CoffeeRepository(
                    CoffeeDatabase.getDatabase(context).coffeeDao()
                )
            )
        )
        MainScreen(viewModel, CartViewModel())
    }
}