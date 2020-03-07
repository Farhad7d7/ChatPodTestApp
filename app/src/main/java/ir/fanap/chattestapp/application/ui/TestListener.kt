package ir.fanap.chattestapp.application.ui

import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage
import com.fanap.podchat.chat.pin.pin_thread.model.ResultPinThread
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles
import com.fanap.podchat.mainmodel.ResultDeleteMessage
import com.fanap.podchat.model.*

interface TestListener {

    fun onGetContact(response: ChatResponse<ResultContact>?) {}
    fun onAddContact(response: ChatResponse<ResultAddContact>?) {}
    fun onCreateThread(response: ChatResponse<ResultThread>?) {}
    fun onBlock(chatResponse: ChatResponse<ResultBlock>?) {}
    fun onUnBlock(response: ChatResponse<ResultBlock>?) {}
    fun onGetThread(chatResponse: ChatResponse<ResultThreads>?) {}
    fun onError(chatResponse: ErrorOutPut?) {}
    fun onRemoveContact(response: ChatResponse<ResultRemoveContact>?) {}
    fun onBlockList(response: ChatResponse<ResultBlockList>?) {}
    fun onLogEvent(log: String) {}
    fun onUpdateContact(response: ChatResponse<ResultUpdateContact>?) {}
    fun onSent(response: ChatResponse<ResultMessage>?) {}
    fun onSeen(response: ChatResponse<ResultMessage>?) {}
    fun onDeliver(response: ChatResponse<ResultMessage>?) {}
    fun onThreadRemoveParticipant(response: ChatResponse<ResultParticipant>?) {
    }

    fun onThreadAddParticipant(response: ChatResponse<ResultAddParticipant>?) {

    }

    fun onLeaveThread(response: ChatResponse<ResultLeaveThread>?) {


    }

    fun onUnmuteThread(response: ChatResponse<ResultMute>?) {


    }

    fun onMuteThread(response: ChatResponse<ResultMute>?) {


    }

    fun onDeleteMessage(response: ChatResponse<ResultDeleteMessage>?) {


    }

    fun onEditedMessage(response: ChatResponse<ResultNewMessage>?) {


    }

    fun onGetHistory(response: ChatResponse<ResultHistory>?) {


    }

    fun onNewMessage(response: ChatResponse<ResultNewMessage>?) {


    }

    fun onGetThreadParticipant(response: ChatResponse<ResultParticipant>?) {


    }

    fun onClearHistory(chatResponse: ChatResponse<ResultClearHistory>?) {

    }

    fun onGetAdminList(content: ChatResponse<ResultParticipant>?) {


    }

    fun onSetRole(outputSetRoleToUser: ChatResponse<ResultSetAdmin>?) {

    }

    fun onGetSeenMessageList(response: ChatResponse<ResultParticipant>?) {


    }

    fun onGetDeliverMessageList(response: ChatResponse<ResultParticipant>?) {


    }

    fun onGetSearchContactResult(response: ChatResponse<ResultContact>?) {


    }

    fun onGetStaticMap(response: ChatResponse<ResultStaticMapImage>?) {


    }

    fun onLogEventWithName(logName: String, json: String) {


    }

    fun onUploadImageFile(content: String?, response: ChatResponse<ResultImageFile>?) {


    }

    fun onUploadFile(response: ChatResponse<ResultFile>?) {


    }

    fun onPinThread(response: ChatResponse<ResultPinThread>?) {


    }

    fun onUnPinThread(response: ChatResponse<ResultPinThread>?) {


    }

    fun onMessagePinned(response: ChatResponse<ResultPinMessage>?) {


    }

    fun onMessageUnPinned(response: ChatResponse<ResultPinMessage>?) {


    }

    fun onGetMentionList(response: ChatResponse<ResultHistory>?) {


    }

    fun onGetCurrentUserRoles(response: ChatResponse<ResultCurrentUserRoles>?) {


    }

    fun onChatProfileUpdated(response: ChatResponse<ResultUpdateProfile>?) {


    }

    fun onGetUserInfo(response: ChatResponse<ResultUserInfo>?) {


    }


}