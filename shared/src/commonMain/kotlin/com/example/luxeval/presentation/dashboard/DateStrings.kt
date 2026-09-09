package com.example.luxeval.presentation.dashboard

import androidx.compose.runtime.Composable
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.date_day_fri
import luxeval.shared.generated.resources.date_day_mon
import luxeval.shared.generated.resources.date_day_sat
import luxeval.shared.generated.resources.date_day_sun
import luxeval.shared.generated.resources.date_day_thu
import luxeval.shared.generated.resources.date_day_tue
import luxeval.shared.generated.resources.date_day_wed
import luxeval.shared.generated.resources.date_full
import luxeval.shared.generated.resources.date_month_apr
import luxeval.shared.generated.resources.date_month_aug
import luxeval.shared.generated.resources.date_month_dec
import luxeval.shared.generated.resources.date_month_feb
import luxeval.shared.generated.resources.date_month_jan
import luxeval.shared.generated.resources.date_month_jul
import luxeval.shared.generated.resources.date_month_jun
import luxeval.shared.generated.resources.date_month_mar
import luxeval.shared.generated.resources.date_month_may
import luxeval.shared.generated.resources.date_month_nov
import luxeval.shared.generated.resources.date_month_oct
import luxeval.shared.generated.resources.date_month_sep
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

data class DateStrings(
    val fullDateTemplate: String,
    
    val monthJan: String,
    val monthFeb: String,
    val monthMar: String,
    val monthApr: String,
    val monthMay: String,
    val monthJun: String,
    val monthJul: String,
    val monthAug: String,
    val monthSep: String,
    val monthOct: String,
    val monthNov: String,
    val monthDec: String,

    val dayMon: String,
    val dayTue: String,
    val dayWed: String,
    val dayThu: String,
    val dayFri: String,
    val daySat: String,
    val daySun: String,
) {
    companion object {
        @Composable
        fun resolve(): DateStrings = DateStrings(
            fullDateTemplate = stringResource(Res.string.date_full),

            monthJan = stringResource(Res.string.date_month_jan),
            monthFeb = stringResource(Res.string.date_month_feb),
            monthMar = stringResource(Res.string.date_month_mar),
            monthApr = stringResource(Res.string.date_month_apr),
            monthMay = stringResource(Res.string.date_month_may),
            monthJun = stringResource(Res.string.date_month_jun),
            monthJul = stringResource(Res.string.date_month_jul),
            monthAug = stringResource(Res.string.date_month_aug),
            monthSep = stringResource(Res.string.date_month_sep),
            monthOct = stringResource(Res.string.date_month_oct),
            monthNov = stringResource(Res.string.date_month_nov),
            monthDec = stringResource(Res.string.date_month_dec),

            dayMon = stringResource(Res.string.date_day_mon),
            dayTue = stringResource(Res.string.date_day_tue),
            dayWed = stringResource(Res.string.date_day_wed),
            dayThu = stringResource(Res.string.date_day_thu),
            dayFri = stringResource(Res.string.date_day_fri),
            daySat = stringResource(Res.string.date_day_sat),
            daySun = stringResource(Res.string.date_day_sun),
            )
    }
    fun dayName(day: DayOfWeek): String = when (day) {
        DayOfWeek.MONDAY -> dayMon
        DayOfWeek.TUESDAY -> dayTue
        DayOfWeek.WEDNESDAY -> dayWed
        DayOfWeek.THURSDAY -> dayThu
        DayOfWeek.FRIDAY -> dayFri
        DayOfWeek.SATURDAY -> daySat
        DayOfWeek.SUNDAY -> daySun
    }

    fun monthName(month: Month): String = when (month) {
        Month.JANUARY -> monthJan
        Month.FEBRUARY -> monthFeb
        Month.MARCH -> monthMar
        Month.APRIL -> monthApr
        Month.MAY -> monthMay
        Month.JUNE -> monthJun
        Month.JULY -> monthJul
        Month.AUGUST -> monthAug
        Month.SEPTEMBER -> monthSep
        Month.OCTOBER -> monthOct
        Month.NOVEMBER -> monthNov
        Month.DECEMBER -> monthDec
    }
}