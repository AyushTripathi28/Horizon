package com.example.horizon.utils

import java.text.SimpleDateFormat
import java.util.*

object UtilFunctions {

    fun timeInMillisToDateFormat(timeInMillis: Long?) : String{

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return simpleDateFormat.format(timeInMillis)
    }
}