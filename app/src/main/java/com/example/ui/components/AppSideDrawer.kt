package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.navigation.Screen
import com.example.ui.theme.getThemeOption
import com.example.ui.viewmodels.StoreViewModel

@Composable
fun AppSideDrawerContent(
    viewModel: StoreViewModel,
    navController: NavController?,
    onCloseDrawer: () -> Unit
) {
    val selectedThemeId by viewModel.selectedThemeId.collectAsStateWithLifecycle()
    val currentTheme = getThemeOption(selectedThemeId)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(310.dp)
                .background(Color(0xFFF3F4F6)) // Light gray background to make cards pop
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header (App Title)
                item {
                    Text(
                        text = "القائمة الرئيسية",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = currentTheme.primaryColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // ------------------------------------
                // SECTION: المبيعات (Burgundy Red Card)
                // ------------------------------------
                item {
                    DrawerSectionCard(
                        title = "المبيعات",
                        isBurgundy = true
                    ) {
                        DrawerMenuItem(
                            title = "شاشة المبيعات",
                            icon = Icons.Default.PointOfSale,
                            isBurgundy = true,
                            onClick = { onCloseDrawer(); navController?.navigate(Screen.Sales.route) }
                        )
                        DrawerMenuItem(
                            title = "سجل فواتير المبيعات",
                            icon = Icons.Default.Receipt,
                            isBurgundy = true,
                            onClick = { onCloseDrawer(); navController?.navigate(Screen.SalesHistory.route) }
                        )
                    }
                }

                // ------------------------------------
                // SECTION: المخازن (Rest of sections, beautiful curved cards, no data change)
                // ------------------------------------
                item {
                    DrawerSectionCard(title = "المخازن") {
                        DrawerMenuItem(
                            title = "المخازن",
                            icon = Icons.Default.Store,
                            onClick = { onCloseDrawer(); navController?.navigate("warehouses") }
                        )
                        DrawerMenuItem(
                            title = "التحويلات المخزنية",
                            icon = Icons.Default.SyncAlt,
                            onClick = { onCloseDrawer(); navController?.navigate("stock_transfers") }
                        )
                        DrawerMenuItem(
                            title = "الأصناف",
                            icon = Icons.Default.Inventory,
                            onClick = { onCloseDrawer(); navController?.navigate(Screen.Products.route) }
                        )
                        DrawerMenuItem(
                            title = "إستيراد الأصناف من أكسل",
                            icon = Icons.Default.ListAlt,
                            onClick = { onCloseDrawer(); navController?.navigate("import_products_excel") }
                        )
                    }
                }

                // ------------------------------------
                // SECTION: الحسابات
                // ------------------------------------
                item {
                    DrawerSectionCard(title = "الحسابات") {
                        DrawerMenuItem(
                            title = "دليل الحسابات",
                            icon = Icons.Default.RequestQuote,
                            onClick = { onCloseDrawer(); navController?.navigate("chart_of_accounts") }
                        )
                        DrawerMenuItem(
                            title = "العملات",
                            icon = Icons.Default.MonetizationOn,
                            onClick = { onCloseDrawer(); navController?.navigate("currencies") }
                        )
                        DrawerMenuItem(
                            title = "استيراد الحسابات من ملف اكسل",
                            icon = Icons.Default.ListAlt,
                            onClick = { onCloseDrawer() /* TODO */ }
                        )
                    }
                }

                // ------------------------------------
                // SECTION: النسخ الإحتياطي
                // ------------------------------------
                item {
                    DrawerSectionCard(title = "النسخ الإحتياطي") {
                        DrawerMenuItem(
                            title = "حفظ نسخة احتياطية على الهاتف",
                            icon = Icons.Default.Storage,
                            onClick = { onCloseDrawer(); navController?.navigate("backup_phone") }
                        )
                        DrawerMenuItem(
                            title = "حفظ نسخة احتياطية على التلجرام",
                            icon = Icons.Default.Send,
                            onClick = { onCloseDrawer(); navController?.navigate("backup_telegram") }
                        )
                        DrawerMenuItem(
                            title = "مشاركة نسخة احتياطية",
                            icon = Icons.Default.Share,
                            onClick = { onCloseDrawer() }
                        )
                        DrawerMenuItem(
                            title = "إستعادة نسخة احتياطية",
                            icon = Icons.Default.Restore,
                            onClick = { onCloseDrawer(); navController?.navigate("backup_restore") }
                        )
                    }
                }

                // ------------------------------------
                // SECTION: جوجل درايف
                // ------------------------------------
                item {
                    DrawerSectionCard(title = "جوجل درايف") {
                        DrawerMenuItem(
                            title = "مزامنة جوجل درايف",
                            icon = Icons.Default.CloudSync,
                            onClick = { onCloseDrawer(); navController?.navigate("google_drive") }
                        )
                    }
                }

                // ------------------------------------
                // SECTION: الإعدادات والخدمات
                // ------------------------------------
                item {
                    DrawerSectionCard(title = "الإعدادات والمظهر") {
                        DrawerMenuItem(
                            title = "اللغة ومظهر التطبيق",
                            icon = Icons.Default.Palette,
                            onClick = { onCloseDrawer(); navController?.navigate("appearance") }
                        )
                        DrawerMenuItem(
                            title = "الإعدادات المتقدمة",
                            icon = Icons.Default.Settings,
                            onClick = { onCloseDrawer(); navController?.navigate("advanced_settings") }
                        )
                        DrawerMenuItem(
                            title = "الإشعارات والتنبيهات",
                            icon = Icons.Default.Notifications,
                            onClick = { onCloseDrawer(); navController?.navigate("notifications") }
                        )
                        DrawerMenuItem(
                            title = "الرسائل الفورية SMS",
                            icon = Icons.Default.Sms,
                            onClick = { onCloseDrawer(); navController?.navigate("sms_messaging") }
                        )
                        DrawerMenuItem(
                            title = "الرسائل الفورية واتساب",
                            icon = Icons.Default.Chat,
                            onClick = { onCloseDrawer(); navController?.navigate("whatsapp_messaging") }
                        )
                        DrawerMenuItem(
                            title = "عروض الأسعار",
                            icon = Icons.Default.LocalOffer,
                            onClick = { onCloseDrawer(); navController?.navigate("quotes") }
                        )
                        DrawerMenuItem(
                            title = "الطلبيات والتوصيل",
                            icon = Icons.Default.LocalShipping,
                            onClick = { onCloseDrawer(); navController?.navigate("orders") }
                        )
                        DrawerMenuItem(
                            title = "سلة المحذوفات",
                            icon = Icons.Default.DeleteSweep,
                            onClick = { onCloseDrawer(); navController?.navigate("trash") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerSectionCard(
    title: String,
    isBurgundy: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBurgundy) Color(0xFF8B1C31) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = title,
                    color = if (isBurgundy) Color.White else Color(0xFF1F2937),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            // Items
            Column(content = content)
        }
    }
}

@Composable
private fun DrawerMenuItem(
    title: String,
    icon: ImageVector,
    isBurgundy: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isBurgundy) Color.White.copy(alpha = 0.9f) else Color(0xFF6B7280),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = if (isBurgundy) Color.White else Color(0xFF374151),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

