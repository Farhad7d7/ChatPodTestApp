package ir.fanap.chattestapp.bussines.model

import com.github.javafaker.Bool


class Method {

    lateinit var methodName: String
    var funcOne: String? = null
    var funcTwo: String? = null
    var funcThree: String? = null
    var funcFour: String? = null

    var methodNameFlag : Boolean? = null
    var funcOneFlag : Boolean? = null
    var funcTwoFlag : Boolean? = null
    var funcThreeFlag : Boolean? = null
    var funcFourFlag : Boolean? = null

    var log :String? = null

    var funcStatusList : MutableList<FunctionStatus>? = null

    var pending :Boolean = false

    var error :Boolean = false

}