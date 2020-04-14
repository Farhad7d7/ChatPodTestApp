package ir.fanap.chattestapp.application.ui.util

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*

import ir.fanap.chattestapp.R
import kotlinx.android.synthetic.main.fragment_token.*


class TokenFragment : FullViewDialogFragment() {


    lateinit var iDialogToken: IDialogToken


    fun setOnTokenSet(iDialogToken: IDialogToken) {

        this.iDialogToken = iDialogToken


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_token, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        btnSetToken.setOnClickListener {

            val token = etToken.text.toString()

            if (token.isNotEmpty())
                iDialogToken.onTokenSet(token)
            else etToken.error = "Enter Token First!"

        }



        btnSetToken.setOnLongClickListener {


            val entry = etToken.text.toString()

            if (entry.isNotEmpty()) {

                if (entry.startsWith("09")) {

                    iDialogToken.onNumberEntered(entry)


                } else {
                    iDialogToken.onOTPEntered(entry)


                }

                etToken.setText("")

            } else etToken.error = "Enter Token First!"

            true

        }


    }


    interface IDialogToken {


        fun onTokenSet(token: String)
        fun onOTPEntered(entry: String)
        fun onNumberEntered(entry: String)

    }


}
