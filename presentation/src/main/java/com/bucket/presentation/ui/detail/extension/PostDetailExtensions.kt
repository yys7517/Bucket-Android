package com.bucket.presentation.ui.detail.extension

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import java.time.LocalDate

// ─── Date Helpers ─────────────────────────────────────────────────────────────

internal fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 31
    }
}

private fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

/** "YYYY-MM-DD" → Triple(year, month, day). 파싱 실패 시 null. */
internal fun parseIsoDateOrNull(date: String?): Triple<Int, Int, Int>? {
    if (date.isNullOrBlank()) return null
    val parts = date.split("-")
    if (parts.size != 3) return null
    val y = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    val d = parts[2].toIntOrNull() ?: return null
    return Triple(y, m, d)
}

internal fun displayKoreanDate(iso: String): String {
    val (y, m, d) = parseIsoDateOrNull(iso) ?: return iso
    return "${y}년 ${m}월 ${d}일"
}

internal fun todayDateParts(): Triple<Int, Int, Int> {
    val today = LocalDate.now()
    return Triple(today.year, today.monthValue, today.dayOfMonth)
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

internal fun String.toKoreanDateText(): String {
    if (isBlank()) return "시작일을 설정해보세요"
    val parts = split("-")
    if (parts.size != 3) return this
    val month = parts[1].toIntOrNull() ?: return this
    val day = parts[2].toIntOrNull() ?: return this
    return "${parts[0]}년 ${month}월 ${day}일"
}

@SuppressLint("UseKtx")
internal fun String.toComposeColor(): Color = try {
    Color(this.toColorInt())
} catch (e: Exception) {
    Color(0xFF8D6BE8)
}


internal fun mandalaGridPosition(index: Int): Int? = when (index) {
    in 0..8 -> index + 1
    else -> null
}?.takeUnless { it == 5 }

internal fun Int.asOuterMandalaPositionOrFallback(fallbackIndex: Int): Int =
    takeIf { it in 1..9 && it != 5 } ?: fallbackIndex.toOuterMandalaPosition()

private fun Int.toOuterMandalaPosition(): Int = if (this < 4) this + 1 else this + 2
