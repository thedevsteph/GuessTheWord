package yass.stephanie.com.rxjavaplayground

import android.view.KeyEvent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@LargeTest
class GuessTheWordTest {


    companion object {
        const val WRONG_GUESS = "Germany"
        const val MAX_NUMBER_OF_CHANCES = "10"
        const val GAME_OVER = "GAME OVER"
        const val CORRECT = "CORRECT"
        const val CORRECT_GUESS = "france"
        const val ATTEMPT_LEFT = "attempt left"
    }

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun activityDisplaysTitleOnTheScreen() {
        seesOnScreen(R.id.color_title)
    }

    @Test
    fun activityDisplaysEditBoxOnScreenTheScreen() {
        seesOnScreen(R.id.guess_color_input)
    }

    @Test
    fun activityDisplaysLivesCounterOnScreen() {
        seesOnScreen(R.id.number_of_attempts)
    }

    @Test
    fun activityDisplaysLivesCounterOnScreenWithMaxValue() {
        seesOnScreen(R.id.number_of_attempts)
        checksViewContainsStringWithText(R.id.number_of_attempts, MAX_NUMBER_OF_CHANCES)
    }

    @Test
    fun activityDisplaysAttemptsTextOnScreenTheScreen() {
        seesOnScreen(R.id.attempts_text)
    }

    @Test
    fun enteringTheWrongGuessReducesCounter() {
        onView(withId(R.id.guess_color_input)).perform(typeText(WRONG_GUESS))
        pressEnterOnView(R.id.guess_color_input)
        checksViewContainsStringWithText(R.id.number_of_attempts, "9")
    }

    @Test
    fun counterGoesToGameOverAfterMaxNumberOfAttempts() {
        viewShowsGameOver()
        checksViewContainsStringWithText(R.id.number_of_attempts, GAME_OVER)
    }

    @Test
    fun onGameOverStartAgainButtonAppears() {
        viewShowsGameOver()
        seesOnScreen(R.id.start_again)
    }

    @Test
    fun onGameOverAttemptsSubTextIsNotVisible(){
        viewShowsGameOver()
        onView(withId(R.id.attempts_text)).check(matches(not(isDisplayed())))
    }

    @Test
    fun guessingTheCorrectAnswerShowsWinningText() {
        makeCorrectGuessInEditText()
        checksViewContainsStringWithText(R.id.number_of_attempts, CORRECT)
        onView(withId(R.id.attempts_text)).check(matches(not(isDisplayed())))
    }


    @Test
    fun checksAttemptCounterReducesValueByOneEachTime() {
        var counter = 10
        val hasGameEnded: Boolean = counter <= 0
        for (i in 1..counter) {
            makeWrongGuessInEditText()
            counter -= 1
            when (hasGameEnded) {
                false -> checksViewContainsStringWithText(R.id.number_of_attempts, counter.toString())
                else -> checksViewContainsStringWithText(R.id.number_of_attempts, GAME_OVER)
            }
        }
    }

    @Test
    fun afterGameIsLostAttemptCounterRemainsAtGameOver() {
        viewShowsGameOver()
        for (i in 1..5) {
            makeWrongGuessInEditText()
        }
        checksViewContainsStringWithText(R.id.number_of_attempts, GAME_OVER)
    }

    @Test
    fun onOneAttemptLeftSubTextChangesToReflectThis(){
        for (i in 1..9) {
            makeWrongGuessInEditText()
        }
        checksViewContainsStringWithText(R.id.number_of_attempts, "1")
        checksViewContainsStringWithText(R.id.attempts_text, ATTEMPT_LEFT)

    }

    @Test
    fun afterGameIsWonAttemptCounterRemainsAtCorrect() {
        makeCorrectGuessInEditText()
        makeWrongGuessInEditText()
        checksViewContainsStringWithText(R.id.number_of_attempts, CORRECT)
    }

    private fun findOnScreenAndInputsStringValue(id: Int, value: String) {
        onView(withId(id)).perform(typeText(value))
    }

    private fun seesOnScreen(id: Int) {
        onView(withId(id))
            .check(matches(isDisplayed()))
    }

    private fun checksViewContainsStringWithText(id: Int, text: String) {
        onView(withId(id)).check(matches(withText(text)))
    }

    private fun pressEnterOnView(id: Int) {
        onView(withId(id)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER))
    }

    private fun viewShowsGameOver() {
        for (i in 0..10) {
           makeWrongGuessInEditText()
        }
    }

    private fun makeWrongGuessInEditText(){
        findOnScreenAndInputsStringValue(R.id.guess_color_input, WRONG_GUESS)
        pressEnterOnView(R.id.guess_color_input)
    }

    private fun makeCorrectGuessInEditText(){
        findOnScreenAndInputsStringValue(R.id.guess_color_input, CORRECT_GUESS)
        pressEnterOnView(R.id.guess_color_input)
    }


}
