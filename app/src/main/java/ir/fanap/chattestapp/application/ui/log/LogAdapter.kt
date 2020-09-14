package ir.fanap.chattestapp.application.ui.log

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ir.fanap.chattestapp.R
import ir.fanap.chattestapp.application.ui.function.FunctionAdapter
import ir.fanap.chattestapp.bussines.model.LogClass


class LogAdapter(
    var logs: MutableList<LogClass>,
    private val listener: ViewHolderListener,
    private val context: Context
) :
    RecyclerView.Adapter<LogAdapter.ViewHolder>(),
    Filterable {


    interface ViewHolderListener {
        fun onItemShowPairedLog(pos: Int, lastSelected: Int, log: LogClass)
    }


    var filteredLogs: MutableList<LogClass> = logs
    var selectedItemPosation = -1

    fun refreshList(logs: MutableList<LogClass>) {
        this.logs = logs
        setLastSelected(-1)
        setIsFirstForFilter()
    }

    fun refreshForFilter(logs: MutableList<LogClass>,position: Int) {
        this.logs = logs
        setLastSelected(position)
    }

    fun setLastSelected(position: Int) {
        selectedItemPosation = position
        notifyDataSetChanged()
    }


    fun setIsFirstForFilter() {
        isFirstSelected = true
    }

    var isFirstSelected = true;

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {


        val logText = logs[position].shoinglog

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

        var colorValue = ContextCompat.getColor(context, R.color.white)
        if (selectedItemPosation != -1) {

            if (selectedItemPosation == position)
                colorValue = ContextCompat.getColor(context, R.color.green_inactive)

        }
        viewHolder.la_parent.setBackgroundColor(colorValue)


        viewHolder.logNum.text = "#${(position + 1)}"


        viewHolder.btnCopy.setOnClickListener {

            setClipboard(
                context = viewHolder.itemView.context,
                text = viewHolder.textViewLog.text.toString()
            )

        }
        viewHolder.itemView.setOnClickListener(View.OnClickListener {
            var pos = position
            if (!isFirstSelected) {
                if (pos > selectedItemPosation && selectedItemPosation != -1)
                    pos = pos - 1
            } else
                isFirstSelected = false

            listener.onItemShowPairedLog(
                pos,
                selectedItemPosation,
                logs[position]
            )

        })

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

                filteredLogs = results?.values as MutableList<LogClass>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var charString: String = constraint.toString()
                if (charString.isEmpty()) {
                    filteredLogs = logs
                } else {
                    var filteredLogsLst: MutableList<LogClass> = mutableListOf()
                    for (row in logs) {
                        if (row.log.toLowerCase().contains(charString.toLowerCase())) {
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
        val la_parent: LinearLayout = itemView.findViewById(R.id.la_parent)
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