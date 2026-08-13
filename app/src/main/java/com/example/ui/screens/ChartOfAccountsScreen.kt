package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.local.Account
import com.example.ui.viewmodels.StoreViewModel

private val Burgundy = Color(0xFF8B1C31)
private val PurpleAccent = Color(0xFF6B21A8) // Deep Purple for accents
private val LightPurple = Color(0xFFF3E8FF) // Very light purple for backgrounds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartOfAccountsScreen(viewModel: StoreViewModel, navController: NavController) {
    val accounts by viewModel.allAccounts.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("دليل الحسابات", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "رجوع", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Burgundy
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = PurpleAccent,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة حساب")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF9FAFB)) // Off-white/light gray
                    .padding(paddingValues)
            ) {
                if (accounts.isEmpty()) {
                    EmptyAccountsView(onAddClick = { showAddDialog = true })
                } else {
                    val groupedAccounts = accounts.groupBy { it.type }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val typesOrder = listOf("ASSET", "LIABILITY", "EQUITY", "REVENUE", "EXPENSE")
                        val typeNames = mapOf(
                            "ASSET" to "الأصول",
                            "LIABILITY" to "الخصوم (الالتزامات)",
                            "EQUITY" to "حقوق الملكية",
                            "REVENUE" to "الإيرادات",
                            "EXPENSE" to "المصروفات"
                        )

                        for (type in typesOrder) {
                            val accountsForType = groupedAccounts[type]
                            if (!accountsForType.isNullOrEmpty()) {
                                item {
                                    AccountGroupHeader(title = typeNames[type] ?: type)
                                }
                                items(accountsForType) { account ->
                                    AccountItemCard(account = account)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddAccountDialog(
                onDismiss = { showAddDialog = false },
                onSave = { code, name, type, initialBalance ->
                    viewModel.addAccount(code, name, type, null, initialBalance)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AccountGroupHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(PurpleAccent)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Burgundy
        )
    }
}

@Composable
fun AccountItemCard(account: Account) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(LightPurple, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = PurpleAccent
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = "رقم الحساب: ${account.code}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${account.currentBalance}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Burgundy
                )
                Text(
                    text = account.currencyCode,
                    fontSize = 12.sp,
                    color = PurpleAccent,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EmptyAccountsView(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountTree,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "دليل الحسابات فارغ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "قم بإضافة الحسابات المالية لتنظيم شجرتك المحاسبية",
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = Burgundy),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("إضافة حساب جديد", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountDialog(
    onDismiss: () -> Unit,
    onSave: (code: String, name: String, type: String, balance: Double) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("ASSET") }
    var balanceStr by remember { mutableStateOf("0.0") }

    val accountTypes = listOf(
        "ASSET" to "أصل (صندوق، بنك، عميل)",
        "LIABILITY" to "خصم (مورد، سلف)",
        "EQUITY" to "حقوق ملكية (رأس مال)",
        "REVENUE" to "إيراد (مبيعات)",
        "EXPENSE" to "مصروف (إيجار، رواتب)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "إضافة حساب جديد",
                fontWeight = FontWeight.Bold,
                color = Burgundy
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("رقم الحساب (الكود)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleAccent,
                        focusedLabelColor = PurpleAccent
                    )
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الحساب") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleAccent,
                        focusedLabelColor = PurpleAccent
                    )
                )

                Text("نوع الحساب:", fontWeight = FontWeight.Medium, color = Color.DarkGray)
                accountTypes.forEach { (typeKey, typeLabel) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { type = typeKey }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (type == typeKey),
                            onClick = { type = typeKey },
                            colors = RadioButtonDefaults.colors(selectedColor = PurpleAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(typeLabel)
                    }
                }

                OutlinedTextField(
                    value = balanceStr,
                    onValueChange = { balanceStr = it },
                    label = { Text("الرصيد الافتتاحي") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleAccent,
                        focusedLabelColor = PurpleAccent
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bal = balanceStr.toDoubleOrNull() ?: 0.0
                    if (code.isNotBlank() && name.isNotBlank()) {
                        onSave(code, name, type, bal)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Burgundy)
            ) {
                Text("حفظ", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color.Gray)
            }
        },
        containerColor = Color.White
    )
}
