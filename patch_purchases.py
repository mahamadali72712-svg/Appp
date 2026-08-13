import re

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "r") as f:
    content = f.read()

content = content.replace("}}}\n", "}\n")

# Replace ProductFormDialog with a simpler QuickAddProductDialog since it's missing.
new_dialog = """
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormDialog(
    product: Product?,
    categories: List<com.example.data.local.ProductCategory>,
    onDismiss: () -> Unit,
    onSave: (String, Double, Double, Double, String?, Double, String?, String?, String?, String, String, String, String?, Double) -> Unit,
    onAdjustStock: ((String) -> Unit)? = null
) {
    // Simple placeholder to fix compilation for PurchasesScreen
    var name by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("إضافة منتج سريع", fontWeight = FontWeight.Bold)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("الاسم") })
                OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("التكلفة") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("البيع") })
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("المخزون") })
                
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("إلغاء") }
                    Button(onClick = {
                        onSave(name, cost.toDoubleOrNull() ?: 0.0, price.toDoubleOrNull() ?: 0.0, stock.toDoubleOrNull() ?: 0.0, null, 0.0, null, null, null, "", "", "حبة", null, 1.0)
                    }) { Text("حفظ") }
                }
            }
        }
    }
}
"""

if "fun ProductFormDialog" not in content:
    content += new_dialog

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "w") as f:
    f.write(content)
