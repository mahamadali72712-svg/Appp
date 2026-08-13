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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.Product
import com.example.data.local.ProductCategory
import com.example.ui.viewmodels.StoreViewModel
import kotlinx.coroutines.launch

private val ScreenBg = Color(0xFFF5F6F8)
private val PrimaryGreen = Color(0xFF106B3F)
private val CardBorder = Color(0xFFE5E7EB)
private val TextPrimary = Color(0xFF1F2937)
private val TextSecondary = Color(0xFF6B7280)
private val DangerRed = Color(0xFFEF4444)

enum class ProductScreenView {
    LIST, ADD, EDIT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: StoreViewModel, navController: androidx.navigation.NavController) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        var currentView by remember { mutableStateOf(ProductScreenView.LIST) }
        var selectedProduct by remember { mutableStateOf<Product?>(null) }

        BackHandler(enabled = currentView != ProductScreenView.LIST) {
            currentView = ProductScreenView.LIST
        }

        AnimatedContent(
            targetState = currentView, 
            label = "ProductsScreenTransition",
            transitionSpec = {
                if (targetState != ProductScreenView.LIST && initialState == ProductScreenView.LIST) {
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                } else {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                }
            }
        ) { view ->
            when (view) {
                ProductScreenView.LIST -> ProductsListContent(
                    viewModel = viewModel,
                    onNavigateToAdd = {
                        selectedProduct = null
                        currentView = ProductScreenView.ADD
                    },
                    onProductClick = { product ->
                        selectedProduct = product
                        currentView = ProductScreenView.EDIT
                    }
                )
                ProductScreenView.ADD -> AddEditProductContent(
                    viewModel = viewModel,
                    productToEdit = null,
                    onBack = { currentView = ProductScreenView.LIST }
                )
                ProductScreenView.EDIT -> AddEditProductContent(
                    viewModel = viewModel,
                    productToEdit = selectedProduct,
                    onBack = { currentView = ProductScreenView.LIST }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsListContent(
    viewModel: StoreViewModel,
    onNavigateToAdd: () -> Unit,
    onProductClick: (Product) -> Unit
) {
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle(initialValue = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredProducts = allProducts.filter {
        it.status != "INACTIVE" && (it.name.contains(searchQuery, true) || 
        it.code?.contains(searchQuery, true) == true || 
        it.barcode?.contains(searchQuery, true) == true)
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("المنتجات", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary) },
                navigationIcon = {
                    IconButton(
                        onClick = { /* Menu */ },
                        modifier = Modifier.padding(start = 16.dp).background(Color.White, CircleShape).size(40.dp)
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
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("بحث بالاسم، الكود، الباركود", fontSize = 13.sp, color = TextSecondary) },
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
                        Icon(Icons.Outlined.FilterAlt, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تصفية", fontSize = 13.sp, color = TextPrimary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductListItem(product, onClick = { onProductClick(product) })
                }
            }
        }
    }
}

@Composable
fun ProductListItem(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(ScreenBg, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Image, contentDescription = null, tint = TextSecondary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(product.barcode ?: product.code ?: "بدون كود", fontSize = 12.sp, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${product.salePrice} ريال", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryGreen)
                val stockColor = if (product.stockQuantity <= product.minStockAlert) DangerRed else TextSecondary
                Text("${product.stockQuantity} ${product.baseUnit}", fontSize = 12.sp, color = stockColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductContent(
    viewModel: StoreViewModel,
    productToEdit: Product?,
    onBack: () -> Unit
) {
    val categories by viewModel.allCategories.collectAsStateWithLifecycle(initialValue = emptyList())
    val context = LocalContext.current
    val isEdit = productToEdit != null

    var name by remember { mutableStateOf(productToEdit?.name ?: "") }
    var sku by remember { mutableStateOf(productToEdit?.code ?: "") }
    var barcode by remember { mutableStateOf(productToEdit?.barcode ?: "") }
    var categoryId by remember { mutableStateOf(productToEdit?.categoryId ?: "") }
    var baseUnit by remember { mutableStateOf(productToEdit?.baseUnit ?: "حبة") }
    var altUnit by remember { mutableStateOf(productToEdit?.altUnit ?: "") }
    var conversionFactor by remember { mutableStateOf(productToEdit?.conversionFactor?.toString() ?: "1.0") }

    var costPrice by remember { mutableStateOf(productToEdit?.costPrice?.toString() ?: "") }
    var salePrice by remember { mutableStateOf(productToEdit?.salePrice?.toString() ?: "") }
    var suggestedPrice by remember { mutableStateOf(productToEdit?.suggestedPrice?.toString() ?: "") }

    var openingBalance by remember { mutableStateOf(if (!isEdit) "" else productToEdit?.stockQuantity?.toString() ?: "0.0") }
    var minStockAlert by remember { mutableStateOf(productToEdit?.minStockAlert?.toString() ?: "") }

    var description by remember { mutableStateOf(productToEdit?.description ?: "") }
    var color by remember { mutableStateOf(productToEdit?.color ?: "") }
    var size by remember { mutableStateOf(productToEdit?.size ?: "") }
    var expiryDateStr by remember { mutableStateOf(productToEdit?.expiryDate?.toString() ?: "") }
    
    var showAdditionalDetails by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isEdit) "تعديل المنتج" else "إضافة منتج", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "رجوع", tint = TextPrimary)
                    }
                },
                actions = {
                    if (isEdit) {
                        IconButton(onClick = {
                            viewModel.archiveProduct(productToEdit!!.id)
                            onBack()
                        }) {
                            Icon(Icons.Outlined.DeleteOutline, contentDescription = "تعطيل", tint = DangerRed)
                        }
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
                    errorMessage?.let {
                        Text(it, color = DangerRed, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Button(
                        onClick = {
                            if (name.isBlank()) { errorMessage = "يرجى إدخال اسم المنتج"; return@Button }
                            if (baseUnit.isBlank()) { errorMessage = "يرجى إدخال الوحدة الأساسية"; return@Button }
                            if (costPrice.toDoubleOrNull() == null) { errorMessage = "سعر التكلفة غير صالح"; return@Button }
                            if (salePrice.toDoubleOrNull() == null) { errorMessage = "سعر البيع غير صالح"; return@Button }
                            
                            val cost = costPrice.toDoubleOrNull() ?: 0.0
                            val sale = salePrice.toDoubleOrNull() ?: 0.0
                            val suggested = suggestedPrice.toDoubleOrNull() ?: 0.0
                            val stock = openingBalance.toDoubleOrNull() ?: 0.0
                            val minStock = minStockAlert.toDoubleOrNull() ?: 0.0
                            val conv = conversionFactor.toDoubleOrNull() ?: 1.0

                            if (isEdit) {
                                val updated = productToEdit!!.copy(
                                    name = name,
                                    code = sku.takeIf { it.isNotBlank() },
                                    barcode = barcode.takeIf { it.isNotBlank() },
                                    categoryId = categoryId.takeIf { it.isNotBlank() },
                                    baseUnit = baseUnit,
                                    altUnit = altUnit.takeIf { it.isNotBlank() },
                                    conversionFactor = conv,
                                    costPrice = cost,
                                    salePrice = sale,
                                    suggestedPrice = suggested,
                                    minStockAlert = minStock,
                                    description = description.takeIf { it.isNotBlank() },
                                    color = color.takeIf { it.isNotBlank() },
                                    size = size.takeIf { it.isNotBlank() },
                                    expiryDate = expiryDateStr.toLongOrNull()
                                )
                                viewModel.updateProduct(
                                    product = updated,
                                    onSuccess = {
                                        Toast.makeText(context, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show()
                                        onBack()
                                    },
                                    onError = { errorMessage = it }
                                )
                            } else {
                                viewModel.addProduct(
                                    name = name,
                                    cost = cost,
                                    salePrice = sale,
                                    suggestedPrice = suggested,
                                    stock = stock,
                                    categoryId = categoryId.takeIf { it.isNotBlank() },
                                    minStockAlert = minStock,
                                    description = description.takeIf { it.isNotBlank() },
                                    color = color.takeIf { it.isNotBlank() },
                                    size = size.takeIf { it.isNotBlank() },
                                    expiryDate = expiryDateStr.toLongOrNull(),
                                    sku = sku.takeIf { it.isNotBlank() },
                                    barcode = barcode.takeIf { it.isNotBlank() },
                                    baseUnit = baseUnit,
                                    altUnit = altUnit.takeIf { it.isNotBlank() },
                                    conversionFactor = conv,
                                    onSuccess = {
                                        Toast.makeText(context, "تمت الإضافة بنجاح", Toast.LENGTH_SHORT).show()
                                        onBack()
                                    },
                                    onError = { errorMessage = it }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isEdit) "حفظ التعديلات" else "إضافة المنتج", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
            
            item {
                ProductSectionCard("المعلومات الأساسية", Icons.Outlined.Info) {
                    CustomTextFieldProduct("اسم المنتج *", name, { name = it; errorMessage = null })
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CustomTextFieldProduct("SKU (اختياري)", sku, { sku = it; errorMessage = null }, modifier = Modifier.weight(1f))
                        CustomTextFieldProduct("الباركود", barcode, { barcode = it; errorMessage = null }, modifier = Modifier.weight(1f))
                    }
                    
                    var expandedCat by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expandedCat, onExpandedChange = { expandedCat = it }) {
                        val catName = categories.find { it.id == categoryId }?.name ?: "بدون تصنيف"
                        OutlinedTextField(
                            value = catName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("التصنيف", fontSize = 12.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = PrimaryGreen)
                        )
                        ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }, modifier = Modifier.background(Color.White)) {
                            DropdownMenuItem(text = { Text("بدون تصنيف") }, onClick = { categoryId = ""; expandedCat = false })
                            categories.forEach { c ->
                                DropdownMenuItem(text = { Text(c.name) }, onClick = { categoryId = c.id; expandedCat = false })
                            }
                        }
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CustomTextFieldProduct("الوحدة الأساسية *", baseUnit, { baseUnit = it }, modifier = Modifier.weight(1f))
                        CustomTextFieldProduct("الوحدة البديلة", altUnit, { altUnit = it }, modifier = Modifier.weight(1f))
                    }
                    if (altUnit.isNotBlank()) {
                        CustomTextFieldProduct("معامل التحويل (كم $baseUnit في $altUnit؟)", conversionFactor, { conversionFactor = it }, isNumeric = true)
                    }
                }
            }
            
            item {
                ProductSectionCard("الأسعار", Icons.Outlined.AttachMoney) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CustomTextFieldProduct("سعر التكلفة *", costPrice, { costPrice = it }, isNumeric = true, modifier = Modifier.weight(1f))
                        CustomTextFieldProduct("سعر البيع *", salePrice, { salePrice = it }, isNumeric = true, modifier = Modifier.weight(1f))
                    }
                    CustomTextFieldProduct("سعر البيع المقترح", suggestedPrice, { suggestedPrice = it }, isNumeric = true)
                }
            }
            
            item {
                ProductSectionCard("المخزون", Icons.Outlined.Inventory2) {
                    if (isEdit) {
                        CustomTextFieldProduct("الرصيد الحالي (للتعديل استخدم تسوية المخزون)", openingBalance, {}, readOnly = true)
                    } else {
                        CustomTextFieldProduct("الرصيد الافتتاحي", openingBalance, { openingBalance = it }, isNumeric = true)
                    }
                    CustomTextFieldProduct("حد التنبيه", minStockAlert, { minStockAlert = it }, isNumeric = true)
                }
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showAdditionalDetails = !showAdditionalDetails }.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.MoreHoriz, contentDescription = null, tint = PrimaryGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("التفاصيل الإضافية", fontWeight = FontWeight.Bold, color = PrimaryGreen)
                            }
                            Icon(
                                if (showAdditionalDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                        if (showAdditionalDetails) {
                            HorizontalDivider(color = CardBorder)
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                CustomTextFieldProduct("الوصف", description, { description = it })
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    CustomTextFieldProduct("اللون", color, { color = it }, modifier = Modifier.weight(1f))
                                    CustomTextFieldProduct("المقاس / الحجم", size, { size = it }, modifier = Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    CustomTextFieldProduct("تاريخ الانتهاء (اختياري)", expiryDateStr, { expiryDateStr = it }, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductSectionCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryGreen)
            }
            HorizontalDivider(color = CardBorder)
            content()
        }
    }
}

@Composable
fun CustomTextFieldProduct(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isNumeric: Boolean = false,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        keyboardOptions = if (isNumeric) KeyboardOptions(keyboardType = KeyboardType.Decimal) else KeyboardOptions.Default,
        readOnly = readOnly,
        singleLine = true,
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
