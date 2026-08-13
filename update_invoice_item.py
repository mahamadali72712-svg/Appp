import re

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "r") as f:
    code = f.read()

new_item_composable = """@Composable
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
}"""

pattern = r'@Composable\s*fun PurchaseInvoiceListItem[\s\S]*?(?=@Composable\s*fun|\Z)'
code = re.sub(pattern, new_item_composable + "\n\n", code, count=1)

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "w") as f:
    f.write(code)

print("Updated PurchaseInvoiceListItem successfully!")
