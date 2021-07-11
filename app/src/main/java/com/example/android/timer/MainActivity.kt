package com.example.android.timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import com.example.android.timer.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

const val initTime=30
class MainActivity : AppCompatActivity() {

    private var timeLeft=0 /** Stores the time left in seconds */

    private var currentTimer=0 /** Stores the current start time of timer */

    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding=ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        var task= CoroutineScope(IO).launch {  }

        var startOrPause=true

        /** initial timer */
        setTimer(initTime)



        binding.timeSelecterSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                /** if timer is already running stop the timer */
                if(!(task.isCancelled)){task.cancel()}
                startOrPause=true
                binding.start.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_play,null))


                /** updating the current timer and textview */
                setTimer(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?){}

            override fun onStopTrackingTouch(seekBar: SeekBar?){}

        })




        /** Button to start or stop the timer */
        binding.start.setOnClickListener {
            if(startOrPause){
                binding.start.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_pause,null))

                //Using coroutines for background task
                task= CoroutineScope(IO).launch{
                    timeTicker()
                }
            }
            else{
                binding.start.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_play,null))
                if(!task.isCancelled){task.cancel()}
            }

            startOrPause=!startOrPause
        }


        /** Button to reset the timer */
        binding.stop.setOnClickListener {
            binding.start.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_play,null))
            startOrPause=true
            if(!task.isCancelled){task.cancel()}
            setTimer(currentTimer)
        }



    }

    /** Sets the timer and progressbar to time provided */
    private fun setTimer(time:Int){
        timeLeft=time
        currentTimer=time
        binding.timeTv.text=secToAnalogTime(time)
        binding.progressBar.apply {
            max=time
            progress=time
        }
    }



    /**
     * Converts given time into analog string
     */
    private fun secToAnalogTime(time:Int):String{
        if(time==0){
            return getString(R.string.time_up)
        }
        var timeInString=""
        var currTime=time
        val hours=currTime/3600
        currTime%=3600
        val minutes=currTime/60
        currTime%=60
        val seconds=currTime

        timeInString += if(hours<10){ "0$hours:" } else{ "$hours:" }

        timeInString += if(minutes<10){ "0$minutes:" } else{ "$minutes:" }

        timeInString += if(seconds<10){ "0$seconds" } else{ "$seconds" }

        return timeInString
    }

    /** decreases the time left after every second */
    private suspend fun timeTicker(){
        while(timeLeft>0){
            delay(1000L)
            timeLeft--
            updateTime()
        }
    }

    /** Updates the timer and progressBar */
    private suspend fun updateTime(){
        val time=secToAnalogTime(timeLeft)

        //Going to main thread
        withContext(Main){
            binding.timeTv.text=time
            binding.progressBar.progress=timeLeft
        }
    }
}