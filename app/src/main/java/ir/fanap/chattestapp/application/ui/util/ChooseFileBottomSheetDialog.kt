package ir.fanap.chattestapp.application.ui.util


import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import ir.fanap.chattestapp.R
import kotlinx.android.synthetic.main.bottom_menu.*

/**
 * A simple [Fragment] subclass.
 */
class ChooseFileBottomSheetDialog : BottomSheetDialogFragment() {


    companion object {


        const val FILE = 0

        const val IMAGE = 1

        const val CANCEL = -1
    }


    private lateinit var listener: IPickFile


    fun setPickerListener(pickListener: IPickFile) {
        listener = pickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.bottom_menu, container, false)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)

        listener.onSelect(CANCEL)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        appCompatImageView_folder.setOnClickListener {


            runScaleAnim(it, Runnable {

                listener.onSelect(FILE)

            })

        }


        appCompatImageView_gallery.setOnClickListener {

            runScaleAnim(it, Runnable {

                listener.onSelect(IMAGE)

            })
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

    private fun runScaleAnim(it: View,endAction:Runnable) {
        it.animate().setDuration(150L).setInterpolator(LinearInterpolator())
            .withEndAction {

                it.animate().setDuration(150L).setInterpolator(LinearInterpolator())
                    .withEndAction(endAction)
                    .scaleX(1f).scaleY(1f).start()

            }
            .scaleX(0.7f).scaleY(0.7f).start()
    }




}


interface IPickFile {

    fun onSelect(type: Int)
}


