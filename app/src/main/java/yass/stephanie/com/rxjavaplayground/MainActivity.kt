package yass.stephanie.com.rxjavaplayground

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {

    companion object {
        private const val SECRET_COUNTRY = "france"
        private const val CORRECT = "CORRECT"
        private const val GAME_OVER = "GAME OVER"
        private const val ATTEMPT = "attempt left"
        private const val ONE_ATTEMPT_LEFT = 1
        private var attemptsCounter: Int = 10
        private lateinit var inputDisposable: Disposable
        private lateinit var gameDisposable: Disposable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val inputtedColorEditText: EditText = findViewById(R.id.guess_color_input)
        val startAgainButton: Button = findViewById(R.id.start_again)
        val numberOfAttemptsText: TextView = findViewById(R.id.number_of_attempts)
        val attemptsSubText: TextView = findViewById(R.id.attempts_text)
        numberOfAttemptsText.text = attemptsCounter.toString()


        val inputtedTextObservable: Observable<String> = Observable.create { result ->
            inputtedColorEditText.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    result.onNext(inputtedColorEditText.text.toString())
                    return@OnKeyListener true
                }
                false
            })
        }


        val gameOverObservable: Observable<String> = Observable.create { result ->
            numberOfAttemptsText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    result.onNext(numberOfAttemptsText.text.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        gameDisposable = gameOverObservable.subscribe { gameText ->
            if (attemptsCounter == ONE_ATTEMPT_LEFT) attemptsSubText.text = ATTEMPT
            if (gameText == GAME_OVER) {
                startAgainButton.visibility = View.VISIBLE
                hideView(attemptsSubText)
            }
        }

        inputDisposable = inputtedTextObservable.subscribe { string ->
            var isMatching = SECRET_COUNTRY.toLowerCase() == string.toLowerCase()
            if (isMatching) hideView(attemptsSubText)
            checkInputtedValue(string, numberOfAttemptsText)
            inputtedColorEditText.text.clear()
        }
    }

    private fun hideView(view: View){
        view.visibility = View.GONE
    }

    private fun checkInputtedValue(string: String, textView: TextView) {
        val isSameValue = SECRET_COUNTRY.toLowerCase() == string.toLowerCase()
        when (isSameValue) {
            true -> showWinnerMessage(textView)
            false -> {
                handleReduceCounter(textView)
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        inputDisposable.dispose()
        resetCounter()
    }

    override fun onStop() {
        super.onStop()
        resetCounter()
    }

    private fun handleReduceCounter(textView: TextView) {
        if (attemptsCounter != 0) attemptsCounter -= 1
        val isZero = attemptsCounter == 0
        when (isZero) {
            true -> showLoserMessage(textView)
            else -> { textView.text = attemptsCounter.toString() }
        }
    }

    private fun resetCounter() {
        attemptsCounter = 10
    }

    private fun showWinnerMessage(textView: TextView) {
        textView.text = CORRECT
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        inputDisposable.dispose()
    }

    private fun showLoserMessage(textView: TextView) {
        textView.text = GAME_OVER
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        inputDisposable.dispose()
    }
}
