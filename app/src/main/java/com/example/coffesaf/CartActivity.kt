package com.example.coffesaf

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffesaf.Database.CoffeeEntity
import com.example.coffesaf.ui.theme.CoffeSafTheme

class CartActivity : ComponentActivity() {
    private val cartViewModel: CartViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialItems =
            intent.getParcelableArrayListExtra<CoffeeEntity>("cartItems") ?: emptyList()
        initialItems.forEach { item ->
            cartViewModel.addToCart(item)
        }

        setContent {
            CoffeSafTheme {
                val phoneVerificationManager = remember { PhoneVerificationManager() }

                CartScreen(
                    cartItems = cartViewModel.cartItems,
                    onBackClick = { finish() },
                    onOrderClick = {
                        if (cartViewModel.cartItems.isNotEmpty()) {
                            val currentCartItems = ArrayList(cartViewModel.cartItems)
                            cartViewModel.updateLoyaltyCard()

                            // Просто показываем диалог без callback
                            phoneVerificationManager.showVerificationDialog()
                        }
                    },
                    onRemoveItem = { item ->
                        cartViewModel.removeFromCart(item)
                    }
                )

                // Выносим VerificationDialog наружу и передаем callback там
                phoneVerificationManager.VerificationDialog { verifiedPhone ->
                    val currentCartItems = ArrayList(cartViewModel.cartItems)
                    val intent = Intent(this@CartActivity, PersonalActivity::class.java).apply {
                        putExtra("phone", verifiedPhone)
                        putParcelableArrayListExtra(
                            "cartItems",
                            ArrayList(currentCartItems) as ArrayList<out Parcelable>
                        )
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CartScreen(
        cartItems: List<CoffeeEntity>,
        onBackClick: () -> Unit,
        onOrderClick: () -> Unit,
        onRemoveItem: (CoffeeEntity) -> Unit
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Корзина (${cartItems.size})",
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
                        containerColor = Color(0xFF003325),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                Button(
                    onClick = onOrderClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003325)
                    ),
                    enabled = cartItems.isNotEmpty()
                ) {
                    Text("Оформить заказ", fontSize = 18.sp)
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                if (cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Пустая корзина",
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Ваша корзина пуста",
                                color = Color.Gray,
                                fontSize = 18.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(cartItems) { item ->
                            SwipeToDismissItem(
                                item = item,
                                onDismiss = { onRemoveItem(item) }
                            )
                            Divider()
                        }
                    }

                    val totalPrice = cartItems.sumOf {
                        it.priceM.replace("₽", "").trim().toIntOrNull() ?: 0
                    }
                    Text(
                        text = "Итого: $totalPrice ₽",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SwipeToDismissItem(
        item: CoffeeEntity,
        onDismiss: () -> Unit
    ) {
        val dismissState = rememberDismissState(
            confirmValueChange = {
                when (it) {
                    // Исправлено на DismissedToStart для свайпа EndToStart
                    DismissValue.DismissedToStart -> {
                        onDismiss()
                        true
                    }

                    else -> false
                }
            }
        )

        SwipeToDismiss(
            state = dismissState,
            background = {
                val color by animateColorAsState(
                    when (dismissState.dismissDirection) {
                        DismissDirection.EndToStart -> Color.Red
                        else -> Color.Transparent
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Удалить",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            dismissContent = {
                CartItemContent(item = item)
            },
            directions = setOf(DismissDirection.EndToStart)
        )
    }

    @Composable
    fun CartItemContent(
        item: CoffeeEntity
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUri,
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.coffe_icon)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold)
                Text(item.type, color = Color.Gray)
                Text(item.priceM, color = Color(0xFF003325))
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewCartActivity() {
//    CoffeSafTheme {
//        CartScreen() { }
//    }
//}