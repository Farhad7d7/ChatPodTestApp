package ir.fanap.chattestapp.application.ui.log

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import ir.fanap.chattestapp.R
import ir.fanap.chattestapp.application.ui.MainViewModel
import ir.fanap.chattestapp.application.ui.TestListener
import kotlinx.android.synthetic.main.fragment_log.*
import android.support.annotation.StringDef
import android.util.Log
import android.widget.Toast
import com.fanap.podchat.mainmodel.ChatMessage
import com.fanap.podchat.util.ChatMessageType
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ir.fanap.chattestapp.bussines.model.LogClass
import kotlin.math.log


class LogFragment : Fragment(), TestListener, LogAdapter.ViewHolderListener {


    private lateinit var mainViewModel: MainViewModel
    private var logs: MutableList<LogClass> = mutableListOf()
    private lateinit var logAdapter: LogAdapter
    private lateinit var searchView: SearchView
    private var selected = SelectedFilterType.FILTER_ALL;


    companion object {
        fun newInstance(): LogFragment {
            return LogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    lateinit var recyclerView: RecyclerView;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerView = view.findViewById(R.id.recyclerV_funcLog)
        val fabGoDown: FloatingActionButton = view.findViewById(R.id.fActionButton)
//        val Toolbar: Toolbar = view.findViewById(R.id.toolbarLog)
//        (activity as AppCompatActivity).setSupportActionBar(toolbarLog)
//        recyclerView.setHasFixedSize(true)

        fabGoDown.hide()
        fabGoTop.hide()

        logAdapter = context?.let { LogAdapter(logs, this, context = it) }!!
        recyclerView.adapter = logAdapter
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager



        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                //changed

                val lastItem = linearLayoutManager.findLastVisibleItemPosition()

                val first = linearLayoutManager.findFirstVisibleItemPosition()

                val itemCount = linearLayoutManager.itemCount

                if (dy > 0) {

                    fabGoDown.show()
                    fabClearLog.show()
                    fabGoTop.hide()

                } else if (dy < 0) {

                    fabGoDown.hide()
                    fabClearLog.hide()
                    fabGoTop.show()

                }

                if ((lastItem + 1) == itemCount) {
                    fabGoDown.hide()
                    fabClearLog.hide()
                }

                if (first == 0) {
                    fabGoTop.hide()
                }


            }


        })

        fabGoDown.setOnClickListener {

            val ls = linearLayoutManager.findLastVisibleItemPosition() + 1


            recyclerView.smoothScrollToPosition(ls)

        }

        fabGoDown.setOnLongClickListener {

            recyclerView.scrollToPosition(logs.size - 1)

            return@setOnLongClickListener true

        }

        fabGoTop.setOnClickListener {


            val fv = linearLayoutManager.findFirstVisibleItemPosition() - 1

            if (fv < 0)
                recyclerView.smoothScrollToPosition(0)
            else
                recyclerView.smoothScrollToPosition(fv)

        }

        fabGoTop.setOnLongClickListener {

            recyclerView.scrollToPosition(0)

            return@setOnLongClickListener true
        }

        fabClearLog.setOnClickListener {

            logAdapter.clearLog()

        }

        //radion group for filter logs
        radioGroup.setOnCheckedChangeListener { group, i ->
            when (group.checkedRadioButtonId) {

                R.id.rdb_all -> {
                    selected = SelectedFilterType.FILTER_ALL;
                }

                R.id.rdb_errors -> {
                    selected = SelectedFilterType.FILTER_ERRORS;
                }

                R.id.rdb_request -> {
                    selected = SelectedFilterType.FILTER_REQUEST;
                }

                R.id.rdb_response -> {
                    selected = SelectedFilterType.FILTER_RESPONSE;
                }
                R.id.rdb_others -> {
                    selected = SelectedFilterType.FILTER_OTHERS;
                }

                else -> {

                }
            }

            refreshWithFilter()
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
                .create(MainViewModel::class.java)

        mainViewModel.setTestListener(this)


        mainViewModel.listOfLogs.onInsertObserver

            .subscribe {

                Log.d("LTAG", "New log added $it")

            }

    }

//    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        super.onCreateOptionsMenu(menu, inflater)
//        menu?.clear()
//        inflater?.inflate(R.menu.menu_log, menu)
//        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
//        searchView.maxWidth = Int.MAX_VALUE
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//                logAdapter.filter.filter(p0)
//                return false
//            }
//
//            override fun onQueryTextChange(p0: String?): Boolean {
//                logAdapter.filter.filter(p0)
//                return false
//            }
//
//        })
//
//    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        if (id == R.id.action_search) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLogEvent(log: String) {
        super.onLogEvent(log)
//        logs.add(log)
//        activity?.runOnUiThread {
//            logAdapter.notifyItemInserted(logs.size - 1)
//            logAdapter.notifyDataSetChanged()
//        }
    }

    override fun onLogEventWithName(logName: String, json: String) {
        super.onLogEventWithName(logName, json)

        if (logName.isEmpty()) return

        val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        val chatMessage: ChatMessage = gson.fromJson(json, ChatMessage::class.java)

        if (chatMessage.type == ChatMessageType.Constants.PING) return

        var logs: ArrayList<LogClass> = refactorLog(logName = logName!!, log = json!!, gson = gson)

        addNewLogToList(logs.get(0))

    }

    fun addNewLogToList(it: LogClass) {

        when (selected) {

            SelectedFilterType.FILTER_ALL -> {

                var test = getItemNormal(it)
                if (test != null)
                    logs.add(test)

            }

            SelectedFilterType.FILTER_ERRORS -> {

                var test = getErrorItems(it)
                if (test != null)
                    logs.add(test)

            }

            SelectedFilterType.FILTER_REQUEST -> {

                var test = getRequestItems(it)
                if (test != null)
                    logs.add(test)

            }

            SelectedFilterType.FILTER_RESPONSE -> {

                var test = getResponseItems(it)
                if (test != null)
                    logs.add(test)

            }

            SelectedFilterType.FILTER_OTHERS -> {

                var test = getOtherItems(it)
                if (test != null)
                    logs.add(test)

            }

            else -> {

                var test = getItemNormal(it)
                if (test != null)
                    logs.add(test)

            }

        }

        activity?.runOnUiThread {
            logAdapter.notifyItemInserted(logs.size - 1)

            logAdapter.notifyDataSetChanged()
        }

    }

    //if filter state change needs to refresh data
    fun refreshWithFilter() {

        var temp: MutableList<LogClass> = mutableListOf()

        mainViewModel.listOfLogs.forEach()
        {
//            addNewLogToList(it)
            when (selected) {

                SelectedFilterType.FILTER_ALL -> {

                    var test = getItemNormal(it)
                    if (test != null)
                        temp.add(test)

                }

                SelectedFilterType.FILTER_ERRORS -> {

                    var test = getErrorItems(it)
                    if (test != null)
                        temp.add(test)

                }

                SelectedFilterType.FILTER_REQUEST -> {

                    var test = getRequestItems(it)
                    if (test != null)
                        temp.add(test)

                }

                SelectedFilterType.FILTER_RESPONSE -> {

                    var test = getResponseItems(it)
                    if (test != null)
                        temp.add(test)

                }

                SelectedFilterType.FILTER_OTHERS -> {

                    var test = getOtherItems(it)
                    if (test != null)
                        temp.add(test)

                }

                else -> {

                    var test = getItemNormal(it)
                    if (test != null)
                        temp.add(test)

                }

            }

        }

        logs = temp;
        logAdapter.refreshList(temp);
    }

    fun getRequestItems(logClass: LogClass): LogClass? {

        if (logClass.logName.startsWith("SEND") || logClass.logName.startsWith("GET")) {
            return getItemNormal(logClass)
        }

        return null

    }

    fun getResponseItems(logClass: LogClass): LogClass? {

        if (logClass.logName.startsWith("RECEIVE")  || logClass.logName.startsWith("ON")) {
            return getItemNormal(logClass)
        }

        return null

    }

    fun getErrorItems(logClass: LogClass): LogClass? {

        if (logClass.logName.startsWith("Error")) {
            return getItemNormal(logClass)
        }

        return null

    }

    fun getItemNormal(logClass: LogClass): LogClass? {

        var temp: String = "-1";
        var jsonS = logClass.getJSon()
        var logName = logClass.logName;

        if (logName.startsWith("Error")) {
            temp = "\n\n  <font color='#EE0000'> <<< <b>$logName</b> >>> </font> \n\n $jsonS"
        } else
            temp = "\n\n <font color='#4caf50' size=8> <<< <b>$logName</b> >>> </font> \n\n $jsonS"

        logClass.shoinglog = temp
        return logClass;

    }

    // getFilterByOthers
    fun getOtherItems(logClass: LogClass): LogClass? {

        if (logClass.logName.startsWith("Error") ||
            logClass.logName.startsWith("SEND") ||
            logClass.logName.startsWith("GET") ||
            logClass.logName.startsWith("ON") ||
            logClass.logName.startsWith("RECEIVE")
        ) {
            return null;
        } else {
            return getItemNormal(logClass)
        }

    }
  // call when click a log from list
    //this method for find paired item with a log of response or requests
    override fun onItemShowPairedLog(pos: Int, lastSelected: Int, log: LogClass) {

        if (pos == lastSelected || selected == SelectedFilterType.FILTER_ALL)
            return


        if (lastSelected != -1)
            logs.removeAt(lastSelected)

        val log = findPairedItem(log)
        if (log != null) {

            logs.add(pos, log)

            logAdapter.refreshForFilter(logs,pos)
            recyclerView.scrollToPosition(pos)
        } else {
            logAdapter.setLastSelected(-1)
            Toast.makeText(context, "NOt Found !!!", Toast.LENGTH_SHORT).show()
        }

    }

    // fing paired log
    fun findPairedItem(log: LogClass): LogClass? {

        for (item in mainViewModel.listOfLogs) {
            if (item.uniqueId == log.uniqueId)
                if (item.logName != log.logName) {
                    getItemNormal(item)?.let {
                        return  it
                    }
                    break
                }
        }

        return null;
    }

}

class SelectedFilterType {



   //filter types
    companion object {
        const val FILTER_ALL = "FILTER_ALL"
        const val FILTER_ERRORS = "FILTER_ERRORS"
        const val FILTER_REQUEST = "FILTER_REQUEST"
        const val FILTER_RESPONSE = "FILTER_RESPONSE"
        const val FILTER_OTHERS = "FILTER_OTHERS"
    }

    @StringDef(
        SelectedFilterType.FILTER_ALL,
        SelectedFilterType.FILTER_ERRORS,
        SelectedFilterType.FILTER_REQUEST,
        SelectedFilterType.FILTER_REQUEST,
        SelectedFilterType.FILTER_OTHERS
    )

    @Retention(AnnotationRetention.SOURCE)
    annotation class CONSTANT
}