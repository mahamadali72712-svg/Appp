package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.data.local.CurrencyRate
import com.example.ui.theme.ThemePresets
import com.example.ui.theme.getThemeOption
import com.example.ui.viewmodels.StoreViewModel

// ---------------------------------------------------
// 1. FULL PAGE: LANGUAGE & APPEARANCE (اللغة ومظهر التطبيق)
// ---------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    viewModel: StoreViewModel,
    navController: NavHostController
) {
    val selectedThemeId by viewModel.selectedThemeId.collectAsStateWithLifecycle()
    val currentTheme = getThemeOption(selectedThemeId)

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text("اللغة ومظهر التطبيق", fontWeight = FontWeight.Bold, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "اختر الثيم اللوني المفضل للتطبيق (12 ثيم مميز):",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(520.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ThemePresets) { theme ->
                        val isSelected = theme.id == selectedThemeId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(115.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) theme.primaryColor else Color(0x33FFFFFF),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.setAppTheme(theme.id) },
                            colors = CardDefaults.cardColors(containerColor = Color(0x331F2937))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(theme.gradientBrush)
                                    .padding(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(theme.primaryColor)
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "محدد",
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = theme.name,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x40111827)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0x33FFFFFF))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = currentTheme.primaryColor)
                                Text("لغة التطبيق", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Text("العربية (افتراضي)", color = currentTheme.primaryColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------
// 2. FULL PAGE: CURRENCY RATES & CONVERTER (أسعار الصرف والعملات)
// ---------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrenciesScreen(
    viewModel: StoreViewModel,
    navController: NavHostController
) {
    val selectedThemeId by viewModel.selectedThemeId.collectAsStateWithLifecycle()
    val currentTheme = getThemeOption(selectedThemeId)
    val currencies by viewModel.allCurrencies.collectAsStateWithLifecycle()

    var calcAmountStr by remember { mutableStateOf("1000") }
    var calcFromCode by remember { mutableStateOf("SAR") }
    var calcToCode by remember { mutableStateOf("YER") }

    var showEditDialog by remember { mutableStateOf<CurrencyRate?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("أسعار الصرف والعملات", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة عملة", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calculator Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x401F2937)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, currentTheme.primaryColor.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "حاسبة تحويل العملات الفورية",
                            color = currentTheme.primaryColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = calcAmountStr,
                            onValueChange = { calcAmountStr = it },
                            label = { Text("المبلغ المراد تحويله") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = currentTheme.primaryColor,
                                unfocusedBorderColor = Color(0x44FFFFFF),
                                focusedLabelColor = currentTheme.primaryColor,
                                unfocusedLabelColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("من عملة:", color = Color.Gray, fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    currencies.take(3).forEach { c ->
                                        FilterChip(
                                            selected = calcFromCode == c.code,
                                            onClick = { calcFromCode = c.code },
                                            label = { Text(c.code) }
                                        )
                                    }
                                }
                            }

                            Icon(Icons.Default.SyncAlt, contentDescription = null, tint = currentTheme.primaryColor)

                            Column(modifier = Modifier.weight(1f)) {
                                Text("إلى عملة:", color = Color.Gray, fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    currencies.take(3).forEach { c ->
                                        FilterChip(
                                            selected = calcToCode == c.code,
                                            onClick = { calcToCode = c.code },
                                            label = { Text(c.code) }
                                        )
                                    }
                                }
                            }
                        }

                        val amount = calcAmountStr.toDoubleOrNull() ?: 0.0
                        val converted = viewModel.convertAmount(amount, calcFromCode, calcToCode)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = currentTheme.primaryColor.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("النتيجة المعادلة:", color = Color.White, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${String.format("%.2f", converted)} $calcToCode",
                                    color = currentTheme.primaryColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "قائمة أسعار الصرف المعرفة:",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(currencies) { curr ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEditDialog = curr },
                    colors = CardDefaults.cardColors(containerColor = Color(0x33111827)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0x2BFFFFFF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(curr.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = currentTheme.primaryColor.copy(alpha = 0.2f)
                                ) {
                                    Text(curr.code, color = currentTheme.primaryColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = if (curr.isMain) "العملة الأساسية للنظام (1.00)" else "1 ${curr.code} = ${curr.exchangeRateToMain} YER",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }

                        IconButton(onClick = { showEditDialog = curr }) {
                            Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = currentTheme.primaryColor)
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog != null) {
        val target = showEditDialog!!
        var newRateStr by remember { mutableStateOf(target.exchangeRateToMain.toString()) }

        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            title = { Text("تحديث سعر صرف ${target.name}") },
            text = {
                OutlinedTextField(
                    value = newRateStr,
                    onValueChange = { newRateStr = it },
                    label = { Text("سعر الصرف مقابل العملة الأساسية") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rate = newRateStr.toDoubleOrNull() ?: target.exchangeRateToMain
                        viewModel.updateExchangeRate(target.code, rate)
                        showEditDialog = null
                    }
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) { Text("إلغاء") }
            }
        )
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var symbol by remember { mutableStateOf("") }
        var rateStr by remember { mutableStateOf("1.0") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة عملة جديدة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم العملة (مثال: درهم إماراتي)") })
                    OutlinedTextField(value = code, onValueChange = { code = it.uppercase() }, label = { Text("رمز العملة (مثال: AED)") })
                    OutlinedTextField(value = symbol, onValueChange = { symbol = it }, label = { Text("الرمز المالي (مثال: د.إ)") })
                    OutlinedTextField(value = rateStr, onValueChange = { rateStr = it }, label = { Text("سعر الصرف مقابل العملة الأساسية") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && code.isNotBlank()) {
                            viewModel.addCurrency(
                                code = code,
                                name = name,
                                symbol = symbol.ifBlank { code },
                                rate = rateStr.toDoubleOrNull() ?: 1.0
                            )
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
            }
        )
    }
}

// ---------------------------------------------------
// 3. FULL PAGE: NOTIFICATIONS (الإشعارات)
// ---------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: StoreViewModel,
    navController: NavHostController
) {
    val selectedThemeId by viewModel.selectedThemeId.collectAsStateWithLifecycle()
    val currentTheme = getThemeOption(selectedThemeId)

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("الإشعارات والتنبيهات", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val notifications = listOf(
                Triple("تنبيه مخزون منخفض", "المنتج 'زيت زيتون فاخر 1 لتر' وصل إلى حد الطلب الأدنى (3 حبات)", "منذ 10 دقائق"),
                Triple("تنبيه استحقاق دفعة", "العميل 'شركة الأمل' لديه مستحقات متأخرة بقيمة 150,000 ريال", "منذ ساعة"),
                Triple("نسخة احتياطية", "تم إنشاء النسخة الاحتياطية التلقائية بنجاح", "اليوم 08:00 ص"),
                Triple("تحديث أسعار الصرف", "تم تعديل سعر صرف الريال السعودي إلى 140 YER", "أمس")
            )

            items(notifications) { (title, desc, time) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x331F2937)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0x2BFFFFFF))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(currentTheme.primaryColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = currentTheme.primaryColor)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(time, color = Color.Gray, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(desc, color = Color(0xFFD1D5DB), fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------
// 4. FULL PAGE: SMS MESSAGING (الرسائل الفورية SMS)
// ---------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsMessagingScreen(
    viewModel: StoreViewModel,
    navController: NavHostController
) {
    val selectedThemeId by viewModel.selectedThemeId.collectAsStateWithLifecycle()
    val currentTheme = getThemeOption(selectedThemeId)

    var smsTemplate by remember { mutableStateOf("عزيزي العميل {NAME}، نود تذكيركم بفاتورتكم رقم {INVOICE} بقيمة {AMOUNT} ريال. شكراً لتعاملكم معنا.") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("الرسائل الفورية SMS", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x331F2937)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, currentTheme.primaryColor.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("قالب رسالة التذكير القصير SMS:", color = Color.White, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = smsTemplate,
                        onValueChange = { smsTemplate = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Button(
                        onClick = { },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primaryColor)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ القالب")
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------
// 5. FULL PAGE: WHATSAPP MESSAGING (الرسائل الفورية واتساب)
// ---------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsappMessagingScreen(
    viewModel: StoreViewModel,
    navController: NavHostController
) {
    val selectedThemeId by viewModel.selectedThemeId.collectAsStateWithLifecycle()
    val currentTheme = getThemeOption(selectedThemeId)

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("الرسائل الفورية واتساب", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x331F2937)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366))
                        Text("ربط واتساب وإرسال الفواتير التلقائية", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(
                        "تتيح لك هذه الميزة إرسال الفواتير والسندات مباشرة إلى رقم واتساب العميل أو المورد بضغطة زر واحدة بأسلوب محترف مع رابط PDF مباشر.",
                        color = Color(0xFFD1D5DB),
                        fontSize = 13.5.sp
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------
// 6. FULL PAGE: PRICE QUOTES (عروض الأسعار)
// ---------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(
    viewModel: StoreViewModel,
    navController: NavHostController
) {
    val selectedThemeId by viewModel.selectedThemeId.collectAsStateWithLifecycle()
    val currentTheme = getThemeOption(selectedThemeId)

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("عروض الأسعار", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = currentTheme.primaryColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "عرض سعر جديد", tint = Color.White)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("قائمة عروض الأسعار المسجلة جاهزة للإضافة", color = Color.Gray, fontSize = 15.sp)
        }
    }
}

// ---------------------------------------------------
// 7. FULL PAGE: ORDERS (الطلبيات)
// ---------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: StoreViewModel,
    navController: NavHostController
) {
    val selectedThemeId by viewModel.selectedThemeId.collectAsStateWithLifecycle()
    val currentTheme = getThemeOption(selectedThemeId)

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("إدارة الطلبيات", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("قائمة الطلبيات والتوصيل جاهزة للإدارة", color = Color.Gray, fontSize = 15.sp)
        }
    }
}

// ---------------------------------------------------
// 8. FULL PAGE: TRASH / RECYCLE BIN (سلة المحذوفات)
// ---------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    viewModel: StoreViewModel,
    navController: NavHostController
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("سلة المحذوفات", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(54.dp))
                Text("سلة المحذوفات فارغة حالياً", color = Color.Gray, fontSize = 15.sp)
            }
        }
    }
}
