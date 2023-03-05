class Timer(_delay: Int) : Thread() {
    private var delay = 10
    private var stopped = false

    init {
        setDelay(_delay)
    }

    fun setDelay(newDelay:Int)
    {
        if(newDelay>0)
            delay = newDelay
    }

    override fun run()
    {
        while(true)
        {
            sleep(delay.toLong())
            if (stopped) break
        }
        stopped = false
    }
    fun stopTimer()
    {
        stopped = true
    }
}