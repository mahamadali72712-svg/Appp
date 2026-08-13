package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettingsScreen(navController: NavController) {
    // Force RTL Layout Direction for native Arabic feeling
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("الإعدادات المتقدمة", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingsSectionHeader(title = "الأقسام الرئيسية")

                SettingsButton(
                    title = "البيانات الشخصية",
                    icon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer) },
                    onClick = { navController.navigate("settings_personal_data") }
                )
                SettingsButton(
                    title = "إعدادات الطباعة",
                    icon = { Icon(Icons.Default.Print, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer) },
                    onClick = { navController.navigate("settings_print") }
                )
                SettingsButton(
                    title = "إعدادات السندات والحوالات",
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer) },
                    onClick = { navController.navigate("settings_vouchers") }
                )
                SettingsButton(
                    title = "إعدادات المخازن والفواتير",
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer) },
                    onClick = { navController.navigate("settings_invoices") }
                )
                SettingsButton(
                    title = "إعدادات الرسائل والواتساب",
                    icon = { Icon(Icons.Default.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer) },
                    onClick = { navController.navigate("settings_whatsapp") }
                )
                SettingsButton(
                    title = "إعدادات أخرى",
                    icon = { Icon(Icons.Default.SettingsSuggest, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer) },
                    onClick = { navController.navigate("settings_other") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = "إدارة النظام")

                // Activation Code Section (Modernized)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "كود التفعيل",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "80e72c8571e4ce7e",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { /* TODO */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("تحقق الان")
                            }
                            FilledTonalButton(
                                onClick = { /* TODO */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("نسخ الكود")
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Storage, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("استعادة الإعدادات من قاعدة البيانات", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
