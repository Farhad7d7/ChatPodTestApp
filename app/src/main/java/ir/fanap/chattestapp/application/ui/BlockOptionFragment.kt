package ir.fanap.chattestapp.application.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanap.podchat.requestobject.RequestThread

import ir.fanap.chattestapp.R

/**
 * A simple [Fragment] subclass.
 */
class BlockOptionFragment(tit:RequestThread) : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_block_option, container, false)
    }

}
