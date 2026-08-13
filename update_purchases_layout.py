import re

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "r") as f:
    code = f.read()

new_purchases_list_content = """@OptIn(ExperimentalMaterial3Api::class)
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
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Top Row: Invoice Number, Supplier Name, Status Badge, More Vert Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Right side (RTL start): Invoice Number
                Text(
                    text = invoice.invoiceNumber,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Center: Supplier Name
                Text(
                    text = supplierName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Left side (RTL end): Status Badge
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
                
                Spacer(modifier = Modifier.width(6.dp))
                
                // Far Left: Three dots menu
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "المزيد",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Bottom Row: Items Count, Date, Total Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Right side (RTL start): Items count with box icon
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    fontSize = 15.sp,
                    color = TextPrimary
                )
            }
        }
    }
}"""

pattern = r'@OptIn\(ExperimentalMaterial3Api::class\)\s*@Composable\s*fun PurchasesListContent[\s\S]*?(?=@OptIn\(ExperimentalMaterial3Api::class\)\s*@Composable\s*fun AddPurchaseInvoiceContent)'

code = re.sub(pattern, new_purchases_list_content + "\n\n", code)

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "w") as f:
    f.write(code)

print("Replaced successfully!")
