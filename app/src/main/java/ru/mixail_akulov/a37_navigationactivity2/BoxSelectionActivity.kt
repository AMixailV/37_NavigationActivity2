package ru.mixail_akulov.a37_navigationactivity2

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import ru.mixail_akulov.a37_navigationactivity2.model.Options
import ru.mixail_akulov.a37_navigationactivity2.databinding.ActivityBoxSelectionBinding
import ru.mixail_akulov.a37_navigationactivity2.databinding.ItemBoxBinding
import kotlin.math.max
import kotlin.properties.Delegates
import kotlin.random.Random

class BoxSelectionActivity : BaseActivity() {

    private lateinit var binding: ActivityBoxSelectionBinding

    private lateinit var options: Options

    private var timerStartTimestamp by Delegates.notNull<Long>()
    private var boxIndex by Delegates.notNull<Int>()
    private var alreadyDone = false

    private var timerHandler: TimerHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoxSelectionBinding.inflate(layoutInflater).also { setContentView(it.root) }

        options = intent.getParcelableExtra(EXTRA_OPTIONS) ?:
                throw IllegalArgumentException("Can't launch BoxSelectionActivity without options")
        boxIndex = savedInstanceState?.getInt(KEY_INDEX) ?: Random.nextInt(options.boxCount)

        timerHandler = if (options.isTimerEnabled) {
            TimerHandler()
        } else {
            null
        }
        timerHandler?.onCreate(savedInstanceState)

        createBoxes()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, boxIndex)
        timerHandler?.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        timerHandler?.onStart()
    }

    override fun onStop() {
        super.onStop()
        timerHandler?.onStop()
    }

    private fun createBoxes() {
        val boxBindings = (0 until options.boxCount).map {index ->
            val boxBinding = ItemBoxBinding.inflate(layoutInflater)
            boxBinding.root.id = View.generateViewId()
            boxBinding.boxTitleTextView.text = getString(R.string.box_title, index + 1)
            boxBinding.root.setOnClickListener { view -> onBoxSelected(view) }
            boxBinding.root.tag = index
            binding.root.addView(boxBinding.root)
            boxBinding
        }
        binding.flow.referencedIds = boxBindings.map { it.root.id }.toIntArray()
    }

    private fun onBoxSelected(view: View) {
        if (view.tag as Int == boxIndex) {
            alreadyDone = true // disabling timer if the user made a right choice
            val intent = Intent(this, BoxActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, R.string.empty_box, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTimerUi() {
        if (getRemainingSeconds() >= 0) {
            binding.timerTextView.visibility = View.VISIBLE
            binding.timerTextView.text = getString(R.string.timer_value, getRemainingSeconds())
        } else {
            binding.timerTextView.visibility = View.GONE
        }
    }

    private fun showTimerEndDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.the_end))
            .setMessage(getString(R.string.timer_end_message))
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ -> finish() }
            .create()
        dialog.show()
    }

    private fun getRemainingSeconds(): Long {
        val finishedAt = timerStartTimestamp + TIMER_DURATION
        return max(0, (finishedAt - System.currentTimeMillis()) / 1000)
    }

    inner class TimerHandler {

        private lateinit var timer: CountDownTimer

        fun onCreate(savedInstanceState: Bundle?) {
            timerStartTimestamp = savedInstanceState?.getLong(KEY_START_TIMESTAMP)
                ?: System.currentTimeMillis()
            alreadyDone = savedInstanceState?.getBoolean(KEY_ALREADY_DONE) ?: false
        }

        fun onSaveInstanceState(outState: Bundle) {
            outState.putLong(KEY_START_TIMESTAMP, timerStartTimestamp)
            outState.putBoolean(KEY_ALREADY_DONE, alreadyDone)
        }

        fun onStart() {
            if (alreadyDone) return
            // таймер приостанавливается, когда приложение свернуто (onTick не вызывается),
            // но мы запоминаем начальную метку времени при запуске экрана,
            // поэтому на самом деле диалоговое окно отображается через 10 секунд
            // после начальной отметки времени в любом случае. Если приложение свернуто более 10 секунд,
            // диалоговое окно отображается сразу после восстановления приложения.
            timer = object : CountDownTimer(getRemainingSeconds() * 1000, 1000) {
                override fun onFinish() {
                    updateTimerUi()
                    showTimerEndDialog()
                }

                override fun onTick(millisUntilFinished: Long) {
                    updateTimerUi()
                }
            }
            updateTimerUi()
            timer.start()
        }

        fun onStop() {
            timer.cancel()
        }
    }

    companion object {
        const val EXTRA_OPTIONS = "EXTRA_OPTIONS"
        private const val KEY_INDEX = "KEY_INDEX"
        private const val KEY_START_TIMESTAMP = "KEY_START_TIMESTAMP"
        private const val KEY_ALREADY_DONE = "KEY_ALREADY_DONE"

        private const val TIMER_DURATION = 10_000L
    }
}