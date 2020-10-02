package com.ani.timegapanalyzer.utils

class Utils {

}

fun getMonth(month: Int) = run {
    when (month) {
        0 -> "Jan"
        1 -> "Feb"
        2 -> "Mar"
        3 -> "Apr"
        4 -> "May"
        5 -> "Jun"
        6 -> "July"
        7 -> "Aug"
        8 -> "Sep"
        9 -> "Oct"
        10 -> "Nov"
        11 -> "Dec"
        else -> {""}
    }
}

fun replaceMonth(date: String) = run {
    when {
        date.contains("Jan") -> {
            date.replace("Jan", "01")
        }
        date.contains("Feb") -> {
            date.replace("Feb", "02")
        }
        date.contains("Mar") -> {
            date.replace("Mar", "03")
        }
        date.contains("Apr") -> {
            date.replace("Apr", "04")
        }
        date.contains("May") -> {
            date.replace("May", "05")
        }
        date.contains("Jun") -> {
            date.replace("Jun", "06")
        }
        date.contains("July") -> {
            date.replace("July", "07")
        }
        date.contains("Aug") -> {
            date.replace("Aug", "08")
        }
        date.contains("Sep") -> {
            date.replace("Sep", "09")
        }
        date.contains("Oct") -> {
            date.replace("Oct", "10")
        }
        date.contains("Nov") -> {
            date.replace("Nov", "11")
        }
        else -> {
            date.replace("Dec", "12")
        }
    }
}