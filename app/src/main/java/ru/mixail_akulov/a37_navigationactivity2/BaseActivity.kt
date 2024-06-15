package ru.mixail_akulov.a37_navigationactivity2

import androidx.appcompat.app.AppCompatActivity

// Базовый класс с операцией,которая есть во всех остальных активити - onSupportNavigateUp(), который
// позволяет возвращаться назад по стрелке finish() (системный метод).
// От него все активити наследуюдся, а он наследуется от AppCompatActivity()
open class BaseActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}