package com.example.projetomobile

import android.content.ContentValues
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class PurchaseDatabaseHelper(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertPurchase(purchase: Purchase): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.PURCHASE_COL_USER_ID, purchase.userId)
            put(DatabaseHelper.PURCHASE_COL_PROTOCOLO, purchase.protocolo)
            put(DatabaseHelper.PURCHASE_COL_DATA, purchase.dataCompra)
            put(DatabaseHelper.PURCHASE_COL_SUBTOTAL, purchase.subtotal)
            put(DatabaseHelper.PURCHASE_COL_FRETE, purchase.frete)
            put(DatabaseHelper.PURCHASE_COL_TOTAL, purchase.total)
            put(DatabaseHelper.PURCHASE_COL_PRODUTOS, purchase.produtos)
            put(DatabaseHelper.PURCHASE_COL_STATUS, purchase.status)
        }
        val id = db.insert(DatabaseHelper.TABLE_PURCHASE, null, values)
        purchase.id = if (id == -1L) null else id
        db.close()
        return id
    }

    fun getPurchase(id: Long): Purchase? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PURCHASE,
            arrayOf(
                DatabaseHelper.PURCHASE_COL_ID,
                DatabaseHelper.PURCHASE_COL_USER_ID,
                DatabaseHelper.PURCHASE_COL_PROTOCOLO,
                DatabaseHelper.PURCHASE_COL_DATA,
                DatabaseHelper.PURCHASE_COL_SUBTOTAL,
                DatabaseHelper.PURCHASE_COL_FRETE,
                DatabaseHelper.PURCHASE_COL_TOTAL,
                DatabaseHelper.PURCHASE_COL_PRODUTOS,
                DatabaseHelper.PURCHASE_COL_STATUS
            ),
            "${DatabaseHelper.PURCHASE_COL_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var purchase: Purchase? = null
        if (cursor.moveToFirst()) {
            val pid = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_ID))
            val userId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_USER_ID))
            val protocolo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_PROTOCOLO))
            val data = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_DATA))
            val subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_SUBTOTAL))
            val frete = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_FRETE))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_TOTAL))
            val produtos = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_PRODUTOS))
            val status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_STATUS))

            purchase = Purchase(pid, userId, protocolo, data, subtotal, frete, total, produtos, status)
        }
        cursor.close()
        db.close()
        return purchase
    }

    fun getPurchasesByUserId(userId: Long): List<Purchase> {
        val purchases = mutableListOf<Purchase>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PURCHASE,
            arrayOf(
                DatabaseHelper.PURCHASE_COL_ID,
                DatabaseHelper.PURCHASE_COL_USER_ID,
                DatabaseHelper.PURCHASE_COL_PROTOCOLO,
                DatabaseHelper.PURCHASE_COL_DATA,
                DatabaseHelper.PURCHASE_COL_SUBTOTAL,
                DatabaseHelper.PURCHASE_COL_FRETE,
                DatabaseHelper.PURCHASE_COL_TOTAL,
                DatabaseHelper.PURCHASE_COL_PRODUTOS,
                DatabaseHelper.PURCHASE_COL_STATUS
            ),
            "${DatabaseHelper.PURCHASE_COL_USER_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "${DatabaseHelper.PURCHASE_COL_DATA} DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val pid = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_ID))
                val uid = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_USER_ID))
                val protocolo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_PROTOCOLO))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_DATA))
                val subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_SUBTOTAL))
                val frete = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_FRETE))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_TOTAL))
                val produtos = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_PRODUTOS))
                val status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PURCHASE_COL_STATUS))

                purchases.add(Purchase(pid, uid, protocolo, data, subtotal, frete, total, produtos, status))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return purchases
    }

    fun generateProtocol(): String {
        val timestamp = System.currentTimeMillis().toString().takeLast(8)
        val random = Random.nextInt(10000, 99999)
        return "PRO-$timestamp-$random"
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}

