package me.cargoapp.cargo.helper;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

public class PermissionHelper {
    private String TAG = this.getClass().getSimpleName();

    public static boolean isPermittedTo(Context context, String permission) {
        switch (permission) {
            case Manifest.permission.SYSTEM_ALERT_WINDOW:
                return Settings.canDrawOverlays(context);
            case Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE:
                String pkgName = context.getPackageName();
                final String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
                if (TextUtils.isEmpty(flat)) return false;

                final String[] names = flat.split(":");
                for (String name : names) {
                    final ComponentName cn = ComponentName.unflattenFromString(name);
                    if (cn == null) continue;

                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
                return false;
            default:
                return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static boolean isPermittedTo(Context context, String[] permissions) {
        boolean permissionsOk = true;

        for (String permission : permissions) {
            if (!PermissionHelper.isPermittedTo(context, permission)) {
                permissionsOk = false;
                break;
            }
        }

        return permissionsOk;
    }
}
