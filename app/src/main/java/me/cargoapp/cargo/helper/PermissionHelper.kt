package me.cargoapp.cargo.helper

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.text.TextUtils

object PermissionHelper {

    fun isPermittedTo(context: Context, permission: String): Boolean {
        when (permission) {
            Manifest.permission.SYSTEM_ALERT_WINDOW -> return Settings.canDrawOverlays(context)
            Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE -> {
                val pkgName = context.packageName
                val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
                if (TextUtils.isEmpty(flat)) return false

                val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (name in names) {
                    val cn = ComponentName.unflattenFromString(name) ?: continue

                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
                return false
            }
            else -> return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun isPermittedTo(context: Context, permissions: Array<String>): Boolean {
        var permissionsOk = true

        for (permission in permissions) {
            if (!PermissionHelper.isPermittedTo(context, permission)) {
                permissionsOk = false
                break
            }
        }

        return permissionsOk
    }
}