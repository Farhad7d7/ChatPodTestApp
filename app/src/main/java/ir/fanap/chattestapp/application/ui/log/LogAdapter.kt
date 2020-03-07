package ir.fanap.chattestapp.application.ui.log

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import ir.fanap.chattestapp.R


class LogAdapter(val logs: MutableList<String>) : RecyclerView.Adapter<LogAdapter.ViewHolder>(),
    Filterable {

    var filteredLogs: MutableList<String> = logs


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {


        val logText = logs[position]

        var beautifyText: String

        beautifyText = logText.replace("{", "{<br>")
        beautifyText = beautifyText.replace("[", "[<br>")
        beautifyText = beautifyText.replace("}", "<br>}")
        beautifyText = beautifyText.replace("]", "<br>]")
        beautifyText = beautifyText.replace(",", ",<br>")
        beautifyText = beautifyText.replace("\n", "<br>")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

//            beautifyText = logText.replace("{","{<br>")
//            beautifyText =beautifyText.replace("[","[<br>")
//            beautifyText =beautifyText.replace("}","<br>}")
//            beautifyText =beautifyText.replace("]","<br>]")
//            beautifyText =beautifyText.replace(",",",<br>")
//            beautifyText =beautifyText.replace("\n","<br>")

            viewHolder.textViewLog.text = Html.fromHtml(beautifyText, Html.FROM_HTML_MODE_LEGACY)

        } else {

//            beautifyText = logText.replace("{","\t{\n")
//            beautifyText =beautifyText.replace("[","\t[\n")
//            beautifyText =beautifyText.replace("}","\n\t}")
//            beautifyText =beautifyText.replace("]","\n\t]")
//            beautifyText =beautifyText.replace(",",",\n")

            viewHolder.textViewLog.text = Html.fromHtml(beautifyText)

        }

        viewHolder.logNum.text = "#${(position + 1)}"


        viewHolder.btnCopy.setOnClickListener {


            setClipboard(
                context = viewHolder.itemView.context,
                text = viewHolder.textViewLog.text.toString()
            )
        }

    }


    fun clearLog() {

        logs.clear()
        filteredLogs.clear()
        notifyDataSetChanged()
        //changed

    }

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                filteredLogs = results?.values as MutableList<String>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var charString: String = constraint.toString()
                if (charString.isEmpty()) {
                    filteredLogs = logs
                } else {
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
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_log, viewGroup, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewLog: TextView = itemView.findViewById(R.id.textView_log)
        var logNum: TextView = itemView.findViewById(R.id.tvLogNum)
        val btnCopy: FloatingActionButton = itemView.findViewById(R.id.btnCopy)
    }


    //Added
    private fun setClipboard(context: Context, text: String) {

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("Copied Text", text)
        clipboard.primaryClip = clip

        Toast.makeText(context, "Text Copied to clipboard", Toast.LENGTH_LONG)
            .show()


    }


}