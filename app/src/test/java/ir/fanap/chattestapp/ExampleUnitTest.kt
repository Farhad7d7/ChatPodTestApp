package ir.fanap.chattestapp

import org.junit.Test

import org.junit.Assert.*
import kotlin.random.Random

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
    fun randomNumbers() {

        val num = Random.nextInt(1,20)
        print(num)


    }

    @Test
    fun listTest() {

        val list = ArrayList<String>()

        list.add("s1")


        list.add("s2")


        list.add("s3")


        list.add(0, "s11")

        print(list)


    }
}
