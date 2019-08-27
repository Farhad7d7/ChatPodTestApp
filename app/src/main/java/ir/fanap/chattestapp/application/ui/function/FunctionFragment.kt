package ir.fanap.chattestapp.application.ui.function

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.*
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import ir.fanap.chattestapp.application.ui.TestListener
import ir.fanap.chattestapp.application.ui.util.ConstantMsgType
import ir.fanap.chattestapp.application.ui.util.ConstantMsgType.Companion.GET_HISTORY
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodFuncFour
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodFuncOne
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodFuncThree
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodFuncTwo
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodNames
import ir.fanap.chattestapp.application.ui.util.TokenFragment
import ir.fanap.chattestapp.bussines.model.Method
import kotlinx.android.synthetic.main.fragment_function.*
import kotlinx.android.synthetic.main.search_contacts_bottom_sheet.*
import kotlinx.android.synthetic.main.search_log_bottom_sheet.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FunctionFragment : Fragment(), FunctionAdapter.ViewHolderListener, TestListener {


    private var contactBIdType: Int = 0
    private var chatReady: Boolean = false
    var TEST_THREAD_ID: Long = 10955L

    private lateinit var buttonConnect: Button
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
    private lateinit var bottomSheetSearch: BottomSheetBehavior<NestedScrollView>
    private lateinit var bottomSheetSearchContacts: BottomSheetBehavior<NestedScrollView>
    private var gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private var methods: MutableList<Method> = mutableListOf()
    private var fucCallback: MapVariable<String, String> = MapVariable()
    private val positionUniqueIds: HashMap<Int, ArrayList<String>> = HashMap()
    private val uniqueIdsLogName: HashMap<String, Map<String, String>> = HashMap()
    private var dataKeeper: Triple<ArrayList<String>, ArrayList<String>, ArrayList<String>> =
        Triple(first = ArrayList(), second = ArrayList(), third = ArrayList())

//    private val logObservable = MapVariable(fucCallback)

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
    private var SAND_TOKEN = "b4735b4c3e5a4b4798ac3eb523087efc"
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


    companion object {

        fun newInstance(): FunctionFragment {
            return FunctionFragment()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        bottomSheetSearch = BottomSheetBehavior.from(bottom_sheet_search)

        bottomSheetSearchContacts = BottomSheetBehavior.from(bottom_sheet_search_contacts)

        bottomSheetSearch.isHideable = true

        bottomSheetSearchContacts.isHideable = true

//        appCompatImageView.setOnClickListener {
//
//
//            showTokenDialog()
//
//        }


        rgSearchContactsType.setOnCheckedChangeListener { _, _ ->

            when {

                rbCellphone.isChecked -> etSearchContacts.inputType = InputType.TYPE_CLASS_PHONE

                rbEmail.isChecked -> etSearchContacts.inputType =
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

                rbFirstName.isChecked -> etSearchContacts.inputType = InputType.TYPE_CLASS_TEXT

                rbLastName.isChecked -> etSearchContacts.inputType = InputType.TYPE_CLASS_TEXT

                rbId.isChecked -> etSearchContacts.inputType = InputType.TYPE_CLASS_NUMBER

            }

        }



        etSearchContacts.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {


                handleSearchContact(view)
            }

            return@setOnEditorActionListener true
        }
        btnSearchContacts.setOnClickListener {

            handleSearchContact(view)


        }

        btnCancelContacts.setOnClickListener {


            hideKeyboard(context, view)

            bottomSheetSearchContacts.state = BottomSheetBehavior.STATE_HIDDEN

        }






        fltBtnSetToken.setOnClickListener {

            showTokenDialog()

        }


        fltBtnSearchMethod.setOnClickListener {

            showSearchInMethods()

        }


        btnSearch.setOnClickListener {

            val query = etSearch.text.toString()


            if (query.length > 2) {

                searchInMethodsWith(query)


            } else {

                etSearch.error = "Query must be at least 3 characters"

            }


        }

        btnCancel.setOnClickListener {


            bottomSheetSearch.state = BottomSheetBehavior.STATE_COLLAPSED
        }



        bottomSheetSearch.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {


            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, state: Int) {

                when (state) {

                    BottomSheetBehavior.STATE_COLLAPSED -> {

                        bottomSheetSearch.state = BottomSheetBehavior.STATE_HIDDEN


                    }


                }

            }
        })

        bottomSheetSearchContacts.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {


            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, p1: Int) {

                when (p1) {

                    BottomSheetBehavior.STATE_COLLAPSED -> {

                        bottomSheetSearchContacts.state = BottomSheetBehavior.STATE_HIDDEN


                    }


                }

            }
        })


    }

    private fun handleSearchContact(view: View) {
        val searchQuery = etSearchContacts.text.toString()


        if (searchQuery.isBlank()) {


            etSearchContacts.error = "Enter something"

            etSearchContacts.requestFocus()

            return
        }


        hideKeyboard(context, view)

        bottomSheetSearchContacts.state = BottomSheetBehavior.STATE_COLLAPSED

        searchContact(searchQuery)
    }


    private fun hideKeyboard(context: Context?, view: View) {

        val imm: InputMethodManager =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(view.windowToken, 0)


    }


    private fun searchInMethodsWith(query: String) {


        var found = false

        val queryS = query.replace(" ", "")

        for (methodIndex in methodNames.indices) {

            val methodName = methodNames[methodIndex].replace(" ", "")


            Log.d("MTAG", "Query: $queryS method: $methodName")

            if (methodName.contains(queryS, ignoreCase = true)) {


                hideKeyboard(context, view!!)


                bottomSheetSearch.state = BottomSheetBehavior.STATE_COLLAPSED

                functionAdapter.changeSearched(methodIndex, true)

                recyclerView.smoothScrollToPosition(methodIndex)


                Handler().postDelayed({


                    functionAdapter.changeSearched(methodIndex, false)


                }, 5000)


                found = true

                break


            }


        }

        if (!found) {


            Toast.makeText(context, "Nothing found!", Toast.LENGTH_LONG)
                .show()


        }


    }

    private fun showSearchInMethods() {


        bottomSheetSearch.state = BottomSheetBehavior.STATE_EXPANDED


    }


    private fun showTokenDialog() {

        val tokenFragment = TokenFragment()

        val tr = childFragmentManager.beginTransaction()

        tokenFragment.show(tr, "TOKEN_FRAG")


        tokenFragment.setOnTokenSet(object : TokenFragment.IDialogToken {

            override fun onTokenSet(token: String) {


                Log.d("MTAG", "new token set: $token")

                SAND_TOKEN = token


                tokenFragment.dismiss()

            }
        })


    }


    override fun onLogClicked(clickedViewHolder: FunctionAdapter.ViewHolder) {

        var position = clickedViewHolder.adapterPosition


//        val uniqueIds = // positionUniqueIds[position]


//        methods[position].log = ""
//
//        uniqueIds?.forEach { uniqueId ->
//
//            val nameAndLogs = uniqueIdsLogName[uniqueId]
//
//            nameAndLogs?.forEach { map ->
//
//                methods[position].log += "\n\n\n <<< ${map.key} >>> \n\n\n ${map.value}"
//
//
//
//            }
//
//        }


        textView_log.text = methods[position].log

        if (textView_log.text.isEmpty()) {
            appCompatImageView_noResponse.visibility = View.VISIBLE
            txtView_noResponse.visibility = View.VISIBLE
        } else {
            appCompatImageView_noResponse.visibility = View.GONE
            txtView_noResponse.visibility = View.GONE
        }

        bottomSheetLog.state = BottomSheetBehavior.STATE_EXPANDED


//        textView_log.append( "\n ********* LOG START ********* \n ${methods[position]} \n" +
//                " ********* LOG END ********* \n" )

//        when (position) {
//            0 -> {
////
////                if (bottomSheetLog.state != null) {
////                    Toast.makeText(activity, bottomSheetLog.state.toString(), Toast.LENGTH_SHORT).show()
////                }
//            }
//            1 -> {
//                textView_log.text = methods[position].log
//            }
//
//
//        }

    }


    //todo add run all button
    //todo test on sand box
    //todo fix conditions that no contact with HasUser field = true occurs

    override fun onIconClicked(clickedViewHolder: FunctionAdapter.ViewHolder) {

        var position = clickedViewHolder.adapterPosition


        runMethodAtPosition(position)

//        for(i in 0..29){
//            runMethodAtPosition(i)
//        }

    }

    private fun runMethodAtPosition(position: Int) {


        removeErrorStateOnFunctionInPosition(position)

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
                getParticipant()
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

                getSearchQuery()
            }

        }
    }

    private fun getSearchQuery() {


        bottomSheetSearchContacts.state = BottomSheetBehavior.STATE_EXPANDED

    }

    private fun searchContact(query: String) {


        var searchContact = SearchContact.Builder("0", "50")


        when {

            rbCellphone.isChecked -> searchContact.cellphoneNumber(query)
            rbEmail.isChecked -> searchContact.email(query)
            rbFirstName.isChecked -> searchContact.firstName(query)
            rbLastName.isChecked -> searchContact.lastName(query)
            rbId.isChecked -> searchContact.id(query)
        }

        var requestSearchContact = searchContact.build()

        changeIconSend(30)

        changeFunOneState(30, Method.RUNNING)

        fucCallback[ConstantMsgType.SEARCH_CONTACT] =
            mainViewModel.searchContact(requestSearchContact)

    }

    private fun getSeenList() {

        changeIconSend(29)


        changeFunOneState(29, Method.RUNNING)

        val requestThread = RequestThread.Builder().build()

        fucCallback[ConstantMsgType.GET_SEEN_LIST] = mainViewModel.getThread(requestThread)


    }

    private fun getDeliverList() {

        changeIconSend(28)

        changeFunOneState(28, Method.RUNNING)

        val requestThread = RequestThread.Builder().build()

        fucCallback[ConstantMsgType.GET_DELIVER_LIST] = mainViewModel.getThread(requestThread)


    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_function, container, false)
        initView(view)

        val buttonClose = view.findViewById(R.id.button_close) as Button

        bottomSheetLog = BottomSheetBehavior.from(bottom_sheet_log)




        bottomSheetLog.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, p1: Float) {

//                bottomSheetLog.state = BottomSheetBehavior.STATE_EXPANDED


            }

            override fun onStateChanged(p0: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
//                        textView_state.text = "close"
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                        bottomSheetLog.state = BottomSheetBehavior.STATE_EXPANDED

                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                        bottomSheetLog.state = BottomSheetBehavior.STATE_EXPANDED

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
        buttonConnect.setOnClickListener { connect() }
        switchCompat_sandBox.setOnCheckedChangeListener { _, isChecked ->
            sandbox = isChecked
        }


        val menuView = view.findViewById<View>(R.id.relativeMenu)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                if ((linearLayoutManager.findLastVisibleItemPosition() + 1) == linearLayoutManager.itemCount) {


                    menuView.animate()
                        .setDuration(250)
                        .translationY(100f)
                        .alpha(0f)
                        .setInterpolator(LinearInterpolator())
                        .start()


                } else {


                    menuView.animate()
                        .setDuration(250)
                        .translationY(0f)
                        .alpha(1f)
                        .setInterpolator(LinearInterpolator())
                        .start()


                }


            }
        })


        return view
    }

    private fun initView(view: View) {
        buttonConnect = view.findViewById(R.id.button_Connect)
        recyclerView = view.findViewById(R.id.recyclerV_funcFrag)
        recyclerView.isNestedScrollingEnabled = true
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

        mainViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
                .create(MainViewModel::class.java)
//            .of(this).get(MainViewModel::class.java)
        mainViewModel.setTestListener(this)
        mainViewModel.observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                textView_state.text = it

                if (it == "CHAT_READY") {

                    chatReady = true
                    avLoadingIndicatorView.visibility = View.GONE
                    textView_state.setTextColor(
                        ContextCompat.getColor(activity?.applicationContext!!, R.color.green_active)
                    )


//                    buttonConnect.animate().scaleX(0.2f).setDuration(450).setInterpolator(BounceInterpolator()).start()

                } else {

                    chatReady = false
                }


            }


//        logObserver
//            .subscribeOn(AndroidSchedulers.mainThread())
//            .doOnNext {
//                Log.d("MTAG","on next $it")
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//
//                        Log.d("MTAG","CHange detected: $it")
//            }
//
//
//        observable.subscribe {
//
//            Log.d("MTAG","Value changed $it")
//
//        }
//        pOb.subscribe {
//
//            Log.d("MTAG","Value changed $it")
//        }


//        fucCallback.observableKeys.subscribe{
//            Log.d("MTAG","KEYS CHANGED: $it")
//
//        }
//        fucCallback.observableVals.subscribe{
//            Log.d("MTAG","VALUES CHANGED: $it")
//        }
//

//        fucCallback.oKey.subscribe{
//            Log.d("MTAG","KEY INSERTED: $it")
//
//        }
//        fucCallback.oValue.subscribe{
////            Log.d("MTAG","VALUE INSERTED: $it")
//
//
//
//
//        }


        fucCallback.onInsertObserver.subscribe {

            Log.d("LTAG", "On Insert to FUCCALLBACK $it")


            if (positionUniqueIds[getPositionOf(it.first)] == null) {

                positionUniqueIds[getPositionOf(it.first)] = ArrayList()

            }

            positionUniqueIds[getPositionOf(it.first)]?.add(it.second)



            Log.d("LTAG", "Position Unique Ids => $positionUniqueIds")


        }


    }

    private fun getHistory() {
        val position = getPositionOf(ConstantMsgType.GET_HISTORY)
        changeIconSend(position)
        changeFunOneState(position, Method.RUNNING)
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.GET_HISTORY] = mainViewModel.getContact(requestGetContact)
    }

    private fun editMessage() {

        val pos = getPositionOf(ConstantMsgType.EDIT_MESSAGE)
        changeIconSend(pos)
        changeFunOneState(pos, Method.RUNNING)
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        val uniqueId = mainViewModel.getContact(requestGetContact)
        fucCallback[ConstantMsgType.EDIT_MESSAGE] = uniqueId
    }

    private fun deleteMessage() {

        val pos = getPositionOf(ConstantMsgType.DELETE_MESSAGE)

        changeIconSend(pos)

        changeFunOneState(pos, Method.RUNNING)

        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()

        fucCallback[ConstantMsgType.DELETE_MESSAGE] = mainViewModel.getContact(requestGetContact)

    }


    override fun onGetSearchContactResult(response: ChatResponse<ResultContact>?) {
        super.onGetSearchContactResult(response)


        val jObj = gson.toJson(response)



        if (fucCallback[ConstantMsgType.SEARCH_CONTACT] == response?.uniqueId) {

            changeIconReceive(30)

            changeFunOneState(30, Method.DONE)


            methods[30].methodNameFlag = true

//            addLogsOfFunctionAtPosition(jObj,30)


        }


    }

    private fun addLogsOfFunctionAtPosition(logName: String, jObj: String?, position: Int) {


        var beautifyText: String? = jObj?.replace("{", "\t{\n")
        beautifyText = beautifyText?.replace("[", "\t[\n")
        beautifyText = beautifyText?.replace("}", "\n\t}")
        beautifyText = beautifyText?.replace("]", "\n\t]")
        beautifyText = beautifyText?.replace(",", ",\n")



        methods[position].log += "\n ********* LOG START ********* \n <<$logName>> \n \n\n $beautifyText \n\n" +
                " ********* LOG END ********* \n"


    }

    override fun onClearHistory(chatResponse: ChatResponse<ResultClearHistory>?) {
        super.onClearHistory(chatResponse)

        val position = 22
        val jObj = gson.toJson(chatResponse)
//        addLogsOfFunctionAtPosition(jObj,position)
        methods[position].methodNameFlag = true
        changeIconReceive(position)
        changeFunTwoState(position, Method.DONE)
    }


    override fun onGetAdminList(content: ChatResponse<ResultParticipant>?) {
        super.onGetAdminList(content)


        if (fucCallback[ConstantMsgType.GET_ADMINS_LIST] == content?.uniqueId) {

            val jObj = gson.toJson(content)

            // positionUniqueIds[getPositionOf(ConstantMsgType.GET_ADMINS_LIST)]?.add(content?.uniqueId!!)

//            addLogsOfFunctionAtPosition(jObj,getPositionOf(ConstantMsgType.GET_ADMINS_LIST))


            handleOnGetAdminList(content?.result)

        }


        if (fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] == content?.uniqueId) {

            val jObj = gson.toJson(content)
//            addLogsOfFunctionAtPosition(jObj,getPositionOf(ConstantMsgType.REMOVE_ADMIN_ROLES))


            val pos = getPositionOf(ConstantMsgType.REMOVE_ADMIN_ROLES)

            changeFunTwoState(pos, Method.DONE)

            changeFunThreeState(pos, Method.RUNNING)


            prepareRemoveAdminRoles(content, fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES])


        }


    }


    private fun handleOnGetAdminList(response: ResultParticipant?) {

        val position = 23

        methods[position].methodNameFlag = true

        changeFunTwoState(position, Method.DONE)

        changeIconReceive(position)


    }

    override fun onSetRole(outputSetRoleToUser: ChatResponse<ResultSetAdmin>?) {
        super.onSetRole(outputSetRoleToUser)


        val jObj = gson.toJson(outputSetRoleToUser)

        val addAdminPosition = 24

        val removeAdminPosition = 25

        when (outputSetRoleToUser?.uniqueId) {

            fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] -> {


//                addLogsOfFunctionAtPosition(jObj,getPositionOf(ConstantMsgType.ADD_ADMIN_ROLES))


                changeFunThreeState(addAdminPosition, Method.DONE)

                changeIconReceive(addAdminPosition)


                methods[addAdminPosition].methodNameFlag = true

            }

            fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] -> {


//                addLogsOfFunctionAtPosition(jObj,getPositionOf(ConstantMsgType.REMOVE_ADMIN_ROLES))


                changeFunThreeState(removeAdminPosition, Method.DONE)

                changeIconReceive(removeAdminPosition)

                methods[removeAdminPosition].methodNameFlag = true


            }


        }


    }

    override fun onBlockList(response: ChatResponse<ResultBlockList>?) {
        super.onBlockList(response)




        if (fucCallback[ConstantMsgType.GET_BLOCK_LIST] == response?.uniqueId) {

            var position = 5
            methods[position].methodNameFlag = true
            changeIconReceive(position)
            changeFunOneState(position, Method.DONE)

        }




        if (fucCallback[ConstantMsgType.UNBLOCK_CONTACT] == response?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.UNBLOCK_CONTACT)

            changeFunOneState(pos, Method.DONE)

            changeFunTwoState(pos, Method.RUNNING)


            handleUnBlockContact(response)


        }

//        addLogsOfFunctionAtPosition(jObj,getPositionOf(ConstantMsgType.GET_BLOCK_LIST))

    }

    override fun onError(chatResponse: ErrorOutPut?) {
        super.onError(chatResponse)
        activity?.runOnUiThread {
            Toast.makeText(activity, chatResponse?.errorMessage, Toast.LENGTH_LONG).show()
        }
        val uniqueId = chatResponse?.uniqueId


        val jObj = gson.toJson(chatResponse)


        fucCallback.forEach {

            if (it.value == uniqueId) {

                setErrorOnFunctionInPosition(getPositionOf(it.key))

            }

        }

        fucCallbacks.forEach {

            if (it.value.contains(uniqueId)) {

                setErrorOnFunctionInPosition(getPositionOf(it.key))

            }

        }

//        for(key in fucCallback.forEach()){
//            
//            
//            
//        }

//        when (uniqueId) {
//
//            fucCallback[ConstantMsgType.CREATE_THREAD] -> {
//
//
//                setErrorOnFunctionInPosition(0)
//
//            }
//
//            fucCallback[ConstantMsgType.GET_CONTACT] -> {
//
//
//                setErrorOnFunctionInPosition(1)
//
//            }
//
//            fucCallback[ConstantMsgType.BLOCK_CONTACT] -> {
//
//
//                setErrorOnFunctionInPosition(2)
//
//            }
//
//            fucCallback[ConstantMsgType.ADD_CONTACT] -> {
//
//
//                setErrorOnFunctionInPosition(3)
//
//            }
//
//            fucCallback[ConstantMsgType.GET_THREAD] -> {
//
//
//                setErrorOnFunctionInPosition(4)
//
//            }
//
//            fucCallback[ConstantMsgType.GET_BLOCK_LIST] -> {
//
//
//                setErrorOnFunctionInPosition(5)
//
//            }
//
//            fucCallback[ConstantMsgType.UNBLOCK_CONTACT] -> {
//
//
//                setErrorOnFunctionInPosition(6)
//
//            }
//
//            fucCallback[ConstantMsgType.UPDATE_CONTACT] -> {
//
//
//                setErrorOnFunctionInPosition(7)
//
//            }
//
//            fucCallback[ConstantMsgType.SEND_MESSAGE] -> {
//
//
//                setErrorOnFunctionInPosition(8)
//
//            }
//
//            fucCallback[ConstantMsgType.REMOVE_CONTACT] -> {
//
//
//                setErrorOnFunctionInPosition(9)
//
//            }
//
//            fucCallback[ConstantMsgType.ADD_PARTICIPANT] -> {
//
//
//                setErrorOnFunctionInPosition(10)
//
//            }
//
//            fucCallback[ConstantMsgType.REMOVE_PARTICIPANT] -> {
//
//
//                setErrorOnFunctionInPosition(11)
//
//            }
//
//            fucCallback[ConstantMsgType.FORWARD_MESSAGE] -> {
//
//
//                setErrorOnFunctionInPosition(12)
//
//            }
//
//            fucCallback[ConstantMsgType.REPLY_MESSAGE] -> {
//
//
//                setErrorOnFunctionInPosition(13)
//
//            }
//
//            fucCallback[ConstantMsgType.LEAVE_THREAD] -> {
//
//
//                setErrorOnFunctionInPosition(14)
//
//            }
//
//            fucCallback[ConstantMsgType.MUTE_THREAD] -> {
//
//
//                setErrorOnFunctionInPosition(15)
//
//            }
//
//            fucCallback[ConstantMsgType.UNMUTE_THREAD] -> {
//
//
//                setErrorOnFunctionInPosition(16)
//
//            }
//
//            fucCallback[ConstantMsgType.DELETE_MESSAGE] -> {
//
//
//                setErrorOnFunctionInPosition(17)
//
//            }
//
//            fucCallback[ConstantMsgType.EDIT_MESSAGE] -> {
//
//
//                setErrorOnFunctionInPosition(18)
//
//            }
//
//            fucCallback[ConstantMsgType.GET_HISTORY] -> {
//
//
//                setErrorOnFunctionInPosition(19)
//
//            }
//
//            fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] -> {
//
//
//                setErrorOnFunctionInPosition(20)
//
//            }
//
//            fucCallback[ConstantMsgType.GET_PARTICIPANT] -> {
//
//
//                setErrorOnFunctionInPosition(21)
//
//            }
//
//            fucCallback[ConstantMsgType.CLEAR_HISTORY] -> {
//
//
//                setErrorOnFunctionInPosition(22)
//
//            }
//
//            fucCallback[ConstantMsgType.GET_ADMINS_LIST] -> {
//
//
//                setErrorOnFunctionInPosition(23)
//
//            }
//
//            fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] -> {
//
//
//                setErrorOnFunctionInPosition(24)
//
//            }
//
//            fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] -> {
//
//
//                setErrorOnFunctionInPosition(25)
//
//            }
//
//            fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] -> {
//
//
//                setErrorOnFunctionInPosition(26)
//
//            }
//
//
//
//            fucCallback[ConstantMsgType.CREATE_THREAD_WITH_MSG] -> {
//
//
//                setErrorOnFunctionInPosition(27)
//
//            }
//
//            fucCallback[ConstantMsgType.GET_DELIVER_LIST] -> {
//
//
//                setErrorOnFunctionInPosition(28)
//
//            }
//
//            fucCallback[ConstantMsgType.GET_SEEN_LIST] -> {
//
//
//                setErrorOnFunctionInPosition(29)
//
//            }
//
//            fucCallback[ConstantMsgType.SEARCH_CONTACT] -> {
//
//
//                setErrorOnFunctionInPosition(30)
//
//            }
//
//
//        }


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

    private fun getPositionOf(key: String): Int {


        return when (key) {

            "CREATE_THREAD" -> 0


            "GET_CONTACT" -> 1


            "BLOCK_CONTACT" -> 2


            "ADD_CONTACT" -> 3


            "GET_THREAD" -> 4


            "GET_BLOCK_LIST" -> 5


            "UNBLOCK_CONTACT" -> 6


            "UPDATE_CONTACT" -> 7


            "SEND_MESSAGE" -> 8


            "REMOVE_CONTACT" -> 9


            "ADD_PARTICIPANT" -> 10


            "REMOVE_PARTICIPANT" -> 11


            //forward message keys

            "FORWARD_MESSAGE" -> 12

            "FORWARD_MESSAGE_CONTACT_B" -> 12

            "FORWARD_MESSAGE_ID" -> 12


            "REPLY_MESSAGE" -> 13


            "LEAVE_THREAD" -> 14


            "MUTE_THREAD" -> 15


            "UNMUTE_THREAD" -> 16


            "DELETE_MESSAGE" -> 17


            "DELETE_MESSAGE_ID" -> 17


            "EDIT_MESSAGE" -> 18


            "GET_HISTORY" -> 19


            "CREATE_THREAD_WITH_FORW_MSG" -> 20


            "GET_PARTICIPANT" -> 21


            "CLEAR_HISTORY" -> 22


            "GET_ADMINS_LIST" -> 23


            "ADD_ADMIN_ROLES" -> 24


            "REMOVE_ADMIN_ROLES" -> 25


            "DELETE_MULTIPLE_MESSAGE" -> 26


            "CREATE_THREAD_WITH_MSG" -> 27


            "GET_DELIVER_LIST" -> 28


            "GET_SEEN_LIST" -> 29


            "SEARCH_CONTACT" -> 30


            else -> -1
        }


    }


    private fun setErrorOnFunctionInPosition(position: Int) {


        if (position == -1)
            return

        recyclerView.smoothScrollToPosition(position)

        functionAdapter.setErrorState(position, true)

        functionAdapter.deActivateFunction(position)

        when (Method.RUNNING) {

            methods[position].funcOneState -> methods[position].funcOneState = Method.FAIL
            methods[position].funcTwoState -> methods[position].funcTwoState = Method.FAIL
            methods[position].funcThreeState -> methods[position].funcThreeState = Method.FAIL
            methods[position].funcFourState -> methods[position].funcFourState = Method.FAIL


        }

        functionAdapter.notifyItemChanged(position)


    }


    private fun removeErrorStateOnFunctionInPosition(position: Int) {

        if (position == -1) return

        activity?.runOnUiThread {
            functionAdapter.setErrorState(position, false)

        }

    }

    override fun onUnBlock(response: ChatResponse<ResultBlock>?) {
        super.onUnBlock(response)

        if (fucCallback[ConstantMsgType.UNBLOCK_CONTACT] == response?.uniqueId) {

            fucCallback.remove(ConstantMsgType.UNBLOCK_CONTACT)

            val position = 6

            changeFunTwoState(position, Method.DONE)

            changeIconReceive(position)
        }
    }

    override fun onGetThread(chatResponse: ChatResponse<ResultThreads>?) {
        super.onGetThread(chatResponse)

//
//        val jObj = try {
//            gson.toJson(chatResponse)
//        } catch (e: Exception) {
//            Log.d("MTAG","Exception in converting: ${e.message}")
//        }


        if (fucCallback[ConstantMsgType.GET_THREAD] == chatResponse?.uniqueId) {
            val position = 4
            fucCallback.remove(ConstantMsgType.GET_THREAD)
            changeIconReceive(position)
            changeFunOneState(position, Method.DONE)
            methods[4].methodNameFlag = true
//            methods[4].log = jObj?.toString()
        }

        if (fucCallback[ConstantMsgType.SEND_MESSAGE] == chatResponse?.uniqueId) {

            prepareSendMessage(chatResponse)
        }

        if (fucCallback[ConstantMsgType.CLEAR_HISTORY] == chatResponse?.uniqueId) {


            changeFunOneState(getPositionOf(ConstantMsgType.CLEAR_HISTORY), Method.DONE)

            changeFunTwoState(getPositionOf(ConstantMsgType.CLEAR_HISTORY), Method.RUNNING)

            prepareClearHistory(chatResponse)


        }

        if (fucCallback[ConstantMsgType.GET_ADMINS_LIST] == chatResponse?.uniqueId) {


            changeFunOneState(23, Method.DONE)


            changeFunTwoState(23, Method.RUNNING)

            // positionUniqueIds[getPositionOf(ConstantMsgType.GET_ADMINS_LIST)]?.add(chatResponse?.uniqueId!!)

            prepareGetAdminList(chatResponse)

        }


        if (fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] == chatResponse?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.ADD_ADMIN_ROLES)

            changeFunOneState(pos, Method.DONE)

            changeFunTwoState(pos, Method.RUNNING)

            prepareAddAdmin(chatResponse)

        }



        if (fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] == chatResponse?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.REMOVE_ADMIN_ROLES)

            changeFunOneState(pos, Method.DONE)

            changeFunTwoState(pos, Method.RUNNING)

            prepareRemoveAdmin(chatResponse)

        }

        if (fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] == chatResponse?.uniqueId) {


            changeFunOneState(getPositionOf(ConstantMsgType.DELETE_MULTIPLE_MESSAGE), Method.DONE)
            changeFunTwoState(
                getPositionOf(ConstantMsgType.DELETE_MULTIPLE_MESSAGE),
                Method.RUNNING
            )
            prepareDeleteMultipleMessage(chatResponse)

        }

        if (fucCallback[ConstantMsgType.GET_SEEN_LIST] == chatResponse?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.GET_SEEN_LIST)

            changeFunOneState(pos, Method.DONE)

            changeFunTwoState(pos, Method.RUNNING)

            prepareSendTxtMsgForGetSeenList(chatResponse)

        }

        if (fucCallback[ConstantMsgType.GET_DELIVER_LIST] == chatResponse?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.GET_DELIVER_LIST)

            changeFunOneState(pos, Method.DONE)

            changeFunTwoState(pos, Method.RUNNING)

            prepareSendTxtMsgForGetDeliverList(chatResponse)

        }

        if (fucCallback[ConstantMsgType.REMOVE_PARTICIPANT] == chatResponse?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.REMOVE_PARTICIPANT)
            changeFunOneState(position, Method.DONE)
            changeFunTwoState(position, Method.RUNNING)
            prepareRemoveParticipants(chatResponse)

        }


    }


    private fun prepareRemoveParticipants(chatResponse: ChatResponse<ResultThreads>?) {


        var targetThreadId = 0L

        if (chatResponse?.result!!.threads.size > 0) {

            val threads = chatResponse.result?.threads!!

            for (thread in threads) {


                if (thread.isGroup && thread.admin) {

                    targetThreadId = thread.id

                    break


                }


            }


            if (targetThreadId == 0L) {

                Toast.makeText(
                    context,
                    "There is no thread with condition <<Group>> and <<Admin>> True! Please Create On First",
                    Toast.LENGTH_LONG
                )
                    .show()

                return

            }


            val request = RequestThreadParticipant.Builder()
                .threadId(targetThreadId)
                .build()


            fucCallback[ConstantMsgType.REMOVE_PARTICIPANT] = mainViewModel.getParticipant(request)


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


            val listOfSendingMessages = ArrayList<String>()



            for (i in 0..5) {

                val requestMessage =
                    RequestMessage.Builder("$i" + "th Message at ${Date().time} ", threadId).build()

                val uId = mainViewModel.sendTextMsg(requestMessage)

                listOfSendingMessages.add(uId)

            }



            fucCallbacks[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] = listOfSendingMessages

//            fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] = mainViewModel.getHistory(requestGetHistory)


        }


    }

    private fun prepareSendMessage(chatResponse: ChatResponse<ResultThreads>?) {
        if (chatResponse?.result?.threads?.size!! > 0) {
//            fucCallback.remove(ConstantMsgType.SEND_MESSAGE)
            val threadId = chatResponse.result.threads[0].id
            val requestMessage = RequestMessage.Builder(faker.lorem().paragraph(), threadId).build()
            fucCallback[ConstantMsgType.SEND_MESSAGE] = mainViewModel.sendTextMsg(requestMessage)

            val position = getPositionOf(ConstantMsgType.SEND_MESSAGE)
            changeFunOneState(position, Method.DONE)
            changeFunTwoState(position, Method.RUNNING)
            changeFunThreeState(position, Method.RUNNING)

        } else {
            val requestGetContact = RequestGetContact.Builder().build()
            fucCallback.remove(ConstantMsgType.SEND_MESSAGE)
            fucCallback[ConstantMsgType.SEND_MESSAGE] = mainViewModel.getContact(requestGetContact)
        }
    }


    private fun prepareGetAdminList(chatResponse: ChatResponse<ResultThreads>?) {

        if (chatResponse?.result?.threads?.size!! > 0) {

//            fucCallback.remove(ConstantMsgType.GET_ADMINS_LIST)


            var threadId = 0L

            for (thread: Thread in chatResponse.result.threads) {

                if (thread.isGroup) {

                    threadId = thread.id

                    break


                }

            }


            if (threadId == 0L) {

                Toast.makeText(context, "There is no group to get admins", Toast.LENGTH_LONG).show()
                val pos = getPositionOf(ConstantMsgType.GET_ADMINS_LIST)
                changeIconReceive(pos)
                changeFunTwoState(pos, Method.DEACTIVE)

                return

            }


            TEST_THREAD_ID = threadId


            val requestGetAdmin = RequestGetAdmin.Builder()
                .admin(true)
                .threadId(threadId)
                .count(50)
                .build()


            fucCallback[ConstantMsgType.GET_ADMINS_LIST] =
                mainViewModel.getAdminList(requestGetAdmin)

//            // positionUniqueIds[getPositionOf(ConstantMsgType.GET_ADMINS_LIST)]?.add(fucCallback[ConstantMsgType.GET_ADMINS_LIST]!!)

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

                if (thread.isGroup && thread.admin) {

                    threadId = thread.id

                    break


                }

            }


            if (threadId == 0L) {


                Toast.makeText(
                    context,
                    "No Thread found with condition Group and Admin true!",
                    Toast.LENGTH_LONG
                )
                    .show()

                changeIconReceive(getPositionOf(ConstantMsgType.ADD_ADMIN_ROLES))

                changeFunTwoState(getPositionOf(ConstantMsgType.ADD_ADMIN_ROLES), Method.DEACTIVE)

                return

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

                if (thread.isGroup && thread.admin) {

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
            changeFunThreeState(position, Method.DONE)
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
            changeFunFourState(position, Method.DONE)
            methods[position].methodNameFlag = true

        } else {


        }


    }

    override fun onEditedMessage(response: ChatResponse<ResultNewMessage>?) {
        super.onEditedMessage(response)
        if (fucCallback[ConstantMsgType.EDIT_MESSAGE] == response?.uniqueId) {
            val position = 18
            changeIconReceive(position)
            changeFunThreeState(position, Method.DONE)
        }
    }

    override fun onSent(response: ChatResponse<ResultMessage>?) {
        super.onSent(response)


        if(fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] == response?.uniqueId){

            val position = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)
            changeIconReceive(position)
            changeFunFourState(position,Method.DONE)

        }


        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG_ID] == response?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)

            changeFunThreeState(pos,Method.DONE)

            changeFunFourState(pos,Method.RUNNING)

            val forwardMsgId = response?.result?.messageId

            val contactId = fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG_CONTCT_ID]

            val inviteList = ArrayList<Invitee>()
            inviteList.add(Invitee(contactId!!.toLong(), 2))


            val forwList: MutableList<Long> = mutableListOf()
            forwList.add(forwardMsgId!!)


            val requestThreadInnerMessage =
                RequestThreadInnerMessage.Builder().message(faker.music().genre())
                    .forwardedMessageIds(forwList).build()


            val requestCreateThread: RequestCreateThread =
                RequestCreateThread.Builder(0, inviteList)
                    .message(requestThreadInnerMessage)
                    .build()


            fucCallbacks[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] = mainViewModel.createThreadWithMessage(requestCreateThread)!!
            fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] =fucCallbacks[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG]!![2]

        }

        if (fucCallback[ConstantMsgType.EDIT_MESSAGE_ID] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.EDIT_MESSAGE)

            changeFunTwoState(pos, Method.DONE)

            changeFunThreeState(pos, Method.RUNNING)

            requestEditMessage(response)
        }

        if (fucCallback[ConstantMsgType.DELETE_MESSAGE_ID] == response?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.DELETE_MESSAGE)

            changeFunTwoState(pos, Method.DONE)
            changeFunThreeState(pos, Method.RUNNING)
            requestDeleteSingleMessage(response)
        }

        if (fucCallback[ConstantMsgType.REPLY_MESSAGE] == response?.uniqueId) {
            val position = 13
            changeIconReceive(position)
            changeFunThreeState(position, Method.DONE)
        }

        if (fucCallback[ConstantMsgType.REPLY_MESSAGE_ID] == response?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.REPLY_MESSAGE)

            changeFunTwoState(pos, Method.DONE)
            changeFunThreeState(pos, Method.RUNNING)


            requestReplayMessage(response)
        }

        if (fucCallback[ConstantMsgType.FORWARD_MESSAGE] == response?.uniqueId) {
            val position = 12
            methods[position].funcOneFlag = true
            changeIconReceive(position)
            changeFunFourState(position, Method.DONE)
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_MSG_MESSAGE] == response?.uniqueId) {

            val position = 27


            changeFunThreeState(position, Method.DONE)

//            changeSecondIconReceive(position)
            methods[position].funcOneFlag = true
        }

        if (fucCallback[ConstantMsgType.FORWARD_MESSAGE_ID] == response?.uniqueId) {

            fucCallback[ConstantMsgType.FORWARD_MESSAGE_ID] =
                response?.result?.messageId?.toString()!!

            val position = 12
            changeFunTwoState(position, Method.DONE)
            changeFunThreeState(position, Method.RUNNING)

            handleCreateThreadWithContactBForForwardMessage(response)

        }

        if (fucCallback[ConstantMsgType.SEND_MESSAGE] == response?.uniqueId) {
            val position = 8
            methods[position].funcOneFlag = true
            changeSecondIconReceive(position)
            changeFunThreeState(position, Method.DONE)

        }

        if (fucCallbacks[ConstantMsgType.DELETE_MULTIPLE_MESSAGE]?.get(0) == response?.uniqueId) {
            val position = getPositionOf(ConstantMsgType.DELETE_MULTIPLE_MESSAGE)

            changeFunTwoState(position, Method.DONE)

            changeFunThreeState(position, Method.RUNNING)

            getHistoryForDeleteMessage(response)

        }


        if(fucCallbacks[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] != null){

            if (fucCallbacks[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG]?.contains(response?.uniqueId)!!) {

                val position = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)
                changeIconReceive(position)
                changeFunFourState(position, Method.DONE)

            }


        }


    }

    private fun requestEditMessage(response: ChatResponse<ResultMessage>?) {
        val requestEditMessage = RequestEditMessage
            .Builder("this is edit ", response!!.result.messageId)
            .build()


        fucCallback[ConstantMsgType.EDIT_MESSAGE] = mainViewModel.editMessage(requestEditMessage)
    }

    private fun requestReplayMessage(response: ChatResponse<ResultMessage>?) {
        val messageId = response?.result?.messageId
        val threadId = fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID]
        val replyMessage =
            RequestReplyMessage.Builder(
                "this is replyMessage",
                threadId?.toLong()!!,
                messageId!!
            ).build()
        fucCallback[ConstantMsgType.REPLY_MESSAGE] = mainViewModel.replyMessage(replyMessage)
    }

    private fun handleCreateThreadWithContactBForForwardMessage(response: ChatResponse<ResultMessage>?) {


        val contactBId = fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B]?.toLong()


        val inviteList = Array(1) {
            Invitee(contactBId!!, contactBIdType)
        }




        fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B] = mainViewModel.createThread(
            ThreadType.Constants.NORMAL, inviteList, "Contact B", ""
            , "", ""
        )


    }

    private fun getHistoryForDeleteMessage(response: ChatResponse<ResultMessage>?) {


        val threadId = response?.subjectId ?: return


        val requestGetHistory = RequestGetHistory
            .Builder(threadId)
            .offset(0)
            .count(50)
            .build()


        fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] =
            mainViewModel.getHistory(requestGetHistory)


    }

    private fun requestDeleteSingleMessage(response: ChatResponse<ResultMessage>?) {

        val listOfMessageIds = ArrayList<Long>()

        listOfMessageIds.add(response!!.result.messageId)

        val requestDeleteMessage = RequestDeleteMessage.Builder()
            .deleteForAll(true)
            .messageIds(listOfMessageIds)
            .build()
        fucCallback[ConstantMsgType.DELETE_MESSAGE] =
            mainViewModel.deleteMessage(requestDeleteMessage)


    }

    override fun onSeen(response: ChatResponse<ResultMessage>?) {
        super.onSeen(response)
//        val position = 8
//        changeFourthIconReceive(position)
//        methods[position].funcFourFlag = true
    }

    override fun onDeliver(response: ChatResponse<ResultMessage>?) {
        super.onDeliver(response)
//        val position = 8
//        changeThirdIconReceive(position)
//        methods[position].funcThreeFlag = true
    }

    override fun onLeaveThread(response: ChatResponse<ResultLeaveThread>?) {
        super.onLeaveThread(response)

        val pos = 14

        changeIconReceive(pos)

        changeFunThreeState(pos, Method.DONE)
    }

    override fun onUpdateContact(response: ChatResponse<ResultUpdateContact>?) {
        super.onUpdateContact(response)
        if (fucCallback[ConstantMsgType.UPDATE_CONTACT] == response?.uniqueId) {
            val position = 7
            methods[position].methodNameFlag = true
            changeIconReceive(position)
            changeFunTwoState(position, Method.DONE)
        }
    }


    override fun onBlock(chatResponse: ChatResponse<ResultBlock>?) {
        super.onBlock(chatResponse)
        if (fucCallback[ConstantMsgType.BLOCK_CONTACT] == chatResponse?.uniqueId) {

            val position = 2
            changeIconReceive(position)
            methods[position].methodNameFlag = true
            changeFunTwoState(position, Method.DONE)

//            val id = chatResponse?.result?.contact?.id
//
//            if (id != null) {
//                val requestUnBlock = RequestUnBlock.Builder()
//                        .contactId(id)
//                        .build()
//                mainViewModel.unBlock(requestUnBlock)
//            }


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
            changeFunOneState(position, Method.DONE)

//            var id = response?.result?.contact?.id
//            if (id != null) {
//                val requestRemoveContact = RequestRemoveContact.Builder(id).build()
//                mainViewModel.removeContact(requestRemoveContact)
//            }


        }

        if (fucCallback[ConstantMsgType.REMOVE_CONTACT] == response?.uniqueId) {


            val position = getPositionOf(ConstantMsgType.REMOVE_CONTACT)


            changeFunOneState(position, Method.DONE)

            changeFunTwoState(position, Method.RUNNING)


            var id = response?.result?.contact?.id
            if (id != null) {
                fucCallback.remove(ConstantMsgType.REMOVE_CONTACT)
                val requestRemoveContact = RequestRemoveContact.Builder(id).build()
                fucCallback[ConstantMsgType.REMOVE_CONTACT] =
                    mainViewModel.removeContact(requestRemoveContact)
            }
        }
    }

    override fun onMuteThread(response: ChatResponse<ResultMute>?) {
        super.onMuteThread(response)
        if (fucCallback[ConstantMsgType.MUTE_THREAD] == response?.uniqueId) {
            val position = 15
            changeIconReceive(position)
            changeFunThreeState(position, Method.DONE)
        }
    }

    override fun onUnmuteThread(response: ChatResponse<ResultMute>?) {
        super.onUnmuteThread(response)
        if (fucCallback[ConstantMsgType.UNMUTE_THREAD] == response?.uniqueId) {
            val position = 16
            changeIconReceive(position)
            changeFunThreeState(position, Method.DONE)
        }
    }

    override fun onGetHistory(response: ChatResponse<ResultHistory>?) {
        super.onGetHistory(response)


        if (fucCallback[ConstantMsgType.GET_HISTORY] == response?.uniqueId) {
            val position = 19
            changeIconReceive(position)
            changeFunThreeState(position,Method.DONE)
        }

        if (fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.DELETE_MULTIPLE_MESSAGE)

            changeFunThreeState(pos, Method.DONE)

            changeFunFourState(pos, Method.RUNNING)

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
                activity?.applicationContext,
                "There is no deletable message! Create a thread with message first",
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


            val position = 21
            changeIconReceive(position)
            changeFunThreeState(position,Method.DONE)


        }

        if (fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] == response?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.ADD_ADMIN_ROLES)

            changeFunTwoState(pos, Method.DONE)

            changeFunThreeState(pos, Method.RUNNING)

            prepareAddAdminRoles(response, fucCallback[ConstantMsgType.ADD_ADMIN_ROLES])
        }


        if (fucCallback[ConstantMsgType.REMOVE_PARTICIPANT] == response?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.REMOVE_PARTICIPANT)
            changeFunTwoState(position, Method.DONE)
            changeFunThreeState(position, Method.RUNNING)
            requestRemoveParticipant(response)

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

    private fun prepareRemoveAdminRoles(
        chatResponse: ChatResponse<ResultParticipant>?,
        uniqueId: String?
    ) {


        var adminParticipant: Participant? = null


        for (par in chatResponse?.result!!.participants) {

            if (par.admin) {

                adminParticipant = par

                break
            }

        }


        if (adminParticipant == null) {
            Toast.makeText(
                context,
                "There is no thread with admin! create one first",
                Toast.LENGTH_LONG
            )
                .show()

            changeIconReceive(getPositionOf(ConstantMsgType.REMOVE_ADMIN_ROLES))
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



        if (fucCallback[ConstantMsgType.SEND_MESSAGE] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.SEND_MESSAGE)
            changeIconReceive(pos)
            changeFunTwoState(pos, Method.DONE)


        }



        if (fucCallback[ConstantMsgType.GET_SEEN_LIST] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.GET_SEEN_LIST)

            changeFunTwoState(pos, Method.DONE)

            changeFunThreeState(pos, Method.RUNNING)

            prepareGetSeenList(response)

        }

        if (fucCallback[ConstantMsgType.GET_DELIVER_LIST] == response?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.GET_DELIVER_LIST)

            changeFunTwoState(pos, Method.DONE)

            changeFunThreeState(pos, Method.RUNNING)

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

            changeFunThreeState(29, Method.DONE)

            methods[29].methodNameFlag = true


        }


    }

    override fun onGetDeliverMessageList(response: ChatResponse<ResultParticipant>?) {
        super.onGetDeliverMessageList(response)


        if (response?.uniqueId == fucCallback[ConstantMsgType.GET_DELIVER_LIST]) {

            changeIconReceive(28)

            changeFunThreeState(28, Method.DONE)

            methods[28].methodNameFlag = true


        }

    }

    override fun onCreateThread(response: ChatResponse<ResultThread>?) {
        super.onCreateThread(response)

        if (fucCallback[ConstantMsgType.GET_PARTICIPANT] == response?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.GET_PARTICIPANT)

            changeFunTwoState(position,Method.DONE)

            changeFunThreeState(position,Method.RUNNING)

            requestGetParticipants(response)
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)

            changeFunTwoState(pos,Method.DONE)

            changeFunThreeState(pos,Method.RUNNING)


            sendMessageForForwardMessage(response)
        }


        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_MSG] == response?.uniqueId) {

//            fucCallback.remove(ConstantMsgType.CREATE_THREAD_WITH_MSG)

            changeFunTwoState(27, Method.DONE)


            changeIconReceive(27)

            methods[27].methodNameFlag = true
        }




        if (fucCallback[ConstantMsgType.GET_HISTORY] == response?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.GET_HISTORY)
            changeFunTwoState(position,Method.DONE)
            changeFunThreeState(position,Method.RUNNING)
            requestGetHistory(response)
        }

        if (fucCallback[ConstantMsgType.UNMUTE_THREAD] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.UNMUTE_THREAD)

            changeFunTwoState(pos, Method.DONE)
            changeFunThreeState(pos, Method.RUNNING)

            requestUnmuteThread(response)
        }

        if (fucCallback[ConstantMsgType.MUTE_THREAD] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.MUTE_THREAD)

            changeFunTwoState(pos, Method.DONE)
            changeFunThreeState(pos, Method.RUNNING)

            requestMuteThread(response)
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

            val pos = 14

            changeFunTwoState(pos, Method.DONE)
            changeFunThreeState(pos, Method.RUNNING)


            requestLeaveThread(response)

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

            val pos = getPositionOf(ConstantMsgType.ADD_PARTICIPANT)

            changeFunTwoState(pos, Method.DONE)
            changeFunThreeState(pos, Method.RUNNING)

            handleAddParticipant(response)

        }


        if (fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B] == response?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.FORWARD_MESSAGE)

            changeFunThreeState(pos, Method.DONE)

            changeFunFourState(pos, Method.RUNNING)

            requestForwardMessage(response)


        }
    }

    private fun requestGetParticipants(response: ChatResponse<ResultThread>?) {
        val threadId = response!!.result.thread.id
        val requestThreadParticipant = RequestThreadParticipant.Builder(threadId).build()
       fucCallback[ConstantMsgType.GET_PARTICIPANT] = mainViewModel.getParticipant(requestThreadParticipant)
    }

    private fun sendMessageForForwardMessage(response: ChatResponse<ResultThread>?) {

        val threadId = response!!.result.thread.id

        val requestMessage =
            RequestMessage.Builder(
                "this is message for create thread with forward message",
                threadId
            )
                .build()
        fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG_ID] =
            mainViewModel.sendTextMsg(requestMessage)
    }

    private fun requestGetHistory(response: ChatResponse<ResultThread>?) {
        val threadId = response!!.result.thread.id
        val requestGetHistory = RequestGetHistory.Builder(threadId).build()
        fucCallback[GET_HISTORY] = mainViewModel.getHistory(requestGetHistory)
    }

    private fun requestUnmuteThread(response: ChatResponse<ResultThread>?) {
        val threadId = response!!.result.thread.id
        val requestMuteThread = RequestMuteThread.Builder(threadId).build()
        fucCallback[ConstantMsgType.UNMUTE_THREAD] =
            mainViewModel.unMuteThread(requestMuteThread)
    }

    private fun requestMuteThread(response: ChatResponse<ResultThread>?) {
        val threadId = response!!.result.thread.id
        val requestMuteThread = RequestMuteThread.Builder(threadId).build()
        fucCallback[ConstantMsgType.MUTE_THREAD] = mainViewModel.muteThread(requestMuteThread)
    }

    private fun requestLeaveThread(response: ChatResponse<ResultThread>?) {
        val threadId = response?.result?.thread?.id

        val requeLeaveThread = RequestLeaveThread.Builder(threadId!!.toLong()).build()

        fucCallback[ConstantMsgType.LEAVE_THREAD] = mainViewModel.leaveThread(requeLeaveThread)
    }

    private fun requestForwardMessage(response: ChatResponse<ResultThread>?) {


        val forwardMessageId = fucCallback[ConstantMsgType.FORWARD_MESSAGE_ID]?.toLong()

        val forwardThreadId = response?.result?.thread?.id

        val messageIds = ArrayList<Long>()

        messageIds.add(forwardMessageId!!)

        val requestForwardMessage =
            RequestForwardMessage.Builder(forwardThreadId!!, messageIds).build()
        fucCallback[ConstantMsgType.FORWARD_MESSAGE] =
            mainViewModel.forwardMessage(requestForwardMessage)[0]


    }

    private fun handleAddParticipant(response: ChatResponse<ResultThread>?) {
        val participantId = fucCallback["ADD_PARTICIPANT_ID"]

        fucCallback.remove("ADD_PARTICIPANT_ID")


        val partId = participantId?.toLong()
        val threadId = response?.result?.thread?.id
        if (partId != null && threadId != null) {
            val contactIdList: MutableList<Long> = mutableListOf()
            contactIdList.add(partId)
            val requestAddParticipants =
                RequestAddParticipants.Builder(threadId, contactIdList).build()
            fucCallback[ConstantMsgType.ADD_PARTICIPANT] =
                mainViewModel.addParticipant(requestAddParticipants)

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
                val viewHolder: RecyclerView.ViewHolder? =
                    recyclerView.findViewHolderForAdapterPosition(position)
                viewHolder?.itemView?.findViewById<AppCompatImageView>(R.id.imageView_tickFourth)
                    ?.setImageResource(R.drawable.ic_round_done_all_24px)

                viewHolder?.itemView?.findViewById<AppCompatImageView>(R.id.imageView_tickFourth)
                    ?.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorPrimary))
            }
        }
    }

    private fun changeThirdIconReceive(position: Int) {
        activity?.runOnUiThread {
            if (view != null) {
                val viewHolder: RecyclerView.ViewHolder? =
                    recyclerView.findViewHolderForAdapterPosition(position)
                viewHolder?.itemView?.findViewById<AppCompatImageView>(R.id.imageView_tickThird)
                    ?.setImageResource(R.drawable.ic_round_done_all_24px)

                viewHolder?.itemView?.findViewById<AppCompatImageView>(R.id.imageView_tickThird)
                    ?.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorPrimary))
            }
        }
    }


    private fun changeSecondIconReceive(position: Int) {
        activity?.runOnUiThread {
            if (view != null) {
                val viewHolder: RecyclerView.ViewHolder? =
                    recyclerView.findViewHolderForAdapterPosition(position)

                viewHolder?.itemView?.findViewById<AppCompatImageView>(R.id.imageView_tickFirst)
                    ?.setImageResource(R.drawable.ic_round_done_all_24px)

                viewHolder?.itemView?.findViewById<AppCompatImageView>(R.id.imageView_tickFirst)
                    ?.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorPrimary))
            }
        }
    }


    override fun onThreadRemoveParticipant(response: ChatResponse<ResultParticipant>?) {
        super.onThreadRemoveParticipant(response)


        if (response?.uniqueId == fucCallback[ConstantMsgType.REMOVE_PARTICIPANT]) {

            val pos = getPositionOf(ConstantMsgType.REMOVE_PARTICIPANT)

            changeIconReceive(pos)

            changeFunThreeState(pos, Method.DONE)

        }


    }

    override fun onRemoveContact(response: ChatResponse<ResultRemoveContact>?) {
        super.onRemoveContact(response)
        if (fucCallback[ConstantMsgType.REMOVE_CONTACT] == response?.uniqueId) {
            val position = 9
            changeIconReceive(position)
            changeFunTwoState(position, Method.DONE)

        }
    }

    override fun onGetContact(response: ChatResponse<ResultContact>?) {
        super.onGetContact(response)

        val contactList = response?.result?.contacts

        if (fucCallback[ConstantMsgType.GET_PARTICIPANT] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.GET_PARTICIPANT)
            changeFunOneState(pos,Method.DONE)
            changeFunTwoState(pos,Method.RUNNING)
            handleGetParticipant(contactList)
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)

            changeFunOneState(pos,Method.DONE)

            changeFunTwoState(pos,Method.RUNNING)

            handleCrtThreadForwMsg(contactList)
        }
        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_MSG] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_MSG)

            changeFunOneState(pos, Method.DONE)

            changeFunTwoState(pos, Method.RUNNING)

            changeFunThreeState(pos, Method.RUNNING)

            prepareCreateThreadWithMsg(contactList)
        }

        if (fucCallback[GET_HISTORY] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.GET_HISTORY)

            changeFunOneState(pos, Method.DONE)

            changeFunTwoState(pos, Method.RUNNING)

            handleGetHistory(contactList)
        }

        if (fucCallback[ConstantMsgType.EDIT_MESSAGE] == response?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.EDIT_MESSAGE)

            changeFunOneState(position, Method.DONE)

            changeFunTwoState(position, Method.RUNNING)

            handleEditMessage(contactList)
        }

        if (fucCallback[ConstantMsgType.DELETE_MESSAGE] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.DELETE_MESSAGE)

            changeFunOneState(pos, Method.DONE)

            changeFunTwoState(pos, Method.RUNNING)

            handleDeleteMessage(contactList)
        }

        if (fucCallback[ConstantMsgType.UNMUTE_THREAD] == response?.uniqueId) {
            val position = 16
            changeFunOneState(position, Method.DONE)
            changeFunTwoState(position, Method.RUNNING)
            handleUnmuteThread(contactList)
        }

        if (fucCallback[ConstantMsgType.MUTE_THREAD] == response?.uniqueId) {

            val position = 15
            changeFunOneState(position, Method.DONE)
            changeFunTwoState(position, Method.RUNNING)
            handleMuteThread(contactList)
        }

        if (fucCallback[ConstantMsgType.LEAVE_THREAD] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.LEAVE_THREAD)
            changeFunOneState(pos, Method.DONE)
            changeFunTwoState(pos, Method.RUNNING)

            handleLeaveThread(contactList)
        }

        if (fucCallback[ConstantMsgType.REPLY_MESSAGE] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.REPLY_MESSAGE)
            changeFunOneState(pos, Method.DONE)
            changeFunTwoState(pos, Method.RUNNING)
            handleReplyMessage(contactList)
        }

        if (fucCallback[ConstantMsgType.SEND_MESSAGE] == response?.uniqueId) {
//            fucCallback.remove(ConstantMsgType.SEND_MESSAGE)
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
            changeFunOneState(1, Method.DONE)

            methods[position].methodNameFlag = true
            var json = gson.toJson(response?.result)
            methods[position].log = json
        }

        if (fucCallback[ConstantMsgType.BLOCK_CONTACT] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.BLOCK_CONTACT)

            val pos = getPositionOf(ConstantMsgType.BLOCK_CONTACT)
            changeFunOneState(pos, Method.DONE)
            changeFunTwoState(pos, Method.RUNNING)

            handleBlockContact(contactList)
        }

        if (fucCallback[ConstantMsgType.UPDATE_CONTACT] == response?.uniqueId) {

            fucCallback.remove(ConstantMsgType.UPDATE_CONTACT)
            val pos = getPositionOf(ConstantMsgType.UPDATE_CONTACT)
            changeFunOneState(pos, Method.DONE)
            changeFunTwoState(pos, Method.RUNNING)

            handleUpdateContact(contactList)
        }

//        if (fucCallback[ConstantMsgType.UNBLOCK_CONTACT] == response?.uniqueId) {
//            fucCallback.remove(ConstantMsgType.UNBLOCK_CONTACT)
//            handleUnBlockContact(contactList)
//        }
        if (fucCallback[ConstantMsgType.ADD_PARTICIPANT] == response?.uniqueId) {
            fucCallback.remove(ConstantMsgType.ADD_PARTICIPANT)
            val pos = getPositionOf(ConstantMsgType.ADD_PARTICIPANT)

            changeFunOneState(pos, Method.DONE)
            changeFunTwoState(pos, Method.RUNNING)

            handleAddParticipant(contactList)
        }
//        if (fucCallback[ConstantMsgType.REMOVE_PARTICIPANT] == response?.uniqueId) {
//            fucCallback.remove(ConstantMsgType.REMOVE_PARTICIPANT)
//            requestRemoveParticipant(contactList)
//        }
        if (fucCallback[ConstantMsgType.FORWARD_MESSAGE] == response?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.FORWARD_MESSAGE)
            changeFunOneState(pos, Method.DONE)
            changeFunTwoState(pos, Method.RUNNING)
            handleForward(contactList)
            // CREATE WITH MESSAGE CONTACT
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
                        inviteList.add(Invitee(contactId, InviteType.Constants.TO_BE_USER_CONTACT_ID))

                        val list = Array(1) { Invitee(inviteList[0].id, inviteList[0].idType) }

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
     * Its created thread and stored another contact id (because its needed that contact id in order to
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
                        inviteList.add(Invitee(contactId, 2))

                        val list = Array(1) { Invitee(inviteList[0].id, 2) }

                        val uniqueId = mainViewModel.createThread(
                            ThreadType.Constants.NORMAL, list, "nothing", ""
                            , "", ""
                        )

                        fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] = uniqueId
                        break
                    }
                    if (choose == 1) {
                        fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG_CONTCT_ID] =
                            contact.id.toString()
                    }
                    choose++
                }
            }

            if(choose<2){




                showToast("You Have Not Enough Contact With User Id")

                val pos = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)

                changeFunTwoState(pos, Method.DEACTIVE)

                changeIconReceive(pos)
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

                    val contactId = contact.id
                    val inviteList = Array<Invitee>(1) { Invitee(contactId, 2) }
                    inviteList[0].id = contactId

                    val uniqueId = mainViewModel.createThread(
                        ThreadType.Constants.NORMAL, inviteList, "", ""
                        , "", ""
                    )
                    fucCallback[ConstantMsgType.GET_HISTORY] = uniqueId
                    choose++
                    break

                }
            }

            if (choose==1){


                showToast("You Have No Contact With User Id")

                val pos = getPositionOf(ConstantMsgType.GET_HISTORY)

                changeFunTwoState(pos, Method.DEACTIVE)

                changeIconReceive(pos)
            }
        }
    }

    private fun handleEditMessage(contactList: ArrayList<Contact>?) {

        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    choose++
                    val contactId = contact.id
                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 2))
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage.Builder().message(faker.music().genre())
                            .build()
                    val requestCreateThread: RequestCreateThread =
                        RequestCreateThread.Builder(0, inviteList)
                            .message(requestThreadInnerMessage)
                            .build()
                    val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
                    fucCallback[ConstantMsgType.EDIT_MESSAGE_ID] = uniqueId!![1]
                    break
                }
            }

            if (choose == 1) {

                showToast("You Have No Contact With User Id")

                val pos = getPositionOf(ConstantMsgType.EDIT_MESSAGE)

                changeFunTwoState(pos, Method.DEACTIVE)

                changeIconReceive(pos)

            }
        }
    }

    private fun handleDeleteMessage(contactList: ArrayList<Contact>?) {

        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id
                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 2))
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage
                            .Builder()
                            .message(faker.music().genre())
                            .build()
                    val requestCreateThread: RequestCreateThread =
                        RequestCreateThread
                            .Builder(0, inviteList)
                            .message(requestThreadInnerMessage)
                            .build()
                    val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
                    fucCallback[ConstantMsgType.DELETE_MESSAGE] = uniqueId!![0]
                    fucCallback[ConstantMsgType.DELETE_MESSAGE_ID] = uniqueId[1]
                    choose++
                    break
                }
            }

            if (choose == 0) {


                showToast("You Have No User")

                val pos = getPositionOf(ConstantMsgType.DELETE_MESSAGE)

                changeFunTwoState(pos, Method.DEACTIVE)

                changeIconReceive(pos)


            }
        }
    }

    private fun handleUnmuteThread(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {

                    val contactId = contact.id
                    val inviteList = Array<Invitee>(1, { i -> Invitee(contactId, 2) })
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

    private fun handleMuteThread(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
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

    private fun handleLeaveThread(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id
                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 2))
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
        } else {
            showToast("You Have no contact")
            val pos = getPositionOf(ConstantMsgType.LEAVE_THREAD)
            changeIconReceive(pos)
            changeFunTwoState(pos, Method.DEACTIVE)
        }
    }

    private fun handleReplyMessage(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 2))
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



        if (fucCallback[ConstantMsgType.ADD_PARTICIPANT] == response?.uniqueId) {

            val position = 10
            changeIconReceive(position)
            changeFunThreeState(position, Method.DONE)


        }

    }

    private fun handleForward(contactList: ArrayList<Contact>?) {


        if (contactList != null) {


            var contactIdsList = ArrayList<Long>()

            val ivList = ArrayList<Invitee>()



            for (contact: Contact in contactList) {

                if (contactIdsList.size == 2) break

                if (contact.isHasUser) {

                    contactIdsList.add(contact.id)
                } else {

                    ivList.add(Invitee(contact.id, 2))

                }

            }


            if (contactIdsList.size > 0) {


                val inviteList = ArrayList<Invitee>()

                inviteList.add(Invitee(contactIdsList[0], 2))


                contactBIdType = 2

                val requestThreadInnerMessage =
                    RequestThreadInnerMessage.Builder().message(faker.music().genre())
                        .build()
                val requestCreateThread: RequestCreateThread =
                    RequestCreateThread.Builder(0, inviteList)
                        .message(requestThreadInnerMessage)
                        .build()
                val uniqueId =
                    mainViewModel.createThreadWithMessage(requestCreateThread)
//                fucCallback[ConstantMsgType.FORWARD_MESSAGE] = uniqueId!![0]
                fucCallback[ConstantMsgType.FORWARD_MESSAGE_ID] = uniqueId!![1]


                if (contactIdsList.size > 1) {

                    fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B] =
                        contactIdsList[1].toString()

                } else {


                    fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B] =
                        contactIdsList[0].toString()


                }


            } else if (ivList.size > 0) {

                contactBIdType = 2


                val requestThreadInnerMessage =
                    RequestThreadInnerMessage.Builder().message(faker.music().genre())
                        .build()
                val requestCreateThread: RequestCreateThread =
                    RequestCreateThread.Builder(0, ivList)
                        .message(requestThreadInnerMessage)
                        .build()
                val uniqueId =
                    mainViewModel.createThreadWithMessage(requestCreateThread)
//                fucCallback[ConstantMsgType.FORWARD_MESSAGE] = uniqueId!![0]
                fucCallback[ConstantMsgType.FORWARD_MESSAGE_ID] = uniqueId!![1]


                if (ivList.size > 1) {

                    fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B] = ivList[1].id.toString()

                } else {


                    fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B] = ivList[0].id.toString()


                }


            }


//            for (contact: Contact in contactList) {
//
//                if (contact.isHasUser) {
//
//                    val contactId = contact.id
//
//                    val inviteList = ArrayList<Invitee>()
//
//                    inviteList.add(Invitee(contactId, 1))
//
//
//                    when (step) {
//
//
//                        1 -> {
//
//                            val requestThreadInnerMessage =
//                                RequestThreadInnerMessage.Builder().message(faker.music().genre())
//                                    .build()
//                            val requestCreateThread: RequestCreateThread =
//                                RequestCreateThread.Builder(0, inviteList)
//                                    .message(requestThreadInnerMessage)
//                                    .build()
//                            val uniqueId =
//                                mainViewModel.createThreadWithMessage(requestCreateThread)
//                            fucCallback[ConstantMsgType.FORWARD_MESSAGE] = uniqueId!![0]
//                            fucCallback[ConstantMsgType.FORWARD_MESSAGE_ID] = uniqueId[1]
//                            step++
//
//                        }
//
//                        2 ->{
//
//
//
//
//                        }
//                    }
//
//                }
//            }


        }
    }

    private fun handleAddParticipant(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val inviteList = Array(1) { Invitee(contactId, 2) }
                    inviteList[0].id = contactId

                    val uniqueId = mainViewModel.createThread(
                        ThreadType.Constants.PUBLIC_GROUP, inviteList, "nothing", ""
                        , "", ""
                    )
                    fucCallback[ConstantMsgType.ADD_PARTICIPANT] = uniqueId
                    choose++
                    if (choose == 1) {
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

        val position = getPositionOf(ConstantMsgType.REMOVE_CONTACT)

        changeIconSend(position)

        changeFunOneState(position, Method.RUNNING)

        fucCallback[ConstantMsgType.REMOVE_CONTACT] = mainViewModel.addContacts(requestAddContact)

    }

    private fun handleSendMessageResponse(contactList: ArrayList<Contact>?) {
        val position = getPositionOf(ConstantMsgType.SEND_MESSAGE)
        changeFunOneState(position, Method.DONE)
        changeFunTwoState(position, Method.RUNNING)
        changeFunThreeState(position, Method.RUNNING)

        var send = false

        if (contactList != null) {


            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 2))
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage.Builder().message(faker.music().genre()).build()
                    val requestCreateThread: RequestCreateThread =
                        RequestCreateThread.Builder(0, inviteList)
                            .message(requestThreadInnerMessage)
                            .build()
                    val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
                    fucCallback[ConstantMsgType.SEND_MESSAGE] = uniqueId!![0]
                    send = true
                    break
                }
            }
        }


        if (!send) {

            changeIconReceive(position)
            changeFunOneState(position, Method.DEACTIVE)
            changeFunTwoState(position, Method.DEACTIVE)
            changeFunThreeState(position, Method.DEACTIVE)

            showToast("You have neither a thread nor a contact to send message")


        }
    }

    private fun showToast(message: String) {

        Toast.makeText(
            context,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    //Response from getContact
    /**
     *
     * */
    private fun handleUnBlockContact(blockList: ChatResponse<ResultBlockList>?) {


        var targetContactId = 0L

        if (blockList != null) {


            if (blockList.result.contacts.isNotEmpty()) {


                targetContactId = blockList.result.contacts[0].id

                val requestUnBlock = RequestUnBlock.Builder()
                    .contactId(targetContactId)
                    .build()

                fucCallback[ConstantMsgType.UNBLOCK_CONTACT] = mainViewModel.unBlock(requestUnBlock)


            }


        }

        if (targetContactId == 0L) {


            Toast.makeText(context, "There is no contact in block list", Toast.LENGTH_LONG).show()

            val pos = getPositionOf(ConstantMsgType.UNBLOCK_CONTACT)
            changeIconReceive(pos)
            changeFunTwoState(pos, Method.DEACTIVE)


        }


//        if (contactList != null) {
//            for (contact: Contact in contactList) {
//                if (contact.isHasUser) {
//                    val contactId = contact.id
//
//                    val requestBlock = RequestBlock.Builder()
//                            .contactId(contactId)
//                            .build()
//                    val uniqueId = mainViewModel.blockContact(requestBlock)
//                    fucCallback[ConstantMsgType.UNBLOCK_CONTACT] = uniqueId
//
//                    break
//                }
//            }
//        }
    }

    private fun handleUpdateContact(contactList: ArrayList<Contact>?) {

        var targetContactId = 0L

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
                    break
                }
            }
        }


        if (targetContactId == 0L) {


            Toast.makeText(context, "You Have no valid contact to update", Toast.LENGTH_LONG).show()

            val position = getPositionOf(ConstantMsgType.UPDATE_CONTACT)

            changeIconReceive(position)

            changeFunOneState(position, Method.DEACTIVE)

            return

        }
    }


    private fun handleBlockContact(contactList: ArrayList<Contact>?) {


        if (contactList != null) {

            var targetContactId = 0L



            for (contact: Contact in contactList) {


                if (contact.isHasUser) {


                    targetContactId = contact.id

                    break
                }
            }



            if (targetContactId == 0L) {


                Toast.makeText(context, "You Have No Contact with user", Toast.LENGTH_LONG)
                    .show()

                val pos = getPositionOf(ConstantMsgType.BLOCK_CONTACT)

                changeIconReceive(pos)

                changeFunTwoState(pos, Method.DEACTIVE)

                return
            }

            val requestBlock = RequestBlock.Builder()
                .contactId(targetContactId)
                .build()


            val uniqueId = mainViewModel.blockContact(requestBlock)


            fucCallback[ConstantMsgType.BLOCK_CONTACT] = uniqueId


        }
    }

    private fun getParticipant() {

        val pos = getPositionOf(ConstantMsgType.GET_PARTICIPANT)
        changeIconSend(pos)
        changeFunOneState(pos,Method.RUNNING)
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.GET_PARTICIPANT] = mainViewModel.getContact(requestGetContact)
    }

    //Get Thread
    // If there is no Thread
    // Its create Thread with someone that has userId
    // Then send Message to that thread
    private fun sendTextMsg() {

        val position = getPositionOf(ConstantMsgType.SEND_MESSAGE)

        changeIconSend(position)
        changeFunOneState(position, Method.RUNNING)
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
        changeFunOneState(position, Method.RUNNING)

//        val requestMuteThread = RequestMuteThread.Builder(threadId).build()
//        mainViewModel.unMuteThread(requestMuteThread)
    }

    private fun muteThread() {

        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.MUTE_THREAD] = mainViewModel.getContact(requestGetContact)
        val position = 15
        changeIconSend(position)
        changeFunOneState(position, Method.RUNNING)
    }

    private fun leaveThread() {

        val pos = getPositionOf(ConstantMsgType.LEAVE_THREAD)
        changeIconSend(pos)

        changeFunOneState(pos, Method.RUNNING)


        val requestGetContact = RequestGetContact.Builder().build()

        fucCallback[ConstantMsgType.LEAVE_THREAD] = mainViewModel.getContact(requestGetContact)
    }

    private fun removeParticipant() {

        val position = 11
        changeIconSend(position)
        changeFunOneState(position, Method.RUNNING)
        val requestThread = RequestThread.Builder().build()
        fucCallback[ConstantMsgType.REMOVE_PARTICIPANT] = mainViewModel.getThread(requestThread)


    }

    private fun blockContact() {

        changeIconSend(2)
        changeFunOneState(position = 2, state = Method.RUNNING)
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
        changeFunOneState(position, Method.RUNNING)
    }

    private fun getContact() {
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        val uniqueId = mainViewModel.getContact(requestGetContact)
        fucCallback[ConstantMsgType.GET_CONTACT] = uniqueId
        var position = 1
        changeIconSend(position)
        changeFunOneState(1, Method.RUNNING)
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

        val pos = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)

        changeIconSend(pos)
        changeFunOneState(pos,Method.RUNNING)
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] =
            mainViewModel.getContact(requestGetContact)

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

        changeFunOneState(22, Method.RUNNING)


        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        val uniqueId = mainViewModel.getThread(requestGetThreads)

        fucCallback[ConstantMsgType.CLEAR_HISTORY] = uniqueId


    }

    private fun createThreadWithMessage() {

        changeIconSend(27)

        changeFunOneState(27, Method.RUNNING)

        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()

        fucCallback[ConstantMsgType.CREATE_THREAD_WITH_MSG] =
            mainViewModel.getContact(requestGetContact)


    }

    private fun deleteMultipleMessage() {

        changeIconSend(26)

        changeFunOneState(26, Method.RUNNING)

        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        val uniqueId = mainViewModel.getThread(requestGetThreads)

        fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] = uniqueId


    }


    private fun getAdminsList() {


        changeIconSend(23)


        changeFunOneState(23, Method.RUNNING)


        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        fucCallback[ConstantMsgType.GET_ADMINS_LIST] = mainViewModel.getThread(requestGetThreads)


//        // positionUniqueIds[23] = ArrayList()
//
//        // positionUniqueIds[23]?.add(fucCallback[ConstantMsgType.GET_ADMINS_LIST]!!)

    }

    private fun removeAdminRoles() {


        changeIconSend(25)

        changeFunOneState(25, Method.RUNNING)

        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        val uniqueId = mainViewModel.getThread(requestGetThreads)

        fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] = uniqueId


    }


    private fun addAdminRoles() {

        changeIconSend(24)

        changeFunOneState(24, Method.RUNNING)

        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        val uniqueId = mainViewModel.getThread(requestGetThreads)

        fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] = uniqueId


    }

    private fun changeFunOneState(position: Int, state: Int) {

        if (chatReady)

            functionAdapter.changeFuncOneStateAtPosition(position, state)
    }

    private fun changeFunTwoState(position: Int, state: Int) {

        if (chatReady)

            functionAdapter.changeFuncTwoStateAtPosition(position, state)

    }


    private fun changeFunThreeState(pos: Int, state: Int) {

        if (chatReady)

            functionAdapter.changeFuncThreeStateAtPosition(pos, state)


    }


    private fun changeFunFourState(pos: Int, state: Int) {

        if (chatReady)

            functionAdapter.changeFuncFourStateAtPosition(pos, state)


    }


    private fun requestRemoveParticipant(response: ChatResponse<ResultParticipant>?) {


        val participants = response?.result?.participants!!

        var targetParticipant: Participant? = null

        if (participants.size > 0) {


            for (participant in participants) {


                if (!participant.admin) {


                    targetParticipant = participant

                }
            }

            if (targetParticipant == null) {

                if (participants.size > 1) {

                    targetParticipant = participants[0]

                }

            }

            if (targetParticipant != null) {


                val parIdList = ArrayList<Long>()

                parIdList.add(targetParticipant.id)

                val requestRemoveParticipant =
                    RequestRemoveParticipants.Builder(response.subjectId, parIdList)
                        .build()

                fucCallback[ConstantMsgType.REMOVE_PARTICIPANT] =
                    mainViewModel.removeParticipant(requestRemoveParticipant)


            } else {

                Toast.makeText(
                    context,
                    "There is no participant in this thread. Please add someone",
                    Toast.LENGTH_LONG
                )
                    .show()


                setErrorOnFunctionInPosition(getPositionOf(ConstantMsgType.REMOVE_PARTICIPANT))
                changeIconReceive(getPositionOf(ConstantMsgType.REMOVE_PARTICIPANT))


            }


        } else {

            Toast.makeText(
                context,
                "There is no participant in this thread. Please add someone",
                Toast.LENGTH_LONG
            )
                .show()


            setErrorOnFunctionInPosition(getPositionOf(ConstantMsgType.REMOVE_PARTICIPANT))
            changeIconReceive(getPositionOf(ConstantMsgType.REMOVE_PARTICIPANT))


        }


//        if (contactList != null) {
//            for (contact: Contact in contactList) {
//                if (contact.isHasUser) {
//                    val contactId = contact.id
//                    val inviteList = ArrayList<Invitee>()
//                    inviteList.add(Invitee(contactId, 1))
//                    val requestThreadInnerMessage =
//                        RequestThreadInnerMessage.Builder().message(faker.music().genre()).build()
//                    val requestCreateThread: RequestCreateThread =
//                        RequestCreateThread.Builder(0, inviteList)
//                            .message(requestThreadInnerMessage)
//                            .build()
//
////                    val uniqueId = mainViewModel.createThread(requestCreateThread)
////                    fucCallback[ConstantMsgType.CREATE_THREAD] = uniqueId
//                    break
//                }
//            }
//        }
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
                    inviteList.add(Invitee(contactId, 2))
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
        val pos = getPositionOf(ConstantMsgType.UNBLOCK_CONTACT)
        changeIconSend(pos)
        changeFunOneState(pos, Method.RUNNING)
        val requestBlockList = RequestBlockList.Builder().build()
        fucCallback[ConstantMsgType.UNBLOCK_CONTACT] = mainViewModel.getBlockList(requestBlockList)
    }

    private fun updateContact() {
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.UPDATE_CONTACT] = mainViewModel.getContact(requestGetContact)
        val position = 7
        changeIconSend(position)
        changeFunOneState(position, Method.RUNNING)
    }

    private fun getBlockList() {

        val pos = getPositionOf(ConstantMsgType.GET_BLOCK_LIST)
        changeIconSend(pos)
        changeFunOneState(pos, Method.RUNNING)
        val requestBlockList = RequestBlockList.Builder().build()

        fucCallback[ConstantMsgType.GET_BLOCK_LIST] = mainViewModel.getBlockList(requestBlockList)
    }

    private fun getThread() {

        val requestThread = RequestThread.Builder().build()

        fucCallback[ConstantMsgType.GET_THREAD] = mainViewModel.getThread(requestThread)

        val position = 4

        changeIconSend(position)

        changeFunOneState(position, Method.RUNNING)

    }

    private fun scroll(position: Int) {
        recyclerViewSmooth.targetPosition = position
        linearLayoutManager.startSmoothScroll(recyclerViewSmooth)
    }

    private fun changeIconReceive(position: Int) {


        methods[position].methodNameFlag = true



        activity?.runOnUiThread {
            functionAdapter.deActivateFunction(position)
        }

//        activity?.runOnUiThread {
//            val viewHolder: RecyclerView.ViewHolder? = recyclerView.findViewHolderForAdapterPosition(position)
//            viewHolder?.itemView?.findViewById<ProgressBar>(R.id.progressMethod)?.visibility = View.GONE
////            viewHolder?.itemView?.findViewById<AppCompatImageView>(R.id.checkBox_ufil)
////                ?.setImageResource(R.drawable.ic_round_done_all_24px)
////            viewHolder?.itemView?.findViewById<AppCompatImageView>(R.id.checkBox_ufil)
////                ?.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorPrimary))
//        }
    }

    /* visibility of progress bar*/
    private fun changeIconSend(position: Int) {

//        activity?.runOnUiThread {
//
//            val viewHolder: RecyclerView.ViewHolder = recyclerView.findViewHolderForAdapterPosition(position)!!
//            viewHolder.itemView.findViewById<ProgressBar>(R.id.progressMethod).visibility = View.VISIBLE
//
////            viewHolder.itemView.findViewById<AppCompatImageView>(R.id.checkBox_ufil)
////                .setImageResource(R.drawable.ic_round_done_all_24px)
//        }
//
//
//

        if (chatReady)
            activity?.runOnUiThread {

                methods[position].methodNameFlag = false
                methods[position].funcOneFlag = false
                methods[position].funcTwoFlag = false
                methods[position].funcThreeFlag = false
                methods[position].funcFourFlag = false
                methods[position].funcOneState = Method.DEACTIVE
                methods[position].funcTwoState = Method.DEACTIVE
                methods[position].funcThreeState = Method.DEACTIVE
                methods[position].funcFourState = Method.DEACTIVE



                methods[position].log = ""

                functionAdapter.activateFunction(position)


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
        changeFunOneState(position, Method.RUNNING)
    }

    private fun forwardMessage() {
        val pos = getPositionOf(ConstantMsgType.FORWARD_MESSAGE)
        changeIconSend(pos)
        changeFunOneState(pos, Method.RUNNING)
        val requestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.FORWARD_MESSAGE] = mainViewModel.getContact(requestGetContact)


        //get contact
        //createThread with message with that first contact
        //createThread with second contact

        //forward the message from first thread to second thread

        // if the sent type come then its sent

    }

    private fun replyMessage() {
        val pos = getPositionOf(ConstantMsgType.REPLY_MESSAGE)

        changeIconSend(pos)
        changeFunOneState(pos, Method.RUNNING)

        val requestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.REPLY_MESSAGE] = mainViewModel.getContact(requestGetContact)

        //getContact
        //CreateThread with message
        //get that message id and thread id and call reply Message
    }


    override fun onLogEventWithName(logName: String, json: String) {
        super.onLogEventWithName(logName, json)

        val chatMessage: ChatMessage = gson.fromJson(json, ChatMessage::class.java)

        val uniqueId: String = chatMessage.uniqueId ?: return

        Log.d("LTAG", ">> NEW LOG: Name: $logName === Unique Id: $uniqueId ")


//        dataKeeper.first.add(logName)
//
//        dataKeeper.second.add(json)
//
//        dataKeeper.third.add(uniqueId)


        Log.d("LTAG", "::::::: positionUniqueIds is: $positionUniqueIds")

        positionUniqueIds.forEach { (k, v) ->

            if (v.contains(uniqueId)) {

                Log.d("LTAG", ">>>>> FOUND Unique id in PUIDS: $uniqueId for position: $k")

            }


        }


//        Log.d("MTAG","::::::: uniqueIdsLogName =======> $uniqueIdsLogName")


//


        uniqueIdsLogName[uniqueId] = mapOf(logName to json)

        // positionUniqueIds.forEach { (key, value) ->


//
//            if(value.contains(uniqueId)){
//
//                addLogsOfFunctionAtPosition(logName,json,key)
//
//
//
//
//
//
//            }
//
//
//
//        }

//
//        fucCallback.forEach {
//
//            if (it.value == uniqueId) {
//
//                addLogsOfFunctionAtPosition(logName,json,getPositionOf(it.key))
//
//            }
//
//        }
//
//        fucCallbacks.forEach {
//
//            if (it.value.contains(uniqueId)) {
//
//                addLogsOfFunctionAtPosition(logName,json,getPositionOf(it.key))
//
//            }
//
//        }


    }

    class Variable<T>(private val defaultValue: T) {
        var value: T = defaultValue
            set(value) {
                field = value
                observable.onNext(value)
            }
        val observable = BehaviorSubject.create(value)
    }


    class MapVariable<K, V> : HashMap<K, V>() {


        override fun put(key: K, value: V): V? {

            observableKeys.onNext(keys)

            observableVals.onNext(values)

            oKey.onNext(key)

            oValue.onNext(value)


            onInsertObserver.onNext(Pair(first = key, second = value))


            return super.put(key, value)


        }


        val observableKeys: BehaviorSubject<Set<K>> = BehaviorSubject.create(keys)

        val observableVals: BehaviorSubject<Collection<V>> = BehaviorSubject.create(values)


        val oKey: BehaviorSubject<K> = BehaviorSubject.create()

        val oValue: BehaviorSubject<V> = BehaviorSubject.create()

        val onInsertObserver: BehaviorSubject<Pair<K, V>> = BehaviorSubject.create()


    }


}

