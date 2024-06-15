package ru.mixail_akulov.a37_navigationactivity2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// в build.gradle надо прописать id 'kotlin-parcelize'
// Опции хранятся в MenuActivity, т.к. он является связующим звеном между
// OptionsActivity и BoxActivity, последние напрямую между собой не взаимодействуют.
@Parcelize
data class Options(
    val boxCount: Int,
    val isTimerEnabled: Boolean
) : Parcelable {

    companion object {
        @JvmStatic val DEFAULT = Options(boxCount = 3, isTimerEnabled = false)
    }
}