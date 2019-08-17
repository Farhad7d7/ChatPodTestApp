package ir.fanap.chattestapp.application.ui.log

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import ir.fanap.chattestapp.R

class LogAdapter(val logs: MutableList<String>) : RecyclerView.Adapter<LogAdapter.ViewHolder>(),Filterable {

    var filteredLogs: MutableList<String> = logs

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {


        val logText = logs[position]

        var beautifyText: String

        beautifyText = logText.replace("{","\t { \n")
        beautifyText =beautifyText.replace("[","\t [ \n")
        beautifyText =beautifyText.replace("}","\n \t }")
        beautifyText =beautifyText.replace("]","\n \t ]")
        beautifyText =beautifyText.replace(",",",\n")

        viewHolder.textViewLog.text = beautifyText

        viewHolder.logNum.text = "#$position"

    }



    fun clearLog(){

        logs.clear()
        filteredLogs.clear()
        notifyDataSetChanged()
        //changed

    }
    override fun getFilter(): Filter {

        return object : Filter(){
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                filteredLogs = results?.values as MutableList<String>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var charString:String = constraint.toString()
                if (charString.isEmpty()) {
                    filteredLogs = logs
                }else{
                var filteredLogsLst: MutableList<String> = mutableListOf()
                    for (row in logs) {
                        if (row.toLowerCase().contains(charString.toLowerCase())) {
                            filteredLogsLst.add(row)
                        }
                    }

                    filteredLogs = filteredLogsLst
                }

                var filterResults: FilterResults? = null
                filterResults?.values = filteredLogs
                return filterResults!!
            }

        }
    }
    override fun getItemCount(): Int {
        return logs.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_log, viewGroup, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewLog: TextView = itemView.findViewById(R.id.textView_log)
        var logNum: TextView = itemView.findViewById(R.id.tvLogNum)
    }


    fun additem() {

    }
}