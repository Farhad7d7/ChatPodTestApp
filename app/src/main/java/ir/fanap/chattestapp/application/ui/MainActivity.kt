package ir.fanap.chattestapp.application.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import com.fanap.podchat.notification.PodNotificationManager
import kotlinx.android.synthetic.main.activity_main_bubble.*
import ir.fanap.chattestapp.R


class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_bubble)


        val titles = arrayOf("Chat", "Function", "Log")
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val pagerAdapter = PagerAdapter(supportFragmentManager, titles)

//        supportFragmentManager.beginTransaction().add(LogFragment.newInstance(), "LogFragment").commit()
        viewPager.adapter = pagerAdapter

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                top_navigation_constraint.setCurrentActiveItem(position)
            }
        })

        top_navigation_constraint.setNavigationChangeListener { view, position ->
            view_pager.setCurrentItem(position, true)
        }
        viewPager.offscreenPageLimit = 2


    }

    //
    override fun onBackPressed() {
        super.onBackPressed()
//        finish()
//        android.os.Process.killProcess(android.os.Process.myPid())

    }


}
