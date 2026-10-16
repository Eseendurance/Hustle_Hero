package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DailyLifeAndMealData
import com.example.model.CurrencyFormatter
import com.example.model.GameState
import com.example.model.MealCuisine
import com.example.model.MealItem
import com.example.ui.HustleHeroViewModel
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceSubtle
import com.example.ui.theme.HustleGold
import com.example.ui.theme.HustleGoldContainer
import com.example.ui.theme.HustleGoldDark
import com.example.ui.theme.HustleGreen
import com.example.ui.theme.HustleGreenContainer
import com.example.ui.theme.HustleGreenDark
import com.example.ui.theme.PositiveGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GourmetMealsScreen(
    state: GameState,
    viewModel: HustleHeroViewModel,
    modifier: Modifier = Modifier
) {
    val toastMessage by viewModel.routineActionToast.collectAsState()
    val allMeals = remember { DailyLifeAndMealData.getAllMeals() }
    var selectedCuisine by remember { mutableStateOf(MealCuisine.NIGERIAN) }

    // Dialog state for Cooking recipe or Takeout delivery
    var cookingModalMeal by remember { mutableStateOf<MealItem?>(null) }
    var takeoutModalMeal by remember { mutableStateOf<MealItem?>(null) }

    val filteredMeals = allMeals.filter { it.cuisine == selectedCuisine }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("gourmet_meals_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header with Takeout Banner
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(modifier = Modifier.height(130.dp).fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_gourmet_takeout),
                            contentDescription = "Gourmet Food Ordering",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.45f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "GOURMET DINING & KITCHEN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = HustleGold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Cook at Home or Order Takeout",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Personal Cash Available", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = CurrencyFormatter.formatNaira(state.personalCash),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HustleGreen
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardSurfaceSubtle
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Hunger: ", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = "${state.dailyRoutine.hungerLevel}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.dailyRoutine.hungerLevel > 50) Color(0xFFDC2626) else PositiveGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Toast Feedback
        if (toastMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = HustleGoldContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HustleGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = toastMessage ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HustleGoldDark,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "DISMISS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HustleGreenDark,
                            modifier = Modifier.clickable { viewModel.clearToast() }
                        )
                    }
                }
            }
        }

        // Horizontal Cuisine Selector Chips
        item {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MealCuisine.entries.forEach { cuisine ->
                    val isSelected = cuisine == selectedCuisine
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) HustleGreen else CardSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) HustleGreen else BorderSubtle),
                        modifier = Modifier
                            .clickable { selectedCuisine = cuisine }
                            .testTag("cuisine_tab_${cuisine.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = cuisine.iconEmoji, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = cuisine.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PureWhite else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Cuisine Title Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${selectedCuisine.iconEmoji} ${selectedCuisine.displayName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Region: ${selectedCuisine.region} • ${filteredMeals.size} Specialties Available",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // Meal Cards List
        items(filteredMeals, key = { it.id }) { meal ->
            MealCard(
                meal = meal,
                userCash = state.personalCash,
                onCookClick = { cookingModalMeal = meal },
                onOrderTakeoutClick = { takeoutModalMeal = meal }
            )
        }

        // Meal History Audit
        if (state.mealHistory.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "📜 Recent Dining History",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                state.mealHistory.take(5).forEach { record ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CardSurfaceSubtle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (record.wasCooked) "👨🏽‍🍳" else "🛵",
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = record.mealName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${if (record.wasCooked) "Cooked Homemade" else "Takeout Delivery"} • Day ${record.dayNumber} (${record.timeLabel})",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Text(
                                text = CurrencyFormatter.formatNaira(record.costPaid),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HustleGreen
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal: COOK YOURSELF (Ingredients checklist & Step-by-step instructions)
    if (cookingModalMeal != null) {
        val meal = cookingModalMeal!!
        ModalBottomSheet(
            onDismissRequest = { cookingModalMeal = null },
            containerColor = CardSurface
        ) {
            LazyColumn(modifier = Modifier.padding(20.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = meal.iconEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "COOKING: ${meal.name}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Authentic Homemade Preparation",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Cost & Time Bar
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = HustleGreenContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Timer, contentDescription = "Time", tint = HustleGreenDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${meal.cookingTimeMinutes} mins prep", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HustleGreenDark)
                            }
                            Text(
                                text = "Groceries Cost: ${CurrencyFormatter.formatNaira(meal.cookingCost)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HustleGreenDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Essential Ingredients Checklist
                    Text(
                        text = "🛒 ESSENTIAL INGREDIENTS LIST",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HustleGoldDark,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    meal.ingredients.forEach { ingredient ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PositiveGreen.copy(alpha = 0.2f),
                                modifier = Modifier.size(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Item", tint = PositiveGreen, modifier = Modifier.size(12.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = ingredient, fontSize = 12.sp, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Step-by-Step Cooking Instructions
                    Text(
                        text = "👩🏽‍🍳 STEP-BY-STEP COOKING INSTRUCTIONS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HustleGreenDark,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    meal.cookingInstructions.forEachIndexed { index, step ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardSurfaceSubtle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(modifier = Modifier.padding(10.dp)) {
                                Surface(
                                    shape = CircleShape,
                                    color = HustleGoldDark,
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = step, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.cookMeal(meal)
                            cookingModalMeal = null
                        },
                        modifier = Modifier.fillMaxWidth().testTag("confirm_cook_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
                    ) {
                        Text(
                            text = "Cook & Feast (Spend ${CurrencyFormatter.formatNaira(meal.cookingCost)})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Modal: ORDER TAKEOUT (Exact Image, delivery presentation, courier tracking)
    if (takeoutModalMeal != null) {
        val meal = takeoutModalMeal!!
        ModalBottomSheet(
            onDismissRequest = { takeoutModalMeal = null },
            containerColor = CardSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Exact Takeout Graphic Presentation Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_gourmet_takeout),
                        contentDescription = "Takeout Delivery of ${meal.name}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "🛵 15-20 MINS EXPRESS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = HustleGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "${meal.iconEmoji} ${meal.name}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = meal.flavorProfile,
                            fontSize = 11.sp,
                            color = PureWhite.copy(alpha = 0.9f),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Delivery Receipt Breakdown
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardSurfaceSubtle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Item Price", fontSize = 12.sp, color = TextSecondary)
                            Text(text = CurrencyFormatter.formatNaira(meal.takeoutPrice - 500L), fontSize = 12.sp, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Courier & Thermal Packaging", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "₦500", fontSize = 12.sp, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderSubtle))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Delivery Cost", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = CurrencyFormatter.formatNaira(meal.takeoutPrice), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = HustleGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rider & satisfaction note
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = HustleGreenContainer, modifier = Modifier.size(32.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🛵", fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Dispatched via SwiftEats Courier", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Arrives piping hot at your door. Zero kitchen cleanup!", fontSize = 10.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        viewModel.orderTakeoutMeal(meal)
                        takeoutModalMeal = null
                    },
                    modifier = Modifier.fillMaxWidth().testTag("confirm_order_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HustleGoldDark)
                ) {
                    Icon(imageVector = Icons.Default.DeliveryDining, contentDescription = "Order")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirm Order (${CurrencyFormatter.formatNaira(meal.takeoutPrice)})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MealCard(
    meal: MealItem,
    userCash: Long,
    onCookClick: () -> Unit,
    onOrderTakeoutClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("meal_card_${meal.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = meal.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = meal.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = meal.tagline,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                // Flavor Variant or Size pill if cake/ice cream
                if (meal.cakeSizeOption != null || meal.flavourVariant != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HustleGoldContainer
                    ) {
                        Text(
                            text = meal.cakeSizeOption ?: meal.flavourVariant ?: "",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = HustleGoldDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = meal.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Vital gains strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CardSurfaceSubtle
                ) {
                    Text(
                        text = "⚡ +${meal.energyBoost}% Energy",
                        fontSize = 10.sp,
                        color = HustleGoldDark,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CardSurfaceSubtle
                ) {
                    Text(
                        text = "😌 -${meal.stressRelief}% Stress",
                        fontSize = 10.sp,
                        color = PositiveGreen,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Two Actions: Cook Yourself vs Order Takeout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCookClick,
                    modifier = Modifier.weight(1f).testTag("cook_button_${meal.id}"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "👨🏽‍🍳 Cook Yourself", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = CurrencyFormatter.formatNaira(meal.cookingCost),
                            fontSize = 10.sp,
                            color = HustleGreen
                        )
                    }
                }

                Button(
                    onClick = onOrderTakeoutClick,
                    modifier = Modifier.weight(1f).testTag("takeout_button_${meal.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HustleGreen)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🛵 Order Takeout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = CurrencyFormatter.formatNaira(meal.takeoutPrice),
                            fontSize = 10.sp,
                            color = PureWhite
                        )
                    }
                }
            }
        }
    }
}
