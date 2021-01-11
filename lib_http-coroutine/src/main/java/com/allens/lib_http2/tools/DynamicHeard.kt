package com.allens.lib_http2.tools

import android.os.Build
import java.util.concurrent.TimeUnit

class DynamicHeard {
    companion object {
         val TODO_HEARD = Build.BRAND + "_" + Build.MODEL + "_"
        val DYNAMIC_URL = TODO_HEARD + "DYNAMIC_URL"
        val DYNAMIC_CONNECT_TIME_OUT = TODO_HEARD + "DYNAMIC_CONNECT_TIME_OUT"
        val DYNAMIC_CONNECT_TIME_OUT_TimeUnit = TODO_HEARD + "DYNAMIC_CONNECT_TIME_OUT_TimeUnit"
        val DYNAMIC_WRITE_TIME_OUT = TODO_HEARD + "DYNAMIC_WRITE_TIME_OUT"
        val DYNAMIC_WRITE_TIME_OUT_TimeUnit = TODO_HEARD + "DYNAMIC_WRITE_TIME_OUT_TimeUnit"
        val DYNAMIC_READ_TIME_OUT = TODO_HEARD + "DYNAMIC_READ_TIME_OUT"
        val DYNAMIC_READ_TIME_OUT_TimeUnit = TODO_HEARD + "DYNAMIC_READ_TIME_OUT_TimeUnit"

        fun timeUnitConvert(timeUnit: TimeUnit): TimeUnitEnum {
            return when (timeUnit) {
                TimeUnit.NANOSECONDS -> TimeUnitEnum.NANOSECONDS
                TimeUnit.MICROSECONDS -> TimeUnitEnum.MICROSECONDS
                TimeUnit.MILLISECONDS -> TimeUnitEnum.MILLISECONDS
                TimeUnit.SECONDS -> TimeUnitEnum.SECONDS
                TimeUnit.MINUTES -> TimeUnitEnum.MINUTES
                TimeUnit.HOURS -> TimeUnitEnum.HOURS
                TimeUnit.DAYS -> TimeUnitEnum.DAYS
                else -> TimeUnitEnum.MILLISECONDS
            }
        }

        fun convertTimeUnit(info: String?):TimeUnit{
            return when (info) {
                "NANOSECONDS" -> TimeUnit.NANOSECONDS
                "MICROSECONDS" -> TimeUnit.MICROSECONDS
                "MILLISECONDS" -> TimeUnit.MILLISECONDS
                "SECONDS" -> TimeUnit.SECONDS
                "MINUTES" -> TimeUnit.MINUTES
                "HOURS" -> TimeUnit.HOURS
                "DAYS" -> TimeUnit.DAYS
                else -> TimeUnit.MILLISECONDS
            }
        }
    }


}

enum class TimeUnitEnum(val info: String, val timeUnit: TimeUnit) {
    NANOSECONDS("NANOSECONDS", TimeUnit.NANOSECONDS),
    MICROSECONDS("MICROSECONDS", TimeUnit.MICROSECONDS),
    MILLISECONDS("MILLISECONDS", TimeUnit.MILLISECONDS),
    SECONDS("SECONDS", TimeUnit.SECONDS),
    MINUTES("MINUTES", TimeUnit.MINUTES),
    HOURS("HOURS", TimeUnit.HOURS),
    DAYS("DAYS", TimeUnit.DAYS),

}