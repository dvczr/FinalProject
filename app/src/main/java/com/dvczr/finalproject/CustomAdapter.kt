package com.dvczr.finalproject

import android.annotation.SuppressLint
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

internal class CustomAdapter(private var itemsList: ArrayList<String>) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    private lateinit var mListener: OnItemClickListener
    private lateinit var mListenerLong: OnLongItemClickListener
    private lateinit var mListenerCheckBox: OnCheckedChangeListener

    var itemFilterList = ArrayList<String>()

    init {
        itemFilterList = itemsList
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnLongItemClickListener  {
        fun onLongItemClick(position: Int)
    }
    fun setOnLongItemClickListener(listenerLong: OnLongItemClickListener) {
        mListenerLong = listenerLong
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(position: Int)
    }
    fun setOnCheckedChangeListener(listenerCheckBox: OnCheckedChangeListener) {
        mListenerCheckBox = listenerCheckBox
    }

    @SuppressLint("NotifyDataSetChanged")
    internal inner class MyViewHolder(view: View, listener: OnItemClickListener, listenerLong: OnLongItemClickListener, listenerCheckBox: OnCheckedChangeListener) : RecyclerView.ViewHolder(view) {
        var itemTextView : TextView = view.findViewById(R.id.item_cardView_text)
        private val itemCheckBox: CheckBox = view.findViewById(R.id.item_checkBox)

        init {
            itemTextView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
            itemTextView.setOnLongClickListener {
                listenerLong.onLongItemClick(bindingAdapterPosition)
                return@setOnLongClickListener true
            }
            itemCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    listenerCheckBox.onCheckedChange(absoluteAdapterPosition)
                    Handler().postDelayed({
                        if (itemCheckBox.isChecked) {
                            println("\tTASK CHECKED")
                            itemsList.removeAt(bindingAdapterPosition)
                            notifyItemRemoved(bindingAdapterPosition)
                            notifyDataSetChanged()
                        } else {
                            println("\tTASK CHECKED UNDONE")
                        }
                    },2000L)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.activity_item,parent,false)
        return MyViewHolder(itemView, mListener, mListenerLong, mListenerCheckBox)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemFilterList[position]
        holder.itemTextView.text = item
    }

    override fun getItemCount(): Int {
        return itemFilterList.size
    }

    fun getFilter() : Filter {
        return object : Filter() {
            @SuppressLint("SetTextI18n")
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    itemFilterList = itemsList
                } else {
                    val resultList = ArrayList<String>()
                    for (row in itemsList) {
                        if (row.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
//                    if (resultList.isEmpty()) {
//                        feedbackText.visibility = View.VISIBLE
////                        resultList.add("Oops!\nCouldn't find what you were searching after, please try again.")
//                    }
                    itemFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = itemFilterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null) {
                    itemFilterList = results.values as ArrayList<String>
                    notifyDataSetChanged()
                }
//                else {
//                    itemFilterList.add("Oops!\nCouldn't find what you were searching after, please try again.")
//                    notifyDataSetChanged()
//                }
            }
        }
    }
    /* SWIPE GESTURE */
    /*  DELETE ITEM  */
    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(view: RecyclerView.ViewHolder, position: Int) {
        itemsList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }
}
