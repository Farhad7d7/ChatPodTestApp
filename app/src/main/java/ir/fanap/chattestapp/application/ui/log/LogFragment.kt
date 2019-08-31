package ir.fanap.chattestapp.application.ui.log

import android.app.SearchManager
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
import android.content.Context
import android.text.Html
import com.fanap.podchat.mainmodel.ChatMessage
import com.fanap.podchat.util.ChatMessageType
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ir.fanap.chattestapp.application.ui.IOnBackPressed





class LogFragment : Fragment(), TestListener, IOnBackPressed {


    private lateinit var mainViewModel: MainViewModel
    private var logs: MutableList<String> = mutableListOf()
    private lateinit var logAdapter: LogAdapter
    private lateinit var searchView: SearchView

    companion object {
        fun newInstance(): LogFragment {
            return LogFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)


        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerV_funcLog)
        val fabGoDown: FloatingActionButton = view.findViewById(R.id.fActionButton)
//        val Toolbar: Toolbar = view.findViewById(R.id.toolbarLog)
//        (activity as AppCompatActivity).setSupportActionBar(toolbarLog)
//        recyclerView.setHasFixedSize(true)

        fabGoDown.hide()
        fabGoTop.hide()

        logAdapter = LogAdapter(logs)
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

                if((lastItem + 1) == itemCount){
                    fabGoDown.hide()
                    fabClearLog.hide()
                }

                if(first == 0){
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

            recyclerView.smoothScrollToPosition(fv)

        }

        fabGoTop.setOnLongClickListener {

            recyclerView.scrollToPosition(0)

            return@setOnLongClickListener true
        }

        fabClearLog.setOnClickListener {

            logAdapter.clearLog()

        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
            .create(MainViewModel::class.java)
        mainViewModel.setTestListener(this)
        setHasOptionsMenu(true)

    }

    override fun onBackPressed(): Boolean {
        if (searchView.isIconified) {
            searchView.setIconifiedByDefault(true)
            return true
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.menu_log, menu)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView.maxWidth = Int.MAX_VALUE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                logAdapter.filter.filter(p0)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                logAdapter.filter.filter(p0)
                return false
            }

        })

    }

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


        if(logName.isEmpty()) return

        val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        val chatMessage: ChatMessage = gson.fromJson(json, ChatMessage::class.java)

        if(chatMessage.type == ChatMessageType.Constants.PING) return


        var jsonS = json.replace("\"type\"","<font color='#03a9f4'>type</font>")



        if (logName=="Error"){

            val ln = "\n\n  <font color='#EE0000'> <<< <b>$logName</b> >>> </font> \n\n $jsonS"

            logs.add(ln)

        }else{

            val logText = "\n\n <font color='#4caf50' size=8> <<< <b>$logName</b> >>> </font> \n\n $jsonS"

            logs.add(logText)

        }






        activity?.runOnUiThread {
            logAdapter.notifyItemInserted(logs.size - 1)
            logAdapter.notifyDataSetChanged()
        }


        //todo model Logs

    }
}