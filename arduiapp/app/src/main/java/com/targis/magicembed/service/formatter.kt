package com.targis.magicembed.service

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


private val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    .withLocale(Locale.GERMANY)
    .withZone(ZoneId.systemDefault())

fun Instant.toStringFormat(): String = formatter.format(this)