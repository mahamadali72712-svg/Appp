package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.Product
import com.example.data.local.PurchaseInvoice
import com.example.data.local.PurchaseInvoiceItem
import com.example.data.local.Supplier
import com.example.ui.viewmodels.StoreViewModel
import com.example.utils.formatCurrency
import com.example.utils.formatQty
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val ScreenBg = Color(0xFFF9FAFB)
private val PrimaryGreen = Color(0xFF106B3F)
private val CardBorder = Color(0xFFE5E7EB)
private val TextPrimary = Color(0xFF1F2937)
private val TextSecondary = Color(0xFF6B7280)
private val DangerRed = Color(0xFFEF4444)
private val SuccessGreen = Color(0xFF10B981)
private val WarningYellow = Color(0xFFF59E0B)

enum class PurchaseScreenView {
    LIST, ADD, DETAILS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesScreen(viewModel: StoreViewModel, navController: androidx.navigation.NavController) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var currentView by remember { mutableStateOf(PurchaseScreenView.LIST) }
        var selectedInvoice by remember { mutableStateOf<PurchaseInvoice?>(null) }

        BackHandler(enabled = currentView != PurchaseScreenView.LIST) {
            currentView = PurchaseScreenView.LIST
        }

        AnimatedContent(
            targetState = currentView,
            label = "PurchaseScreenTransition",
            transitionSpec = {
                if (targetState != PurchaseScreenView.LIST && initialState == PurchaseScreenView.LIST) {
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                } else {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                }
            }
        ) { view ->
            when (view) {
                PurchaseScreenView.LIST -> PurchasesListContent(
                    viewModel = viewModel,
                    onNavigateToAdd = {
                        selectedInvoice = null
                        currentView = PurchaseScreenView.ADD
                    },
                    onInvoiceClick = { invoice ->
                        selectedInvoice = invoice
                        currentView = PurchaseScreenView.DETAILS
                    }
                )
                PurchaseScreenView.ADD -> AddPurchaseInvoiceContent(
                    viewModel = viewModel,
                    onBack = { currentView = PurchaseScreenView.LIST }
                )
                PurchaseScreenView.DETAILS -> PurchaseInvoiceDetailsContent(
                    viewModel = viewModel,
                    invoice = selectedInvoice,
                    onBack = { currentView = PurchaseScreenView.LIST }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesListContent(
    viewModel: StoreViewModel,
    onNavigateToAdd: () -> Unit,
    onInvoiceClick: (PurchaseInvoice) -> Unit
) {
    val invoices by viewModel.allPurchaseInvoices.collectAsStateWithLifecycle(initialValue = emptyList())
    val suppliers by viewModel.allSuppliers.collectAsStateWithLifecycle(initialValue = emptyList())
    
    var searchQuery by remember { mutableStateOf("") }
    
    val totalPurchasesAmount = invoices.sumOf { it.totalAmount }
    val totalDue = invoices.sumOf { it.remainingAmount }
    val totalCount = invoices.size
    
    val filteredInvoices = invoices.filter { inv ->
        val supplierName = suppliers.find { it.id == inv.supplierId }?.name ?: ""
        inv.invoiceNumber.contains(searchQuery, true) || supplierName.contains(searchQuery, true)
    }.sortedByDescending { it.invoiceDate }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("المشتريات", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateToAdd,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .background(PrimaryGreen, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* Menu */ },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(Color.White, CircleShape)
                            .size(40.dp)
                            .border(1.dp, CardBorder, CircleShape)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "القائمة", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ScreenBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Statistics Cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    title = "إجمالي المبالغ المستحقة",
                    value = totalDue.formatCurrency().replace("ريال", "").trim(),
                    unit = "ريال",
                    icon = Icons.Outlined.ReceiptLong,
                    iconTint = DangerRed,
                    iconBg = Color(0xFFFEE2E2),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "عدد الفواتير",
                    value = "$totalCount",
                    unit = "فاتورة",
                    icon = Icons.Outlined.Description,
                    iconTint = Color(0xFF2563EB),
                    iconBg = Color(0xFFE0F2FE),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "إجمالي المشتريات",
                    value = totalPurchasesAmount.formatCurrency().replace("ريال", "").trim(),
                    unit = "ريال",
                    icon = Icons.Outlined.CreditCard,
                    iconTint = PrimaryGreen,
                    iconBg = Color(0xFFDCFCE7),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search & Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("بحث برقم الفاتورة أو اسم المورد", fontSize = 12.sp, color = TextSecondary) },
                    trailingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = TextSecondary) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = CardBorder,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .clickable { /* Filter */ }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("تصفية", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Outlined.FilterList, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Invoices List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredInvoices, key = { it.id }) { invoice ->
                    val supplier = suppliers.find { it.id == invoice.supplierId }
                    var itemsCount by remember { mutableStateOf(0) }
                    LaunchedEffect(invoice.id) {
                        itemsCount = viewModel.getPurchaseInvoiceItems(invoice.id).size
                    }
                    PurchaseInvoiceListItem(
                        invoice = invoice,
                        supplierName = supplier?.name ?: "مورد غير معروف",
                        itemsCount = itemsCount,
                        onClick = { onInvoiceClick(invoice) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = unit,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(iconBg, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PurchaseInvoiceListItem(
    invoice: PurchaseInvoice,
    supplierName: String,
    itemsCount: Int,
    onClick: () -> Unit,
    onMenuClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth()
        ) {
            // Top Row: Invoice Number, Supplier Name, Status Badge, More Vert Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Right side (RTL start): Invoice Code (smaller font size for long numbers as requested)
                Text(
                    text = invoice.invoiceNumber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1.1f)
                )
                
                // Center: Supplier Name
                Text(
                    text = supplierName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1.4f)
                )
                
                // Left side (RTL end): Status Badge & Options
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(1.2f)
                ) {
                    val (statusText, statusBg, statusColor) = when {
                        invoice.paidAmount >= invoice.totalAmount -> Triple("مدفوعة", Color(0xFFDCFCE7), Color(0xFF15803D))
                        invoice.paidAmount == 0.0 -> Triple("غير مدفوعة", Color(0xFFFEE2E2), Color(0xFFDC2626))
                        else -> Triple("جزئية", Color(0xFFFEF3C7), Color(0xFFD97706))
                    }
                    Box(
                        modifier = Modifier
                            .background(statusBg, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 11.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(2.dp))
                    
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "المزيد",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Bottom Row: Items Count, Date, Total Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Right side (RTL start): Items count with box icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$itemsCount أصناف",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Center: Date with calendar icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                    Text(
                        text = format.format(Date(invoice.invoiceDate)),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Left side (RTL end): Total Amount
                Text(
                    text = "${invoice.totalAmount.formatCurrency().replace("ريال", "").trim()} ريال",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1.1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPurchaseInvoiceContent(viewModel: StoreViewModel, onBack: () -> Unit) {
    val suppliers by viewModel.allSuppliers.collectAsStateWithLifecycle(initialValue = emptyList())
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle(initialValue = emptyList())
    var selectedSupplier by remember { mutableStateOf<Supplier?>(null) }
    var invoiceNumber by remember { mutableStateOf("PUR-${System.currentTimeMillis().toString().takeLast(6)}") }
    var notes by remember { mutableStateOf("") }
    
    val selectedItems = remember { mutableStateListOf<PurchaseInvoiceItem>() }
    var discount by remember { mutableStateOf("0") }
    var paidAmount by remember { mutableStateOf("0") }
    var shipping by remember { mutableStateOf("0") }
    var taxPercent by remember { mutableStateOf("0") }
    
    
    var showProductSelection by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<PurchaseInvoiceItem?>(null) }

    
    val context = LocalContext.current
    
    val subTotal = selectedItems.sumOf { it.lineTotal }
    val discountVal = discount.toDoubleOrNull() ?: 0.0
    val shippingVal = shipping.toDoubleOrNull() ?: 0.0
    val taxVal = subTotal * ((taxPercent.toDoubleOrNull() ?: 0.0) / 100.0)
    val grandTotal = subTotal - discountVal + shippingVal + taxVal

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("إضافة فاتورة شراء", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "رجوع", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ScreenBg)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            if (selectedSupplier == null) {
                                Toast.makeText(context, "الرجاء اختيار مورد", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (selectedItems.isEmpty()) {
                                Toast.makeText(context, "الرجاء إضافة أصناف للفاتورة", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.processPurchase(
                                supplierId = selectedSupplier!!.id,
                                items = selectedItems.toList(),
                                discount = discountVal,
                                paidAmount = paidAmount.toDoubleOrNull() ?: grandTotal,
                                onSuccess = {
                                    Toast.makeText(context, "تم حفظ الفاتورة بنجاح", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Save, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ الفاتورة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Text("حفظ كمسودة", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            // Section 1: Supplier & Invoice Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Assignment, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("بيانات المورد والفاتورة", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryGreen)
                        }
                        HorizontalDivider(color = CardBorder)
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = selectedSupplier?.name ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("المورد *", fontSize = 12.sp) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = PrimaryGreen)
                                )
                                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color.White)) {
                                    suppliers.forEach { s ->
                                        DropdownMenuItem(text = { Text(s.name) }, onClick = { selectedSupplier = s; expanded = false })
                                    }
                                }
                            }
                            
                            val format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                            CustomTextField("تاريخ الفاتورة *", format.format(Date()), {}, readOnly = true, modifier = Modifier.weight(1f))
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CustomTextField("رقم الفاتورة", invoiceNumber, { invoiceNumber = it }, modifier = Modifier.weight(1f))
                            CustomTextField("تاريخ الاستحقاق", "", {}, readOnly = true, trailingIcon = Icons.Outlined.DateRange, modifier = Modifier.weight(1f))
                        }
                        
                        CustomTextField("المستودع *", "المستودع الرئيسي", {}, readOnly = true)
                        CustomTextField("ملاحظات", notes, { notes = it }, placeholder = "اكتب ملاحظة (اختياري)")
                    }
                }
            }
            
            // Section 2: Items
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("الأصناف", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryGreen)
                        }
                        HorizontalDivider(color = CardBorder)
                        
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.height(48.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryGreen).clickable { showProductSelection = true }.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("منتج جديد", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                placeholder = { Text("ابحث عن منتج بالاسم أو الباركود أو SKU", fontSize = 12.sp, color = TextSecondary) },
                                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = TextSecondary) },
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier.weight(1f).height(48.dp).clickable { showProductSelection = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = CardBorder,
                                    disabledContainerColor = Color.White,
                                )
                            )
                        }
                        
                        if (selectedItems.isNotEmpty()) {
                            // Table Header
                            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F5F0)).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("المنتج", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(2f))
                                Text("الوحدة", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text("الكمية", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text("سعر التكلفة", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                                Text("الإجمالي", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.width(24.dp))
                            }
                            
                            // Items
                            selectedItems.forEachIndexed { index, item ->
                                val product = allProducts.find { it.id == item.productId }
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable { itemToEdit = item }.padding(horizontal = 12.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(32.dp).background(ScreenBg, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Outlined.Image, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(product?.name ?: "منتج غير معروف", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(product?.barcode ?: product?.code ?: "", fontSize = 10.sp, color = TextSecondary)
                                        }
                                    }
                                    Text(product?.baseUnit ?: "حبة", fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                    Text(item.quantity.formatQty(), fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                    Text(item.unitCost.formatQty(), fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                                    Text(item.lineTotal.formatQty(), fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                                    Icon(
                                        Icons.Outlined.DeleteOutline,
                                        contentDescription = "حذف",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(20.dp).clickable { selectedItems.removeAt(index) }
                                    )
                                }
                                if (index < selectedItems.size - 1) HorizontalDivider(color = CardBorder)
                            }
                        }
                    }
                }
            }
            
            // Section 3: Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            SummaryItem("عدد الأصناف", "${selectedItems.size}", Modifier.weight(1f))
                            SummaryItem("إجمالي الكمية", selectedItems.sumOf { it.quantity }.formatQty(), Modifier.weight(1f))
                            SummaryItem("الإجمالي قبل الضريبة", subTotal.formatQty(), Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            SummaryItem("إجمالي الخصم", discountVal.formatQty(), Modifier.weight(1f))
                            SummaryItem("الشحن", shippingVal.formatQty(), Modifier.weight(1f))
                            SummaryItem("الضريبة (${taxPercent}%)", taxVal.formatQty(), Modifier.weight(1f))
                        }
                        HorizontalDivider(color = CardBorder)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("الإجمالي", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                            Text(grandTotal.formatCurrency(), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = PrimaryGreen)
                        }
                    }
                }
            }
        }
    }
    
    
    if (showProductSelection) {
        ProductSelectionDialog(
            viewModel = viewModel,
            onDismiss = { showProductSelection = false },
            onProductSelected = { product ->
                val existing = selectedItems.find { it.productId == product.id }
                if (existing != null) {
                    val index = selectedItems.indexOf(existing)
                    selectedItems[index] = existing.copy(
                        quantity = existing.quantity + 1,
                        lineTotal = (existing.quantity + 1) * existing.unitCost
                    )
                } else {
                    selectedItems.add(
                        PurchaseInvoiceItem(
                            invoiceId = "", // assigned at save
                            productId = product.id,
                            quantity = 1.0,
                            unitCost = product.costPrice,
                            lineTotal = product.costPrice
                        )
                    )
                }
                showProductSelection = false
            }
        )
    }
    
    itemToEdit?.let { item ->
        EditItemDialog(
            item = item,
            productName = allProducts.find { it.id == item.productId }?.name ?: "منتج",
            onDismiss = { itemToEdit = null },
            onSave = { updatedItem ->
                val index = selectedItems.indexOf(item)
                if (index != -1) {
                    selectedItems[index] = updatedItem
                }
                itemToEdit = null
            }
        )
    }

}

@Composable
fun SummaryItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 11.sp, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 13.sp, color = valueColor, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isNumeric: Boolean = false,
    readOnly: Boolean = false,
    placeholder: String = "",
    trailingIcon: ImageVector? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder, fontSize = 12.sp, color = TextSecondary) } } else null,
        keyboardOptions = if (isNumeric) KeyboardOptions(keyboardType = KeyboardType.Decimal) else KeyboardOptions.Default,
        readOnly = readOnly,
        singleLine = true,
        trailingIcon = trailingIcon?.let { { Icon(it, contentDescription = null, tint = TextSecondary) } },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryGreen,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = if (readOnly) ScreenBg else Color.White
        )
    )
}

@Composable
fun ProductSelectionDialog(
    viewModel: StoreViewModel,
    onDismiss: () -> Unit,
    onProductSelected: (Product) -> Unit
) {
    val products by viewModel.allProducts.collectAsStateWithLifecycle(initialValue = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredProducts = products.filter {
        it.status != "INACTIVE" && (it.name.contains(searchQuery, true) || 
        it.code?.contains(searchQuery, true) == true || 
        it.barcode?.contains(searchQuery, true) == true)
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxSize().padding(top = 40.dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = ScreenBg
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextPrimary)
                    }
                    Text("اختر منتج", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                    TextButton(onClick = { /* Add New Product shortcut */ }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("منتج جديد", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
                
                // Search
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("ابحث عن منتج بالاسم أو الباركود أو SKU", fontSize = 13.sp, color = TextSecondary) },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = TextSecondary) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = CardBorder,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                    Box(
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).border(1.dp, CardBorder, RoundedCornerShape(12.dp)).clickable { /* Filter */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.FilterList, contentDescription = null, tint = TextPrimary)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tabs (dummy for UI matching)
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Box(modifier = Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(20.dp)).background(PrimaryGreen), contentAlignment = Alignment.Center) {
                        Text("الكل", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Box(modifier = Modifier.weight(1f).height(40.dp), contentAlignment = Alignment.Center) {
                        Text("المفضلة", color = TextSecondary, fontSize = 14.sp)
                    }
                    Box(modifier = Modifier.weight(1f).height(40.dp), contentAlignment = Alignment.Center) {
                        Text("الأكثر استخداماً", color = TextSecondary, fontSize = 14.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // List
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onProductSelected(product) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.StarBorder, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${product.barcode ?: ""} | ${product.code ?: ""}", fontSize = 11.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val stockColor = if (product.stockQuantity <= product.minStockAlert) DangerRed else PrimaryGreen
                                    Text("${product.stockQuantity.formatQty()} ${product.baseUnit}", fontSize = 12.sp, color = stockColor, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Box(modifier = Modifier.size(48.dp).background(ScreenBg, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Outlined.Image, contentDescription = null, tint = TextSecondary)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(product.costPrice.formatCurrency(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseInvoiceDetailsContent(
    viewModel: StoreViewModel,
    invoice: PurchaseInvoice?,
    onBack: () -> Unit
) {
    if (invoice == null) {
        onBack()
        return
    }

    val suppliers by viewModel.allSuppliers.collectAsStateWithLifecycle(initialValue = emptyList())
    val supplierName = suppliers.find { it.id == invoice.supplierId }?.name ?: "مورد غير معروف"

    var items by remember { mutableStateOf<List<PurchaseInvoiceItem>>(emptyList()) }
    val products by viewModel.allProducts.collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(invoice.id) {
        items = viewModel.getPurchaseInvoiceItems(invoice.id)
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("تفاصيل فاتورة الشراء", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Menu */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "المزيد", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ScreenBg)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 12.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Right button (RTL start): Add Payment (Main Green Button)
                    Button(
                        onClick = { /* Add Payment */ },
                        modifier = Modifier
                            .weight(1.8f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إضافة دفعة", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    }

                    // Middle button: Send
                    OutlinedButton(
                        onClick = { /* Send */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Icon(Icons.Outlined.Send, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إرسال", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // Left button (RTL end): Print
                    OutlinedButton(
                        onClick = { /* Print */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Icon(Icons.Outlined.Print, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("طباعة", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Top Header Card (Invoice Info & Financial Summary)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Top Row: Info (RTL Right) and Status Badge (RTL Left)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            // RTL Left (End): Status Badge
                            val (statusText, statusBg, statusColor) = when {
                                invoice.paidAmount >= invoice.totalAmount -> Triple("مدفوعة", Color(0xFFDCFCE7), Color(0xFF15803D))
                                invoice.paidAmount == 0.0 -> Triple("غير مدفوعة", Color(0xFFFEE2E2), Color(0xFFDC2626))
                                else -> Triple("جزئية", Color(0xFFFEF3C7), Color(0xFFD97706))
                            }
                            Box(
                                modifier = Modifier
                                    .background(statusBg, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = statusText,
                                    fontSize = 12.sp,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // RTL Right (Start): Invoice Code, Supplier Name, Date
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = invoice.invoiceNumber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp, // Smaller font for PUR code as requested
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = supplierName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                                Text(
                                    text = format.format(Date(invoice.invoiceDate)),
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = CardBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Bottom Row: 3 Financial Columns separated by vertical grid lines
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SummaryItem(
                                title = "الإجمالي",
                                value = "${invoice.totalAmount.formatCurrency().replace("ريال", "").trim()} ريال",
                                modifier = Modifier.weight(1f)
                            )
                            VerticalDivider(modifier = Modifier.height(36.dp), color = CardBorder)
                            SummaryItem(
                                title = "المستحق",
                                value = "${invoice.remainingAmount.formatCurrency().replace("ريال", "").trim()} ريال",
                                modifier = Modifier.weight(1f)
                            )
                            VerticalDivider(modifier = Modifier.height(36.dp), color = CardBorder)
                            SummaryItem(
                                title = "المدفوع",
                                value = "${invoice.paidAmount.formatCurrency().replace("ريال", "").trim()} ريال",
                                valueColor = if (invoice.paidAmount > 0) Color(0xFF15803D) else TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Tabs Bar
            item {
                var selectedTab by remember { mutableStateOf(0) }
                val tabs = listOf("تفاصيل الفاتورة", "الدفعات (1)", "المرفقات", "سجل التعديلات")

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ScreenBg,
                    contentColor = PrimaryGreen,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = PrimaryGreen,
                            height = 3.dp
                        )
                    },
                    divider = { HorizontalDivider(color = CardBorder) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) PrimaryGreen else TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
            }

            // Real Receipt Grid Table (Green Tint Header + Grid Borders)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Green Tint Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFECFDF5))
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "المنتج",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier
                                    .weight(2.2f)
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                textAlign = TextAlign.Start
                            )
                            VerticalDivider(color = CardBorder)
                            Text(
                                text = "الوحدة",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                textAlign = TextAlign.Center
                            )
                            VerticalDivider(color = CardBorder)
                            Text(
                                text = "الكمية",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                textAlign = TextAlign.Center
                            )
                            VerticalDivider(color = CardBorder)
                            Text(
                                text = "سعر التكلفة",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier
                                    .weight(1.3f)
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                textAlign = TextAlign.Center
                            )
                            VerticalDivider(color = CardBorder)
                            Text(
                                text = "الإجمالي",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier
                                    .weight(1.3f)
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        HorizontalDivider(color = CardBorder)

                        // Data Rows with full grid vertical dividers
                        items.forEachIndexed { index, item ->
                            val product = products.find { it.id == item.productId }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Product Name + Code + Icon
                                Row(
                                    modifier = Modifier
                                        .weight(2.2f)
                                        .padding(horizontal = 8.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF3F4F6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Inventory2,
                                            contentDescription = null,
                                            tint = PrimaryGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = product?.name ?: "منتج",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = product?.barcode ?: product?.code ?: "001-31009",
                                            fontSize = 10.sp,
                                            color = TextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                VerticalDivider(color = CardBorder)

                                Text(
                                    text = product?.baseUnit ?: "حبة",
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp),
                                    textAlign = TextAlign.Center
                                )

                                VerticalDivider(color = CardBorder)

                                Text(
                                    text = item.quantity.formatQty(),
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp),
                                    textAlign = TextAlign.Center
                                )

                                VerticalDivider(color = CardBorder)

                                Text(
                                    text = item.unitCost.formatQty(),
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .padding(4.dp),
                                    textAlign = TextAlign.Center
                                )

                                VerticalDivider(color = CardBorder)

                                Text(
                                    text = item.lineTotal.formatQty(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .padding(4.dp),
                                    textAlign = TextAlign.Center
                                )
                            }

                            if (index < items.size - 1) {
                                HorizontalDivider(color = CardBorder)
                            }
                        }
                    }
                }
            }

            // Summary Footer Card (Green Tinted Financial Totals Box as in Image 2)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الشحن",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                            VerticalDivider(color = CardBorder)
                            Text(
                                text = "الخصم",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                            VerticalDivider(color = CardBorder)
                            Text(
                                text = "الضريبة (%0)",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                            VerticalDivider(color = CardBorder)
                            Text(
                                text = "الإجمالي شامل الضريبة",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier
                                    .weight(1.2f)
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        HorizontalDivider(color = CardBorder)

                        // Values Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "0.00",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 10.dp),
                                textAlign = TextAlign.Center
                            )
                            VerticalDivider(color = CardBorder)
                            Text(
                                text = invoice.discount.formatQty(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 10.dp),
                                textAlign = TextAlign.Center
                            )
                            VerticalDivider(color = CardBorder)
                            Text(
                                text = "0.00",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 10.dp),
                                textAlign = TextAlign.Center
                            )
                            VerticalDivider(color = CardBorder)
                            Text(
                                text = "${items.sumOf { it.lineTotal }.formatQty()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier
                                    .weight(1.2f)
                                    .padding(vertical = 10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditItemDialog(
    item: PurchaseInvoiceItem,
    productName: String,
    onDismiss: () -> Unit,
    onSave: (PurchaseInvoiceItem) -> Unit
) {
    var qty by remember { mutableStateOf(item.quantity.formatQty()) }
    var cost by remember { mutableStateOf(item.unitCost.formatQty()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("تعديل: $productName", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                
                CustomTextField(
                    label = "الكمية",
                    value = qty,
                    onValueChange = { qty = it },
                    isNumeric = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                CustomTextField(
                    label = "سعر التكلفة",
                    value = cost,
                    onValueChange = { cost = it },
                    isNumeric = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("إلغاء", color = TextPrimary)
                    }
                    Button(
                        onClick = {
                            val newQty = qty.toDoubleOrNull() ?: item.quantity
                            val newCost = cost.toDoubleOrNull() ?: item.unitCost
                            onSave(item.copy(
                                quantity = newQty,
                                unitCost = newCost,
                                lineTotal = newQty * newCost
                            ))
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("حفظ")
                    }
                }
            }
        }
    }
}
