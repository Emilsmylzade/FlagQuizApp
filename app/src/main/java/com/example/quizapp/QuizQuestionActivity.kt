package com.example.quizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.lang.reflect.GenericArrayType

class QuizQuestionActivity : AppCompatActivity(), View.OnClickListener {

    private var myCurrentPosition: Int = 1
    private var myQuestionList: ArrayList<Question>? = null
    private var mySelectedOptionPosition: Int = 0
    private var myUserName: String? = null
    private var myCorrectAnswers: Int = 0

    private var progressBar: ProgressBar? = null
    private var tvProgress: TextView? = null
    private var tvQuestion: TextView? = null
    private var ivImage: ImageView? = null

    private var tvOptionOne: TextView? = null
    private var tvOptionTwo: TextView? = null
    private var tvOptionThree: TextView? = null
    private var tvOptionFour: TextView? = null

    private var btnSubmit: Button? = null

    private var correctVoiceEffect: MediaPlayer? = null
    private var wrongVoiceEffect: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizz_question)

        progressBar = findViewById(R.id.progress_bar)
        tvProgress = findViewById(R.id.tv_progress)
        tvQuestion = findViewById(R.id.tv_question)
        ivImage = findViewById(R.id.iv_image)
        myUserName = intent.getStringExtra(Constants.USER_NAME)

        tvOptionOne = findViewById(R.id.tv_optionOne)
        tvOptionTwo = findViewById(R.id.tv_optionTwo)
        tvOptionThree = findViewById(R.id.tv_optionThree)
        tvOptionFour = findViewById(R.id.tv_optionFour)

        btnSubmit = findViewById(R.id.btn_submit)

        tvOptionOne?.setOnClickListener(this)
        tvOptionTwo?.setOnClickListener(this)
        tvOptionThree?.setOnClickListener(this)
        tvOptionFour?.setOnClickListener(this)
        btnSubmit?.setOnClickListener(this)

        myQuestionList = Constants.getQuestions()

        correctVoiceEffect = MediaPlayer.create(this, R.raw.correct_answer_voice_effect)
        wrongVoiceEffect = MediaPlayer.create(this, R.raw.wrong_answer_voice_effect)

        setQuestion()

    }

    private fun setQuestion() {
        val questionsList = Constants.getQuestions()
        Log.i("Questions List size", "${questionsList.size}")

        val question: Question = questionsList[myCurrentPosition - 1]
        ivImage?.setImageResource(question.image)
        progressBar?.progress = myCurrentPosition
        tvProgress?.text = "$myCurrentPosition/${(progressBar?.max)?.plus(1)}"
        tvQuestion?.text = question.question
        tvOptionOne?.text = question.optionOne
        tvOptionTwo?.text = question.optionTwo
        tvOptionThree?.text = question.optionThree
        tvOptionFour?.text = question.optionFour

        btnSubmit?.text = "SUBMIT"

    }

    private fun defaultOptionView(){
        val options = ArrayList<TextView>()
        tvOptionOne?.let{
            options.add(0,it)
        }
        tvOptionTwo?.let{
            options.add(1,it)
        }
        tvOptionThree?.let{
            options.add(2,it)
        }
        tvOptionFour?.let{
            options.add(3,it)
        }

        for(option in options){
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this, R.drawable.default_option_border_bg)
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int){
        defaultOptionView()

        mySelectedOptionPosition = selectedOptionNum

        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)
    }


    override fun onClick(view: View?) {
        when(view?.id){
            // Handle click event for tv_optionOne
            R.id.tv_optionOne -> {
                if (btnSubmit?.text != "NEXT QUESTION") {
                    tvOptionOne?.let{
                        selectedOptionView(it, 1)
                    }
                }
            }
            // Handle click event for tv_optionTwo
            R.id.tv_optionTwo -> {
                if (btnSubmit?.text != "NEXT QUESTION") {
                    tvOptionTwo?.let{
                        selectedOptionView(it, 2)
                    }
                }
            }
            // Handle click event for tv_optionThree
            R.id.tv_optionThree -> {
                if (btnSubmit?.text != "NEXT QUESTION") {
                    tvOptionThree?.let{
                        selectedOptionView(it, 3)
                    }
                }
            }
            // Handle click event for tv_optionFour
            R.id.tv_optionFour -> {
                if (btnSubmit?.text != "NEXT QUESTION") {
                    tvOptionFour?.let{
                        selectedOptionView(it, 4)
                    }
                }
            }
            R.id.btn_submit ->{
                if(btnSubmit?.text == "NEXT QUESTION"){
                    myCurrentPosition++
                    setQuestion()
                    defaultOptionView()
                    mySelectedOptionPosition = 0
                }
                else if(btnSubmit?.text == "FINISH"){
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra(Constants.USER_NAME, myUserName)
                    intent.putExtra(Constants.CORRECT_ANSWERS,myCorrectAnswers)
                    intent.putExtra(Constants.TOTAL_QUESTIONS, myQuestionList?.size)
                    startActivity(intent)
                    finish()
                }
                else{
                    if(mySelectedOptionPosition == 0){
                        if(myCurrentPosition <= myQuestionList!!.size) {
                            setQuestion()
                        }
                    }
                    else{
                        val question = myQuestionList?.get(myCurrentPosition-1)
                        if(question!!.correctAnswer != mySelectedOptionPosition){
                            answerView(mySelectedOptionPosition, R.drawable.wrong_option_border_bg)
                            wrongVoiceEffect?.start()
                            wrongVoiceEffect = MediaPlayer.create(this, R.raw.wrong_answer_voice_effect)
                        }
                        else{
                            myCorrectAnswers++
                            correctVoiceEffect?.start()
                            correctVoiceEffect = MediaPlayer.create(this, R.raw.correct_answer_voice_effect)
                        }
                        answerView(question.correctAnswer, R.drawable.correct_option_border_bg)




                        if(myCurrentPosition == myQuestionList!!.size){
                            btnSubmit?.text = "FINISH"
                        }else{
                            btnSubmit?.text = "NEXT QUESTION"
                        }
                    }
                }

            }
        }
    }

    private fun answerView(answer: Int, drawableView: Int){
        when(answer){
            1 -> {
                tvOptionOne?.background = ContextCompat.getDrawable(this,drawableView)
                tvOptionOne?.setTypeface(tvOptionOne?.typeface, Typeface.BOLD)
                tvOptionOne?.setTextColor((Color.parseColor("#363A43")))
            }
            2 -> {
                tvOptionTwo?.background = ContextCompat.getDrawable(this,drawableView)
                tvOptionTwo?.setTypeface(tvOptionTwo?.typeface, Typeface.BOLD)
                tvOptionTwo?.setTextColor((Color.parseColor("#363A43")))
            }
            3 -> {
                tvOptionThree?.background = ContextCompat.getDrawable(this,drawableView)
                tvOptionThree?.setTypeface(tvOptionThree?.typeface, Typeface.BOLD)
                tvOptionThree?.setTextColor((Color.parseColor("#363A43")))
            }
            4 -> {
                tvOptionFour?.background = ContextCompat.getDrawable(this,drawableView)
                tvOptionFour?.setTypeface(tvOptionFour?.typeface, Typeface.BOLD)
                tvOptionFour?.setTextColor((Color.parseColor("#363A43")))
            }

        }
    }
}