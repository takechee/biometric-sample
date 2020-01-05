package me.r09i.biometricsampleapplication

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt

import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            when (BiometricManager.from(this).canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    /* 生体認証が利用可能 */
                    biometric()
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    /* 生体情報が端末に登録されていない */
                    showSnackbar("生体情報が端末に登録されていない")
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    /* 生体認証ハードウェアが利用不可 */
                    showSnackbar("生体認証ハードウェアが利用不可")
                }

                else -> showSnackbar("Unknown")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun biometric() {
        val promptInfo = BiometricPrompt.PromptInfo
            .Builder()
            .setTitle("タイトル") // 必須項目
            .setSubtitle("サブタイトル") // 任意
            .setDescription("説明文") // 任意
            .setNegativeButtonText("CANCEL") // 任意
            .build()

        val executor = Executors.newSingleThreadExecutor()
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    // 成功した場合の処理
                    showSnackbar("認証成功")
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    showSnackbar("エラーコード：$errorCode, $errString")
                }

                override fun onAuthenticationFailed() {
                    showSnackbar("認証に失敗しました")
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    private fun showSnackbar(text: String, length: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(fab, text, length).show()
    }

}
