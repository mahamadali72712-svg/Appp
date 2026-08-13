import re

def fix_purchases(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Remove fixed heights from OutlinedTextField modifiers
    content = content.replace('modifier = Modifier.weight(1f).height(52.dp)', 'modifier = Modifier.weight(1f)')
    content = content.replace('modifier = Modifier.weight(1f).height(48.dp)', 'modifier = Modifier.weight(1f)')
    content = content.replace('modifier = Modifier.fillMaxWidth().height(48.dp)', 'modifier = Modifier.fillMaxWidth()')
    
    # Increase the Box sizes next to search bars to 56.dp to match natural OutlinedTextField height
    content = content.replace('modifier = Modifier.height(52.dp)', 'modifier = Modifier.height(56.dp)')
    content = content.replace('modifier = Modifier.size(48.dp)', 'modifier = Modifier.size(56.dp)')
    content = content.replace('modifier = Modifier.height(48.dp)', 'modifier = Modifier.height(56.dp)')
    
    # We also need to fix AnimatedContent transition in PurchasesScreen
    # It currently is AnimatedContent(targetState = currentView, label = "PurchasesScreenTransition") { view ->
    
    animated_content_old = 'AnimatedContent(targetState = currentView, label = "PurchasesScreenTransition") { view ->'
    animated_content_new = '''AnimatedContent(
            targetState = currentView, 
            label = "PurchasesScreenTransition",
            transitionSpec = {
                if (targetState != PurchaseScreenView.LIST && initialState == PurchaseScreenView.LIST) {
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                } else {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                }
            }
        ) { view ->'''
    
    content = content.replace(animated_content_old, animated_content_new)
    
    with open(filepath, 'w') as f:
        f.write(content)

def fix_products(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Remove fixed heights from OutlinedTextField modifiers
    content = content.replace('modifier = Modifier.weight(1f).height(52.dp)', 'modifier = Modifier.weight(1f)')
    content = content.replace('modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)', 'modifier = Modifier.fillMaxWidth()')
    
    # Match the Box height to the new natural TextField height
    content = content.replace('modifier = Modifier.height(52.dp)', 'modifier = Modifier.height(56.dp)')

    animated_content_old = 'AnimatedContent(targetState = currentView, label = "ProductsScreenTransition") { view ->'
    animated_content_new = '''AnimatedContent(
            targetState = currentView, 
            label = "ProductsScreenTransition",
            transitionSpec = {
                if (targetState != ProductScreenView.LIST && initialState == ProductScreenView.LIST) {
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                } else {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                }
            }
        ) { view ->'''
    
    content = content.replace(animated_content_old, animated_content_new)

    with open(filepath, 'w') as f:
        f.write(content)

fix_purchases("app/src/main/java/com/example/ui/screens/PurchasesScreen.kt")
fix_products("app/src/main/java/com/example/ui/screens/ProductsScreen.kt")
print("Done")
