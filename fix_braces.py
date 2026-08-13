import re

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "r") as f:
    content = f.read()

# Replace any sequence of 3 or more '}' at the start of a line or alone with just '}'
content = re.sub(r'\}\}\}\}+', '}', content)
content = re.sub(r'\}\}\}', '}', content)

with open("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt", "w") as f:
    f.write(content)
