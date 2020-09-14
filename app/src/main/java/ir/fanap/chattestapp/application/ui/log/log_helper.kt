package ir.fanap.chattestapp.application.ui.log

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.fanap.chattestapp.bussines.model.LogClass
import org.json.JSONObject


/**
 *
 * Keep all received Logs
 *
 * Keep all uniqueIds
 *
 * Map each logs to it function by uniqueId
 *
 * */


fun refactorLog(logName: String, log: String, gson: Gson): ArrayList<LogClass> {


    val listOfLogs = ArrayList<LogClass>()


    val jsonReader = try {
        JSONObject(log)
    } catch (e: Exception) {
        Log.e("LTAG", e.message)
        null
    }


    /**
     *
     * ==> situation 1: when response doesn't have an uniqueId in it main body and it is in "content" body
     *
     **/

    if (!jsonReader?.has("uniqueId")!! || jsonReader["uniqueId"] == "") {

        try {

            if (jsonReader.has("content")) {


                val contentString = jsonReader["content"].toString()

                val content = JSONObject(contentString)

                if (content.has("uniqueIds")) {

                    val jsonUniqueIds = content["uniqueIds"].toString()

                    val uniqueIdsList: ArrayList<String> =
                        gson.fromJson(jsonUniqueIds, object : TypeToken<ArrayList<String>>() {

                        }.type)

                    uniqueIdsList.forEach { uniqueId ->

                        listOfLogs.add(createReadableLog(uniqueId, logName, log))

                    }


                }


            }
        } catch (e: Exception) {
            Log.e("LTAG", e.message)
        }



        return listOfLogs

    }


    val uniqueId: String = jsonReader["uniqueId"].toString()


    /**
     *
     * ==> situation 2: when log has a list of uniqueIds instead one
     *
     **/

    if (uniqueId[0] == '[') {

        val uniqueIdsList: ArrayList<String> =
            gson.fromJson(uniqueId, object : TypeToken<ArrayList<String>>() {

            }.type)

        uniqueIdsList.forEach { uniqueIdInList ->

            listOfLogs.add(createReadableLog(uniqueIdInList, logName, log))

        }

        return listOfLogs

    }


    /**
     *
     * ==> situation 3: a single regular log
     *
     **/

    listOfLogs.add(createReadableLog(uniqueId, logName, log))


    return listOfLogs

}

fun createReadableLog(uniqueId: String, logName: String, log: String): LogClass {


    var beautyName = logName
//    var beautyName = logName.replace("Error", changeWordColor(logName, "#f44336"))


    var beautyLog = log.replace("{", "{<br>")


    beautyLog = beautyLog.replace("[", "[<br>")

    beautyLog = beautyLog.replace("}", "<br>}")

    beautyLog = beautyLog.replace("]", "<br>]")

    beautyLog = beautyLog.replace(",", ",<br>")

    beautyLog = beautyLog.replace("\n", "<br>")

    beautyLog = beautyLog.replace("\"type\"", changeWordColor("\"type\"", "blue"))

    beautyLog = beautyLog.replace("\"uniqueId\"", changeWordColor("\"uniqueId\"", "purple"))





    return LogClass(uniqueId, beautyName, beautyLog)


}


fun changeWordColor(word: String, color: String = "#000000"): String {


    return "<font color='$color'>$word</font>"


}