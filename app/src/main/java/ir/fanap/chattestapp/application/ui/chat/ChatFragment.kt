package ir.fanap.chattestapp.application.ui.chat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.fanap.chattestapp.R
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.support.design.circularreveal.CircularRevealCompat
import android.support.design.circularreveal.cardview.CircularRevealCardView
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import com.fanap.podchat.ProgressHandler
import com.fanap.podchat.mainmodel.*
import com.fanap.podchat.model.*
import com.fanap.podchat.requestobject.*
import com.fanap.podchat.util.FileUtils
import com.fanap.podchat.util.InviteType
import com.fanap.podchat.util.ThreadType
import com.github.javafaker.Faker
import ir.fanap.chattestapp.SpecificLogFragment
import ir.fanap.chattestapp.application.ui.MainViewModel
import ir.fanap.chattestapp.application.ui.TestListener
import ir.fanap.chattestapp.application.ui.util.ConstantMsgType
import ir.fanap.chattestapp.application.ui.util.SmartHashMap
import ir.fanap.chattestapp.bussines.model.LogClass
import kotlinx.android.synthetic.main.fragment_chat.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.util.ArrayList
import kotlin.math.hypot
import kotlin.math.max

@SuppressLint("SetTextI18n")
class ChatFragment : Fragment(), TestListener {

    private lateinit var tvPickedFileName: TextView
    private val PICKFILE_REQUEST_CODE: Int = 2

    private var chatReady: Boolean = false

    private lateinit var atach_file: AppCompatImageView
    private lateinit var mainViewModel: MainViewModel
    private var fucCallback: SmartHashMap<String, String> = SmartHashMap()
    private val REQUEST_TAKE_PHOTO = 0
    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    private lateinit var contextFrag: Context
    private var imageUri: Uri? = null
    private var fileUri: Uri? = null

    private lateinit var txtViewFileMsg: TextView
    private lateinit var tvReplyFileMessageStatus: TextView
    private lateinit var tvSendFileMessageStatus: TextView
    private lateinit var tvUploadFileStatus: TextView
    private lateinit var txtViewUploadFile: TextView
    private lateinit var txtViewUploadImage: TextView
    private lateinit var txtViewReplyFileMsg: TextView
    private lateinit var tvSendLocationMessageStatus: TextView
    private lateinit var tvUploadImageStatus: TextView
    private lateinit var imageView_tickOne: AppCompatImageView
    private lateinit var imageView_tickTwo: AppCompatImageView
    private lateinit var imageView_tickThree: AppCompatImageView
    private lateinit var imageView_tickFour: AppCompatImageView
    private lateinit var progressBarUploadImage: ProgressBar
    private lateinit var progressLocationMessage: ProgressBar
    private lateinit var progressBarReplyFileMsg: ProgressBar
    private lateinit var progressBarSendFileMessage: ProgressBar
    private lateinit var progressBarUploadFile: ProgressBar
    private lateinit var contentProgressLocationMessage: ProgressBar
    private lateinit var contentProgressFileMessage: ProgressBar
    private lateinit var contentProgressUploadFile: ProgressBar
    private lateinit var contentProgressUploadImage: ProgressBar
    private lateinit var contentProgressReplyFileMessage: ProgressBar
    private lateinit var buttonUploadImage: AppCompatImageView


    private lateinit var buttonFileMsg: AppCompatImageView
    private lateinit var buttonUploadFile: AppCompatImageView
    private lateinit var buttonReplyFileMsg: AppCompatImageView
    private lateinit var btnSendLocationMessage: AppCompatImageView


    private lateinit var imgViewMap: AppCompatImageView
    private lateinit var imgViewCheckLocationMessage: AppCompatImageView
    private lateinit var imageViewSelectedPic: AppCompatImageView
    private lateinit var circularCard: CircularRevealCardView

    private val positionUniqueId: HashMap<Int, ArrayList<String>> = HashMap()


//    private lateinit var imBtnShowFileMessageLog : ImageButton
//    private lateinit var imBtnShowUploadFileLog : ImageButton
//    private lateinit var imBtnShowFileMessageLog : ImageButton
//    private lateinit var imBtnShowFileMessageLog : ImageButton
//    private lateinit var imBtnShowFileMessageLog : ImageButton


    var isOpen = false

    private val faker: Faker = Faker()

    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }


        const val FILE_MESSAGE = 1
        const val UPLOAD_FILE = 2
        const val REPLY_FILE_MESSAGE = 3
        const val UPLOAD_IMAGE = 4
        const val LOCATION_MESSAGE = 5


    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        contextFrag = context!!
    }

    /*private <T extends View & CircularRevealWidget> void circularRevealFromMiddle(@NonNull final T circularRevealWidget) {
    circularRevealWidget.post(new Runnable() {
        @Override
        public void run() {
            int viewWidth = circularRevealWidget.getWidth();
            int viewHeight = circularRevealWidget.getHeight();

            int viewDiagonal = (int) Math.sqrt(viewWidth * viewWidth + viewHeight * viewHeight);

            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    CircularRevealCompat.createCircularReveal(circularRevealWidget, viewWidth / 2, viewHeight / 2, 10, viewDiagonal / 2),
                    ObjectAnimator.ofArgb(circularRevealWidget, CircularRevealWidget.CircularRevealScrimColorProperty.CIRCULAR_REVEAL_SCRIM_COLOR, Color.RED, Color.TRANSPARENT));

            animatorSet.setDuration(5000);
            animatorSet.start();
        }
    });
}*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_chat, container, false)





        initViews(view)

        setListeners()


//        val appCmpImgViewFolder: AppCompatImageView = view.findViewById(R.id.appCompatImageView_folder)

        val appCompatImageViewGallery: AppCompatImageView =
            view.findViewById(R.id.appCompatImageView_gallery)

        val appCompatImageViewFile: AppCompatImageView =
            view.findViewById(R.id.appCompatImageView_folder)



        appCompatImageViewFile.setOnClickListener {

            runScaleAnim(it)

            openFilePicker()

        }

        appCompatImageViewGallery.setOnClickListener {
            runScaleAnim(it)
            openImagePicker()
        }




        atach_file.setOnClickListener {


            runScaleAnim(it)


            if (!isOpen) {

                openCircularCard()

            } else {

                closeCircularCard()

            }


        }


        return view
    }


    private fun openFilePicker() {


        val intent = Intent(Intent.ACTION_GET_CONTENT)

        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.type = "*/*"

        val pickIntent = Intent.createChooser(intent, "Choose a file")

        startActivityForResult(pickIntent, PICKFILE_REQUEST_CODE)
    }

    private fun changeColor(
        image: AppCompatImageView?,
        color: Int
    ) {

        try {
            activity?.runOnUiThread {

                image?.setColorFilter(
                    ContextCompat.getColor(
                        activity!!,
                        color
                    )
                )

            }
        } catch (e: Exception) {
        }
    }

    private fun runScaleAnim(it: View) {
        it.animate().setDuration(150L).setInterpolator(LinearInterpolator())
            .withEndAction {

                it.animate().setDuration(150L).setInterpolator(LinearInterpolator())
                    .scaleX(1f).scaleY(1f).start()

            }
            .scaleX(0.7f).scaleY(0.7f).start()
    }

    private fun closeCircularCard() {

        val x = circularCard.left
        val y = circularCard.bottom

        val endRadius = max(circularCard.width, circularCard.height)
        val startRadius = 0


        val anim: Animator =
            CircularRevealCompat.createCircularReveal(
                circularCard,
                x.toFloat(),
                y.toFloat(),
                startRadius.toFloat(),
                endRadius.toFloat()
            )


        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                circularCard.visibility = View.GONE
            }
        })


        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.duration = 150
        anim.start()
        isOpen = false

        changeColor(atach_file, R.color.grey_active)

    }

    private fun openCircularCard() {

        val x = circularCard.left
        val y = circularCard.bottom

        val startRadius = 0

        val endRadius = hypot(circularCard.width.toDouble(), circularCard.height.toDouble())

        val anim: Animator =
            CircularRevealCompat.createCircularReveal(
                circularCard,
                x.toFloat(),
                y.toFloat(),
                startRadius.toFloat(),
                endRadius.toFloat()
            )


        anim.interpolator = AccelerateDecelerateInterpolator()
        circularCard.visibility = View.VISIBLE
        circularCard.requestFocus()
        anim.duration = 150
        anim.start()
        isOpen = true

        changeColor(atach_file, R.color.blue_inactive)
    }

    private fun setListeners() {


        txtViewFileMsg.setOnClickListener {

            sendFileMsg()
        }
        txtViewUploadFile.setOnClickListener { uploadFile() }
        txtViewReplyFileMsg.setOnClickListener { replyFileMsg() }
//        buttonUploadImage.setOnClickListener {
//            //            uploadImage()
//            uploadImageProgress()
//        }


        buttonFileMsg.setOnClickListener {

            sendFileMsg()

        }

        buttonUploadFile.setOnClickListener {

            uploadFile()
        }

        buttonReplyFileMsg.setOnClickListener {

            replyFileMsg()

        }

        buttonUploadImage.setOnClickListener {

            //   uploadImage()

            uploadImageProgress()
//
        }

        btnSendLocationMessage.setOnClickListener {


            sendLocationMessage()


        }


    }

    private fun sendLocationMessage() {


        if (!chatReady) return



        changeToNormalState(
            tickImage = imgViewCheckLocationMessage,
            mainView = constraintSendLocationMessage,
            contentProgress = contentProgressLocationMessage,
            textView = tvSendLocationMessageStatus,
            progressBar = progressLocationMessage

        )



        showView(contentProgressLocationMessage)

        val requestThread = RequestThread
            .Builder()
            .build()


        setTextOf(tvSendLocationMessageStatus, "Get List of threads...")


        fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE] = mainViewModel.getThread(requestThread)


    }


    override fun onGetThread(chatResponse: ChatResponse<ResultThreads>?) {
        super.onGetThread(chatResponse)



        if (chatResponse?.uniqueId == fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE]) {





            prepareSendLocationMessage(chatResponse?.result?.threads)


        }


    }


    override fun onNewMessage(response: ChatResponse<ResultNewMessage>?) {
        super.onNewMessage(response)
    }


    private fun prepareSendLocationMessage(threads: List<Thread>?) {


        try {
            activity?.runOnUiThread {


                setTextOf(tvSendLocationMessageStatus, "Get Static Map From Neshan API")


                increaseProgressOf(progressLocationMessage)

            }
        } catch (e: Exception) {
            Log.e("MTAG",e.message)
        }




        if (threads!!.isNotEmpty()) {


            val targetThreadId = threads[0].id

//            val center = "-1"
            val center = "35.7003510,51.3376472"

            val requestLocationMessage = RequestLocationMessage.Builder()
                .center(center)
                .message("This is location ")
                .activity(activity)
                .threadId(targetThreadId)
                .build()

            fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE] = mainViewModel
                .sendLocationMessage(
                    requestLocationMessage,
                    object : ProgressHandler.sendFileMessage {


                        override fun onProgressUpdate(
                            uniqueId: String?,
                            bytesSent: Int,
                            totalBytesSent: Int,
                            totalBytesToSend: Int
                        ) {
                            super.onProgressUpdate(
                                uniqueId,
                                bytesSent,
                                totalBytesSent,
                                totalBytesToSend
                            )


                            try {
                                activity?.runOnUiThread {


                                    setTextOf(
                                        tvSendLocationMessageStatus,
                                        "Uploading... $bytesSent%"
                                    )


                                    updateProgressBar(progressLocationMessage, bytesSent)


                                }
                            } catch (e: Exception) {
                                Log.e("MTAG", e.message)
                            }


                        }

                        override fun onFinishImage(
                            json: String?,
                            chatResponse: ChatResponse<ResultImageFile>?
                        ) {
                            super.onFinishImage(json, chatResponse)

                            Log.d("MTAG", "finish upload")
//                        progressLocationMessage.incrementProgressBy(100)
//                        tvSendLocationMessageStatus.setTextColor(ContextCompat.getColor(context!!,R.color.green_active))
//                        tvSendLocationMessageStatus.text = "Upload Location Image Done"
//
//
//                        imgViewCheckLocationMessage.setImageResource(R.drawable.ic_round_done_all_24px)
//                        imgViewCheckLocationMessage.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorPrimary))

                        }

                        override fun onError(jsonError: String?, error: ErrorOutPut?) {
                            super.onError(jsonError, error)

                            Log.e("MTAG", "upload error $jsonError")

                            Toast.makeText(context, jsonError, Toast.LENGTH_LONG).show()


                        }

                    })


        }


    }

    override fun onUploadFile(response: ChatResponse<ResultFile>?) {
        super.onUploadFile(response)


        if(response?.uniqueId == null) return




        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_FILE_MESSAGE]) {


            updateUploadFileMessageStatusOnFileUploaded()


        }

        if (fucCallback[ConstantMsgType.UPLOAD_FILE] == response?.uniqueId) {

            updateUploadFileStatusToDone()


        }

        if (fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] == response?.uniqueId) {


            updateUIUploadFileReplyMessageOnFileUploaded()


        }


    }

    private fun updateUIUploadFileReplyMessageOnFileUploaded() {

        try {
            activity?.runOnUiThread {

                setTextOf(tvReplyFileMessageStatus, "Sending Message")

                changeImageViewColorToGreen(imageView_tickThree)

                changeImageViewResourceDone(imageView_tickThree)

                updateProgressBar(progressBarReplyFileMsg, 95)

            }
        } catch (e: Exception) {
            Log.e("MTAG", e.message)
        }


    }

    private fun updateUploadFileStatusToDone() {


        try {
            activity?.runOnUiThread {

                changeTextAndColorToGreenOf(tvUploadFileStatus, "File Uploaded Successfully")

                changeImageViewResourceToDoneAll(imageView_tickTwo)

                updateProgressBar(progressBarUploadFile)

                hideView(contentProgressUploadFile)


            }
        } catch (e: Exception) {
            Log.e("MTAG", e.message)
        }


    }

    private fun updateUploadFileMessageStatusOnFileUploaded() {

        try {
            activity?.runOnUiThread {


                changeImageViewColorToGreen(imageView_tickOne)

                changeImageViewResourceDone(imageView_tickOne)

                updateProgressBar(progressBarSendFileMessage, 95)

                setTextOf(tvSendFileMessageStatus, "Sending Message...")

            }
        } catch (e: Exception) {
            Log.e("MTAG", e.message)
        }


    }

    override fun onUploadImageFile(content: String?, response: ChatResponse<ResultImageFile>?) {
        super.onUploadImageFile(content, response)


        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE]) {


            handleOnMapImageUploaded()

        }


        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_FILE_MESSAGE]) {


            setTextOf(tvSendFileMessageStatus, "Sending File Message")


        }

        if (fucCallback[ConstantMsgType.UPLOAD_IMAGE] == response?.uniqueId) {


            handleFinishUploadImage()


        }

    }


    private fun handleOnMapImageUploaded() {

        try {
            activity?.runOnUiThread {


                updateProgressBar(progressLocationMessage, 80)

                setTextOf(tvSendLocationMessageStatus, "Send Text Message With File")


                changeImageViewColorToGreen(imgViewCheckLocationMessage)

                changeImageViewResourceDone(imgViewCheckLocationMessage)


            }
        } catch (e: Exception) {
        }


    }

    override fun onGetStaticMap(response: ChatResponse<ResultStaticMapImage>?) {
        super.onGetStaticMap(response)


        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE]) {


            updateLocationMessageStatusOnGetMap(response)

        }
    }

    private fun updateLocationMessageStatusOnGetMap(response: ChatResponse<ResultStaticMapImage>?) {

        try {
            activity?.runOnUiThread {

                setTextOf(tvSendLocationMessageStatus, "Upload Image to server")

                increaseProgressOf(progressLocationMessage)

                val size = FileUtils.getReadableFileSize(response?.result?.bitmap?.byteCount!!)

                setTextOf(tvPickedFileName, "$size")

                showView(imgViewSelectedPic)

                setImageBitmap(imgViewSelectedPic, response?.result?.bitmap)

            }
        } catch (e: Exception) {
            Log.e("MTAG", e.message)
        }


    }

    private fun setImageBitmap(imgViewSelectedPic: AppCompatImageView?, bitmap: Bitmap?) {

        imgViewSelectedPic?.setImageBitmap(bitmap)

    }

    private fun initViews(view: View) {

        imageView_tickOne = view.findViewById(R.id.checkBox_Send_File_Msg)
        imageView_tickTwo = view.findViewById(R.id.checkBox_Upload_File)
        imageView_tickThree = view.findViewById(R.id.checkBox_Reply_File_Msg)
        imageView_tickFour = view.findViewById(R.id.checkBox_ufil)

        buttonUploadImage = view.findViewById(R.id.buttonUploadImage)
        buttonFileMsg = view.findViewById(R.id.buttonFileMsg)
        buttonUploadFile = view.findViewById(R.id.buttonUploadFile)
        buttonReplyFileMsg = view.findViewById(R.id.buttonReplyFileMsg)
        btnSendLocationMessage = view.findViewById(R.id.btnSendLocationMessage)


        imgViewMap = view.findViewById(R.id.imgViewMapStatic)
        tvSendLocationMessageStatus = view.findViewById(R.id.tvSendLocationStatus)
        tvUploadImageStatus = view.findViewById(R.id.tvUploadImageStatus)
        progressLocationMessage = view.findViewById(R.id.progressBarLocationMessage)
        progressBarReplyFileMsg = view.findViewById(R.id.progressBarReplyFileMsg)
        progressBarSendFileMessage = view.findViewById(R.id.progressBarSendFileMessage)
        progressBarUploadFile = view.findViewById(R.id.progressBarUploadFile)
        contentProgressLocationMessage = view.findViewById(R.id.progressSendLocationMessage)
        contentProgressFileMessage = view.findViewById(R.id.progressSendFileMessage)
        contentProgressUploadFile = view.findViewById(R.id.contentProgressUploadFile)
        contentProgressUploadImage = view.findViewById(R.id.contentProgressUploadImage)
        contentProgressReplyFileMessage = view.findViewById(R.id.contentProgressReplyFileMessage)

        txtViewFileMsg = view.findViewById(R.id.TxtViewFileMsg)
        tvSendFileMessageStatus = view.findViewById(R.id.tvSendFileMessageStatus)
        tvReplyFileMessageStatus = view.findViewById(R.id.tvReplayFileMessageStatus)
        tvUploadFileStatus = view.findViewById(R.id.tvUploadFileStatus)
        txtViewUploadFile = view.findViewById(R.id.TxtViewUploadFile)
        txtViewUploadImage = view.findViewById(R.id.TxtViewUploadImage)
        txtViewReplyFileMsg = view.findViewById(R.id.TxtViewReplyFileMsg)
        progressBarUploadImage = view.findViewById(R.id.progressUploadImage)
        atach_file = view.findViewById(R.id.atach_file)
        imgViewCheckLocationMessage = view.findViewById(R.id.checkBoxLocationMessage)


        circularCard = view.findViewById(R.id.ccv_attachment_reveal)

        imageViewSelectedPic = view.findViewById(R.id.imgViewSelectedPic)

        tvPickedFileName = view.findViewById(R.id.tvFileName)


    }

    override fun onSent(response: ChatResponse<ResultMessage>?) {
        super.onSent(response)


        if (fucCallback[ConstantMsgType.REPLY_MESSAGE_ID] == response?.uniqueId) {


            prepareReplyWithMessage(response)

        }


        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE]) {

            handleFinishSendLocationMessage()

        }


        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_FILE_MESSAGE]) {


            handleFinishFileMessage()


        }


        if (fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] == response?.uniqueId) {

            try {
                activity?.runOnUiThread {

                    changeImageViewColorToGreen(imageView_tickThree)

                    changeTextColorToGreen(tvReplyFileMessageStatus)

                    updateProgressBar(progressBarReplyFileMsg, 100)

                    hideView(contentProgressReplyFileMessage)

                    changeImageViewResourceToDoneAll(imageView_tickThree)

                    setTextOf(tvReplyFileMessageStatus, "Message Sent Successfully")

                }
            } catch (e: Exception) {
                Log.e("MTAG", e.message)
            }


        }


    }

    private fun updateProgressBar(progressBar: ProgressBar, progress: Int = 100) {

        progressBar.progress = progress

    }

    private fun increaseProgressOf(progressBar: ProgressBar, progress: Int = 5) {

        progressBar.incrementProgressBy(progress)

    }

    private fun hideView(view: View) {
        view.visibility = View.GONE
    }

    private fun setTextOf(textView: TextView, text: String = "") {
        textView.text = text
    }

    private fun changeTextColorToGreen(textView: TextView) {

        textView.setTextColor(
            ContextCompat.getColor(
                activity!!,
                R.color.green_active
            )
        )
    }

    private fun changeImageViewColor(image: AppCompatImageView?, color: Int) {

        image?.setColorFilter(ContextCompat.getColor(activity!!, color))
    }

    private fun changeImageViewColorToGreen(image: AppCompatImageView?) {

        image?.setColorFilter(ContextCompat.getColor(activity!!, R.color.green_active))
    }

    private fun handleFinishFileMessage() {


        try {

            activity?.runOnUiThread {


                changeImageViewColorToGreen(imageView_tickOne)

                hideView(contentProgressFileMessage)

                changeImageViewResourceToDoneAll(imageView_tickOne)

                updateProgressBar(progressBarSendFileMessage)

                setTextOf(tvSendFileMessageStatus, "Message Sent Successfully")

                changeTextColorToGreen(tvSendFileMessageStatus)

            }
        } catch (e: Exception) {
            Log.e("MTAG", e.message)
        }
    }

    private fun handleFinishSendLocationMessage() {

        activity?.runOnUiThread {

            try {

                updateProgressBar(progressLocationMessage)

                changeImageViewResourceToDoneAll(imgViewCheckLocationMessage)

                changeTextColorToGreen(tvSendLocationMessageStatus)

                setTextOf(tvSendLocationMessageStatus, "Message Sent Successfully!")

                hideView(contentProgressLocationMessage)

            } catch (e: Exception) {
                Log.e("MTAG", e.message)
            }

        }

    }

    private fun prepareReplyWithMessage(response: ChatResponse<ResultMessage>?) {


        try {
            activity?.runOnUiThread {

                setTextOf(tvReplyFileMessageStatus, "Uploading File")


                increaseProgressOf(progressBarReplyFileMsg)

            }
        } catch (e: Exception) {
            Log.e("MTAG", e.message)
        }


        val messageId = response?.result?.messageId
        val threadId = fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID]
        val replyFileMessage = RequestReplyFileMessage
            .Builder(
                "this is replyMessage",
                threadId?.toLong()!!,
                messageId!!,
                fileUri,
                activity
            ).build()



        fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] =
            mainViewModel.replyWithFile(replyFileMessage,
                object : ProgressHandler.sendFileMessage {


                    override fun onProgressUpdate(
                        uniqueId: String?,
                        bytesSent: Int,
                        totalBytesSent: Int,
                        totalBytesToSend: Int
                    ) {
                        super.onProgressUpdate(
                            uniqueId,
                            bytesSent,
                            totalBytesSent,
                            totalBytesToSend
                        )


                        try {
                            activity?.runOnUiThread {


                                setTextOf(tvReplyFileMessageStatus, "Uploading $bytesSent%")


                                if (bytesSent < 95)
                                    updateProgressBar(progressBarReplyFileMsg, bytesSent)

                                if (bytesSent == 100) {

                                    setTextOf(tvReplyFileMessageStatus, "Sending Reply Message")

                                }


                            }
                        } catch (e: Exception) {
                            Log.e("MTAG", e.message)
                        }


                    }
                })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mainViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
                .create(MainViewModel::class.java)

        mainViewModel.observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                if (it == "CHAT_READY") {

                    chatReady = true

                }
            }

        mainViewModel.setTestListener(this)

        fucCallback.onInsertObserver.subscribe { pair ->

            Log.d("QTAG", "INSERTED: $pair")

            saveUniqueId(pair)

        }
    }

    private fun saveUniqueId(pair: Pair<String, String>) {

        val pos = getPosition(pair.first)

        if (positionUniqueId[pos] == null)
            positionUniqueId[pos] = ArrayList()

        positionUniqueId[pos]?.add(pair.second)
    }

    private fun getPosition(position: String): Int {

        return when (position) {

            ConstantMsgType.SEND_FILE_MESSAGE -> 1

            ConstantMsgType.UPLOAD_FILE -> 2

            ConstantMsgType.REPLY_FILE_MESSAGE -> 3
            ConstantMsgType.REPLY_MESSAGE_THREAD_ID -> 3
            ConstantMsgType.REPLY_MESSAGE_ID -> 3

            ConstantMsgType.UPLOAD_IMAGE -> 4

            ConstantMsgType.SEND_LOCATION_MESSAGE -> 5

            else -> -1

        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        imageButtonShowFMLog.setOnClickListener {

            showLogsFor(FILE_MESSAGE)


        }


        imageButtonShowUFLog.setOnClickListener {

            showLogsFor(UPLOAD_FILE)


        }

        imageButtonShowRFMLog.setOnClickListener {

            showLogsFor(REPLY_FILE_MESSAGE)


        }

        imageButtonShowUILog.setOnClickListener {

            showLogsFor(UPLOAD_IMAGE)


        }

        imageButtonShowLMLog.setOnClickListener {

            showLogsFor(LOCATION_MESSAGE)


        }


    }

    private fun showLogsFor(position: Int) {


        Log.d("MTAG", "Clicked on $position")

        val listOfLogs: ArrayList<LogClass> = getLogsForPosition(position)

        val logFragment = SpecificLogFragment()

        val bundle = Bundle()

        bundle.putParcelableArrayList("LOGS", listOfLogs)

        logFragment.arguments = bundle

        logFragment.show(childFragmentManager, "LOG_FRAG")


    }

    private fun getLogsForPosition(position: Int): ArrayList<LogClass> {

        val listOfLogClass = ArrayList<LogClass>()

        val uniqueIds = positionUniqueId[position]


        uniqueIds?.forEach { uniqueId ->

            mainViewModel.listOfLogs.forEach { log ->

                if (log.uniqueId == uniqueId) {

                    listOfLogClass.add(log)

                }
            }


        }



        return listOfLogClass


    }

    override fun onGetContact(response: ChatResponse<ResultContact>?) {
        super.onGetContact(response)


        if (fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] == response?.uniqueId) {

            handleSendFileMsg(response!!.result.contacts)
        }

        if (fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] == response?.uniqueId) {


            handleReplyFileMessageOnGetContact(response)


        }
    }

    private fun handleReplyFileMessageOnGetContact(response: ChatResponse<ResultContact>?) {
        try {
            activity?.runOnUiThread {

                setTextOf(tvReplyFileMessageStatus, "Create Thread with Message")

                increaseProgressOf(progressBarReplyFileMsg)
            }
        } catch (e: Exception) {
            Log.e("MTAG", "Exception: ${e.message}")
        }


        handleReplyFileMessage(response!!.result.contacts)
    }

    override fun onError(chatResponse: ErrorOutPut?) {
        super.onError(chatResponse)

        showToast(chatResponse?.errorMessage!!)

        val uniqueId = chatResponse?.uniqueId


        fucCallback.forEach {

            if (it.value == uniqueId) {

                setErrorOnFunctionInPosition(getPosition(it.key))

            }

        }


    }

    private fun changeToNormalState(

        tickImage: AppCompatImageView,
        mainView: View,
        contentProgress: ProgressBar,
        progressBar: ProgressBar,
        textView: TextView


    ) {

        try {
            activity?.runOnUiThread {

                changeViewBackground(tickImage, android.R.color.transparent)

                changeImageViewResourceToDoneDefault(tickImage)

                changeViewBackground(mainView, R.drawable.background_top_method_item)

                hideView(contentProgress)

                updateProgressBar(progressBar, 0)

                changeViewBackground(textView, android.R.color.transparent)

                changeTextAndColorToGreyOf(textView, "")

            }
        } catch (e: Exception) {
        }

    }


    private fun changeToErrorState(

        tickImage: AppCompatImageView,
        mainView: View,
        progressToHide: ProgressBar,
        textView: TextView


    ) {

        try {
            activity?.runOnUiThread {

                changeViewBackground(tickImage, R.drawable.default_background_rounded_rect)

                changeImageViewResourceToError(tickImage)

                changeViewBackground(mainView, R.drawable.background_top_method_item_error)

                hideView(progressToHide)

                changeViewBackground(textView, R.drawable.default_background_rounded_rect)

                changeTextColor(textView, R.color.red_active)

                appendTextToTextView(textView, " (Failed)")

            }
        } catch (e: Exception) {
        }

    }

    private fun setErrorOnFunctionInPosition(position: Int) {


        when (position) {

            FILE_MESSAGE -> {

                changeToErrorState(
                    tickImage = imageView_tickOne,
                    mainView = constraintSendFileMessage,
                    progressToHide = contentProgressFileMessage,
                    textView = tvSendFileMessageStatus

                )

            }
            UPLOAD_FILE -> {

                changeToErrorState(
                    tickImage = imageView_tickTwo,
                    mainView = constraintUploadFile,
                    progressToHide = contentProgressUploadFile,
                    textView = tvUploadFileStatus

                )


            }
            REPLY_FILE_MESSAGE -> {

                changeToErrorState(
                    tickImage = imageView_tickThree,
                    mainView = constraintReplyFileMessage,
                    progressToHide = contentProgressReplyFileMessage,
                    textView = tvReplyFileMessageStatus

                )

            }
            UPLOAD_IMAGE -> {

                changeToErrorState(
                    tickImage = imageView_tickFour,
                    mainView = constraintUploadImage,
                    progressToHide = contentProgressUploadImage,
                    textView = tvUploadImageStatus

                )


            }
            LOCATION_MESSAGE -> {

                changeToErrorState(
                    tickImage = imgViewCheckLocationMessage,
                    mainView = constraintSendLocationMessage,
                    progressToHide = contentProgressLocationMessage,
                    textView = tvSendLocationMessageStatus

                )


            }


        }


    }


    private fun changeViewBackground(view: View, backgroundResource: Int) {

        view.setBackgroundResource(backgroundResource)

    }

    override fun onCreateThread(response: ChatResponse<ResultThread>?) {
        super.onCreateThread(response)



        if (fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] == response?.uniqueId) {


            handleSendFileMessage(response)

        }



        if (fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID] == response?.uniqueId) {

            handleOnReplyFileMessageThreadCreate(response)

        }
    }

    private fun handleOnReplyFileMessageThreadCreate(response: ChatResponse<ResultThread>?) {

        fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID] =
            response?.result?.thread?.id.toString()

        try {
            activity?.runOnUiThread {

                setTextOf(tvReplyFileMessageStatus, "Thread Created")

                increaseProgressOf(progressBarReplyFileMsg)

            }
        } catch (e: Exception) {
        }


    }

    private fun handleSendFileMessage(response: ChatResponse<ResultThread>?) {


        val requestFileMessage =
            RequestFileMessage.Builder(activity, response!!.result.thread.id, fileUri).build()



        fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] = mainViewModel.sendFileMessage(
            requestFileMessage,
            object : ProgressHandler.sendFileMessage {
                override fun onFinishImage(
                    json: String?,
                    chatResponse: ChatResponse<ResultImageFile>?
                ) {
                    super.onFinishImage(json, chatResponse)



                    try {
                        activity?.runOnUiThread {

                            setTextOf(tvSendFileMessageStatus, "Sending Text Message")

                            updateProgressBar(progressBarSendFileMessage, 95)


                        }
                    } catch (e: Exception) {
                        Log.e("MTAG", e.message)
                    }

                }

                override fun onProgressUpdate(
                    uniqueId: String?,
                    bytesSent: Int,
                    totalBytesSent: Int,
                    totalBytesToSend: Int
                ) {
                    super.onProgressUpdate(
                        uniqueId,
                        bytesSent,
                        totalBytesSent,
                        totalBytesToSend
                    )

                    try {
                        activity?.runOnUiThread {

                            setTextOf(tvSendFileMessageStatus, "Uploading %$bytesSent")

                            if (bytesSent < 95)
                                updateProgressBar(progressBarSendFileMessage, bytesSent)


                        }
                    } catch (e: Exception) {
                        Log.e("MTAG", e.message)
                    }


                }
            })
    }

    private fun handleReplyFileMessage(contactList: ArrayList<Contact>) {


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
                    fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID] = uniqueId!![0]
                    fucCallback[ConstantMsgType.REPLY_MESSAGE_ID] = uniqueId[1]
                    break
                }


            }

            if (choose == 0) {


                showToast("not contact found with user")

                hideView(contentProgressReplyFileMessage)

            }
        }
    }

    private fun handleSendFileMsg(contactList: ArrayList<Contact>) {

        try {
            activity?.runOnUiThread {

                increaseProgressOf(progressBarSendFileMessage)

                setTextOf(tvSendFileMessageStatus, "Creating Thread...")
            }
        } catch (e: Exception) {
        }

        var choose = 0


        for (contact: Contact in contactList) {

            if (contact.isHasUser) {

                val list =
                    Array(1) { Invitee(contact.id, InviteType.Constants.TO_BE_USER_CONTACT_ID) }

                fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] = mainViewModel.createThread(
                    ThreadType.Constants.NORMAL, list, "nothing", ""
                    , "", ""
                )

                choose++

                break
            }
        }

        if (choose == 0) {

            val contactId = 121L

            val inviteList = ArrayList<Invitee>()
            inviteList.add(Invitee(contactId, 1))

            val list = Array(1) { Invitee(inviteList[0].id, 1) }

            val uniqueId = mainViewModel.createThread(
                ThreadType.Constants.NORMAL, list, "nothing", ""
                , "", ""
            )

            fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] = uniqueId

            setTextOf(tvSendFileMessageStatus, "Creating Thread With id 121...")


        }
    }

    private fun sendFileMsg() {


        if (!chatReady) {


            showToast("Chat is not ready")

            return

        }

        if (fileUri != null) {

            if (!chatReady) return



            changeToNormalState(
                tickImage = imageView_tickOne,
                mainView = constraintSendFileMessage,
                contentProgress = contentProgressFileMessage,
                textView = tvSendFileMessageStatus,
                progressBar = progressBarSendFileMessage

            )


            changeTextAndColorToGreyOf(tvSendFileMessageStatus, "Getting Contacts...")

            showView(contentProgressFileMessage)

            val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
            val uniqueId = mainViewModel.getContact(requestGetContact)
            fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] = uniqueId


        } else {

            showToast("Pick a file our image first")

            openCircularCard()


        }


    }

    private fun appendTextToTextView(textView: TextView, text: String) {

        textView.append(text)


    }

    private fun uploadFile() {

        if (!chatReady) {


            showToast("Chat is not ready")


            return
        }

        if (fileUri != null) {


            changeToNormalState(
                tickImage = imageView_tickTwo,
                mainView = constraintUploadFile,
                contentProgress = contentProgressUploadFile,
                textView = tvUploadFileStatus,
                progressBar = progressBarUploadFile

            )


            showView(contentProgressUploadFile)

            setTextOf(tvUploadFileStatus, "Uploading...")

            val requestUploadFile = RequestUploadFile.Builder(activity, fileUri).build()

            fucCallback[ConstantMsgType.UPLOAD_FILE] = mainViewModel.uploadFileProgress(
                requestUploadFile,
                object : ProgressHandler.onProgressFile{

                    override fun onProgress(
                        uniqueId: String?,
                        bytesSent: Int,
                        totalBytesSent: Int,
                        totalBytesToSend: Int
                    ) {
                        super.onProgress(uniqueId, bytesSent, totalBytesSent, totalBytesToSend)

                        try {
                            activity?.runOnUiThread {

                                setTextOf(tvUploadFileStatus, "$bytesSent%")

                                updateProgressBar(progressBarUploadFile, bytesSent)


                            }
                        } catch (e: Exception) {
                        }

                    }

                    override fun onProgressUpdate(bytesSent: Int) {

                    }
                })


        } else {

            showToast("Pick a File")

            openCircularCard()

            atach_file.requestFocus()


        }
    }

    //Chat needs update
    private fun uploadImage() {


        if (imageUri != null) {

            fucCallback[ConstantMsgType.UPLOAD_IMAGE] =
                mainViewModel.uploadImage(activity, imageUri!!)

        } else {

            showToast("PICK IMAGE")

            openImagePicker()

            return
        }
    }

    private fun uploadImageProgress() {


        if (imageUri != null) {


            changeToNormalState(
                tickImage = imageView_tickFour,
                mainView = constraintUploadImage,
                contentProgress = contentProgressUploadImage,
                progressBar = progressBarUploadImage,
                textView = tvUploadImageStatus

            )

            showView(contentProgressUploadImage)

            setTextOf(tvUploadImageStatus, "Uploading...")


            val requestUploadImage: RequestUploadImage = RequestUploadImage
                .Builder(activity, imageUri)
                .build()


            fucCallback[ConstantMsgType.UPLOAD_IMAGE] = mainViewModel.uploadImageProgress(
                requestUploadImage, object : ProgressHandler.onProgress {
                    override fun onProgressUpdate(
                        uniqueId: String?,
                        bytesSent: Int,
                        totalBytesSent: Int,
                        totalBytesToSend: Int
                    ) {
                        super.onProgressUpdate(
                            uniqueId,
                            bytesSent,
                            totalBytesSent,
                            totalBytesToSend
                        )

                        try {
                            activity?.runOnUiThread {

                                updateProgressBar(progressBarUploadImage, progress = bytesSent)

                            }
                        } catch (e: Exception) {
                        }

                    }

                    override fun onFinish(
                        imageJson: String?,
                        chatResponse: ChatResponse<ResultImageFile>?
                    ) {
                        super.onFinish(imageJson, chatResponse)


                    }
                })
        } else {

            openImagePicker()

            showToast("Pick an Image")
        }
    }

    private fun showView(view: View) {
        view.visibility = View.VISIBLE
    }


    private fun changeTextAndColorToGreenOf(textView: TextView, text: String) {

        setTextOf(textView, text)

        changeTextColorToGreen(textView)


    }

    private fun changeTextAndColorToGreyOf(textView: TextView, text: String) {

        setTextOf(textView, text)

        changeTextColor(textView, R.color.grey)


    }

    private fun changeTextColor(textView: TextView, colorResId: Int) {

        textView.setTextColor(ContextCompat.getColor(activity!!, colorResId))

    }

    private fun handleFinishUploadImage() {

        updateProgressBar(progressBarUploadImage)

        hideView(contentProgressUploadImage)

        changeTextAndColorToGreenOf(tvUploadImageStatus, "Image Uploaded")

        changeImageViewResourceToDoneAll(imageView_tickFour)


    }

    private fun showToast(message: String) {

        activity?.runOnUiThread {

            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()

        }
    }


    private fun replyFileMsg() {


        if (!chatReady) {

            showToast("Chat is not Ready")

            return
        }

        if (fileUri == null) {

            showToast("Pick a file")

            openCircularCard()

            atach_file.requestFocus()

            return

        }


        changeToNormalState(
            tickImage = imageView_tickThree,
            mainView = constraintReplyFileMessage,
            contentProgress = contentProgressReplyFileMessage,
            textView = tvReplyFileMessageStatus,
            progressBar = progressBarReplyFileMsg

        )

        showView(contentProgressReplyFileMessage)

        setTextOf(tvReplyFileMessageStatus, "Get Contacts")

        val requestGetContact = RequestGetContact.Builder().build()

        fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] =
            mainViewModel.getContact(requestGetContact)


    }

    private fun changeImageViewResource(image: AppCompatImageView?, resId: Int) {

        image?.setImageResource(resId)

    }

    private fun changeImageViewResourceDone(image: AppCompatImageView?) {


        image?.setImageResource(R.drawable.ic_done_black_24dp)

    }

    private fun changeImageViewResourceToDoneDefault(image: AppCompatImageView?) {

        changeImageViewColor(image, R.color.grey_light)

        image?.setImageResource(R.drawable.ic_done_black_24dp)

    }

    private fun changeImageViewResourceToDoneAll(image: AppCompatImageView?) {


        changeImageViewColorToGreen(image)

        image?.setImageResource(R.drawable.ic_round_done_all_24px)


    }

    private fun changeImageViewResourceToError(imageView: AppCompatImageView) {

        changeImageViewColor(imageView, R.color.red_active)

        imageView.setImageResource(R.drawable.ic_round_close_24px)


    }


    private fun openImagePicker() {

        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(i, REQUEST_SELECT_IMAGE_IN_ALBUM)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (data != null && resultCode == Activity.RESULT_OK) {

            imageViewSelectedPic.visibility = View.VISIBLE


            if (REQUEST_SELECT_IMAGE_IN_ALBUM == requestCode) {

                imageUri = data.data
                fileUri = imageUri
                imageViewSelectedPic.setImageURI(imageUri)


            }

            if (PICKFILE_REQUEST_CODE == requestCode) {

                fileUri = data.data

                changeImageViewResource(imageViewSelectedPic, R.drawable.ic_file)


            }

            if (data.data != null) {

                tvPickedFileName.text = getFileName(data.data!!, data.dataString!!)


            }

            closeCircularCard()

            changeColor(atach_file, R.color.grey_active)

        }
    }

    private fun getFileName(uri: Uri, uriString: String): String {


        val file = File(uriString)

        var displayName = ""

        if (uriString.startsWith("content://")) {

            var cursor: Cursor? = null

            try {
                cursor = activity?.contentResolver?.query(uri, null, null, null, null)

                if (cursor != null && cursor.moveToFirst()) {
                    displayName =
                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        } else if (uriString.startsWith("file://")) {
            displayName = file.name
        }


        return displayName

    }


}

