package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.SettingsDataStore
import com.example.data.SettingsKeys
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherSettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { SettingsDataStore(context) }

    val enableMultiCurrency by dataStore.getSetting(SettingsKeys.ENABLE_MULTI_CURRENCY, false).collectAsState(initial = false)
    val cloudBackupDestination by dataStore.getSetting(SettingsKeys.CLOUD_BACKUP_DESTINATION, "جوجل درايف").collectAsState(initial = "جوجل درايف")
    val directBackupToEmail by dataStore.getSetting(SettingsKeys.DIRECT_BACKUP_TO_EMAIL, false).collectAsState(initial = false)
    val backupPrefixName by dataStore.getSetting(SettingsKeys.BACKUP_PREFIX_NAME, "").collectAsState(initial = "")
    val repeatBackupAlertOnClose by dataStore.getSetting(SettingsKeys.REPEAT_BACKUP_ALERT_ON_CLOSE, false).collectAsState(initial = false)
    val runEmailBackupInBackground by dataStore.getSetting(SettingsKeys.RUN_EMAIL_BACKUP_IN_BACKGROUND, false).collectAsState(initial = false)
    
    val enableAudioAlerts by dataStore.getSetting(SettingsKeys.ENABLE_AUDIO_ALERTS, true).collectAsState(initial = true)
    val disableSizesSystem by dataStore.getSetting(SettingsKeys.DISABLE_SIZES_SYSTEM, false).collectAsState(initial = false)
    val fixReportsDate by dataStore.getSetting(SettingsKeys.FIX_REPORTS_DATE, false).collectAsState(initial = false)
    
    val showDailyAsCashbox by dataStore.getSetting(SettingsKeys.SHOW_DAILY_AS_CASHBOX, false).collectAsState(initial = false)
    val showBackButtonInReports by dataStore.getSetting(SettingsKeys.SHOW_BACK_BUTTON_IN_REPORTS, true).collectAsState(initial = true)
    val showAccountDebtPeriod by dataStore.getSetting(SettingsKeys.SHOW_ACCOUNT_DEBT_PERIOD, false).collectAsState(initial = false)
    
    val showTotalMainAccountsBalance by dataStore.getSetting(SettingsKeys.SHOW_TOTAL_MAIN_ACCOUNTS_BALANCE, false).collectAsState(initial = false)
    val groupMainAccountsBalanceBy by dataStore.getSetting(SettingsKeys.GROUP_MAIN_ACCOUNTS_BALANCE_BY, "العملة").collectAsState(initial = "العملة")
    
    val cloudBackupOptions = listOf("جوجل درايف", "تلجرام")
    val groupByOptions = listOf("العملة", "نوع الحساب", "المنطقة")

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("إعدادات أخرى", fontWeight = FontWeight.Bold) },
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
                SettingsSectionHeader("إعدادات العملات")
                SettingsSwitchRow("تفعيل ميزة تعدد العملات في الحساب", enableMultiCurrency, { scope.launch { dataStore.saveSetting(SettingsKeys.ENABLE_MULTI_CURRENCY, it) } })

                SettingsSectionHeader("النسخ الاحتياطي السحابي")
                SettingsRadioGroup("النسخ السحابي اليومي الى:", cloudBackupOptions, cloudBackupDestination) { scope.launch { dataStore.saveSetting(SettingsKeys.CLOUD_BACKUP_DESTINATION, it) } }
                SettingsSwitchRow("النسخ المباشر الى البريد الإلكتروني", directBackupToEmail, { scope.launch { dataStore.saveSetting(SettingsKeys.DIRECT_BACKUP_TO_EMAIL, it) } })
                SettingsTextInput("بداية اسم النسخة الاحتياطية", backupPrefixName) { scope.launch { dataStore.saveSetting(SettingsKeys.BACKUP_PREFIX_NAME, it) } }
                SettingsSwitchRow("تكرار تنبيه النسخ عند كل اغلاق", repeatBackupAlertOnClose, { scope.launch { dataStore.saveSetting(SettingsKeys.REPEAT_BACKUP_ALERT_ON_CLOSE, it) } })
                SettingsSwitchRow("تنفيذ عملية النسخ الى البريد في الخلفية", runEmailBackupInBackground, { scope.launch { dataStore.saveSetting(SettingsKeys.RUN_EMAIL_BACKUP_IN_BACKGROUND, it) } })

                SettingsSectionHeader("إعدادات النظام العامة")
                SettingsSwitchRow("تشغيل التنبيهات الصوتية", enableAudioAlerts, { scope.launch { dataStore.saveSetting(SettingsKeys.ENABLE_AUDIO_ALERTS, it) } })
                SettingsSwitchRow("لا أحتاج نظام المقاسات (للملابس/الأحذية)", disableSizesSystem, { scope.launch { dataStore.saveSetting(SettingsKeys.DISABLE_SIZES_SYSTEM, it) } })
                
                SettingsSectionHeader("إعدادات التقارير والعرض")
                SettingsSwitchRow("تثبيت تاريخ التقارير (شهري - كلي - سنوي)", fixReportsDate, { scope.launch { dataStore.saveSetting(SettingsKeys.FIX_REPORTS_DATE, it) } })
                SettingsSwitchRow("عرض الحركة اليومية كحركة الصندوق", showDailyAsCashbox, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_DAILY_AS_CASHBOX, it) } })
                SettingsSwitchRow("إظهار سهم الرجوع في التقارير", showBackButtonInReports, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_BACK_BUTTON_IN_REPORTS, it) } })
                SettingsSwitchRow("عرض فترة المديونية للحسابات", showAccountDebtPeriod, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_ACCOUNT_DEBT_PERIOD, it) } })
                
                SettingsSectionHeader("إعدادات الواجهة الرئيسية")
                SettingsSwitchRow("عرض اجمالي الرصيد للحسابات الرئيسية", showTotalMainAccountsBalance, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_TOTAL_MAIN_ACCOUNTS_BALANCE, it) } })
                SettingsRadioGroup("تجميع ارصدة الحسابات الرئيسية حسب:", groupByOptions, groupMainAccountsBalanceBy) { scope.launch { dataStore.saveSetting(SettingsKeys.GROUP_MAIN_ACCOUNTS_BALANCE_BY, it) } }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
