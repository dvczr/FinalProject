package com.dvczr.finalproject

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PopupActivity : AppCompatActivity() {

    private var popupTextToEdit = ""
    private var index = 0
    private var itemsList = ArrayList<String>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0,0)
        setContentView(R.layout.activity_popup)

        val popupConstraintLayout = findViewById<ConstraintLayout>(R.id.popup_constraintLayout)
        val popupOuterBorder = findViewById<CardView>(R.id.popup_cardView_outerBorder)
        val popupTitle = findViewById<TextView>(R.id.popup_cardView_title)
        val popupText = findViewById<EditText>(R.id.popup_cardView_text)
        val popupBtnSave = findViewById<Button>(R.id.popup_btnSave)
        val popupBtnCancel = findViewById<Button>(R.id.popup_btnCancel)

        itemsList = intent.getStringArrayListExtra("itemsList") as ArrayList<String>

        val bundle = intent.extras
        index = bundle?.getInt("index") ?: -1
        popupTextToEdit = bundle?.getString("textToEdit", "") ?: ""

        popupTitle.text = "#${index+1}  EDIT TASK"
        popupText.setText(popupTextToEdit)

        // Fade animation for the background of Popup Window
        val alpha = 0
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#ffffff"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)

        colorAnimation.duration = 250 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popupConstraintLayout.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()

        // Fade animation for the Popup Window
        popupOuterBorder.alpha = 0f
        popupOuterBorder
            .animate()
            .alpha(1f)
            .setDuration(250)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Close the Popup Window when you press the button
        popupBtnSave.setOnClickListener {
            onBackPressed()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("editedItemsList", itemsList)
            intent.putExtra("itemTextEdited", popupText.text.toString())
            intent.putExtra("editIndex", index)
            startActivity(intent)
        }

        popupBtnCancel.setOnClickListener {
            onBackPressed()

        }

    }




    override fun onBackPressed() {
        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 0
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#552f6e"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)

        val popupConstraintLayout = findViewById<ConstraintLayout>(R.id.popup_constraintLayout)
        val popupOuterBorder = findViewById<CardView>(R.id.popup_cardView_outerBorder)

        colorAnimation.duration = 250
        colorAnimation.addUpdateListener { animator ->
            popupConstraintLayout.setBackgroundColor(
                animator.animatedValue as Int
            )
        }
        // Fade animation for the Popup Window when you press the back button
        popupOuterBorder
            .animate()
            .alpha(0f)
            .setDuration(250)
            .setInterpolator(DecelerateInterpolator())
            .start()
        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }
}