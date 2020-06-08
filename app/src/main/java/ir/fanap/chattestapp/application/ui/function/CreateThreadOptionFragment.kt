package ir.fanap.chattestapp.application.ui.function

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.fanap.podchat.util.InviteType
import com.fanap.podchat.util.ThreadType

import ir.fanap.chattestapp.R
import ir.fanap.chattestapp.application.ui.util.FullViewDialogFragment
import kotlinx.android.synthetic.main.fragment_create_thread_option.*

/**
 * A simple [Fragment] subclass.
 */
class CreateThreadOptionFragment : FullViewDialogFragment() {

    var selectedThreadType = 0
    var selectedInviteeType = 1

    companion object {

        const val MODE_KEY = "MODE"

        const val MODE_NORMAL = "NORMAL_THREAD"

        const val MODE_PUBLIC = "PUBLIC_THREAD"

        var mode = ""


    }

    private var listener: ICreateThreadOption? = null

    fun setListener(iCreateThreadOption: ICreateThreadOption) {

        listener = iCreateThreadOption
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mode = arguments?.getString(MODE_KEY, "")!!

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_thread_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initial()
        setOnClickListeners()
    }

    private fun runScaleAnim(it: View, onEnd: () -> Unit) {
        it.animate().setDuration(150L).setInterpolator(LinearInterpolator())
            .withEndAction {

                it.animate()
                    .setDuration(150L)
                    .setInterpolator(LinearInterpolator())
                    .withEndAction(onEnd)
                    .scaleX(1f)
                    .scaleY(1f)
                    .start()

            }
            .scaleX(0.7f).scaleY(0.7f).start()
    }


    private fun setOnClickListeners() {


        /*
        Action Buttons
         */

        btnCancel.setOnClickListener { dialog?.dismiss() }

        btnDone.setOnClickListener {

            runScaleAnim(it, onEnd = {

                listener?.onSelected(
                    threadType = selectedThreadType,
                    inviteeType = selectedInviteeType
                )


            })


        }


        /*
        Public Thread Buttons
         */

        btnPublicGroup.setOnClickListener {

            runScaleAnim(it, onEnd = {
                updateThreadType(ThreadType.Constants.PUBLIC_GROUP)
            })
        }
        btnChannelGroup.setOnClickListener {

            runScaleAnim(it, onEnd = {
                updateThreadType(ThreadType.Constants.CHANNEL_GROUP)
            })
        }


        /*
        Normal Thread Buttons
         */

        btnNormalThread.setOnClickListener {

            runScaleAnim(it, onEnd = {
                updateThreadType(ThreadType.Constants.NORMAL)
            })
        }
        btnOwnerGroup.setOnClickListener {

            runScaleAnim(it, onEnd = {
                updateThreadType(ThreadType.Constants.OWNER_GROUP)
            })

        }
        btnChannel.setOnClickListener {

            runScaleAnim(it, onEnd = {
                updateThreadType(ThreadType.Constants.CHANNEL)
            })

        }


        /*
        Invitee Type Buttons
         */

        btnSSOId.setOnClickListener {

            runScaleAnim(it, onEnd = {
                updateInviteType(InviteType.Constants.TO_BE_USER_SSO_ID)
            })
        }
        btnContactId.setOnClickListener {

            runScaleAnim(it, onEnd = {
                updateInviteType(InviteType.Constants.TO_BE_USER_CONTACT_ID)
            })

        }
        btnCellphoneNumber.setOnClickListener {

            runScaleAnim(it, onEnd = {
                updateInviteType(InviteType.Constants.TO_BE_USER_CELLPHONE_NUMBER)
            })
        }
        btnUsername.setOnClickListener {

            runScaleAnim(it, onEnd = {
                updateInviteType(InviteType.Constants.TO_BE_USER_USERNAME)
            })
        }
        btnUserId.setOnClickListener {

            runScaleAnim(it, onEnd = {
                updateInviteType(InviteType.Constants.TO_BE_USER_ID)
            })
        }


    }

    private fun updateInviteType(type: Int) {

        tvInviteeType.text = "$type"
        selectedInviteeType = type

    }

    private fun updateThreadType(type: Int) {

        tvThreadType.text = "$type"
        selectedThreadType = type

    }

    @SuppressLint("SetTextI18n")
    private fun initial() {

        tvInviteeType.text = "${InviteType.Constants.TO_BE_USER_SSO_ID}"
        if (mode == MODE_PUBLIC) {
            tvTitle.text = "Public Thread"
            tvThreadType.text = "${ThreadType.Constants.PUBLIC_GROUP}"

            btnChannelGroup.visibility = View.VISIBLE
            btnPublicGroup.visibility = View.VISIBLE

            btnNormalThread.visibility = View.INVISIBLE
            btnChannel.visibility = View.INVISIBLE
            btnOwnerGroup.visibility = View.INVISIBLE

        } else {
            tvTitle.text = "Normal Thread"
            tvThreadType.text = "${ThreadType.Constants.NORMAL}"

        }

    }


    interface ICreateThreadOption {

        fun onSelected(threadType: Int, inviteeType: Int)

    }

}
