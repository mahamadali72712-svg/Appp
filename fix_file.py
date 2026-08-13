with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "r") as f:
    text = f.read()

# Let's completely remove the showQuickAddProductDialog block from PurchasesScreen and append the dialog at the end
start = text.find("    if (showQuickAddProductDialog) {")
if start != -1:
    end = text.find("@Composable\nfun LuxuryPurchaseTopBar", start)
    if end != -1:
        # replace everything in between with just a closing brace for the main function
        text = text[:start] + "}\n\n" + text[end:]

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "w") as f:
    f.write(text)
