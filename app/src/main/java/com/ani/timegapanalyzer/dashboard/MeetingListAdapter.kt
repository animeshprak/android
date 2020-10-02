package com.ani.timegapanalyzer.dashboard

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.ani.timegapanalyzer.R


class MeetingListAdapter(private var mMeetingModelList: ArrayList<AdapterModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_header_layout, parent, false)
            VHHeader(itemView)
        } else  {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_item_layout, parent, false)
            VHItem(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val adapterModel = mMeetingModelList[position]
        if (holder is VHItem) {
            val vhItem = holder as VHItem
            vhItem.timeDifference?.text = adapterModel.timeDifference
        } else if (holder is VHHeader) {
            val vhHeader = holder as VHHeader
            vhHeader.title?.text = adapterModel.dateString
        }
    }

    override fun getItemCount(): Int {
        return mMeetingModelList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPositionHeader(mMeetingModelList[position].itemType)) TYPE_HEADER else TYPE_ITEM
    }

    private fun isPositionHeader(position: Int): Boolean {
        return position == 0
    }

    private fun getItem(position: Int): AdapterModel {
        return mMeetingModelList[position - 1]
    }

    internal inner class VHItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var timeDifference: AppCompatTextView? = null
        init {
            timeDifference = itemView.findViewById(R.id.time_diff_item_text_view)
        }
    }

    fun reloadList(list : ArrayList<AdapterModel>) {
        mMeetingModelList = list
        notifyDataSetChanged()
    }

    internal inner class VHHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: AppCompatTextView? = null
        init {
            title = itemView.findViewById(R.id.date_text_view)
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }
}