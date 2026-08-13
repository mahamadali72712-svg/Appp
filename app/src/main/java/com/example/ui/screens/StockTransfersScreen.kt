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
import com.example.data.local.StockTransfer
import com.example.data.local.StockTransferItem
import com.example.ui.viewmodels.StoreViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Burgundy = Color(0xFF8B1C31)
private val PurpleAccent = Color(0xFF6B21A8)
private val LightPurple = Color(0xFFF3E8FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockTransfersScreen(viewModel: StoreViewModel, navController: NavController) {
    val transfers by viewModel.allStockTransfers.collectAsStateWithLifecycle()
    val warehouses by viewModel.allWarehouses.collectAsStateWithLifecycle()
    
    var showAddScreen by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        if (showAddScreen) {
            AddStockTransferScreen(
                viewModel = viewModel,
                onBack = { showAddScreen = false },
                onSave = { from, to, items, note ->
                    viewModel.processStockTransfer(from, to, items, note) {
                        showAddScreen = false
                    }
                }
            )
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("التحويلات المخزنية", fontWeight = FontWeight.Bold, color = Color.White) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "رجوع", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Burgundy)
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { showAddScreen = true },
                        containerColor = PurpleAccent,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "تحويل جديد")
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF9FAFB))
                        .padding(paddingValues)
                ) {
                    if (transfers.isEmpty()) {
                        EmptyTransfersView { showAddScreen = true }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(transfers) { transfer ->
                                TransferItemCard(
                                    transfer = transfer,
                                    getWarehouseName = { id -> warehouses.find { it.id == id }?.name ?: "مخزن غير معروف" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransferItemCard(transfer: StockTransfer, getWarehouseName: (String) -> String) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(transfer.transferDate))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transfer.transferNumber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PurpleAccent
                )
                Text(
                    text = dateString,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("من مخزن", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = getWarehouseName(transfer.fromWarehouseId),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1F2937)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowBack, // Since it's RTL, ArrowBack points to the left (destination)
                    contentDescription = null,
                    tint = Burgundy,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text("إلى مخزن", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = getWarehouseName(transfer.toWarehouseId),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1F2937)
                    )
                }
            }
            if (!transfer.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ملاحظة: ${transfer.note}",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun EmptyTransfersView(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.SyncAlt,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "لا توجد تحويلات سابقة",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "قم بنقل البضائع بين المخازن لحفظ سجلاتها هنا",
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = Burgundy),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("تحويل مخزني جديد", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockTransferScreen(
    viewModel: StoreViewModel,
    onBack: () -> Unit,
    onSave: (fromWarehouse: String, toWarehouse: String, items: List<StockTransferItem>, note: String) -> Unit
) {
    val warehouses by viewModel.allWarehouses.collectAsStateWithLifecycle()
    val products by viewModel.allProducts.collectAsStateWithLifecycle()
    
    var fromWarehouseId by remember { mutableStateOf("") }
    var toWarehouseId by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    
    // For simplicity in this screen, we'll allow selecting one product to transfer.
    // In a fully-fledged ERP, this would be a dynamic list of items.
    var selectedProductId by remember { mutableStateOf("") }
    var quantityStr by remember { mutableStateOf("") }

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }
    var productExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تحويل بضاعة", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val qty = quantityStr.toDoubleOrNull() ?: 0.0
                            if (fromWarehouseId.isNotBlank() && toWarehouseId.isNotBlank() && 
                                fromWarehouseId != toWarehouseId && selectedProductId.isNotBlank() && qty > 0) {
                                
                                val items = listOf(
                                    StockTransferItem(
                                        transferId = "", // assigned in repo
                                        productId = selectedProductId,
                                        quantity = qty
                                    )
                                )
                                onSave(fromWarehouseId, toWarehouseId, items, note)
                            }
                        }
                    ) {
                        Text("حفظ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Burgundy)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // From Warehouse
            ExposedDropdownMenuBox(
                expanded = fromExpanded,
                onExpandedChange = { fromExpanded = !fromExpanded }
            ) {
                OutlinedTextField(
                    value = warehouses.find { it.id == fromWarehouseId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("من المخزن (المصدر)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleAccent,
                        focusedLabelColor = PurpleAccent
                    )
                )
                ExposedDropdownMenu(
                    expanded = fromExpanded,
                    onDismissRequest = { fromExpanded = false }
                ) {
                    warehouses.forEach { wh ->
                        DropdownMenuItem(
                            text = { Text(wh.name) },
                            onClick = {
                                fromWarehouseId = wh.id
                                fromExpanded = false
                            }
                        )
                    }
                }
            }

            // To Warehouse
            ExposedDropdownMenuBox(
                expanded = toExpanded,
                onExpandedChange = { toExpanded = !toExpanded }
            ) {
                OutlinedTextField(
                    value = warehouses.find { it.id == toWarehouseId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("إلى المخزن (الوجهة)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleAccent,
                        focusedLabelColor = PurpleAccent
                    )
                )
                ExposedDropdownMenu(
                    expanded = toExpanded,
                    onDismissRequest = { toExpanded = false }
                ) {
                    warehouses.filter { it.id != fromWarehouseId }.forEach { wh ->
                        DropdownMenuItem(
                            text = { Text(wh.name) },
                            onClick = {
                                toWarehouseId = wh.id
                                toExpanded = false
                            }
                        )
                    }
                }
            }

            // Select Product
            ExposedDropdownMenuBox(
                expanded = productExpanded,
                onExpandedChange = { productExpanded = !productExpanded }
            ) {
                OutlinedTextField(
                    value = products.find { it.id == selectedProductId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("الصنف المراد تحويله") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleAccent,
                        focusedLabelColor = PurpleAccent
                    )
                )
                ExposedDropdownMenu(
                    expanded = productExpanded,
                    onDismissRequest = { productExpanded = false }
                ) {
                    products.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.name) },
                            onClick = {
                                selectedProductId = p.id
                                productExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = quantityStr,
                onValueChange = { quantityStr = it },
                label = { Text("الكمية") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleAccent,
                    focusedLabelColor = PurpleAccent
                )
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("ملاحظات (اختياري)") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleAccent,
                    focusedLabelColor = PurpleAccent
                )
            )
        }
    }
}
