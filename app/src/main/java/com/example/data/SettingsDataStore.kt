package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    fun <T> getSetting(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    suspend fun <T> saveSetting(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}

object SettingsKeys {
    // Personal
    val ARABIC_NAME = stringPreferencesKey("arabic_name")
    val ENGLISH_NAME = stringPreferencesKey("english_name")
    val ARABIC_ADDRESS = stringPreferencesKey("arabic_address")
    val ENGLISH_ADDRESS = stringPreferencesKey("english_address")
    val CONTACT_NUMBERS = stringPreferencesKey("contact_numbers")
    val USE_READY_HEADER = booleanPreferencesKey("use_ready_header")
    val LOGO_SIZE = floatPreferencesKey("logo_size")
    val SIGNATURE_SIZE = floatPreferencesKey("signature_size")
    val STAMP_SIZE = floatPreferencesKey("stamp_size")
    val LOGO_URI = stringPreferencesKey("logo_uri")
    val STAMP_URI = stringPreferencesKey("stamp_uri")
    val SIGNATURE_URI = stringPreferencesKey("signature_uri")

    // Print
    val PRINT_METHOD = stringPreferencesKey("print_method")
    val PAPER_SIZE = stringPreferencesKey("paper_size")
    val CONNECTION_TYPE = stringPreferencesKey("connection_type")
    val SHOW_HEADER_DATA = booleanPreferencesKey("show_header_data")
    val REPEAT_HEADER = booleanPreferencesKey("repeat_header")
    val SHOW_PRINT_DATE = booleanPreferencesKey("show_print_date")
    val SHOW_PRINT_TIME = booleanPreferencesKey("show_print_time")
    val BONDS_STAMP_SIGNATURE = stringPreferencesKey("bonds_stamp_signature")
    val INVOICES_STAMP_SIGNATURE = stringPreferencesKey("invoices_stamp_signature")
    val ACCOUNT_STATEMENT_STAMP_SIGNATURE = stringPreferencesKey("account_statement_stamp_signature")
    val BALANCE_AUTH_STAMP_SIGNATURE = stringPreferencesKey("balance_auth_stamp_signature")
    val QUOTES_STAMP_SIGNATURE = stringPreferencesKey("quotes_stamp_signature")
    val SHOW_ACCOUNT_NOTE = booleanPreferencesKey("show_account_note")
    val SHOW_ACCOUNT_PHONE = booleanPreferencesKey("show_account_phone")
    val SHOW_BOND_NUMBER = booleanPreferencesKey("show_bond_number")
    val ADD_NOTE_TO_STATEMENT = booleanPreferencesKey("add_note_to_statement")
    val FONT_SIZE = floatPreferencesKey("font_size")
    val LINE_SPACING = floatPreferencesKey("line_spacing")
    val FIXED_ROWS_INVOICES = floatPreferencesKey("fixed_rows_invoices")
    val FIXED_ROWS_QUOTES = floatPreferencesKey("fixed_rows_quotes")

    // Vouchers
    val RECEIPT_TYPE_TITLE = stringPreferencesKey("receipt_type_title")
    val PAYMENT_TYPE_TITLE = stringPreferencesKey("payment_type_title")
    val RECEIPT_FORMAT_LINE_1 = stringPreferencesKey("receipt_format_line1")
    val RECEIPT_FORMAT_LINE_2 = stringPreferencesKey("receipt_format_line2")
    val PAYMENT_FORMAT_LINE_1 = stringPreferencesKey("payment_format_line1")
    val PAYMENT_FORMAT_LINE_2 = stringPreferencesKey("payment_format_line2")
    val ENTRY_FORMAT_DEBIT = stringPreferencesKey("entry_format_debit")
    val ENTRY_FORMAT_CREDIT = stringPreferencesKey("entry_format_credit")
    val SIGNATURE_BONDS = stringPreferencesKey("signature_bonds")
    val SIGNATURE_STATEMENTS = stringPreferencesKey("signature_statements")
    val BONDS_NOTE = stringPreferencesKey("bonds_note")
    val TRANSFERS_NOTE = stringPreferencesKey("transfers_note")
    val NOTIFICATIONS_NOTE = stringPreferencesKey("notifications_note")
    val STATEMENTS_NOTE = stringPreferencesKey("statements_note")
    val BALANCE_AUTH_NOTE = stringPreferencesKey("balance_auth_note")
    val UPDATE_TIME_WHEN_IDLE = booleanPreferencesKey("update_time_when_idle")
    val ADD_ACCOUNT_NAME_TO_ENTRY = booleanPreferencesKey("add_account_name_to_entry")
    val USE_DEBIT_CREDIT_BUTTONS = booleanPreferencesKey("use_debit_credit_buttons")
    val ADD_REMAINING_BALANCE = booleanPreferencesKey("add_remaining_balance")
    val SHOW_SENDER_RECEIVER_IN_MESSAGE = booleanPreferencesKey("show_sender_receiver_in_message")
    val ADOPT_INCOMING_TRANSFER_AS_PAYMENT = booleanPreferencesKey("adopt_incoming_transfer_as_payment")
    val SHOW_PREVIOUS_BALANCE = booleanPreferencesKey("show_previous_balance")
    val SHOW_TOTAL_OPERATIONS_COUNT = booleanPreferencesKey("show_total_operations_count")
    val CAN_ADD_FEE_TO_OUTGOING = booleanPreferencesKey("can_add_fee_to_outgoing")
    val CAN_ADD_FEE_TO_INCOMING = booleanPreferencesKey("can_add_fee_to_incoming")
    val SEARCH_SUGGESTIONS_ROWS = floatPreferencesKey("search_suggestions_rows")
    val MAX_ACCOUNT_NAME_LENGTH = floatPreferencesKey("max_account_name_length")

    // Invoices
    val FREE_INVOICE_NAME = stringPreferencesKey("free_invoice_name")
    val FREE_INVOICE_FORMAT = stringPreferencesKey("free_invoice_format")
    val STORE_INVOICE_SALE_FORMAT = stringPreferencesKey("store_invoice_sale_format")
    val STORE_INVOICE_PURCHASE_FORMAT = stringPreferencesKey("store_invoice_purchase_format")
    val QUOTE_FORMAT = stringPreferencesKey("quote_format")
    val INVOICE_SIGNATURE = stringPreferencesKey("invoice_signature")
    val INVOICE_NOTE = stringPreferencesKey("invoice_note")
    val DECIMAL_PLACES = floatPreferencesKey("decimal_places")
    val SHOW_INVOICE_ITEMS_IN_DESCRIPTION = booleanPreferencesKey("show_invoice_items_in_description")
    val USE_STORE_UNITS = booleanPreferencesKey("use_store_units")
    val SHOW_QUANTITY_AND_UNIT_IN_DESCRIPTION = booleanPreferencesKey("show_quantity_and_unit_in_description")
    val ENABLE_EXPIRATION_DATE = booleanPreferencesKey("enable_expiration_date")
    val SHOW_EXPIRATION_DATE_IN_INVOICE = booleanPreferencesKey("show_expiration_date_in_invoice")
    val SHOW_TOTAL_QUANTITY_IN_PRINT = booleanPreferencesKey("show_total_quantity_in_print")
    val ENABLE_DISCOUNT_IN_SALES = booleanPreferencesKey("enable_discount_in_sales")
    val ENABLE_PERCENTAGE_DISCOUNT = booleanPreferencesKey("enable_percentage_discount")
    val ENABLE_VAT_IN_SALES = booleanPreferencesKey("enable_vat_in_sales")
    val CALC_TAX_AFTER_DISCOUNT = booleanPreferencesKey("calc_tax_after_discount")
    val DEDUCT_FROM_INVENTORY_ON_SALE = booleanPreferencesKey("deduct_from_inventory_on_sale")
    val ALLOW_SELLING_BELOW_COST = booleanPreferencesKey("allow_selling_below_cost")
    val ALLOW_DUPLICATE_ITEMS = booleanPreferencesKey("allow_duplicate_items")
    val ALLOW_DUPLICATE_BARCODE = booleanPreferencesKey("allow_duplicate_barcode")
    val SELLER_NAME = stringPreferencesKey("seller_name")
    val TAX_NUMBER = stringPreferencesKey("tax_number")
    val DEFAULT_TAX_RATE = stringPreferencesKey("default_tax_rate")
    val PRINT_TAX_CODE = booleanPreferencesKey("print_tax_code")
    val SHOW_CUSTOMER_TAX_NUMBER = booleanPreferencesKey("show_customer_tax_number")
    val TAX_CODE_PRINT_SIZE = floatPreferencesKey("tax_code_print_size")

    // WhatsApp
    val TEXT_MESSAGE_INTRO = stringPreferencesKey("text_message_intro")
    val TEXT_MESSAGE_OUTRO = stringPreferencesKey("text_message_outro")
    val PAYMENT_REQ_OUTRO = stringPreferencesKey("payment_req_outro")
    val MESSAGE_FORMAT_DEBIT = stringPreferencesKey("message_format_debit")
    val MESSAGE_FORMAT_CREDIT = stringPreferencesKey("message_format_credit")
    val SEND_TEXT_METHOD = stringPreferencesKey("send_text_method")
    val PREFERRED_WHATSAPP = stringPreferencesKey("preferred_whatsapp")
    val COUNTRY_CODE = stringPreferencesKey("country_code")
    val SHARE_NOTIFICATION_AS = stringPreferencesKey("share_notification_as")
    val SHARED_DOCUMENT_FORMAT = stringPreferencesKey("shared_document_format")
    val SHARED_DOCUMENT_TYPE = stringPreferencesKey("shared_document_type")
    val WHATSAPP_IMAGE_SIZE = floatPreferencesKey("whatsapp_image_size")
    val WHATSAPP_IMAGE_QUALITY = floatPreferencesKey("whatsapp_image_quality")

    // Other
    val ENABLE_MULTI_CURRENCY = booleanPreferencesKey("enable_multi_currency")
    val CLOUD_BACKUP_DESTINATION = stringPreferencesKey("cloud_backup_destination")
    val DIRECT_BACKUP_TO_EMAIL = booleanPreferencesKey("direct_backup_to_email")
    val BACKUP_PREFIX_NAME = stringPreferencesKey("backup_prefix_name")
    val REPEAT_BACKUP_ALERT_ON_CLOSE = booleanPreferencesKey("repeat_backup_alert_on_close")
    val RUN_EMAIL_BACKUP_IN_BACKGROUND = booleanPreferencesKey("run_email_backup_in_background")
    val ENABLE_AUDIO_ALERTS = booleanPreferencesKey("enable_audio_alerts")
    val DISABLE_SIZES_SYSTEM = booleanPreferencesKey("disable_sizes_system")
    val FIX_REPORTS_DATE = booleanPreferencesKey("fix_reports_date")
    val SHOW_DAILY_AS_CASHBOX = booleanPreferencesKey("show_daily_as_cashbox")
    val SHOW_BACK_BUTTON_IN_REPORTS = booleanPreferencesKey("show_back_button_in_reports")
    val SHOW_ACCOUNT_DEBT_PERIOD = booleanPreferencesKey("show_account_debt_period")
    val SHOW_TOTAL_MAIN_ACCOUNTS_BALANCE = booleanPreferencesKey("show_total_main_accounts_balance")
    val GROUP_MAIN_ACCOUNTS_BALANCE_BY = stringPreferencesKey("group_main_accounts_balance_by")
}
