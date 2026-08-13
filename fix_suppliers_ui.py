import re

def fix_suppliers(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Remove fixed heights from OutlinedTextField modifiers
    content = content.replace('modifier = Modifier.weight(1f).height(52.dp)', 'modifier = Modifier.weight(1f)')
    content = content.replace('modifier = Modifier.fillMaxWidth().height(48.dp)', 'modifier = Modifier.fillMaxWidth()')
    content = content.replace('modifier = Modifier.height(48.dp).weight(1f)', 'modifier = Modifier.weight(1f)')
    
    # Increase the Box sizes next to search bars to 56.dp to match natural OutlinedTextField height
    content = content.replace('modifier = Modifier.height(52.dp)', 'modifier = Modifier.height(56.dp)')
    content = content.replace('modifier = Modifier.size(48.dp)', 'modifier = Modifier.size(56.dp)')
    content = content.replace('modifier = Modifier.height(48.dp)', 'modifier = Modifier.height(56.dp)')
    
    animated_content_old = 'AnimatedContent(targetState = currentView, label = "SuppliersScreenTransition") { view ->'
    animated_content_new = '''AnimatedContent(
            targetState = currentView, 
            label = "SuppliersScreenTransition",
            transitionSpec = {
                if (targetState != SupplierScreenView.LIST && initialState == SupplierScreenView.LIST) {
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                } else {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                }
            }
        ) { view ->'''
    
    content = content.replace(animated_content_old, animated_content_new)

    with open(filepath, 'w') as f:
        f.write(content)

fix_suppliers("app/src/main/java/com/example/ui/screens/SuppliersScreen.kt")
print("Done")
