package ru.mixail_akulov.a37_navigationactivity2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import ru.mixail_akulov.a37_navigationactivity2.model.Options
import ru.mixail_akulov.a37_navigationactivity2.databinding.ActivityMenuBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var options: Options  // хранит текущие настройки

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater).also { setContentView(it.root) }

        binding.openBoxButton.setOnClickListener { onOpenBoxPressed() }
        binding.optionsButton.setOnClickListener { onOptionsPressed() }
        binding.aboutButton.setOnClickListener { onAboutPressed() }
        binding.exitButton.setOnClickListener { onExitPressed() }

        // получает настройки из savedInstanceState по ключу KEY_OPTIONS, если же там null, то создает дефолтные
        options = savedInstanceState?.getParcelable(KEY_OPTIONS) ?: Options.DEFAULT
    }


    // сохраняем текущие опции под ключом KEY_OPTIONS в Bundle
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_OPTIONS, options)
    }

    // сюда приходит результат работы из других активити по коду requestCode. Код resultCode показывает было ли
    // что-то сделано в активити или нет
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPTIONS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // получаем опции по ключу EXTRA_OPTIONS через getParcelableExtra()
            options = data?.getParcelableExtra(OptionsActivity.EXTRA_OPTIONS) ?:
                    throw IllegalArgumentException("Can't get the updated data from options activity")
        }
    }

    private fun onOpenBoxPressed() {
        val intent = Intent(this, BoxSelectionActivity::class.java)
        intent.putExtra(BoxSelectionActivity.EXTRA_OPTIONS, options)
        startActivity(intent)
    }

    private fun onOptionsPressed() {
        val intent = Intent(this, OptionsActivity::class.java)
        intent.putExtra(OptionsActivity.EXTRA_OPTIONS, options) // передаем в OptionsActivity опции по ключу EXTRA_OPTIONS
        startActivityForResult(intent, OPTIONS_REQUEST_CODE) // и не только стартуем активити, но и получаем от нее какой-то результат (отредактированные опции)
    }

    private fun onAboutPressed() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    private fun onExitPressed() {
        finish()    // закрывает активити
    }

    private companion object {
        const val KEY_OPTIONS = "OPTIONS"
        const val OPTIONS_REQUEST_CODE = 1 // для определения от какой активити пришел результат, т.к. активити может быть много
    }
}