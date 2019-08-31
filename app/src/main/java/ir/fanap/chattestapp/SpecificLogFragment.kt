package ir.fanap.chattestapp


import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.fanap.chattestapp.application.ui.log.LogAdapter2
import ir.fanap.chattestapp.bussines.model.LogClass
import kotlinx.android.synthetic.main.bottom_sheet_log.*
import kotlinx.android.synthetic.main.fragment_log.*
import kotlinx.android.synthetic.main.specific_log_fragment.*
import kotlinx.android.synthetic.main.specific_log_fragment.fabGoTop

/**
 * A simple [Fragment] subclass.
 */
class SpecificLogFragment : BottomSheetDialogFragment() {

    private var logs: ArrayList<LogClass> = ArrayList()

    private lateinit var logAdapter: LogAdapter2


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.specific_log_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logs = arguments?.getParcelableArrayList("LOGS")!!

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initial()

    }

    private fun initial() {



        if(logs.isEmpty()){

            imgViewNoLog.visibility = View.VISIBLE
            tvNoLog.visibility = View.VISIBLE
        }

        logAdapter = LogAdapter2(logs)

        recyclerViewLogs.adapter = logAdapter

        val linearLayoutManager = LinearLayoutManager(context)

        recyclerViewLogs.layoutManager = linearLayoutManager



        buttonCloseLogs.setOnClickListener {

            dialog.dismiss()

        }

        fabGoDown.hide()
        
        fabGoTop.hide()

        buttonCloseLogs.show()


        recyclerViewLogs.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerViewLogs: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerViewLogs, dx, dy)


                val lastItem = linearLayoutManager.findLastVisibleItemPosition()

                val first = linearLayoutManager.findFirstVisibleItemPosition()

                val itemCount = linearLayoutManager.itemCount

                if (dy > 0) {

                    fabGoDown.show()
                    fabClear.show()
                    buttonCloseLogs.show()
                    fabGoTop.hide()

                } else if (dy < 0) {

                    fabGoDown.hide()
                    fabClear.hide()
                    fabGoTop.show()
//                    buttonCloseLogs.hide()

                }

                if((lastItem + 1) == itemCount){
                    fabGoDown.hide()
                    fabClear.hide()
//                    buttonCloseLogs.hide()
                }

                if(first == 0){
                    fabGoTop.hide()

                }


            }



        })

        fabGoDown.setOnClickListener {

            val ls = linearLayoutManager.findLastVisibleItemPosition() + 1


            recyclerViewLogs.smoothScrollToPosition(ls)

        }

        fabGoDown.setOnLongClickListener {

            recyclerViewLogs.scrollToPosition(logs.size - 1)

            return@setOnLongClickListener true

        }

        fabGoTop.setOnClickListener {



            val fv = linearLayoutManager.findFirstVisibleItemPosition() - 1

            recyclerViewLogs.smoothScrollToPosition(fv)

        }

        fabGoTop.setOnLongClickListener {

            recyclerViewLogs.scrollToPosition(0)

            return@setOnLongClickListener true
        }

        fabClear.setOnClickListener {

            logAdapter.clearLog()

            dialog.dismiss()

        }
        
        

    }

    fun addLog(logs:ArrayList<LogClass> ){


        this.logs = logs

        logAdapter.notifyDataSetChanged()
    }


}
