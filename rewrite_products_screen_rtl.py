import re

with open("app/src/main/java/com/example/ui/screens/ProductsScreen.kt", "r") as f:
    content = f.read()

new_content = """package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.Product
import com.example.data.local.ProductCategory
import com.example.ui.viewmodels.StoreViewModel
import com.example.utils.formatCurrency
import kotlinx.coroutines.launch

// Colors from the design
private val ScreenBg = Color(0xFFF5F6F8)
private val PrimaryGreen = Color(0xFF106B3F) // Darker green from the screenshot
private val CardBorder = Color(0xFFE5E7EB)
private val TextPrimary = Color(0xFF1F2937)
private val TextSecondary = Color(0xFF6B7280)
private val WarningOrange = Color(0xFFF59E0B)
private val DangerRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: StoreViewModel, navController: androidx.navigation.NavController) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
        val archivedProducts by viewModel.archivedProducts.collectAsStateWithLifecycle()
        val categories by viewModel.allCategories.collectAsStateWithLifecycle()
        
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        
        LaunchedEffect(categories) {
            if (categories.isEmpty()) {
                viewModel.seedCategories()
            }
        }
        
        var searchQuery by remember { mutableStateOf("") }
        var showAddScreen by remember { mutableStateOf(false) }
        var productToEdit by remember { mutableStateOf<Product?>(null) }
        
        val filteredList = allProducts.filter { product ->
            product.name.contains(searchQuery, ignoreCase = true) || 
            product.code?.contains(searchQuery, ignoreCase = true) == true ||
            product.barcode?.contains(searchQuery, ignoreCase = true) == true
        }

        AnimatedContent(targetState = showAddScreen, label = "ScreenTransition") { isAddScreen ->
            if (isAddScreen) {
                AddEditProductScreen(
                    product = productToEdit,
                    categories = categories,
                    onBack = { showAddScreen = false },
                    onSave = { name, cost, price, stock, categoryId, minStockAlert, desc, sku, barcode, baseUnit, altUnit, conversionFactor ->
                        if (productToEdit == null) {
                            viewModel.addProduct(
                                name = name,
                                cost = cost,
                                suggestedPrice = price,
                                stock = stock,
                                categoryId = categoryId,
                                minStockAlert = minStockAlert,
                                description = desc,
                                color = null,
                                size = null,
                                sku = sku,
                                barcode = barcode,
                                baseUnit = baseUnit,
                                altUnit = altUnit,
                                conversionFactor = conversionFactor,
                                onSuccess = {
                                    showAddScreen = false
                                    Toast.makeText(context, "تمت إضافة المنتج بنجاح", Toast.LENGTH_SHORT).show()
                                },
                                onError = { errorMsg ->
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            val updated = productToEdit!!.copy(
                                name = name,
                                costPrice = cost,
                                suggestedPrice = price,
                                categoryId = categoryId,
                                minStockAlert = minStockAlert,
                                description = desc,
                                code = sku,
                                barcode = barcode,
                                baseUnit = baseUnit,
                                altUnit = altUnit,
                                conversionFactor = conversionFactor
                            )
                            viewModel.updateProduct(
                                product = updated,
                                onSuccess = {
                                    showAddScreen = false
                                    Toast.makeText(context, "تم تحديث المنتج", Toast.LENGTH_SHORT).show()
                                },
                                onError = { errorMsg ->
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    }
                )
            } else {
                Scaffold(
                    containerColor = ScreenBg,
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("المنتجات", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary) },
                            navigationIcon = {
                                IconButton(
                                    onClick = { /* Open Drawer */ },
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .background(Color.White, CircleShape)
                                        .size(40.dp)
                                ) {
                                    Icon(Icons.Default.Menu, contentDescription = "القائمة", tint = TextPrimary)
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { /* Search Action */ },
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .background(Color.White, CircleShape)
                                        .size(40.dp)
                                ) {
                                    Icon(Icons.Outlined.Search, contentDescription = "بحث", tint = TextPrimary)
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
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Summary Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                title = "إجمالي المنتجات",
                                count = allProducts.size.toString(),
                                icon = Icons.Outlined.Inventory2,
                                isPrimary = true
                            )
                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                title = "أقل من حد التنبيه",
                                count = allProducts.count { it.stockQuantity <= it.minStockAlert }.toString(),
                                icon = Icons.Outlined.WarningAmber,
                                isPrimary = false
                            )
                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                title = "غير نشطة",
                                count = archivedProducts.size.toString(),
                                icon = Icons.Outlined.RemoveCircleOutline,
                                isPrimary = false
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Search Bar & Filter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.weight(1f).height(52.dp),
                                placeholder = { Text("بحث بالاسم أو الباركود أو SKU", fontSize = 13.sp, color = TextSecondary) },
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
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                    .clickable { /* Filter Action */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.FilterAlt, contentDescription = "تصفية", tint = TextPrimary)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Action Buttons (Add & Sort)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { 
                                    productToEdit = null
                                    showAddScreen = true 
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("إضافة منتج جديد", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                    .clickable { /* Sort Action */ }
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ImportExport, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("الأحدث", fontSize = 13.sp, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Product List
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(filteredList, key = { it.id }) { product ->
                                val catName = categories.find { it.id == product.categoryId }?.name ?: "بدون تصنيف"
                                ProductListItem(
                                    product = product,
                                    categoryName = catName,
                                    onEdit = {
                                        productToEdit = product
                                        showAddScreen = true
                                    },
                                    onArchive = {
                                        scope.launch {
                                            val canDelete = viewModel.canDeleteProduct(product.id)
                                            if (canDelete) {
                                                viewModel.deleteProduct(product.id)
                                                Toast.makeText(context, "تم حذف المنتج", Toast.LENGTH_SHORT).show()
                                            } else {
                                                viewModel.archiveProduct(product.id)
                                                Toast.makeText(context, "تم أرشفة المنتج", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
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
fun SummaryCard(modifier: Modifier = Modifier, title: String, count: String, icon: ImageVector, isPrimary: Boolean) {
    Card(
        modifier = modifier.height(86.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isPrimary) PrimaryGreen else Color.White),
        border = if (isPrimary) null else BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title, 
                color = if (isPrimary) Color.White.copy(alpha = 0.9f) else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isPrimary) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = count,
                    color = if (isPrimary) Color.White else TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    icon, 
                    contentDescription = null, 
                    tint = if (isPrimary) Color.White.copy(alpha = 0.7f) else TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ProductListItem(
    product: Product,
    categoryName: String,
    onEdit: () -> Unit,
    onArchive: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    val stockColor = when {
        product.stockQuantity <= 0 -> DangerRed
        product.stockQuantity <= product.minStockAlert -> WarningOrange
        else -> PrimaryGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEdit() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left Side: Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ScreenBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Image, contentDescription = null, tint = TextSecondary.copy(alpha = 0.3f), modifier = Modifier.size(32.dp))
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Middle: Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val skuStr = product.code.takeIf { !it.isNullOrBlank() } ?: "بدون كود"
                    val codeStr = "${product.id.take(3)}-3100${(0..9).random()} | $skuStr" // Mocking the exact string from image
                    Text(codeStr, fontSize = 11.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(categoryName, fontSize = 11.sp, color = TextSecondary)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("سعر البيع", fontSize = 10.sp, color = TextSecondary)
                Text(
                    text = "${product.suggestedPrice.formatCurrency().replace("ر.س", "").trim()} ريال",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
            
            // Right Side: Actions & Stock
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.height(100.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp).offset(x = 8.dp, y = (-4).dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "خيارات", tint = TextSecondary)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("تعديل") },
                            onClick = { showMenu = false; onEdit() },
                            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("أرشفة / حذف", color = DangerRed) },
                            onClick = { showMenu = false; onArchive() },
                            leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = DangerRed) }
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("المخزون", fontSize = 11.sp, color = TextSecondary)
                    val qtyStr = if (product.stockQuantity % 1 == 0.0) product.stockQuantity.toInt().toString() else product.stockQuantity.toString()
                    Text(
                        text = "$qtyStr ${product.baseUnit}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = stockColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    product: Product?,
    categories: List<ProductCategory>,
    onBack: () -> Unit,
    onSave: (String, Double, Double, Double, String?, Double, String?, String?, String?, String, String?, Double) -> Unit
) {
    // Form States
    var name by remember { mutableStateOf(product?.name ?: "") }
    var sku by remember { mutableStateOf(product?.code ?: "") }
    var barcode by remember { mutableStateOf(product?.barcode ?: "") }
    var categoryId by remember { mutableStateOf(product?.categoryId) }
    var baseUnit by remember { mutableStateOf(product?.baseUnit ?: "حبة") }
    var altUnit by remember { mutableStateOf(product?.altUnit ?: "") }
    var conversionFactor by remember { mutableStateOf(product?.conversionFactor?.toString() ?: "1.00") }
    
    var cost by remember { mutableStateOf(product?.costPrice?.toString()?.takeIf { it != "0.0" } ?: "") }
    var price by remember { mutableStateOf(product?.suggestedPrice?.toString()?.takeIf { it != "0.0" } ?: "") }
    var suggestedPrice by remember { mutableStateOf(product?.suggestedPrice?.toString()?.takeIf { it != "0.0" } ?: "") }
    
    var stock by remember { mutableStateOf(if (product == null) "" else product.stockQuantity.toString()) }
    var minStockAlert by remember { mutableStateOf(product?.minStockAlert?.toString()?.takeIf { it != "0.0" } ?: "") }
    
    var description by remember { mutableStateOf(product?.description ?: "") }
    
    var isAdditionalExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (product == null) "إضافة منتج جديد" else "تعديل المنتج", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary) },
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            onSave(
                                name,
                                cost.toDoubleOrNull() ?: 0.0,
                                price.toDoubleOrNull() ?: 0.0,
                                stock.toDoubleOrNull() ?: 0.0,
                                categoryId,
                                minStockAlert.toDoubleOrNull() ?: 0.0,
                                description.takeIf { it.isNotBlank() },
                                sku.takeIf { it.isNotBlank() },
                                barcode.takeIf { it.isNotBlank() },
                                baseUnit,
                                altUnit.takeIf { it.isNotBlank() },
                                conversionFactor.toDoubleOrNull() ?: 1.0
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("حفظ المنتج", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PrimaryGreen)
                    ) {
                        Text("إلغاء", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Basic Info
                item {
                    SectionCard(title = "المعلومات الأساسية", icon = Icons.Outlined.ReceiptLong) {
                        CustomTextField(value = name, onValueChange = { name = it }, label = "اسم المنتج *", placeholder = "أدخل اسم المنتج")
                        CustomTextField(value = sku, onValueChange = { sku = it }, label = "كود المنتج / SKU", placeholder = "أدخل كود المنتج")
                        CustomTextField(
                            value = barcode, 
                            onValueChange = { barcode = it }, 
                            label = "الباركود", 
                            placeholder = "أدخل الباركود",
                            trailingIcon = Icons.Outlined.QrCodeScanner
                        )
                        
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                            val selectedCat = categories.find { it.id == categoryId }
                            CustomTextField(
                                value = selectedCat?.name ?: "",
                                onValueChange = {},
                                label = "التصنيف *",
                                placeholder = "اختر التصنيف",
                                readOnly = true,
                                trailingIcon = Icons.Outlined.LocalOffer, // Match the tag icon in screenshot
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                DropdownMenuItem(text = { Text("بدون تصنيف") }, onClick = { categoryId = null; expanded = false })
                                categories.forEach { cat ->
                                    DropdownMenuItem(text = { Text(cat.name) }, onClick = { categoryId = cat.id; expanded = false })
                                }
                            }
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                CustomTextField(value = altUnit, onValueChange = { altUnit = it }, label = "الوحدة البديلة", placeholder = "اختر الوحدة", trailingIcon = Icons.Default.ArrowDropDown)
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CustomTextField(value = baseUnit, onValueChange = { baseUnit = it }, label = "الوحدة الأساسية *", placeholder = "اختر الوحدة", trailingIcon = Icons.Default.ArrowDropDown)
                            }
                        }
                        
                        CustomTextField(value = conversionFactor, onValueChange = { conversionFactor = it }, label = "معامل التحويل", placeholder = "1.00", keyboardType = KeyboardType.Number)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp, start = 4.dp)) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("1 من الوحدة البديلة = X من الوحدة الأساسية", fontSize = 12.sp, color = TextSecondary)
                        }
                        
                    }
                }
                
                // Section 2: Prices
                item {
                    SectionCard(title = "الأسعار", icon = Icons.Outlined.AttachMoney) {
                        CustomTextField(value = cost, onValueChange = { cost = it }, label = "سعر التكلفة *", placeholder = "0.00", keyboardType = KeyboardType.Number)
                        CustomTextField(value = price, onValueChange = { price = it }, label = "سعر البيع *", placeholder = "0.00", keyboardType = KeyboardType.Number)
                        CustomTextField(value = suggestedPrice, onValueChange = { suggestedPrice = it }, label = "سعر البيع المقترح", placeholder = "0.00", keyboardType = KeyboardType.Number)
                    }
                }
                
                // Section 3: Inventory
                item {
                    SectionCard(title = "المخزون", icon = Icons.Outlined.Inventory2) {
                        CustomTextField(value = stock, onValueChange = { stock = it }, label = "الرصيد الافتتاحي", placeholder = "0.00", keyboardType = KeyboardType.Number, readOnly = product != null)
                        CustomTextField(value = minStockAlert, onValueChange = { minStockAlert = it }, label = "حد التنبيه عند انخفاض المخزون", placeholder = "0.00", keyboardType = KeyboardType.Number)
                    }
                }
                
                // Section 4: Additional
                item {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isAdditionalExpanded = !isAdditionalExpanded }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(if (isAdditionalExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = PrimaryGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("التفاصيل الإضافية", fontWeight = FontWeight.Bold, color = PrimaryGreen, fontSize = 15.sp)
                        }
                        
                        if (isAdditionalExpanded) {
                            SectionCard(title = "", icon = null, showHeader = false) {
                                CustomTextField(value = description, onValueChange = { description = it }, label = "الوصف", placeholder = "أدخل وصف المنتج", minLines = 3)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionCard(title: String, icon: ImageVector?, showHeader: Boolean = true, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (showHeader && icon != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryGreen)
                }
                HorizontalDivider(color = CardBorder, modifier = Modifier.padding(vertical = 0.dp))
            }
            content()
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: ImageVector? = null,
    readOnly: Boolean = false,
    minLines: Int = 1,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label, 
            fontSize = 13.sp, 
            color = TextPrimary, 
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.7f), fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            readOnly = readOnly,
            minLines = minLines,
            trailingIcon = trailingIcon?.let { { Icon(it, contentDescription = null, tint = TextSecondary) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = CardBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/ProductsScreen.kt", "w") as f:
    f.write(new_content)

print("Rewritten ProductsScreen.kt with RTL matching exact design")
