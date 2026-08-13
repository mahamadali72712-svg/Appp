import re

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "r") as f:
    content = f.read()

# Add a state for editing an item
edit_state_code = """
    var showProductSelection by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<PurchaseInvoiceItem?>(null) }
"""

content = content.replace("var showProductSelection by remember { mutableStateOf(false) }", edit_state_code)

# Add clickable to the Row for editing
row_code = """Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                )"""
row_code_new = """Row(
                                    modifier = Modifier.fillMaxWidth().clickable { itemToEdit = item }.padding(horizontal = 12.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                )"""
content = content.replace(row_code, row_code_new)

# Add the dialog at the end of AddPurchaseInvoiceContent
dialog_code = """
    if (showProductSelection) {
        ProductSelectionDialog(
            viewModel = viewModel,
            onDismiss = { showProductSelection = false },
            onProductSelected = { product ->
                val existing = selectedItems.find { it.productId == product.id }
                if (existing != null) {
                    val index = selectedItems.indexOf(existing)
                    selectedItems[index] = existing.copy(
                        quantity = existing.quantity + 1,
                        lineTotal = (existing.quantity + 1) * existing.unitCost
                    )
                } else {
                    selectedItems.add(
                        PurchaseInvoiceItem(
                            invoiceId = "", // assigned at save
                            productId = product.id,
                            quantity = 1.0,
                            unitCost = product.costPrice,
                            lineTotal = product.costPrice
                        )
                    )
                }
                showProductSelection = false
            }
        )
    }
    
    itemToEdit?.let { item ->
        EditItemDialog(
            item = item,
            productName = viewModel.allProducts.collectAsState(initial = emptyList()).value.find { it.id == item.productId }?.name ?: "منتج",
            onDismiss = { itemToEdit = null },
            onSave = { updatedItem ->
                val index = selectedItems.indexOf(item)
                if (index != -1) {
                    selectedItems[index] = updatedItem
                }
                itemToEdit = null
            }
        )
    }
"""
# Replace the existing product selection dialog block
content = re.sub(r'if \(showProductSelection\) \{[\s\S]*?showProductSelection = false\n            \}\n        \)\n    \}', dialog_code, content)


with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "w") as f:
    f.write(content)

print("Updated with edit item capability.")
