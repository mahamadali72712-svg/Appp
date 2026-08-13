package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// Common sync metadata fields
// deviceId: String? = null
// version: Int = 1
// syncStatus: Int = 0
// createdAt: Long = System.currentTimeMillis()
// updatedAt: Long = System.currentTimeMillis()
// isDeleted: Int = 0

@Entity(tableName = "currency_rates")
data class CurrencyRate(
    @PrimaryKey val code: String, // e.g. "YER", "SAR", "USD"
    val name: String, // e.g. "ريال يمني", "ريال سعودي", "دولار أمريكي"
    val symbol: String, // e.g. "ر.ي", "ر.س", "$"
    val exchangeRateToMain: Double = 1.0, // relative to main currency (e.g., 140.0 for SAR to YER)
    val isMain: Boolean = false,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "product_categories")
data class ProductCategory(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val icon: String? = null,
    val sortOrder: Int = 0,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val categoryId: String? = null,
    val code: String? = null,
    val barcode: String? = null,
    val baseUnit: String = "حبة",
    val altUnit: String? = null,
    val conversionFactor: Double = 1.0,
    val description: String? = null,
    val color: String? = null,
    val size: String? = null,
    val expiryDate: Long? = null,
    val stockQuantity: Double = 0.0,
    val costPrice: Double = 0.0,
    val salePrice: Double = 0.0,
    val suggestedPrice: Double = 0.0,
    val minStockAlert: Double = 0.0,
    val status: String = "ACTIVE",
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "stock_movements")
data class StockMovement(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val type: String, // PURCHASE, SALE, PURCHASE_RETURN, SALE_RETURN, OPENING_BALANCE, ADJUSTMENT, STOCKTAKE
    val quantity: Double, // positive or negative
    val referenceType: String? = null,
    val referenceId: String? = null,
    val note: String? = null,
    val movementDate: Long = System.currentTimeMillis(),
    val warehouseId: String? = null,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "sales_invoices")
data class SalesInvoice(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val invoiceNumber: String,
    val customerName: String?,
    val customerId: String? = null,
    val invoiceDate: Long = System.currentTimeMillis(),
    val paymentType: String, // CASH, CREDIT, PARTIAL
    val totalAmount: Double,
    val totalCost: Double,
    val discount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val totalProfit: Double = 0.0,
    val currencyCode: String = "YER",
    val exchangeRate: Double = 1.0,
    val totalAmountMain: Double = totalAmount,
    val status: String = "CONFIRMED",
    val warehouseId: String? = null,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "sales_invoice_items")
data class SalesInvoiceItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val invoiceId: String,
    val productId: String,
    val quantity: Double,
    val unitPrice: Double, 
    val unitCost: Double, 
    val lineTotal: Double,
    val lineCost: Double,
    val lineProfit: Double,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val categoryId: String,
    val expenseType: String, // OPERATIONAL or PERSONAL
    val amount: Double,
    val currencyCode: String = "YER",
    val exchangeRate: Double = 1.0,
    val amountMain: Double = amount,
    val expenseDate: Long = System.currentTimeMillis(),
    val note: String?,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String? = null,
    val address: String? = null,
    val notes: String? = null,
    val balance: Double = 0.0, // positive = I owe them in main currency
    val totalPurchased: Double = 0.0,
    val totalPaid: Double = 0.0,
    val lastTransactionDate: Long? = null,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "supplier_payments")
data class SupplierPayment(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val supplierId: String,
    val amount: Double,
    val currencyCode: String = "YER",
    val exchangeRate: Double = 1.0,
    val amountMain: Double = amount,
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentMethod: String, // CASH, BANK
    val reference: String? = null,
    val note: String? = null,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String? = null,
    val address: String? = null,
    val notes: String? = null,
    val balance: Double = 0.0, // positive = they owe me in main currency
    val totalPurchased: Double = 0.0,
    val totalPaid: Double = 0.0,
    val lastTransactionDate: Long? = null,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "customer_payments")
data class CustomerPayment(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val customerId: String,
    val amount: Double,
    val currencyCode: String = "YER",
    val exchangeRate: Double = 1.0,
    val amountMain: Double = amount,
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentMethod: String, // CASH, BANK
    val reference: String? = null,
    val note: String? = null,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "purchase_invoices")
data class PurchaseInvoice(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val invoiceNumber: String,
    val supplierId: String,
    val invoiceDate: Long = System.currentTimeMillis(),
    val paymentType: String,
    val totalAmount: Double,
    val discount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val currencyCode: String = "YER",
    val exchangeRate: Double = 1.0,
    val totalAmountMain: Double = totalAmount,
    val status: String = "CONFIRMED",
    val warehouseId: String? = null,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "purchase_invoice_items")
data class PurchaseInvoiceItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val invoiceId: String,
    val productId: String,
    val quantity: Double,
    val unitCost: Double,
    val lineTotal: Double,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "cash_movements")
data class CashMovement(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val movementType: String,
    val direction: String, // IN or OUT
    val amount: Double,
    val currencyCode: String = "YER",
    val exchangeRate: Double = 1.0,
    val amountMain: Double = amount,
    val balanceAfter: Double = 0.0,
    val referenceType: String? = null,
    val referenceId: String? = null,
    val note: String? = null,
    val movementDate: Long = System.currentTimeMillis(),
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "sync_logs")
data class SyncLog(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val syncDate: Long = System.currentTimeMillis(),
    val syncType: String, // IMPORT or EXPORT
    val sourceDevice: String?,
    val recordsAdded: Int = 0,
    val recordsUpdated: Int = 0,
    val recordsSkipped: Int = 0,
    val recordsDeleted: Int = 0,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val code: String, // e.g., "1", "11", "111"
    val name: String,
    val type: String, // e.g., "ASSET", "LIABILITY", "EQUITY", "REVENUE", "EXPENSE"
    val parentId: String? = null,
    val initialBalance: Double = 0.0,
    val currentBalance: Double = 0.0,
    val currencyCode: String = "YER",
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "warehouses")
data class Warehouse(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val location: String? = null,
    val isDefault: Boolean = false,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "product_stocks")
data class ProductStock(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val warehouseId: String,
    val quantity: Double = 0.0,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "stock_transfers")
data class StockTransfer(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val transferNumber: String,
    val fromWarehouseId: String,
    val toWarehouseId: String,
    val transferDate: Long = System.currentTimeMillis(),
    val note: String? = null,
    val status: String = "COMPLETED", // PENDING, COMPLETED
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)

@Entity(tableName = "stock_transfer_items")
data class StockTransferItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val transferId: String,
    val productId: String,
    val quantity: Double,
    val deviceId: String? = null,
    val version: Int = 1,
    val syncStatus: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Int = 0
)
