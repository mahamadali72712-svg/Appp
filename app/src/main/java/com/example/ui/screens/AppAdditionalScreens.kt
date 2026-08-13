package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.ui.theme.getThemeOption
import com.example.ui.viewmodels.StoreViewModel

// ---------------------------------------------------
// 9. FULL PAGE: EXCEL PRODUCT IMPORT (إستيراد الأصناف من أكسل - مطابق تماماً للصورة)
// ---------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportProductsExcelScreen(
    viewModel: StoreViewModel,
    navController: NavHostController
) {
    var importedSuccess by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("إستيراد الأصناف من أكسل", fontWeight = FontWeight.Bold, color = Color(0xFF1F2937)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color(0xFF1F2937))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // EXCEL TABLE PREVIEW CARD (مطابق تماماً لجدول الإكسل في الصورة رقم 1)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Yellow Excel Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEAB308))
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            val headers = listOf("الصنف", "الباركود", "الوحدة الأولى", "سعر الشراء", "سعر البيع", "الوحدة الثانية", "سعر البيع", "تاريخ الانتهاء")
                            headers.forEach { h ->
                                Text(h, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }

                        // Sample Data Rows
                        val sampleRows = listOf(
                            listOf("منتج 1", "001-3099", "حبة", "1000", "1500", "درزن", "18000", "20-12-2024"),
                            listOf("منتج 2", "001-31004", "حبة", "2000", "2500", "درزن", "18000", "21-12-2024"),
                            listOf("منتج 3", "001-31010", "حبة", "3000", "3500", "درزن", "18000", "22-12-2024"),
                            listOf("منتج 4", "001-31022", "حبة", "4000", "4500", "درزن", "18000", "23-12-2024")
                        )

                        sampleRows.forEachIndexed { idx, row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (idx % 2 == 0) Color(0xFFF9FAFB) else Color.White)
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                row.forEach { cell ->
                                    Text(cell, fontSize = 8.5.sp, color = Color(0xFF374151))
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF3F4F6))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // EXPLANATION INSTRUCTION TEXT (مطابق تماماً للخط والنص في الصورة)
                Text(
                    text = "لاستيراد الأصناف من ملف إكسل بشكل صحيح يجب ان يكون الملف مطابق لنموذج إستيراد الأصناف الخاص بنا\n\nإذا كان الملف مطابق للنموذج قم باختياره لاستيراد الاصناف",
                    color = Color(0xFF4B5563),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                if (importedSuccess) {
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669))
                            Text("تم استيراد الأصناف بنجاح وإضافتها لقاعدة البيانات", color = Color(0xFF065F46), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // FOUR ACTION TEXT BUTTONS AT BOTTOM (مطابق تماماً للأسفل بالصورة)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(onClick = { importedSuccess = true }) {
                    Text("اختيار الملف", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFD97706))
                }

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("إلغاء", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFD97706))
                }

                TextButton(onClick = { }) {
                    Text("شاهد الشرح", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFDC2626))
                }

                TextButton(onClick = { }) {
                    Text("نموذج", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0284C7))
                }
            }
        }
    }
}

// ---------------------------------------------------
// 10. FULL PAGE: WAREHOUSES MANAGEMENT (المخازن)
// ---------------------------------------------------
// 13. FULL PAGE: BACKUP & RESTORE SCREENS (حفظ واستعادة النسخ الاحتياطية)
// ---------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupManagementScreen(
    type: String, // "phone", "telegram", "restore", "gdrive"
    viewModel: StoreViewModel,
    navController: NavHostController
) {
    val selectedThemeId by viewModel.selectedThemeId.collectAsStateWithLifecycle()
    val currentTheme = getThemeOption(selectedThemeId)

    var showSuccess by remember { mutableStateOf(false) }

    val title = when (type) {
        "phone" -> "حفظ نسخة احتياطية على الهاتف"
        "telegram" -> "حفظ نسخة احتياطية على التلجرام"
        "restore" -> "إستعادة نسخة احتياطية"
        else -> "النسخ التلقائي على جوجل درايف"
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x331E293B)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, currentTheme.primaryColor.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (type == "restore") Icons.Default.Restore else Icons.Default.Backup,
                        contentDescription = null,
                        tint = currentTheme.primaryColor,
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        text = if (type == "restore") "اختر ملف النسخة الاحتياطية (.db / .json) لاستعادة كافة البيانات المالية والمبيعات" else "انقر أدناه لإنشاء ملف نسخة احتياطية آمنة ومفرغة لحفظ بياناتك في أمان تام",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Button(
                        onClick = { showSuccess = true },
                        colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primaryColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (type == "restore") "تحديد ملف والاستعادة" else "بدء إجراء النسخ الاحتياطي الان", fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (showSuccess) {
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFF10B981))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                        Text("تمت العملية بنجاح وبسرعة عالية", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
