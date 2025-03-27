package expo.modules.notify.example

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.widget.Toast

import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

import expo.modules.ReactActivityDelegateWrapper
import expo.modules.notify.service.MqttJobService
import expo.modules.notify.service.MqttService

class MainActivity : ReactActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    // Set the theme to AppTheme BEFORE onCreate to support
    // coloring the background, status bar, and navigation bar.
    // This is required for expo-splash-screen.
    setTheme(R.style.AppTheme);
    super.onCreate(null)




//      scheduleMqttJob(this)
//
//      startMqttService()
//
//      scheduleMqttReconnect(this)
//
//      val serviceIntent = Intent(this, MqttService::class.java)
//      startService(serviceIntent)
//
//// 申请忽略电池优化
//      requestIgnoreBatteryOptimizations(this)


  }

    fun startMqttService() {
        val serviceIntent = Intent(this, MqttService::class.java)
        startService(serviceIntent)
    }

    fun scheduleMqttReconnect(context: Context) {
        val intent = Intent(context, MqttService::class.java)
        val pendingIntent = PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 60000, // 1 分钟后启动
            60000, // 每 1 分钟拉起一次
            pendingIntent
        )
    }

    fun scheduleMqttJob(context: Context) {
        val component = ComponentName(context, MqttJobService::class.java)
        val jobInfo = JobInfo.Builder(1, component)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // 任何网络都可以执行
            .setPeriodic(15 * 60 * 1000) // 每 15 分钟执行一次
            .build()

        val scheduler = context.getSystemService(JobScheduler::class.java)
        scheduler.schedule(jobInfo)
    }

    private fun requestIgnoreBatteryOptimizations(context: Context) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(intent)
            Toast.makeText(context, "请允许应用后台运行，以保证 MQTT 连接稳定", Toast.LENGTH_LONG).show()
        }
    }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "main"

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate {
    return ReactActivityDelegateWrapper(
          this,
          BuildConfig.IS_NEW_ARCHITECTURE_ENABLED,
          object : DefaultReactActivityDelegate(
              this,
              mainComponentName,
              fabricEnabled
          ){})
  }

  /**
    * Align the back button behavior with Android S
    * where moving root activities to background instead of finishing activities.
    * @see <a href="https://developer.android.com/reference/android/app/Activity#onBackPressed()">onBackPressed</a>
    */
  override fun invokeDefaultOnBackPressed() {
      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
          if (!moveTaskToBack(false)) {
              // For non-root activities, use the default implementation to finish them.
              super.invokeDefaultOnBackPressed()
          }
          return
      }

      // Use the default back button implementation on Android S
      // because it's doing more than [Activity.moveTaskToBack] in fact.
      super.invokeDefaultOnBackPressed()
  }


    override fun onDestroy() {
        super.onDestroy()

        startMqttService()
    }

}
