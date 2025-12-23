package com.notex.sd.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import com.notex.sd.BuildConfig
import com.notex.sd.ui.screens.debug.DebugActivity
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

class CrashHandler private constructor(
    private val applicationContext: Context
) : Thread.UncaughtExceptionHandler {

    private val defaultHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    companion object {
        private var instance: CrashHandler? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = CrashHandler(context.applicationContext)
                Thread.setDefaultUncaughtExceptionHandler(instance)
            }
        }

        fun getInstance(): CrashHandler? = instance
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val crashLog = buildCrashLog(thread, throwable)
            launchDebugActivity(crashLog)
        } catch (e: Exception) {
            defaultHandler?.uncaughtException(thread, throwable)
        }

        Process.killProcess(Process.myPid())
        exitProcess(1)
    }

    private fun buildCrashLog(thread: Thread, throwable: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        throwable.printStackTrace(printWriter)
        val stackTrace = stringWriter.toString()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        val timestamp = dateFormat.format(Date())

        return buildString {
            appendLine("═══════════════════════════════════════════════════════════════")
            appendLine("                        CRASH REPORT")
            appendLine("═══════════════════════════════════════════════════════════════")
            appendLine()
            appendLine("Timestamp: $timestamp")
            appendLine("Thread: ${thread.name} (ID: ${thread.id})")
            appendLine()
            appendLine("───────────────────────────────────────────────────────────────")
            appendLine("                      DEVICE INFORMATION")
            appendLine("───────────────────────────────────────────────────────────────")
            appendLine()
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Brand: ${Build.BRAND}")
            appendLine("Product: ${Build.PRODUCT}")
            appendLine("Android Version: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            appendLine("Build ID: ${Build.ID}")
            appendLine("Build Type: ${Build.TYPE}")
            appendLine("Hardware: ${Build.HARDWARE}")
            appendLine("Board: ${Build.BOARD}")
            appendLine()
            appendLine("───────────────────────────────────────────────────────────────")
            appendLine("                     APPLICATION INFORMATION")
            appendLine("───────────────────────────────────────────────────────────────")
            appendLine()
            appendLine("App Name: NoteX")
            appendLine("Package: ${BuildConfig.APPLICATION_ID}")
            appendLine("Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            appendLine("Build Type: ${BuildConfig.BUILD_TYPE}")
            appendLine("Debug: ${BuildConfig.DEBUG}")
            appendLine()
            appendLine("───────────────────────────────────────────────────────────────")
            appendLine("                         EXCEPTION")
            appendLine("───────────────────────────────────────────────────────────────")
            appendLine()
            appendLine("Type: ${throwable.javaClass.name}")
            appendLine("Message: ${throwable.message ?: "No message"}")
            appendLine()
            appendLine("───────────────────────────────────────────────────────────────")
            appendLine("                        STACK TRACE")
            appendLine("───────────────────────────────────────────────────────────────")
            appendLine()
            appendLine(stackTrace)
            appendLine()

            throwable.cause?.let { cause ->
                appendLine("───────────────────────────────────────────────────────────────")
                appendLine("                         CAUSED BY")
                appendLine("───────────────────────────────────────────────────────────────")
                appendLine()
                appendLine("Type: ${cause.javaClass.name}")
                appendLine("Message: ${cause.message ?: "No message"}")
                appendLine()
                val causeWriter = StringWriter()
                cause.printStackTrace(PrintWriter(causeWriter))
                appendLine(causeWriter.toString())
            }

            appendLine("═══════════════════════════════════════════════════════════════")
            appendLine("                      END OF CRASH REPORT")
            appendLine("═══════════════════════════════════════════════════════════════")
        }
    }

    private fun launchDebugActivity(crashLog: String) {
        val intent = Intent(applicationContext, DebugActivity::class.java).apply {
            putExtra(DebugActivity.EXTRA_CRASH_LOG, crashLog)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }
        applicationContext.startActivity(intent)

        Thread.sleep(500)
    }
}
