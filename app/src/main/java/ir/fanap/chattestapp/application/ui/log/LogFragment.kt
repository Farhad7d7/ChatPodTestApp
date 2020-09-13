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
import com.fanap.podchat.mainmodel.ChatMessage
import com.fanap.podchat.util.ChatMessageType
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ir.fanap.chattestapp.bussines.model.LogClass


class LogFragment : Fragment(), TestListener, LogAdapter.ViewHolderListener {


    private lateinit var mainViewModel: MainViewModel
    private var logs: MutableList<String> = mutableListOf()
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


        var jsonS = json.replace("\"type\"", "<font color='#03a9f4'>type</font>")


//
//
//        if (logName == "Error") {
//
//            val ln = "\n\n  <font color='#EE0000'> <<< <b>$logName</b> >>> </font> \n\n $jsonS"
//
//            logs.add(ln)
//
//        } else {
//
//            val logText =
//                "\n\n <font color='#4caf50' size=8> <<< <b>$logName</b> >>> </font> \n\n $jsonS"
//
//            logs.add(logText)
//
//        }


        //  resetLogs(refactorLog(logName = logName!!, log = json!!, gson = gson), logName);

//        activity?.runOnUiThread {
//            logAdapter.notifyItemInserted(logs.size - 1)
//
//            logAdapter.notifyDataSetChanged()
//        }
    }


    fun checkFilterState() {

        //if filter state change needs to refresh data

        when (selected) {

            SelectedFilterType.FILTER_ALL -> {

            }

            SelectedFilterType.FILTER_ERRORS -> {

            }

            SelectedFilterType.FILTER_REQUEST -> {

            }

            SelectedFilterType.FILTER_RESPONSE -> {

            }
            else -> {
            }

        }

    }

    fun refreshWithFilter() {
        var temp: MutableList<String> = mutableListOf()

        mainViewModel.listOfLogs.forEach()
        {

            when (selected) {

                SelectedFilterType.FILTER_ALL -> {

                    var test = prepareNormal(it)
                    if (test != "-1")
                        temp.add(test)

                }

                SelectedFilterType.FILTER_ERRORS -> {

                    var test = prepareErrorItem(it)
                    if (test != "-1")
                        temp.add(test)

                }

                SelectedFilterType.FILTER_REQUEST -> {

                    var test = prepareRequestItem(it)
                    if (test != "-1")
                        temp.add(test)

                }

                SelectedFilterType.FILTER_RESPONSE -> {

                    var test = prepareResponseItem(it)
                    if (test != "-1")
                        temp.add(test)

                }

                SelectedFilterType.FILTER_OTHERS -> {

                    var test = prepareOthers(it)
                    if (test != "-1")
                        temp.add(test)

                }

                else -> {

                    var test = prepareNormal(it)
                    if (test != "-1")
                        temp.add(test)

                }

            }

            Log.e(tag, it.logName + "--" + it.uniqueId)

        }

        isFirstSelected = true
        logs = temp;
        logAdapter.refreshList(temp);
    }

    fun prepareRequestItem(logClass: LogClass): String {

        var temp: String = "-1";

        if (logClass.logName.startsWith("SEND")) {
            temp = logClass.logName;
        }

        return temp

    }

    fun prepareResponseItem(logClass: LogClass): String {

        var temp: String = "-1";

        if (logClass.logName.startsWith("RECEIVE")) {
            temp = logClass.logName;
        }

        return temp

    }

    fun prepareErrorItem(logClass: LogClass): String {

        var temp: String = "-1";

        if (logClass.logName.startsWith("Error")) {
            temp = logClass.logName;
        }

        return temp

    }

    fun prepareNormal(logClass: LogClass): String {

        return logClass.logName;

    }

    fun prepareOthers(logClass: LogClass): String {

        var temp: String = "-1";

        if (logClass.logName.startsWith("Error") ||
            logClass.logName.startsWith("SEND") ||
            logClass.logName.startsWith("RECEIVE")
        ) {
            return temp;
        } else
            temp = logClass.logName;

        return temp;

    }


    override fun onItemShowParedLog(pos: Int, lastSelected: Int) {

        if (pos == lastSelected)
            return
        if (pos == lastSelected + 1 && lastSelected != -1)
            return


        logs = removeLastSelected(pos, lastSelected)

        logs = prepareAddParedItems(pos, lastSelected)

        logAdapter.refreshList(logs)
        if (pos != 0)
            recyclerView.scrollToPosition(pos - 1)

    }

    fun removeLastSelected(pos: Int, lastSelected: Int): MutableList<String> {

        var temp: MutableList<String> = mutableListOf()
        var trackerPos = 0;

        logs.forEach() {
            if (lastSelected != trackerPos)
                temp.add(it)

            trackerPos++;
        }

        return temp;

    }

    var isFirstSelected = true;
    fun prepareAddParedItems(pos: Int, lastSelected: Int): MutableList<String> {

        var temp: MutableList<String> = mutableListOf()
        var trackerPos = 0;
        var selectedPos=pos;
        if (!isFirstSelected) {
//            selectedPos = selectedPos - 1
        }else
            isFirstSelected = false

        logs.forEach() {

            if (selectedPos == trackerPos)
                temp.add("paredreq")


            temp.add(it)

            trackerPos++;
        }

        return temp;

    }

}

class SelectedFilterType {
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