package com.example.android.timer

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import com.example.android.timer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var timeLeft=0 /** Stores the time left in seconds */

    var currentTimer=30 /** Stores the current start time of countdown */

    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding=ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        var task=TimerTask()

        var StartOrPause=true

        /** initial timer stage */
        timeLeft=currentTimer
        binding.timeTv.text=task.convertSec(currentTimer)
        binding.progressBar.apply {
            max=currentTimer
            progress=currentTimer
        }



        binding.timeSelecterSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                /** if timer is already running stop the timer */
                if(!task.isCancelled){task.cancel(true)}
                StartOrPause=true
                binding.start.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_play,null))


                /** updating the current timer and textview */
                currentTimer=progress
                timeLeft=currentTimer
                binding.timeTv.text=task.convertSec(progress)
                binding.progressBar.apply {
                    max=currentTimer
                    this.progress=progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })




        /** Button to start or stop the timer */
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


        /** Button to reset the timer */
        binding.stop.setOnClickListener {
            binding.start.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_play,null))
            StartOrPause=true
            if(task.isCancelled==false){task.cancel(true)}
            timeLeft=currentTimer
            binding.timeTv.text=task.convertSec(currentTimer)
            binding.progressBar.apply {
                max=currentTimer
                progress=currentTimer
            }
        }



    }

    /**
     * time passed to be calculated in background thread using AsyncTask
     * */
        inner class TimerTask:AsyncTask<Int,Int,Void>(){

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
            binding.progressBar.progress=values[0]!!
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
           public fun convertSec(time:Int):String{
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