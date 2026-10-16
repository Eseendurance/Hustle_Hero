package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.logic.AjoSavingsManager
import com.example.model.AjoSavingGoal
import com.example.model.CurrencyFormatter
import com.example.model.GameState
import com.example.model.WalletSource
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceSubtle
import com.example.ui.theme.DangerRed
import com.example.ui.theme.HustleGold
import com.example.ui.theme.HustleGoldContainer
import com.example.ui.theme.HustleGoldDark
import com.example.ui.theme.HustleGreen
import com.example.ui.theme.HustleGreenContainer
import com.example.ui.theme.HustleGreenDark
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.max
import kotlin.math.min

/**
 * AjoGoalContributionBottomSheet
 *
 * Full responsive Material 3 modal bottom sheet allowing users to:
 * 1. View and select an Ajo saving goal (or create a custom one)
 * 2. Select the funding balance (Personal Cash, Savings, or Business Capital)
 * 3. Input custom deposit amount with real-time balance validation and quick chips
 * 4. Execute the contribution directly through the responsive backend
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjoGoalContributionBottomSheet(
    state: GameState,
    onDismiss: () -> Unit,
    onContribute: (goalId: String, amount: Long, source: WalletSource) -> Unit,
    onCreateCustomGoal: ((title: String, description: String, category: String, emoji: String, target: Long, weeklyTarget: Long, perk: String) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current

    // Active selection state
    val initialGoalId = state.selectedAjoGoalId ?: state.ajoGoals.firstOrNull()?.id ?: ""
    var selectedGoalId by remember { mutableStateOf(initialGoalId) }
    var selectedSource by remember { mutableStateOf(WalletSource.PERSONAL_CASH) }
    var inputAmountText by remember { mutableStateOf("10000") }
    var showCustomGoalDialog by remember { mutableStateOf(false) }

    // Custom Goal Form state
    var customTitle by remember { mutableStateOf("") }
    var customTargetText by remember { mutableStateOf("100000") }
    var customCategory by remember { mutableStateOf("Business Growth") }
    var customEmoji by remember { mutableStateOf("🎯") }
    var customPerk by remember { mutableStateOf("+10 Business Reputation") }

    // Locate current selected goal
    val currentGoal = state.ajoGoals.find { it.id == selectedGoalId } ?: state.ajoGoals.firstOrNull()
    val availableBalance = AjoSavingsManager.getWalletBalance(state, selectedSource)
    val parsedAmount = inputAmountText.toLongOrNull() ?: 0L

    // Real-time backend validation check
    val validation = if (currentGoal != null) {
        AjoSavingsManager.validateContribution(state, currentGoal.id, parsedAmount, selectedSource)
    } else null

    val isInputValid = validation?.isValid == true && parsedAmount > 0L
    val remainingToGoal = currentGoal?.remainingAmount ?: 0L
    val willCompleteGoal = currentGoal != null && !currentGoal.isCompleted && (parsedAmount >= remainingToGoal)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardSurface,
        tonalElevation = 6.dp,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 44.dp, height = 4.dp),
                shape = RoundedCornerShape(2.dp),
                color = BorderSubtle
            ) {}
        },
        modifier = Modifier.testTag("ajo_goal_contribution_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(HustleGoldContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎯", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Ajo Saving Goal Deposit",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "West African Rotating & Targeted Wealth Circle",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp).testTag("close_ajo_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 1: GOAL SELECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1. SELECT AJO SAVING GOAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HustleGoldDark,
                    letterSpacing = 0.5.sp
                )

                if (onCreateCustomGoal != null) {
                    Text(
                        text = if (showCustomGoalDialog) "Hide Custom" else "+ Custom Goal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HustleGreenDark,
                        modifier = Modifier
                            .clickable { showCustomGoalDialog = !showCustomGoalDialog }
                            .padding(4.dp)
                            .testTag("toggle_custom_goal_button")
                    )
                }
            }

            // Inline Custom Goal Form
            AnimatedVisibility(visible = showCustomGoalDialog) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardSurfaceSubtle,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HustleGreen.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Create New Custom Ajo Target",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HustleGreenDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customTitle,
                            onValueChange = { customTitle = it },
                            label = { Text("Goal Title (e.g. New POS Stand)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("custom_goal_title_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HustleGreen,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = customTargetText,
                                onValueChange = { customTargetText = it.filter { char -> char.isDigit() } },
                                label = { Text("Target ₦", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1.2f).testTag("custom_goal_target_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = HustleGreen,
                                    unfocusedBorderColor = BorderSubtle
                                )
                            )

                            OutlinedTextField(
                                value = customEmoji,
                                onValueChange = { customEmoji = it.take(2) },
                                label = { Text("Emoji", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(0.8f).testTag("custom_goal_emoji_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = HustleGreen,
                                    unfocusedBorderColor = BorderSubtle
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val target = customTargetText.toLongOrNull() ?: 100_000L
                                if (customTitle.isNotBlank()) {
                                    onCreateCustomGoal?.invoke(
                                        customTitle,
                                        "Personal Goal: $customTitle",
                                        customCategory,
                                        customEmoji,
                                        target,
                                        target / 5,
                                        customPerk
                                    )
                                    showCustomGoalDialog = false
                                    customTitle = ""
                                }
                            },
                            enabled = customTitle.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = HustleGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("create_custom_goal_submit")
                        ) {
                            Text("Save Target to Ajo Circle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Carousel / List of Goals
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.ajoGoals) { goal ->
                    val isSelected = goal.id == selectedGoalId
                    GoalCardItem(
                        goal = goal,
                        isSelected = isSelected,
                        onClick = {
                            selectedGoalId = goal.id
                            // Auto-set helpful preset if goal has remaining amount
                            if (goal.remainingAmount > 0L) {
                                val suggested = min(goal.recommendedWeekly, goal.remainingAmount)
                                inputAmountText = suggested.toString()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 2: FUNDING WALLET SELECTION
            Text(
                text = "2. CHOOSE SOURCE ACCOUNT",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HustleGoldDark,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WalletSourceCard(
                    modifier = Modifier.weight(1f),
                    title = "Personal Cash",
                    balance = state.personalCash,
                    icon = Icons.Default.MonetizationOn,
                    isSelected = selectedSource == WalletSource.PERSONAL_CASH,
                    activeColor = HustleGreen,
                    onClick = { selectedSource = WalletSource.PERSONAL_CASH }
                )

                WalletSourceCard(
                    modifier = Modifier.weight(1f),
                    title = "Emergency Savings",
                    balance = state.personalSavings,
                    icon = Icons.Default.AccountBalance,
                    isSelected = selectedSource == WalletSource.PERSONAL_SAVINGS,
                    activeColor = HustleGold,
                    onClick = { selectedSource = WalletSource.PERSONAL_SAVINGS }
                )

                WalletSourceCard(
                    modifier = Modifier.weight(1f),
                    title = "Business Capital",
                    balance = state.businessCash,
                    icon = Icons.Default.AccountBalanceWallet,
                    isSelected = selectedSource == WalletSource.BUSINESS_CASH,
                    activeColor = Color(0xFF3B82F6),
                    onClick = { selectedSource = WalletSource.BUSINESS_CASH }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 3: AMOUNT INPUT
            Text(
                text = "3. CONTRIBUTION AMOUNT",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HustleGoldDark,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = inputAmountText,
                onValueChange = { newText ->
                    inputAmountText = newText.filter { it.isDigit() }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ajo_contribution_amount_input"),
                prefix = {
                    Text(
                        text = "₦ ",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                },
                trailingIcon = {
                    if (inputAmountText.isNotEmpty()) {
                        IconButton(onClick = { inputAmountText = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isInputValid) HustleGreen else DangerRed,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = CardSurfaceSubtle,
                    unfocusedContainerColor = CardSurfaceSubtle
                )
            )

            // Preset Amount Chips
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(5_000L, 10_000L, 25_000L, 50_000L).forEach { preset ->
                    PresetChip(
                        label = "+${CurrencyFormatter.formatCompact(preset)}",
                        onClick = {
                            val cur = inputAmountText.toLongOrNull() ?: 0L
                            inputAmountText = (cur + preset).toString()
                        }
                    )
                }

                // Fill Remainder Chip
                if (remainingToGoal > 0L) {
                    PresetChip(
                        label = "🎯 Exact Remainder",
                        highlight = true,
                        onClick = {
                            inputAmountText = remainingToGoal.toString()
                        }
                    )
                }

                // All Available Balance Chip
                if (availableBalance > 0L) {
                    PresetChip(
                        label = "💰 Max",
                        onClick = {
                            inputAmountText = availableBalance.toString()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real-time Dynamic Feedback & Validation
            if (validation != null) {
                if (!validation.isValid) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DangerRed.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚠️", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = validation.errorMessage ?: "Invalid contribution amount.",
                                fontSize = 11.sp,
                                color = DangerRed,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    // Valid State Overview
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (willCompleteGoal) HustleGoldContainer else HustleGreenContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (willCompleteGoal) HustleGold else HustleGreen.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (willCompleteGoal) Icons.Default.Star else Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (willCompleteGoal) HustleGoldDark else HustleGreenDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (willCompleteGoal) "🎉 COMPLETION DEPOSIT!" else "VALID DEPOSIT",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (willCompleteGoal) HustleGoldDark else HustleGreenDark
                                    )
                                }

                                Text(
                                    text = "Balance After: ${CurrencyFormatter.formatNaira(max(0L, availableBalance - parsedAmount))}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            if (currentGoal != null) {
                                val nextSaved = currentGoal.currentAmount + parsedAmount
                                val nextPercent = if (currentGoal.targetAmount > 0) {
                                    ((nextSaved.toFloat() / currentGoal.targetAmount.toFloat()) * 100).toInt()
                                } else 100

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Progress: ${((currentGoal.progressPercent) * 100).toInt()}% ➔ $nextPercent% (${CurrencyFormatter.formatNaira(nextSaved)} of ${CurrencyFormatter.formatNaira(currentGoal.targetAmount)})",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )

                                if (willCompleteGoal) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Unlocked Perk: ${currentGoal.perkReward}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HustleGoldDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.weight(0.8f).height(48.dp)
                ) {
                    Text("Cancel", color = TextSecondary, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        if (currentGoal != null && isInputValid) {
                            onContribute(currentGoal.id, parsedAmount, selectedSource)
                            onDismiss()
                        }
                    },
                    enabled = isInputValid && currentGoal != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (willCompleteGoal) HustleGold else HustleGreen,
                        disabledContainerColor = CardSurfaceSubtle
                    ),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp)
                        .testTag("ajo_confirm_deposit_button")
                ) {
                    Text(
                        text = if (isInputValid) "Deposit ${CurrencyFormatter.formatNaira(parsedAmount)}" else "Enter Amount",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = if (isInputValid) PureWhite else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GoalCardItem(
    goal: AjoSavingGoal,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(220.dp)
            .clickable(onClick = onClick)
            .testTag("ajo_goal_card_${goal.id}"),
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) HustleGoldContainer.copy(alpha = 0.5f) else CardSurfaceSubtle,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) HustleGold else BorderSubtle
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = goal.iconEmoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = goal.category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = HustleGoldDark
                    )
                }

                if (goal.isCompleted) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = HustleGreenContainer
                    ) {
                        Text(
                            text = "DONE ✅",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = HustleGreenDark,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = HustleGoldContainer
                    ) {
                        Text(
                            text = "⏳ Wk ${goal.deadlineWeek}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = HustleGoldDark,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = goal.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { goal.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (goal.isCompleted) HustleGreen else HustleGold,
                trackColor = BorderSubtle
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${((goal.progressPercent) * 100).toInt()}% saved",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (goal.isCompleted) HustleGreenDark else HustleGoldDark
                )
                Text(
                    text = CurrencyFormatter.formatNaira(goal.targetAmount),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Text(
                text = if (goal.remainingAmount > 0) "${CurrencyFormatter.formatNaira(goal.remainingAmount)} to go" else "Target met!",
                fontSize = 9.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "⭐ ${goal.perkReward}",
                fontSize = 8.5.sp,
                color = HustleGoldDark,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun WalletSourceCard(
    modifier: Modifier = Modifier,
    title: String,
    balance: Long,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("wallet_source_${title.replace(" ", "_")}"),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) activeColor.copy(alpha = 0.12f) else CardSurfaceSubtle,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) activeColor else BorderSubtle
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) activeColor else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) TextPrimary else TextSecondary,
                maxLines = 1
            )
            Text(
                text = CurrencyFormatter.formatCompact(balance),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (highlight) HustleGoldContainer else CardSurfaceSubtle,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (highlight) HustleGold else BorderSubtle
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) HustleGoldDark else TextPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}
