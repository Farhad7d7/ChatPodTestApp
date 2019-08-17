package ir.fanap.chattestapp.application.ui.util


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.*

import ir.fanap.chattestapp.R
import kotlinx.android.synthetic.main.fragment_token.*


class TokenFragment : DialogFragment() {



    lateinit var iDialogToken: IDialogToken


    fun setOnTokenSet(iDialogToken: IDialogToken){

        this.iDialogToken = iDialogToken


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {




        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_token, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        btnSetToken.setOnClickListener {

            val token = etToken.text.toString()

            if(token.isNotEmpty())
                iDialogToken.onTokenSet(token)
            else etToken.error = "Enter Token First!"



        }





    }


    interface IDialogToken{


        fun onTokenSet(token:String)

    }


}
