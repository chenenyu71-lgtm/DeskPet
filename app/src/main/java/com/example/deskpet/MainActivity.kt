包com.example.deskpet

进口android.Manifest
进口android.content.Intent
进口android.content.pm.PackageManager
进口android.net.Uri
进口android.os.Build
进口android.os.Bundle
进口android.provider。设置
进口android.widget.Button
进口android.widget.LinearLayout
进口android.widget.TextView
进口androidx.appcompat.app.AppCompatActivity
进口androidx.core.app.ActivityCompat
进口androidx.core.content.ContextCompat

班级MainActivity:AppCompatActivity(){

覆盖乐趣onCreate(savedInstanceState:Bundle？){
超级.onCreate(savedInstanceState)

Val布局=线性布局(这).应用{
方向=线性布局.垂直
setPadding(50, 100, 50, 50)
        }

Val标题=TextView(这).应用{
文本="手机桌宠\n\n1. 点下面按钮\n2. 允许悬浮窗权限\n3. 回到桌面看宠物\n\n单击宠物：摸头说话\n双击宠物：喂食"
textSize=18F
        }

Val BTN=按钮(这).应用{
文本="启动桌宠"
setOnClickListener{
如果(构建。版本。SDK_INT>=Build.version_CODES。米&&
！Settings.canDrawOverlays(这@MainActivity)
) {
Val意图=意向(
Settings.ACTION_MANAGE_OVERLAY_PERMISSION，
Uri.parse("包：$PackageName")
                    )
startActivity(intent)
} 其他 {
requestNotificationPermission()
startPetService()
                }
            }
        }

Val stopBtn=按钮(这).应用{
文本="停止桌宠"
setOnClickListener{
stopService(目的(这@MainActivity，PetService：：班级.Java))
            }
        }

layout.addView(标题)
layout.addView(btn)
layout.addView(stopBtn)
setContentView(布局)
    }

私人的乐趣requestNotificationPermission(){
如果(Build.VERSION.SDK_INT>=33) {
如果(ContextCompat.checkSelfPermission(
这,
manifest.permission.POST_NOTIFICATIONS
)！=PackageManager.PERMISSION_GRANTED
) {
ActivityCompat.requestPermissions(
这,
arrayof(清单。许可。post_NOTIFICATIONS)，
                    1001
                )
            }
        }
    }

私人的乐趣startPetService(){
Val意图=意向(这，PetService：：班级java)
如果(构建。版本。SDK_INT>=Build.version_CODES.O){
startForegroundService(intent)
} 其他 {
StartService(intent)
        }
    }
}
