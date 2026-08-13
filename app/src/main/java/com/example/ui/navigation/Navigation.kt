package com.example.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.CashboxScreen
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.PurchasesScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SalesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SuppliersScreen
import com.example.ui.viewmodels.StoreViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.getThemeOption
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: @Composable () -> Unit) {
    object Dashboard : Screen("dashboard", "الرئيسية", { Icon(Icons.Filled.Home, contentDescription = "Home") })
    object Products : Screen("products", "المنتجات", { Icon(Icons.Filled.List, contentDescription = "Products") })
    object Sales : Screen("sales", "المبيعات", { Icon(Icons.Filled.ShoppingCart, contentDescription = "Sales") })
    object Reports : Screen("reports", "التقارير", { Icon(Icons.Filled.DateRange, contentDescription = "Reports") })
    object Settings : Screen("settings", "الإعدادات", { Icon(Icons.Filled.Settings, contentDescription = "Settings") })
    
    object Purchases : Screen("purchases", "المشتريات", { Icon(Icons.Filled.AddCircle, contentDescription = "Purchases") })
    object Expenses : Screen("expenses", "المصروفات", { Icon(Icons.Filled.Settings, contentDescription = "Expenses") })
    object Suppliers : Screen("suppliers", "الموردين", { Icon(Icons.Filled.Person, contentDescription = "Suppliers") })
    object Customers : Screen("customers", "العملاء", { Icon(Icons.Filled.Person, contentDescription = "Customers") })
    object Cashbox : Screen("cashbox", "الصندوق", { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cashbox") })
    object SalesHistory : Screen("sales_history", "سجل المبيعات", { Icon(Icons.Filled.List, contentDescription = "Sales History") })
    object PurchasesHistory : Screen("purchases_history", "سجل المشتريات", { Icon(Icons.Filled.List, contentDescription = "Purchases History") })
    object LowStock : Screen("low_stock", "نواقص المخزون", { Icon(Icons.Filled.List, contentDescription = "Low Stock") })
}

val MainBgGradient = Brush.verticalGradient(
    0.0f to Color(0xFF1E3C72),
    0.4f to Color(0xFF2A5298),
    0.7f to Color(0xFF2980B9),
    1.0f to Color(0xFF1E3C72)
)

@Composable
fun CustomGlassBottomNavBar(
    currentRoute: String?,
    bottomBarItems: List<Screen>,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x2BFFFFFF))
            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(24.dp))
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomBarItems.forEach { screen ->
                val isSelected = currentRoute == screen.route
                val iconVector = when (screen.route) {
                    "dashboard" -> Icons.Rounded.Home
                    "products" -> Icons.Rounded.List
                    "sales" -> Icons.Rounded.ShoppingCart
                    "reports" -> Icons.Rounded.DateRange
                    "settings" -> Icons.Rounded.Settings
                    else -> Icons.Rounded.Home
                }

                if (isSelected) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFA855F7), Color(0xFF8B5CF6))
                                )
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigate(screen.route) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = screen.title,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = screen.title,
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigate(screen.route) }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = screen.title,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = screen.title,
                            color = Color(0xFF9CA3AF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: StoreViewModel) {
    val navController = rememberNavController()
    val bottomBarItems = listOf(Screen.Dashboard, Screen.Products, Screen.Sales, Screen.Reports, Screen.Settings)
    val mainRoutes = bottomBarItems.map { it.route }

    val selectedThemeId by viewModel.selectedThemeId.collectAsStateWithLifecycle()
    val currentTheme = getThemeOption(selectedThemeId)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            com.example.ui.components.AppSideDrawerContent(
                viewModel = viewModel,
                navController = navController,
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize().background(currentTheme.gradientBrush)) {
            // Glow at top left
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.TopStart)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(currentTheme.accentGlow, Color.Transparent),
                            radius = 900f,
                            center = androidx.compose.ui.geometry.Offset(0f, 200f)
                        )
                    )
            )
            
            // Glow at center right
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.CenterEnd)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(currentTheme.secondaryColor.copy(alpha = 0.35f), Color.Transparent),
                            radius = 800f,
                            center = androidx.compose.ui.geometry.Offset(1000f, 500f)
                        )
                    )
            )

            // TOP RIGHT CLEAN MENU BUTTON TO OPEN SIDE DRAWER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 12.dp, end = 16.dp)
                    .zIndex(1000f),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x55111827))
                        .border(1.dp, currentTheme.primaryColor.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "القائمة الجانبية",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
                    
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    
                    if (currentRoute in mainRoutes) {
                        CustomGlassBottomNavBar(
                            currentRoute = currentRoute,
                            bottomBarItems = bottomBarItems,
                            onNavigate = { route ->
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Dashboard.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Dashboard.route) { 
                        DashboardScreen(
                            viewModel = viewModel, 
                            navController = navController
                        )
                    }
                    composable(Screen.Products.route) { ProductsScreen(viewModel, navController) }
                    composable(Screen.Sales.route) { SalesScreen(viewModel, navController) }
                    composable(Screen.Purchases.route) { PurchasesScreen(viewModel, navController) }
                    composable(Screen.Expenses.route) { ExpensesScreen(viewModel, navController) }
                    composable(Screen.Reports.route) { ReportsScreen(viewModel, navController) }
                    composable(Screen.Settings.route) { SettingsScreen(viewModel, navController) }
                    composable(Screen.Suppliers.route) { SuppliersScreen(viewModel, navController) }
                    composable(Screen.Customers.route) { CustomersScreen(viewModel, navController) }
                    composable(Screen.Cashbox.route) { CashboxScreen(viewModel, navController) }
                    composable(Screen.SalesHistory.route) { com.example.ui.screens.SalesHistoryScreen(viewModel, navController) }
                    composable(Screen.PurchasesHistory.route) { com.example.ui.screens.PurchasesHistoryScreen(viewModel, navController) }
                    composable(Screen.LowStock.route) { com.example.ui.screens.LowStockScreen(viewModel, navController) }
                    
                    composable("appearance") { com.example.ui.screens.AppearanceScreen(viewModel, navController) }
                    composable("currencies") { com.example.ui.screens.CurrenciesScreen(viewModel, navController) }
                    composable("notifications") { com.example.ui.screens.NotificationsScreen(viewModel, navController) }
                    composable("sms_messaging") { com.example.ui.screens.SmsMessagingScreen(viewModel, navController) }
                    composable("whatsapp_messaging") { com.example.ui.screens.WhatsappMessagingScreen(viewModel, navController) }
                    composable("quotes") { com.example.ui.screens.QuotesScreen(viewModel, navController) }
                    composable("orders") { com.example.ui.screens.OrdersScreen(viewModel, navController) }
                    composable("trash") { com.example.ui.screens.TrashScreen(viewModel, navController) }
                    
                    composable("advanced_settings") { com.example.ui.settings.AdvancedSettingsScreen(navController) }
                    composable("settings_personal_data") { com.example.ui.settings.PersonalDataScreen(navController) }
                    composable("settings_print") { com.example.ui.settings.PrintSettingsScreen(navController) }
                    composable("settings_vouchers") { com.example.ui.settings.VoucherSettingsScreen(navController) }
                    composable("settings_invoices") { com.example.ui.settings.InvoiceSettingsScreen(navController) }
                    composable("settings_whatsapp") { com.example.ui.settings.WhatsAppSettingsScreen(navController) }
                    composable("settings_other") { com.example.ui.settings.OtherSettingsScreen(navController) }
                    
                    composable("import_products_excel") { com.example.ui.screens.ImportProductsExcelScreen(viewModel, navController) }
                    composable("warehouses") { com.example.ui.screens.WarehousesScreen(viewModel, navController) }
                    composable("stock_transfers") { com.example.ui.screens.StockTransfersScreen(viewModel, navController) }
                    composable("chart_of_accounts") { com.example.ui.screens.ChartOfAccountsScreen(viewModel, navController) }
                    
                    composable("backup_phone") { com.example.ui.screens.BackupManagementScreen("phone", viewModel, navController) }
                    composable("backup_telegram") { com.example.ui.screens.BackupManagementScreen("telegram", viewModel, navController) }
                    composable("backup_restore") { com.example.ui.screens.BackupManagementScreen("restore", viewModel, navController) }
                    composable("google_drive") { com.example.ui.screens.BackupManagementScreen("gdrive", viewModel, navController) }
                    
                    composable("supplier_detail/{supplierId}") { backStackEntry ->
                        val supplierId = backStackEntry.arguments?.getString("supplierId") ?: ""
                        com.example.ui.screens.SupplierDetailScreen(supplierId, viewModel, navController)
                    }
                    composable("customer_detail/{customerId}") { backStackEntry ->
                        val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
                        com.example.ui.screens.CustomerDetailScreen(customerId, viewModel, navController)
                    }
                }
            }
        }
    }
}
