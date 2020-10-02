package com.ani.timegapanalyzer.dashboard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ani.timegapanalyzer.R
import com.ani.timegapanalyzer.utils.getMonth
import com.ani.timegapanalyzer.utils.replaceMonth
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Time
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var startDateStr: String = ""
    private var endDateStr: String = ""
    private var startTimeStr: String = ""
    private var endTimeStr: String = ""
    private var adapter : MeetingListAdapter? = null

    private val mMeetingModelList : ArrayList<MeetingModel> = arrayListOf()
    private val meetingDiffModelList : ArrayList<AdapterModel> = arrayListOf()
    private val mMap: LinkedHashMap<String, ArrayList<TimeModel>> = linkedMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(view: View) {
        when (view?.id) {
            R.id.start_time_edit_text -> {
                datePicker(true)
            }
            R.id.end_time_edit_text -> {
                datePicker()
            }
            R.id.submit_button -> {
                validateAndAdd()
            }
            else -> { }
        }
    }

    private fun validateAndAdd() {
        if (!start_time_edit_text?.text.isNullOrEmpty() && !end_time_edit_text?.text.isNullOrEmpty()) {
            val startTime = "T" +  startTimeStr.replace(" ","") + ":00Z"
            val endTime = "T" +  endTimeStr.replace(" ","") + ":00Z"
            val meetingModel = MeetingModel(start_date_text_view?.text?.toString(), end_date_text_view?.text?.toString(),
                startTime, endTime)
            mMeetingModelList.add(meetingModel)
            start_time_edit_text?.setText("")
            end_time_edit_text?.setText("")
            start_date_text_view?.text = ""
            end_date_text_view?.text = ""
            compareAndPushToAdapter()
        } else {
            Toast.makeText(this, "Please enter valid details", Toast.LENGTH_LONG).show()
        }
    }

    private fun compareAndPushToAdapter() {
        if (!mMeetingModelList.isNullOrEmpty()) {
            Collections.sort(mMeetingModelList, object : Comparator<MeetingModel> {
                override fun compare(o1: MeetingModel, o2: MeetingModel): Int {
                    return if (o1.startDate == null || o2.startDate == null) 0 else getDateFromString(o1.startDate+o1.startTime).compareTo(
                        getDateFromString(o2.startDate+o2.startTime)
                    )
                }
            })
        }

        reloadAdapter()
    }

    private fun reloadAdapter() {
        mMap.clear()
        for (i in 0 until mMeetingModelList.size) {
            findAndPlaceInMap(mMeetingModelList[i].startDate!!, TimeModel(mMeetingModelList[i].startTime, mMeetingModelList[i].endTime, mMeetingModelList[i].startDate!!+mMeetingModelList[i].startTime!!))
        }

        getList()
        if (adapter == null) {
            adapter = MeetingListAdapter(meetingDiffModelList)
            time_calculator_recycler_view.setHasFixedSize(true)
            time_calculator_recycler_view.layoutManager = LinearLayoutManager(this)
            time_calculator_recycler_view.adapter = adapter
        } else {
            adapter?.reloadList(meetingDiffModelList)
        }
    }

    private fun getList() {
        meetingDiffModelList.clear()
        for (key in mMap.keys) {
            meetingDiffModelList.add(AdapterModel(key, "", 0))
            val list:ArrayList<TimeModel> = mMap[key]!!
            Collections.sort(list, object : Comparator<TimeModel> {
                override fun compare(o1: TimeModel, o2:TimeModel): Int {
                    return if (o1 == null || o2 == null) 0 else getDateFromString(o1.startDate!!).compareTo(
                        getDateFromString(o2.startDate!!)
                    )
                }
            })
            for (i in 0 until list.size) {
                var diff = "No Gaps"
                if (i >= 1) {
                    if (!list[i].startTime.equals(list[i-1].endTime)) {
                        val endTimeArr = list[i-1].endTime?.split(":")
                        val startTimeArr = list[i].startTime?.split(":")
                        if (endTimeArr != null && 1 < endTimeArr!!.size
                            && startTimeArr != null && 1 < startTimeArr!!.size) {
                            val min = startTimeArr[1].trim().toInt() - endTimeArr[1].trim().toInt()
                            val hour = startTimeArr[0].trim().replace("T", "").replace("T", "").toInt() - endTimeArr[0].trim().replace("T", "").toInt()
                            if (hour != 0 || min != 0) {
                                var gapStart = ""
                                var gapEnd = ""
                                var startHour = endTimeArr[0].trim().replace("T", "").toInt()
                                if (endTimeArr[1].trim().toInt() + 1 == 60) {
                                    startHour += 1
                                    if (startHour > 9) {
                                        gapStart = (startHour).toString() + " : 00"
                                    } else {
                                        gapStart = "0" + (startHour).toString() + " : 00"
                                    }
                                } else {
                                    var startMinute = (endTimeArr[1].trim().toInt() + 1).toString()
                                    if (startMinute.length == 1) {
                                        startMinute = "0$startMinute"
                                    }
                                    if (startHour > 9) {
                                        gapStart = (endTimeArr[0].replace("T", "")) + " : " + startMinute
                                    } else {
                                        gapStart = "0" + (endTimeArr[0].replace("T", "")) + " : " + startMinute
                                    }
                                }
                                gapStart += if (endTimeArr[0].trim().replace("T", "").toInt() >  11) {
                                    " PM"
                                } else {
                                    " AM"
                                }

                                var endHour = startTimeArr[0].trim().replace("T", "").toInt() - 1
                                if (startTimeArr[1].trim().toInt() - 1 < 0) {
                                    if (endHour > 9) {
                                        gapEnd = (endHour).toString() + " : 59"
                                    } else {
                                        gapEnd = "0" + (endHour).toString() + " : 59"
                                    }
                                } else {
                                    var hour = startTimeArr[0].trim().replace("T", "")
                                    var min = (startTimeArr[1].trim().toInt() - 1).toString()
                                    if (hour.length == 1) {
                                        hour = "0$hour"
                                    }
                                    if (min.length == 1) {
                                        min = "0$min"
                                    }
                                    gapEnd = ("$hour : $min")
                                }
                                gapEnd += if (endHour >  11) {
                                    " PM"
                                } else {
                                    " AM"
                                }

                                diff = "$gapStart - $gapEnd"
                                meetingDiffModelList.add(AdapterModel("", diff, 1))
                            } else {
                                meetingDiffModelList.add(AdapterModel("", diff, 1))
                            }
                        }
                    }
                    if (i >= list.size - 1) {
                        break
                    }
                } else if (list.size == 1) {
                    meetingDiffModelList.add(AdapterModel("", diff, 1))
                }
            }

        }
    }

    private fun datePicker(isStartDate: Boolean = false) {
        // Get Current Date
        val c = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val date = year.toString() + "-" + (getMonth(monthOfYear )) + "-" + dayOfMonth.toString()
                if (isStartDate) {
                    start_date_text_view?.text = "$date"
                    startDateStr = date
                } else {
                    end_date_text_view?.text = "$date"
                    endDateStr = date
                }
                timePicker(isStartDate)
            }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    private fun timePicker(isStartDate: Boolean) {
        // Get Current Time
        val c = Calendar.getInstance()
        mHour = c.get(Calendar.HOUR_OF_DAY)
        mMinute = c.get(Calendar.MINUTE)

        // Launch Time Picker Dialog
        val timePickerDialog = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
               // val time = "'T'$hourOfDay : $minute:00'Z'"
                if (isStartDate) {
                    startTimeStr = "$hourOfDay : $minute"
                    if (minute > 9) {
                        start_time_edit_text?.setText("$hourOfDay : $minute" + if(hourOfDay > 12) " PM" else " AM")
                    } else {
                        start_time_edit_text?.setText("$hourOfDay : 0$minute" + if(hourOfDay > 12) " PM" else " AM")
                    }
                } else {
                    endTimeStr = "$hourOfDay : $minute"
                    if (minute > 9) {
                        end_time_edit_text?.setText("$hourOfDay : $minute" + if(hourOfDay > 12) " PM" else " AM")
                    } else {
                        end_time_edit_text?.setText("$hourOfDay : 0$minute" + if(hourOfDay > 12) " PM" else " AM")
                    }
                }
            }, mHour, mMinute, true)
        timePickerDialog.show()
    }

    private fun getDateFromString(dateString: String) : Date {
        var date = Date()
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        try {
            date = format.parse(replaceMonth(dateString))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    private fun findAndPlaceInMap(key: String, value: TimeModel) {
        if (mMap.containsKey(key)) {
            mMap[key]?.add(value)
        } else {
            val list = arrayListOf<TimeModel>()
            list.add(value)
            mMap[key] = list
        }
    }

    fun printDifference(startDate: Date, endDate: Date) : String {
        //milliseconds
        var different = endDate.time - startDate.time

        println("startDate : $startDate")
        println("endDate : $endDate")
        println("different : $different")

        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        val elapsedDays = different / daysInMilli
        different %= daysInMilli

        val elapsedHours = different / hoursInMilli
        different = different % hoursInMilli

        val elapsedMinutes = different / minutesInMilli
        different = different % minutesInMilli

        val elapsedSeconds = different / secondsInMilli

        return "$elapsedDays days + $elapsedHours hours + $elapsedMinutes mins + $elapsedSeconds secs"

    }
}