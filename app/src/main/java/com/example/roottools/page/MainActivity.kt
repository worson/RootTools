package com.example.roottools.page

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.Utils
import com.example.roottools.R
import com.example.roottools.utils.Logger
import com.example.roottools.utils.ShellUtil
import com.example.roottools.utils.SuFileUtils
import com.topjohnwu.superuser.Shell


class MainActivity : AppCompatActivity() {

    var tvResult: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Logger.setListener { level, tag, msg -> appendLog("$msg\n") }
        tvResult = findViewById(R.id.tvResult)
        tvResult?.text = ""
        onShowProxy()
    }

    private fun test() {
        Shell.cmd(getResources().openRawResource(R.raw.show_sdcard)).submit { result ->
            updateUI(
                result
            )
        }
    }


    private fun updateUI(result: Shell.Result) {
        var sb = StringBuilder()
        sb.append("isSuccess = ${result.isSuccess} \n")
        sb.append("code = ${result.code} \n")
        sb.append("out = ${result.out} \n")
        sb.append("err = ${result.err} \n")
        appendLog(sb.toString())
    }

    private fun appendLog(msg: String) {
        Log.d("", msg)
        tvResult?.append(msg)
    }

    fun onCopyCertificate(view: View) {
        var files = mutableListOf<String>()
        files.add("7cb91b1e.0")//pixel 2 xl origin
        files.add("5bb59c0c.0")//k20 pro
        files.add("6fbe4e0f.0")//pixel 2 xl user dir
        copyCertificate(files)

    }

    fun onDeleteCertificate(view: View) {
        deleteCertificate("7cb91b1e.0")//pixel 2 xl origin
        deleteCertificate("5bb59c0c.0")//k20 pro
        deleteCertificate("6fbe4e0f.0")//pixel 2 xl user dir
    }

    private fun deleteCertificate(sourcePath: String) {
        var targetPath = "/system/etc/security/cacerts/" + sourcePath
        Shell.cmd(
            "su", "mount -o rw,remount /",
            "mount -o rw,remount /system",
            "rm -f ${targetPath}",
        ).submit { result ->
            updateUI(
                result
            )
            if (result.isSuccess) {
                Logger.log("删除证书成功:${sourcePath}")
            } else {
                Logger.log("删除证书失败:${sourcePath}")
            }
        }
    }

    private fun copyCertificate(sourcePaths: List<String>) {
        Logger.log("开始拷贝证书")
        var cmds = mutableListOf<String>(
            "su", "mount -o rw,remount /",
            "mount -o rw,remount /system",
        )
        for (sourcePath in sourcePaths) {
            var tempPath = "/sdcard/Download/$sourcePath"
            var targetPath = "/system/etc/security/cacerts/$sourcePath"
//            var targetPath2 = "/data/misc/user/0/cacerts-added/$sourcePath"
            var success = SuFileUtils.copyFileFromAssets(sourcePath, tempPath)
            if (success) {
                cmds.add("cp ${tempPath} ${targetPath}")
                cmds.add("chmod 644 ${targetPath}")
//                cmds.add("cp ${tempPath} ${targetPath2}")
            } else {
                Logger.log("拷贝证书失败:${sourcePath}")
            }
        }
        var sb = StringBuilder()
        for (cmd in cmds) {
            sb.append(cmd + "\n")
        }
        Logger.log("准备开始执行命令：")
        Logger.log(sb.toString())
        ShellUtil.cmd(cmds).submit { result ->
            updateUI(
                result
            )
            if (result.isSuccess) {
                Logger.log("拷贝证书shell执行成功")
            } else {
                Logger.log("拷贝证书shell执行失败")
            }
        }
    }

    fun onRebootDevice(view: View) {
        Shell.cmd("reboot").submit()
    }

    fun onClearLog(view: View) {
        tvResult?.text = ""
    }

    fun onShowProxy() {
        ShellUtil.cmd("settings get global http_proxy").submit { result ->
            result.isSuccess.let {
                Logger.log("当前的代理信息为:" + result.out)
            }
        }
    }

    fun onClearProxy(view: View) {
        Logger.log("准备清除代理")
        ShellUtil.cmd("settings put global http_proxy :0").submit { result ->
            updateUI(
                result
            )
            onShowProxy()
        }
    }


}