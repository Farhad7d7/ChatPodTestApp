package ir.fanap.chattestapp.application.ui.function

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.fanap.podchat.util.InviteType
import com.fanap.podchat.util.TextMessageType
import com.fanap.podchat.util.ThreadType

import ir.fanap.chattestapp.R
import ir.fanap.chattestapp.application.ui.util.FullViewDialogFragment
import kotlinx.android.synthetic.main.fragment_create_thread_option.*
import kotlinx.android.synthetic.main.fragment_get_history_by_type.*
import kotlinx.android.synthetic.main.fragment_leave_thread_option.*

/**
 * A simple [Fragment] subclass.
 */
class LeaveThreadOptionFragment : FullViewDialogFragment() {

    var leaveType = 0 // 0 keep history && 1 == clear history


    private var listener: ICreateThreadOption? = null

    fun setListener(iCreateThreadOption: ICreateThreadOption) {

        listener = iCreateThreadOption
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leave_thread_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btn_leave.setOnClickListener(this::setTypeListener)
        btn_keep.setOnClickListener(this::setTypeListener)
    }

    private fun setTypeListener(view: View) {
        when (view.id) {
            R.id.btn_keep -> leaveType = 0
            R.id.btn_leave -> leaveType = 1

            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }

        runScaleAnim(view, onEnd = {
            listener?.onSelected(leaveType)
            dialog?.dismiss()
        })
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


    interface ICreateThreadOption {

        fun onSelected(leaveType: Int)

    }

}
