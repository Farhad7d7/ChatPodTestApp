package ir.fanap.chattestapp

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun listTest(){

        val list=ArrayList<String>()

        list.add("s1")


        list.add("s2")


        list.add("s3")


        list.add(0,"s11")

        print(list)


    }
}
