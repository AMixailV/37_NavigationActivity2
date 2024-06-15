package ru.mixail_akulov.a37_navigationactivity2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import ru.mixail_akulov.a37_navigationactivity2.model.Options
import ru.mixail_akulov.a37_navigationactivity2.databinding.ActivityOptionsBinding

class OptionsActivity : BaseActivity() {

    private lateinit var binding: ActivityOptionsBinding

    private lateinit var options: Options

    private lateinit var boxCountItems: List<BoxCountItem>
    private lateinit var adapter: ArrayAdapter<BoxCountItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionsBinding.inflate(layoutInflater).also { setContentView(it.root) }

        options = savedInstanceState?.getParcelable<Options>(KEY_OPTIONS) ?:
                intent?.getParcelableExtra(EXTRA_OPTIONS) ?:
                throw IllegalArgumentException("You need to specify EXTRA_OPTIONS to launch this activity")

        setupSpinner()
        updateUi() // обновляем интерфейс

        binding.cancelButton.setOnClickListener { onCancelPressed() }
        binding.confirmButton.setOnClickListener { onConfirmPressed() }
    }

    private fun setupSpinner() {
        boxCountItems = (1..6).map { BoxCountItem(it, resources.getQuantityString(R.plurals.boxes, it, it)) }
        adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            boxCountItems
        )
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)

        binding.boxCountSpinner.adapter = adapter
        binding.boxCountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            //при выборе пункта из спинера
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val count = boxCountItems[position].count // берем его значение
                options = options.copy(boxCount = count) // и передаем в опции
            }
        }
    }

    private fun updateUi() {
        binding.enableTimerCheckBox.isChecked = options.isTimerEnabled // чекбоксу присваиваем состояние из полученных опций

        val currentIndex = boxCountItems.indexOfFirst { it.count == options.boxCount }
        binding.boxCountSpinner.setSelection(currentIndex)
    }

    private fun onCancelPressed() {
        finish()
    }

    private fun onConfirmPressed() {
        options = options.copy(isTimerEnabled = binding.enableTimerCheckBox.isChecked) // присваиваем состояние чекБокса
        val intent = Intent() // создаемпустую активити
        intent.putExtra(EXTRA_OPTIONS, options) // по ключу передаем в нее опции, которые всегда актуальны       setResult(Activity.RESULT_OK, intent) // передаем результат работы в активити и саму активити с данными
        finish()
    }

    companion object {
        const val EXTRA_OPTIONS = "EXTRA_OPTIONS" // ключ для передачи опций в другую активити

        private const val KEY_OPTIONS = "KEY_OPTIONS"
    }

    class BoxCountItem(
        val count: Int,
        private val optionTitle: String
    ) {
        override fun toString(): String {
            return optionTitle
        }
    }
}