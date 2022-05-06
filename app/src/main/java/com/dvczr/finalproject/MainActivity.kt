package com.dvczr.finalproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private var itemsList = ArrayList<String>()
    private lateinit var viewModel: ItemClickCountViewModel
    private lateinit var customAdapter: CustomAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[ItemClickCountViewModel::class.java]



        val recyclerView = findViewById<RecyclerView>(R.id.main_recycleView)
        customAdapter = CustomAdapter(itemsList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter

        val btnAdd = findViewById<FloatingActionButton>(R.id.main_fab)
        val nestedScrollView: View = findViewById(R.id.main_nestedScrollView)
        bottomSheetBehavior = BottomSheetBehavior.from(nestedScrollView)
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val searchView = findViewById<SearchView>(R.id.main_searchView)
        val userTextInput = findViewById<EditText>(R.id.main_editText)
        val intentPopupEdit = Intent(this, PopupActivity::class.java)

        val bundle = intent.extras
        val editIndex = bundle?.getInt("editIndex") ?: -1
        val editText = bundle?.getString("itemTextEdited", "¿isBenny?") ?: ""
        val editedItemsList = intent.getStringArrayListExtra("editedItemsList")

//        val feedbackText = findViewById<TextView>(R.id.main_searchView_feedbackText)

        if (editedItemsList != null) {
            if (editedItemsList.isNotEmpty()) {
                for (index in 0..editedItemsList.lastIndex) {
                    itemsList.add((editedItemsList[index].toString()))
                }
                itemsList[editIndex] = editText
            }
        }

        /* SWIPE GESTURE */
        val swipeGesture = object : SwipeGesture(this) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction) {
                    ItemTouchHelper.LEFT -> {
                        customAdapter.deleteItem(viewHolder, viewHolder.bindingAdapterPosition)
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(recyclerView)

        /* SOFTKEYBORD MOD: IME_ACTION_DONE */
        userTextInput.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)) {
                        Handler().postDelayed({
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        },200)
                        plus()
                        addTodo(userTextInput)
                    }
                    else {
                        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                    true
                }
                else -> false
            }
        }

        btnAdd.setOnClickListener {
            println("CLICK was called")
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        btnAdd.setOnLongClickListener {view ->
            println("LONG CLICK was called")
            Snackbar.make(view, "Load DATA", Snackbar.LENGTH_LONG)
                .setAction("Ok", View.OnClickListener {
                    loadItemsList()
                } )
                .show()
            return@setOnLongClickListener true
        }

        customAdapter.setOnItemClickListener(object: CustomAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                plus()
            }
        })


        customAdapter.setOnCheckedChangeListener(object: CustomAdapter.OnCheckedChangeListener {
            override fun onCheckedChange(position: Int) {

            }
        })


        customAdapter.setOnLongItemClickListener(object: CustomAdapter.OnLongItemClickListener{
            override fun onLongItemClick(position: Int) {
                if (position >= 0) {
//                    intentPopupEdit.putExtra("itemsList", itemsList)
                    intentPopupEdit.putExtra("itemsList", itemsList)
                    intentPopupEdit.putExtra("index", position)
                    intentPopupEdit.putExtra("textToEdit", itemsList[position])
//                    intentPopupEdit.putExtra("textToEdit", itemsList[position])
                    startActivity(intentPopupEdit)

                }
            }
        })

        bottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                println("bottomSheetBehavior state changed: $newState")
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                btnAdd.animate()
                    .scaleX(1F-slideOffset)
                    .scaleY(1F-slideOffset)
                    .setDuration(150)
                    .start()
            }
        })
        /* SEARCH VIEW */
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(query: String): Boolean {
                customAdapter.getFilter().filter(query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                customAdapter.getFilter().filter(newText)
                return false
            }
        })
    }

    /* CHECKBOX TOGGLE DONE FUNCTION */
    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            when (view.id) {
                R.id.item_checkBox -> {
                    if (view.isChecked) {
                        Handler().postDelayed({
                            view.isChecked = false
                        },2000L)
                    }
                }
            }
        }
    }
    /* ADD TO DO FUNCTION */
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun addTodo(userTextInput : EditText) {

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            if (userTextInput.text.isNotEmpty()) {
                itemsList.add(userTextInput.text.toString())
                customAdapter.notifyDataSetChanged()
                userTextInput.setText("")
            }
            else {
                Toast.makeText(this, "Ooops something went wrong!\nPlease try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    /* ADD ITEMS TO LIST FUNCTION */
    @SuppressLint("NotifyDataSetChanged")
    private fun loadItemsList() {
        itemsList.add(" 1.\tLorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent eget scelerisque sapien. Sed cras.")
        itemsList.add(" 2.\tNulla at dui a ante congue elementum sodales eu elit. Pellentesque venenatis congue arcu ut viverra.")
        itemsList.add(" 3.\tSed accumsan metus ipsum. Duis tempus felis ut ex vestibulum condimentum. Sed vel purus quis nullam.")
        itemsList.add(" 4.\tNunc mauris elit, rutrum ac erat eget, tincidunt porta orci. Sed sapien nisi, feugiat non arcu quam.")
        itemsList.add(" 5.\tPraesent blandit odio ac scelerisque varius. Donec condimentum est arcu, id dapibus massa tincidunt.")
        itemsList.add(" 6.\tDonec et auctor libero. Donec a nisl nec nisi suscipit eleifend. Vivamus luctus ullamcorper vivamus.")
        itemsList.add(" 7.\tUt pharetra pharetra enim, in scelerisque dolor sagittis id. Duis scelerisque purus nec risus donec.")
        itemsList.add(" 8.\tNulla vel felis dictum, pellentesque magna nec, blandit neque. Praesent lectus ipsum, malesuada leo.")
        itemsList.add(" 9.\tInteger consectetur nibh ut dui interdum mollis eget a enim. Sed lacinia augue et dictum porta ante.")
        itemsList.add("10.\tNam in turpis tristique, tempus nisi nec, facilisis dui. In elit diam, molestie at lacinia in justo.")
        customAdapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    private fun plus() {
        val feedbackText: TextView = findViewById(R.id.main_searchView_feedbackText)
        viewModel.listNr++
        feedbackText.text = "You have clicked on ${viewModel.listNr} items so far"
    }
}