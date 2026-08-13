package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.CashMovement
import com.example.data.local.Product
import com.example.data.local.SalesInvoiceItem
import com.example.data.local.SalesInvoice
import com.example.data.local.Supplier
import com.example.data.local.Customer
import com.example.data.local.PurchaseInvoiceItem
import com.example.data.local.Expense
import com.example.data.repository.StoreRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StoreViewModel(private val repository: StoreRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.seedDefaultCurrencies()
        }
    }

    // Currencies State
    val allCurrencies: StateFlow<List<com.example.data.local.CurrencyRate>> = repository.allCurrencies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedThemeId = kotlinx.coroutines.flow.MutableStateFlow("ocean")
    val selectedThemeId: StateFlow<String> = _selectedThemeId

    fun setAppTheme(themeId: String) {
        _selectedThemeId.value = themeId
    }

    fun updateExchangeRate(code: String, rate: Double) {
        viewModelScope.launch {
            repository.updateExchangeRate(code, rate)
        }
    }

    fun addCurrency(code: String, name: String, symbol: String, rate: Double) {
        viewModelScope.launch {
            repository.addCurrency(
                com.example.data.local.CurrencyRate(
                    code = code,
                    name = name,
                    symbol = symbol,
                    exchangeRateToMain = rate,
                    isMain = false
                )
            )
        }
    }

    fun convertAmount(amount: Double, fromCode: String, toCode: String): Double {
        val currencies = allCurrencies.value
        val fromRate = currencies.find { it.code == fromCode }?.exchangeRateToMain ?: 1.0
        val toRate = currencies.find { it.code == toCode }?.exchangeRateToMain ?: 1.0
        if (toRate == 0.0) return 0.0
        return (amount * fromRate) / toRate
    }

    val allSalesInvoices: StateFlow<List<com.example.data.local.SalesInvoice>> = repository.allSalesInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPurchaseInvoices: StateFlow<List<com.example.data.local.PurchaseInvoice>> = repository.allPurchaseInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    suspend fun getSalesInvoiceItems(invoiceId: String) = repository.getSalesInvoiceItems(invoiceId)
    suspend fun getPurchaseInvoiceItems(invoiceId: String) = repository.getPurchaseInvoiceItems(invoiceId)
    suspend fun getProductById(id: String) = repository.getProductById(id)

    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedProducts: StateFlow<List<Product>> = repository.archivedProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<Product>> = repository.lowStockProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<com.example.data.local.ProductCategory>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val syncLogs: StateFlow<List<com.example.data.local.SyncLog>> = repository.syncLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _reportData = kotlinx.coroutines.flow.MutableStateFlow<com.example.data.repository.ReportData?>(null)
    val reportData: StateFlow<com.example.data.repository.ReportData?> = _reportData

    fun fetchReport(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _reportData.value = repository.generateReport(startDate, endDate)
        }
    }


    fun addProduct(
        name: String,
        cost: Double,
        salePrice: Double = 0.0,
        suggestedPrice: Double,
        stock: Double,
        categoryId: String?,
        minStockAlert: Double,
        description: String?,
        color: String?,
        size: String?,
        expiryDate: Long? = null,
        sku: String? = null,
        barcode: String? = null,
        baseUnit: String = "حبة",
        altUnit: String? = null,
        conversionFactor: Double = 1.0,
        onSuccess: () -> Unit = {},
        onSuccessProduct: (Product) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            if (!sku.isNullOrBlank()) {
                val existing = repository.getProductBySku(sku)
                if (existing != null) {
                    onError("هذا الكود (SKU) مرتبط بالفعل بمنتج موجود.")
                    return@launch
                }
            }
            if (!barcode.isNullOrBlank()) {
                val existing = repository.getProductByBarcode(barcode)
                if (existing != null) {
                    onError("هذا الباركود مرتبط بالفعل بمنتج موجود.")
                    return@launch
                }
            }

            val newProd = Product(
                name = name,
                costPrice = cost,
                salePrice = salePrice,
                suggestedPrice = suggestedPrice,
                stockQuantity = stock,
                categoryId = categoryId,
                minStockAlert = minStockAlert,
                description = description,
                color = color,
                size = size,
                expiryDate = expiryDate,
                code = sku,
                barcode = barcode,
                baseUnit = baseUnit,
                altUnit = altUnit,
                conversionFactor = conversionFactor
            )
            repository.addProduct(newProd, stock)
            onSuccess()
            onSuccessProduct(newProd)
        }
    }

    fun updateProduct(
        product: Product,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            if (!product.code.isNullOrBlank()) {
                val existing = repository.getProductBySku(product.code)
                if (existing != null && existing.id != product.id) {
                    onError("هذا الكود (SKU) مرتبط بالفعل بمنتج موجود.")
                    return@launch
                }
            }
            if (!product.barcode.isNullOrBlank()) {
                val existing = repository.getProductByBarcode(product.barcode)
                if (existing != null && existing.id != product.id) {
                    onError("هذا الباركود مرتبط بالفعل بمنتج موجود.")
                    return@launch
                }
            }

            repository.updateProduct(product)
            onSuccess()
        }
    }

    fun adjustStock(productId: String, amount: Double, note: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.adjustStock(productId, amount, note)
            onSuccess()
        }
    }

    fun addCategory(name: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addCategory(com.example.data.local.ProductCategory(name = name))
            onSuccess()
        }
    }

    fun seedCategories() {
        viewModelScope.launch {
            val current = repository.allCategories.first()
            if (current.isEmpty()) {
                val defaults = listOf("ملابس وموضة", "إلكترونيات وهواتف", "أغذية وسوبرماركت", "عطور وتجميل", "أثاث وديكور", "عمومي")
                defaults.forEachIndexed { index, name ->
                    repository.addCategory(com.example.data.local.ProductCategory(name = name, sortOrder = index))
                }
            }
        }
    }

    fun applyDomainCategories(categoryNames: List<String>, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val current = repository.allCategories.first()
            val existingNames = current.map { it.name }.toSet()
            categoryNames.forEachIndexed { index, name ->
                if (!existingNames.contains(name)) {
                    repository.addCategory(com.example.data.local.ProductCategory(name = name, sortOrder = current.size + index))
                }
            }
            onSuccess()
        }
    }

    suspend fun canDeleteProduct(productId: String): Boolean = repository.canDeleteProduct(productId)

    fun archiveProduct(productId: String) {
        viewModelScope.launch { repository.archiveProduct(productId) }
    }

    fun restoreProduct(productId: String) {
        viewModelScope.launch { repository.restoreProduct(productId) }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch { repository.deleteProduct(productId) }
    }

    val totalSales: StateFlow<Double> = repository.totalSales
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalSupplierDebts: StateFlow<Double> = repository.allSuppliers
        .map { suppliers -> suppliers.sumOf { it.balance.coerceAtLeast(0.0) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCustomerDebts: StateFlow<Double> = repository.allCustomers
        .map { customers -> customers.sumOf { it.balance.coerceAtLeast(0.0) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalProfit: StateFlow<Double> = repository.totalProfit
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cashMovements: StateFlow<List<CashMovement>> = repository.cashMovements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cashBalance: StateFlow<Double> = repository.cashBalance
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
        
    val operationalExpenses: StateFlow<Double> = repository.operationalExpenses
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val allSuppliers: StateFlow<List<Supplier>> = repository.allSuppliers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCustomers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSupplier(name: String, phone: String, address: String = "", notes: String = "", onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addSupplier(Supplier(
                name = name, 
                phone = phone.takeIf { it.isNotBlank() },
                address = address.takeIf { it.isNotBlank() },
                notes = notes.takeIf { it.isNotBlank() }
            ))
            onSuccess()
        }
    }
    
    suspend fun getSupplierById(id: String) = repository.getSupplierById(id)
    suspend fun getSupplierPayments(supplierId: String) = repository.getSupplierPayments(supplierId)
    suspend fun getSupplierPurchases(supplierId: String) = repository.getSupplierPurchases(supplierId)
    
    fun addSupplierPayment(supplierId: String, amount: Double, method: String, reference: String?, note: String?, currencyCode: String = "YER", exchangeRate: Double = 1.0, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addSupplierPayment(supplierId, amount, method, reference, note, currencyCode, exchangeRate)
            onSuccess()
        }
    }

    fun updateSupplier(supplier: Supplier, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateSupplier(supplier)
            onSuccess()
        }
    }

    fun archiveSupplier(supplierId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.archiveSupplier(supplierId)
            onSuccess()
        }
    }

    fun addCustomer(name: String, phone: String, address: String = "", notes: String = "", onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addCustomer(Customer(
                name = name, 
                phone = phone.takeIf { it.isNotBlank() },
                address = address.takeIf { it.isNotBlank() },
                notes = notes.takeIf { it.isNotBlank() }
            ))
            onSuccess()
        }
    }

    fun updateCustomer(customer: Customer, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
            onSuccess()
        }
    }

    fun archiveCustomer(customerId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.archiveCustomer(customerId)
            onSuccess()
        }
    }

    suspend fun getCustomerById(id: String) = repository.getCustomerById(id)
    suspend fun getCustomerPayments(customerId: String) = repository.getCustomerPayments(customerId)
    suspend fun getCustomerSales(customerId: String) = repository.getCustomerSales(customerId)
    
    fun addCustomerPayment(customerId: String, amount: Double, method: String, reference: String?, note: String?, currencyCode: String = "YER", exchangeRate: Double = 1.0, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addCustomerPayment(customerId, amount, method, reference, note, currencyCode, exchangeRate)
            onSuccess()
        }
    }

    fun processPurchase(
        supplierId: String,
        items: List<PurchaseInvoiceItem>,
        discount: Double,
        paidAmount: Double,
        currencyCode: String = "YER",
        exchangeRate: Double = 1.0,
        warehouseId: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.processPurchase(supplierId, items, discount, paidAmount, currencyCode, exchangeRate, warehouseId)
            onSuccess()
        }
    }

    val allExpenses: StateFlow<List<Expense>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAccounts: StateFlow<List<com.example.data.local.Account>> = repository.allAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWarehouses: StateFlow<List<com.example.data.local.Warehouse>> = repository.allWarehouses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStockTransfers: StateFlow<List<com.example.data.local.StockTransfer>> = repository.allStockTransfers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun processStockTransfer(
        fromWarehouseId: String,
        toWarehouseId: String,
        items: List<com.example.data.local.StockTransferItem>,
        note: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.processStockTransfer(fromWarehouseId, toWarehouseId, items, note)
            onSuccess()
        }
    }

    fun addWarehouse(name: String, location: String, isDefault: Boolean = false, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addWarehouse(
                com.example.data.local.Warehouse(
                    name = name,
                    location = location.takeIf { it.isNotBlank() },
                    isDefault = isDefault
                )
            )
            onSuccess()
        }
    }

    fun addAccount(code: String, name: String, type: String, parentId: String? = null, initialBalance: Double = 0.0, currencyCode: String = "YER", onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addAccount(
                com.example.data.local.Account(
                    code = code,
                    name = name,
                    type = type,
                    parentId = parentId,
                    initialBalance = initialBalance,
                    currentBalance = initialBalance,
                    currencyCode = currencyCode
                )
            )
            onSuccess()
        }
    }

    fun updateExpense(expense: Expense, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateExpense(expense)
            onSuccess()
        }
    }

    fun archiveExpense(id: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.archiveExpense(id)
            onSuccess()
        }
    }

    fun addExpense(categoryId: String, type: String, amount: Double, note: String, currencyCode: String = "YER", exchangeRate: Double = 1.0, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addExpense(
                Expense(
                    categoryId = categoryId,
                    expenseType = type,
                    amount = amount,
                    currencyCode = currencyCode,
                    exchangeRate = exchangeRate,
                    amountMain = amount * exchangeRate,
                    note = note.takeIf { it.isNotBlank() }
                )
            )
            onSuccess()
        }
    }

    fun processReturn(
        originalInvoice: SalesInvoice,
        returnItems: List<SalesInvoiceItem>,
        refundCash: Boolean,
        returnAmount: Double,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.processReturn(originalInvoice, returnItems, refundCash, returnAmount)
            onSuccess()
        }
    }

    fun processSale(
        customerName: String,
        customerId: String?,
        items: List<SalesInvoiceItem>,
        discount: Double,
        paidAmount: Double,
        currencyCode: String = "YER",
        exchangeRate: Double = 1.0,
        warehouseId: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.processSale(customerName, customerId, items, discount, paidAmount, currencyCode, exchangeRate, warehouseId)
            onSuccess()
        }
    }

    fun exportData(context: android.content.Context, uri: android.net.Uri, isDelta: Boolean = false, fromTime: Long? = null, toTime: Long? = null, onComplete: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                repository.syncEngine.exportData(uri, isDelta, fromTime, toTime)
                onComplete()
            } catch (e: Throwable) {
                onError(Exception(e))
            }
        }
    }

    fun getSyncPreview(uri: android.net.Uri, onPreview: (com.example.data.sync.SyncPreview?) -> Unit) {
        viewModelScope.launch {
            try {
                val preview = repository.syncEngine.getPreview(uri)
                onPreview(preview)
            } catch (e: Throwable) {
                onPreview(null)
            }
        }
    }

    fun restoreBackup(context: android.content.Context, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val success = repository.syncEngine.restoreBackup()
                onComplete(success)
                if (success) {
                    // Force restart or exit to re-init DB
                    kotlin.system.exitProcess(0)
                }
            } catch (e: Throwable) {
                onComplete(false)
            }
        }
    }
    private val _importState = kotlinx.coroutines.flow.MutableStateFlow<com.example.data.sync.ImportState>(com.example.data.sync.ImportState.Idle)
    val importState: kotlinx.coroutines.flow.StateFlow<com.example.data.sync.ImportState> = _importState

    fun importData(context: android.content.Context, uri: android.net.Uri) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            _importState.value = com.example.data.sync.ImportState.Loading(0, "بدء الاستيراد...")
            try {
                val result = repository.syncEngine.importData(uri) { progress, message ->
                    _importState.value = com.example.data.sync.ImportState.Loading(progress, message)
                }
                _importState.value = com.example.data.sync.ImportState.Success(result)
            } catch (e: Throwable) {
                android.util.Log.e("Import", "Import failed", e)
                _importState.value = com.example.data.sync.ImportState.Error(
                    e.localizedMessage ?: "حدث خطأ غير متوقع أثناء الاستيراد"
                )
            }
        }
    }
    
    fun resetImportState() {
        _importState.value = com.example.data.sync.ImportState.Idle
    }
}

class StoreViewModelFactory(private val repository: StoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
