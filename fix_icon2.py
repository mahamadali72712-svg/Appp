with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "r") as f:
    content = f.read()

content = content.replace("Icons.Filled.Send", "Icons.AutoMirrored.Filled.Send")
content = content.replace("Icons.AutoMirrored.Filled.Send", "Icons.Default.Send")

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "w") as f:
    f.write(content)

print("Fixed icon again")
