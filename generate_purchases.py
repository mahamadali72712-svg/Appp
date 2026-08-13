import re

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "r") as f:
    old_content = f.read()

# Generate the new PurchasesScreen.kt
new_content = """package com.example.ui.screens

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
    val invoices by viewModel.allPurchases.collectAsStateWithLifecycle(initialValue = emptyList())
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
                        onClick = { /* Menu */ },
                        modifier = Modifier.padding(start = 16.dp).background(Color.White, CircleShape).size(40.dp).border(1.dp, CardBorder, CircleShape)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "القائمة", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToAdd,
                        modifier = Modifier.padding(end = 16.dp).background(PrimaryGreen, CircleShape).size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ScreenBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Statistics Cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("إجمالي المبالغ المستحقة", totalDue.formatCurrency(), Icons.Outlined.AccountBalanceWallet, DangerRed, Modifier.weight(1f))
                StatCard("عدد الفواتير", "$totalCount فاتورة", Icons.Outlined.Receipt, Color(0xFF3B82F6), Modifier.weight(1f))
                StatCard("إجمالي المشتريات", totalPurchasesAmount.formatCurrency(), Icons.Outlined.ShoppingCart, PrimaryGreen, Modifier.weight(1f))
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
                    placeholder = { Text("بحث برقم الفاتورة أو اسم المورد", fontSize = 13.sp, color = TextSecondary) },
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
                    modifier = Modifier.height(56.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).border(1.dp, CardBorder, RoundedCornerShape(12.dp)).clickable { /* Filter */ }.padding(horizontal = 16.dp),
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
fun StatCard(title: String, value: String, icon: ImageVector, iconTint: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 11.sp, color = TextSecondary, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value.replace(" ريال", "").trim(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text("ريال", fontSize = 10.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun PurchaseInvoiceListItem(invoice: PurchaseInvoice, supplierName: String, itemsCount: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(supplierName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Category, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$itemsCount أصناف", fontSize = 12.sp, color = TextSecondary)
                }
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text(invoice.invoiceNumber, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.DateRange, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    val format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                    Text(format.format(Date(invoice.invoiceDate)), fontSize = 12.sp, color = TextSecondary)
                }
            }
            
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                val (statusText, statusBg, statusColor) = when {
                    invoice.paidAmount >= invoice.totalAmount -> Triple("مدفوعة", Color(0xFFD1FAE5), PrimaryGreen)
                    invoice.paidAmount == 0.0 -> Triple("غير مدفوعة", Color(0xFFFEE2E2), DangerRed)
                    else -> Triple("جزئية", Color(0xFFFEF3C7), WarningYellow)
                }
                Box(modifier = Modifier.background(statusBg, RoundedCornerShape(16.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(statusText, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(invoice.totalAmount.formatCurrency(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.MoreVert, contentDescription = "المزيد", tint = TextSecondary)
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "w") as f:
    f.write(new_content)

print("Done part 1")
