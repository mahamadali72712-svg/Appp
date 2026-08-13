import re

with open("app/src/main/java/com/example/ui/screens/ProductsScreen.kt", "r") as f:
    content = f.read()

# We will generate a new content for the entire file.

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.Product
import com.example.data.local.ProductCategory
import com.example.ui.viewmodels.StoreViewModel
import com.example.utils.formatCurrency
import kotlinx.coroutines.launch

// Custom Colors based on the image
private val ScreenBg = Color(0xFFF7F8FA)
private val PrimaryGreen = Color(0xFF137B47)
private val DarkGreen = Color(0xFF0F5A37)
private val CardBorder = Color(0xFFE5E7EB)
private val TextPrimary = Color(0xFF1E1E1E)
private val TextSecondary = Color(0xFF6B7280)
private val WarningOrange = Color(0xFFE57A00)
private val DangerRed = Color(0xFFD63232)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: StoreViewModel, navController: androidx.navigation.NavController) {
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
    
    val currentList = allProducts + archivedProducts // Showing all or filtering based on logic? Image shows active products.
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
                        title = { Text("المنتجات", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                        navigationIcon = {
                            IconButton(onClick = { /* Open Drawer */ }) {
                                Icon(Icons.Default.Menu, contentDescription = "القائمة")
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* Search Action */ }) {
                                Icon(Icons.Outlined.Search, contentDescription = "بحث")
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
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("بحث بالاسم أو الباركود أو SKU", fontSize = 13.sp) },
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
                                .size(56.dp)
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
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إضافة منتج جديد", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { /* Sort Action */ }) {
                            Text("الأحدث", fontSize = 14.sp, color = TextPrimary)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            Icon(Icons.Default.ImportExport, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
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

@Composable
fun SummaryCard(modifier: Modifier = Modifier, title: String, count: String, icon: ImageVector, isPrimary: Boolean) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isPrimary) DarkGreen else Color.White),
        border = if (isPrimary) null else BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title, 
                color = if (isPrimary) Color.White else TextSecondary,
                fontSize = 11.sp,
                maxLines = 1
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = count,
                    color = if (isPrimary) Color.White else TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    icon, 
                    contentDescription = null, 
                    tint = if (isPrimary) Color.White else TextSecondary,
                    modifier = Modifier.size(20.dp)
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Right Side: Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ScreenBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Image, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(32.dp))
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
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val codeStr = listOfNotNull(product.barcode, product.code.takeIf { !it.isNullOrBlank() }).joinToString(" | ")
                    if (codeStr.isNotEmpty()) {
                        Text(codeStr, fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Text(categoryName, fontSize = 11.sp, color = TextSecondary)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("سعر البيع", fontSize = 10.sp, color = TextSecondary)
                Text(
                    text = "${product.suggestedPrice.formatCurrency().replace("ر.س", "").trim()} ريال",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
            
            // Left Side: Actions & Stock
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.height(90.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
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
                        fontSize = 13.sp,
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
    var conversionFactor by remember { mutableStateOf(product?.conversionFactor?.toString() ?: "1.0") }
    
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
                title = { Text(if (product == null) "إضافة منتج جديد" else "تعديل المنتج", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ScreenBg)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حفظ المنتج", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
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
                    SectionCard(title = "المعلومات الأساسية", icon = Icons.Outlined.Receipt) {
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
                                trailingIcon = Icons.Default.ArrowDropDown,
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
                        
                        if (altUnit.isNotBlank()) {
                            CustomTextField(value = conversionFactor, onValueChange = { conversionFactor = it }, label = "معامل التحويل", placeholder = "1.0", keyboardType = KeyboardType.Number)
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp, start = 4.dp)) {
                                Icon(Icons.Outlined.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("1 من $altUnit = $conversionFactor من $baseUnit", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
                
                // Section 2: Prices
                item {
                    SectionCard(title = "الأسعار", icon = Icons.Outlined.Paid) {
                        CustomTextField(value = cost, onValueChange = { cost = it }, label = "سعر التكلفة *", placeholder = "0.00", keyboardType = KeyboardType.Number)
                        CustomTextField(value = price, onValueChange = { price = it }, label = "سعر البيع *", placeholder = "0.00", keyboardType = KeyboardType.Number)
                        CustomTextField(value = suggestedPrice, onValueChange = { suggestedPrice = it }, label = "سعر البيع المقترح", placeholder = "0.00", keyboardType = KeyboardType.Number)
                    }
                }
                
                // Section 3: Inventory
                item {
                    SectionCard(title = "المخزون", icon = Icons.Outlined.Inventory) {
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("التفاصيل الإضافية", fontWeight = FontWeight.Bold, color = PrimaryGreen, fontSize = 15.sp)
                            Icon(if (isAdditionalExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = PrimaryGreen)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showHeader && icon != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryGreen)
                }
                HorizontalDivider(color = CardBorder, modifier = Modifier.padding(vertical = 4.dp))
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
        Text(label, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.padding(bottom = 6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextSecondary, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            readOnly = readOnly,
            minLines = minLines,
            trailingIcon = trailingIcon?.let { { Icon(it, contentDescription = null, tint = TextSecondary) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = CardBorder,
                focusedContainerColor = Color(0xFFFAFAFA),
                unfocusedContainerColor = Color(0xFFFAFAFA)
            )
        )
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/ProductsScreen.kt", "w") as f:
    f.write(new_content)

print("Rewritten ProductsScreen.kt")
