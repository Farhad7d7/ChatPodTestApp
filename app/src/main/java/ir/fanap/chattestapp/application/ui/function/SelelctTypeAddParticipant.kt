package ir.fanap.chattestapp.application.ui.function

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.fanap.podchat.util.TextMessageType
import ir.fanap.chattestapp.R
import ir.fanap.chattestapp.application.ui.util.FullViewDialogFragment
import kotlinx.android.synthetic.main.fragment_get_history_by_type.*
import kotlinx.android.synthetic.main.fragment_get_history_by_type.btn_tppicture
import kotlinx.android.synthetic.main.fragment_get_history_by_type.btn_tptext
import kotlinx.android.synthetic.main.fragment_get_history_by_type.btn_tpvoice
import kotlinx.android.synthetic.main.fragment_get_history_by_type.edt_Thread_Id
import kotlinx.android.synthetic.main.fragment_get_history_by_type.tvThreadType
import kotlinx.android.synthetic.main.fragment_select_type_participant.*

class SelelctTypeAddParticipant : FullViewDialogFragment() {


    var selectedMessageType = TextMessageType.Constants.TEXT

    var ThereadId: Long = 0;

    private var listener: IGetHistory? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_type_participant, container, false)
    }

    fun setThreadId(id: Long) {
        ThereadId = id;

    }

    fun setListener(iCreateThreadOption: IGetHistory) {

        listener = iCreateThreadOption
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        btn_ContactId.setOnClickListener(this::setTypeListener)
        btn_username.setOnClickListener(this::setTypeListener)
        btn_userid.setOnClickListener(this::setTypeListener)
        updateSelectedType()
        edt_Thread_Id.setText(ThereadId.toString())

    }

    private fun setTypeListener(view: View) {
        when (view.id) {
            R.id.btn_ContactId -> selectedMessageType = 2
            R.id.btn_username -> selectedMessageType = 3
            R.id.btn_userid -> selectedMessageType = 1
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }
        setThreadId(edt_Thread_Id.text.toString().toLong())
        updateSelectedType();
        runScaleAnim(view, onEnd = {
            listener?.onSelected(messageType = selectedMessageType, selectedTHereadId = ThereadId)
            dialog?.dismiss()
        })
    }

    private fun updateSelectedType() {
        when (selectedMessageType) {
            2 -> tvThreadType.setText("btn_ContactId")
            3 -> tvThreadType.setText("btn_username")
            1 -> tvThreadType.setText("btn_userid")
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }
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


    interface IGetHistory {

        fun onSelected(messageType: Int, selectedTHereadId: Long)

    }

}