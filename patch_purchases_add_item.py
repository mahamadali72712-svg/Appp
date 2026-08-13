import re

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "r") as f:
    content = f.read()

# Replace QuickAddProductForPurchaseDialog with ProductFormDialog call
start = content.find("    if (showQuickAddProductDialog) {")
end = content.find("    }\n}\n", start) + 5

if start == -1 or end == -1:
    print("Could not find dialog call")
    exit(1)

new_call = """    if (showQuickAddProductDialog) {
        ProductFormDialog(
            product = null,
            categories = categories,
            onDismiss = { showQuickAddProductDialog = false },
            onSave = { name, cost, price, stock, categoryId, minStockAlert, desc, color, size, sku, barcode, baseUnit, altUnit, conversionFactor ->
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
                    onSuccessProduct = { newProduct ->
                        invoiceItems.add(
                            PurchaseItemUiModel(
                                product = newProduct,
                                quantity = stock,
                                unitCost = cost,
                                lineTotal = stock * cost
                            )
                        )
                        Toast.makeText(context, "تم تسجيل المنتج (${newProduct.name}) وإضافته للفاتورة", Toast.LENGTH_SHORT).show()
                    }
                )
                showQuickAddProductDialog = false
                showAddProductSheet = false
            }
        )
    }
}
"""

new_content = content[:start] + new_call + content[end:]

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "w") as f:
    f.write(new_content)

print("Patched PurchasesScreen.kt")
