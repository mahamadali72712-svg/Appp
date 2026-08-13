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
fun VoucherSettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { SettingsDataStore(context) }

    val receiptTypeTitle by dataStore.getSetting(SettingsKeys.RECEIPT_TYPE_TITLE, "له").collectAsState(initial = "له")
    val paymentTypeTitle by dataStore.getSetting(SettingsKeys.PAYMENT_TYPE_TITLE, "عليه").collectAsState(initial = "عليه")
    
    val receiptFormatLine1 by dataStore.getSetting(SettingsKeys.RECEIPT_FORMAT_LINE_1, "الأخ").collectAsState(initial = "الأخ")
    val receiptFormatLine2 by dataStore.getSetting(SettingsKeys.RECEIPT_FORMAT_LINE_2, "لكم مبلغ").collectAsState(initial = "لكم مبلغ")
    
    val paymentFormatLine1 by dataStore.getSetting(SettingsKeys.PAYMENT_FORMAT_LINE_1, "الأخ").collectAsState(initial = "الأخ")
    val paymentFormatLine2 by dataStore.getSetting(SettingsKeys.PAYMENT_FORMAT_LINE_2, "عليكم مبلغ").collectAsState(initial = "عليكم مبلغ")
    
    val entryFormatDebit by dataStore.getSetting(SettingsKeys.ENTRY_FORMAT_DEBIT, "تحويل الى حساب").collectAsState(initial = "تحويل الى حساب")
    val entryFormatCredit by dataStore.getSetting(SettingsKeys.ENTRY_FORMAT_CREDIT, "تحويل من حساب").collectAsState(initial = "تحويل من حساب")
    
    val signatureBonds by dataStore.getSetting(SettingsKeys.SIGNATURE_BONDS, "المستلم, مدير الحسابات, الصندوق, المدير العام").collectAsState(initial = "المستلم, مدير الحسابات, الصندوق, المدير العام")
    val signatureStatements by dataStore.getSetting(SettingsKeys.SIGNATURE_STATEMENTS, "مدير الحسابات, 2, 3, المدير العام").collectAsState(initial = "مدير الحسابات, 2, 3, المدير العام")
    
    val bondsNote by dataStore.getSetting(SettingsKeys.BONDS_NOTE, "").collectAsState(initial = "")
    val transfersNote by dataStore.getSetting(SettingsKeys.TRANSFERS_NOTE, "").collectAsState(initial = "")
    val notificationsNote by dataStore.getSetting(SettingsKeys.NOTIFICATIONS_NOTE, "").collectAsState(initial = "")
    val statementsNote by dataStore.getSetting(SettingsKeys.STATEMENTS_NOTE, "").collectAsState(initial = "")
    val balanceAuthNote by dataStore.getSetting(SettingsKeys.BALANCE_AUTH_NOTE, "الرجاء مصادقة الرصيد وموافاتنا بالرد...").collectAsState(initial = "الرجاء مصادقة الرصيد وموافاتنا بالرد...")

    val updateTimeWhenIdle by dataStore.getSetting(SettingsKeys.UPDATE_TIME_WHEN_IDLE, false).collectAsState(initial = false)
    val addAccountNameToEntry by dataStore.getSetting(SettingsKeys.ADD_ACCOUNT_NAME_TO_ENTRY, false).collectAsState(initial = false)
    val useDebitCreditButtons by dataStore.getSetting(SettingsKeys.USE_DEBIT_CREDIT_BUTTONS, false).collectAsState(initial = false)
    val addRemainingBalance by dataStore.getSetting(SettingsKeys.ADD_REMAINING_BALANCE, false).collectAsState(initial = false)
    val showSenderReceiverInMessage by dataStore.getSetting(SettingsKeys.SHOW_SENDER_RECEIVER_IN_MESSAGE, false).collectAsState(initial = false)
    val adoptIncomingTransferAsPayment by dataStore.getSetting(SettingsKeys.ADOPT_INCOMING_TRANSFER_AS_PAYMENT, false).collectAsState(initial = false)
    val showPreviousBalance by dataStore.getSetting(SettingsKeys.SHOW_PREVIOUS_BALANCE, false).collectAsState(initial = false)
    val showTotalOperationsCount by dataStore.getSetting(SettingsKeys.SHOW_TOTAL_OPERATIONS_COUNT, false).collectAsState(initial = false)
    val canAddFeeToOutgoing by dataStore.getSetting(SettingsKeys.CAN_ADD_FEE_TO_OUTGOING, false).collectAsState(initial = false)
    val canAddFeeToIncoming by dataStore.getSetting(SettingsKeys.CAN_ADD_FEE_TO_INCOMING, false).collectAsState(initial = false)

    val searchSuggestionsRows by dataStore.getSetting(SettingsKeys.SEARCH_SUGGESTIONS_ROWS, 3.0f).collectAsState(initial = 3.0f)
    val maxAccountNameLength by dataStore.getSetting(SettingsKeys.MAX_ACCOUNT_NAME_LENGTH, 20.0f).collectAsState(initial = 20.0f)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("إعدادات السندات والحوالات", fontWeight = FontWeight.Bold) },
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
                SettingsSectionHeader("تسميات أنواع العمليات")
                SettingsTextInput("قبض", receiptTypeTitle) { scope.launch { dataStore.saveSetting(SettingsKeys.RECEIPT_TYPE_TITLE, it) } }
                SettingsTextInput("صرف", paymentTypeTitle) { scope.launch { dataStore.saveSetting(SettingsKeys.PAYMENT_TYPE_TITLE, it) } }

                SettingsSectionHeader("صيغ السندات")
                SettingsTextInput("صيغة سند القبض (السطر الأول)", receiptFormatLine1) { scope.launch { dataStore.saveSetting(SettingsKeys.RECEIPT_FORMAT_LINE_1, it) } }
                SettingsTextInput("صيغة سند القبض (السطر الثاني)", receiptFormatLine2) { scope.launch { dataStore.saveSetting(SettingsKeys.RECEIPT_FORMAT_LINE_2, it) } }
                SettingsTextInput("صيغة سند الصرف (السطر الأول)", paymentFormatLine1) { scope.launch { dataStore.saveSetting(SettingsKeys.PAYMENT_FORMAT_LINE_1, it) } }
                SettingsTextInput("صيغة سند الصرف (السطر الثاني)", paymentFormatLine2) { scope.launch { dataStore.saveSetting(SettingsKeys.PAYMENT_FORMAT_LINE_2, it) } }
                SettingsTextInput("صيغة بيان القيد (عليه)", entryFormatDebit) { scope.launch { dataStore.saveSetting(SettingsKeys.ENTRY_FORMAT_DEBIT, it) } }
                SettingsTextInput("صيغة بيان القيد (له)", entryFormatCredit) { scope.launch { dataStore.saveSetting(SettingsKeys.ENTRY_FORMAT_CREDIT, it) } }

                SettingsSectionHeader("التوقيعات")
                SettingsTextInput("توقيع السندات (مفصول بفاصلة)", signatureBonds) { scope.launch { dataStore.saveSetting(SettingsKeys.SIGNATURE_BONDS, it) } }
                SettingsTextInput("توقيع كشف الحساب (مفصول بفاصلة)", signatureStatements) { scope.launch { dataStore.saveSetting(SettingsKeys.SIGNATURE_STATEMENTS, it) } }

                SettingsSectionHeader("الملاحظات السفلية")
                SettingsTextInput("ملاحظة اسفل السندات", bondsNote) { scope.launch { dataStore.saveSetting(SettingsKeys.BONDS_NOTE, it) } }
                SettingsTextInput("ملاحظة اسفل سندات الحوالات", transfersNote) { scope.launch { dataStore.saveSetting(SettingsKeys.TRANSFERS_NOTE, it) } }
                SettingsTextInput("ملاحظة اسفل الإشعارات", notificationsNote) { scope.launch { dataStore.saveSetting(SettingsKeys.NOTIFICATIONS_NOTE, it) } }
                SettingsTextInput("ملاحظة اسفل كشف الحساب", statementsNote) { scope.launch { dataStore.saveSetting(SettingsKeys.STATEMENTS_NOTE, it) } }
                SettingsTextInput("ملاحظة اسفل مصادقة الرصيد", balanceAuthNote) { scope.launch { dataStore.saveSetting(SettingsKeys.BALANCE_AUTH_NOTE, it) } }

                SettingsSectionHeader("خيارات متقدمة")
                SettingsSwitchRow("تحديث الوقت عند ترك واجهة الادخال مفتوحة لمدة طويلة", updateTimeWhenIdle, { scope.launch { dataStore.saveSetting(SettingsKeys.UPDATE_TIME_WHEN_IDLE, it) } })
                SettingsSwitchRow("إضافة اسم الحساب في بيان القيد البسيط", addAccountNameToEntry, { scope.launch { dataStore.saveSetting(SettingsKeys.ADD_ACCOUNT_NAME_TO_ENTRY, it) } })
                SettingsSwitchRow("إستخدام أزرار (له، عليه) بدلاً من زر الحفظ", useDebitCreditButtons, { scope.launch { dataStore.saveSetting(SettingsKeys.USE_DEBIT_CREDIT_BUTTONS, it) } })
                SettingsSwitchRow("إضافة الرصيد المتبقي في السند", addRemainingBalance, { scope.launch { dataStore.saveSetting(SettingsKeys.ADD_REMAINING_BALANCE, it) } })
                SettingsSwitchRow("عرض اسم المرسل والمستلم في الرسالة", showSenderReceiverInMessage, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_SENDER_RECEIVER_IN_MESSAGE, it) } })
                SettingsSwitchRow("إعتماد الحوالة الواردة كسند صرف في الحركة النقدية", adoptIncomingTransferAsPayment, { scope.launch { dataStore.saveSetting(SettingsKeys.ADOPT_INCOMING_TRANSFER_AS_PAYMENT, it) } })
                SettingsSwitchRow("عرض الرصيد السابق عند اضافة سند", showPreviousBalance, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_PREVIOUS_BALANCE, it) } })
                SettingsSwitchRow("عرض اجمالي عدد العمليات في كشف الحساب", showTotalOperationsCount, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_TOTAL_OPERATIONS_COUNT, it) } })
                SettingsSwitchRow("إمكانية اضافة عمولة في الحوالة الصادرة", canAddFeeToOutgoing, { scope.launch { dataStore.saveSetting(SettingsKeys.CAN_ADD_FEE_TO_OUTGOING, it) } })
                SettingsSwitchRow("إمكانية اضافة عمولة في الحوالة الواردة", canAddFeeToIncoming, { scope.launch { dataStore.saveSetting(SettingsKeys.CAN_ADD_FEE_TO_INCOMING, it) } })

                SettingsSectionHeader("أبعاد ومقاسات الواجهة")
                SettingsNumberPicker("عدد صفوف مقترحات البحث", searchSuggestionsRows, { scope.launch { dataStore.saveSetting(SettingsKeys.SEARCH_SUGGESTIONS_ROWS, it) } }, step = 1f)
                SettingsNumberPicker("الحد الاعلى لعدد احرف اسم الحساب المعروضة", maxAccountNameLength, { scope.launch { dataStore.saveSetting(SettingsKeys.MAX_ACCOUNT_NAME_LENGTH, it) } }, step = 1f)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
