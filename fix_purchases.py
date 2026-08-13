import re

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "r") as f:
    content = f.read()

content = content.replace("val invoices by viewModel.allPurchases.collectAsStateWithLifecycle(initialValue = emptyList())", "val invoices by viewModel.allPurchaseInvoices.collectAsStateWithLifecycle(initialValue = emptyList())")
content = content.replace("Icons.AutoMirrored.Outlined.Send", "Icons.AutoMirrored.Filled.Send")

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "w") as f:
    f.write(content)

print("Fixed")
