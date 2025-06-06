package kz.enpf.enabler

import android.content.ContentResolver
import android.provider.Settings
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Main : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!lpparam.packageName.equals("kz.enpf.mobile"))
            return
        // БЖЗК қосымшасы ашылды
        XposedBridge.log("ENPF_ENABLER OPENED in ${lpparam.packageName}")

        findAndHookMethod(
            Settings.Secure::class.java,
            "getInt",
            ContentResolver::class.java,
            String::class.java,
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val key = param.args[1] as String
                    if (key == "development_settings_enabled") {
                        param.result = 0 // Force dev mode OFF
                    }
                }
            }
        )
        // БЖЗК қосымшасындағы бағдарламаушылар тексерісі өшірілді
        XposedBridge.log("ENPF_ENABLER  dev mode check is off")

        findAndHookMethod(
            "com.scottyab.rootbeer.RootBeer",
            lpparam.classLoader,
            "isRooted",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    param.result = false // Force "not rooted"
                }
            }
        )
        // БЖЗК қосымшасындағы рут тексерісі өшірілді
        XposedBridge.log("ENPF_ENABLER root check is off ${lpparam.packageName}")
    }
}