import re

with open("app/src/main/java/com/example/ui/screens/ProductsScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    'var size by remember { mutableStateOf(productToEdit?.size ?: "") }',
    'var size by remember { mutableStateOf(productToEdit?.size ?: "") }\n    var expiryDateStr by remember { mutableStateOf(productToEdit?.expiryDate?.toString() ?: "") }'
)

content = content.replace(
    'size = size.takeIf { it.isNotBlank() }',
    'size = size.takeIf { it.isNotBlank() },\n                                    expiryDate = expiryDateStr.toLongOrNull()'
)

content = content.replace(
    'size = size.takeIf { it.isNotBlank() },\n                                    sku = sku.takeIf { it.isNotBlank() }',
    'size = size.takeIf { it.isNotBlank() },\n                                    expiryDate = expiryDateStr.toLongOrNull(),\n                                    sku = sku.takeIf { it.isNotBlank() }'
)

content = content.replace(
    'CustomTextFieldProduct("المقاس / الحجم", size, { size = it }, modifier = Modifier.weight(1f))',
    'CustomTextFieldProduct("المقاس / الحجم", size, { size = it }, modifier = Modifier.weight(1f))\n                                }\n                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {\n                                    CustomTextFieldProduct("تاريخ الانتهاء (اختياري)", expiryDateStr, { expiryDateStr = it }, modifier = Modifier.weight(1f))'
)

with open("app/src/main/java/com/example/ui/screens/ProductsScreen.kt", "w") as f:
    f.write(content)
print("Done")
