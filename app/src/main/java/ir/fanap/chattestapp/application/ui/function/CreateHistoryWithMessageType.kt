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

class CreateHistoryWithMessageType : FullViewDialogFragment() {


    var selectedMessageType = TextMessageType.Constants.TEXT

    var ThereadId : Long = 0;

    private var listener: IGetHistory? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_history_by_type, container, false)
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
        btn_tptext.setOnClickListener(this::setTypeListener)
        btn_tpvoice.setOnClickListener(this::setTypeListener)
        btn_tppicture.setOnClickListener(this::setTypeListener)
        btn_tpvideo.setOnClickListener(this::setTypeListener)
        btn_tpsound.setOnClickListener(this::setTypeListener)
        btn_tpfile.setOnClickListener(this::setTypeListener)
        updateSelectedType()
        edt_Thread_Id.setText(ThereadId.toString())
    }

    private fun setTypeListener(view: View) {
        when (view.id) {
            R.id.btn_tptext -> selectedMessageType = TextMessageType.Constants.TEXT
            R.id.btn_tpvoice -> selectedMessageType = TextMessageType.Constants.VOICE
            R.id.btn_tppicture -> selectedMessageType = TextMessageType.Constants.PICTURE
            R.id.btn_tpvideo -> selectedMessageType = TextMessageType.Constants.VIDEO
            R.id.btn_tpsound -> selectedMessageType = TextMessageType.Constants.SOUND
            R.id.btn_tpfile -> selectedMessageType = TextMessageType.Constants.FILE
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }
        setThreadId(edt_Thread_Id.text.toString().toLong())
        updateSelectedType();
        runScaleAnim(view, onEnd = {
            listener?.onSelected(messageType = selectedMessageType,selectedTHereadId = ThereadId)
            dialog?.dismiss()
        })
    }

    private fun updateSelectedType() {
        when (selectedMessageType) {
            TextMessageType.Constants.TEXT -> tvThreadType.setText("TEXT")
            TextMessageType.Constants.VOICE -> tvThreadType.setText("VOICE")
            TextMessageType.Constants.PICTURE -> tvThreadType.setText("PICTURE")
            TextMessageType.Constants.VIDEO -> tvThreadType.setText("VIDEO")
            TextMessageType.Constants.SOUND -> tvThreadType.setText("SOUND")
            TextMessageType.Constants.FILE -> tvThreadType.setText("FILE")
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

        fun onSelected(messageType: Int,selectedTHereadId :Long)

    }

}