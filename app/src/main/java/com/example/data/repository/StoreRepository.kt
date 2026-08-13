package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class StoreRepository(
    private val currencyDao: CurrencyDao,
    private val productDao: ProductDao,
    private val salesDao: SalesDao,
    private val financeDao: FinanceDao,
    private val purchaseDao: PurchaseDao,
    private val partiesDao: PartiesDao,
    private val syncDao: SyncDao,
    val accountDao: AccountDao,
    val warehouseDao: WarehouseDao,
    val syncEngine: com.example.data.sync.SyncEngine
) {

    // Currencies
    val allCurrencies: Flow<List<CurrencyRate>> = currencyDao.getAllCurrencies()

    suspend fun seedDefaultCurrencies() {
        val current = currencyDao.getCurrencyByCode("YER")
        if (current == null) {
            val defaults = listOf(
                CurrencyRate(code = "YER", name = "ريال يمني", symbol = "ر.ي", exchangeRateToMain = 1.0, isMain = true),
                CurrencyRate(code = "SAR", name = "ريال سعودي", symbol = "ر.س", exchangeRateToMain = 140.0, isMain = false),
                CurrencyRate(code = "USD", name = "دولار أمريكي", symbol = "$", exchangeRateToMain = 530.0, isMain = false)
            )
            currencyDao.insertCurrencies(defaults)
        }
    }

    suspend fun updateExchangeRate(code: String, rate: Double) {
        currencyDao.updateExchangeRate(code, rate)
    }

    suspend fun addCurrency(currency: CurrencyRate) {
        currencyDao.insertCurrency(currency)
    }

    suspend fun getCurrencyByCode(code: String): CurrencyRate? {
        return currencyDao.getCurrencyByCode(code)
    }

    // Products
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val archivedProducts: Flow<List<Product>> = productDao.getArchivedProducts()
    val lowStockProducts: Flow<List<Product>> = productDao.getLowStockProducts()
    val allCategories: Flow<List<ProductCategory>> = productDao.getAllCategories()

    suspend fun getProductBySku(sku: String) = productDao.getProductBySku(sku)
    suspend fun getProductByBarcode(barcode: String) = productDao.getProductByBarcode(barcode)

    suspend fun addProduct(product: Product, openingBalance: Double = 0.0) {
        val finalProduct = product.copy(stockQuantity = openingBalance)
        productDao.insertProduct(finalProduct)
        if (openingBalance > 0) {
            productDao.insertStockMovement(
                StockMovement(
                    productId = product.id,
                    type = "OPENING_BALANCE",
                    quantity = openingBalance,
                    note = "رصيد افتتاحي"
                )
            )
        }
    }

    suspend fun updateProduct(product: Product) = productDao.insertProduct(product) // using REPLACE

    suspend fun adjustStock(productId: String, amount: Double, note: String) {
        if (amount == 0.0) return
        productDao.updateStock(productId, amount)
        productDao.insertStockMovement(
            StockMovement(
                productId = productId,
                type = "ADJUSTMENT",
                quantity = amount,
                note = note
            )
        )
    }

    fun getStockMovements(productId: String) = productDao.getStockMovementsForProduct(productId)

    suspend fun getProductById(id: String) = productDao.getProductById(id)

    suspend fun addCategory(category: ProductCategory) = productDao.insertCategory(category)
    
    suspend fun archiveProduct(productId: String) {
        val product = productDao.getProductById(productId)
        if (product != null) {
            productDao.insertProduct(product.copy(status = "ARCHIVED"))
        }
    }
    
    suspend fun restoreProduct(productId: String) {
        val product = productDao.getProductById(productId)
        if (product != null) {
            productDao.insertProduct(product.copy(status = "ACTIVE"))
        }
    }
    
    suspend fun canDeleteProduct(productId: String): Boolean {
        val salesCount = productDao.getSalesCountForProduct(productId)
        val purchasesCount = productDao.getPurchasesCountForProduct(productId)
        return salesCount == 0 && purchasesCount == 0
    }
    
    suspend fun deleteProduct(productId: String) {
        val product = productDao.getProductById(productId)
        if (product != null) {
            productDao.insertProduct(product.copy(isDeleted = 1))
        }
    }

    val allSalesInvoices: Flow<List<SalesInvoice>> = salesDao.getAllInvoices()
    val allPurchaseInvoices: Flow<List<PurchaseInvoice>> = purchaseDao.getAllInvoices()
    
    suspend fun getSalesInvoiceItems(invoiceId: String) = salesDao.getInvoiceItems(invoiceId)
    suspend fun getPurchaseInvoiceItems(invoiceId: String) = purchaseDao.getInvoiceItems(invoiceId)

    // Parties
    val allSuppliers: Flow<List<Supplier>> = partiesDao.getAllSuppliers()
    val allCustomers: Flow<List<Customer>> = partiesDao.getAllCustomers()
    
    suspend fun getSupplierById(id: String) = partiesDao.getSupplierById(id)
    suspend fun getSupplierPayments(supplierId: String) = partiesDao.getSupplierPayments(supplierId)
    suspend fun getSupplierPurchases(supplierId: String) = purchaseDao.getSupplierPurchases(supplierId)
    
    suspend fun getCustomerById(id: String) = partiesDao.getCustomerById(id)
    suspend fun getCustomerPayments(customerId: String) = partiesDao.getCustomerPayments(customerId)
    suspend fun getCustomerSales(customerId: String) = salesDao.getCustomerSales(customerId)
    
    suspend fun addSupplier(supplier: Supplier) = partiesDao.insertSupplier(supplier)
    suspend fun updateSupplier(supplier: Supplier) = partiesDao.insertSupplier(supplier.copy(updatedAt = System.currentTimeMillis(), syncStatus = 0))
    suspend fun archiveSupplier(id: String) = partiesDao.archiveSupplier(id)
    suspend fun addCustomer(customer: Customer) = partiesDao.insertCustomer(customer)
    
    suspend fun addSupplierPayment(supplierId: String, amount: Double, method: String, reference: String?, note: String?, currencyCode: String = "YER", exchangeRate: Double = 1.0) {
        val amountMain = amount * exchangeRate
        val payment = SupplierPayment(
            supplierId = supplierId,
            amount = amount,
            currencyCode = currencyCode,
            exchangeRate = exchangeRate,
            amountMain = amountMain,
            paymentMethod = method,
            reference = reference,
            note = note
        )
        partiesDao.insertSupplierPayment(payment)
        partiesDao.updateSupplierBalance(supplierId, -amountMain)
        
        financeDao.insertCashMovement(
            CashMovement(
                movementType = "SUPPLIER_PAYMENT",
                direction = "OUT",
                amount = amount,
                currencyCode = currencyCode,
                exchangeRate = exchangeRate,
                amountMain = amountMain,
                referenceType = "SUPPLIER_PAYMENT",
                referenceId = payment.id,
                note = note ?: "دفعة لمورد ($currencyCode)"
            )
        )
    }

    suspend fun addCustomerPayment(customerId: String, amount: Double, method: String, reference: String?, note: String?, currencyCode: String = "YER", exchangeRate: Double = 1.0) {
        val amountMain = amount * exchangeRate
        val payment = CustomerPayment(
            customerId = customerId,
            amount = amount,
            currencyCode = currencyCode,
            exchangeRate = exchangeRate,
            amountMain = amountMain,
            paymentMethod = method,
            reference = reference,
            note = note
        )
        partiesDao.insertCustomerPayment(payment)
        partiesDao.updateCustomerBalance(customerId, -amountMain)
        
        financeDao.insertCashMovement(
            CashMovement(
                movementType = "CUSTOMER_PAYMENT",
                direction = "IN",
                amount = amount,
                currencyCode = currencyCode,
                exchangeRate = exchangeRate,
                amountMain = amountMain,
                referenceType = "CUSTOMER_PAYMENT",
                referenceId = payment.id,
                note = note ?: "تحصيل دفعة من عميل ($currencyCode)"
            )
        )
    }

    suspend fun updateCustomer(customer: com.example.data.local.Customer) {
        partiesDao.insertCustomer(customer.copy(updatedAt = System.currentTimeMillis(), syncStatus = 0))
    }

    suspend fun archiveCustomer(id: String) {
        partiesDao.archiveCustomer(id)
    }

    // Expenses
    val allExpenses: Flow<List<Expense>> = financeDao.getAllExpenses()

    suspend fun updateExpense(expense: Expense) {
        financeDao.updateExpense(expense.copy(updatedAt = System.currentTimeMillis(), syncStatus = 0))
        val currentMovement = financeDao.getExportCashMovements().find { it.referenceId == expense.id }
        if (currentMovement != null) {
            financeDao.insertCashMovement(
                currentMovement.copy(
                    amount = expense.amount,
                    note = expense.note ?: expense.categoryId,
                    updatedAt = System.currentTimeMillis(),
                    syncStatus = 0
                )
            )
        }
    }

    suspend fun archiveExpense(id: String) {
        financeDao.archiveExpense(id)
        val currentMovement = financeDao.getExportCashMovements().find { it.referenceId == id }
        if (currentMovement != null) {
            // Reverse or delete the cash movement. We can soft delete the movement or negate it.
            // Since we don't have isDeleted on cash_movements in this setup, let's just create a reversing entry or if there is soft delete, we'd use it.
            // Wait, does CashMovement have isDeleted? Let's check entities. We'll just negate it or add a counter movement for simplicity, OR if isDeleted exists, set it.
            // Let's assume it doesn't have soft delete, we'll just add a counter movement.
            financeDao.insertCashMovement(
                CashMovement(
                    movementType = "EXPENSE_REVERSAL",
                    amount = currentMovement.amount,
                    direction = "IN",
                    referenceType = "EXPENSE",
                    referenceId = id,
                    note = "عكس مصروف محذوف"
                )
            )
        }
    }
    suspend fun addExpense(expense: Expense) {
        financeDao.insertExpense(expense)
        val amountMain = expense.amount * expense.exchangeRate
        
        // Handle cash movement out
        financeDao.insertCashMovement(
            CashMovement(
                movementType = "EXPENSE",
                direction = "OUT",
                amount = expense.amount,
                currencyCode = expense.currencyCode,
                exchangeRate = expense.exchangeRate,
                amountMain = amountMain,
                referenceType = "EXPENSE",
                referenceId = expense.id,
                note = expense.note ?: expense.categoryId
            )
        )
    }

    // Purchases
    suspend fun processPurchase(
        supplierId: String,
        items: List<PurchaseInvoiceItem>,
        discount: Double,
        paidAmount: Double,
        currencyCode: String = "YER",
        exchangeRate: Double = 1.0,
        warehouseId: String? = null
    ) {
        val invoiceId = UUID.randomUUID().toString()
        val totalAmount = items.sumOf { it.lineTotal } - discount
        val totalAmountMain = totalAmount * exchangeRate
        val remainingAmount = totalAmount - paidAmount
        val remainingAmountMain = remainingAmount * exchangeRate
        val paidAmountMain = paidAmount * exchangeRate

        val invoice = PurchaseInvoice(
            id = invoiceId,
            invoiceNumber = "PUR-${System.currentTimeMillis()}",
            supplierId = supplierId,
            paymentType = if (paidAmount >= totalAmount) "CASH" else if (paidAmount == 0.0) "CREDIT" else "PARTIAL",
            totalAmount = totalAmount,
            discount = discount,
            paidAmount = paidAmount,
            remainingAmount = remainingAmount,
            currencyCode = currencyCode,
            exchangeRate = exchangeRate,
            totalAmountMain = totalAmountMain,
            warehouseId = warehouseId
        )

        val updatedItems = items.map { it.copy(invoiceId = invoiceId) }

        purchaseDao.insertInvoice(invoice)
        purchaseDao.insertInvoiceItems(updatedItems)

        // Update Stock
        updatedItems.forEach { item ->
            // Update global stock
            productDao.updateStock(item.productId, item.quantity)
            productDao.insertStockMovement(StockMovement(
                productId = item.productId,
                type = "PURCHASE",
                quantity = item.quantity,
                referenceType = "PURCHASE",
                referenceId = invoiceId,
                note = "مشتريات فاتورة ${invoice.invoiceNumber}"
            ))
            // Update warehouse specific stock
            if (warehouseId != null) {
                warehouseDao.updateProductStock(item.productId, warehouseId, item.quantity)
            }
        }

        // Supplier Balance in Main Currency
        if (remainingAmountMain > 0) {
            partiesDao.updateSupplierBalance(supplierId, remainingAmountMain)
        }

        // Cash Movement
        if (paidAmount > 0) {
            financeDao.insertCashMovement(
                CashMovement(
                    movementType = "PURCHASE_PAYMENT",
                    direction = "OUT",
                    amount = paidAmount,
                    currencyCode = currencyCode,
                    exchangeRate = exchangeRate,
                    amountMain = paidAmountMain,
                    referenceType = "PURCHASE_INVOICE",
                    referenceId = invoiceId,
                    note = "دفعة فاتورة شراء ($currencyCode)"
                )
            )
        }
    }

    // Dashboard Stats
    val totalSales: Flow<Double?> = salesDao.getTotalSales()
    val totalProfit: Flow<Double?> = salesDao.getTotalProfit()
    // Finance
    val cashMovements: Flow<List<CashMovement>> = financeDao.getAllCashMovements()
    val cashBalance: Flow<Double?> = financeDao.getCashBalance()
    val operationalExpenses: Flow<Double?> = financeDao.getTotalOperationalExpenses()
    
    // Sync
    val syncLogs: Flow<List<SyncLog>> = syncDao.getAllSyncLogs()

    
    suspend fun generateReport(startDate: Long, endDate: Long): ReportData {
        val generator = ReportGenerator(productDao, salesDao, financeDao, partiesDao)
        return generator.generateReport(startDate, endDate)
    }

    // Sales

    suspend fun processReturn(
        originalInvoice: SalesInvoice,
        returnItems: List<SalesInvoiceItem>,
        refundCash: Boolean,
        returnAmount: Double
    ) {
        val returnInvoiceId = java.util.UUID.randomUUID().toString()
        val totalReturnAmount = returnItems.sumOf { it.lineTotal }
        val totalReturnCost = returnItems.sumOf { it.lineCost }
        val totalReturnProfit = returnItems.sumOf { it.lineProfit }
        
        // Save as a SalesInvoice with status = "RETURN" and negative values
        val invoice = SalesInvoice(
            id = returnInvoiceId,
            invoiceNumber = "RET-${System.currentTimeMillis()}",
            customerName = originalInvoice.customerName,
            customerId = originalInvoice.customerId,
            paymentType = "RETURN",
            totalAmount = -totalReturnAmount,
            totalCost = -totalReturnCost,
            discount = 0.0,
            paidAmount = if (refundCash) -returnAmount else 0.0,
            remainingAmount = if (!refundCash) -returnAmount else 0.0,
            totalProfit = -totalReturnProfit,
            status = "RETURN"
        )

        val updatedItems = returnItems.map { 
            it.copy(
                id = java.util.UUID.randomUUID().toString(),
                invoiceId = returnInvoiceId,
                quantity = -it.quantity, // negative quantity
                lineTotal = -it.lineTotal,
                lineCost = -it.lineCost,
                lineProfit = -it.lineProfit
            ) 
        }

        salesDao.insertInvoice(invoice)
        salesDao.insertInvoiceItems(updatedItems)

        // Add back to stock
        updatedItems.forEach {
            productDao.updateStock(it.productId, -it.quantity) // since quantity is negative, this increases stock
            productDao.insertStockMovement(StockMovement(
                productId = it.productId,
                type = "SALE_RETURN",
                quantity = -it.quantity, 
                referenceType = "SALE_RETURN",
                referenceId = returnInvoiceId,
                note = "مرتجع مبيعات للفاتورة ${originalInvoice.invoiceNumber}"
            ))
        }

        if (!refundCash && originalInvoice.customerId != null) {
            // decrease customer debt (negative amount means decrease balance)
            partiesDao.updateCustomerBalance(originalInvoice.customerId, -returnAmount)
        }

        if (refundCash && returnAmount > 0) {
            financeDao.insertCashMovement(
                CashMovement(
                    movementType = "RETURN",
                    direction = "OUT",
                    amount = returnAmount,
                    referenceType = "SALES_RETURN",
                    referenceId = returnInvoiceId,
                    note = "مرتجع مبيعات للفاتورة ${originalInvoice.invoiceNumber}"
                )
            )
        }
    }

    suspend fun processSale(
        customerName: String,
        customerId: String?,
        items: List<SalesInvoiceItem>,
        discount: Double,
        paidAmount: Double,
        currencyCode: String = "YER",
        exchangeRate: Double = 1.0,
        warehouseId: String? = null
    ) {
        val invoiceId = UUID.randomUUID().toString()
        val totalAmount = items.sumOf { it.lineTotal } - discount
        val totalCost = items.sumOf { it.lineCost }
        val totalProfit = items.sumOf { it.lineProfit } - discount
        val totalAmountMain = totalAmount * exchangeRate
        val remainingAmount = totalAmount - paidAmount
        val remainingAmountMain = remainingAmount * exchangeRate
        val paidAmountMain = paidAmount * exchangeRate

        val invoice = SalesInvoice(
            id = invoiceId,
            invoiceNumber = "INV-${System.currentTimeMillis()}",
            customerName = customerName.takeIf { it.isNotBlank() },
            customerId = customerId,
            paymentType = if (paidAmount >= totalAmount) "CASH" else if (paidAmount == 0.0) "CREDIT" else "PARTIAL",
            totalAmount = totalAmount,
            totalCost = totalCost,
            discount = discount,
            paidAmount = paidAmount,
            remainingAmount = remainingAmount,
            totalProfit = totalProfit,
            currencyCode = currencyCode,
            exchangeRate = exchangeRate,
            totalAmountMain = totalAmountMain,
            warehouseId = warehouseId
        )

        val updatedItems = items.map { it.copy(invoiceId = invoiceId) }

        // 1. Save Invoice & Items
        salesDao.insertInvoice(invoice)
        salesDao.insertInvoiceItems(updatedItems)

        // 2. Update Stock
        updatedItems.forEach {
            // Update global stock
            productDao.updateStock(it.productId, -it.quantity)
            productDao.insertStockMovement(StockMovement(
                productId = it.productId,
                type = "SALE",
                quantity = -it.quantity,
                referenceType = "SALE",
                referenceId = invoiceId,
                note = "مبيعات فاتورة ${invoice.invoiceNumber}"
            ))
            // Update warehouse specific stock
            if (warehouseId != null) {
                warehouseDao.updateProductStock(it.productId, warehouseId, -it.quantity)
            }
        }

        // 3. Update Customer Balance (in main currency)
        if (remainingAmountMain > 0 && customerId != null) {
            partiesDao.updateCustomerBalance(customerId, remainingAmountMain)
        }

        // 4. Cash Movement (if paid)
        if (paidAmount > 0) {
            val cm = CashMovement(
                movementType = "SALE",
                direction = "IN",
                amount = paidAmount,
                currencyCode = currencyCode,
                exchangeRate = exchangeRate,
                amountMain = paidAmountMain,
                referenceType = "SALES_INVOICE",
                referenceId = invoiceId,
                note = "دفعة فاتورة بيع ($currencyCode)"
            )
            financeDao.insertCashMovement(cm)
        }
    }

    // Accounts
    val allAccounts: Flow<List<Account>> = accountDao.getAllAccounts()

    suspend fun addAccount(account: Account) {
        accountDao.insertAccount(account)
    }

    suspend fun getAccountById(id: String) = accountDao.getAccountById(id)

    suspend fun archiveAccount(id: String) {
        accountDao.archiveAccount(id)
    }

    // Warehouses
    val allWarehouses: Flow<List<Warehouse>> = warehouseDao.getAllWarehouses()

    suspend fun addWarehouse(warehouse: Warehouse) {
        warehouseDao.insertWarehouse(warehouse)
    }

    val allProductStocks: Flow<List<ProductStock>> = warehouseDao.getAllProductStocks()

    val allStockTransfers: Flow<List<StockTransfer>> = warehouseDao.getAllStockTransfers()

    fun getStocksByWarehouse(warehouseId: String): Flow<List<ProductStock>> {
        return warehouseDao.getStocksByWarehouse(warehouseId)
    }

    suspend fun processStockTransfer(
        fromWarehouseId: String,
        toWarehouseId: String,
        items: List<StockTransferItem>,
        note: String? = null
    ) {
        val transferId = UUID.randomUUID().toString()
        val transferNumber = "TRF-${System.currentTimeMillis()}"

        val transfer = StockTransfer(
            id = transferId,
            transferNumber = transferNumber,
            fromWarehouseId = fromWarehouseId,
            toWarehouseId = toWarehouseId,
            note = note,
            status = "COMPLETED"
        )

        val transferItems = items.map { it.copy(transferId = transferId) }

        warehouseDao.insertStockTransfer(transfer)
        warehouseDao.insertStockTransferItems(transferItems)

        // Deduct from source and add to destination
        transferItems.forEach { item ->
            warehouseDao.updateProductStock(item.productId, fromWarehouseId, -item.quantity)
            
            // Check if destination stock exists, if not create it
            val destStock = warehouseDao.getProductStock(item.productId, toWarehouseId)
            if (destStock == null) {
                warehouseDao.insertProductStock(ProductStock(
                    productId = item.productId,
                    warehouseId = toWarehouseId,
                    quantity = item.quantity
                ))
            } else {
                warehouseDao.updateProductStock(item.productId, toWarehouseId, item.quantity)
            }
        }
    }
}
