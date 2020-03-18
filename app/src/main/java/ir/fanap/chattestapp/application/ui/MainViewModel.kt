package ir.fanap.chattestapp.application.ui

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.net.Uri
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.fanap.podchat.ProgressHandler
import com.fanap.podchat.chat.Chat
import com.fanap.podchat.chat.ChatListener
import com.fanap.podchat.chat.mention.model.RequestGetMentionList
import com.fanap.podchat.chat.pin.pin_message.model.RequestPinMessage
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage
import com.fanap.podchat.chat.pin.pin_thread.model.RequestPinThread
import com.fanap.podchat.chat.pin.pin_thread.model.ResultPinThread
import com.fanap.podchat.chat.user.profile.RequestUpdateProfile
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles
import com.fanap.podchat.mainmodel.Invitee
import com.fanap.podchat.mainmodel.ResultDeleteMessage
import com.fanap.podchat.mainmodel.RequestSearchContact
import com.fanap.podchat.model.*
import com.fanap.podchat.notification.INotification
import com.fanap.podchat.requestobject.*
import com.fanap.podchat.util.NetworkPingSender
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ir.fanap.chattestapp.application.ui.log.refactorLog
import ir.fanap.chattestapp.application.ui.util.SmartArrayList
import ir.fanap.chattestapp.bussines.model.LogClass
import rx.subjects.PublishSubject
import kotlin.collections.ArrayList

class MainViewModel(application: Application) : AndroidViewModel(application) {

        val NOTIFICATION_APPLICATION_ID = "a7ef47ebe966e41b612216b457ccba222a33332de52e948c66708eb4e3a5328f";

    val listOfLogs: SmartArrayList<LogClass> = SmartArrayList()

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private var chat: Chat = Chat.init(application)
    private lateinit var testListener: TestListener
    var observable: PublishSubject<String> = PublishSubject.create()
    var observableLog: PublishSubject<String> = PublishSubject.create()

    init {

        val networkStateConfig = NetworkPingSender.NetworkStateConfig()
            .setHostName("chat-sandbox.pod.ir")
            .setPort(443)
            .setDisConnectionThreshold(2)
            .setInterval(7000)
            .setConnectTimeout(10000)
            .build()

        chat.setNetworkStateConfig(networkStateConfig)

        chat.isLoggable(true)



        chat.addListener(object : ChatListener {

            override fun onChatState(state: String?) {
                super.onChatState(state)

                observable.onNext(state)

            }


            override fun onUserInfo(content: String?, response: ChatResponse<ResultUserInfo>?) {
                super.onUserInfo(content, response)

                testListener.onGetUserInfo(response)

            }

            override fun onChatProfileUpdated(response: ChatResponse<ResultUpdateProfile>?) {
                super.onChatProfileUpdated(response)

                testListener.onChatProfileUpdated(response)

            }

            override fun onGetCurrentUserRoles(response: ChatResponse<ResultCurrentUserRoles>?) {
                super.onGetCurrentUserRoles(response)

                testListener.onGetCurrentUserRoles(response)


            }

            override fun onGetMentionList(response: ChatResponse<ResultHistory>?) {
                super.onGetMentionList(response)

                testListener.onGetMentionList(response)

            }

            override fun onUnPinMessage(response: ChatResponse<ResultPinMessage>?) {
                super.onUnPinMessage(response)

                testListener.onMessageUnPinned(response)
            }

            override fun onPinMessage(response: ChatResponse<ResultPinMessage>?) {
                super.onPinMessage(response)

                testListener.onMessagePinned(response)
            }

            override fun onUnPinThread(response: ChatResponse<ResultPinThread>?) {
                super.onUnPinThread(response)

                testListener.onUnPinThread(response)
            }

            override fun onPinThread(response: ChatResponse<ResultPinThread>?) {
                super.onPinThread(response)

                testListener.onPinThread(response)
            }

            //todo implement not seen duration
            override fun OnNotSeenDuration(resultNotSeen: OutPutNotSeenDurations?) {
                super.OnNotSeenDuration(resultNotSeen)
            }

            override fun onGetThreadParticipant(
                content: String?,
                response: ChatResponse<ResultParticipant>?) {
                super.onGetThreadParticipant(content, response)

                testListener.onGetThreadParticipant(response)
            }

            override fun onNewMessage(content: String?, response: ChatResponse<ResultNewMessage>?) {
                super.onNewMessage(content, response)

                testListener.onNewMessage(response)

            }

            override fun onError(content: String?, OutPutError: ErrorOutPut?) {
                super.onError(content, OutPutError)
                testListener.onError(OutPutError)
            }

            override fun onCreateThread(content: String?, response: ChatResponse<ResultThread>?) {
                super.onCreateThread(content, response)
                testListener.onCreateThread(response)
            }

            override fun onContactAdded(
                content: String?,
                response: ChatResponse<ResultAddContact>?
            ) {
                super.onContactAdded(content, response)
                testListener.onAddContact(response)
            }


            override fun onRemoveContact(
                content: String?,
                response: ChatResponse<ResultRemoveContact>?
            ) {
                super.onRemoveContact(content, response)
                testListener.onRemoveContact(response)
            }

            override fun onBlock(content: String?, response: ChatResponse<ResultBlock>?) {
                super.onBlock(content, response)
                testListener.onBlock(response)
            }

            override fun onGetContacts(content: String?, response: ChatResponse<ResultContact>?) {
                super.onGetContacts(content, response)
                testListener.onGetContact(response)
            }

            override fun onGetBlockList(
                content: String?,
                response: ChatResponse<ResultBlockList>?
            ) {
                super.onGetBlockList(content, response)
                testListener.onBlockList(response)
            }


            override fun onLogEvent(logName: String?, json: String?) {


                saveLogs(logName, json)

                testListener.onLogEventWithName(logName!!, json!!)
            }

            override fun onLogEvent(log: String?) {
                super.onLogEvent(log)
                testListener.onLogEvent(log!!)
            }

            override fun onUpdateContact(
                content: String?,
                response: ChatResponse<ResultUpdateContact>?
            ) {
                super.onUpdateContact(content, response)
                testListener.onUpdateContact(response)
            }

            override fun onUnBlock(content: String?, response: ChatResponse<ResultBlock>?) {
                super.onUnBlock(content, response)
                testListener.onUnBlock(response)
            }

            override fun onSent(content: String?, response: ChatResponse<ResultMessage>?) {
                super.onSent(content, response)
                testListener.onSent(response)
            }

            override fun onGetThread(content: String?, thread: ChatResponse<ResultThreads>?) {
                super.onGetThread(content, thread)
                testListener.onGetThread(thread)
            }

            override fun onSeen(content: String?, response: ChatResponse<ResultMessage>?) {
                super.onSeen(content, response)
                testListener.onSeen(response)
            }

            override fun onDeliver(content: String?, response: ChatResponse<ResultMessage>?) {
                super.onDeliver(content, response)
                testListener.onDeliver(response)
            }

            override fun onThreadRemoveParticipant(
                content: String?,
                response: ChatResponse<ResultParticipant>?
            ) {
                super.onThreadRemoveParticipant(content, response)
                testListener.onThreadRemoveParticipant(response)
            }

            override fun onThreadAddParticipant(
                content: String?,
                response: ChatResponse<ResultAddParticipant>?
            ) {
                super.onThreadAddParticipant(content, response)
                testListener.onThreadAddParticipant(response)
            }

            override fun onThreadLeaveParticipant(
                content: String?,
                response: ChatResponse<ResultLeaveThread>?
            ) {
                super.onThreadLeaveParticipant(content, response)
                testListener.onLeaveThread(response)
            }

            override fun onMuteThread(content: String?, response: ChatResponse<ResultMute>?) {
                super.onMuteThread(content, response)
                testListener.onMuteThread(response)
            }

            override fun onUnmuteThread(content: String?, response: ChatResponse<ResultMute>?) {
                super.onUnmuteThread(content, response)
                testListener.onUnmuteThread(response)
            }

            override fun onDeleteMessage(
                content: String?,
                response: ChatResponse<ResultDeleteMessage>?
            ) {
                super.onDeleteMessage(content, response)
                testListener.onDeleteMessage(response)
            }

            override fun onEditedMessage(
                content: String?,
                response: ChatResponse<ResultNewMessage>?
            ) {
                super.onEditedMessage(content, response)
                testListener.onEditedMessage(response)
            }

            override fun onGetHistory(content: String?, response: ChatResponse<ResultHistory>?) {
                super.onGetHistory(content, response)
                testListener.onGetHistory(response)
            }

            override fun OnClearHistory(
                content: String?,
                chatResponse: ChatResponse<ResultClearHistory>?
            ) {
                super.OnClearHistory(content, chatResponse)
                testListener.onClearHistory(chatResponse)
            }


            override fun onGetThreadAdmin(
                content: String?,
                chatResponse: ChatResponse<ResultParticipant>?
            ) {
                super.onGetThreadAdmin(content, chatResponse)

                testListener.onGetAdminList(chatResponse)

            }

            override fun OnSetRule(outputSetRoleToUser: ChatResponse<ResultSetAdmin>?) {
                super.OnSetRule(outputSetRoleToUser)

                testListener.onSetRole(outputSetRoleToUser)

            }

            override fun OnSeenMessageList(
                content: String?,
                response: ChatResponse<ResultParticipant>?
            ) {
                super.OnSeenMessageList(content, response)

                testListener.onGetSeenMessageList(response)
            }

            override fun OnDeliveredMessageList(
                content: String?,
                response: ChatResponse<ResultParticipant>?
            ) {
                super.OnDeliveredMessageList(content, response)

                testListener.onGetDeliverMessageList(response)

            }


            override fun onSearchContact(content: String?, response: ChatResponse<ResultContact>?) {
                super.onSearchContact(content, response)

                testListener.onGetSearchContactResult(response)
            }


            override fun OnStaticMap(response: ChatResponse<ResultStaticMapImage>?) {
                super.OnStaticMap(response)

                testListener.onGetStaticMap(response)
            }

            override fun onUploadImageFile(
                content: String?,
                response: ChatResponse<ResultImageFile>?
            ) {
                super.onUploadImageFile(content, response)

                testListener.onUploadImageFile(content, response)
            }

            override fun onUploadFile(content: String?, response: ChatResponse<ResultFile>?) {
                super.onUploadFile(content, response)

                testListener.onUploadFile(response)
            }
        })
    }


    public fun setNotif(activity: Activity){

        chat.enableNotification(NOTIFICATION_APPLICATION_ID,activity
        ) { userId -> Log.d("MTAG","new USer name $userId" ) }

    }
    private fun saveLogs(logName: String?, json: String?) {


        listOfLogs.addAll(refactorLog(logName = logName!!, log = json!!, gson = gson))


    }

    private fun addToLogsAndLogNames(uniqueId: String, logName: String?, log: String?) {


        listOfLogs.add(LogClass(uniqueId = uniqueId, logName = logName!!, log = log!!))


    }

    fun setTestListener(testListener: TestListener) {
        this.testListener = testListener
    }

    fun connect(
        socketAddress: String, appId: String, severName: String, token: String,
        ssoHost: String, platformHost: String, fileServer: String, typeCode: String?
    ) {

        val rb: RequestConnect = RequestConnect.Builder(
            socketAddress,
            appId,
            severName,
            token,
            ssoHost,
            platformHost,
            fileServer
        ).build()

        chat.connect(rb)

    }

    fun uploadFile(requestUploadFile: RequestUploadFile): String {
        return chat.uploadFile(requestUploadFile)
    }

    fun uploadFileProgress(
        requestUploadFile: RequestUploadFile,
        progress: ProgressHandler.onProgressFile
    ): String {
        return chat.uploadFileProgress(requestUploadFile, progress)
    }

    fun uploadImage(activity: FragmentActivity?, uri: Uri): String {
        return chat.uploadImage(activity, uri)
    }

    fun uploadImageProgress(
        context: Context,
        activity: FragmentActivity?,
        uri: Uri?,
        progress: ProgressHandler.onProgress
    ): String {
        return chat.uploadImageProgress(activity, uri, progress)
    }

    fun uploadImageProgress(
        requestUploadImage: RequestUploadImage,
        progress: ProgressHandler.onProgress
    ): String {
        return chat.uploadImageProgress(requestUploadImage, progress)
    }

    fun sendFileMessage(
        requestFileMessage: RequestFileMessage,
        objects: ProgressHandler.sendFileMessage
    ): String {
        return chat.sendFileMessage(requestFileMessage, objects)
    }

    fun replyWithFile(
        requestReplyMessage: RequestReplyFileMessage,
        objects: ProgressHandler.sendFileMessage
    ): String {
        return chat.replyFileMessage(requestReplyMessage, objects)
    }

    fun getHistory(requestGetHistory: RequestGetHistory): String {
        return chat.getHistory(requestGetHistory, null)
    }

    fun editMessage(requestEditMessage: RequestEditMessage): String {
        return chat.editMessage(requestEditMessage, null)
    }

    fun muteThread(requestMuteThread: RequestMuteThread): String {
        return chat.muteThread(requestMuteThread, null)
    }

    fun unMuteThread(requestMuteThread: RequestMuteThread): String {
        return chat.unMuteThread(requestMuteThread, null)
    }

    fun createThread(
        threadType: Int,
        invitee: Array<Invitee>,
        threadTitle: String,
        description: String,
        image: String,
        metadata: String
    ): String {
        return chat.createThread(
            threadType,
            invitee,
            threadTitle,
            description,
            image,
            metadata,
            null
        )
    }


    fun createThreadWithMessage(requestCreateThread: RequestCreateThread): ArrayList<String>? {
        return chat.createThreadWithMessage(requestCreateThread)
    }

    fun updateContact(requestUpdateContact: RequestUpdateContact): String {
        return chat.updateContact(requestUpdateContact)
    }


    fun getContact(requestGetContact: RequestGetContact): String {
        return chat.getContacts(requestGetContact, null)
    }

    fun addContacts(requestAddContact: RequestAddContact): String {

        return chat.addContact(requestAddContact)
    }

    fun removeContact(requestRemoveContact: RequestRemoveContact): String {
        return chat.removeContact(requestRemoveContact)
    }


    fun blockContact(requestBlock: RequestBlock): String {
        return chat.block(requestBlock, null)
    }

    fun unBlock(requestUnBlock: RequestUnBlock): String {
        return chat.unblock(requestUnBlock, null)
    }

    fun deleteMessage(requestDeleteMessage: RequestDeleteMessage): String {
        return chat.deleteMessage(requestDeleteMessage, null)
    }

    fun getThread(requestThread: RequestThread): String {
        return chat.getThreads(requestThread, null)
    }

    fun getBlockList(requestBlockList: RequestBlockList): String {
        return chat.getBlockList(requestBlockList, null)
    }

    fun sendTextMsg(requestMessage: RequestMessage): String {
        return chat.sendTextMessage(requestMessage, null)
    }

    fun forwardMessage(requestForwardMessage: RequestForwardMessage): List<String> {
        return chat.forwardMessage(requestForwardMessage)
    }

    fun replyMessage(replyMessage: RequestReplyMessage): String {
        return chat.replyMessage(replyMessage, null)
    }

    fun addParticipant(requestAddParticipants: RequestAddParticipants): String {
        return chat.addParticipants(requestAddParticipants, null)
    }

    fun removeParticipant(requestRemoveParticipants: RequestRemoveParticipants): String {
        return chat.removeParticipants(requestRemoveParticipants, null)
    }

    fun leaveThread(requestLeaveThread: RequestLeaveThread): String {
        return chat.leaveThread(requestLeaveThread, null)
    }

    fun getParticipant(requestThreadParticipant: RequestThreadParticipant): String {
        return chat.getThreadParticipants(requestThreadParticipant, null)
    }

    fun clearHistory(requestClearHistory: RequestClearHistory): String {
        return chat.clearHistory(requestClearHistory)
    }

    fun getAdminList(requestGetAdmin: RequestGetAdmin): String {

        return chat.getAdminList(requestGetAdmin)
    }

    fun addAdmin(addAdmin: RequestSetAdmin?): String {

        return chat.addAdmin(addAdmin)
    }

    fun removeAdminRoles(addAdmin: RequestSetAdmin?): String {

        return chat.removeAdmin(addAdmin)
    }

    fun getDeliverMessageList(request: RequestDeliveredMessageList): String {

        return chat.getMessageDeliveredList(request)

    }

    fun getSeenMessageList(request: RequestSeenMessageList): String {

        return chat.getMessageSeenList(request)
    }

    fun searchContact(searchContact: RequestSearchContact): String {

        return chat.searchContact(searchContact)
    }

    fun sendLocationMessage(
        requestLocationMessage: RequestLocationMessage,
        param: ProgressHandler.sendFileMessage
    ): String {

        return chat.sendLocationMessage(requestLocationMessage, param)


    }

    fun deleteMultipleMessage(requestDeleteMessage: RequestDeleteMessage): ArrayList<String> {

        return ArrayList(chat.deleteMultipleMessage(requestDeleteMessage, null))
    }

    fun spamThread(requestSpam: RequestSpam): String {

        return chat.spam(requestSpam)
    }

    fun setToken(sandToken: String) {

        chat.setToken(sandToken)

    }

    fun pinThread(request: RequestPinThread): String {

        return chat.pinThread(request)

    }

    fun unpinThread(request: RequestPinThread): String {

        return chat.unPinThread(request)
    }

    fun pinMessage(request: RequestPinMessage): String {

        return chat.pinMessage(request)

    }

    fun unpinMessage(request: RequestPinMessage?): String {

        return chat.unPinMessage(request)
    }

    fun getMentionList(request: RequestGetMentionList?): String {

        return chat.getMentionList(request)

    }

    fun getCurrentUserRoles(request: RequestGetUserRoles?): String {

        return chat.getCurrentUserRoles(request)
    }

    fun updateChatProfile(request: RequestUpdateProfile): String {

        return chat.updateChatProfile(request)
    }

    fun getUserInfo(): String {

        return chat.getUserInfo(null)

    }


}