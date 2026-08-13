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
fun InvoiceSettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { SettingsDataStore(context) }

    val freeInvoiceName by dataStore.getSetting(SettingsKeys.FREE_INVOICE_NAME, "فاتورة خدمية").collectAsState(initial = "فاتورة خدمية")
    val freeInvoiceFormat by dataStore.getSetting(SettingsKeys.FREE_INVOICE_FORMAT, "الأخ").collectAsState(initial = "الأخ")
    
    val storeInvoiceSaleFormat by dataStore.getSetting(SettingsKeys.STORE_INVOICE_SALE_FORMAT, "الأخ").collectAsState(initial = "الأخ")
    val storeInvoicePurchaseFormat by dataStore.getSetting(SettingsKeys.STORE_INVOICE_PURCHASE_FORMAT, "الأخ").collectAsState(initial = "الأخ")
    
    val quoteFormat by dataStore.getSetting(SettingsKeys.QUOTE_FORMAT, "").collectAsState(initial = "")
    
    val invoiceSignature by dataStore.getSetting(SettingsKeys.INVOICE_SIGNATURE, "المستلم, المخازن, الصندوق, المدير العام").collectAsState(initial = "المستلم, المخازن, الصندوق, المدير العام")
    val invoiceNote by dataStore.getSetting(SettingsKeys.INVOICE_NOTE, "").collectAsState(initial = "")
    
    val decimalPlaces by dataStore.getSetting(SettingsKeys.DECIMAL_PLACES, 2.0f).collectAsState(initial = 2.0f)
    
    val showInvoiceItemsInDescription by dataStore.getSetting(SettingsKeys.SHOW_INVOICE_ITEMS_IN_DESCRIPTION, false).collectAsState(initial = false)
    val useStoreUnits by dataStore.getSetting(SettingsKeys.USE_STORE_UNITS, false).collectAsState(initial = false)
    val showQuantityAndUnitInDescription by dataStore.getSetting(SettingsKeys.SHOW_QUANTITY_AND_UNIT_IN_DESCRIPTION, false).collectAsState(initial = false)
    
    val enableExpirationDate by dataStore.getSetting(SettingsKeys.ENABLE_EXPIRATION_DATE, false).collectAsState(initial = false)
    val showExpirationDateInInvoice by dataStore.getSetting(SettingsKeys.SHOW_EXPIRATION_DATE_IN_INVOICE, false).collectAsState(initial = false)
    val showTotalQuantityInPrint by dataStore.getSetting(SettingsKeys.SHOW_TOTAL_QUANTITY_IN_PRINT, false).collectAsState(initial = false)
    
    val enableDiscountInSales by dataStore.getSetting(SettingsKeys.ENABLE_DISCOUNT_IN_SALES, false).collectAsState(initial = false)
    val enablePercentageDiscount by dataStore.getSetting(SettingsKeys.ENABLE_PERCENTAGE_DISCOUNT, false).collectAsState(initial = false)
    
    val enableVATInSales by dataStore.getSetting(SettingsKeys.ENABLE_VAT_IN_SALES, false).collectAsState(initial = false)
    val calcTaxAfterDiscount by dataStore.getSetting(SettingsKeys.CALC_TAX_AFTER_DISCOUNT, false).collectAsState(initial = false)
    
    val deductFromInventoryOnSale by dataStore.getSetting(SettingsKeys.DEDUCT_FROM_INVENTORY_ON_SALE, false).collectAsState(initial = false)
    val allowSellingBelowCost by dataStore.getSetting(SettingsKeys.ALLOW_SELLING_BELOW_COST, false).collectAsState(initial = false)
    val allowDuplicateItems by dataStore.getSetting(SettingsKeys.ALLOW_DUPLICATE_ITEMS, false).collectAsState(initial = false)
    val allowDuplicateBarcode by dataStore.getSetting(SettingsKeys.ALLOW_DUPLICATE_BARCODE, false).collectAsState(initial = false)
    
    val sellerName by dataStore.getSetting(SettingsKeys.SELLER_NAME, "").collectAsState(initial = "")
    val taxNumber by dataStore.getSetting(SettingsKeys.TAX_NUMBER, "").collectAsState(initial = "")
    val defaultTaxRate by dataStore.getSetting(SettingsKeys.DEFAULT_TAX_RATE, "15").collectAsState(initial = "15")
    val printTaxCode by dataStore.getSetting(SettingsKeys.PRINT_TAX_CODE, false).collectAsState(initial = false)
    val showCustomerTaxNumber by dataStore.getSetting(SettingsKeys.SHOW_CUSTOMER_TAX_NUMBER, false).collectAsState(initial = false)
    val taxCodePrintSize by dataStore.getSetting(SettingsKeys.TAX_CODE_PRINT_SIZE, 80.0f).collectAsState(initial = 80.0f)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("إعدادات المخازن والفواتير", fontWeight = FontWeight.Bold) },
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
                SettingsSectionHeader("صيغ وتسميات الفواتير")
                SettingsTextInput("تسمية الفواتير الحرة", freeInvoiceName) { scope.launch { dataStore.saveSetting(SettingsKeys.FREE_INVOICE_NAME, it) } }
                SettingsTextInput("صيغة الفواتير الحرة", freeInvoiceFormat) { scope.launch { dataStore.saveSetting(SettingsKeys.FREE_INVOICE_FORMAT, it) } }
                SettingsTextInput("صيغة فواتير المبيعات", storeInvoiceSaleFormat) { scope.launch { dataStore.saveSetting(SettingsKeys.STORE_INVOICE_SALE_FORMAT, it) } }
                SettingsTextInput("صيغة فواتير المشتريات", storeInvoicePurchaseFormat) { scope.launch { dataStore.saveSetting(SettingsKeys.STORE_INVOICE_PURCHASE_FORMAT, it) } }
                SettingsTextInput("صيغة عرض السعر", quoteFormat) { scope.launch { dataStore.saveSetting(SettingsKeys.QUOTE_FORMAT, it) } }

                SettingsSectionHeader("التوقيعات والملاحظات")
                SettingsTextInput("توقيع الفواتير (مفصول بفاصلة)", invoiceSignature) { scope.launch { dataStore.saveSetting(SettingsKeys.INVOICE_SIGNATURE, it) } }
                SettingsTextInput("ملاحظة اسفل الفواتير", invoiceNote) { scope.launch { dataStore.saveSetting(SettingsKeys.INVOICE_NOTE, it) } }

                SettingsSectionHeader("خيارات المخازن والفواتير")
                SettingsNumberPicker("عدد الخانات العشرية في السعر", decimalPlaces, { scope.launch { dataStore.saveSetting(SettingsKeys.DECIMAL_PLACES, it) } }, step = 1f)
                SettingsSwitchRow("عرض اصناف الفاتورة في البيان", showInvoiceItemsInDescription, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_INVOICE_ITEMS_IN_DESCRIPTION, it) } })
                SettingsSwitchRow("إستخدام الوحدات المخزنية للأصناف", useStoreUnits, { scope.launch { dataStore.saveSetting(SettingsKeys.USE_STORE_UNITS, it) } })
                SettingsSwitchRow("عرض الكمية والوحدة لأصناف الفاتورة في البيان", showQuantityAndUnitInDescription, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_QUANTITY_AND_UNIT_IN_DESCRIPTION, it) } })
                
                SettingsSwitchRow("تفعيل تاريخ الانتهاء للاصناف", enableExpirationDate, { scope.launch { dataStore.saveSetting(SettingsKeys.ENABLE_EXPIRATION_DATE, it) } })
                SettingsSwitchRow("عرض تاريخ الانتهاء في الفواتير", showExpirationDateInInvoice, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_EXPIRATION_DATE_IN_INVOICE, it) } })
                SettingsSwitchRow("عرض اجمالي الكمية في الفاتورة عند الطباعة", showTotalQuantityInPrint, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_TOTAL_QUANTITY_IN_PRINT, it) } })

                SettingsSectionHeader("الخصومات والأسعار")
                SettingsSwitchRow("تفعيل الخصم في المبيعات", enableDiscountInSales, { scope.launch { dataStore.saveSetting(SettingsKeys.ENABLE_DISCOUNT_IN_SALES, it) } })
                SettingsSwitchRow("تفعيل الخصم بالنسبة بدلا من المبلغ", enablePercentageDiscount, { scope.launch { dataStore.saveSetting(SettingsKeys.ENABLE_PERCENTAGE_DISCOUNT, it) } })
                SettingsSwitchRow("السماح بالبيع بسعر اقل من سعر الشراء", allowSellingBelowCost, { scope.launch { dataStore.saveSetting(SettingsKeys.ALLOW_SELLING_BELOW_COST, it) } })

                SettingsSectionHeader("خيارات متقدمة للعمليات")
                SettingsSwitchRow("إعتماد المخزون عند البيع (خصم الكمية)", deductFromInventoryOnSale, { scope.launch { dataStore.saveSetting(SettingsKeys.DEDUCT_FROM_INVENTORY_ON_SALE, it) } })
                SettingsSwitchRow("السماح بتكرار الصنف في الفاتورة", allowDuplicateItems, { scope.launch { dataStore.saveSetting(SettingsKeys.ALLOW_DUPLICATE_ITEMS, it) } })
                SettingsSwitchRow("السماح بتكرار الباركود للاصناف", allowDuplicateBarcode, { scope.launch { dataStore.saveSetting(SettingsKeys.ALLOW_DUPLICATE_BARCODE, it) } })

                SettingsSectionHeader("البيانات الضريبية (القيمة المضافة)")
                SettingsSwitchRow("تفعيل القيمة المضافة في المبيعات", enableVATInSales, { scope.launch { dataStore.saveSetting(SettingsKeys.ENABLE_VAT_IN_SALES, it) } })
                SettingsSwitchRow("احتساب الضريبة بعد الخصم", calcTaxAfterDiscount, { scope.launch { dataStore.saveSetting(SettingsKeys.CALC_TAX_AFTER_DISCOUNT, it) } })
                SettingsTextInput("إسم البائع (للفاتورة الضريبية)", sellerName) { scope.launch { dataStore.saveSetting(SettingsKeys.SELLER_NAME, it) } }
                SettingsTextInput("الرقم الضريبي", taxNumber) { scope.launch { dataStore.saveSetting(SettingsKeys.TAX_NUMBER, it) } }
                SettingsTextInput("نسبة الضريبة الافتراضية (%)", defaultTaxRate) { scope.launch { dataStore.saveSetting(SettingsKeys.DEFAULT_TAX_RATE, it) } }
                SettingsSwitchRow("طباعة كود الضريبة (QR) على الفاتورة", printTaxCode, { scope.launch { dataStore.saveSetting(SettingsKeys.PRINT_TAX_CODE, it) } })
                SettingsSwitchRow("عرض الرقم الضريبي للعميل في الفاتورة", showCustomerTaxNumber, { scope.launch { dataStore.saveSetting(SettingsKeys.SHOW_CUSTOMER_TAX_NUMBER, it) } })
                SettingsNumberPicker("حجم كود الضريبة عند الطباعة", taxCodePrintSize, { scope.launch { dataStore.saveSetting(SettingsKeys.TAX_CODE_PRINT_SIZE, it) } }, step = 10f)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
