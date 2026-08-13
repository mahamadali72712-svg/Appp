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
fun WhatsAppSettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { SettingsDataStore(context) }

    val textMessageIntro by dataStore.getSetting(SettingsKeys.TEXT_MESSAGE_INTRO, "").collectAsState(initial = "")
    val textMessageOutro by dataStore.getSetting(SettingsKeys.TEXT_MESSAGE_OUTRO, "").collectAsState(initial = "")
    val paymentReqOutro by dataStore.getSetting(SettingsKeys.PAYMENT_REQ_OUTRO, "").collectAsState(initial = "")
    val messageFormatDebit by dataStore.getSetting(SettingsKeys.MESSAGE_FORMAT_DEBIT, "عليكم").collectAsState(initial = "عليكم")
    val messageFormatCredit by dataStore.getSetting(SettingsKeys.MESSAGE_FORMAT_CREDIT, "لكم").collectAsState(initial = "لكم")
    
    val sendTextMethod by dataStore.getSetting(SettingsKeys.SEND_TEXT_METHOD, "مشاركة اخرى").collectAsState(initial = "مشاركة اخرى")
    val preferredWhatsApp by dataStore.getSetting(SettingsKeys.PREFERRED_WHATSAPP, "أعمال").collectAsState(initial = "أعمال")
    val countryCode by dataStore.getSetting(SettingsKeys.COUNTRY_CODE, "+967").collectAsState(initial = "+967")
    
    val shareNotificationAs by dataStore.getSetting(SettingsKeys.SHARE_NOTIFICATION_AS, "مستند مع رسالة").collectAsState(initial = "مستند مع رسالة")
    val sharedDocumentFormat by dataStore.getSetting(SettingsKeys.SHARED_DOCUMENT_FORMAT, "ملف pdf").collectAsState(initial = "ملف pdf")
    val sharedDocumentType by dataStore.getSetting(SettingsKeys.SHARED_DOCUMENT_TYPE, "إشعار").collectAsState(initial = "إشعار")
    
    val whatsappImageSize by dataStore.getSetting(SettingsKeys.WHATSAPP_IMAGE_SIZE, 80.0f).collectAsState(initial = 80.0f)
    val whatsappImageQuality by dataStore.getSetting(SettingsKeys.WHATSAPP_IMAGE_QUALITY, 100.0f).collectAsState(initial = 100.0f)

    val sendTextMethodOptions = listOf("الى رقم الجوال (مباشرة)", "مشاركة اخرى")
    val preferredWhatsAppOptions = listOf("الأخضر", "أعمال", "أخرى")
    val shareNotificationAsOptions = listOf("رسالة نصية", "مستند مع رسالة")
    val sharedDocumentFormatOptions = listOf("ملف pdf", "صورة")
    val sharedDocumentTypeOptions = listOf("سند", "إشعار")

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("إعدادات الرسائل والواتساب", fontWeight = FontWeight.Bold) },
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
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text("تخصيص وإدارة نماذج الرسائل")
                }

                SettingsSectionHeader("نصوص الرسائل")
                SettingsTextInput("مقدمة الرسالة النصية", textMessageIntro) { scope.launch { dataStore.saveSetting(SettingsKeys.TEXT_MESSAGE_INTRO, it) } }
                SettingsTextInput("ختام الرسالة النصية", textMessageOutro) { scope.launch { dataStore.saveSetting(SettingsKeys.TEXT_MESSAGE_OUTRO, it) } }
                SettingsTextInput("ختام طلب السداد", paymentReqOutro) { scope.launch { dataStore.saveSetting(SettingsKeys.PAYMENT_REQ_OUTRO, it) } }
                SettingsTextInput("صيغة الرسائل (عليه)", messageFormatDebit) { scope.launch { dataStore.saveSetting(SettingsKeys.MESSAGE_FORMAT_DEBIT, it) } }
                SettingsTextInput("صيغة الرسائل (له)", messageFormatCredit) { scope.launch { dataStore.saveSetting(SettingsKeys.MESSAGE_FORMAT_CREDIT, it) } }

                SettingsSectionHeader("إعدادات الإرسال والواتساب")
                SettingsRadioGroup("طريقة ارسال الرسائل النصية", sendTextMethodOptions, sendTextMethod) { scope.launch { dataStore.saveSetting(SettingsKeys.SEND_TEXT_METHOD, it) } }
                SettingsRadioGroup("تطبيق الواتساب المستخدم افتراضياً", preferredWhatsAppOptions, preferredWhatsApp) { scope.launch { dataStore.saveSetting(SettingsKeys.PREFERRED_WHATSAPP, it) } }
                SettingsTextInput("مفتاح الدولة الافتراضي (مثال: +967)", countryCode) { scope.launch { dataStore.saveSetting(SettingsKeys.COUNTRY_CODE, it) } }

                SettingsSectionHeader("خيارات مشاركة الإشعارات")
                SettingsRadioGroup("مشاركة الإشعارات عبر الواتساب كـ", shareNotificationAsOptions, shareNotificationAs) { scope.launch { dataStore.saveSetting(SettingsKeys.SHARE_NOTIFICATION_AS, it) } }
                SettingsRadioGroup("صيغة المستند المشارك", sharedDocumentFormatOptions, sharedDocumentFormat) { scope.launch { dataStore.saveSetting(SettingsKeys.SHARED_DOCUMENT_FORMAT, it) } }
                SettingsRadioGroup("نوع المستند المشارك", sharedDocumentTypeOptions, sharedDocumentType) { scope.launch { dataStore.saveSetting(SettingsKeys.SHARED_DOCUMENT_TYPE, it) } }

                SettingsSectionHeader("جودة وحجم الصور")
                SettingsNumberPicker("حجم الصورة المرسلة عبر الواتساب", whatsappImageSize, { scope.launch { dataStore.saveSetting(SettingsKeys.WHATSAPP_IMAGE_SIZE, it) } }, step = 10f)
                SettingsNumberPicker("دقة الصورة المرسلة عبر الواتساب (%)", whatsappImageQuality, { scope.launch { dataStore.saveSetting(SettingsKeys.WHATSAPP_IMAGE_QUALITY, it) } }, step = 10f)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
