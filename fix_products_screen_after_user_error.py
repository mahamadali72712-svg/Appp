import re

with open("app/src/main/java/com/example/ui/screens/ProductsScreen.kt", "r") as f:
    content = f.read()

start_idx = content.find("    if (showAddDialog) {")
end_idx = content.find("@Composable\nfun LuxuryTopBar(")

if start_idx == -1 or end_idx == -1:
    print("Could not find boundaries")
    exit(1)

new_call = """    var showAdjustDialog by remember { mutableStateOf<String?>(null) }
    
    if (showAddDialog) {
        ProductFormDialog(
            product = productToEdit,
            categories = categories,
            onDismiss = { showAddDialog = false },
            onSave = { name, cost, price, stock, categoryId, minStockAlert, desc, color, size, sku, barcode, baseUnit, altUnit, conversionFactor ->
                if (productToEdit == null) {
                    viewModel.addProduct(
                        name = name,
                        cost = cost,
                        suggestedPrice = price,
                        stock = stock,
                        categoryId = categoryId,
                        minStockAlert = minStockAlert,
                        description = desc,
                        color = color,
                        size = size,
                        sku = sku,
                        barcode = barcode,
                        baseUnit = baseUnit,
                        altUnit = altUnit,
                        conversionFactor = conversionFactor,
                        onSuccess = {
                            showAddDialog = false
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
                        color = color,
                        size = size,
                        code = sku,
                        barcode = barcode,
                        baseUnit = baseUnit,
                        altUnit = altUnit,
                        conversionFactor = conversionFactor
                        // stockQuantity is not updated directly here!
                    )
                    viewModel.updateProduct(
                        product = updated,
                        onSuccess = {
                            showAddDialog = false
                            Toast.makeText(context, "تم تحديث المنتج", Toast.LENGTH_SHORT).show()
                        },
                        onError = { errorMsg ->
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            },
            onAdjustStock = { prodId ->
                showAddDialog = false
                showAdjustDialog = prodId
            }
        )
    }

    if (showAdjustDialog != null) {
        var adjustAmount by remember { mutableStateOf("") }
        var adjustNote by remember { mutableStateOf("تسوية جرد") }
        val prodId = showAdjustDialog!!
        val prod = allProducts.find { it.id == prodId } ?: archivedProducts.find { it.id == prodId }
        AlertDialog(
            onDismissRequest = { showAdjustDialog = null },
            title = { Text("تسوية المخزون: ${prod?.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("الكمية الحالية: ${prod?.stockQuantity}")
                    OutlinedTextField(
                        value = adjustAmount,
                        onValueChange = { adjustAmount = it },
                        label = { Text("مقدار التعديل (مثال: 5 أو -3)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = adjustNote,
                        onValueChange = { adjustNote = it },
                        label = { Text("سبب التسوية") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val amt = adjustAmount.toDoubleOrNull() ?: 0.0
                    if (amt != 0.0) {
                        viewModel.adjustStock(prodId, amt, adjustNote) {
                            showAdjustDialog = null
                            Toast.makeText(context, "تمت التسوية بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) { Text("حفظ") }
            },
            dismissButton = {
                TextButton(onClick = { showAdjustDialog = null }) { Text("إلغاء") }
            }
        )
    }
}

"""

new_content = content[:start_idx] + new_call + content[end_idx:]

with open("app/src/main/java/com/example/ui/screens/ProductsScreen.kt", "w") as f:
    f.write(new_content)

print("Fixed ProductsScreen!")
