package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
fun PersonalDataScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { SettingsDataStore(context) }

    val arabicName by dataStore.getSetting(SettingsKeys.ARABIC_NAME, "محل الرطب").collectAsState(initial = "محل الرطب")
    val englishName by dataStore.getSetting(SettingsKeys.ENGLISH_NAME, "الرطب").collectAsState(initial = "الرطب")
    val arabicAddress by dataStore.getSetting(SettingsKeys.ARABIC_ADDRESS, "اليمن").collectAsState(initial = "اليمن")
    val englishAddress by dataStore.getSetting(SettingsKeys.ENGLISH_ADDRESS, "Yemen").collectAsState(initial = "Yemen")
    val contactNumbers by dataStore.getSetting(SettingsKeys.CONTACT_NUMBERS, "774815332").collectAsState(initial = "774815332")
    
    val useReadyHeader by dataStore.getSetting(SettingsKeys.USE_READY_HEADER, false).collectAsState(initial = false)
    
    val logoSize by dataStore.getSetting(SettingsKeys.LOGO_SIZE, 0.0f).collectAsState(initial = 0.0f)
    val signatureSize by dataStore.getSetting(SettingsKeys.SIGNATURE_SIZE, 0.0f).collectAsState(initial = 0.0f)
    val stampSize by dataStore.getSetting(SettingsKeys.STAMP_SIZE, 0.0f).collectAsState(initial = 0.0f)
    
    val logoUri by dataStore.getSetting(SettingsKeys.LOGO_URI, "").collectAsState(initial = "")
    val stampUri by dataStore.getSetting(SettingsKeys.STAMP_URI, "").collectAsState(initial = "")
    val signatureUri by dataStore.getSetting(SettingsKeys.SIGNATURE_URI, "").collectAsState(initial = "")

    val logoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> 
        uri?.let { scope.launch { dataStore.saveSetting(SettingsKeys.LOGO_URI, it.toString()) } } 
    }
    val stampPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> 
        uri?.let { scope.launch { dataStore.saveSetting(SettingsKeys.STAMP_URI, it.toString()) } } 
    }
    val signaturePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> 
        uri?.let { scope.launch { dataStore.saveSetting(SettingsKeys.SIGNATURE_URI, it.toString()) } } 
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("البيانات الشخصية", fontWeight = FontWeight.Bold) },
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
                
                SettingsSectionHeader("بيانات المؤسسة")
                
                SettingsTextInput("الإسم باللغة العربية", arabicName) { scope.launch { dataStore.saveSetting(SettingsKeys.ARABIC_NAME, it) } }
                SettingsTextInput("الإسم باللغة الإنجليزية", englishName) { scope.launch { dataStore.saveSetting(SettingsKeys.ENGLISH_NAME, it) } }
                SettingsTextInput("العنوان باللغة العربية", arabicAddress) { scope.launch { dataStore.saveSetting(SettingsKeys.ARABIC_ADDRESS, it) } }
                SettingsTextInput("العنوان باللغة الإنجليزية", englishAddress) { scope.launch { dataStore.saveSetting(SettingsKeys.ENGLISH_ADDRESS, it) } }
                SettingsTextInput("أرقام التواصل", contactNumbers) { scope.launch { dataStore.saveSetting(SettingsKeys.CONTACT_NUMBERS, it) } }
                
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader("إعدادات الترويسة والصور")

                SettingsSwitchRow(
                    title = "استخدام ترويسة جاهزة (صورة كاملة)",
                    description = "عند التفعيل، يتم عرض الشعار المرفوع كترويسة كاملة بعرض الورقة وإخفاء البيانات النصية",
                    checked = useReadyHeader,
                    onCheckedChange = { scope.launch { dataStore.saveSetting(SettingsKeys.USE_READY_HEADER, it) } }
                )
                
                SettingsImagePicker(
                    title = "اختر الشعار الخاص بك",
                    imageUri = logoUri,
                    onPickImage = { logoPicker.launch("image/*") },
                    onRemoveImage = { scope.launch { dataStore.saveSetting(SettingsKeys.LOGO_URI, "") } }
                )
                SettingsImagePicker(
                    title = "اختر الختم الخاص بك",
                    imageUri = stampUri,
                    onPickImage = { stampPicker.launch("image/*") },
                    onRemoveImage = { scope.launch { dataStore.saveSetting(SettingsKeys.STAMP_URI, "") } }
                )
                SettingsImagePicker(
                    title = "اضافة التوقيع الخاص بك",
                    imageUri = signatureUri,
                    onPickImage = { signaturePicker.launch("image/*") },
                    onRemoveImage = { scope.launch { dataStore.saveSetting(SettingsKeys.SIGNATURE_URI, "") } }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader("أحجام الطباعة")

                SettingsNumberPicker("حجم الشعار عند الطباعة", logoSize, { scope.launch { dataStore.saveSetting(SettingsKeys.LOGO_SIZE, it) } })
                SettingsNumberPicker("حجم التوقيع عند الطباعة", signatureSize, { scope.launch { dataStore.saveSetting(SettingsKeys.SIGNATURE_SIZE, it) } })
                SettingsNumberPicker("حجم الختم عند الطباعة", stampSize, { scope.launch { dataStore.saveSetting(SettingsKeys.STAMP_SIZE, it) } })
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
