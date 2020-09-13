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
import com.fanap.podchat.chat.mention.model.RequestGetMentionList
import com.fanap.podchat.chat.pin.pin_message.model.RequestPinMessage
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage
import com.fanap.podchat.chat.pin.pin_thread.model.RequestPinThread
import com.fanap.podchat.chat.pin.pin_thread.model.ResultPinThread
import com.fanap.podchat.chat.thread.public_thread.RequestCheckIsNameAvailable
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread
import com.fanap.podchat.chat.thread.public_thread.ResultIsNameAvailable
import com.fanap.podchat.chat.user.profile.RequestUpdateProfile
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles
import com.fanap.podchat.mainmodel.*
import com.fanap.podchat.model.*
import com.fanap.podchat.requestobject.*
import com.fanap.podchat.util.InviteType
import com.fanap.podchat.util.TextMessageType
import com.fanap.podchat.util.ThreadType
import com.github.javafaker.Faker
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wang.avi.AVLoadingIndicatorView
import ir.fanap.chattestapp.R
import ir.fanap.chattestapp.application.MyApp
import ir.fanap.chattestapp.application.ui.MainViewModel
import ir.fanap.chattestapp.application.ui.TestListener
import ir.fanap.chattestapp.application.ui.log.SpecificLogFragment
import ir.fanap.chattestapp.application.ui.util.ConstantMsgType
import ir.fanap.chattestapp.application.ui.util.ConstantMsgType.Companion.GET_HISTORY
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodFuncFour
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodFuncOne
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodFuncThree
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodFuncTwo
import ir.fanap.chattestapp.application.ui.util.MethodList.Companion.methodNames
import ir.fanap.chattestapp.application.ui.util.SmartHashMap
import ir.fanap.chattestapp.application.ui.util.TokenFragment
import ir.fanap.chattestapp.bussines.model.LogClass
import ir.fanap.chattestapp.bussines.model.Method
import kotlinx.android.synthetic.main.fragment_function.*
import kotlinx.android.synthetic.main.search_contacts_bottom_sheet.*
import kotlinx.android.synthetic.main.search_log_bottom_sheet.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random

class FunctionFragment : Fragment(),
    FunctionAdapter.ViewHolderListener,
    TestListener {


    private var nextSearchPosition: Int = 0
    private var searchInMethodsResults: ArrayList<Int> = ArrayList()

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
    private var fucCallback: SmartHashMap<String, String> = SmartHashMap()
    private val positionUniqueIds: HashMap<Int, ArrayList<String>> = HashMap()
    private val uniqueIdLogName: HashMap<String, ArrayList<String>> = HashMap()
    private val uniqueIdLog: HashMap<String, ArrayList<String>> = HashMap()

    //    private val listOfLogs: ArrayList<LogClass> = ArrayList()
    private var positionLogs: HashMap<Int, ArrayList<LogClass>> = HashMap()


//    private val logObservable = SmartHashMap(fucCallback)

    private var fucCallbacks: HashMap<String, ArrayList<String>> = hashMapOf()
    private lateinit var textView_state: TextView
    private lateinit var textView_log: TextView
    private lateinit var functionAdapter: FunctionAdapter
    private var mainServer = false
    private val faker: Faker = Faker()


    private var TOKEN = "128ffece74294fc4b269805b09a2bf49"


    private val ssoHost = MyApp.getInstance().getString(R.string.sandbox_ssoHost)
    private val serverName = "chat-server"
    private val sand_appId = "POD-Chat"
    private val typeCode: String? = null


    private val threadsList: ArrayList<Thread> = arrayListOf()

    private val contactList: ArrayList<Contact> = arrayListOf()

    private val blockedList: ArrayList<BlockedContact> = arrayListOf()


    /**
     *
     * SandBox Config:
     *
     */

    private val sandSocketAddress = MyApp.getInstance().getString(R.string.sandbox_socketAddress)
    private val sandPlatformHost = MyApp.getInstance().getString(R.string.sandbox_platformHost)
    private val sandFileServer = MyApp.getInstance().getString(R.string.sandbox_fileServer)


    /**
     *
     * MainServer Config:
     *
     *
     */


    private val main_socketAddress = MyApp.getInstance().getString(R.string.socketAddress)
    private val main_platformHost = MyApp.getInstance().getString(R.string.platformHost)
    private val main_fileServer = MyApp.getInstance().getString(R.string.fileServer)
    private val podSpaceUrl = MyApp.getInstance().getString(R.string.podspace_file_server_main)


    /**
     * LOCAL Mehdi Sheikh Hosseini
     */

//    works:
//    private val socketAddress =
//        MyApp.getInstance().getString(R.string.integration_serverName) // {**REQUIRED**} Socket Address
//    private val platformHost = MyApp.getInstance().getString(R.string.platformHost)
//    private val fileServer =
//        MyApp.getInstance().getString(R.string.fileServer) // {**REQUIRED**} File Server Address


    companion object {

        private const val TAG = "TEST_APP"


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
        imageButtonNextResult.setOnClickListener {


            showNextSearchResult()

        }
        imageButtonPreviousResult.setOnClickListener {
            showPreviousSearchResult()
        }
        imageCloseSearchResult.setOnClickListener {
            closeSearchResult()
        }

    }

    private fun showPreviousSearchResult() {

        if (nextSearchPosition > 0)
            nextSearchPosition--

        focusOnNextSearchResult()

    }

    private fun showNextSearchResult() {

        if (nextSearchPosition < searchInMethodsResults.size - 1)
            nextSearchPosition++

        focusOnNextSearchResult(next = true)

    }

    private fun focusOnNextSearchResult(next: Boolean = false) {


        /**
         * *
         *
         * next = true means that we are scrolling to next position, not previous.
         *
         * because next item hides behind bottom menu, we plus 2 to the next position.
         *
         * so our item comes up and shown in middle of screen.
         *
         *
         * */


        val lastHideTime = Date().time


        activity?.runOnUiThread {

            tvNextResult.text = (nextSearchPosition + 1).toString()

            functionAdapter.changeSearched(searchInMethodsResults[nextSearchPosition], true)

            if (next)
                recyclerView.smoothScrollToPosition(searchInMethodsResults[nextSearchPosition] + 2)
            else {
                recyclerView.smoothScrollToPosition(searchInMethodsResults[nextSearchPosition])
            }


            Handler().postDelayed({

                val currentTime = Date().time


                if (currentTime - lastHideTime > 3000)
                    hideLastSearchedResults()


            }, 3000)


        }

    }

    private fun hideLastSearchedResults() {


        /**
         *
         * when we request for next result, if last result is highlighted,
         *
         * it will remain highlighted until we hide it all.
         *
         *
         * */

        for (index in searchInMethodsResults.indices) {


            functionAdapter.changeSearched(searchInMethodsResults[index], false)

            if (index == nextSearchPosition)
                break


        }

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


        searchInMethodsResults.clear()

        var found = false


        //remove spaces for better search


        val queryS = query.replace(" ", "")

        for (methodIndex in methodNames.indices) {

            val methodName = methodNames[methodIndex].replace(" ", "")


            Log.d("MTAG", "Query: $queryS method: $methodName")

            if (methodName.contains(queryS, ignoreCase = true)) {

                searchInMethodsResults.add(methodIndex)

                found = true

            }


        }

        if (!found) {


            showToast("Nothing found!")

            return
        }

        tvSearchQuery.text = query


        hideKeyboard(context, view!!)

        showSearchResult()

        bottomSheetSearch.state = BottomSheetBehavior.STATE_COLLAPSED

        nextSearchPosition = 0

        tvNextResult.text = (nextSearchPosition + 1).toString()

        tvResultCount.text = searchInMethodsResults.size.toString()

        focusOnNextSearchResult()


    }

    private fun showSearchResult() {

        layoutSearchResult.animate()
            .setStartDelay(200)
            .setDuration(250)
            .setInterpolator(LinearInterpolator())
            .translationY(0f)
            .alpha(1f)
            .withStartAction {

                layoutSearchResult.visibility = View.VISIBLE
            }
            .start()

    }

    private fun closeSearchResult() {


        layoutSearchResult.animate()
            .setDuration(250)
            .setInterpolator(LinearInterpolator())
            .translationY(-200f)
            .alpha(0f)
            .withStartAction {

                hideAllSearchResult()

            }
            .withEndAction {

                searchInMethodsResults.clear()


                layoutSearchResult.visibility = View.GONE
            }
            .start()

    }

    private fun hideAllSearchResult() {


        for (searchedIndex in searchInMethodsResults) {

            functionAdapter.changeSearched(searchedIndex, false)

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

                TOKEN = token

                if (textView_state.text.toString() == "ASYNC_READY") {

                    mainViewModel.setToken(TOKEN)

                }

                tokenFragment.dismiss()

            }

            override fun onOTPEntered(entry: String) {

                mainViewModel.verifyOTPCode(entry)

                tokenFragment.dismiss()
            }

            override fun onNumberEntered(entry: String) {

                mainViewModel.checkNumber(entry)

            }
        })


    }

    override fun onConnectWithOTP(token: String?) {

        TOKEN = token!!

        connect()


    }

    override fun onLogClicked(position: Int) {


        setLogsToPosition(position)


        val logs = positionLogs[position]

        val logFragment = SpecificLogFragment()

        val bundle = Bundle()

        bundle.putParcelableArrayList("LOGS", logs)

        logFragment.arguments = bundle

        logFragment.show(childFragmentManager, "LOG_FRAG")


    }


    override fun onIconClicked(clickedViewHolder: FunctionAdapter.ViewHolder) {

        val position = clickedViewHolder.adapterPosition


        runMethodAtPosition(position)


    }

    private fun runMethodAtPosition(position: Int) {


        removeErrorStateOnFunctionInPosition(position)

        positionUniqueIds[position] = ArrayList()

        recyclerView.smoothScrollToPosition(position)


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
            31 -> {

                spamOrReportThread()
            }
            32 -> {
                pinThreadUnPinThread()
            }
            33 -> {

                pinMessageUnpinMessage()

            }
            34 -> {

                getUserMentionList()

            }
            35 -> {

                getCurrentUserRoles()
            }
            36 -> {

                updateChatProfile()
            }
            37 -> {

                isNameAvailable()

            }
            38 -> {

                createPublicThread()
            }
            39 -> {

                GetHistoryByType()

            }
            40 -> {

                AddParticipantByType()

            }


        }
    }

    private fun GetHistoryByType() {
        Log.e("testtest", "GetHistoryByType")
        val pos = getPositionOf(ConstantMsgType.GET_HISTORYWITHMSGTYPE)
        changeIconSend(pos)
        changeFunOneState(pos, Method.RUNNING)

        val requestGetThread = RequestThread.Builder()
            .build()

        fucCallback[ConstantMsgType.GET_HISTORYWITHMSGTYPE] =
            mainViewModel.getThreads(requestGetThread)

    }

    //types = id , user id ,core userid
    private fun AddParticipantByType() {
        Log.e("testtest", "AddParticipantByType")
        val pos = getPositionOf(ConstantMsgType.ADD_PARTICIPANTBYTYPE)
        changeIconSend(pos)
        changeFunOneState(pos, Method.RUNNING)

        val requestGetThread = RequestThread.Builder()
            .build()

        fucCallback[ConstantMsgType.ADD_PARTICIPANTBYTYPE] =
            mainViewModel.getThreads(requestGetThread)

    }

    private fun createPublicThread() {

        val pos = getPositionOf(ConstantMsgType.CREATE_PUBLIC_THREAD)

        changeIconSend(pos)

        if (contactList.isNullOrEmpty()) {

            changeFunOneState(pos, Method.RUNNING)

            val requestGetContact: RequestGetContact = RequestGetContact
                .Builder()
                .build()

            fucCallback[ConstantMsgType.CREATE_PUBLIC_THREAD] =
                mainViewModel.getContact(requestGetContact)

        } else {

            changeFunOneState(pos, Method.DONE)

            changeFunTwoState(pos, Method.RUNNING)

            fucCallback[ConstantMsgType.CREATE_PUBLIC_THREAD] = createIsNameAvailableRequest()
        }


    }

    private fun isNameAvailable() {

        val pos = getPositionOf(ConstantMsgType.IS_NAME_AVAILABLE)

        changeIconSend(pos)

        changeFunOneState(pos, Method.RUNNING)

        fucCallback[ConstantMsgType.IS_NAME_AVAILABLE] = createIsNameAvailableRequest()


    }

    private fun createIsNameAvailableRequest(): String {

        val request = RequestCheckIsNameAvailable
            .Builder("Thread_Name_${Date().time}")
            .build()

        return mainViewModel.checkIsNameAvailable(request)
    }


    private fun updateChatProfile() {

        val pos = getPositionOf(ConstantMsgType.UPDATE_CHAT_PROFILE)

        changeIconSend(pos)

        changeFunOneState(pos, Method.RUNNING)


        val request: RequestUpdateProfile =
            RequestUpdateProfile.Builder("Updated by Test app at ${Date()}")
                .build()


        fucCallback[ConstantMsgType.UPDATE_CHAT_PROFILE] = mainViewModel.updateChatProfile(request)


    }


    private fun getCurrentUserRoles() {

        val pos = getPositionOf(ConstantMsgType.GET_CURRENT_USER_ROLES)

        changeIconSend(pos)

        changeFunOneState(pos, Method.RUNNING)


        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()


        fucCallback[ConstantMsgType.GET_CURRENT_USER_ROLES] =
            mainViewModel.getContact(requestGetContact)


//        val requestGetThread = RequestThread.Builder()
//            .build()
//
//        fucCallback[ConstantMsgType.GET_CURRENT_USER_ROLES] =
//            mainViewModel.getThread(requestGetThread)


    }

    private fun getUserMentionList() {

        val pos = getPositionOf(ConstantMsgType.GET_MENTION_LIST)

        changeIconSend(pos)

        changeFunOneState(pos, Method.RUNNING)

        val requestGetThread = RequestThread.Builder()
            .build()

        fucCallback[ConstantMsgType.GET_MENTION_LIST] =
            mainViewModel.getThreads(requestGetThread)

    }

    private fun pinMessageUnpinMessage() {

        val pos = getPositionOf(ConstantMsgType.PIN_UN_PIN_MESSAGE)

        changeIconSend(pos)

        changeFunOneState(pos, Method.RUNNING)

        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()

        fucCallback[ConstantMsgType.PIN_UN_PIN_MESSAGE] =
            mainViewModel.getContact(requestGetContact)


    }

    private fun pinThreadUnPinThread() {


        val pos = getPositionOf(ConstantMsgType.PIN_UN_PIN_THREAD)

        changeIconSend(pos)

        changeFunOneState(pos, Method.RUNNING)

        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()

        fucCallback[ConstantMsgType.PIN_UN_PIN_THREAD] = mainViewModel.getContact(requestGetContact)

    }

    private fun spamOrReportThread() {

        val pos = getPositionOf(ConstantMsgType.SPAM_THREAD)

        changeIconSend(pos)

        changeFunOneState(pos, Method.RUNNING)

        val requestGetThread: RequestThread = RequestThread.Builder().build()

        fucCallback[ConstantMsgType.SPAM_THREAD] = mainViewModel.getThreads(requestGetThread)


    }


    private fun getSearchQuery() {


        bottomSheetSearchContacts.state = BottomSheetBehavior.STATE_EXPANDED

    }

    private fun searchContact(query: String) {


        var searchContact = RequestSearchContact.Builder("0", "50")


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

        fucCallback[ConstantMsgType.GET_SEEN_LIST] = mainViewModel.getThreads(requestThread)


    }


    private fun getDeliverList() {

        changeIconSend(28)

        changeFunOneState(28, Method.RUNNING)

        val requestThread = RequestThread.Builder().build()

        fucCallback[ConstantMsgType.GET_DELIVER_LIST] = mainViewModel.getThreads(requestThread)


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

        mainServer = switchCompat_sandBox.isChecked

        switchCompat_sandBox.setOnCheckedChangeListener { _, isChecked ->
            mainServer = isChecked
        }


        val menuView = view.findViewById<View>(R.id.relativeMenu)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if ((linearLayoutManager.findLastVisibleItemPosition() + 1) == linearLayoutManager.itemCount) {
                    //hide bottom menu
                    hideBottomMenu(menuView)
                } else {
                    //show bottom menu
                    showBottomMenu(menuView)
                }
            }
        })

        return view
    }


    private fun showBottomMenu(menuView: View) {
        menuView.animate()
            .setDuration(250)
            .translationY(0f)
            .alpha(1f)
            .setInterpolator(LinearInterpolator())
            .start()
    }

    private fun hideBottomMenu(menuView: View) {
        menuView.animate()
            .setDuration(250)
            .translationY(100f)
            .alpha(0f)
            .setInterpolator(LinearInterpolator())
            .start()
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
            .doOnError {
                Log.e("CHAT_TEST_UI", "Message ${it.message} ")
            }
            .subscribe {


                try {
                    textView_state.text = it
                } catch (e: Exception) {
                    Log.e("CHAT_TEST_UI", "1")
                }



                if (it == "CHAT_READY") {

                    chatReady = true



                    avLoadingIndicatorView.visibility = View.GONE

                    try {
                        textView_state.setTextColor(
                            ContextCompat.getColor(
                                activity?.applicationContext!!,
                                R.color.green_active
                            )
                        )
                    } catch (e: Exception) {
                        Log.e("CHAT_TEST_UI", "2")

                    }


//                    buttonConnect.animate().scaleX(0.2f).setDuration(450).setInterpolator(BounceInterpolator()).start()

                } else {

                    chatReady = false

                    try {
                        textView_state.setTextColor(
                            ContextCompat.getColor(activity?.applicationContext!!, R.color.grey)
                        )
                    } catch (e: Exception) {
                        Log.e("CHAT_TEST_UI", "3")

                    }
                }


            }

//        mainViewModel.setupNotification(activity!!)

        fucCallback.onInsertObserver.subscribe { pair ->


            saveUniqueId(pair)


        }



        mainViewModel.setDownloadDire(context?.cacheDir)

        mainViewModel.setupNotification(activity!!)


    }

//    override fun onStop() {
//        super.onStop()
//        mainViewModel.closeChat()
//    }

//    override fun onResume() {
//        super.onResume()
//        connect()
//    }

    private fun saveUniqueId(pair: Pair<String, String>) {


        if (pair.second == "" || pair.second.length < 30) return


        if (positionUniqueIds[getPositionOf(pair.first)] == null) {

            positionUniqueIds[getPositionOf(pair.first)] = ArrayList()

        }

        positionUniqueIds[getPositionOf(pair.first)]?.add(pair.second)
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
        fucCallback[ConstantMsgType.EDIT_MESSAGE] = mainViewModel.getContact(requestGetContact)
    }

    private fun deleteMessage() {

        val pos = getPositionOf(ConstantMsgType.DELETE_MESSAGE)

        changeIconSend(pos)

        changeFunOneState(pos, Method.RUNNING)

        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()

        fucCallback[ConstantMsgType.DELETE_MESSAGE] = mainViewModel.getContact(requestGetContact)

    }

    override fun onCheckIsNameAvailable(response: ChatResponse<ResultIsNameAvailable>?) {
        super.onCheckIsNameAvailable(response)

        if (response?.uniqueId == fucCallback[ConstantMsgType.IS_NAME_AVAILABLE]) {

            val pos = getPositionOf(ConstantMsgType.IS_NAME_AVAILABLE)

            changeIconReceive(pos)

            changeFunOneState(pos, Method.DONE)

        }

        if (fucCallback[ConstantMsgType.CREATE_PUBLIC_THREAD] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.CREATE_PUBLIC_THREAD)

            changeFunTwoState(pos, Method.DONE)

            createPublicThreadNow(response?.result?.uniqueName)


        }

    }


    override fun onGetUserInfo(response: ChatResponse<ResultUserInfo>?) {
        super.onGetUserInfo(response)

        val uniqueId = response?.uniqueId

        if (uniqueId != null && uniqueId == fucCallback[ConstantMsgType.UPDATE_CHAT_PROFILE]) {

            val pos = getPositionOf(ConstantMsgType.UPDATE_CHAT_PROFILE)

            changeFunTwoState(pos, Method.DONE)

            changeIconReceive(pos)

        }


    }

    override fun onChatProfileUpdated(response: ChatResponse<ResultUpdateProfile>?) {
        super.onChatProfileUpdated(response)

        val uniqueId = response?.uniqueId

        if (uniqueId != null && uniqueId == fucCallback[ConstantMsgType.UPDATE_CHAT_PROFILE]) {

            val pos = getPositionOf(ConstantMsgType.UPDATE_CHAT_PROFILE)

            changeFunOneState(pos, Method.DONE)

            changeFunTwoState(pos, Method.RUNNING)

            fucCallback[ConstantMsgType.UPDATE_CHAT_PROFILE] = mainViewModel.getUserInfo()


        }


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


        blockedList.clear()
        blockedList.addAll(response?.result?.contacts!!)



        if (fucCallback[ConstantMsgType.GET_BLOCK_LIST] == response?.uniqueId) {

            var position = 5
            methods[position].methodNameFlag = true
            changeIconReceive(position)
            changeFunOneState(position, Method.DONE)

        }




        if (fucCallback[ConstantMsgType.UNBLOCK_CONTACT] == response.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.UNBLOCK_CONTACT)

            changeFunOneState(pos, Method.DONE)

            unBlockNow()


        }

//        addLogsOfFunctionAtPosition(jObj,getPositionOf(ConstantMsgType.GET_BLOCK_LIST))

    }

    override fun onError(chatResponse: ErrorOutPut?) {
        super.onError(chatResponse)

        showToast(chatResponse?.errorMessage!!)

        val uniqueId = chatResponse?.uniqueId

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

    }

    private fun getPositionOf(key: String): Int {


        return when (key) {

            "CREATE_THREAD" -> 0
            "CREATE_THREAD_CHANNEL" -> 0
            "CREATE_THREAD_CHANNEL_GROUP" -> 0
            "CREATE_THREAD_PUBLIC_GROUP" -> 0
            "CREATE_THREAD_OWNER_GROUP" -> 0


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

            "REPLY_MESSAGE_THREAD_ID" -> 13

            "REPLY_MESSAGE_ID" -> 13


            "LEAVE_THREAD" -> 14


            "MUTE_THREAD" -> 15


            "UNMUTE_THREAD" -> 16


            "DELETE_MESSAGE" -> 17


            "DELETE_MESSAGE_ID" -> 17


            "EDIT_MESSAGE" -> 18


            "GET_HISTORY" -> 19


            "CREATE_THREAD_WITH_FORW_MSG" -> 20

            "CREATE_THREAD_WITH_FORW_MSG_ID" -> 20


            "GET_PARTICIPANT" -> 21


            "CLEAR_HISTORY" -> 22


            "GET_ADMINS_LIST" -> 23


            "ADD_ADMIN_ROLES" -> 24


            "REMOVE_ADMIN_ROLES" -> 25


            "DELETE_MULTIPLE_MESSAGE" -> 26


            "CREATE_THREAD_WITH_MSG" -> 27


            "CREATE_THREAD_WITH_MSG_MESSAGE" -> 27


            "GET_DELIVER_LIST" -> 28


            "GET_SEEN_LIST" -> 29


            "SEARCH_CONTACT" -> 30

            "SPAM_THREAD" -> 31
            "SPAM_THREAD_MESSAGE" -> 31


            "PIN AND UNPIN THREAD" -> 32

            "PIN AND UNPIN MESSAGE" -> 33

            "GET_MENTION_LIST" -> 34

            "GET_CURRENT_USER_ROLES" -> 35

            "UPDATE_CHAT_PROFILE" -> 36

            "IS_NAME_AVAILABLE" -> 37

            "CREATE_PUBLIC_THREAD" -> 38

            "GET_HISTORYWITHMSGTYPE" -> 39
            "ADD_PARTICIPANTBYTYPE" -> 40

            else -> -1
        }


    }


    private fun setErrorOnFunctionInPosition(position: Int) {


        if (position == -1)
            return

        recyclerView.smoothScrollToPosition(position)

        functionAdapter.setErrorState(position, true)

        deactiveFunction(position)

        when (Method.RUNNING) {

            methods[position].funcOneState -> methods[position].funcOneState = Method.FAIL
            methods[position].funcTwoState -> methods[position].funcTwoState = Method.FAIL
            methods[position].funcThreeState -> methods[position].funcThreeState = Method.FAIL
            methods[position].funcFourState -> methods[position].funcFourState = Method.FAIL

        }


//        if(methods[position].funcOneState  == Method.RUNNING) methods[position].funcOneState  = Method.DONE
//        if(methods[position].funcTwoState  == Method.RUNNING) methods[position].funcTwoState  = Method.DONE
//        if(methods[position].funcThreeState == Method.RUNNING) methods[position].funcThreeState = Method.DONE
//        if(methods[position].funcFourState == Method.RUNNING) methods[position].funcFourState = Method.DONE

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

            val position = 6

            changeFunTwoState(position, Method.DONE)

            changeIconReceive(position)

            try {
                val unBlocked = response?.result?.contact

                blockedList.forEach { contact ->
                    if (contact.blockId == unBlocked?.blockId)
                        blockedList.remove(contact)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message)

            }
        }
    }

    override fun onGetThread(chatResponse: ChatResponse<ResultThreads>?) {
        super.onGetThread(chatResponse)

        updateThreadList(chatResponse)

        if (chatResponse?.uniqueId.isNullOrBlank()) return;

        if (fucCallback[ConstantMsgType.BLOCK_CONTACT] == chatResponse?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.BLOCK_CONTACT)

            changeFunTwoState(pos, Method.DONE)

            blockNow()

        }

        if (fucCallback[ConstantMsgType.SPAM_THREAD] == chatResponse?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.SPAM_THREAD)

            changeFunOneState(position, Method.DONE)

          //  changeFunTwoState(position, Method.RUNNING)

            changeIconReceive(position)

            requestSpamThread(chatResponse)
        }


        if (fucCallback[ConstantMsgType.GET_MENTION_LIST] == chatResponse?.uniqueId) {


            findMentionedThreads(chatResponse)


        }


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

        if (fucCallback[ConstantMsgType.GET_HISTORYWITHMSGTYPE] == chatResponse?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.GET_HISTORYWITHMSGTYPE)
            changeFunOneState(position, Method.DONE)
            changeFunTwoState(position, Method.RUNNING)
            prepareGetHistoryByType(chatResponse)

        }

        if (fucCallback[ConstantMsgType.ADD_PARTICIPANTBYTYPE] == chatResponse?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.ADD_PARTICIPANTBYTYPE)
            changeFunOneState(position, Method.DONE)
            changeFunTwoState(position, Method.RUNNING)
            selectTypeIdDialog(chatResponse);

        }


    }

    private fun selectTypeIdDialog(chatResponse: ChatResponse<ResultThreads>?) {
        var threadId: Long = chatResponse?.result?.threads!![0].id;
        val selectTypeAddParticipantDialog = SelelctTypeAddParticipant()
        selectTypeAddParticipantDialog.setThreadId(threadId)
        selectTypeAddParticipantDialog.setListener(object :
            SelelctTypeAddParticipant.IGetHistory {
            override fun onSelected(type: Int, selectedTHereadId: Long) {
                if (threadId != selectedTHereadId)
                    threadId = selectedTHereadId;

                fucCallback["ADD_PARTICIPANT_THREADID"] = threadId.toString()
                fucCallback["ADD_PARTICIPANT_SELECTEDTYPE"] = type.toString()
                getContactsForAddParticipant()
            }
        })

        selectTypeAddParticipantDialog.show(childFragmentManager, "ADD_PARTICIPANTBYTYPE")
    }

    private fun getContactsForAddParticipant() {
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.ADD_PARTICIPANTBYTYPE] =
            mainViewModel.getContact(requestGetContact)
    }

    private fun prepareGetHistoryByType(chatResponse: ChatResponse<ResultThreads>?) {
        var threadId: Long = chatResponse?.result?.threads!![0].id;


        val createHistoryWithMessageType = CreateHistoryWithMessageType()
        createHistoryWithMessageType.setThreadId(threadId);
        createHistoryWithMessageType.setListener(object :
            CreateHistoryWithMessageType.IGetHistory {
            override fun onSelected(messageType: Int, selectedTHereadId: Long) {
                if (threadId != selectedTHereadId)
                    threadId = selectedTHereadId;

                prepareGetHistoryWithType(messageType, threadId);
            }
        })

        createHistoryWithMessageType.show(childFragmentManager, "CREATE_HISTORY_WITHME_SAGETYPE")
    }

    private fun prepareGetHistoryWithType(type: Int, threadId: Long) {

        val requestGetHistory = RequestGetHistory
            .Builder(threadId)
            .offset(0)
            .count(10)
            .setMessageType(type)
            .build()

        fucCallback[ConstantMsgType.GET_HISTORYWITHMSGTYPE] =
            mainViewModel.getHistory(requestGetHistory)
    }

    private fun updateThreadList(chatResponse: ChatResponse<ResultThreads>?) {
        threadsList.clear()
        threadsList.addAll(chatResponse?.result?.threads!!)
    }

    private fun findAdminThreadsForGetRoles(chatResponse: ChatResponse<ResultThreads>?) {


        val pos = getPositionOf(ConstantMsgType.GET_CURRENT_USER_ROLES)

        changeFunOneState(pos, Method.DONE)

        changeFunTwoState(pos, Method.RUNNING)


        var threadId: Long = 0

        var groupThreadId: Long = 0


        if (chatResponse?.result?.threads?.size!! > 0) {

            val threads = chatResponse.result?.threads!!


            for (thread in threads) {

                if (thread.admin) {
                    threadId = thread.id
                    break
                }

                if (thread.isGroup)
                    groupThreadId = thread.id

            }

            if (threadId == 0L) {

                showToast("You are not admin in any thread")


                if (groupThreadId != 0L)
                    getCurrentUserRolesInThread(groupThreadId)
                else
                    showToast("There is no group to get mention list")

            } else {

                getCurrentUserRolesInThread(threadId)

            }

        }

    }


    override fun onGetCurrentUserRoles(response: ChatResponse<ResultCurrentUserRoles>?) {
        super.onGetCurrentUserRoles(response)


        if (response?.uniqueId == fucCallback[ConstantMsgType.GET_CURRENT_USER_ROLES]) {

            val pos = getPositionOf(ConstantMsgType.GET_CURRENT_USER_ROLES)

            changeFunThreeState(pos, Method.DONE)

            changeIconReceive(pos)

        }


    }

    private fun getCurrentUserRolesInThread(threadId: Long) {


        val request = RequestGetUserRoles.Builder()
            .setThreadId(threadId)
            .build()


        fucCallback[ConstantMsgType.GET_CURRENT_USER_ROLES] =
            mainViewModel.getCurrentUserRoles(request)


    }

    private fun findMentionedThreads(chatResponse: ChatResponse<ResultThreads>?) {
        val pos = getPositionOf(ConstantMsgType.GET_MENTION_LIST)

        changeFunOneState(pos, Method.DONE)

        changeFunTwoState(pos, Method.RUNNING)

        var threadId: Long = 0

        var groupThreadId: Long = 0

        if (chatResponse?.result?.threads?.size!! > 0) {

            val threads = chatResponse.result?.threads!!

            for (thread in threads) {

                if (thread.isMentioned) {
                    threadId = thread.id
                    break
                }

                if (thread.isGroup)
                    groupThreadId = thread.id
            }

            if (threadId == 0L) {

                showToast("You are not mentioned in any thread")

                if (groupThreadId != 0L)
                    getMentionListOfThread(groupThreadId)
                else
                    showToast("There is no group to get mention list")

            } else {

                getMentionListOfThread(threadId)

            }


        }
    }


    override fun onGetMentionList(response: ChatResponse<ResultHistory>?) {
        super.onGetMentionList(response)


        if (fucCallback[ConstantMsgType.GET_MENTION_LIST] == response?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.GET_MENTION_LIST)
            changeFunThreeState(pos, Method.DONE)
            changeIconReceive(pos)


        }


    }

    private fun getMentionListOfThread(threadId: Long) {

        val pos = getPositionOf(ConstantMsgType.GET_MENTION_LIST)


        changeFunTwoState(pos, Method.DONE)

        changeFunThreeState(pos, Method.RUNNING)

        val request = RequestGetMentionList.Builder(threadId)
            .setAllMentioned(true)
            .build()


        fucCallback[ConstantMsgType.GET_MENTION_LIST] = mainViewModel.getMentionList(request)


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

                showToast("There is no thread with condition <<Group>> and <<Admin>> True! Please Create On First")

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

            val targetThreadId = chatResponse.result!!.threads[0].id


            val requestMessage = RequestMessage
                .Builder("test text message ${Date()}", targetThreadId)
                .build()
            requestMessage.messageType = TextMessageType.Constants.TEXT;


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
            requestMessage.messageType = TextMessageType.Constants.TEXT;

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
                .count(10)
                .build()


            val listOfSendingMessages = ArrayList<String>()



            for (i in 0..3) {

                val requestMessage =
                    RequestMessage.Builder("$i" + "th Message at ${Date().time} ", threadId).build()
                requestMessage.messageType = TextMessageType.Constants.TEXT;
                val uniqueId =
                    mainViewModel.sendTextMsg(requestMessage)

                listOfSendingMessages.add(uniqueId)

                fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] = uniqueId


            }

//            fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] = ""


            fucCallbacks[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] = listOfSendingMessages

            fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] =
                mainViewModel.getHistory(requestGetHistory)


        }


    }

    private fun prepareSendMessage(chatResponse: ChatResponse<ResultThreads>?) {
        if (chatResponse?.result?.threads?.size!! > 0) {
//            fucCallback.remove(ConstantMsgType.SEND_MESSAGE)
            val threadId = chatResponse.result.threads[0].id
            val requestMessage = RequestMessage.Builder(faker.lorem().paragraph(), threadId).build()
            requestMessage.messageType = TextMessageType.Constants.TEXT;
            fucCallback[ConstantMsgType.SEND_MESSAGE] = mainViewModel.sendTextMsg(requestMessage)

            val position = getPositionOf(ConstantMsgType.SEND_MESSAGE)
            changeFunOneState(position, Method.DONE)
            changeFunTwoState(position, Method.RUNNING)
            changeFunThreeState(position, Method.RUNNING)

        } else {
            val requestGetContact = RequestGetContact.Builder().build()
//            fucCallback.remove(ConstantMsgType.SEND_MESSAGE)
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

                showToast("There is no group to get admins")
                val pos = getPositionOf(ConstantMsgType.GET_ADMINS_LIST)
                changeIconReceive(pos)
                changeFunTwoState(pos, Method.DEACTIVE)

                return

            }


            TEST_THREAD_ID = threadId


            val requestGetAdmin = RequestGetAdmin.Builder(TEST_THREAD_ID)
                .admin(true)
                .count(50)
                .build()


            fucCallback[ConstantMsgType.GET_ADMINS_LIST] =
                mainViewModel.getAdminList(requestGetAdmin)


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


                showToast("No Thread found with condition Group and Admin true!")

                deactiveFunction(getPositionOf(ConstantMsgType.ADD_ADMIN_ROLES))

                changeFunTwoState(getPositionOf(ConstantMsgType.ADD_ADMIN_ROLES), Method.DEACTIVE)

                return

            }


            TEST_THREAD_ID = threadId


            val requestThreadParticipant = RequestGetAdmin.Builder(threadId)
                .threadId(threadId)
                .count(50)
                .build()

            fucCallback[ConstantMsgType.ADD_ADMIN_ROLES] =
                mainViewModel.getParticipant(requestThreadParticipant)


        }
    }

    private fun prepareRemoveAdmin(chatResponse: ChatResponse<ResultThreads>?) {

        if (chatResponse?.result?.threads?.size!! > 0) {

//            fucCallback.remove(ConstantMsgType.REMOVE_ADMIN_ROLES)

            var threadId = 0L

            for (thread: Thread in chatResponse.result.threads) {

                if (thread.isGroup && thread.admin) {

                    threadId = thread.id

                    break


                }

            }

            if (threadId == 0L) {

                val pos = getPositionOf(ConstantMsgType.REMOVE_ADMIN_ROLES)

                showToast("Can't find a thread that you have set role permission")

                deactiveFunction(pos)

                changeFunTwoState(pos, Method.DEACTIVE)

                return

            }


            TEST_THREAD_ID = threadId

            val requestGetAdmin = RequestGetAdmin.Builder(threadId)
                .admin(true)
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

//            fucCallback.remove(ConstantMsgType.CLEAR_HISTORY)


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



        if (fucCallback[ConstantMsgType.PIN_UN_PIN_MESSAGE] == response?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.PIN_UN_PIN_MESSAGE)

            changeFunTwoState(position, Method.DONE)

            changeFunThreeState(position, Method.RUNNING)

            val messageid = response?.result?.messageId


            val request = RequestPinMessage.Builder()
                .setMessageId(messageid!!)
                .setNotifyAll(true)
                .build()

            fucCallback[ConstantMsgType.PIN_UN_PIN_MESSAGE] = mainViewModel.pinMessage(request)

        }



        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] == response?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)
            changeIconReceive(position)
            changeFunFourState(position, Method.DONE)

        }


        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG_ID] == response?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)

            changeFunThreeState(pos, Method.DONE)

            changeFunFourState(pos, Method.RUNNING)

            val forwardMsgId = response?.result?.messageId

            val contactId = fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG_CONTCT_ID]

            val inviteList = ArrayList<Invitee>()
            inviteList.add(Invitee(contactId, 2))


            val forwList: MutableList<Long> = mutableListOf()
            forwList.add(forwardMsgId!!)


            val requestThreadInnerMessage =
                RequestThreadInnerMessage.Builder(TextMessageType.Constants.TEXT)
                    .message(faker.music().genre())
                    .forwardedMessageIds(forwList).build()


            val requestCreateThread: RequestCreateThread =
                RequestCreateThread.Builder(0, inviteList)
                    .message(requestThreadInnerMessage)
                    .build()


            fucCallbacks[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] =
                mainViewModel.createThreadWithMessage(requestCreateThread)!!


            fucCallbacks[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG]?.forEach {

                fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] = it

            }

            fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] =
                fucCallbacks[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG]!![2]

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

        if (fucCallbacks[ConstantMsgType.DELETE_MULTIPLE_MESSAGE]?.last() == response?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.DELETE_MULTIPLE_MESSAGE)

            changeFunTwoState(position, Method.DONE)

            changeFunThreeState(position, Method.RUNNING)

            getHistoryForDeleteMessage(response)

        }


        if (fucCallbacks[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] != null) {

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
            .typeCode("default")
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
                messageId!!,
                TextMessageType.Constants.TEXT
            )
                .build()
        fucCallback[ConstantMsgType.REPLY_MESSAGE] = mainViewModel.replyMessage(replyMessage)
    }

    private fun handleCreateThreadWithContactBForForwardMessage(response: ChatResponse<ResultMessage>?) {


        val contactBId = fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B]?.toLong()


        val inviteList = Array(1) {
            Invitee(contactBId?.toString(), contactBIdType)
        }


        val uniqueId = mainViewModel.createThread(
            ThreadType.Constants.NORMAL, inviteList, "Contact B", "", "", ""
        )

//        fucCallback[ConstantMsgType.FORWARD_MESSAGE] = uniqueId

        fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B] = uniqueId

//        fucCallback[ConstantMsgType.FORWARD_MESSAGE] = ""


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

            val position = getPositionOf(ConstantMsgType.BLOCK_CONTACT)
            changeFunThreeState(position, Method.DONE)
            changeIconReceive(position)

            try {
                val blocked = chatResponse?.result?.contact

                threadsList.forEach { thread ->
                    if (thread.partner == blocked?.contactVO?.userId)
                        threadsList.remove(thread)
                }
                contactList.forEach { contact ->
                    if (contact.userId == blocked?.contactVO?.userId) {
                        contactList.remove(contact)
                        contactList.add(blocked.contactVO)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }


        }
        if (fucCallback[ConstantMsgType.UNBLOCK_CONTACT] == chatResponse?.uniqueId) {
            fucCallback.remove(ConstantMsgType.UNBLOCK_CONTACT)
            val contactId = chatResponse?.result?.contact?.blockId
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


        if (fucCallback[ConstantMsgType.UNMUTE_THREAD] == response?.uniqueId) {
            val position = 16
            changeFunThreeState(position, Method.DONE)
            changeFunFourState(position, Method.RUNNING)

            requestUnMuteThread(response?.result)
        }
    }

    private fun requestUnMuteThread(result: ResultMute?) {


        val threadId = result?.threadId

        val requestMuteThread = RequestMuteThread.Builder(threadId!!).build()

        fucCallback[ConstantMsgType.UNMUTE_THREAD] =
            mainViewModel.unMuteThread(requestMuteThread)

    }

    override fun onUnmuteThread(response: ChatResponse<ResultMute>?) {
        super.onUnmuteThread(response)
        if (fucCallback[ConstantMsgType.UNMUTE_THREAD] == response?.uniqueId) {
            val position = 16
            changeIconReceive(position)
            changeFunFourState(position, Method.DONE)
        }
    }

    override fun onGetHistory(response: ChatResponse<ResultHistory>?) {
        super.onGetHistory(response)


        if (fucCallback[ConstantMsgType.GET_HISTORY] == response?.uniqueId) {
            val position = 19
            changeIconReceive(position)
            changeFunThreeState(position, Method.DONE)
        }

        if (fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.DELETE_MULTIPLE_MESSAGE)

            changeFunThreeState(pos, Method.DONE)

            changeFunFourState(pos, Method.RUNNING)

            requestDeleteMultipleMessage(response)

        }
        if (fucCallback[ConstantMsgType.GET_HISTORYWITHMSGTYPE] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.GET_HISTORYWITHMSGTYPE)

            changeFunTwoState(pos, Method.DONE)
            changeIconReceive(pos)
            Log.e("test", "message by type is done");

        }


    }


    private fun requestDeleteMultipleMessage(response: ChatResponse<ResultHistory>?) {

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

            showToast("There is no deletable message! Create a thread with message first")

            changeIconReceive(26)

            return

        }


        var requestDeleteMessage = RequestDeleteMessage
            .Builder()
            .messageIds(deleteListIds)
            .deleteForAll(true)
            .build()


        fucCallbacks[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] =
            mainViewModel.deleteMultipleMessage(requestDeleteMessage)


        fucCallbacks[ConstantMsgType.DELETE_MULTIPLE_MESSAGE]?.forEach {

            fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] = it


        }


    }

    override fun onRemoveRoleFromUser(response: ChatResponse<ResultSetAdmin>?) {
        super.onRemoveRoleFromUser(response)

        if (fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] == response?.uniqueId) {

            val removeAdminPosition = getPositionOf(ConstantMsgType.REMOVE_ADMIN_ROLES)

            changeFunThreeState(removeAdminPosition, Method.DONE)

            changeIconReceive(removeAdminPosition)

            methods[removeAdminPosition].methodNameFlag = true

        }

    }

    override fun onGetThreadParticipant(response: ChatResponse<ResultParticipant>?) {
        super.onGetThreadParticipant(response)



        if (fucCallback[ConstantMsgType.GET_PARTICIPANT] == response?.uniqueId) {


            val position = 21
            changeIconReceive(position)
            changeFunThreeState(position, Method.DONE)


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


        val requestRoles = ArrayList<RequestRole>()

        requestRoles.add(requestRole)


        val addAdmin = RequestSetAdmin
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

            showToast("There is no thread with admin! create one first")

            changeIconReceive(getPositionOf(ConstantMsgType.REMOVE_ADMIN_ROLES))
            return
        }

        val removableRole = adminParticipant.roles

        val requestRole = RequestRole()

        requestRole.id = adminParticipant.id

        requestRole.roleTypes = ArrayList(removableRole)


        val requestRoles = ArrayList<RequestRole>()

        requestRoles.add(requestRole)

        val addAdmin = RequestSetAdmin
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


    override fun onMessageUnPinned(response: ChatResponse<ResultPinMessage>?) {
        super.onMessageUnPinned(response)


        if (fucCallback[ConstantMsgType.PIN_UN_PIN_MESSAGE] == response?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.PIN_UN_PIN_MESSAGE)

            changeFunFourState(position, Method.DONE)

            changeIconReceive(position)


        }


    }

    override fun onMessagePinned(response: ChatResponse<ResultPinMessage>?) {
        super.onMessagePinned(response)



        if (fucCallback[ConstantMsgType.PIN_UN_PIN_MESSAGE] == response?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.PIN_UN_PIN_MESSAGE)

            changeFunThreeState(position, Method.DONE)

            changeFunFourState(position, Method.RUNNING)

            val messageid = response?.result?.messageId

            val request = RequestPinMessage.Builder()
                .setMessageId(messageid!!.toLong())
                .build()

            fucCallback[ConstantMsgType.PIN_UN_PIN_MESSAGE] = mainViewModel.unpinMessage(request)

        }


    }

    override fun onUnPinThread(response: ChatResponse<ResultPinThread>?) {
        super.onUnPinThread(response)

        val uniqueId = response?.uniqueId

        if (uniqueId.isNullOrBlank()) return



        if (fucCallback[ConstantMsgType.PIN_UN_PIN_THREAD] == uniqueId) {

            val position = getPositionOf(ConstantMsgType.PIN_UN_PIN_THREAD)

            changeFunFourState(position, Method.DONE)

            changeIconReceive(position)

        }


    }

    override fun onPinThread(response: ChatResponse<ResultPinThread>?) {
        super.onPinThread(response)

        val uniqueId = response?.uniqueId

        if (uniqueId.isNullOrBlank()) return



        if (fucCallback[ConstantMsgType.PIN_UN_PIN_THREAD] == uniqueId) {

            val position = getPositionOf(ConstantMsgType.PIN_UN_PIN_THREAD)

            changeFunThreeState(position, Method.DONE)

            changeFunFourState(position, Method.RUNNING)


            val request: RequestPinThread = RequestPinThread
                .Builder(response.subjectId)
                .build()

            fucCallback[ConstantMsgType.PIN_UN_PIN_THREAD] = mainViewModel.unpinThread(request)


        }


    }

    override fun onCreateThread(response: ChatResponse<ResultThread>?) {
        super.onCreateThread(response)


        if (fucCallback[ConstantMsgType.CREATE_PUBLIC_THREAD] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.CREATE_PUBLIC_THREAD)

            changeIconReceive(pos)

            changeFunThreeState(pos, Method.DONE)


        }

        if (fucCallback[ConstantMsgType.GET_CURRENT_USER_ROLES] == response?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.GET_CURRENT_USER_ROLES)

            changeFunTwoState(position, Method.DONE)
//
            changeFunThreeState(position, Method.RUNNING)

            val thread = response?.result?.thread

            getCurrentUserRolesInThread(thread?.id!!)


        }


        if (fucCallback[ConstantMsgType.PIN_UN_PIN_MESSAGE] == response?.uniqueId) {

//            val position = getPositionOf(ConstantMsgType.PIN_UN_PIN_MESSAGE)

//            changeFunTwoState(position, Method.DONE)
//
//            changeFunThreeState(position, Method.RUNNING)

            val thread = response?.result?.thread

            fucCallback[ConstantMsgType.PIN_UN_PIN_MESSAGE] = sendTextMessageToThread(thread?.id!!)


        }





        if (fucCallback[ConstantMsgType.PIN_UN_PIN_THREAD] == response?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.PIN_UN_PIN_THREAD)

            changeFunTwoState(position, Method.DONE)

            changeFunThreeState(position, Method.RUNNING)

            val thread = response?.result?.thread


            val request: RequestPinThread = RequestPinThread
                .Builder(response?.result?.thread?.id!!)
                .build()


            if (thread?.isPin!!) {

                changeFunThreeState(position, Method.DONE)

                changeFunFourState(position, Method.RUNNING)

                fucCallback[ConstantMsgType.PIN_UN_PIN_THREAD] = mainViewModel.unpinThread(request)


            } else {

                fucCallback[ConstantMsgType.PIN_UN_PIN_THREAD] = mainViewModel.pinThread(request)

            }


        }





        if (fucCallback[ConstantMsgType.GET_PARTICIPANT] == response?.uniqueId) {

            val position = getPositionOf(ConstantMsgType.GET_PARTICIPANT)

            changeFunTwoState(position, Method.DONE)

            changeFunThreeState(position, Method.RUNNING)

            requestGetParticipants(response)
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)

            changeFunTwoState(pos, Method.DONE)

            changeFunThreeState(pos, Method.RUNNING)


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
            changeFunTwoState(position, Method.DONE)
            changeFunThreeState(position, Method.RUNNING)
            requestGetHistory(response)
        }

        if (fucCallback[ConstantMsgType.UNMUTE_THREAD] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.UNMUTE_THREAD)

            changeFunTwoState(pos, Method.DONE)
            changeFunThreeState(pos, Method.RUNNING)

            requestMuteThreadForUnMute(response)
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
            changeFunTwoState(position, Method.DONE)
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_CHANNEL] == response?.uniqueId) {
            val position = 0
            changeIconReceive(position)

            changeFunOneState(position, Method.DONE)
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_CHANNEL_GROUP] == response?.uniqueId) {
            val position = 0
            changeIconReceive(position)

            changeFunTwoState(position, Method.DONE)

        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_PUBLIC_GROUP] == response?.uniqueId) {
            val position = 0
            changeIconReceive(position)

            changeFunThreeState(position, Method.DONE)

        }
        if (fucCallback[ConstantMsgType.CREATE_THREAD_OWNER_GROUP] == response?.uniqueId) {
            val position = 0
            changeIconReceive(position)
            changeFunFourState(position, Method.DONE)

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

    private fun requestSpamThread(response: ChatResponse<ResultThreads>?) {

        var targetThreadId = -1L



        for (thread in response?.result?.threads!!.filter { t -> !t.isGroup }) {

            if (thread.canSpam) {

                targetThreadId = thread.id

                break
            }

        }

        if (targetThreadId != -1L) {


            val requestSpam = RequestSpam.Builder().threadId(targetThreadId)
                .build()


            showToast("SENDING SPAM REQUEST")
           fucCallback[ConstantMsgType.SPAM_THREAD] = mainViewModel.spamThread(requestSpam)


        } else {

            val pos = getPositionOf(ConstantMsgType.SPAM_THREAD)

            deactiveFunction(pos)

            changeFunTwoState(pos, Method.DEACTIVE)

            showToast("Could't find a spam thread")

        }


    }

    private fun requestGetParticipants(response: ChatResponse<ResultThread>?) {


        val threadId = response!!.result.thread.id
        val requestThreadParticipant = RequestThreadParticipant.Builder(threadId).build()
        fucCallback[ConstantMsgType.GET_PARTICIPANT] =
            mainViewModel.getParticipant(requestThreadParticipant)


    }

    private fun sendMessageForForwardMessage(response: ChatResponse<ResultThread>?) {

        val threadId = response!!.result.thread.id

        val requestMessage =
            RequestMessage.Builder(
                "this is message for create thread with forward message",
                threadId
            ).messageType(TextMessageType.Constants.TEXT)
                .build()


        fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG_ID] =
            mainViewModel.sendTextMsg(requestMessage)
    }


    private fun sendTextMessageToThread(
        threadId: Long,
        text: String = "Text Message From Android Test App at ${Date().time}"
    ): String {

        val requestMessage =
            RequestMessage.Builder(text, threadId)
                .messageType(TextMessageType.Constants.TEXT)
                .build()

        return mainViewModel.sendTextMsg(requestMessage)

    }

    private fun requestGetHistory(response: ChatResponse<ResultThread>?) {
        val threadId = response!!.result.thread.id
        val requestGetHistory = RequestGetHistory.Builder(threadId).build()
        fucCallback[GET_HISTORY] = mainViewModel.getHistory(requestGetHistory)
    }

    private fun requestMuteThreadForUnMute(response: ChatResponse<ResultThread>?) {

        val threadId = response!!.result.thread.id

        val requestMuteThread = RequestMuteThread.Builder(threadId).build()

        fucCallback[ConstantMsgType.UNMUTE_THREAD] =
            mainViewModel.muteThread(requestMuteThread)


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


        val uniqueIds = mainViewModel.forwardMessage(requestForwardMessage)


        for (indexOfUniqueId in uniqueIds.indices) {


            if (indexOfUniqueId == 0) continue


            fucCallback[ConstantMsgType.FORWARD_MESSAGE] = uniqueIds[indexOfUniqueId]

        }




        fucCallback[ConstantMsgType.FORWARD_MESSAGE] = uniqueIds[0]


    }

    private fun handleAddParticipant(response: ChatResponse<ResultThread>?) {
        val participantId = fucCallback["ADD_PARTICIPANT_ID"]

//        fucCallback.remove("ADD_PARTICIPANT_ID")


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

        if (mainServer) {

            mainViewModel.connect(
                main_socketAddress,
                sand_appId,
                serverName,
                TOKEN,
                ssoHost,
                main_platformHost,
                main_fileServer,
                podSpaceUrl,
                typeCode
            )


        } else {

            //sandBox
            mainViewModel.connect(
                sandSocketAddress,
                sand_appId,
                serverName,
                TOKEN,
                ssoHost,
                sandPlatformHost,
                sandFileServer,
                podSpaceUrl,
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

        updateContactsList(response)



        if (fucCallback[ConstantMsgType.CREATE_PUBLIC_THREAD] == response?.uniqueId) {

            fucCallback[ConstantMsgType.CREATE_PUBLIC_THREAD] = createIsNameAvailableRequest()

        }

        if (fucCallback[ConstantMsgType.GET_CURRENT_USER_ROLES] == response?.uniqueId) {

            createGroupForGetUserRoles(contactList)

        }




        if (fucCallback[ConstantMsgType.PIN_UN_PIN_MESSAGE] == response?.uniqueId) {

            createGroupForPinUnpinMessage(contactList)

        }

        if (fucCallback[ConstantMsgType.PIN_UN_PIN_THREAD] == response?.uniqueId) {

            createThreadForPinUnpinThread(contactList)

        }

        if (fucCallback[ConstantMsgType.GET_PARTICIPANT] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.GET_PARTICIPANT)
            changeFunOneState(pos, Method.DONE)
            changeFunTwoState(pos, Method.RUNNING)
            handleGetParticipant(contactList)
        }

        if (fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] == response?.uniqueId) {

            val pos = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)

            changeFunOneState(pos, Method.DONE)

            changeFunTwoState(pos, Method.RUNNING)

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
            handleUnMuteThread(contactList)
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
            createThreadNow()
        }

        if (fucCallback[ConstantMsgType.GET_CONTACT] == response?.uniqueId) {
            val position = 1
            changeIconReceive(position)
            changeFunOneState(1, Method.DONE)
        }

        if (fucCallback[ConstantMsgType.BLOCK_CONTACT] == response?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.BLOCK_CONTACT)

            changeFunOneState(pos, Method.DONE)

            if (threadsList.isNullOrEmpty()) {

                changeFunTwoState(pos, Method.RUNNING)
                val requestThread: RequestThread = RequestThread.Builder().build()
                requestThread.count = 10;
                fucCallback[ConstantMsgType.BLOCK_CONTACT] = mainViewModel.getThreads(requestThread)

            } else {

                blockNow()
            }
        }

        if (fucCallback[ConstantMsgType.UPDATE_CONTACT] == response?.uniqueId) {

//            fucCallback.remove(ConstantMsgType.UPDATE_CONTACT)
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

        if (fucCallback[ConstantMsgType.ADD_PARTICIPANTBYTYPE] == response?.uniqueId) {


            val pos = getPositionOf(ConstantMsgType.ADD_PARTICIPANTBYTYPE)

            changeFunTwoState(pos, Method.DONE)

            changeFunThreeState(pos, Method.RUNNING)

            handleAddParticipantByType(contactList)
        }
    }

    fun handleAddParticipantByType(contactList: ArrayList<Contact>?) {
        var threadId = fucCallback["ADD_PARTICIPANT_THREADID"].toString().toLong()
        var selectedType = fucCallback["ADD_PARTICIPANT_SELECTEDTYPE"].toString().toInt()
        var request: RequestAddParticipants? =null;
        if (contactList != null) {
            when (selectedType) {
                1 -> {// add participant by userid 1
                    request = RequestAddParticipants
                        .newBuilder()
                        .threadId(threadId)
                        .withCoreUserIds( prepareGetthreeContact_UserId(contactList)
                         )
                        .build()

                }
                2 -> {// add participant by contactid 2
                    request = RequestAddParticipants
                        .newBuilder()
                        .threadId(threadId)
                        .withContactIds(prepareGetthreeContact_ContactId(contactList)
                        )
                        .build()

                }
                3 -> {// add participant by username 3
                    request = RequestAddParticipants
                        .newBuilder()
                        .threadId(threadId)
                        .withUserNames(prepareGetthreeContact_UserName(contactList)
                        )
                        .build()

                }
                else ->  { // add participant by userid by default 1
                    request = RequestAddParticipants
                        .newBuilder()
                        .threadId(threadId)
                        .withContactIds( prepareGetthreeContact_UserId(contactList)
                        )
                        .build()
                }
            }
        }

        fucCallback[ConstantMsgType.ADD_PARTICIPANTBYTYPE] = mainViewModel.addParticipant(request!!)

    }

    private fun prepareGetthreeContact_ContactId(contactList: ArrayList<Contact>?) : List<Long> {
        var intList: MutableList<Long> = ArrayList()
        var choose = 0
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    var contactId = contact.id
                    if (choose < 3)
                        intList.add(contactId)
                    else
                        break
                    choose++
                }
            }
        }
        return intList.toList();
    }

    private fun prepareGetthreeContact_UserId(contactList: ArrayList<Contact>?) : List<Long>{
        var intList: MutableList<Long> = ArrayList()
        var choose = 0
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    var contactId = contact.userId
                    if (choose < 3)
                        intList.add(contactId)
                    else
                        break
                    choose++
                }
            }
        }
      return intList.toList();
    }

    private fun prepareGetthreeContact_UserName(contactList: ArrayList<Contact>?) : List<String>{
        var intList: MutableList<String> = ArrayList()
        var choose = 0
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    var contactId = contact.linkedUser.username
                    if (choose < 3)
                        intList.add(contactId)
                    else
                        break
                    choose++
                }
            }
        }
        return intList.toList()
    }

    private fun updateContactsList(response: ChatResponse<ResultContact>?) {
        contactList.clear()
        contactList.addAll(response?.result?.contacts!!)
    }

    private fun createGroupForGetUserRoles(contactList: java.util.ArrayList<Contact>?) {


        val pos = getPositionOf(ConstantMsgType.GET_CURRENT_USER_ROLES)

        changeFunOneState(pos, Method.DONE)

        if (contactList.isNullOrEmpty()) {
            showNoContactToast()
            return
        }

        changeFunTwoState(pos, Method.RUNNING)


        val invitee = getInviteeFromContactList(contactList, 2)

        fucCallback[ConstantMsgType.GET_CURRENT_USER_ROLES] = sendCreateGroupRequest(invitee)


    }

    private fun createGroupForPinUnpinMessage(contactList: java.util.ArrayList<Contact>?) {

        val pos = getPositionOf(ConstantMsgType.PIN_UN_PIN_MESSAGE)

        changeFunOneState(pos, Method.DONE)

        if (contactList.isNullOrEmpty()) {
            showNoContactToast()
            return
        }

        changeFunTwoState(pos, Method.RUNNING)


        val invitee = getInviteeFromContactList(contactList, 2)

        fucCallback[ConstantMsgType.PIN_UN_PIN_MESSAGE] = sendCreateGroupRequest(invitee)


    }

    private fun createThreadForPinUnpinThread(contactList: java.util.ArrayList<Contact>?) {


        val pos = getPositionOf(ConstantMsgType.PIN_UN_PIN_THREAD)

        changeFunOneState(pos, Method.DONE)

        if (contactList.isNullOrEmpty()) {
            showNoContactToast()
            return
        }

        changeFunTwoState(pos, Method.RUNNING)


        val invitee = getInviteeFromContactList(contactList)

        val request = generateCreateThreadRequest(invitee, "Create Thread for Pin Unpin")

        val uniqueIds = mainViewModel.createThreadWithMessage(request)


        //unique id at position 0 belongs to Create Thread and position 1 is inner text message

        fucCallback[ConstantMsgType.PIN_UN_PIN_THREAD] = uniqueIds!![0]


    }

    private fun generateCreateThreadRequest(
        invList: ArrayList<Invitee>,
        innerMessageText: String
    ): RequestCreateThread {

        val message = RequestThreadInnerMessage
            .Builder(TextMessageType.Constants.TEXT)
            .message(innerMessageText)
            .build()


        return RequestCreateThread
            .Builder(ThreadType.Constants.NORMAL, invList)
            .message(message)
            .build()
    }

    private fun sendCreateGroupRequest(
        invList: ArrayList<Invitee>,
        title: String = "Title From Test App",
        desc: String = "From Android Test App Created At " + System.currentTimeMillis()
    ): String {


        return mainViewModel.createThread(
            ThreadType.Constants.OWNER_GROUP, invList.toTypedArray(), title, desc, "", ""
        )


    }

    private fun getInviteeFromContactList(contactList: ArrayList<Contact>): ArrayList<Invitee> {

        var contactId = 0L


        for (contact: Contact in contactList) {
            if (contact.isHasUser) {
                contactId = contact.id
                break
            }
        }

        val invList = ArrayList<Invitee>()


        if (contactId != 0L) {

            invList.add(
                Invitee(
                    contactId.toString(),
                    InviteType.Constants.TO_BE_USER_CONTACT_ID
                )
            )

        } else {

            invList.add(Invitee("2", InviteType.Constants.TO_BE_USER_SSO_ID))

        }
        return invList
    }

    private fun createInviteeFromContactList(
        contactList: List<Contact>,
        inviteType: Int
    ): ArrayList<Invitee> {

        val invList = ArrayList<Invitee>()

        contactList.forEach { contact ->

            when (inviteType) {
                InviteType.Constants.TO_BE_USER_ID -> invList.add(
                    Invitee(
                        contact.userId,
                        inviteType
                    )
                )
                InviteType.Constants.TO_BE_USER_USERNAME -> invList.add(
                    Invitee(
                        contact.linkedUser.username,
                        inviteType
                    )
                )
                InviteType.Constants.TO_BE_USER_CELLPHONE_NUMBER -> invList.add(
                    Invitee(
                        contact.cellphoneNumber,
                        inviteType
                    )
                )
                InviteType.Constants.TO_BE_USER_CONTACT_ID -> invList.add(
                    Invitee(
                        contact.id,
                        inviteType
                    )
                )
            }


        }
        return invList
    }


    private fun getInviteeFromContactList(
        contactList: ArrayList<Contact>,
        count: Int = 1
    ): ArrayList<Invitee> {

        var numberOfSelected = 0

        val invList = ArrayList<Invitee>()

        for (contact: Contact in contactList) {

            if (contact.isHasUser) {

                invList.add(
                    Invitee(
                        contact.id.toString(),
                        InviteType.Constants.TO_BE_USER_CONTACT_ID
                    )
                )

                numberOfSelected++

                if (numberOfSelected >= count)
                    break
            }


        }

        if (invList.size == count)
            return invList




        for (i in 0..(count - invList.size)) {

            invList.add(Invitee("${i + 1}", InviteType.Constants.TO_BE_USER_SSO_ID))

        }

        return invList

    }


    private fun handleGetParticipant(contactList: ArrayList<Contact>?) {

        if (!contactList.isNullOrEmpty()) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {

                    val contactId = contact.id

                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(
                        Invitee(
                            contactId.toString(),
                            InviteType.Constants.TO_BE_USER_CONTACT_ID
                        )
                    )

                    val list = Array(1) { Invitee(inviteList[0].id, inviteList[0].idType) }

                    val uniqueId = mainViewModel.createThread(
                        ThreadType.Constants.NORMAL, list, "nothing", "", "", ""
                    )

                    fucCallback[ConstantMsgType.GET_PARTICIPANT] = uniqueId
                    choose++
                    break

                }
            }

            if (choose == 0) {

                showNoContactToast()
                val pos = getPositionOf(ConstantMsgType.GET_PARTICIPANT)
                deactiveFunction(pos)
                changeFunTwoState(pos, Method.DEACTIVE)

            }
        } else {

            showNoContactToast()
            val pos = getPositionOf(ConstantMsgType.GET_PARTICIPANT)
            deactiveFunction(pos)
            changeFunTwoState(pos, Method.DEACTIVE)


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
                        inviteList.add(Invitee(contactId.toString(), 2))

                        val list = Array(1) { Invitee(inviteList[0].id, 2) }

                        val uniqueId = mainViewModel.createThread(
                            ThreadType.Constants.NORMAL, list, "nothing", "", "", ""
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

            if (choose < 2) {


                showNoContactToast()

                val pos = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)

                changeFunTwoState(pos, Method.DEACTIVE)

                deactiveFunction(pos)
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

                invList.add(
                    Invitee(
                        contactId.toString(),
                        InviteType.Constants.TO_BE_USER_CONTACT_ID
                    )
                )

            } else {

                invList.add(Invitee("121", InviteType.Constants.TO_BE_USER_SSO_ID))

            }


            val message = RequestThreadInnerMessage
                .Builder(TextMessageType.Constants.TEXT)
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
                    val inviteList = Array<Invitee>(1) {
                        Invitee(
                            contactId.toString(),
                            InviteType.Constants.TO_BE_USER_CONTACT_ID
                        )
                    }
                    inviteList[0].id = contactId.toString()

                    val uniqueId = mainViewModel.createThread(
                        ThreadType.Constants.NORMAL, inviteList, "", "", "", ""
                    )
                    fucCallback[ConstantMsgType.GET_HISTORY] = uniqueId
                    choose++
                    break

                }
            }

            if (choose == 0) {


                showNoContactToast()

                val pos = getPositionOf(ConstantMsgType.GET_HISTORY)

                deactiveFunction(pos)

                changeFunTwoState(pos, Method.DEACTIVE)


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
                    inviteList.add(
                        Invitee(
                            contactId.toString(),
                            InviteType.Constants.TO_BE_USER_CONTACT_ID
                        )
                    )
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage.Builder(
                            TextMessageType.Constants.TEXT
                        ).message(faker.music().genre())
                            .build()
                    val requestCreateThread: RequestCreateThread =
                        RequestCreateThread.Builder(0, inviteList)
                            .message(requestThreadInnerMessage)
                            .build()

                    val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)

                    fucCallback[ConstantMsgType.EDIT_MESSAGE_ID] = uniqueId!![1]

                    uniqueId.forEach {

                        fucCallback[ConstantMsgType.EDIT_MESSAGE] = it
                    }

                    break
                }
            }

            if (choose == 0) {

                showNoContactToast()

                val pos = getPositionOf(ConstantMsgType.EDIT_MESSAGE)

                deactiveFunction(pos)

                changeFunTwoState(pos, Method.DEACTIVE)


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
                    inviteList.add(Invitee(contactId.toString(), 2))
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage
                            .Builder(TextMessageType.Constants.TEXT)
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


                showNoContactToast()

                val pos = getPositionOf(ConstantMsgType.DELETE_MESSAGE)

                changeFunTwoState(pos, Method.DEACTIVE)

                deactiveFunction(pos)


            }
        }
    }

    private fun handleUnMuteThread(contactList: ArrayList<Contact>?) {

        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {

                    val contactId = contact.id
                    val inviteList =
                        Array(1) { Invitee(contactId, InviteType.Constants.TO_BE_USER_CONTACT_ID) }
                    inviteList[0].id = contactId.toString()

                    val uniqueId = mainViewModel.createThread(
                        ThreadType.Constants.NORMAL, inviteList, "", "", "", ""
                    )
                    choose++
                    fucCallback[ConstantMsgType.UNMUTE_THREAD] = uniqueId
                    break

                }
            }

            if (choose == 0) {

                showNoContactToast()

                val pos = getPositionOf(ConstantMsgType.UNMUTE_THREAD)

                deactiveFunction(pos)

                changeFunTwoState(pos, Method.DEACTIVE)


            }
        }
    }

    private fun handleMuteThread(contactList: ArrayList<Contact>?) {
        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val inviteList =
                        Array(1) { Invitee(contactId, InviteType.Constants.TO_BE_USER_CONTACT_ID) }

                    val uniqueId = mainViewModel.createThread(
                        ThreadType.Constants.NORMAL, inviteList, "", "", "", ""
                    )
                    choose++
                    fucCallback[ConstantMsgType.MUTE_THREAD] = uniqueId
                    break

                }
            }

            if (choose == 0) {

                showNoContactToast()

                val pos = getPositionOf(ConstantMsgType.MUTE_THREAD)

                deactiveFunction(pos)

                changeFunTwoState(pos, Method.DEACTIVE)

            }
        }
    }

    private fun handleLeaveThread(contactList: ArrayList<Contact>?) {

        val pos = getPositionOf(ConstantMsgType.LEAVE_THREAD)


        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id
                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 2))
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage.Builder(
                            TextMessageType.Constants.TEXT
                        ).message(faker.music().genre()).build()
                    val requestCreateThread: RequestCreateThread =
                        RequestCreateThread.Builder(ThreadType.Constants.OWNER_GROUP, inviteList)
                            .message(requestThreadInnerMessage)
                            .build()
                    val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
                    choose++
                    if (uniqueId?.get(0) != null) {
                        fucCallback[ConstantMsgType.LEAVE_THREAD] = uniqueId[0]
                    }
                    break
                }
            }

            if (choose == 0) {

                showNoContactToast()

                deactiveFunction(pos)

                changeFunTwoState(pos, Method.DEACTIVE)


            }

        } else {
            showNoContactToast()

            deactiveFunction(pos)

            changeFunTwoState(pos, Method.DEACTIVE)
        }
    }

    private fun handleReplyMessage(contactList: ArrayList<Contact>?) {

        val pos = getPositionOf(ConstantMsgType.REPLY_MESSAGE)

        var choose = 0

        if (contactList != null) {

            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, 2))
                    val requestThreadInnerMessage =
                        RequestThreadInnerMessage.Builder(
                            TextMessageType.Constants.TEXT
                        ).message(faker.music().genre()).build()
                    val requestCreateThread: RequestCreateThread =
                        RequestCreateThread.Builder(0, inviteList)
                            .message(requestThreadInnerMessage)
                            .build()
                    val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)
                    choose++
                    fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID] = uniqueId!![0]
                    fucCallback[ConstantMsgType.REPLY_MESSAGE_ID] = uniqueId[1]
                    break

                }
            }
        }



        if (choose == 0) {

            showNoContactToast()

            deactiveFunction(pos)

            changeFunTwoState(pos, Method.DEACTIVE)

        }
    }

    override fun onThreadAddParticipant(response: ChatResponse<ResultAddParticipant>?) {
        super.onThreadAddParticipant(response)



        if (fucCallback[ConstantMsgType.ADD_PARTICIPANT] == response?.uniqueId) {

            val position = 10
            changeIconReceive(position)
            changeFunThreeState(position, Method.DONE)


        }

        if (fucCallback[ConstantMsgType.ADD_PARTICIPANTBYTYPE] == response?.uniqueId) {

            val position = 40
            changeIconReceive(position)
            changeFunThreeState(position, Method.DONE)


        }

    }

    private fun handleForward(contactList: ArrayList<Contact>?) {


        var choose = 0

        if (contactList != null) {


            var contactIdsList = ArrayList<Long>()

            val ivList = ArrayList<Invitee>()

            ivList.add(Invitee(121, InviteType.Constants.TO_BE_USER_SSO_ID))

            ivList.add(Invitee(122, InviteType.Constants.TO_BE_USER_SSO_ID))


            for (contact: Contact in contactList) {


                if (contact.isHasUser) {

                    if (contactIdsList.size == 2) break

                    contactIdsList.add(contact.id)

                }

            }


            if (contactIdsList.size > 0) {


                val inviteList = ArrayList<Invitee>()

                inviteList.add(
                    Invitee(
                        contactIdsList[0],
                        InviteType.Constants.TO_BE_USER_CONTACT_ID
                    )
                )

                contactBIdType = InviteType.Constants.TO_BE_USER_CONTACT_ID

                val requestThreadInnerMessage =
                    RequestThreadInnerMessage.Builder(
                        TextMessageType.Constants.TEXT
                    ).message(faker.music().genre())
                        .build()

                val requestCreateThread: RequestCreateThread =
                    RequestCreateThread.Builder(ThreadType.Constants.NORMAL, inviteList)
                        .message(requestThreadInnerMessage)
                        .build()

                val uniqueId = mainViewModel.createThreadWithMessage(requestCreateThread)

                saveUniqueId(Pair(first = ConstantMsgType.FORWARD_MESSAGE, second = uniqueId!![0]))

                fucCallback[ConstantMsgType.FORWARD_MESSAGE_ID] = uniqueId!![1]


                if (contactIdsList.size > 1) {

                    fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B] =
                        contactIdsList[1].toString()

                } else {


                    fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B] =
                        contactIdsList[0].toString()


                }


            } else if (ivList.size > 0) {


                val inviteList = ArrayList<Invitee>()

                inviteList.add(
                    ivList[0]
                )


                contactBIdType = InviteType.Constants.TO_BE_USER_SSO_ID


                val requestThreadInnerMessage =
                    RequestThreadInnerMessage.Builder(
                        TextMessageType.Constants.TEXT
                    ).message(faker.music().genre())
                        .build()
                val requestCreateThread: RequestCreateThread =
                    RequestCreateThread.Builder(0, inviteList)
                        .message(requestThreadInnerMessage)
                        .build()
                val uniqueId =
                    mainViewModel.createThreadWithMessage(requestCreateThread)
//                fucCallback[ConstantMsgType.FORWARD_MESSAGE] = uniqueId!![0]


                saveUniqueId(Pair(first = ConstantMsgType.FORWARD_MESSAGE, second = uniqueId!![0]))

                fucCallback[ConstantMsgType.FORWARD_MESSAGE_ID] = uniqueId!![1]



                if (ivList.size > 1) {

                    fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B] = ivList[1].id.toString()

                } else {

                    fucCallback[ConstantMsgType.FORWARD_MESSAGE_CONTACT_B] = ivList[0].id.toString()


                }


            }

        }
    }

    private fun handleAddParticipant(contactList: ArrayList<Contact>?) {
        var choose = 0
        if (contactList != null) {
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    val contactId = contact.id

                    val inviteList = Array(1) { Invitee(contactId, 2) }
                    inviteList[0].id = contactId.toString()


                    val request = RequestCreatePublicThread.Builder(
                        ThreadType.Constants.PUBLIC_GROUP,
                        Arrays.asList(*inviteList),
                        "thread_${Date().time}"
                    )
                        .withDescription("desc at " + Date())
                        .title("My Public Group _${Date()}")
                        .withImage("http://google.com")
                        .build()

                    val uniqueId = mainViewModel.createPublicThread(request)

                    fucCallback[ConstantMsgType.ADD_PARTICIPANT] = uniqueId
                    choose++
                    if (choose == 1) {
                        fucCallback["ADD_PARTICIPANT_ID"] = contactId.toString()
                        break
                    }
                }
            }
        }

        if (choose == 0) {

            val pos = getPositionOf(ConstantMsgType.ADD_PARTICIPANT)

            showNoContactToast()

            deactiveFunction(pos)

            changeFunTwoState(pos, Method.DEACTIVE)


        }
    }

    private fun removeContact() {
//        val
        val requestAddContact = RequestAddContact.Builder()
            .firstName(faker.name().firstName())
            .lastName(faker.name().lastName())
            .email(faker.lordOfTheRings().character() + "@gmail.com")
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
                        RequestThreadInnerMessage.Builder(
                            TextMessageType.Constants.TEXT
                        ).message(faker.music().genre()).build()
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

            showNoContactToast()
            changeIconReceive(position)
            changeFunOneState(position, Method.DEACTIVE)
            changeFunTwoState(position, Method.DEACTIVE)
            changeFunThreeState(position, Method.DEACTIVE)


        }
    }

    private fun showToast(message: String) {

        try {
            activity?.runOnUiThread {

                Toast.makeText(
                    activity,
                    message,
                    Toast.LENGTH_LONG
                ).show()


            }
        } catch (e: Exception) {
            Log.e("MTAG", e.message)

        }
    }

    private fun showNoContactToast() {

        val message = "You haven't any contact with user (hasUser = true)"

        try {
            activity?.runOnUiThread {

                Toast.makeText(
                    activity,
                    message,
                    Toast.LENGTH_LONG
                ).show()


            }
        } catch (e: Exception) {
            Log.e("MTAG", e.message)

        }
    }

    //Response from getContact
    /**
     *
     * */
    private fun unBlockNow() {

        val pos = getPositionOf(ConstantMsgType.UNBLOCK_CONTACT)

        if (!blockedList.isNullOrEmpty()) {

            val targetContact: BlockedContact = blockedList[0]

            val blockOptionFragment =
                BlockOptionFragment()

            val requestUnBlockBuilder = RequestUnBlock.Builder()

            deactiveFunction(pos)

            val onBlockJob = Runnable {

                blockOptionFragment.dismiss()

                changeIconSend(pos)

                changeFunOneState(pos, Method.DONE)

                changeFunTwoState(pos, Method.RUNNING)

                val requestUnBlock = requestUnBlockBuilder.build()

                fucCallback[ConstantMsgType.UNBLOCK_CONTACT] = mainViewModel.unBlock(requestUnBlock)


            }


            val bundle = Bundle().apply {
                putLong(BlockOptionFragment.USER_ID, targetContact.contactVO?.userId ?: 0)
                putLong(BlockOptionFragment.CONTACT_ID, targetContact.contactVO?.id ?: 0)
                putLong(BlockOptionFragment.BLOCK_ID, targetContact.blockId)
                putString(
                    BlockOptionFragment.CONTACT_NAME,
                    "${targetContact.firstName} ${targetContact.lastName}"
                )
                putString(
                    BlockOptionFragment.ARG_TITLE,
                    "UNBLOCK"
                )

            }

            blockOptionFragment.arguments = bundle

            blockOptionFragment.setListener(object : BlockOptionFragment.IBlocOption {
                override fun doWithUserIdSelected(userId: Long) {

                    requestUnBlockBuilder.userId(userId)

                    onBlockJob.run()

                }

                override fun doWithBlockIdSelected(blockId: Long) {

                    requestUnBlockBuilder.blockId(blockId)

                    onBlockJob.run()
                }

                override fun doWithContactIdSelected(contactId: Long) {

                    requestUnBlockBuilder.contactId(contactId)

                    onBlockJob.run()

                }
            })

            blockOptionFragment.show(childFragmentManager, "BLOCK_OPTION")


        } else {
            deactiveFunction(pos)
            showToast("There is no contact in block list")
        }

    }


    private fun handleUpdateContact(contactList: ArrayList<Contact>?) {

        var selected = false


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

                    fucCallback[ConstantMsgType.UPDATE_CONTACT] =
                        mainViewModel.updateContact(requestUpdateContact)
                    selected = true
                    break
                }
            }
        }


        if (!selected) {

            showNoContactToast()

            val position = getPositionOf(ConstantMsgType.UPDATE_CONTACT)

            deactiveFunction(position)

            changeFunTwoState(position, Method.DEACTIVE)

            return

        }
    }

    private fun getParticipant() {

        val pos = getPositionOf(ConstantMsgType.GET_PARTICIPANT)
        changeIconSend(pos)
        changeFunOneState(pos, Method.RUNNING)
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
        requestThread.count = 10
        fucCallback[ConstantMsgType.SEND_MESSAGE] = mainViewModel.getThreads(requestThread)

//        val requestMessage = RequestMessage.Builder()
//        mainViewModel.sendTextMsg()ConstantMsgType.UNBLOCK_CONTACT
    }


    private fun unMuteThread() {
        val position = 16
        changeIconSend(position)
        changeFunOneState(position, Method.RUNNING)
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        requestGetContact.count = 10
        fucCallback[ConstantMsgType.UNMUTE_THREAD] = mainViewModel.getContact(requestGetContact)

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
        fucCallback[ConstantMsgType.REMOVE_PARTICIPANT] = mainViewModel.getThreads(requestThread)


    }

    private fun blockContact() {

        val pos = getPositionOf(ConstantMsgType.BLOCK_CONTACT)
        changeIconSend(pos)
        changeFunOneState(position = pos, state = Method.RUNNING)
        var uniqueId: String = ""

        when {
            contactList.isEmpty() -> {

                val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
                uniqueId = mainViewModel.getContact(requestGetContact)

            }
            threadsList.isEmpty() -> {
                changeFunOneState(position = pos, state = Method.DONE)

                changeFunTwoState(pos, Method.RUNNING)
                val requestThread: RequestThread = RequestThread.Builder().build()
                uniqueId = mainViewModel.getThreads(requestThread)

            }
            else -> {
                changeFunOneState(pos, Method.DONE)
                changeFunTwoState(pos, Method.DONE)


                blockNow()

            }
        }

        if (uniqueId.isNotEmpty()) {
            fucCallback[ConstantMsgType.BLOCK_CONTACT] = uniqueId
        }

    }

    private fun blockNow() {

        val pos = getPositionOf(ConstantMsgType.BLOCK_CONTACT)

        val targetContact = getBlockableContact()

        val targetThread = getBlockableThread()

        val blockOptionFragment =
            BlockOptionFragment()

        val args = Bundle().apply {
            putString(BlockOptionFragment.ARG_TITLE, "Block")
            putLong(BlockOptionFragment.USER_ID, targetContact?.userId ?: 0)
            putLong(BlockOptionFragment.CONTACT_ID, targetContact?.id ?: 0)
            putLong(BlockOptionFragment.THREAD_ID, targetThread?.id ?: 0)
            putString(BlockOptionFragment.THREAD_NAME, targetThread?.title ?: "")
            putString(
                BlockOptionFragment.CONTACT_NAME,
                "${targetContact?.firstName} ${targetContact?.lastName}"
            )
        }

        blockOptionFragment.arguments = args

        val requestBlockBuilder = RequestBlock.Builder()

        deactiveFunction(pos)

        val blockJob = Runnable {

            blockOptionFragment.dismiss()

            changeIconSend(pos)
            changeFunOneState(pos, Method.DONE)
            changeFunTwoState(pos, Method.DONE)
            changeFunThreeState(pos, Method.RUNNING)

            val request = requestBlockBuilder.build()


            fucCallback[ConstantMsgType.BLOCK_CONTACT] = mainViewModel.blockContact(request)

        }

        blockOptionFragment.setListener(object : BlockOptionFragment.IBlocOption {

            override fun doWithUserIdSelected(userId: Long) {

                requestBlockBuilder.userId(userId)

                blockJob.run()

            }

            override fun doWithThreadIdSelected(threadId: Long) {

                requestBlockBuilder.threadId(threadId)

                blockJob.run()
            }

            override fun doWithContactIdSelected(contactId: Long) {

                requestBlockBuilder.contactId(contactId)

                blockJob.run()
            }

        })

        blockOptionFragment.show(childFragmentManager, "BLOCK_OPTION")


    }


    private fun getBlockableContact(): Contact? {

        if (!contactList.isNullOrEmpty()) {

            val shuffled = contactList.shuffled()

            for (contact: Contact in shuffled) {

                if (contact.isHasUser && !contact.blocked) {

                    return contact
                }
            }
        }

        showNoContactToast()

        return null

    }

    private fun getBlockableThread(): Thread? {


        if (!threadsList.isNullOrEmpty()) {

            val shuffled = threadsList.shuffled()

            for (thread in shuffled) {

                if (!thread.isGroup) {

                    return thread

                }

            }

        }

        showToast("There isn't any pv thread")

        return null

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
        requestGetContact.count = 10
        val uniqueId = mainViewModel.getContact(requestGetContact)
        fucCallback[ConstantMsgType.GET_CONTACT] = uniqueId
        var position = 1
        changeIconSend(position)
        changeFunOneState(1, Method.RUNNING)
    }

    private fun createThread() {


        if (!contactList.isNullOrEmpty()) {


            createThreadNow()

        } else {

            val pos = getPositionOf(ConstantMsgType.CREATE_THREAD)
            changeIconSend(pos)
            changeFunOneState(pos, Method.RUNNING)

            val requestGetContact: RequestGetContact = RequestGetContact
                .Builder()
                .build()

            fucCallback[ConstantMsgType.CREATE_THREAD] = mainViewModel.getContact(requestGetContact)

        }


    }

    private fun createThreadNow() {

        val pos = getPositionOf(ConstantMsgType.CREATE_THREAD)

        deactiveFunction(pos)

        changeFunOneState(pos, Method.DONE)

        val contacts = getRandomContactsWithUser(count = getRandomCount())

        if (!contacts.isNullOrEmpty()) {


            var requestBuilder: RequestCreateThread.Builder? = null

            val createThreadOptionFragment = CreateThreadOptionFragment()

            val arg = Bundle().apply {

                putString(
                    CreateThreadOptionFragment.MODE_KEY,
                    CreateThreadOptionFragment.MODE_NORMAL
                )
            }

            createThreadOptionFragment.arguments = arg


            val createThreadJob = Runnable {

                createThreadOptionFragment.dismiss()

                changeIconSend(pos)

                changeFunOneState(pos, Method.DONE)

                changeFunTwoState(pos, Method.RUNNING)

                val request = requestBuilder
                    ?.title("Test App ${Date().time}")
                    ?.withDescription("created for test")
                    ?.build()

                showToast("$request")

                fucCallback[ConstantMsgType.CREATE_THREAD] =
                    mainViewModel.createThread(request)

            }


            createThreadOptionFragment.setListener(object :
                CreateThreadOptionFragment.ICreateThreadOption {

                override fun onSelected(threadType: Int, inviteeType: Int) {

                    val invitees = createInviteeFromContactList(ArrayList(contacts), inviteeType)

                    if (invitees.isNullOrEmpty()) {
                        deactiveFunction(pos)
                        showNoContactToast()
                        return
                    }
                    if (threadType == ThreadType.Constants.NORMAL) {
                        val invitee = invitees[0]
                        invitees.clear()
                        invitees.add(invitee)
                    }
                    requestBuilder = RequestCreateThread.Builder(threadType, invitees)

                    createThreadJob.run()

                }
            })

            createThreadOptionFragment.show(childFragmentManager, "CREATE_THREAD_OPTION")

        } else {
            deactiveFunction(pos)
            showNoContactToast()
        }


    }


    private fun createPublicThreadNow(uniqueName: String?) {

        val pos = getPositionOf(ConstantMsgType.CREATE_PUBLIC_THREAD)

        deactiveFunction(pos)

        changeFunOneState(pos, Method.DONE)

        changeFunTwoState(pos, Method.DONE)

        val contacts = getRandomContactsWithUser(count = getRandomCount())

        if (!contacts.isNullOrEmpty()) {

            var requestBuilder: RequestCreatePublicThread.Builder? = null

            val createThreadOptionFragment = CreateThreadOptionFragment()

            val arg = Bundle().apply {

                putString(
                    CreateThreadOptionFragment.MODE_KEY,
                    CreateThreadOptionFragment.MODE_PUBLIC
                )
            }

            createThreadOptionFragment.arguments = arg

            val createThreadJob = Runnable {

                createThreadOptionFragment.dismiss()

                changeIconSend(pos)

                changeFunOneState(pos, Method.DONE)

                changeFunTwoState(pos, Method.DONE)

                changeFunThreeState(pos, Method.RUNNING)

                val request = requestBuilder
                    ?.title("Test App ${Date().time}")
                    ?.withDescription("created for test")
                    ?.build()

                fucCallback[ConstantMsgType.CREATE_PUBLIC_THREAD] =
                    mainViewModel.createPublicThread(request)

            }

            createThreadOptionFragment.setListener(object :
                CreateThreadOptionFragment.ICreateThreadOption {

                override fun onSelected(threadType: Int, inviteeType: Int) {

                    val invitees = createInviteeFromContactList(ArrayList(contacts), inviteeType)

                    if (invitees.isNullOrEmpty()) {
                        deactiveFunction(pos)
                        showNoContactToast()
                        return
                    }

                    requestBuilder =
                        RequestCreatePublicThread.Builder(threadType, invitees, uniqueName)

                    createThreadJob.run()

                }
            })

            createThreadOptionFragment.show(childFragmentManager, "CREATE_THREAD_OPTION")


        } else {
            deactiveFunction(pos)
            showNoContactToast()
        }

    }


    private fun getRandomContactWithLinkedUser(): Contact? =
        if (contactList.filter { contact -> contact.isHasUser }.isNullOrEmpty())
            null
        else contactList.filter { contact -> contact.isHasUser }.shuffled()[0]


    private fun getRandomContactsWithUser() =
        if (contactList.filter { contact -> contact.isHasUser }.isNullOrEmpty())
            null
        else contactList.filter { contact -> contact.isHasUser }.shuffled()

    private fun getRandomContactsWithUser(count: Int = 2): List<Contact>? {

        return if (contactList.filter { contact -> contact.isHasUser }.isNullOrEmpty())
            null else {
            val randomContacts = contactList.filter { contact -> contact.isHasUser }.shuffled()

            if (randomContacts.size < count - 1) randomContacts
            else randomContacts.subList(0, count)

        }
    }


    private fun createThreadWithForwMessage() {

        val pos = getPositionOf(ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG)

        changeIconSend(pos)
        changeFunOneState(pos, Method.RUNNING)
        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.CREATE_THREAD_WITH_FORW_MSG] =
            mainViewModel.getContact(requestGetContact)

    }

    private fun createThreadOwnerGroup(inviteList: ArrayList<Invitee>) {

        val list = Array(1) { Invitee(inviteList[0].id, 2) }

        val uniqueId = mainViewModel.createThread(
            ThreadType.Constants.OWNER_GROUP, list, "nothing", "", "", ""
        )
        fucCallback[ConstantMsgType.CREATE_THREAD_OWNER_GROUP] = uniqueId
    }

    private fun createThreadPublicGroup(inviteList: ArrayList<Invitee>) {
        val list = Array(1) { Invitee(inviteList[0].id, 2) }

        val uniqueId = mainViewModel.createThread(
            ThreadType.Constants.PUBLIC_GROUP, list, "nothing", "", "", ""
        )
        fucCallback[ConstantMsgType.CREATE_THREAD_PUBLIC_GROUP] = uniqueId
    }

    private fun createThreadChannelGroup(inviteList: ArrayList<Invitee>) {
        val list = Array(1) { Invitee(inviteList[0].id, 2) }

        val uniqueId = mainViewModel.createThread(
            ThreadType.Constants.CHANNEL_GROUP, list, "nothing", "", "", ""
        )
        fucCallback[ConstantMsgType.CREATE_THREAD_CHANNEL_GROUP] = uniqueId

    }

    private fun createThreadChannel(inviteList: ArrayList<Invitee>) {

        val list = Array(1) { Invitee(inviteList[0].id, 2) }

        val uniqueId = mainViewModel.createThread(
            ThreadType.Constants.CHANNEL, list, "nothing", "", "", ""
        )
        fucCallback[ConstantMsgType.CREATE_THREAD_CHANNEL] = uniqueId
    }


    private fun clearHistory() {


        changeIconSend(22)

        changeFunOneState(22, Method.RUNNING)


        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        val uniqueId = mainViewModel.getThreads(requestGetThreads)

        fucCallback[ConstantMsgType.CLEAR_HISTORY] = uniqueId


    }

    private fun createThreadWithMessage() {

        changeIconSend(27)

        changeFunOneState(27, Method.RUNNING)

        val requestGetContact: RequestGetContact = RequestGetContact
            .Builder()
            .build()

        fucCallback[ConstantMsgType.CREATE_THREAD_WITH_MSG] =
            mainViewModel.getContact(requestGetContact)


    }

    private fun deleteMultipleMessage() {

        changeIconSend(26)

        changeFunOneState(26, Method.RUNNING)

        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        fucCallback[ConstantMsgType.DELETE_MULTIPLE_MESSAGE] =
            mainViewModel.getThreads(requestGetThreads)


    }


    private fun getAdminsList() {


        changeIconSend(23)


        changeFunOneState(23, Method.RUNNING)


        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        fucCallback[ConstantMsgType.GET_ADMINS_LIST] = mainViewModel.getThreads(requestGetThreads)


//        // positionUniqueIds[23] = ArrayList()
//
//        // positionUniqueIds[23]?.add(fucCallback[ConstantMsgType.GET_ADMINS_LIST]!!)

    }

    private fun removeAdminRoles() {


        changeIconSend(25)

        changeFunOneState(25, Method.RUNNING)

        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        val uniqueId = mainViewModel.getThreads(requestGetThreads)

        fucCallback[ConstantMsgType.REMOVE_ADMIN_ROLES] = uniqueId


    }


    private fun addAdminRoles() {

        changeIconSend(24)

        changeFunOneState(24, Method.RUNNING)

        val requestGetThreads: RequestThread = RequestThread.Builder()
            .build()

        val uniqueId = mainViewModel.getThreads(requestGetThreads)

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


            if (targetParticipant != null) {


                val parIdList = ArrayList<Long>()

                parIdList.add(targetParticipant.id)

                val requestRemoveParticipant =
                    RequestRemoveParticipants.Builder(response.subjectId, parIdList)
                        .build()

                fucCallback[ConstantMsgType.REMOVE_PARTICIPANT] =
                    mainViewModel.removeParticipant(requestRemoveParticipant)


            } else {

                targetParticipant = participants.last()

                showToast("No regular participant found. Removing an Admin...")

                val parIdList = ArrayList<Long>()

                parIdList.add(targetParticipant.id)

                val requestRemoveParticipant =
                    RequestRemoveParticipants.Builder(response.subjectId, parIdList)
                        .build()

                fucCallback[ConstantMsgType.REMOVE_PARTICIPANT] =
                    mainViewModel.removeParticipant(requestRemoveParticipant)


            }


        } else {

            showToast("No participant found to remove")

            val pos = getPositionOf(ConstantMsgType.REMOVE_PARTICIPANT)

            deactiveFunction(pos)

            changeFunThreeState(pos, Method.DEACTIVE)


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
    private fun handleGetContactForCreateThread() {


        var choose = 0

        if (!contactList.isNullOrEmpty()) {

            for (contact: Contact in contactList.shuffled()) {

                if (contact.isHasUser) {
                    val contactId = contact.id
                    val inviteList = ArrayList<Invitee>()
                    inviteList.add(Invitee(contactId, InviteType.Constants.TO_BE_USER_CONTACT_ID))

                    choose++



                    break
                }
            }
        }


        if (choose == 0) {

//            showNoContactToast()
//
//            deactiveFunction(pos)
//
//            changeFunOneState(pos, Method.DEACTIVE)
//            changeFunTwoState(pos, Method.DEACTIVE)
//            changeFunThreeState(pos, Method.DEACTIVE)
//            changeFunFourState(pos, Method.DEACTIVE)


        }
    }


    private fun unBlockContact() {
        // get Contact
        // block contact with 3 params
        //
        val pos = getPositionOf(ConstantMsgType.UNBLOCK_CONTACT)
        changeIconSend(pos)
        changeFunOneState(pos, Method.RUNNING)

        if (blockedList.isNullOrEmpty()) {

            val requestBlockList = RequestBlockList.Builder().build()
            fucCallback[ConstantMsgType.UNBLOCK_CONTACT] =
                mainViewModel.getBlockList(requestBlockList)

        } else {

            changeFunOneState(pos, Method.DONE)

            unBlockNow()

        }

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
        requestThread.count = 10;
        fucCallback[ConstantMsgType.GET_THREAD] = mainViewModel.getThreads(requestThread)

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
            deactiveFunction(position)
        }


    }

    private fun deactiveFunction(pos: Int) {
        functionAdapter.deActivateFunction(pos)
    }

    /* visibility of progress bar*/
    private fun changeIconSend(position: Int) {

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

    fun getRandomCount() = Random.nextInt(1, 20)

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

    private fun setLogsToPosition(position: Int) {


        /**
         *
         * 1. loop through all received logs
         *
         * 2. loop through all uniqueIds of specific function at specific position
         *
         * 3. Map logs to functions by uniqueId
         *
         *
         * */

        val listOfLogs = mainViewModel.listOfLogs

        positionLogs[position] = ArrayList()

        //for each unique id that set to this position
        positionUniqueIds[position]?.forEach {

                uniqueId ->

            //for each log that received
            for (log in listOfLogs) {

                if (log.uniqueId == uniqueId) {

                    if (positionLogs[position] == null)
                        positionLogs[position] = ArrayList()

                    positionLogs[position]?.add(log)

                    methods[position].addLog(log)
                }
            }
        }

    }


}

