package com.japtej.chunkingpractice

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintSet
import android.support.constraint.solver.widgets.ConstraintWidget
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_practice.*
import java.security.SecureRandom
import java.util.*
import kotlin.collections.ArrayList

class PracticeActivity : AppCompatActivity() {

    val alphabets = arrayOf("S","T","U", "X", "L")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice)


        val difficulty =  intent.getIntExtra("Difficulty",0)
        val numWords = intent.getStringExtra("Number").toInt()

        practiceViewLayout.setOnClickListener({
            hideKeyboard()
        })


        var countArr = flashBuilder(difficulty + 1, numWords)

        //flash alphabets on screen for 2 seconds
        Handler().postDelayed({
            flashLayout.removeAllViews()
            resultBuilder(difficulty, alphabets, countArr)
        },2000)

    }

    fun hideKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    fun resultBuilder(difficulty: Int, alphabets: Array<String>, countAr: ArrayList<String>){

        var countArr = countAr
        //creating views and adding constraints
        for(i in 0..difficulty){

            //textView initialization
            val textView = TextView(this)
            textView.id = i+1
            textView.text= "Total ${alphabets[i]}"
            textView.textSize=24F
            textView.typeface= Typeface.DEFAULT_BOLD
            textView.gravity = Gravity.CENTER

            resultLayout.addView(textView)

            val editText = EditText(this)

            editText.id= i+10
            editText.setText("0")
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.textSize=24F
            editText.gravity= Gravity.CENTER

            resultLayout.addView(editText)

            val resultText = TextView(this)

            resultText.id = i + 20
            resultText.visibility = View.INVISIBLE
            resultText.textSize=24F
            resultText.typeface= Typeface.DEFAULT_BOLD
            resultText.gravity = Gravity.CENTER

            resultLayout.addView(resultText)

            //adding constraints to views
            val set = ConstraintSet()
            set.constrainWidth(textView.id, ConstraintSet.WRAP_CONTENT)
            set.constrainHeight(textView.id, ConstraintSet.WRAP_CONTENT)
            set.constrainWidth(editText.id, ConstraintSet.WRAP_CONTENT)
            set.constrainHeight(editText.id, ConstraintSet.WRAP_CONTENT)
            set.constrainWidth(resultText.id, ConstraintSet.WRAP_CONTENT)
            set.constrainHeight(resultText.id, ConstraintSet.WRAP_CONTENT)

            var parentID = textView.id - 1

            set.connect(textView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            set.connect(textView.id, ConstraintSet.RIGHT, editText.id, ConstraintSet.LEFT)

            if(i == 0){

                set.connect(textView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                set.connect(textView.id, ConstraintSet.BOTTOM, parentID, ConstraintSet.BOTTOM)
                set.setVerticalBias(textView.id, 0.2F)

            }
            else
                set.connect(textView.id, ConstraintSet.TOP, parentID, ConstraintSet.BOTTOM, 8)


            val editParent = textView.id
            set.connect(editText.id, ConstraintSet.LEFT, textView.id, ConstraintSet.RIGHT)
            set.connect(editText.id, ConstraintSet.RIGHT, resultText.id, ConstraintSet.LEFT)
            set.connect(editText.id, ConstraintSet.BASELINE, editParent, ConstraintSet.BASELINE)


            val resultParent = editText.id
            set.connect(resultText.id, ConstraintSet.LEFT, editText.id, ConstraintSet.RIGHT)
            set.connect(resultText.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            set.connect(resultText.id, ConstraintSet.BASELINE, resultParent, ConstraintSet.BASELINE)

            set.createHorizontalChain(
                    textView.id,
                    ConstraintSet.LEFT,
                    resultText.id,
                    ConstraintSet.RIGHT,
                    intArrayOf(textView.id, resultText.id),
                    null,
                    ConstraintWidget.CHAIN_SPREAD

            )

            set.applyTo(resultLayout)

        }

        val resultButton = Button(this)
        val id = 500
        resultButton.id = id
        resultLayout.addView(resultButton)
        resultButton.text = "RESULT"
        val set = ConstraintSet()
        set.constrainWidth(resultButton.id, ConstraintSet.WRAP_CONTENT)
        set.constrainHeight(resultButton.id, ConstraintSet.WRAP_CONTENT)
        set.connect(resultButton.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(resultButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.connect(resultButton.id, ConstraintSet.TOP, findViewById<View>(difficulty+20).id, ConstraintSet.BOTTOM)
        set.connect(resultButton.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        set.applyTo(resultLayout)

        for(i in 0..difficulty){

            findViewById<TextView>(i+20).text = ""
        }

        val numWords = intent.getStringExtra("Number").toInt()

        resultButton.setOnClickListener({
            if(resultButton.text == "RESULT")
                checkResult(difficulty, countArr)
            else {
                resultLayout.removeAllViews()
                countArr = flashBuilder(difficulty + 1, numWords)
                Handler().postDelayed({
                    flashLayout.removeAllViews()
                    resultBuilder(difficulty, alphabets, countArr)
                },2000)
        }
        })

    }

    fun flashBuilder(difficulty: Int, numWords:Int) : ArrayList<String>{

        //to store alphabets displayed
        val arrBuild = ArrayList<String>()
        var id = 0

        //need to assign r1 to var because arguments in function call are by default val in Kotlin
        var range = numWords
        val r = SecureRandom()

        //dimensions of screen covered by app. Cannot be measured during onCreate, thus passed
        //through intent of previous activity
        val height = intent.getIntExtra("Height", 0)
        val width = intent.getIntExtra("Width",0)

        var limiter = 0



        //to ensure no extra alphabets are displayed
        while(range > 0){

            //to iterate through alphabets. Higher difficulty, more alphabets
            for (i in 1..difficulty){


                id++
                val randAlpha = SecureRandom()

                //to ensure no iteration is made after range = 0
                if(range == 0)
                    break
                limiter = when{

                    limiter> range -> {
                        range
                    }
                    i == difficulty -> {
                        range
                    }
                    else -> {
                        r.nextInt(range)
                    }
                }

                //to add multiple alphabets
                for (x in 1..limiter){

                    id++
                    val selectAlpha = randAlpha.nextInt(difficulty)
                    arrBuild.add(alphabets[selectAlpha])

                    val tv = TextView(this)
                    tv.text = alphabets[selectAlpha]
                    tv.textSize = 26F
                    tv.setTextColor(Color.BLACK)
                    tv.setTypeface(tv.typeface, Typeface.BOLD)
                    tv.id = id
                    tv.setPadding(10,10,10,10)

                    flashLayout.addView(tv)

                    var set = ConstraintSet()

                    set.constrainHeight(tv.id, ConstraintSet.WRAP_CONTENT)
                    set.constrainWidth(tv.id, ConstraintSet.WRAP_CONTENT)

                    //add constraints with random margins
                    set.connect(tv.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, r.nextInt(width))
                    set.connect(tv.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, r.nextInt(width))
                    set.connect(tv.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, r.nextInt(height))
                    set.connect(tv.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, r.nextInt(height))

                    set.applyTo(flashLayout)

                }

                range -= limiter
            }

        }
        return arrBuild

    }

    fun checkResult(difficulty: Int, countArr: ArrayList<String>){
        for(i in 0..difficulty){
            when (i) {
                0 -> {
                    val count = Collections.frequency(countArr, "S")
                    findViewById<TextView>(i+20).text = """(${findViewById<EditText>(i + 10).text}/$count)"""
                    if(count.toString() == findViewById<EditText>(i+10).text.toString())
                        findViewById<TextView>(i+20).setTextColor(Color.GREEN)
                    else
                        findViewById<TextView>(i+20).setTextColor(Color.RED)
                }
                1 -> {
                    val count = Collections.frequency(countArr, "T")
                    findViewById<TextView>(i+20).text = """(${findViewById<EditText>(i + 10).text}/$count)"""
                    if(count.toString() == findViewById<EditText>(i+10).text.toString())
                        findViewById<TextView>(i+20).setTextColor(Color.GREEN)
                    else
                        findViewById<TextView>(i+20).setTextColor(Color.RED)
                }
                2 -> {
                    val count = Collections.frequency(countArr, "U")
                    findViewById<TextView>(i+20).text = """(${findViewById<EditText>(i + 10).text}/$count)"""
                    if(count.toString() == findViewById<EditText>(i+10).text.toString())
                        findViewById<TextView>(i+20).setTextColor(Color.GREEN)
                    else
                        findViewById<TextView>(i+20).setTextColor(Color.RED)
                }
                3 -> {
                    val count = Collections.frequency(countArr, "X")
                    findViewById<TextView>(i+20).text = """(${findViewById<EditText>(i + 10).text}/$count)"""
                    if(count.toString() == findViewById<EditText>(i+10).text.toString())
                        findViewById<TextView>(i+20).setTextColor(Color.GREEN)
                    else
                        findViewById<TextView>(i+20).setTextColor(Color.RED)
                }
                4 -> {
                    val count = Collections.frequency(countArr, "L")
                    findViewById<TextView>(i+20).text = """(${findViewById<EditText>(i + 10).text}/$count)"""
                    if(count.toString() == findViewById<EditText>(i+10).text.toString())
                        findViewById<TextView>(i+20).setTextColor(Color.GREEN)
                    else
                        findViewById<TextView>(i+20).setTextColor(Color.RED)
                }
            }

        }
        val i = 500
        findViewById<Button>(i).text = "RETRY"
    }

}
