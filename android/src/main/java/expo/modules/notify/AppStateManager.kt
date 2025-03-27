package expo.modules.notify

import android.app.ActivityManager
import android.content.Context
import android.util.Log

object AppStateManager {
    /**
     * 检查应用是否在前台
     */
    fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false

        val packageName = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.processName == packageName &&
                appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            ) {
                Log.d("AppStateManager", "应用在前台")
                return true
            }
        }
        Log.d("AppStateManager", "应用在后台")
        return false
    }
}