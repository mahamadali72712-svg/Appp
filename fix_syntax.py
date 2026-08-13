with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "r") as f:
    lines = f.readlines()

# let's just find the `fun PurchasesScreen` and count braces.
# Actually, the problem is likely at the end of `PurchasesScreen` function.
# Let's see the lines around 570-580
for i, line in enumerate(lines):
    if i > 560:
        print(f"{i+1}: {line.strip()}")
