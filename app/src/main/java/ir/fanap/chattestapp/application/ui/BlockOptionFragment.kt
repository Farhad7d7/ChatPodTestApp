package ir.fanap.chattestapp.application.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.view.animation.LinearInterpolator


import ir.fanap.chattestapp.R
import ir.fanap.chattestapp.application.ui.util.FullViewDialogFragment
import kotlinx.android.synthetic.main.fragment_block_option.*

/**
 * A simple [Fragment] subclass.
 */
class BlockOptionFragment : FullViewDialogFragment() {

    private var blockId: Long? = 0

    private var title: String? = null

    private var userId: Long? = 0

    private var threadId: Long? = 0

    private var contactId: Long? = null

    private var listener: IBlocOption? = null

    private var targetContactName: String? = ""

    private var targetThreadName: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

            title = it.getString(ARG_TITLE)
            userId = it.getLong(USER_ID)
            threadId = it.getLong(THREAD_ID)
            contactId = it.getLong(CONTACT_ID)
            blockId = it.getLong(BLOCK_ID)
            targetThreadName = it.getString(THREAD_NAME)
            targetContactName = it.getString(CONTACT_NAME)


        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_block_option, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initial()
        setOnClickListeners()

    }

    @SuppressLint("SetTextI18n")
    private fun initial() {

        tvTitle.text = title

        tvActionType.text = "$title with:"

        tvBlockMode.text = "User Id"

        etTargetField.setText("$userId")

        tvTargetContact.text = targetContactName

        tvTargetThread.text = targetThreadName

        if (title.equals("UNBLOCK")) {

            tvTargetThread.visibility = View.INVISIBLE

            btnWithThreadId.visibility = View.INVISIBLE

            btnCustomThreadId.visibility = View.INVISIBLE

            btnWithBlockId.visibility = View.VISIBLE

            btnCustomBlockId.visibility = View.VISIBLE

        }


    }

    @SuppressLint("SetTextI18n")
    private fun setOnClickListeners() {


        btnWithBlockId.setOnClickListener {

            updateField(it)

            tvBlockMode.text = "Block id"

            listener?.doWithBlockIdSelected(blockId!!)


        }

        btnCustomBlockId.setOnClickListener {

            updateField(it)

            tvBlockMode.text = "Custom Block id"

            if (!etTargetField?.text.isNullOrEmpty())
                listener?.doWithBlockIdSelected(etTargetField?.text.toString().toLong())

        }

        btnWithUserId.setOnClickListener {

            updateField(it)

            tvBlockMode.text = "User Id"

            listener?.doWithUserIdSelected(userId!!)

        }

        btnWithContactId.setOnClickListener {

            updateField(it)

            tvBlockMode.text = "Contact Id"

            listener?.doWithContactIdSelected(contactId!!)

        }

        btnWithCustomContactId.setOnClickListener {

            updateField(it)

            tvBlockMode.text = "Custom Contact Id"

            if (!etTargetField?.text.isNullOrEmpty())
                listener?.doWithContactIdSelected(etTargetField?.text.toString().toLong())
        }

        btnWithCustomUserId.setOnClickListener {

            updateField(it)

            tvBlockMode.text = "Custom User Id"

            if (!etTargetField?.text.isNullOrEmpty())
                listener?.doWithUserIdSelected(etTargetField?.text.toString().toLong())


        }

        btnWithThreadId.setOnClickListener {

            updateField(it)

            tvBlockMode.text = "Thread id"

            listener?.doWithThreadIdSelected(threadId!!)

        }

        btnCustomThreadId.setOnClickListener {

            updateField(it)

            tvBlockMode.text = "Custom thread Id"

            if (!etTargetField?.text.isNullOrEmpty())
                listener?.doWithThreadIdSelected(etTargetField?.text.toString().toLong())
        }

        btnCancel.setOnClickListener { dialog?.dismiss() }


    }

    private fun updateField(it: View) {

        runScaleAnim(it)

    }

    companion object {

        const val ARG_TITLE = "argTitle"

        const val USER_ID = "USER_ID"

        const val CONTACT_ID = "CONTACT_ID"

        const val THREAD_ID = "THREAD_ID"

        const val THREAD_NAME = "THREAD_NAME"

        const val CONTACT_NAME = "CONTACT_NAME"

        const val BLOCK_ID = "BLOCK_ID"


    }

    fun setListener(iBlocOption: IBlocOption) {

        listener = iBlocOption
    }

    private fun runScaleAnim(it: View) {
        it.animate().setDuration(150L).setInterpolator(LinearInterpolator())
            .withEndAction {

                it.animate().setDuration(150L).setInterpolator(LinearInterpolator())
                    .scaleX(1f).scaleY(1f).start()

            }
            .scaleX(0.7f).scaleY(0.7f).start()
    }


    interface IBlocOption {

        fun doWithUserIdSelected(userId: Long)

        fun doWithThreadIdSelected(threadId: Long) {}

        fun doWithBlockIdSelected(blockId: Long) {}

        fun doWithContactIdSelected(contactId: Long)

    }

}
