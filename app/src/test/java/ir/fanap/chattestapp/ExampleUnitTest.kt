package ir.fanap.chattestapp

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt
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
    fun testCalcRanTime() {

//        assertEquals("1 s", calcRanTime(1000f))
        assertEquals("500.0 ms", calcRanTime(500f))
        assertEquals("1500 ms", calcRanTime(1500f))
        assertEquals("900 ms", calcRanTime(9_00f))
        assertEquals("1000 ms", calcRanTime(10_000f))


    }

    private fun calcRanTime(ranTime: Float): String {
        return when{
            ranTime == 0f -> ""
            else -> (ranTime).toString() + " ms"
        }
    }

    @Test
    fun randomNumbers() {

        val num = Random.nextInt(1, 20)
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
