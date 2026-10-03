包com.example.deskpet

进口android.app.Notification
进口android.app.NotificationChannel
进口android.app.NotificationManager
进口android.app.Service
进口android.content.Context
进口android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import kotlin.math.abs

class PetService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var petView: TextView
    private lateinit var bubbleView: TextView
    private lateinit var petParams: WindowManager.LayoutParams
    private lateinit var bubbleParams: WindowManager.LayoutParams

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false
    private var lastClickTime = 0L

    private val handler = Handler(Looper.getMainLooper())
    private val emojis = listOf("🐱", "😺", "😸", "😻", "🐾", "😴", "😋")

    private val happyWords = listOf(
        "你好呀～", "摸摸我！", "今天也要开心哦", "我在这里！",
        "别戳我啦～", "嘿嘿～", "陪我玩嘛", "我最喜欢你了"
    )
    私人的 Val hungryWords=listOf("我饿了…", "想吃小鱼干", "喂喂我嘛", "肚子咕咕叫")
私人的Val fullWords=listOf("谢谢！好好吃～", "饱饱的~", "最喜欢你了！")

    私人的 Val 首选项 通过惰性{
getSharedPreferences("宠物", Context.MODE_PRIVATE)
    }

私人的Val moveRunnable=对象：可运行{
覆盖 乐趣 跑() {
如果(：：petView.isInitialized){
petParams.x+=(-12..12).随机()
petParams.y+=(-12..12).随机()
夹钳位置()
WindowManager.updateViewLayout(petView，petParams)
updateBubblePosition()
handler.postDelayed(这，1500)
            }
        }
    }

私人的Val talkRunnable=对象：可运行{
覆盖 乐趣 跑() {
如果(：：petView.isInitialized){
Val饥饿的=prefs.getInt("饥饿", 0) +1
prefs.edit().putInt("饥饿"，饿了).应用()
Val单词=如果(饥饿>5)hungryWords其他happyWords
showBubble(words.random())
            }
handler.postDelayed(这，8000)
        }
    }

    private val bubbleHideRunnable = Runnable {
        if (::bubbleView.isInitialized && bubbleView.visibility == View.VISIBLE) {
            bubbleView.visibility = View.GONE
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createPetView()
        createBubbleView()
    }

    private fun createNotification(): Notification {
        val channelId = "deskpet"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "桌宠运行中",
                NotificationManager.IMPORTANCE_LOW
            )
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("桌宠")
            .setContentText("宠物正在桌面上陪你")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }

    private fun createPetView() {
        petView = TextView(this).apply {
            text = emojis.first()
            textSize = 52f
            setPadding(20, 20, 20, 20)
        }

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        petParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 200
            y = 500
        }

        petView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = petParams.x
                    initialY = petParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDragging = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - initialTouchX
                    val dy = event.rawY - initialTouchY
                    if (abs(dx) > 10 || abs(dy) > 10) isDragging = true
                    petParams.x = initialX + dx.toInt()
                    petParams.y = initialY + dy.toInt()
                    clampPosition()
                    windowManager.updateViewLayout(petView, petParams)
                    updateBubblePosition()
                    true
                }

MotionEvent.ACTION_UP->{
                    如果(！isDragging){
                        Val 现在=System.currentTimeMillis()
                        如果(现在-lastClickTime<300) {
onFeedClick()
                        } 其他 {
onPetClick()
                        }
lastClickTime=now
                    }
                    正确
                }

                其他->假的
            }
        }

windowManager.addView(petView，petParams)
handler.postDelayed(moveRunnable，1500)
handler.postDelayed(talkRunnable，5000)
    }

    私人的 乐趣 createBubbleView() {
bubbleView=文本视图(这).应用{
文本="你好呀~"
textSize=16F
SetTextColor(0xFF000000.toInt())
setPadding(30, 18, 30, 18)
            setBackgroundColor(0xEEEEDD99.toInt())
            visibility = View.GONE
        }

Val类型=如果(Build.版本。SDK_INT>=Build.version_CODES.O){
WindowManager.LayoutParams.Type_APPLICATION_OVERLAY
} 其他 {
            @Suppress("贬低")
WindowManager.LayoutParams.TYPE_PHONE
        }

bubbleParams=WindowManager.LayoutParams(
WindowManager.LayoutParams.WRAP_CONTENT，
WindowManager.LayoutParams.WRAP_CONTENT，
类型，
WindowManager.LayoutParams.FLAG_NOT_cocusable或
WindowManager.LayoutParams.Flag_NOT_TOUCHABLE，
PixelFormat.TRANSLUCENT
).应用{
gravity=Gravity.TOP或Gravity.START
X=petParams.x
y=petParams.y-180
        }

添加视图(bubbleView，bubbleParams)
}

私人的乐趣onPetClick(){
Val亲密=prefs.getInt("亲密", 0) +1
prefs.edit().putInt("亲密"，亲密关系).应用()"亲密"，亲密关系).应用()

petView.text=emojis.random()

Val饥饿的=prefs.GetInt("饥饿"，0)"饥饿"，0)"饥饿"，0)"饥饿"，0)petParams.y=petParams.y.cocein(0，最大)0，最大)0，最大)0，最大)0，最大)0，最大)0，最大)0，最大)0，最大)0，最大)0，最大)0，Maxy)0，Maxy)0，Maxy)，0)"饥饿"，0)"饥饿"，0)"饥饿"，0)"饥饿"，0)"饥饿"，0)petParams.Y=petParams.y.cocein(0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，Maxy)0，maxY)0，maxY)0，maxY)0，maxY)，0)"饥饿"，0)"饥饿"，0)"饥饿"，0)"饥饿"，0)"饥饿"，0)petParams.Y=petParams.y.cocein(0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，Maxy)0，maxY)0，maxY)0，maxY)0，maxY)，0)"饥饿"，0)"饥饿"，0)"饥饿"，0)"饥饿"，0)"饥饿", 0)petParams.Y=petParams.y.cocein(0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)，0)"饥饿", 0)
Val单词=如果(饥饿>5)highywords其他happyWords5)hungryWords其他happyWords高词其他happyWords5)hungryWords其他happyWords高词其他happyWords5)hungryWords其他happyWords高词其他happyWords5)hungryWords其他happyWords高词其他happyWords5)hungryWords其他happyWords高词其他happyWords5)hungryWords其他happyWords高词其他happyWords5)hungryWords其他happyWords高词其他happyWords5)hungryWords其他happyWords
showBubble(words.random()+"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")"\n亲密度：$intimacy")
    }

强制至少(
Val满的=prefs.GetInt("full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full",0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full"，0)+10"full", 0) +10
prefs.edit().putInt（“完整”，完整）。PUTINT("饥饿"，0).apply()"full"，完整).PUTINT("饥饿"，0).apply("完整"，完整).PUTINT("饥饿"，0).apply()"full"，完整).PUTINT("饥饿"，0).apply("完整"，完整).PUTINT("饥饿"，0).apply()"full"，完整).PUTINT("饥饿"，0).apply("完整"，完整).PUTINT("饥饿"，0).apply()"full"，完整）.PUTINT("饥饿"，0).apply()"饥饿"，0).apply()"full"，完整).PUTINT("饥饿"，0).apply("完整"，完整).PUTINT("饥饿"，0).apply()"full"，完整).PUTINT("饥饿"，0).apply("完整"，完整).PUTINT("饥饿"，0).apply()"full"，完整).PUTINT("饥饿"，0).apply("完整"，完整).PUTINT("饥饿"，0).apply()"full"，完整）.PUTINT("饥饿"，0).appl"饥饿"，0).apply()"full"，完整).PUTINT("饥饿"，0).apply("完整"，完整).PUTINT("饥饿"，0).apply()"full"，完整）.PUTINT("饥饿"，0).apply()"完整"，完整).PUTINT("饥饿"，0).apply()"full"，完整).PUTINT("饥饿"，0).apply()"完整"，完整).PUTINT("饥饿"，0).apply()"full"，完整).PUTINT("饥饿"，0).apply()"饥饿"，0).apply()"full"，完整).PUTINT("饥饿"，0).apply("完整"，完整).PUTINT("饥饿"，0).apply()"full"，完整）.PUTINT("饥饿"，0).apply()"完整"，完整).PUTINT("饥饿"，0).apply()"full"，完整).PUTINT("饥饿"，0).apply()"完整"，完整).PUTINT("饥饿"，0).apply()"full"，完整).PUTINT("饥饿", 0).apply()

petView.text="😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋""😋"
showBubble(fullWords.random()+"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$满足")"\n饱食度：$full")
    }

包
如果(：：bubbleView.isInitialized){
bubbleView.text=文本
bubbleView.visibility=View.VISIBLE
updateBubblePosition()
handler.removeCallbacks(bubbleHideRunnable)
handler.postDelayed(bubbleHideRunnable，3000)
        }
    }

私人的乐趣updateBubblePosition(){
如果(：：bubbleView。IsInitialized&&：：petParams.IsInitialized){
bubbleParams.x=petParams.x
强制至少(0)180返回强制至少(0)180返回start_STICKY0)180)。强制至少(0)180返回start_STICKY0)0)180返回start_STICKY0)180).强制至少(0)180返回start_STICKY0)0)180返回start_STICKY0)180).强制至少(0)180返回start_STICKY0)180).强制至少(0)180返回start_STICKY0)180).强制至少(0)180返回start_STICKY0)0)180返回强制至少(0)180返回start_STICKY0)180)。强制至少(0)180返回start_STICKY0)0)180返回start_STICKY0)180).强制至少(0)180返回start_STICKY0)180).强制至少(0)180返回start_STICKY0)0)180返回start_STICKY0)180)。强制至少(0)180返回start_STICKY0)180).强制至少(0)180返回start_STICKY0)0)180返回start_STICKY0)180)。强制至少(0)180返回start_STICKY0)180)。强制至少(0)180返回start_STICKY0)180).强制至少(0)180返回start_STICKY0)
WindowManager.updateViewLayout(bubbleView，bubbleParams)
        }
    }

私人的乐趣clampPosition(){
Val指标=resources.displayMetrics
Val Maxx=metrics.width像素-200200200200200200200200200200200200200200200200200200200200200200200200200200200200200200200200
Val Maxy=度量。高度像素300300300300300300300300300300300300300300300300
petParams.X=petParams.x.cocein(0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)0，Maxx)
petParams.Y=petParams.y.cocein(0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)0，maxY)
    }

覆盖兴趣onStartCommand(intent:Intent？，标志：int，startid:Int)：Int{
私人的乐趣showBubble(文本：字符串){
    }

覆盖乐趣onDestroy(){
超级.onDestroy()
handler.removeCallbacksAndMessages(无效的)

如果(：：petView.isInitialized){
windowManager.removeView(petView)
        }
如果(：：bubbleView.isInitialized){
windowManager.removeView(bubbleView)
        }
    }

覆盖乐趣onBind(意图：意图？)：IBinder？=无效的
}
