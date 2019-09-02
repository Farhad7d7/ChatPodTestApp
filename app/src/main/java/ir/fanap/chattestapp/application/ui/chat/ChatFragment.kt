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
import android.graphics.Color
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
import com.fanap.podchat.util.InviteType
import com.fanap.podchat.util.ThreadType
import com.github.javafaker.Faker
import ir.fanap.chattestapp.application.ui.MainViewModel
import ir.fanap.chattestapp.application.ui.TestListener
import ir.fanap.chattestapp.application.ui.util.ConstantMsgType
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
    private var fucCallback: HashMap<String, String> = hashMapOf()
    private val REQUEST_TAKE_PHOTO = 0
    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    private lateinit var contextFrag: Context
    private var imageUrl: Uri? = null

    private lateinit var txtViewFileMsg: TextView
    private lateinit var tvReplayFileMessageStatus: TextView
    private lateinit var tvSendFileMessageStatus: TextView
    private lateinit var tvUploadFileStatus: TextView
    private lateinit var txtViewUploadFile: TextView
    private lateinit var txtViewUploadImage: TextView
    private lateinit var txtViewReplyFileMsg: TextView
    private lateinit var tvSendLocationMessageStatus: TextView
    private lateinit var imageView_tickOne: AppCompatImageView
    private lateinit var imageView_tickTwo: AppCompatImageView
    private lateinit var imageView_tickThree: AppCompatImageView
    private lateinit var imageView_tickFour: AppCompatImageView
    private lateinit var prgressbarUploadImg: ProgressBar
    private lateinit var progressLocationMessage: ProgressBar
    private lateinit var progressBarReplyFileMsg: ProgressBar
    private lateinit var progressBarSendFileMessage: ProgressBar
    private lateinit var progressBarUploadFile: ProgressBar
    private lateinit var contentProgressLocationMessage: ProgressBar
    private lateinit var contentProgressFileMessage: ProgressBar
    private lateinit var contentProgressUploadFile: ProgressBar
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


    var isOpen = false

    private val faker: Faker = Faker()

    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
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

                changeColor(atach_file, R.color.blue_inactive)


            } else {

                closeCircularCard()

                changeColor(atach_file, R.color.grey_active)

            }

            isOpen = !isOpen


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

        image?.setColorFilter(
            ContextCompat.getColor(
                activity!!,
                color
            )
        )
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

            uploadImage()

            uploadImageProgress()

        }

        btnSendLocationMessage.setOnClickListener {


            sendLocationMessage()


        }


    }

    private fun sendLocationMessage() {


        if (!chatReady) return

        contentProgressLocationMessage.visibility = View.VISIBLE

        imgViewCheckLocationMessage.visibility = View.VISIBLE

        imgViewCheckLocationMessage.setImageResource(R.drawable.ic_done_black_24dp)

        imgViewCheckLocationMessage.setColorFilter(
            ContextCompat.getColor(
                activity!!,
                R.color.grey_light
            )
        )

        progressLocationMessage.progress = 0


        val requestThread = RequestThread
            .Builder()
            .build()


        tvSendLocationMessageStatus.setTextColor(ContextCompat.getColor(context!!, R.color.grey))
        tvSendLocationMessageStatus.text = "Get List of threads..."

        fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE] = mainViewModel.getThread(requestThread)

    }


    override fun onGetThread(chatResponse: ChatResponse<ResultThreads>?) {
        super.onGetThread(chatResponse)



        if (chatResponse?.uniqueId == fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE]) {


            tvSendLocationMessageStatus.text = "Get Static Map From Neshan API"

            progressLocationMessage.incrementProgressBy(5)

            prepareSendLocationMessage(chatResponse?.result?.threads)


        }


    }


    override fun onNewMessage(response: ChatResponse<ResultNewMessage>?) {
        super.onNewMessage(response)
    }


    private fun prepareSendLocationMessage(threads: List<Thread>?) {


        if (threads!!.isNotEmpty()) {


            val targetThreadId = threads[0].id

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

                            Log.d(
                                "MTAG",
                                "update progress: $bytesSent $totalBytesSent $totalBytesToSend"
                            )


//                            try {
//
//                                tvSendLocationMessageStatus.text =
//                                    "Upload Location Image %$bytesSent"
//
//                                progressLocationMessage.incrementProgressBy(bytesSent)
//
////                            activity!!.runOnUiThread {
////
////
////
////
////
////                            }
//                            } catch (e: Exception) {
//                                Log.d("MTAG", "Fail to update progress bc: ${e.message}")
//                                println(e)
//                            }


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

                            Log.d("MTAG", "upload error $jsonError")

                            Toast.makeText(context, jsonError, Toast.LENGTH_LONG).show()


                        }

                    })


        }


    }

    override fun onUploadFile(response: ChatResponse<ResultFile>?) {
        super.onUploadFile(response)

        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_FILE_MESSAGE]) {


            imageView_tickOne.setImageResource(R.drawable.ic_done_black_24dp)

            imageView_tickOne.setColorFilter(R.color.green_active)

            progressBarSendFileMessage.incrementProgressBy(40)

//            Handler().postDelayed({
//
//                tvSendFileMessageStatus.text = "Sending Message..."
//
//            }, 500)
            tvSendFileMessageStatus.text = "Sending Message..."


        }

        if (fucCallback[ConstantMsgType.UPLOAD_FILE] == response?.uniqueId) {

            tvSendFileMessageStatus.text = "File Uploaded Successfully"

            imageView_tickTwo.setImageResource(R.drawable.ic_round_done_all_24px)

            imageView_tickTwo.setColorFilter(R.color.green_active)

            progressBarUploadFile.progress = 100


            contentProgressUploadFile.visibility = View.GONE


        }

        if (fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] == response?.uniqueId) {


            imageView_tickThree.setImageResource(R.drawable.ic_done_black_24dp)

            imageView_tickThree.setColorFilter(R.color.green_active)

            tvReplayFileMessageStatus.text = "Sending Message"

            progressBarReplyFileMsg.incrementProgressBy(40)


        }


    }

    override fun onUploadImageFile(content: String?, response: ChatResponse<ResultImageFile>?) {
        super.onUploadImageFile(content, response)


        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE]) {

//
//            activity?.runOnUiThread {
//
//
//
//            }


            progressLocationMessage.progress = 80

            tvSendLocationMessageStatus.text = "Send Text Message With File"

            imgViewCheckLocationMessage.setImageResource(R.drawable.ic_done_black_24dp)

            imgViewCheckLocationMessage.setColorFilter(
                ContextCompat.getColor(
                    activity!!,
                    R.color.colorPrimary
                )
            )


//            activity?.runOnUiThread {
//
//
//
//            }

        }


        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_FILE_MESSAGE]) {


            tvSendFileMessageStatus.text = "Sending File Message"


        }

    }

    override fun onGetStaticMap(response: ChatResponse<ResultStaticMapImage>?) {
        super.onGetStaticMap(response)


        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE]) {


            tvSendLocationMessageStatus.text = "Upload Image to server"

            progressLocationMessage.incrementProgressBy(5)

//            imgViewMap.visibility = View.VISIBLE
//
//            imgViewMap.setImageBitmap(response?.result?.bitmap)
//
//            imgViewMap.scaleType = ImageView.ScaleType.FIT_XY


            tvPickedFileName.text = ""

            imgViewSelectedPic.visibility = View.VISIBLE

            imgViewSelectedPic.setImageBitmap(response?.result?.bitmap)

            val size = response?.result?.bitmap?.byteCount

            val toKb = (size?.div(1000f))

            val toMb = toKb?.div(1000f)

        }
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
        progressLocationMessage = view.findViewById(R.id.progressBarLocationMessage)
        progressBarReplyFileMsg = view.findViewById(R.id.progressBarReplyFileMsg)
        progressBarSendFileMessage = view.findViewById(R.id.progressBarSendFileMessage)
        progressBarUploadFile = view.findViewById(R.id.progressBarUploadFile)
        contentProgressLocationMessage = view.findViewById(R.id.progressSendLocationMessage)
        contentProgressFileMessage = view.findViewById(R.id.progressSendFileMessage)
        contentProgressUploadFile = view.findViewById(R.id.contentProgressUploadFile)
        contentProgressReplyFileMessage = view.findViewById(R.id.contentProgressReplyFileMessage)

        txtViewFileMsg = view.findViewById(R.id.TxtViewFileMsg)
        tvSendFileMessageStatus = view.findViewById(R.id.tvSendFileMessageStatus)
        tvReplayFileMessageStatus = view.findViewById(R.id.tvReplayFileMessageStatus)
        tvUploadFileStatus = view.findViewById(R.id.tvUploadFileStatus)
        txtViewUploadFile = view.findViewById(R.id.TxtViewUploadFile)
        txtViewUploadImage = view.findViewById(R.id.TxtViewUploadImage)
        txtViewReplyFileMsg = view.findViewById(R.id.TxtViewReplyFileMsg)
        prgressbarUploadImg = view.findViewById(R.id.progress_UploadImage)
        atach_file = view.findViewById(R.id.atach_file)
        imgViewCheckLocationMessage = view.findViewById(R.id.checkBoxLocationMessage)


        circularCard = view.findViewById(R.id.ccv_attachment_reveal)

        imageViewSelectedPic = view.findViewById(R.id.imgViewSelectedPic)

        tvPickedFileName = view.findViewById(R.id.tvFileName)

    }

    override fun onSent(response: ChatResponse<ResultMessage>?) {
        super.onSent(response)


        if (fucCallback[ConstantMsgType.REPLY_MESSAGE_ID] == response?.uniqueId) {


            try {
                activity?.runOnUiThread {
                    tvReplayFileMessageStatus.text = "Uploading File"

                    progressBarReplyFileMsg.incrementProgressBy(10)
                }
            } catch (e: Exception) {
                Log.e("MTAG", e.message)
            }

            prepareReplyWithMessage(response)

        }


        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE]) {

            handleSendLocationMessage()

        }


        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_FILE_MESSAGE]) {


            contentProgressFileMessage.visibility = View.GONE

            imageView_tickOne.setImageResource(R.drawable.ic_round_done_all_24px)

            imageView_tickOne.setColorFilter(R.color.green_active)


            progressBarSendFileMessage.progress = 100

            tvSendFileMessageStatus.text = "Message Sent Successfully"

            tvSendFileMessageStatus.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.green_active
                )
            )


        }


        if (fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] == response?.uniqueId) {

            try {
                activity?.runOnUiThread {

                    progressBarReplyFileMsg.progress = 100

                    contentProgressReplyFileMessage.visibility = View.GONE

                    imageView_tickThree.setImageResource(R.drawable.ic_round_done_all_24px)

                    imageView_tickThree.setColorFilter(R.color.green_active)

                    tvReplayFileMessageStatus.text = "Message Sent Successfully"

                    tvReplayFileMessageStatus.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.green_active
                        )
                    )

                }
            } catch (e: Exception) {
                Log.e("MTAG", e.message)
            }


        }


    }

    private fun handleSendLocationMessage() {
        progressLocationMessage.progress = 100
        imgViewCheckLocationMessage.setImageResource(R.drawable.ic_round_done_all_24px)


        tvSendLocationMessageStatus.setTextColor(
            ContextCompat.getColor(
                activity!!,
                R.color.green_active
            )
        )

        tvSendLocationMessageStatus.text = "Message Sent Successfully!"

        contentProgressLocationMessage.visibility = View.GONE
    }

    private fun prepareReplyWithMessage(response: ChatResponse<ResultMessage>?) {


        val messageId = response?.result?.messageId
        val threadId = fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID]
        val replyFileMessage = RequestReplyFileMessage
            .Builder(
                "this is replyMessage",
                threadId?.toLong()!!,
                messageId!!,
                imageUrl,
                activity
            )
            .build()



        fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] =
            mainViewModel.replyWithFile(replyFileMessage,
                object : ProgressHandler.sendFileMessage {
                    override fun onFinishImage(
                        json: String?,
                        chatResponse: ChatResponse<ResultImageFile>?
                    ) {
                        super.onFinishImage(json, chatResponse)
//                    imageView_tickFour.setImageResource(R.drawable.ic_round_done_all_24px)
//                    imageView_tickFour.setColorFilter(
//                        ContextCompat.getColor(
//                            activity!!,
//                            R.color.colorPrimary
//                        )
//                    )
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
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onGetContact(response: ChatResponse<ResultContact>?) {
        super.onGetContact(response)
        if (fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] == response?.uniqueId) {
            handleSendFileMsg(response!!.result.contacts)
        }

        if (fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] == response?.uniqueId) {


            try {
                activity?.runOnUiThread {

                    tvReplayFileMessageStatus.text = "Create Thread with Message"

                    progressBarReplyFileMsg.incrementProgressBy(10)
                }
            } catch (e: Exception) {
                Log.e("MTAG", "Exception: ${e.message}")
            }


            handleReplyFileMessage(response!!.result.contacts)
        }
    }

    override fun onError(chatResponse: ErrorOutPut?) {
        super.onError(chatResponse)
        activity?.runOnUiThread {
            Toast.makeText(activity, chatResponse?.errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateThread(response: ChatResponse<ResultThread>?) {
        super.onCreateThread(response)



        if (fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] == response?.uniqueId) {


            handleSendFileMessage(response)

        }



        if (fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID] == response?.uniqueId) {

            fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID] = response?.result?.thread?.id.toString()

            activity?.runOnUiThread {
                tvReplayFileMessageStatus.text = "Thread created"

                progressBarReplyFileMsg.incrementProgressBy(10)
            }


        }
    }

    private fun handleSendFileMessage(response: ChatResponse<ResultThread>?) {
        val requestFileMessage =
            RequestFileMessage.Builder(activity, response!!.result.thread.id, imageUrl).build()


        progressBarSendFileMessage.incrementProgressBy(10)

        tvSendFileMessageStatus.text = "Uploading File..."

        fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] = mainViewModel.sendFileMessage(
            requestFileMessage,
            object : ProgressHandler.sendFileMessage {
                override fun onFinishImage(
                    json: String?,
                    chatResponse: ChatResponse<ResultImageFile>?
                ) {
                    super.onFinishImage(json, chatResponse)
                    imageView_tickOne.setImageResource(R.drawable.ic_round_done_all_24px)
                    imageView_tickOne.setColorFilter(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.colorPrimary
                        )
                    )
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

                contentProgressReplyFileMessage.visibility = View.INVISIBLE

            }
        }
    }

    private fun handleSendFileMsg(contactList: ArrayList<Contact>) {

        activity?.runOnUiThread {

            progressBarSendFileMessage.incrementProgressBy(10)

            tvSendFileMessageStatus.text = "Creating Thread..."
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

            tvSendFileMessageStatus.text = "Creating Thread With id 121..."


        }
    }

    private fun sendFileMsg() {


        if (!chatReady) {


            showToast("Chat is not ready")

            return

        }

        if (imageUrl != null) {

            if (!chatReady) return
            tvSendFileMessageStatus.text = "Getting Contacts..."
            contentProgressFileMessage.visibility = View.VISIBLE

            val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
            val uniqueId = mainViewModel.getContact(requestGetContact)
            fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] = uniqueId


        } else {

            showToast("Pick a file our image first")

            openCircularCard()


        }


//            mainViewModel.sendFileMessage()

    }

    private fun uploadFile() {

        if (!chatReady) {


            showToast("Chat is not ready")


            return
        }

        if (imageUrl != null) {

            contentProgressUploadFile.visibility = View.VISIBLE

            tvUploadFileStatus.text = "Uploading..."

            val requestUploadFile = RequestUploadFile.Builder(activity, imageUrl).build()

            fucCallback[ConstantMsgType.UPLOAD_FILE] = mainViewModel.uploadFile(requestUploadFile)


        } else {

            showToast("Pick a File")

            openCircularCard()

            atach_file.requestFocus()


        }
    }

    //Chat needs update
    private fun uploadImage() {


        if (imageUrl != null) {

            fucCallback[ConstantMsgType.UPLOAD_IMAGE] =
                mainViewModel.uploadImage(activity, imageUrl!!)

        } else {

            showToast("PICK IMAGE")

            openImagePicker()

            return
        }
    }

    private fun uploadImageProgress() {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            prgressbarUploadImg.setProgress(100, true)
//        }


        if (!imageUrl.toString().isEmpty()) {
//            prgressbarUploadImg.max = 100
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                prgressbarUploadImg.setProgress(10, true)
//            }
            prgressbarUploadImg.incrementProgressBy(10)


            mainViewModel.uploadImageProgress(
                contextFrag,
                activity,
                imageUrl, object : ProgressHandler.onProgress {
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
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        prgressbarUploadImg.setProgress(bytesSent, true)
//                    }
                        prgressbarUploadImg.incrementProgressBy(bytesSent)
                    }

                    override fun onFinish(
                        imageJson: String?,
                        chatResponse: ChatResponse<ResultImageFile>?
                    ) {
                        super.onFinish(imageJson, chatResponse)
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        prgressbarUploadImg.setProgress(50, true)
//                    }
                        prgressbarUploadImg.incrementProgressBy(100)

                    }
                })
        } else {

            openImagePicker()

            showToast("Select Image First")
        }
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

        if (imageUrl == null) {

            showToast("Pick a file")

            openCircularCard()

            atach_file.requestFocus()

            return

        }

        contentProgressReplyFileMessage.visibility = View.VISIBLE

        tvReplayFileMessageStatus.text = "Get Contacts"

        progressBarReplyFileMsg.progress = 0

        imageView_tickThree.setImageResource(R.drawable.ic_done_black_24dp)

        imageView_tickThree.setColorFilter(Color.parseColor("#E7E6E6"))


        val requestGetContact = RequestGetContact.Builder().build()

        fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] =
            mainViewModel.getContact(requestGetContact)


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

                imageUrl = data.data
                imageViewSelectedPic.setImageURI(imageUrl)


            }

            if (PICKFILE_REQUEST_CODE == requestCode) {

                imageUrl = data.data

                imageViewSelectedPic.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_file
                    )
                )

            }

            if (data.data != null) {

                tvPickedFileName.text = getFileName(data.data!!, data.dataString!!)


            }

            circularCard.visibility = View.GONE
            changeColor(atach_file, R.color.grey_active)
            isOpen = !isOpen
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

