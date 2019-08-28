package ir.fanap.chattestapp.bussines.model

import android.widget.CheckBox

class Method() {


    companion object{

        const val DEACTIVE = 0

        const val RUNNING = 1

        const val DONE = 2

        const val FAIL = 3

    }

    var funcOneState = DEACTIVE
    var funcTwoState = DEACTIVE
    var funcThreeState = DEACTIVE
    var funcFourState = DEACTIVE


    lateinit var methodName: String
    var funcOne: String? = null
    var funcTwo: String? = null
    var funcThree: String? = null
    var funcFour: String? = null

    var methodNameFlag : Boolean? = null
    var funcOneFlag : Boolean? = this.funcOneState == DONE
    var funcTwoFlag : Boolean? = this.funcTwoState == DONE
    var funcThreeFlag : Boolean? = this.funcThreeState == DONE
    var funcFourFlag : Boolean? = this.funcFourState == DONE





    var log :String? = null

    var logs:ArrayList<LogClass> = ArrayList()

    var response:Boolean? = null

    var methodNameImage: Int? = null

    var desc: String? = null
    lateinit var checkBox: CheckBox

    var isSearched:Boolean =false
    var isExpanded:Boolean=false
    var isActive:Boolean=false
    var hasError:Boolean=false



    fun addLog(log: LogClass){


        logs.add(log)

    }





}