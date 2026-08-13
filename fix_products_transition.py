import re

def fix_products(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    animated_content_old = 'AnimatedContent(targetState = showAddScreen, label = "ScreenTransition") { isAddScreen ->'
    animated_content_new = '''AnimatedContent(
            targetState = showAddScreen, 
            label = "ScreenTransition",
            transitionSpec = {
                if (targetState) {
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                } else {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                }
            }
        ) { isAddScreen ->'''
    
    content = content.replace(animated_content_old, animated_content_new)

    with open(filepath, 'w') as f:
        f.write(content)

fix_products("app/src/main/java/com/example/ui/screens/ProductsScreen.kt")
print("Done")
