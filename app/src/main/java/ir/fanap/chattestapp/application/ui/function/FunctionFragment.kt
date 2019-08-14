package ir.fanap.chattestapp.application.ui.function

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fanap.podchat.chat.RoleType
import com.fanap.podchat.mainmodel.*
import com.fanap.podchat.model.*
import com.fanap.podchat.requestobject.*
import com.fanap.podchat.util.InviteType
import com.fanap.podchat.util.ThreadType
import com.github.javafaker.Faker
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wang.avi.AVLoadingIndicatorView
import ir.fanap.chattestapp.R
import ir.fanap.chattestapp.application.ui.MainViewModel
import ir.fanap.chattestapp.bussines.model.Method
import ir.fanap.chattestapp.application.ui.TestListener
import ir.fanap.chattestapp.application.ui.util.ConstantMsgType
import ir.fanap.chattestapp.application.ui.util.ConstantMsgType.Companion.GET_HISTORY
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodFuncFour
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodFuncOne
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodFuncThree
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodFuncTwo
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodNames
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FunctionFragment : Fragment(), FunctionAdapter.ViewHolderListener, TestListener {


    var TEST_THREAD_ID: Long = 10955L

    private lateinit var buttonConect: Button
    private lateinit var switchCompat_sandBox: SwitchCompat
    private lateinit var recyclerView: RecyclerView
    private lateinit var mainViewModel: MainViewModel
    private lateinit var appCompatImageView_noResponse: AppCompatImageView
    private lateinit var txtView_noResponse: TextView


    private lateinit var avLoadingIndicatorView: AVLoadingIndicatorView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerViewSmooth: RecyclerView.SmoothScroller
    private lateinit var bottom_sheet_log: ConstraintLayout
    private lateinit var bottomSheetLog: BottomSheetBehavior<ConstraintLayout>
    private var gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private var methods: MutableList<Method> = mutableListOf()
    private var fucCallback: HashMap<String, String> = hashMapOf()
    private var fucCallbacks: HashMap<String, ArrayList<String>> = hashMapOf()
    private lateinit var textView_state: TextView
    private lateinit var textView_log: TextView
    private lateinit var functionAdapter: FunctionAdapter
    private var sandbox = false
    private val faker: Faker = Faker()


    /**
     *
     * SandBox Config:
     *
     *
     */

    private val sand_name = "SandBox"
    private val SAND_TOKEN = "b4735b4c3e5a4b4798ac3eb523087efc"
    private val sand_socketAddress = "wss://chat-sandbox.pod.land/ws"
    private val sand_serverName = "chat-server"
    private val sand_appId = "POD-Chat"
    private val sand_ssoHost = "https://accounts.pod.land/"
    private val sand_platformHost = "https://sandbox.pod.land:8043/srv/basic-platform/"
    private val sand_fileServer = "https://sandbox.pod.land:8443/"


    /**
     * Mehdi Sheikh Hosseini
     */

//    works:
    private val name = "zizi"
    private val TOKEN = "7cba09ff83554fc98726430c30afcfc6"
    private val socketAddress = "ws://172.16.110.131:8003/ws" // {**REQUIRED**} Socket Address
    private val ssoHost = "http://172.16.110.76" // {**REQUIRED**} Socket Address
    private val platformHost = "http://172.16.110.131:8080/"
    private val fileServer = "http://172.16.110.131:8080/" // {**REQUIRED**} File Server Address
    private val serverName = "chat-server2"
    private val typeCode: String? = null


    //TODO change to CallBackMethod
    /**/
    companion object {

        fun newInstance(): FunctionFragment {
            return FunctionFragment()
        }
    }

    override fun onLogClicked(clickedViewHolder: FunctionAdapter.ViewHolder) {
        var position = clickedViewHolder.adapterPosition
        if (textView_log.text.isEmpty()) {
            appCompatImageView_noResponse.visibility = View.VISIBLE
            txtView_noResponse.visibility = View.VISIBLE
        } else {
            appCompatImageView_noResponse.visibility = View.GONE
            txtView_noResponse.visibility = View.GONE
        }

        bottomSheetLog.state = BottomSheetBehavior.STATE_EXPANDED
        when (position) {
            0 -> {
//
//                if (bottomSheetLog.state != null) {
//                    Toast.makeText(activity, bottomSheetLog.state.toString(), Toast.LENGTH_SHORT).show()
//                }
            }
            1 -> {
                textView_log.text = methods[position].log
            }


        }

    }

    override fun onIconClicked(clickedViewHolder: FunctionAdapter.ViewHolder) {
        var position = clickedViewHolder.adapterPosition
        when (position) {
            0 -> {
                createThread()
            }
            1 -> {
                getContact()
            }
            2 -> {
                blockContact()
            }
            3 -> {
                addContact()
            }
            4 -> {
                getThread()
            }
            5 -> {
                getBlockList()
            }
            6 -> {
                unBlockContact()
            }
            7 -> {
                updateContact()
            }
            8 -> {
                sendTextMsg()
            }
            9 -> {
                removeContact()
            }
            10 -> {
                addParticipant()
            }
            11 -> {
                removeParticipant()
            }
            12 -> {
                forwardMessage()
            }
            13 -> {
                replyMessage()
            }
            14 -> {
                leaveThread()
            }
            15 -> {
                muteThread()
            }
            16 -> {
                unMuteThread()
            }
            17 -> {
                deleteMessage()
            }
            18 -> {
                editMessage()
            }
            19 -> {
                getHistory()
            }
            20 -> {
                createThreadWithForwMessage()
            }
            21 -> {
                getPartitipant()
            }
            22 -> {

                clearHistory()

            }
            23 -> {

                getAdminsList()

            }
            24 -> {

                addAdminRoles()

            }
            25 -> {

                removeAdminRoles()

            }
            26 -> {

                deleteMultipleMessage()
            }
            27 -> {

                createThreadWithMessage()
            }
            28 -> {

                getDeliverList()
            }
            29 -> {

                getSeenList()
            }
            30 -> {

                searchContact()
            }

        }
    }

    private fun searchContact() {

        changeIconSend(30)

        val searchContact = SearchContact.Builder("0", "50")
            .firstName("Pooria")
            .build()

        fucCallback[ConstantMsgType.SEARCH_CONTACT] = mainViewModel.searchContact(searchContact)

    }

    private fun getSeenList() {

        changeIconSend(29)


        val requestThread = RequestThread.Builder().build()



        fucCallback[ConstantMsgType.GET_SEEN_LIST] = mainViewModel.getThread(requestThread)


    }

    private fun getDeliverList() {

        changeIconSend(28)

        val requestThread = RequestThread.Builder().build()

        fucCallback[ConstantMsgType.GET_DELIVER_LIST] = mainViewModel.getThread(requestThread)


    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_function, container, false)
        initView(view)

        val buttonClose = view.findViewById(R.id.button_close) as Button

        bottomSheetLog = BottomSheetBehavior.from<ConstraintLayout>(bottom_sheet_log)
        bottomSheetLog.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
//                        textView_state.text = "close"
                    }

                }
            }

        })

        buttonClose.setOnClickListener {
            if (bottomSheetLog.state != BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetLog.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        recyclerView.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerViewSmooth = object : LinearSmoothScroller(activity) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }

        for (i in 0 until methodNames.size) {
            val method = Method()
            method.methodName = methodNames[i]
            method.funcOne = methodFuncOne[i]
            method.funcTwo = methodFuncTwo[i]
            method.funcThree = methodFuncThree[i]
            method.funcFour = methodFuncFour[i]
            methods.add(method)
        }

        functionAdapter = FunctionAdapter(this.activity!!, methods, this)
        recyclerView.adapter = functionAdapter

        recyclerView.childCount
        buttonConect.setOnClickListener { connect() }
        switchCompat_sandBox.setOnCheckedChangeListener { _, isChecked ->
            sandbox = isChecked
        }

        return view
    }

    private fun initView(view: View) {
        buttonConect = view.findViewById(R.id.button_Connect)
        recyclerView = view.findViewById(R.id.recyclerV_funcFrag)
        textView_state = view.findViewById(R.id.textView_state)
        switchCompat_sandBox = view.findViewById(R.id.switchCompat_sandBox)
        avLoadingIndicatorView = view.findViewById(R.id.AVLoadingIndicatorView)
        bottom_sheet_log = view.findViewById(R.id.bottom_sheet_log)
        textView_log = view.findViewById(R.id.textView_log)
        appCompatImageView_noResponse = view.findViewById(R.id.appCompatImageView_noResponse)
        txtView_noResponse = view.findViewById(R.id.TxtView_noResponse)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        mainViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
            .create(MainViewModel::class.java)
//            .of(this).get(MainViewModel::class.java)
        mainViewModel.setTestListener(this)
        mainViewModel.observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                textView_state.text = it

                if (it == "CHAT_READY") {
                    avLoadingIndicatorView.visibility = View.GONE
                    textView_state.setTextColor(
                        ContextCompat.getColor(activity?.applicationContext!!, R.color.green_active)
                    )
                }


            }
    }

    private fun getHistory() {
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        val uniqueId = mainViewModel.getContact(requestGetContact)
        fucCallback[ConstantMsgType.GET_HISTORY] = uniqueId
    }

    private fun editMessage() {
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        val uniqueId = mainViewModel.getContact(requestGetContact)
        fucCallback[ConstantMsgType.EDIT_MESSAGE] = uniqueId
    }

    private fun deleteMessage() {
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        val uniqueId = mainViewModel.getContact(requestGetContact)
        fucCallback[ConstantMsgType.DELETE_MESSAGE] = uniqueId
    }


    override fun onGetSearchContactResult(response: ChatResponse<ResultContact>?) {
        super.onGetSearchContactResult(response)

        if (fucCallback[ConstantMsgType.SEARCH_CONTACT] == response?.uniqueId) {

            changeIconReceive(30)


            methods[30].methodNameFlag = true


        }


    }

    override fun onClearHistory(chatResponse: ChatResponse<ResultClearHistory>?) {
        super.onClearHistory(chatResponse)
        val position = 22
        changeIconReceive(position)
        methods[position].methodNameFlag = true
    }


    override fun onGetAdminList(content: ChatResponse<ResultParticipant>?) {
        super.onGetAdminList(content)


        if (fucCallback[ConstantMsgType.GET_ADMINS_LIST] == content?.uniqueId) {

            handleOnGetAdminList(content?.result)

            return

        }


        if (fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] == content?.uniqueId) {

            prepareRemoveAdminRoles(content, fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES])


        }


    }


    private fun handleOnGetAdminList(response: ResultParticipant?) {

        val position = 23

        fucCallback.remove(ConstantMsgType.GET_ADMINS_LIST)

        changeIconReceive(position)

        methods[position].methodNameFlag = true


    }

    override fun onSetRole(outputSetRoleToUser: ChatResponse<ResultSetAdmin>?) {

        super.onSetRole(outputSetRoleToUser)

        val addAdminPosition = 24

        val removeAdminPosition = 25

        when (outputSetRoleToUser?.uniqueId) {

            fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] -> {


                changeIconReceive(addAdminPosition)

                methods[addAdminPosition].methodNameFlag = true

            }

            fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] -> {


                changeIconReceive(removeAdminPosition)

                methods[removeAdminPosition].methodNameFlag = true


            }


        }


    }

    override fun onBlockList(response: ChatResponse<ResultBlockList>?) {
        super.onBlockList(response)
        var position = 5
        changeIconReceive(position)
        methods[position].methodNameFlag = true
    }

    override fun onError(chatResponse: ErrorOutPut?) {
        super.onError(chatResponse)
        activity?.runOnUiThread {
            Toast.makeText(activity, chatResponse?.errorMessage, Toast.LENGTH_LONG).show()
        }
        val uniqueId = chatResponse?.uniqueId

//        if (uniqueId == fucCallback[ConstantMsgType.ADD_CONTACT]) {
//            fucCallback[uniqueId]
//            activity?.runOnUiThread {
//
//                val viewHolder: RecyclerView.ViewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(3))
//
//                viewHolder.itemView.findViewById<AppCompatImageView>(R.id.checkBox_ufil)
//
//                    .setColorFilter(ContextCompat.getColor(activity!!, R.color.colorAccent))
//            }
//        }
    }

    override fun onUnBlock(response: ChatResponse<ResultBlock>?) {
        super.onUnBlock(response)
        val position = 6
        if (fucCallback[ConstantMsgType.UNBLOCK_CONTACT] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.UNBLOCK_CONTACT)

            changeIconReceive(position)
        }
    }

    override fun onGetThread(chatResponse: ChatResponse<ResultThreads>?) {
        super.onGetThread(chatResponse)



        if (fucCallback[ConstantMsgType.GET_THREAD] == chatResponse?.uniqueId) {
            val position = 4
            fucCallback.remove(ConstantMsgType.GET_THREAD)
            changeIconReceive(position)
            methods[4].methodNameFlag = true
        }

        if (fucCallback[ConstantMsgType.SEND_MESSAGE] == chatResponse?.uniqueId) {
            prepareSendMessage(chatResponse)
        }

        if (fucCallback[ConstantMsgType.CLEAR_HISTORY] == chatResponse?.uniqueId) {

            prepareClearHistory(chatResponse)


        }

        if (fucCallback[ConstantMsgType.GET_ADMINS_LIST] == chatResponse?.uniqueId) {

            prepareGetAdminList(chatResponse)

        }


        if (fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] == chatResponse?.uniqueId) {

            prepareAddAdmin(chatResponse)

        }



        if (fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] == chatResponse?.uniqueId) {

            prepareRemoveAdmin(chatResponse)

        }

        if (fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] == chatResponse?.uniqueId) {

            prepareDeleteMultipleMessage(chatResponse)

        }

        if (fucCallback[ConstantMsgType.GET_SEEN_LIST] == chatResponse?.uniqueId) {

            prepareSendTxtMsgForGetSeenList(chatResponse)

        }

        if (fucCallback[ConstantMsgType.GET_DELIVER_LIST] == chatResponse?.uniqueId) {

            prepareSendTxtMsgForGetDeliverList(chatResponse)

        }


    }

    private fun prepareSendTxtMsgForGetDeliverList(chatResponse: ChatResponse<ResultThreads>?) {

        if (chatResponse?.result!!.threads.size > 0) {

            val targetThreadId = chatResponse?.result!!.threads[0].id


            val requestMessage = RequestMessage
                .Builder("test text message ${Date()}", targetThreadId)
                .build()


            fucCallback[ConstantMsgType.GET_DELIVER_LIST] =
                mainViewModel.sendTextMsg(requestMessage)


        }


    }

    private fun prepareSendTxtMsgForGetSeenList(chatResponse: ChatResponse<ResultThreads>?) {

        if (chatResponse?.result!!.threads.size > 0) {

            val targetThreadId = chatResponse?.result!!.threads[0].id


            val requestMessage = RequestMessage
                .Builder("test text message ${Date()}", targetThreadId)
                .build()


            fucCallback[ConstantMsgType.GET_SEEN_LIST] =
                mainViewModel.sendTextMsg(requestMessage)


        }


    }

    private fun prepareDeleteMultipleMessage(chatResponse: ChatResponse<ResultThreads>?) {

        if (chatResponse?.result?.threads?.size!! > 0) {


            var threadId = chatResponse.result.threads[0].id


            for (thread: Thread in chatResponse.result.threads) {

                try {
                    if (thread.lastMessage != null) {

                        threadId = thread.id

                        break

                    }
                } catch (e: Exception) {
                    Log.d("MTAG", e.message)
                    continue
                }

            }



            if (threadId == 0L) {

                threadId = chatResponse.result.threads[0].id

            }


            TEST_THREAD_ID = threadId

            val requestGetHistory = RequestGetHistory
                .Builder(threadId)
                .offset(0)
                .count(50)
                .build()



            fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] = mainViewModel.getHistory(requestGetHistory)


        }


    }

    private fun prepareSendMessage(chatResponse: ChatResponse<ResultThreads>?) {
        if (chatResponse?.result?.threads?.size!! > 0) {
            fucCallback.remove(ConstantMsgType.SEND_MESSAGE)
            val threadId = chatResponse.result.threads[0].id
            val requestMessage = RequestMessage.Builder(faker.lorem().paragraph(), threadId).build()
            fucCallback[ConstantMsgType.SEND_MESSAGE] = mainViewModel.sendTextMsg(requestMessage)
        } else {
            val requestGetContact = RequestGetContact.Builder().build()
            fucCallback.remove(ConstantMsgType.SEND_MESSAGE)
            fucCallback[ConstantMsgType.SEND_MESSAGE] = mainViewModel.getContact(requestGetContact)
        }
    }


    private fun prepareGetAdminList(chatResponse: ChatResponse<ResultThreads>?) {

        if (chatResponse?.result?.threads?.size!! > 0) {

            fucCallback.remove(ConstantMsgType.GET_ADMINS_LIST)


            var threadId = 0L

            for (thread: Thread in chatResponse.result.threads) {

                if (thread.isGroup) {

                    threadId = thread.id

                    break


                }

            }


            //todo find correct threadId
            if (threadId == 0L) {

                threadId = chatResponse.result.threads[0].id

            }


            TEST_THREAD_ID = threadId


            val requestGetAdmin = RequestGetAdmin.Builder()
                .admin(true)
                .threadId(threadId)
                .count(50)
                .build()


            fucCallback[ConstantMsgType.GET_ADMINS_LIST] =
                mainViewModel.getAdminList(requestGetAdmin)

//
//                Toast
//                    .makeText(activity,"No Group found!",Toast.LENGTH_LONG)
//                    .show()
//
//
//                changeIconReceive(23)
//
//
//


        }
    }

    private fun prepareAddAdmin(chatResponse: ChatResponse<ResultThreads>?) {

        if (chatResponse?.result?.threads?.size!! > 0) {

//            fucCallback.remove(ConstantMsgType.ADD_ADMIN_ROLES)

            var threadId = 0L

            for (thread: Thread in chatResponse.result.threads) {

                if (thread.isGroup) {

                    threadId = thread.id

                    break


                }

            }


            //todo find correct threadId
            if (threadId == 0L) {

                threadId = chatResponse.result.threads.last().id

            }


            TEST_THREAD_ID = threadId


            val requestThreadParticipant = RequestGetAdmin.Builder()
                .threadId(threadId)
                .count(50)
                .build()

            fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] =
                mainViewModel.getParticipant(requestThreadParticipant)


//            when (chatResponse.uniqueId) {
//
//
//                fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] -> {
//
//                    val requestThreadParticipant = RequestGetAdmin.Builder()
//                        .threadId(threadId)
//                        .count(50)
//                        .build()
//
//                    fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] =
//                        mainViewModel.getParticipant(requestThreadParticipant)
//
//                }
//                fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] -> {
//
//
//                    val requestGetAdmin = RequestGetAdmin.Builder()
//                        .admin(true)
//                        .threadId(threadId)
//                        .count(50)
//                        .build()
//
//                    fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] =
//                        mainViewModel.getAdminList(requestGetAdmin)
//
//
//                }
//
//
//            }


//
//                Toast
//                    .makeText(activity,"No Group found!",Toast.LENGTH_LONG)
//                    .show()
//
//
//                changeIconReceive(23)
//
//
//


        }
    }

    private fun prepareRemoveAdmin(chatResponse: ChatResponse<ResultThreads>?) {

        if (chatResponse?.result?.threads?.size!! > 0) {

            fucCallback.remove(ConstantMsgType.REMOVE_ADMIN_ROLES)

            var threadId = 0L

            for (thread: Thread in chatResponse.result.threads) {

                if (thread.isGroup) {

                    threadId = thread.id

                    break


                }

            }


            //todo find correct threadId
            if (threadId == 0L) {

                threadId = chatResponse.result.threads[0].id

            }


            TEST_THREAD_ID = threadId

            val requestGetAdmin = RequestGetAdmin.Builder()
                .admin(true)
                .threadId(threadId)
                .count(50)
                .build()


            fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] =
                mainViewModel.getAdminList(requestGetAdmin)

//
//                Toast
//                    .makeText(activity,"No Group found!",Toast.LENGTH_LONG)
//                    .show()
//
//
//                changeIconReceive(23)
//
//
//


        }
    }

    private fun prepareClearHistory(chatResponse: ChatResponse<ResultThreads>?) {
        if (chatResponse?.result?.threads?.size!! > 0) {

            fucCallback.remove(ConstantMsgType.CLEAR_HISTORY)


            var threadId = 0L

            for (thread: Thread in chatResponse.result.threads) {

                try {
                    if (thread.lastMessage != null) {

                        threadId = thread.id

                        break

                    }
                } catch (e: Exception) {
                    Log.d("MTAG", e.message)
                    continue
                }

            }


            if (threadId == 0L)
                threadId = chatResponse.result.threads[0].id

            val requestClearHistory = RequestClearHistory
                .Builder(threadId)
                .build()

            fucCallback[ConstantMsgType.CLEAR_HISTORY] =
                mainViewModel.clearHistory(requestClearHistory)


        }
    }

    override fun onDeleteMessage(response: ChatResponse<ResultDeleteMessage>?) {
        super.onDeleteMessage(response)
        if (fucCallback[ConstantMsgType.DELETE_MESSAGE] == response?.uniqueId) {
            val position = 17
            changeIconReceive(position)
            methods[position].methodNameFlag = true
        }

        if (fucCallbacks[ConstantMsgType.DELETE_MULTIPLE_MESSAGE]?.contains(response?.uniqueId)!!) {


            handleDeleteMessage(response)


        }
    }

    private fun handleDeleteMessage(response: ChatResponse<ResultDeleteMessage>?) {


        fucCallbacks[ConstantMsgType.DELETE_MULTIPLE_MESSAGE]!!.remove(response?.uniqueId)

        if (fucCallbacks[ConstantMsgType.DELETE_MULTIPLE_MESSAGE]!!.size == 0) {

            val position = 26
            changeIconReceive(position)
            methods[position].methodNameFlag = true

        }


    }

    override fun onEditedMessage(response: ChatResponse<ResultNewMessage>?) {
        super.onEditedMessage(response)
        if (fucCallback[ConstantMsgType.EDIT_MESSAGE] == response?.uniqueId) {
            val position = 18
            changeIconReceive(position)
            methods[position].methodNameFlag = true
        }
    }

    override fun onSent(response: ChatResponse<ResultMessage>?) {
        super.onSent(response)

        //TODO create Thread with forward message should changed

        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG_ID] == response?.uniqueId) {
            val forwardMsgId = response?.result?.messageId
            val contactId = fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG_CONTCT_ID]

            val inviteList = ArrayList<Invitee>()
            inviteList.add(Invitee(contactId!!.toLong(), 1))
            val forwList: MutableList<Long> = mutableListOf()
            forwList.add(forwardMsgId!!)
            val requestThreadInnerMessage = RequestThreadInnerMessage.Builder().message(faker.music().genre())
                .forwardedMessageIds(forwList).build()
            val requestCreateThread: RequestCreateThread =
                RequestCreateThread.Builder(0, inviteList)
                    .message(requestThreadInnerMessage)
                    .build()
            val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
            fucCallback[ConstantMsgType.DELETE_MESSAGE_ID] = uniqueId!![1]

        }

        if (fucCallback[ConstantMsgType.EDIT_MESSAGE_ID] == response?.uniqueId) {
            val requestEditMessage = RequestEditMessage.Builder("this is edit ", response!!.result.messageId).build()
            fucCallback[ConstantMsgType.EDIT_MESSAGE] = mainViewModel.editMessage(requestEditMessage)
        }

        if (fucCallback[ConstantMsgType.DELETE_MESSAGE_ID] == response?.uniqueId) {
//            val requestDeleteMessage = RequestDeleteMessage.Builder(response!!.result.messageId).build()
//            fucCallback[ConstantMsgType.DELETE_MESSAGE] = mainViewModel.deleteMessage(requestDeleteMessage)
        }

        if (fucCallback[ConstantMsgType.REPLY_MESSAGE] == response?.uniqueId) {
            val position = 13
            changeIconReceive(position)
            methods[position].methodNameFlag = true
        }

        if (fucCallback[ConstantMsgType.REPLY_MESSAGE_ID] == response?.uniqueId) {
            val messageId = response?.result?.messageId
            val threadId = fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID]
            val replyMessage =
                RequestReplyMessage.Builder("this is replyMessage", threadId?.toLong()!!, messageId!!).build()
            fucCallback[ConstantMsgType.REPLY_MESSAGE] = mainViewModel.replyMessage(replyMessage)
            val position = 13
            changeIconSend(position)
        }

        if (fucCallback[ConstantMsgType.FORWARD_MESSAGE] == response?.uniqueId) {
            val position = 12
            changeSecondIconReceive(position)
            methods[position].funcOneFlag = true
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_MSG_MESSAGE] == response?.uniqueId) {
            val position = 27
            changeSecondIconReceive(position)
            methods[position].funcOneFlag = true
        }




        if (fucCallback[ConstantMsgType.FORWARD_MESSAGE_ID] == response?.uniqueId) {

            val messageIds = ArrayList<Long>()
            messageIds.add(response?.result?.messageId!!)

            val threadId = fucCallback[ConstantMsgType.FORWARD_MESSAGE_THREAD_ID]
            val requestForwardMessage = RequestForwardMessage.Builder(threadId!!.toLong(), messageIds).build()
            fucCallback[ConstantMsgType.FORWARD_MESSAGE] = mainViewModel.forwardMessage(requestForwardMessage)[0]
            val position = 12
            changeIconSend(position)
        }

        if (fucCallback[ConstantMsgType.SEND_MESSAGE] == response?.uniqueId) {
            val position = 8
            changeSecondIconReceive(position)
            methods[position].funcOneFlag = true
        }
    }

    override fun onSeen(response: ChatResponse<ResultMessage>?) {
        super.onSeen(response)
        val position = 8
        changeFourthIconReceive(position)
        methods[position].funcFourFlag = true
    }

    override fun onDeliver(response: ChatResponse<ResultMessage>?) {
        super.onDeliver(response)
        val position = 8
        changeThirdIconReceive(position)
        methods[position].funcThreeFlag = true
    }

    override fun onLeaveThread(response: ChatResponse<ResultLeaveThread>?) {
        super.onLeaveThread(response)
        val position = 14
        changeIconReceive(position)
        methods[position].methodNameFlag = true
    }

    override fun onUpdateContact(response: ChatResponse<ResultUpdateContact>?) {
        super.onUpdateContact(response)
        if (fucCallback[ConstantMsgType.UPDATE_CONTACT] == response?.uniqueId) {
            val position = 7
            changeIconReceive(position)
            methods[position].methodNameFlag = true
        }
    }

    /**
     * OnBlock receive message
     * */
    override fun onBlock(chatResponse: ChatResponse<ResultBlock>?) {
        super.onBlock(chatResponse)
        if (fucCallback[ConstantMsgType.BLOCK_CONTACT] == chatResponse?.uniqueId) {
            val position = 2

            changeIconReceive(position)
            methods[position].methodNameFlag = true

            val id = chatResponse?.result?.contact?.id
            if (id != null) {
                val requestUnBlock = RequestUnBlock.Builder()
                    .contactId(id)
                    .build()
                mainViewModel.unBlock(requestUnBlock)
            }
        }
        if (fucCallback[ConstantMsgType.UNBLOCK_CONTACT] == chatResponse?.uniqueId) {
            fucCallback.remove(ConstantMsgType.UNBLOCK_CONTACT)
            val contactId = chatResponse?.result?.contact?.id
            if (contactId != null) {
                val requestUnBlock = RequestUnBlock.Builder()
                    .contactId(contactId)
                    .build()
                fucCallback[ConstantMsgType.UNBLOCK_CONTACT] = mainViewModel.unBlock(requestUnBlock)
            }
        }
    }

    override fun onAddContact(response: ChatResponse<ResultAddContact>?) {
        super.onAddContact(response)
        if (fucCallback[ConstantMsgType.ADD_CONTACT] == response?.uniqueId) {
            val position = 3
            changeIconReceive(position)
            methods[position].methodNameFlag = true

            var id = response?.result?.contact?.id
            if (id != null) {
                val requestRemoveContact = RequestRemoveContact.Builder(id).build()
                mainViewModel.removeContact(requestRemoveContact)
            }
        }

        if (fucCallback[ConstantMsgType.REMOVE_CONTACT] == response?.uniqueId) {
            var id = response?.result?.contact?.id
            if (id != null) {
                fucCallback.remove(ConstantMsgType.REMOVE_CONTACT)
                val requestRemoveContact = RequestRemoveContact.Builder(id).build()
                fucCallback[ConstantMsgType.REMOVE_CONTACT] = mainViewModel.removeContact(requestRemoveContact)
                val position = 9
                changeIconSend(position)
            }
        }
    }

    override fun onMuteThread(response: ChatResponse<ResultMute>?) {
        super.onMuteThread(response)
        if (fucCallback[ConstantMsgType.MUTE_THREAD] == response?.uniqueId) {
            val position = 15
            changeIconReceive(position)
            methods[position].methodNameFlag = true
        }
    }

    override fun onUnmuteThread(response: ChatResponse<ResultMute>?) {
        super.onUnmuteThread(response)
        if (fucCallback[ConstantMsgType.UNMUTE_THREAD] == response?.uniqueId) {
            val position = 16
            changeIconReceive(position)
            methods[position].methodNameFlag = true
        }
    }

    override fun onGetHistory(response: ChatResponse<ResultHistory>?) {
        super.onGetHistory(response)


        if (fucCallback[ConstantMsgType.GET_HISTORY] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.GET_HISTORY)
            val position = 19
            changeIconReceive(position)
        }

        if (fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] == response?.uniqueId) {

            requestDeleteMessage(response)

        }


    }

    private fun requestDeleteMessage(response: ChatResponse<ResultHistory>?) {


        var deleteListIds = ArrayList<Long>()

        var counter = 0

        for (message in response?.result!!.history) {

            if (counter > 2) {


                break
            }

            if (message.isDeletable) {


                deleteListIds.add(message.id)

                counter++

            }


        }



        if (deleteListIds.isEmpty()) {

            Toast.makeText(
                activity?.applicationContext, "There is no deletable message! Create a thread with message first",
                Toast.LENGTH_LONG
            )
                .show()

            changeIconReceive(26)

            return

        }


        var requestDeleteMessage = RequestDeleteMessage
            .Builder()
            .messageIds(deleteListIds)
            .deleteForAll(true)
            .build()


        var uniqueIds: ArrayList<String> = mainViewModel.deleteMultipleMessage(requestDeleteMessage)


        fucCallbacks[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] = uniqueIds


    }

    override fun onGetThreadParticipant(response: ChatResponse<ResultParticipant>?) {
        super.onGetThreadParticipant(response)



        if (fucCallback[ConstantMsgType.GET_PARTICIPANT] == response?.uniqueId) {

            fucCallback.remove(ConstantMsgType.GET_PARTICIPANT)
            val position = 21
            changeIconReceive(position)


        }

        if (fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] == response?.uniqueId) {


            prepareAddAdminRoles(response, fucCallback[ConstantMsgType.ADD_ADMIN_ROLES])

        }


    }


    /**
     *
     * Remove admin roles
     */

//    private fun prepareSetRole(output: OutPutParticipant?, uniqueId: String?) {
//
//
//        var parId = 0L
//
//
//        for (par in output?.result!!.participants) {
//
//            if (!par.admin) {
//
//                parId = par.id
//
//                break
//            }
//
//        }
//
//        if (parId == 0L)
//            parId = output.result!!.participants.last().id
//
//        val roles = ArrayList<String>()
//
//        roles.add(RoleType.Constants.THREAD_ADMIN)
//        roles.add(RoleType.Constants.ADD_NEW_USER)
//        roles.add(RoleType.Constants.REMOVE_USER)
//
//        val requestRole = RequestRole()
//
//        requestRole.id = parId
//
//        requestRole.roleTypes = roles
//
//
//        when (uniqueId) {
//
//            fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] -> {
//
//                requestRole.roleOperation = "add"
//
//            }
//
//            fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] -> {
//
//                requestRole.roleOperation = "remove"
//            }
//
//        }
//
//
//        val requestRoles = ArrayList<RequestRole>()
//
//        requestRoles.add(requestRole)
//
//
//        val addAdmin = RequestAddAdmin
//            .Builder(output.subjectId, requestRoles)
//            .build()
//
//
//        if (fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] == uniqueId) {
//
//            fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] = mainViewModel.addAdmin(addAdmin)
//
//        }
//
//        if (fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] == uniqueId) {
//
//            fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] = mainViewModel.removeAdminRoles(addAdmin)
//
//        }
//
//
//    }
//
//    /**
//     *
//     * Add admin roles
//     *
//     */
//
//    private fun prepareSetRole(output: ChatResponse<ResultParticipant>?, uniqueId: String?) {
//
//
////        fucCallback.remove(uniqueId)
//
//
////        val participant = output?.result!!.participants[0]
//        var parId = 0L
//
////todo separate add and remove
//
//
//        for (par in output?.result!!.participants) {
//
//            if (!par.admin) {
//
//                parId = par.id
//
//                break
//            }
//
//        }
//
//        if (parId == 0L)
//            parId = output.result!!.participants.last().id
//
//        val roles = ArrayList<String>()
//
//        roles.add(RoleType.Constants.THREAD_ADMIN)
//        roles.add(RoleType.Constants.ADD_NEW_USER)
//        roles.add(RoleType.Constants.REMOVE_USER)
//
//        val requestRole = RequestRole()
//
//        requestRole.id = parId
//
//        requestRole.roleTypes = roles
//
//
//        when (uniqueId) {
//
//            fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] -> {
//
//                requestRole.roleOperation = "add"
//
//            }
//
//            fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] -> {
//
//                requestRole.roleOperation = "remove"
//            }
//
//        }
//
//
//        val requestRoles = ArrayList<RequestRole>()
//
//        requestRoles.add(requestRole)
//
//
//        val addAdmin = RequestAddAdmin
//            .Builder(output.subjectId, requestRoles)
//            .build()
//
//
//        if (fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] == uniqueId) {
//
//            fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] = mainViewModel.addAdmin(addAdmin)
//
//        }
//
//        if (fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] == uniqueId) {
//
//            fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] = mainViewModel.removeAdminRoles(addAdmin)
//
//        }
//
//
//    }
//
//
    private fun prepareAddAdminRoles(output: ChatResponse<ResultParticipant>?, uniqueId: String?) {


        var parId = 0L

        for (par in output?.result!!.participants) {

            if (!par.admin) {

                parId = par.id

                break
            }

        }



        if (parId == 0L)
            parId = output.result!!.participants.last().id


        val roles = ArrayList<String>()

        roles.add(RoleType.Constants.THREAD_ADMIN)
        roles.add(RoleType.Constants.ADD_NEW_USER)
        roles.add(RoleType.Constants.REMOVE_USER)

        val requestRole = RequestRole()

        requestRole.id = parId

        requestRole.roleTypes = roles

        requestRole.roleOperation = "add"


        val requestRoles = ArrayList<RequestRole>()

        requestRoles.add(requestRole)


        val addAdmin = RequestAddAdmin
            .Builder(output.subjectId, requestRoles)
            .build()



        fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] = mainViewModel.addAdmin(addAdmin)


    }

    private fun prepareRemoveAdminRoles(chatResponse: ChatResponse<ResultParticipant>?, uniqueId: String?) {


        var adminParticipant = Participant()

        for (par in chatResponse?.result!!.participants) {

            if (par.admin) {

                adminParticipant = par

                break
            }

        }


        if (adminParticipant.equals(null)) {
            Toast.makeText(context, "There is no thread with admin! create one first", Toast.LENGTH_LONG)
                .show()
            return
        }

        val removableRole = adminParticipant.roles

        val requestRole = RequestRole()

        requestRole.id = adminParticipant.id

        requestRole.roleTypes = ArrayList(removableRole)

        requestRole.roleOperation = "remove"


        val requestRoles = ArrayList<RequestRole>()

        requestRoles.add(requestRole)

        val addAdmin = RequestAddAdmin
            .Builder(chatResponse.subjectId, requestRoles)
            .build()

        fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] = mainViewModel.removeAdminRoles(addAdmin)


    }


    override fun onNewMessage(response: ChatResponse<ResultNewMessage>?) {
        super.onNewMessage(response)

        val uniqueId = response?.uniqueId

        if (uniqueId == fucCallback[ConstantMsgType.GET_SEEN_LIST]) {

            prepareGetSeenList(response)

        }

        if (uniqueId == fucCallback[ConstantMsgType.GET_DELIVER_LIST]) {

            prepareGetDeliveryList(response)


        }


    }

    private fun prepareGetDeliveryList(response: ChatResponse<ResultNewMessage>?) {


        val targetMessageId = response?.result?.messageVO?.id


        val request = RequestDeliveredMessageList.Builder(targetMessageId!!).build()

        fucCallback[ConstantMsgType.GET_DELIVER_LIST] =
            mainViewModel.getDeliverMessageList(request)


    }

    private fun prepareGetSeenList(response: ChatResponse<ResultNewMessage>?) {

        val targetMessageId = response?.result?.messageVO?.id


        val request = RequestSeenMessageList.Builder(targetMessageId!!).build()

        fucCallback[ConstantMsgType.GET_SEEN_LIST] =
            mainViewModel.getSeenMessageList(request)


    }


    override fun onGetSeenMessageList(response: ChatResponse<ResultParticipant>?) {
        super.onGetSeenMessageList(response)


        if (response?.uniqueId == fucCallback[ConstantMsgType.GET_SEEN_LIST]) {

            changeIconReceive(29)

            methods[29].methodNameFlag = true


        }


    }

    override fun onGetDeliverMessageList(response: ChatResponse<ResultParticipant>?) {
        super.onGetDeliverMessageList(response)


        if (response?.uniqueId == fucCallback[ConstantMsgType.GET_DELIVER_LIST]) {

            changeIconReceive(28)

            methods[28].methodNameFlag = true


        }

    }

    override fun onCreateThread(response: ChatResponse<ResultThread>?) {
        super.onCreateThread(response)

        if (fucCallback[ConstantMsgType.GET_PARTICIPANT] == response?.uniqueId) {
            val threadId = response!!.result.thread.id
            val requestThreadParticipant = RequestThreadParticipant.Builder(threadId).build()
            mainViewModel.getParticipant(requestThreadParticipant)
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] == response?.uniqueId) {
            val threadId = response!!.result.thread.id
            val requestMessage =
                RequestMessage.Builder("this is message for create thread with forward message", threadId)
                    .build()
            fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG_ID] = mainViewModel.sendTextMsg(requestMessage)
        }


        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_MSG] == response?.uniqueId) {

            fucCallback.remove(ConstantMsgType.CREATE_THREAD_WITH_MSG)

            changeIconReceive(27)

            methods[27].methodNameFlag = true
        }




        if (fucCallback[ConstantMsgType.GET_HISTORY] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.GET_HISTORY)
            val threadId = response!!.result.thread.id
            val requestGetHistory = RequestGetHistory.Builder(threadId).build()
            fucCallback[ConstantMsgType.GET_HISTORY] = mainViewModel.getHistory(requestGetHistory)
        }

        if (fucCallback[ConstantMsgType.UNMUTE_THREAD] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.UNMUTE_THREAD)
            val threadId = response!!.result.thread.id
            val requestMuteThread = RequestMuteThread.Builder(threadId).build()
            fucCallback[ConstantMsgType.UNMUTE_THREAD] = mainViewModel.unMuteThread(requestMuteThread)
        }

        if (fucCallback[ConstantMsgType.MUTE_THREAD] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.MUTE_THREAD)
            val threadId = response!!.result.thread.id
            val requestMuteThread = RequestMuteThread.Builder(threadId).build()
            fucCallback[ConstantMsgType.MUTE_THREAD] = mainViewModel.muteThread(requestMuteThread)
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD] == response?.uniqueId) {
            val position = 0
            changeIconReceive(position)
            methods[position].methodNameFlag = true
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_CHANNEL] == response?.uniqueId) {
            val position = 0
            changeSecondIconReceive(position)
            methods[position].funcOneFlag = true
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_CHANNEL_GROUP] == response?.uniqueId) {
            val position = 0
            changeThirdIconReceive(position)
            methods[position].funcTwoFlag = true
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_PUBLIC_GROUP] == response?.uniqueId) {
            val position = 0
            changeFourthIconReceive(position)
            methods[position].funcThreeFlag = true
        }

        if (fucCallback[ConstantMsgType.LEAVE_THREAD] == response?.uniqueId) {
            val threadId = response?.result?.thread?.id
            val requeLeaveThread = RequestLeaveThread.Builder(threadId!!.toLong()).build()
            fucCallback[ConstantMsgType.LEAVE_THREAD] = mainViewModel.leaveThread(requeLeaveThread)
            val position = 14
            changeIconSend(position)
        }

        if (fucCallback[ConstantMsgType.FORWARD_MESSAGE] == response?.uniqueId) {
            val threadId = response?.result?.thread?.id
            fucCallback[ConstantMsgType.FORWARD_MESSAGE_THREAD_ID] = threadId.toString()
        }
        if (fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.REPLY_MESSAGE_THREAD_ID)
            val threadId = response?.result?.thread?.id
            fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID] = threadId.toString()
        }

        if (fucCallback[ConstantMsgType.ADD_PARTICIPANT] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.ADD_PARTICIPANT)
            var participantId = fucCallback["ADD_PARTICIPANT_ID"]
            val partId = participantId?.toLong()
            val threadId = response?.result?.thread?.id
            if (partId != null && threadId != null) {
                val contactIdList: MutableList<Long> = mutableListOf()
                contactIdList.add(partId)
                val requestAddParticipants = RequestAddParticipants.Builder(threadId, contactIdList).build()
                mainViewModel.addParticipant(requestAddParticipants)
                fucCallback.remove("ADD_PARTICIPANT_ID")
            }
        }
    }

    private fun connect() {

        avLoadingIndicatorView.visibility = View.VISIBLE

        if (sandbox) {

            //sandBox
            mainViewModel.connect(
                sand_socketAddress,
                sand_appId,
                sand_serverName,
                SAND_TOKEN,
                sand_ssoHost,
                sand_platformHost,
                sand_fileServer,
                typeCode
            )
        } else {

            //Local
//            mainViewModel.connect(
//                BuildConfig.SOCKET_ADDRESS, BuildConfig.APP_ID, BuildConfig.SERVER_NAME
//                , TOKEN, BuildConfig.SSO_HOST, BuildConfig.PLATFORM_HOST, BuildConfig.FILE_SERVER, null
//            )
            mainViewModel.connect(
                socketAddress,
                sand_appId,
                serverName,
                TOKEN,
                ssoHost,
                platformHost,
                fileServer,
                typeCode
            )
        }
    }

    private fun changeFourthIconReceive(position: Int) {
        activity?.runOnUiThread {
            if (view != null) {
                val viewHolder: RecyclerView.ViewHolder = recyclerView.findViewHolderForAdapterPosition(position)!!
                viewHolder.itemView.findViewById<AppCompatImageView>(R.id.imageView_tickFourth)
                    .setImageResource(R.drawable.ic_round_done_all_24px)

                viewHolder.itemView.findViewById<AppCompatImageView>(R.id.imageView_tickFourth)
                    .setColorFilter(ContextCompat.getColor(activity!!, R.color.colorPrimary))
            }
        }
    }

    private fun changeThirdIconReceive(position: Int) {
        activity?.runOnUiThread {
            if (view != null) {
                val viewHolder: RecyclerView.ViewHolder = recyclerView.findViewHolderForAdapterPosition(position)!!
                viewHolder.itemView.findViewById<AppCompatImageView>(R.id.imageView_tickThird)
                    .setImageResource(R.drawable.ic_round_done_all_24px)

                viewHolder.itemView.findViewById<AppCompatImageView>(R.id.imageView_tickThird)
                    .setColorFilter(ContextCompat.getColor(activity!!, R.color.colorPrimary))
            }
        }
    }


    private fun changeSecondIconReceive(position: Int) {
        activity?.runOnUiThread {
            if (view != null) {
                val viewHolder: RecyclerView.ViewHolder = recyclerView.findViewHolderForAdapterPosition(position)!!
                viewHolder.itemView.findViewById<AppCompatImageView>(R.id.imageView_tickFirst)
                    .setImageResource(R.drawable.ic_round_done_all_24px)

                viewHolder.itemView.findViewById<AppCompatImageView>(R.id.imageView_tickFirst)
                    .setColorFilter(ContextCompat.getColor(activity!!, R.color.colorPrimary))
            }
        }
    }

    override fun onRemoveContact(response: ChatResponse<ResultRemoveContact>?) {
        super.onRemoveContact(response)
        if (fucCallback[ConstantMsgType.REMOVE_CONTACT] == response?.uniqueId) {
            val position = 9
            changeIconReceive(position)
        }
    }

    override fun onGetContact(response: ChatResponse<ResultContact>?) {
        super.onGetContact(response)

        val contactList = response?.result?.contacts

        if (fucCallback[ConstantMsgType.GET_PARTICIPANT] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.GET_PARTICIPANT)
            handleGetParticipant(contactList)
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] == response?.uniqueId) {
            handleCrtThreadForwMsg(contactList)
        }
        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_MSG] == response?.uniqueId) {
            prepareCreateThreadWithMsg(contactList)
        }

        if (fucCallback[GET_HISTORY] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.GET_HISTORY)
            handleGetHistory(contactList)
        }

        if (fucCallback[ConstantMsgType.EDIT_MESSAGE] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.DELETE_MESSAGE)
            handleEditMessage(contactList)
        }

        if (fucCallback[ConstantMsgType.DELETE_MESSAGE] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.DELETE_MESSAGE)
            handleDeleteMessage(contactList)
        }

        if (fucCallback[ConstantMsgType.UNMUTE_THREAD] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.UNMUTE_THREAD)
            handleUnmuteThread(contactList)
        }

        if (fucCallback[ConstantMsgType.MUTE_THREAD] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.MUTE_THREAD)
            handleMuteThread(contactList)
        }

        if (fucCallback[ConstantMsgType.LEAVE_THREAD] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.LEAVE_THREAD)
            handleLeaveThread(contactList)
        }

        if (fucCallback[ConstantMsgType.REPLY_MESSAGE] == response?.uniqueId) {
            handleReplyMessage(contactList)
        }

        if (fucCallback[ConstantMsgType.SEND_MESSAGE] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.SEND_MESSAGE)
            handleSendMessageResponse(contactList)
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.CREATE_THREAD)
            handleGetThreadResponse(contactList)
        }
        if (fucCallback[ConstantMsgType.GET_CONTACT] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.GET_CONTACT)
            val position = 1
            changeIconReceive(position)
            methods[position].methodNameFlag = true
            var json = gson.toJson(response?.result)
            methods[position].log = json
        }

        if (fucCallback[ConstantMsgType.BLOCK_CONTACT] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.BLOCK_CONTACT)
            handleBlockContact(contactList)
        }

        if (fucCallback[ConstantMsgType.UPDATE_CONTACT] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.UPDATE_CONTACT)
            handleUpdateContact(contactList)
        }

        if (fucCallback[ConstantMsgType.UNBLOCK_CONTACT] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.UNBLOCK_CONTACT)
            handleUnBlockContact(contactList)
        }
        if (fucCallback[ConstantMsgType.ADD_PARTICIPANT] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.ADD_PARTICIPANT)
            handleAddParticipant(contactList)
        }
        if (fucCallback[ConstantMsgType.REMOVE_PARTICIPANT] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.REMOVE_PARTICIPANT)
            handleRemoveParticipant(contactList)
        }
        if (fucCallback[ConstantMsgType.FORWARD_MESSAGE] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.FORWARD_MESSAGE)
            handleForward(contactList)
            // CREATE WITH MSSAGE CONTACT
            // STORE THE MESSAGE ID
            //
        }
    }

    private fun handleGetParticipant(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    if (choose == 2) {
                        val contactId = contact.id

                        val inviteList = ArrayList<Invitee>()
                        inviteList.add(Invitee(contactId, 1))

                        val list = Array(1) { Invitee(inviteList[0].id, 2) }

                        val uniqueId = mainViewModel.createThread(
                            ThreadType.Constants.NORMAL, list, "nothing", ""
                            , "", ""
                        )

                        fucCallback[ConstantMsgType.GET_PARTICIPANT] = uniqueId
                        break
                    }
                    choose++
                }
            }
        }
    }

    /**
     * Its created thread and stored another contact id (because its needed tha contact id in order to
     * create another thread) Its sent message(its used as forward message id)/
     * */
    private fun handleCrtThreadForwMsg(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    if (choose == 2) {
                        val contactId = contact.id

                        val inviteList = ArrayList<Invitee>()
                        inviteList.add(Invitee(contactId, 1))

                        val list = Array(1) { Invitee(inviteList[0].id, 2) }

                        val uniqueId = mainViewModel.createThread(
                            ThreadType.Constants.NORMAL, list, "nothing", ""
                            , "", ""
                        )

                        fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] = uniqueId
                        break
                    }
                    if (choose == 1) {
                        fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG_CONTCT_ID] = contact.id.toString()
                    }
                    choose++
                }
            }
        }
    }


    private fun prepareCreateThreadWithMsg(contactList: ArrayList<Contact>?) {

/*available invitee types*/
        /*int TO_BE_USER_SSO_ID = 1;
         *int TO_BE_USER_CONTACT_ID = 2;
         *int TO_BE_USER_CELLPHONE_NUMBER = 3;
         *int TO_BE_USER_USERNAME = 4;
         *TO_BE_USER_ID = 5  // just for p2p
         */


        if (contactList != null) {

            var contactId = 0L


            for (contact: Contact in contactList) {
                if (contact.isHasUser) {


                    contactId = contact.id

                    break

                }
            }


            var invList = ArrayList<Invitee>()


            if (contactId != 0L) {

                invList.add(Invitee(contactId, InviteType.Constants.TO_BE_USER_CONTACT_ID))

            } else {

                contactId = contactList[0].id

                invList.add(Invitee(121, InviteType.Constants.TO_BE_USER_SSO_ID))

            }


            val message = RequestThreadInnerMessage
                .Builder()
                .message("Test create thread with message at: " + Date().toString())
//                .forwardedMessageIds(listForwardIds)
                .build()


            val requestCreateThread = RequestCreateThread
                .Builder(ThreadType.Constants.NORMAL, invList)
                .message(message)
                .build()


            val uniqueIds = mainViewModel.createThreadWithMessage(requestCreateThread)

            fucCallback[ConstantMsgType.CREATE_THREAD_WITH_MSG] = uniqueIds!![0]

            fucCallback[ConstantMsgType.CREATE_THREAD_WITH_MSG_MESSAGE] = uniqueIds[1]


        }


    }

    private fun handleGetHistory(contactList: ArrayList<Contact>?) {

        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    if (choose == 1) {
                        val contactId = contact.id
                        val userId = contact.userId
                        val inviteList = Array<Invitee>(1) { Invitee(contactId, 2) }
                        inviteList[0].id = contactId

                        val uniqueId = mainViewModel.createThread(
                            ThreadType.Constants.NORMAL, inviteList, "", ""
                            , "", ""
                        )
                        fucCallback[ConstantMsgType.GET_HISTORY] = uniqueId
                        break
                    }
                    choose++
                }
            }
        }
    }

    private fun handleEditMessage(contactList: ArrayList<Contact>?) {

        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    choose++
                    if (choose == 1) {
                        val contactId = contact.id

                        val inviteList = ArrayList<Invitee>()
                        inviteList.add(Invitee(contactId, 1))
                        val requestThreadInnerMessage =
                            RequestThreadInnerMessage.Builder().message(faker.music().genre()).build()
                        val requestCreateThread: RequestCreateThread =
                            RequestCreateThread.Builder(0, inviteList)
                                .message(requestThreadInnerMessage)
                                .build()
                        val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
                        fucCallback[ConstantMsgType.DELETE_MESSAGE_ID] = uniqueId!![1]
                    }
                    break
                }
            }
        }
    }

    private fun handleDeleteMessage(contactList: ArrayList<Contact>?) {

        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    choose++
                    if (choose == 1) {
                        val contactId = contact.id

                        val inviteList = ArrayList<Invitee>()
                        inviteList.add(Invitee(contactId, 1))
                        val requestThreadInnerMessage =
                            RequestThreadInnerMessage.Builder().message(faker.music().genre()).build()
                        val requestCreateThread: RequestCreateThread =
                            RequestCreateThread.Builder(0, inviteList)
                                .message(requestThreadInnerMessage)
                                .build()
                        val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
                        fucCallback[ConstantMsgType.DELETE_MESSAGE_ID] = uniqueId!![1]
                    }
                    break
                }
            }
        }
    }

    private fun handleUnmuteThread(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    choose++
                    if (choose == 2) {
                        val contactId = contact.id
                        val userId = contact.userId
                        val inviteList = Array<Invitee>(1, { i -> Invitee(contactId, 5) })
                        inviteList[0].id = contactId

                        val uniqueId = mainViewModel.createThread(
                            ThreadType.Constants.NORMAL, inviteList, "", ""
                            , "", ""
                        )
                        fucCallback[ConstantMsgType.UNMUTE_THREAD] = uniqueId
                        break
                    }
                }
            }
        }
    }

    private fun handleMuteThread(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    choose++
                    if (choose == 2) {
                        val contactId = contact.id

                        val inviteList = Array<Invitee>(1, { i -> Invitee(contactId, 2) })
                        inviteList[0].id = contactId

                        val uniqueId = mainViewModel.createThread(
                            ThreadType.Constants.NORMAL, inviteList, "", ""
                            , "", ""
                        )
                        fucCallback[ConstantMsgType.MUTE_THREAD] = uniqueId
                        break
                    }
                }
            }
        }
    }

    private fun handleLeaveThread(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 1))
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage.Builder().message(faker.music().genre()).build()
                    val requestCreateThread: RequestCreateThread =
                        RequestCreateThread.Builder(0, inviteList)
                            .message(requestThreadInnerMessage)
                            .build()
                    val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
                    if (uniqueId?.get(0) != null) {
                        fucCallback[ConstantMsgType.LEAVE_THREAD] = uniqueId[0]
                    }
                    break
                }
            }
        }
    }

    private fun handleReplyMessage(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 1))
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage.Builder().message(faker.music().genre()).build()
                    val requestCreateThread: RequestCreateThread =
                        RequestCreateThread.Builder(0, inviteList)
                            .message(requestThreadInnerMessage)
                            .build()
                    val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
                    fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID] = uniqueId!![0]
                    fucCallback[ConstantMsgType.REPLY_MESSAGE_ID] = uniqueId[1]
                    break
                }
            }
        }
    }

    override fun onThreadAddParticipant(response: ChatResponse<ResultAddParticipant>?) {
        super.onThreadAddParticipant(response)

        val position = 10
        changeIconReceive(position)
    }

    private fun handleForward(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 1))
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage.Builder().message(faker.music().genre()).build()
                    val requestCreateThread: RequestCreateThread =
                        RequestCreateThread.Builder(0, inviteList)
                            .message(requestThreadInnerMessage)
                            .build()
                    val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
                    fucCallback[ConstantMsgType.FORWARD_MESSAGE] = uniqueId!![0]
                    fucCallback[ConstantMsgType.FORWARD_MESSAGE_ID] = uniqueId[1]
                    break
                }
            }
        }
    }

    private fun handleAddParticipant(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val inviteList = Array<Invitee>(1) { i -> Invitee(contactId, 2) }
                    inviteList[0].id = contactId

                    val uniqueId = mainViewModel.createThread(
                        ThreadType.Constants.PUBLIC_GROUP, inviteList, "nothing", ""
                        , "", ""
                    )
                    fucCallback[ConstantMsgType.ADD_PARTICIPANT] = uniqueId
                    choose++
                    if (choose == 2) {
                        fucCallback["ADD_PARTICIPANT_ID"] = contactId.toString()
                        break
                    }
                }
            }
        }
    }

    private fun removeContact() {
//        val
        val requestAddContact = RequestAddContact.Builder()
            .firstName(faker.name().firstName())
            .lastName(faker.name().lastName())
            .email(faker.lordOfTheRings().character() + "@Gmail.com")
            .cellphoneNumber(faker.phoneNumber().cellPhone())
            .build()
        fucCallback[ConstantMsgType.REMOVE_CONTACT] = mainViewModel.addContacts(requestAddContact)

    }

    private fun handleSendMessageResponse(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 1))
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage.Builder().message(faker.music().genre()).build()
                    val requestCreateThread: RequestCreateThread =
                        RequestCreateThread.Builder(0, inviteList)
                            .message(requestThreadInnerMessage)
                            .build()
                    val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
                    fucCallback[ConstantMsgType.SEND_MESSAGE] = uniqueId!![0]
                    break
                }
            }
        }
    }

    //Response from getContact
    /**
     *
     * */
    private fun handleUnBlockContact(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val requestBlock = RequestBlock.Builder()
                        .contactId(contactId)
                        .build()
                    val uniqueId = mainViewModel.blockContact(requestBlock)
                    fucCallback[ConstantMsgType.UNBLOCK_CONTACT] = uniqueId

                    break
                }
            }
        }
    }

    private fun handleUpdateContact(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactid = contact.id
                    val cellPhoneNumber = contact.cellphoneNumber
                    val firstName = contact.firstName
                    val lastName = contact.lastName
                    val email = contact.email

                    val requestUpdateContact = RequestUpdateContact.Builder(contactid)
                        .cellphoneNumber(cellPhoneNumber)
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .build()

                    val uniqueId = mainViewModel.updateContact(requestUpdateContact)
                    fucCallback[ConstantMsgType.UPDATE_CONTACT] = uniqueId
                    changeIconSend(7)
                    break
                }
            }
        }
    }


    private fun handleBlockContact(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val requestBlock = RequestBlock.Builder()
                        .contactId(contactId)
                        .build()
                    val uniqueId = mainViewModel.blockContact(requestBlock)
                    fucCallback[ConstantMsgType.BLOCK_CONTACT] = uniqueId
                    changeIconSend(2)
                    break
                }
            }
        }
    }

    private fun getPartitipant() {
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.GET_PARTICIPANT] = mainViewModel.getContact(requestGetContact)
    }

    //Get Thread
    // If there is no Thread
    // Its create Thread with someone that has userId
    // Then send Message to that thread
    private fun sendTextMsg() {
        val requestThread = RequestThread.Builder().build()
        fucCallback[ConstantMsgType.SEND_MESSAGE] = mainViewModel.getThread(requestThread)

//        val requestMessage = RequestMessage.Builder()
//        mainViewModel.sendTextMsg()ConstantMsgType.UNBLOCK_CONTACT
    }


    private fun unMuteThread() {
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.UNMUTE_THREAD] = mainViewModel.getContact(requestGetContact)
        val position = 16
        changeIconSend(position)
//        val requestMuteThread = RequestMuteThread.Builder(threadId).build()
//        mainViewModel.unMuteThread(requestMuteThread)
    }

    private fun muteThread() {

        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.MUTE_THREAD] = mainViewModel.getContact(requestGetContact)
        val position = 15
        changeIconSend(position)
    }

    private fun leaveThread() {
        val requestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.LEAVE_THREAD] = mainViewModel.getContact(requestGetContact)
    }

    private fun removeParticipant() {

        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.REMOVE_PARTICIPANT] = mainViewModel.getContact(requestGetContact)
        val position = 11
        changeIconSend(position)
    }

    private fun blockContact() {

        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        val uniqueId = mainViewModel.getContact(requestGetContact)
        fucCallback[ConstantMsgType.BLOCK_CONTACT] = uniqueId
    }

    private fun addContact() {

        val requestAddContact = RequestAddContact.Builder()
            .cellphoneNumber(faker.phoneNumber()?.phoneNumber())
            .firstName(faker.name()?.firstName())
            .lastName(faker.name()?.lastName())
            .build()
        val uniqueId = mainViewModel.addContacts(requestAddContact)
        fucCallback[ConstantMsgType.ADD_CONTACT] = uniqueId
        val position = 3
        changeIconSend(position)
    }

    private fun getContact() {
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        val uniqueId = mainViewModel.getContact(requestGetContact)
        fucCallback[ConstantMsgType.GET_CONTACT] = uniqueId
        var position = 1
        changeIconSend(position)
    }

    private fun createThread() {
        //get contact
        // search for evey one that has user
        // create thread with that

        val requestGetContact: RequestGetContact = RequestGetContact
            .Builder()
            .build()

        val uniqueId = mainViewModel.getContact(requestGetContact)
        fucCallback[ConstantMsgType.CREATE_THREAD] = uniqueId
        changeIconSend(0)

    }


    private fun createThreadWithForwMessage() {

        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] = mainViewModel.getContact(requestGetContact)

    }

    private fun createThreadOwnerGroup(inviteList: ArrayList<Invitee>) {

        val list = Array<Invitee>(1) { Invitee(inviteList[0].id, 2) }

        val uniqueId = mainViewModel.createThread(
            ThreadType.Constants.OWNER_GROUP, list, "nothing", ""
            , "", ""
        )
        fucCallback[ConstantMsgType.CREATE_THREAD_OWNER_GROUP] = uniqueId
    }

    private fun createThreadPublicGroup(inviteList: ArrayList<Invitee>) {
        val list = Array<Invitee>(1) { Invitee(inviteList[0].id, 2) }

        val uniqueId = mainViewModel.createThread(
            ThreadType.Constants.PUBLIC_GROUP, list, "nothing", ""
            , "", ""
        )
        fucCallback[ConstantMsgType.CREATE_THREAD_PUBLIC_GROUP] = uniqueId
    }

    private fun createThreadChannelGroup(inviteList: ArrayList<Invitee>) {
        val list = Array<Invitee>(1, { i -> Invitee(inviteList[0].id, 2) })

        val uniqueId = mainViewModel.createThread(
            ThreadType.Constants.CHANNEL_GROUP, list, "nothing", ""
            , "", ""
        )
        fucCallback[ConstantMsgType.CREATE_THREAD_CHANNEL_GROUP] = uniqueId

    }

    private fun createThreadChannel(inviteList: ArrayList<Invitee>) {
        val list = Array<Invitee>(1, { i -> Invitee(inviteList[0].id, 2) })

        val uniqueId = mainViewModel.createThread(
            ThreadType.Constants.CHANNEL, list, "nothing", ""
            , "", ""
        )
        fucCallback[ConstantMsgType.CREATE_THREAD_CHANNEL] = uniqueId
    }


    private fun clearHistory() {

        changeIconSend(22)

        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        val uniqueId = mainViewModel.getThread(requestGetThreads)

        fucCallback[ConstantMsgType.CLEAR_HISTORY] = uniqueId


    }

    private fun createThreadWithMessage() {

        changeIconSend(27)

        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()

        fucCallback[ConstantMsgType.CREATE_THREAD_WITH_MSG] = mainViewModel.getContact(requestGetContact)


    }

    private fun deleteMultipleMessage() {

        changeIconSend(26)

        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        val uniqueId = mainViewModel.getThread(requestGetThreads)

        fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] = uniqueId


    }


    private fun removeAdminRoles() {


        changeIconSend(25)

        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        val uniqueId = mainViewModel.getThread(requestGetThreads)

        fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] = uniqueId


    }

    private fun addAdminRoles() {

        changeIconSend(24)

        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        val uniqueId = mainViewModel.getThread(requestGetThreads)

        fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] = uniqueId


    }


    private fun getAdminsList() {

        changeIconSend(23)

        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        val uniqueId = mainViewModel.getThread(requestGetThreads)

        fucCallback[ConstantMsgType.GET_ADMINS_LIST] = uniqueId


    }


    private fun handleRemoveParticipant(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id
                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 1))
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage.Builder().message(faker.music().genre()).build()
                    val requestCreateThread: RequestCreateThread =
                        RequestCreateThread.Builder(0, inviteList)
                            .message(requestThreadInnerMessage)
                            .build()
//                    val uniqueId = mainViewModel.createThread(requestCreateThread)
//                    fucCallback[ConstantMsgType.CREATE_THREAD] = uniqueId
                    break
                }
            }
        }
    }

    //handle getContact response to create thread
    //
    /**
     * Create the thread to p to p/channel/group. The list below is showing all of the threads type
     * int NORMAL = 0;
     * int OWNER_GROUP = 1;
     * int PUBLIC_GROUP = 2;
     * int CHANNEL_GROUP = 4;
     * int TO_BE_USER_ID = 5;
     * <p>
     * int CHANNEL = 8;
     */
    private fun handleGetThreadResponse(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id
                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 1))
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage.Builder().message(faker.music().genre()).build()
                    val requestCreateThread: RequestCreateThread =
                        RequestCreateThread.Builder(0, inviteList)
                            .message(requestThreadInnerMessage)
                            .build()
                    val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
                    fucCallback[ConstantMsgType.CREATE_THREAD] = uniqueId!![0]

                    createThreadChannel(inviteList)
                    createThreadChannelGroup(inviteList)
                    createThreadOwnerGroup(inviteList)
                    createThreadPublicGroup(inviteList)
                    break
                }
            }
        }
    }


    private fun unBlockContact() {
        // get Contact
        // block contact with 3 params
        //
        val requestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.UNBLOCK_CONTACT] = mainViewModel.getContact(requestGetContact)
    }

    private fun updateContact() {
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.UPDATE_CONTACT] = mainViewModel.getContact(requestGetContact)
        val position = 7
        changeIconSend(position)
    }

    private fun getBlockList() {
        val requestBlockList = RequestBlockList.Builder().build()
        mainViewModel.getBlockList(requestBlockList)
    }

    private fun getThread() {
        val requestThread = RequestThread.Builder().build()
        fucCallback[ConstantMsgType.GET_THREAD] = mainViewModel.getThread(requestThread)
        val position = 4
        changeIconSend(position)
    }

    private fun scroll(position: Int) {
        recyclerViewSmooth.targetPosition = position
        linearLayoutManager.startSmoothScroll(recyclerViewSmooth)
    }

    private fun changeIconReceive(position: Int) {
        methods[position].methodNameFlag = true
        activity?.runOnUiThread {
            val viewHolder: RecyclerView.ViewHolder? = recyclerView.findViewHolderForAdapterPosition(position)
            viewHolder?.itemView?.findViewById<ProgressBar>(R.id.progress_method)?.visibility = View.GONE
//            viewHolder?.itemView?.findViewById<AppCompatImageView>(R.id.checkBox_ufil)
//                ?.setImageResource(R.drawable.ic_round_done_all_24px)
//            viewHolder?.itemView?.findViewById<AppCompatImageView>(R.id.checkBox_ufil)
//                ?.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorPrimary))
        }
    }

    /* visibility of progress bar*/
    private fun changeIconSend(position: Int) {
        activity?.runOnUiThread {

            val viewHolder: RecyclerView.ViewHolder = recyclerView.findViewHolderForAdapterPosition(position)!!
            viewHolder.itemView.findViewById<ProgressBar>(R.id.progress_method).visibility = View.VISIBLE

//            viewHolder.itemView.findViewById<AppCompatImageView>(R.id.checkBox_ufil)
//                .setImageResource(R.drawable.ic_round_done_all_24px)
        }
    }
/*
* getContact
*choose one of the contact and create thread with that TYPE_PUBLIC_GROUP
 *choose another to add as a participant
* */

    private fun addParticipant() {
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.ADD_PARTICIPANT] = mainViewModel.getContact(requestGetContact)
        val position = 10
        changeIconSend(position)
    }

    private fun forwardMessage() {

        val requestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.FORWARD_MESSAGE] = mainViewModel.getContact(requestGetContact)


        //get contact
        //createThread with message with that first contact
        //createThread with second contact

        //forward the message from first thread to second thread

        // if the sent type come then its sent

    }

    private fun replyMessage() {
        val requestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.REPLY_MESSAGE] = mainViewModel.getContact(requestGetContact)

        //getContact
        //CreateThread with message
        //get that message id and thread id and call reply Message
    }


}

