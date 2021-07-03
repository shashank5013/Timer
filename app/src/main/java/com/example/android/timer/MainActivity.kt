package com.example.android.timer

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.example.android.timer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var timeLeft=0 //Stores the time left in seconds

    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding=ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        timeLeft=600

        var task=TimerTask()

        var StartOrPause=true


        //Button to start or stop the timer
        binding.start.setOnClickListener {
            if(StartOrPause){
                binding.start.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_pause,null))
                task=TimerTask()
                task.execute(timeLeft)
            }
            else{
                binding.start.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_play,null))
                if(task.isCancelled==false){task.cancel(true)}
            }

            StartOrPause=!StartOrPause
        }
        binding.stop.setOnClickListener {
            if(task.isCancelled==false){task.cancel(true)}
            timeLeft=600
            binding.timeTv.text=getString(R.string.initial_time)
        }


    }

    /**
     * time passed to be calculated in background thread using AsyncTask
     * */
    private inner class TimerTask:AsyncTask<Int,Int,Void>(){

        /**
         * Calculates every second until no time left or timer stopped
         */
        override fun doInBackground(vararg params: Int?): Void? {
            var time=params[0]!!
            while(time>0 && isCancelled()==false){
               wait1Sec()
                time-=1
                publishProgress(time)

            }
            return null
        }

        /**
         * Updates the textView after every second
         */
        override fun onProgressUpdate(vararg values: Int?) {
            timeLeft=values[0]!!
            val timeinString=convertSec(values[0]!!)
            binding.timeTv.text=timeinString
        }

        /**
         * Calculates if a second has passed using System.currentTimeMillis()
         */
        private fun wait1Sec() {
            val currTime=System.currentTimeMillis()
            while(System.currentTimeMillis()<currTime+1000){}
        }

        /**
         * Converts given time into analog string
         */
        private fun convertSec(time:Int):String{
            var timeInString=""
            var currTime=time
            val hours=currTime/3600
            currTime%=3600
            val minutes=currTime/60
            currTime%=60
            val seconds=currTime

            timeInString += if(hours<10){ "0$hours:" }
            else{ "$hours:" }

            timeInString += if(minutes<10){ "0$minutes:" }
            else{ "$minutes:" }

            timeInString += if(seconds<10){ "0$seconds" }
            else{ "$seconds" }

            return timeInString
        }

        /**
         * if time is over displays "Time Up"
         */
        override fun onPostExecute(result: Void?) {
            binding.timeTv.text=getString(R.string.time_up)
        }

    }
}