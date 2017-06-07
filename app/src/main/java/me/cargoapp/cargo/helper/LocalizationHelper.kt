package me.cargoapp.cargo.helper

import android.content.Context
import android.content.res.Configuration
import java.util.*


object LocalizationHelper {
    fun getString(context: Context, locale: Locale, resId: Int, vararg formatArgs: Any): String {
        var conf = context.resources.configuration
        conf = Configuration(conf)
        conf.setLocale(locale)
        val localizedContext = context.createConfigurationContext(conf)
        return localizedContext.resources.getString(resId, *formatArgs)
    }
}
