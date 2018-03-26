package com.japtej.chunkingpractice

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var difficulty = 1
        radioGroup.check(radioEasy2.id)
        radioGroup.setOnCheckedChangeListener({ _,_ ->
            when {
                radioEasy1.isChecked -> {
                    difficulty = 0
                    numberText.setText("4")
                }

                radioEasy2.isChecked -> {
                    difficulty = 1
                    numberText.setText("4")
                }
                radioNormal.isChecked -> {
                    difficulty = 2
                    numberText.setText("6")
                }
                radioHard.isChecked -> {
                    difficulty = 3
                    numberText.setText("8")
                }
                radioExpert.isChecked -> {
                    difficulty = 4
                    numberText.setText("8")
                }
            }
        })



        activity_main.setOnClickListener({
            hideKeyboard()
        })

        startButton.setOnClickListener({

            val height = activity_main.height
            val width = activity_main.width

            val intent = Intent(this, PracticeActivity::class.java)
            if (radioGroup.checkedRadioButtonId == -1)
                Toast.makeText(this, "Please select a difficulty", Toast.LENGTH_SHORT).show()
            else{
                intent.putExtra("Difficulty", difficulty)
                intent.putExtra("Number", numberText.text.toString())
                intent.putExtra("Width", width)
                intent.putExtra("Height", height)
                startActivity(intent)
            }


            })
    }
    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}
