package ir.fanap.chattestapp.application.ui.chat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.fanap.chattestapp.R
import android.animation.Animator
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.support.design.circularreveal.CircularRevealCompat
import android.support.design.circularreveal.cardview.CircularRevealCardView
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.fanap.podchat.ProgressHandler
import com.fanap.podchat.mainmodel.Contact
import com.fanap.podchat.mainmodel.Invitee
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage
import com.fanap.podchat.mainmodel.Thread
import com.fanap.podchat.model.*
import com.fanap.podchat.requestobject.*
import com.fanap.podchat.util.ThreadType
import com.github.javafaker.Faker
import ir.fanap.chattestapp.application.ui.MainViewModel
import ir.fanap.chattestapp.application.ui.TestListener
import ir.fanap.chattestapp.application.ui.util.ConstantMsgType
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.ArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.math.hypot
import kotlin.math.max

class ChatFragment : Fragment(), TestListener {

    private lateinit var atach_file: AppCompatImageView
    private lateinit var mainViewModel: MainViewModel
    private var fucCallback: HashMap<String, String> = hashMapOf()
    private val REQUEST_TAKE_PHOTO = 0
    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    private lateinit var contextFrag: Context
    private var imageUrl: Uri? = null
    private lateinit var txtViewFileMsg: TextView
    private lateinit var txtViewUploadFile: TextView
    private lateinit var txtViewUploadImage: TextView
    private lateinit var txtViewReplyFileMsg: TextView
    private lateinit var imageView_tickOne: AppCompatImageView
    private lateinit var imageView_tickTwo: AppCompatImageView
    private lateinit var imageView_tickThree: AppCompatImageView
    private lateinit var imageView_tickFour: AppCompatImageView
    private lateinit var prgressbarUploadImg: ProgressBar
    private lateinit var buttonUploadImage: AppCompatImageView

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_chat, container, false)


        val cicuralCard: CircularRevealCardView = view.findViewById(R.id.ccv_attachment_reveal)


        initViews(view)

        setListeners()


//        val appCmpImgViewFolder: AppCompatImageView = view.findViewById(R.id.appCompatImageView_folder)

        val appCompatImageViewGallery: AppCompatImageView =
            view.findViewById(R.id.appCompatImageView_gallery)



        appCompatImageViewGallery.setOnClickListener {
            selectImageInAlbum()
        }




        atach_file.setOnClickListener {

            if (!isOpen) {

                val x = cicuralCard.left
                val y = cicuralCard.bottom

                val startRadius = 0
                val endRadius = hypot(cicuralCard.width.toDouble(), cicuralCard.height.toDouble())

                val anim: Animator =
                    CircularRevealCompat.createCircularReveal(
                        cicuralCard,
                        x.toFloat(),
                        y.toFloat(),
                        startRadius.toFloat(),
                        endRadius.toFloat()
                    )


                anim.interpolator = AccelerateDecelerateInterpolator()
                cicuralCard.visibility = View.VISIBLE
                anim.duration = 1000
                anim.start()

                isOpen = true

            } else {

                val x = cicuralCard.left
                val y = cicuralCard.bottom

                val endRadius = max(cicuralCard.width, cicuralCard.height)
                val startRadius = 0


                val anim: Animator =
                    CircularRevealCompat.createCircularReveal(
                        cicuralCard,
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
                        cicuralCard.visibility = View.GONE
                    }
                })
                anim.interpolator = AccelerateDecelerateInterpolator()
                anim.duration = 1000
                anim.start()
                isOpen = false
            }
        }


        return view
    }

    private fun setListeners() {

        txtViewFileMsg.setOnClickListener { fileMsg() }
        txtViewUploadFile.setOnClickListener { uploadFile() }
        txtViewReplyFileMsg.setOnClickListener { replyFileMsg() }
        buttonUploadImage.setOnClickListener {
            //            uploadImage()
            uploadImageProgress()
        }


    }

    private fun sendLocationMessage() {


        progressSendLocationMessage.visibility = View.VISIBLE


        checkBoxLocationMessage.setImageResource(R.drawable.ic_done_black_24dp)
        checkBoxLocationMessage.setColorFilter(ContextCompat.getColor(activity!!, R.color.abc_color_highlight_material))

        progressBarLocationMessage.progress = 0


        val requestThread = RequestThread
            .Builder()
            .build()


        progressBarLocationMessage.incrementProgressBy(5)

        fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE] = mainViewModel.getThread(requestThread)

    }


    override fun onGetThread(chatResponse: ChatResponse<ResultThreads>?) {
        super.onGetThread(chatResponse)



        if (chatResponse?.uniqueId == fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE]) {

            progressBarLocationMessage.incrementProgressBy(5)
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




            progressBarLocationMessage.incrementProgressBy(5)

            fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE] = mainViewModel
                .sendLocationMessage(requestLocationMessage, object : ProgressHandler.sendFileMessage {


                    override fun onProgressUpdate(
                        uniqueId: String?,
                        bytesSent: Int,
                        totalBytesSent: Int,
                        totalBytesToSend: Int
                    ) {
                        super.onProgressUpdate(uniqueId, bytesSent, totalBytesSent, totalBytesToSend)

                        Log.d("MTAG", "update progress: $bytesSent $totalBytesSent $totalBytesToSend")

                        progressBarLocationMessage.incrementProgressBy(bytesSent)


                    }

                    override fun onFinishImage(json: String?, chatResponse: ChatResponse<ResultImageFile>?) {
                        super.onFinishImage(json, chatResponse)

                        Log.d("MTAG", "finish upload")
                        progressBarLocationMessage.incrementProgressBy(100)

                        checkBoxLocationMessage.setImageResource(R.drawable.ic_round_done_all_24px)
                        checkBoxLocationMessage.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorPrimary))

                    }

                    override fun onError(jsonError: String?, error: ErrorOutPut?) {
                        super.onError(jsonError, error)

                        Log.d("MTAG", "upload error $jsonError")

                        Toast.makeText(context, jsonError, Toast.LENGTH_LONG).show()


                    }

                })


        }


    }


    override fun onGetStaticMap(response: ChatResponse<ResultStaticMapImage>?) {
        super.onGetStaticMap(response)


        if (response?.uniqueId == fucCallback[ConstantMsgType.SEND_LOCATION_MESSAGE]) {


            progressSendLocationMessage.visibility = View.INVISIBLE

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

        txtViewFileMsg = view.findViewById(R.id.TxtViewFileMsg)
        txtViewUploadFile = view.findViewById(R.id.TxtViewUploadFile)
        txtViewUploadImage = view.findViewById(R.id.TxtViewUploadImage)
        txtViewReplyFileMsg = view.findViewById(R.id.TxtViewReplyFileMsg)
        prgressbarUploadImg = view.findViewById(R.id.progress_UploadImage)
        atach_file = view.findViewById(R.id.atach_file)

    }

    override fun onSent(response: ChatResponse<ResultMessage>?) {
        super.onSent(response)
        if (fucCallback[ConstantMsgType.REPLY_MESSAGE_ID] == response?.uniqueId) {
            val messageId = response?.result?.messageId
            val threadId = fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID]
            val replyFileMessage = RequestReplyFileMessage
                .Builder("this is replyMessage", threadId?.toLong()!!, messageId!!, imageUrl, activity)
                .build()
            fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] = mainViewModel
                .replyWithFile(replyFileMessage, object : ProgressHandler.sendFileMessage {
                    override fun onFinishImage(json: String?, chatResponse: ChatResponse<ResultImageFile>?) {
                        super.onFinishImage(json, chatResponse)
                        imageView_tickFour.setImageResource(R.drawable.ic_round_done_all_24px)
                        imageView_tickFour.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorPrimary))
                    }

                })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
            .create(MainViewModel::class.java)

        mainViewModel.setTestListener(this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutSendLocationMessage.setOnClickListener {


            sendLocationMessage()


        }
    }

    override fun onGetContact(response: ChatResponse<ResultContact>?) {
        super.onGetContact(response)
        if (fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] == response?.uniqueId) {
            handleSendFileMsg(response!!.result.contacts)
        }

        if (fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] == response?.uniqueId) {
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
            val requestFileMessage = RequestFileMessage.Builder(activity, response!!.result.thread.id, imageUrl).build()
            mainViewModel.sendFileMessage(requestFileMessage, object : ProgressHandler.sendFileMessage {
                override fun onFinishImage(json: String?, chatResponse: ChatResponse<ResultImageFile>?) {
                    super.onFinishImage(json, chatResponse)
                    imageView_tickOne.setImageResource(R.drawable.ic_round_done_all_24px)
                    imageView_tickOne.setColorFilter(ContextCompat.getColor(activity!!, R.color.colorPrimary))
                }

                override fun onProgressUpdate(
                    uniqueId: String?,
                    bytesSent: Int,
                    totalBytesSent: Int,
                    totalBytesToSend: Int
                ) {
                    super.onProgressUpdate(uniqueId, bytesSent, totalBytesSent, totalBytesToSend)

                }
            })
        }

        if ((fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID] == response?.uniqueId)) {
            val threadId = response?.result?.thread?.id
            fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID] = threadId.toString()
        }
    }

    private fun handleReplyFileMessage(contactList: ArrayList<Contact>) {
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
                        fucCallback[ConstantMsgType.REPLY_MESSAGE_THREAD_ID] = uniqueId!![0]
                        fucCallback[ConstantMsgType.REPLY_MESSAGE_ID] = uniqueId[1]
                    }
                    break
                }
            }
        }
    }

    private fun handleSendFileMsg(contactList: ArrayList<Contact>) {

        if (contactList != null) {
            var choose = 0
            for (contact: Contact in contactList) {
                if (contact.isHasUser) {
                    choose++
                    if (choose == 1) {
                        val contactId = contact.userId

                        val inviteList = ArrayList<Invitee>()
                        inviteList.add(Invitee(contactId, 1))

                        val list = Array(1) { Invitee(inviteList[0].id, 2) }

                        val uniqueId = mainViewModel.createThread(
                            ThreadType.Constants.NORMAL, list, "nothing", ""
                            , "", ""
                        )
                        fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] = uniqueId
                    }
                    break
                }
            }

            if(choose == 0){
                val contactId = 121L

                val inviteList = ArrayList<Invitee>()
                inviteList.add(Invitee(contactId, 1))

                val list = Array(1) { Invitee(inviteList[0].id, 1) }

                val uniqueId = mainViewModel.createThread(
                    ThreadType.Constants.NORMAL, list, "nothing", ""
                    , "", ""
                )
                fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] = uniqueId

            }
        }
    }

    private fun fileMsg() {

        val requestGetContact: RequestGetContact = RequestGetContact.Builder().build()
        val uniqueId = mainViewModel.getContact(requestGetContact)
        fucCallback[ConstantMsgType.SEND_FILE_MESSAGE] = uniqueId
//            mainViewModel.sendFileMessage()

    }

    fun uploadFile() {
        if (imageUrl != null) {

            val requestUploadFile = RequestUploadFile.Builder(activity, imageUrl).build()
            mainViewModel.uploadFile(requestUploadFile)
        }
    }

    //Chat needs update
    fun uploadImage() {
        if (!imageUrl.toString().isEmpty()) {
            mainViewModel.uploadImage(activity, imageUrl!!)
        }
    }

    fun uploadImageProgress() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            prgressbarUploadImg.setProgress(100, true)
//        }


        if (!imageUrl.toString().isEmpty()) {
//            prgressbarUploadImg.max = 100
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                prgressbarUploadImg.setProgress(10, true)
//            }
            prgressbarUploadImg.incrementProgressBy(10)


            mainViewModel.uploadImageProgress(contextFrag, activity, imageUrl, object : ProgressHandler.onProgress {
                override fun onProgressUpdate(
                    uniqueId: String?,
                    bytesSent: Int,
                    totalBytesSent: Int,
                    totalBytesToSend: Int
                ) {
                    super.onProgressUpdate(uniqueId, bytesSent, totalBytesSent, totalBytesToSend)
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        prgressbarUploadImg.setProgress(bytesSent, true)
//                    }
                    prgressbarUploadImg.incrementProgressBy(bytesSent)
                }

                override fun onFinish(imageJson: String?, chatResponse: ChatResponse<ResultImageFile>?) {
                    super.onFinish(imageJson, chatResponse)
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        prgressbarUploadImg.setProgress(50, true)
//                    }
                    prgressbarUploadImg.incrementProgressBy(100)

                }
            })
        }
    }

    fun replyFileMsg() {
        val requestGetContact = RequestGetContact.Builder().build()
        fucCallback[ConstantMsgType.REPLY_FILE_MESSAGE] = mainViewModel.getContact(requestGetContact)
    }

    fun selectImageInAlbum() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, REQUEST_SELECT_IMAGE_IN_ALBUM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (REQUEST_SELECT_IMAGE_IN_ALBUM == requestCode) {
                imageUrl = data.data
            }
        }
    }
}

