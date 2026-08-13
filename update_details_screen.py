import re

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "r") as f:
    code = f.read()

# 1. Update SummaryItem
new_summary_item = """@Composable
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
}"""

summary_pattern = r'@Composable\s*fun SummaryItem[\s\S]*?(?=@Composable\s*fun|\Z)'
code = re.sub(summary_pattern, new_summary_item + "\n\n", code, count=1)

# 2. Update PurchaseInvoiceDetailsContent
new_details_composable = """@OptIn(ExperimentalMaterial3Api::class)
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
}"""

details_pattern = r'@Composable\s*fun PurchaseInvoiceDetailsContent[\s\S]*?(?=@Composable\s*fun EditItemDialog|\Z)'
code = re.sub(details_pattern, new_details_composable + "\n\n", code, count=1)

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "w") as f:
    f.write(code)

print("Updated PurchaseInvoiceDetailsContent & SummaryItem successfully!")
