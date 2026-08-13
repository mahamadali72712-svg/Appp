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
fun PrintSettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { SettingsDataStore(context) }

    val printMethod by dataStore.getSetting(SettingsKeys.PRINT_METHOD, "الى pdf").collectAsState(initial = "الى pdf")
    val paperSize by dataStore.getSetting(SettingsKeys.PAPER_SIZE, "58mm").collectAsState(initial = "58mm")
    val connectionType by dataStore.getSetting(SettingsKeys.CONNECTION_TYPE, "متصلة بالتطبيق").collectAsState(initial = "متصلة بالتطبيق")
    
    val showHeaderData by dataStore.getSetting(SettingsKeys.SHOW_HEADER_DATA, false).collectAsState(initial = false)
    val repeatHeader by dataStore.getSetting(SettingsKeys.REPEAT_HEADER, false).collectAsState(initial = false)
    val showPrintDate by dataStore.getSetting(SettingsKeys.SHOW_PRINT_DATE, false).collectAsState(initial = false)
    val showPrintTime by dataStore.getSetting(SettingsKeys.SHOW_PRINT_TIME, false).collectAsState(initial = false)

    val bondsStampSignature by dataStore.getSetting(SettingsKeys.BONDS_STAMP_SIGNATURE, "لا شيء").collectAsState(initial = "لا شيء")
    val invoicesStampSignature by dataStore.getSetting(SettingsKeys.INVOICES_STAMP_SIGNATURE, "لا شيء").collectAsState(initial = "لا شيء")
    val accountStatementStampSignature by dataStore.getSetting(SettingsKeys.ACCOUNT_STATEMENT_STAMP_SIGNATURE, "لا شيء").collectAsState(initial = "لا شيء")
    val balanceAuthStampSignature by dataStore.getSetting(SettingsKeys.BALANCE_AUTH_STAMP_SIGNATURE, "لا شيء").collectAsState(initial = "لا شيء")
    val quotesStampSignature by dataStore.getSetting(SettingsKeys.QUOTES_STAMP_SIGNATURE, "لا شيء").collectAsState(initial = "لا شيء")

    val showAccountNote by dataStore.getSetting(SettingsKeys.SHOW_ACCOUNT_NOTE, false).collectAsState(initial = false)
    val showAccountPhone by dataStore.getSetting(SettingsKeys.SHOW_ACCOUNT_PHONE, false).collectAsState(initial = false)
    val showBondNumber by dataStore.getSetting(SettingsKeys.SHOW_BOND_NUMBER, false).collectAsState(initial = false)
    val addNoteToStatement by dataStore.getSetting(SettingsKeys.ADD_NOTE_TO_STATEMENT, false).collectAsState(initial = false)

    val fontSize by dataStore.getSetting(SettingsKeys.FONT_SIZE, 14.0f).collectAsState(initial = 14.0f)
    val lineSpacing by dataStore.getSetting(SettingsKeys.LINE_SPACING, 1.0f).collectAsState(initial = 1.0f)
    val fixedRowsInvoices by dataStore.getSetting(SettingsKeys.FIXED_ROWS_INVOICES, 0.0f).collectAsState(initial = 0.0f)
    val fixedRowsQuotes by dataStore.getSetting(SettingsKeys.FIXED_ROWS_QUOTES, 0.0f).collectAsState(initial = 0.0f)

    val printMethodOptions = listOf("الى pdf", "الى الطابعة")
    val paperSizeOptions = listOf("58mm", "80mm", "A4")
    val connectionTypeOptions = listOf("متصلة بالتطبيق", "عبر وسيط اخر")
    val stampSignatureOptions = listOf("لا شيء", "الكل", "الختم", "التوقيع")

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("إعدادات الطباعة", fontWeight = FontWeight.Bold) },
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
                SettingsSectionHeader("إعدادات الطابعة")
                SettingsRadioGroup("طريقة الطباعة", printMethodOptions, printMethod) { scope.launch { dataStore.saveSetting(SettingsKeys.PRINT_METHOD, it) } }
                SettingsRadioGroup("حجم ورق الطباعة", paperSizeOptions, paperSize) { scope.launch { dataStore.saveSetting(SettingsKeys.PAPER_SIZE, it) } }
                SettingsRadioGroup("نوع اتصال الطابعة", connectionTypeOptions, connectionType) { scope.launch { dataStore.saveSetting(SettingsKeys.CONNECTION_TYPE, it) } }

                Button(
                    onClick = { /* TODO: Show Color Picker */ },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Text("تغيير لون الطباعة")
                }

                SettingsSectionHeader("خيارات الترويسة والتذييل")
                SettingsSwitchRow("عرض بيانات رأس الصفحة (الترويسة)", showHeaderData, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_HEADER_DATA, it) } })
                SettingsSwitchRow("تكرار الترويسة في كل الصفحات", repeatHeader, { scope.launch { dataStore.saveSetting(SettingsKeys.REPEAT_HEADER, it) } })
                SettingsSwitchRow("عرض تاريخ الطباعة", showPrintDate, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_PRINT_DATE, it) } })
                SettingsSwitchRow("عرض الوقت في الطباعة", showPrintTime, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_PRINT_TIME, it) } })

                SettingsSectionHeader("الختم والتوقيع")
                SettingsRadioGroup("عرض الختم والتوقيع على السندات", stampSignatureOptions, bondsStampSignature) { scope.launch { dataStore.saveSetting(SettingsKeys.BONDS_STAMP_SIGNATURE, it) } }
                SettingsRadioGroup("عرض الختم والتوقيع على الفواتير", stampSignatureOptions, invoicesStampSignature) { scope.launch { dataStore.saveSetting(SettingsKeys.INVOICES_STAMP_SIGNATURE, it) } }
                SettingsRadioGroup("عرض الختم والتوقيع على كشف الحساب", stampSignatureOptions, accountStatementStampSignature) { scope.launch { dataStore.saveSetting(SettingsKeys.ACCOUNT_STATEMENT_STAMP_SIGNATURE, it) } }
                SettingsRadioGroup("عرض الختم والتوقيع على المصادقة", stampSignatureOptions, balanceAuthStampSignature) { scope.launch { dataStore.saveSetting(SettingsKeys.BALANCE_AUTH_STAMP_SIGNATURE, it) } }
                SettingsRadioGroup("عرض الختم والتوقيع على عروض السعر والطلبيات", stampSignatureOptions, quotesStampSignature) { scope.launch { dataStore.saveSetting(SettingsKeys.QUOTES_STAMP_SIGNATURE, it) } }

                SettingsSectionHeader("خيارات إضافية للطباعة")
                SettingsSwitchRow("عرض ملاحظة الحساب في الطباعة", showAccountNote, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_ACCOUNT_NOTE, it) } })
                SettingsSwitchRow("عرض رقم جوال الحساب في الطباعة", showAccountPhone, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_ACCOUNT_PHONE, it) } })
                SettingsSwitchRow("عرض رقم السند في الطباعة (للفواتير)", showBondNumber, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_BOND_NUMBER, it) } })
                SettingsSwitchRow("إضافة ملاحظة في اسفل كشف الحساب عند الطباعة", addNoteToStatement, { scope.launch { dataStore.saveSetting(SettingsKeys.ADD_NOTE_TO_STATEMENT, it) } })

                SettingsSectionHeader("التنسيق والأبعاد")
                SettingsNumberPicker("حجم الخط", fontSize, { scope.launch { dataStore.saveSetting(SettingsKeys.FONT_SIZE, it) } }, step = 1f)
                SettingsNumberPicker("المسافة بين السطور في نصوص وجداول المستند", lineSpacing, { scope.launch { dataStore.saveSetting(SettingsKeys.LINE_SPACING, it) } }, step = 0.5f)
                SettingsNumberPicker("عدد الصفوف الثابتة في رأس الفواتير", fixedRowsInvoices, { scope.launch { dataStore.saveSetting(SettingsKeys.FIXED_ROWS_INVOICES, it) } }, step = 1f)
                SettingsNumberPicker("عدد الصفوف الثابتة في رأس عروض الأسعار", fixedRowsQuotes, { scope.launch { dataStore.saveSetting(SettingsKeys.FIXED_ROWS_QUOTES, it) } }, step = 1f)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
