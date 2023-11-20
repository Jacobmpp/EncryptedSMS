package com.esms.services

import android.content.Context
import android.net.Uri
import com.esms.models.SMSMessage
import android.telephony.SmsManager

/**
 * Service class for reading SMS messages from the device.
 *
 * @param context Context used to access the content resolver for querying SMS messages.
 */
class SmsService(private val context: Context) {

    /**
     * Reads SMS messages of a specified type from the device and returns them as a list.
     *
     * @param type The type of SMS messages to read (e.g., 'inbox', 'sent', etc.).
     * @return List of SMSMessage objects representing the read messages.
     */
    fun readMessages(type: String): List<SMSMessage> {
        val messages = mutableListOf<SMSMessage>()
        val cursor = context.contentResolver.query(
            Uri.parse("content://sms/$type"),
            null,
            null,
            null,
            null
        )
        cursor?.use {
            val indexBody = it.getColumnIndex("body")
            val indexSender = it.getColumnIndex("address")
            val indexDate = it.getColumnIndex("date")
            val indexRead = it.getColumnIndex("read")
            val indexType = it.getColumnIndex("type")
            val indexThread = it.getColumnIndex("thread_id")
            // val indexService = it.getColumnIndex("service_center")

            while (it.moveToNext()) {
                messages.add(
                    SMSMessage(
                        body = it.getString(indexBody),
                        sender = it.getString(indexSender),
                        date = it.getLong(indexDate),
                        read = it.getString(indexRead).toBoolean(),
                        type = it.getInt(indexType),
                        thread = it.getInt(indexThread),
                        // service = it.getString(indexService)
                    )
                )
            }
        }

        return messages
    }
    /**
     * Sends an SMS message to the specified recipient.
     *
     * @param phoneNumber The phone number of the recipient.
     * @param message The text message to be sent.
     */
    fun sendMessage(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault() // Known Bug, will only fail on multi-SIM devices
            val messageParts = smsManager.divideMessage(message.ifEmpty{ " " })

            smsManager.sendMultipartTextMessage(phoneNumber, null, messageParts, null, null)
        } catch (e: Exception) {
            // Handle exceptions like invalid number format or no SMS service available
            e.printStackTrace()
        }
    }
}
