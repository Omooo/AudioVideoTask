# AudioVideoTask
---
任务列表
---

### 1. 在Android平台绘制一张图片，使用至少三种不同的API，ImageView、SurfaceView、自定义View

参考：[https://www.jianshu.com/p/4f2c1d00f32f](https://www.jianshu.com/p/4f2c1d00f32f)

**SurfaceView与View的区别**

SurfaceView是在一个新起的单独线程中可以重新绘制画面，而View必须在UI主线程中更新画面。

**SurfaceView原理**

* 什么是Surface？

  简单来说Surface对应了一块屏幕缓存区，每个window对应一个Surface，任何View都是画在Surface上的，传统的View共享一块屏幕缓存区，所有的绘制必须在UI线程中进行。

* Surface和SurfaceView的关系

  SurfaceView封装了一个Surface对象，而不是Canvas，因为Surface可以使用后台线程绘制。

  对于那些资源敏感的操作或者要求快速更新或者高速帧率的地方，例如使用3D图形或者实时预览摄像头，这一点特别有用。

* 什么是SurfaceHolder.CallBack？

  SurfaceHolder.CallBack主要是当底层的Surface被创建、销毁或者改变时提供回调通知。

  由于绘制必须在Surface被创建后才能进行，因此SurfaceHolder.Callback中的surfaceCreated和surfaceDestoryed就成了绘图处理代码的边界。

  SurfaceHolder，可以把它当成Surface的容器和控制器，用来操纵Surface，处理它的Canvas上画的效果和动画，控制表面、大小、像素等。

* 使用代价

  独立于GUI线程进行绘图的代价是额外的内存消耗，所以，虽然它是创建定制View的有效方式，有时甚至是必须的，但是使用SurfaceView的时候仍需谨慎。

* 应该注意

  所有SurfaceView和SurfaceHolder.Callback的方法都应该在UI线程里调用，一般来说就是应用程序主线程。渲染线程所要访问的各种变量应该做同步处理。

参考：[https://www.jianshu.com/p/70912c55a03b](https://www.jianshu.com/p/70912c55a03b)

**SurfaceView优缺点**
优点：

* 在一个子线程中对自己进行绘制，避免造成UI线程阻塞
* 高效复杂的UI效果
* 独立的Surface、独立的Window
* 使用双缓冲机制，播放视频时画面更加流畅

缺点：

* 每次绘制都会优先绘制黑色背景，更新不及时会出现黑边现象
* Surface不在View hierachy中，它的显示也不受View的属性控制，平移、缩放等变换

**SurfaceView双缓冲区**

双缓冲：在运用时可以理解为：SurfaceView在更新视图时用到了两张Canvas，一张frontCanvas和一张backCanvas，每次实际显示的是frontCanvas，backCanvas存储的是上一次更改前的视图。当你播放这一帧的时候，它已经提前帮你加载好后面一帧了，所以播放起来更加流畅。

当使用lockCanvas()获取画布时，实际上是backCanvas而不是正在显示的frontCanvas，之后你在获取到的backCanvas上绘制新视图，在unlockCanvasAndPost此视图，那么上传的这张Canvas将替换原有的frontCanvas作为新的frontCanvas，原来的frontCanvas将切换到后台作为backCanvas。相当于多个线程，交替解析和渲染每一帧视频数据。

SurfaceHolder.lockCanvas ----> SurfaceHolder.unlockCanvasAndPost(canvas)

![](https://upload-images.jianshu.io/upload_images/1592280-bea74430778c80b1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/413)



双缓冲技术是游戏开发中的一个重要的技术。当一个动画争先显示时，程序又在改变它，前面还没有显示完，程序又请求重新绘制，这样屏幕就会不停地**闪烁**。而双缓冲技术是把要处理的图片在内存中处理好之后，再将其显示在屏幕上。双缓冲主要是为了解决 反复局部刷屏带来的闪烁。把要画的东西先画到一个内存区域里，然后整体的一次性显示出来。需要注意的是，每次在绘制的时候都需要清除Canvas画布，不然出现画面重叠现象。

这种做的好处就是：

1. 提高渲染效率
2. 可以避免刷新评率过高而出现闪烁现象


**SurfaceView怎么进行旋转，透明操作的？**

* 普通View旋转后，View的内容也跟着同步做了旋转
* SurfaceView在旋转后，其显示内容并没有跟着一起旋转

这就好比在墙上开了一个窗（Surface），通过窗口可以看到外面的花花世界，但是无论窗口怎么变化，窗外面的画面是不会随着窗口变化的。

**一般视频播放器可以横竖屏切换，是如何实现的？**

在Activity中覆写onConfigurationChanged方法就可以，根据横竖屏切换，修改SurfaceView的Parameter的宽高参数即可。



### 2. 在Android平台使用AudioRecord和AudioTrack API完成音频PCM数据的采集和播放，并实现读写音频wav文件

[音频采集 AudioRecorder](https://www.jianshu.com/p/125b94af7c08)

[音频播放 AudioTrack](https://www.jianshu.com/p/632dce664c3d)

[PCM转WAV](https://www.jianshu.com/p/f7863638acbe)

##### Android 音频采集

Android平台上的音频采集一般就两种方式：

1. 使用MediaRecorder进行音频采集

   MediaRecorder是基于AudioRecorder的API（最终还是会创建AudioRecord用来与AudioFlinger进行交互），它可以直接将采集到的音频数据转化为执行的编码格式并保存。这种方案相较于调用系统内置的应用程序，便于开发者在UI界面上布局，而且系统封装的很好，便于使用，唯一的缺点是使用它录下来的音频是经过编码的，没有办法得到原始的音频。同时MediaRecorder既可用于音频的捕获也可以用于视频的捕获，相当的强大，实际开发中没有特殊的需求的话，用的比较多。

2. 使用AudioRecord进行音频采集

   AudioRecord是一个比较偏底层的API，它可以获取到一帧帧PCM数据，之后可以对这些数据进行处理。AudioRecord这种方式采集最为灵活，使得开发者最大限度的处理采集的音频，同时它捕获到的音频是原始的PCM格式。像做变声处理的需要必须要用它收集音频。

在实际开发中，它也是最常用来采集音频的手段。如直播技术采用的就是AudioRecorder采集音频数据。

AudioRecord保存的音频文件为.pcm格式，不能直接播放，它是一个原始的文件，没有任何播放格式，因此就无法被播放器识别并播放。

播放PCM文件有两种：

1. 使用AudioTrack播放PCM格式的音频数据
2. 将PCM数据转化为wav格式的数据，这样就可以被播放器识别

**AudioRecord的基本参数说明**

```java
public AudioRecord(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat,
            int bufferSizeInBytes)
```

* audioSource   音频采集的来源
* audioSampleRate   音频采样率
* channelConfig   声道
* audioFormat   音频采样精度，指定采样的数据格式和每次采样的大小
* bufferSizeInBytes   AudioRecord采集到的音频数据所存放的缓冲区大小

**AudioRecord的状态用意**

* 获取AudioRecord的状态

  用于检测AudioRecord是否确保了获得适当的硬件资源，在AudioRecord对象实例化之后调用getState()

  * STATE_INITIALIZED 舒适化完毕
  * STATE_UNINITIALIZED 未初始化

* 开始采集时调用mAudioRecord.startRecording()开始录音

  开始采集之后，状态自动变为RECORDSTATE_RECORDING

* 停止采集时调用mAudioRecord.stop()停止录音

  停止采集之后，状态自动变为RECORDSTATE_STOPPED

* 读取录制内存，将采集到的数据读取到缓存区，方法调用的返回值的状态码

  情况异常：ERROR_INVALID_OPERATION，ERROR_BAD_VALUE



##### 音频播放AudioTrack

**音频播放**
音频播放声音分为MediaPlayer和AudioTrack两种方案。MediaPlayer可以播放多种格式的声音文件，例如MP3、WAV、AAC等。然而AudioTrack只能播放PCM数据流，当然两者之间还是有紧密的联系。MediaPlayer在播放音频时，在framework层还是会创建AudioTrack，把解码后的PCM数据流传递给AudioTrack，最后由AudioFlinger进行混音，传递音频给硬件播放出来。利用AudioTrack播放只是跳过MediaPlayer的解码部分而已。

**AudioTrack基本参数**

```java
public AudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat,
            int bufferSizeInBytes, int mode)
```

* streamType   音频流类型

  最主要的几种Stream类型：

  * AudioManager.STREAM_MUSIC   用于音乐播放的音频流
  * AudioManager.STREAM_SYSTEM   用于系统声音的音频流
  * AudioManager.STREAM_RING 用于电话铃声的音频流
  * AudioManager.STREAM_VOICE_CALL   用于电话通话的音频流
  * AudioManager.STREAM_ALARM   用于警报的音频流
  * AudioManager.STREAM_NOTIFICATION   用于通知的音频流
  * ...

* mode   

  只有两种：

  - AudioTrack_MODE_STREAM
    STREAM的意思是由用户在应用程序通过write方式把数据一次一次的写到AudioTrack中，应用层从某个地方获取数据，例如通过编解码得到PCM数据，然后write到AudioTrack，这种方式的坏处就是总是在Java层和Native层交互，效率损失较大

  - AudioTrack_MODE_STATIC

    STATIC就是数据一次性交付给接收方，好处是简单高效，只需要进行一次操作就完成了数据的传递。缺点也很明显，对于数据量较大的音频回放，显然是不能胜任的，因而通常只用于播放铃声、系统提醒等对内存小的操作

**AudioTrack作用**
AudioTrack是管理和播放单一音频资源的类。AudioTrack仅仅能播放已经解码的PCM流，用于PCM音频流的回放。

AudioTrack实现PCM音频播放流程：

1. 配置基本参数
2. 获取最小缓冲区大小
3. 创建AudioTrack对象
4. 获取PCM文件，转成DataInputStream
5. 开启/停止播放

系统将这几种声音区分管理，好处就是比如你在听歌的时候接电话，接电话调大音量，在挂电话的时候继续放歌的音量肯定不用再调了。



##### PCM转WAV

PCM：

​	PCM是在由模拟信号向数字信号转化的一种常用的编码格式，称为脉冲编码调制，PCM将模拟信号按照一定的间距划分为多段，然后通过二进制去量化每一个间距的强度。

PCM表示的是音频文件中随着时间的流逝的一段音频的振幅，Android在WAV文件中支持PCM的音频数据。

WAV：

​	WAV、MP3等比较常见的音频格式，不同的编码格式对应不同的原始音频，为了方便传输，通常会压缩原始音频。为了辨别出音频格式，每种格式有特定的头文件（header）。

WAV以RIFF为标准，RIFF是一种资源交换档案标准，RIFF将文件存储在每一个标记快中。

基本构成单位是trunk，每个trunk是由标记位、数据大小、数据存储，三个部分构成。

PCM打包成WAV：

PCM是原始音频数据，WAV是windows中常见的音频格式，只是在PCM数据中添加了一个文件头。



### 3. 在Android平台使用Camera API进行视频采集，分别使用SurfaceView、TextureView预览Camera数据，取的NV21的数据回调

[Android 分别使用 SurfaceView 和 TextureView 来预览 Camera，获取NV21数据](https://rustfisher.github.io/2018/02/26/Android_note/Android-camera_nv21_surfaceview_textureview/)

[Android openGl开发详解(二)——通过SurfaceView，TextureView，GlSurfaceView显示相机预览（附Demo）](https://www.jianshu.com/p/db8ecba6037a)

Camera预览回调中默认使用NV21格式。

**使用SurfaceView预览Camera**

```java
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启预览
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

**使用TextureView预览Camera**
TextureView可用于显示内容流，内容流可以是视频或者OpenGL的场景。内容流可来自应用进程或者远程其他进程。

TextureView必须在硬件加速开启的窗口中使用，若是软解，TextureView不会显示。

不同于SurfaceView，TextureView不会建立一个单独的窗口，而是像一个常规的View一样，这使得TextureView可以被移动、转换或是添加动画。

|          | TextureView | SurfaceView  |
| -------- | ----------- | ------------ |
| 绘制       | 稍微延时        | 及时           |
| 内存       | 高           | 低            |
| 耗电       | 高           | 低            |
| 动画       | 支持          | 不支持          |
| 适用场景（推荐） | 视频播放，相机应用   | 大量画布更新（游戏绘制） |

```java
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            mCamera.setDisplayOrientation(90);
        } else {
            mCamera.setDisplayOrientation(0);
        }
        try {
            mCamera.setPreviewCallback(mCameraPreviewCallback);
            mCamera.setPreviewTexture(surface); //使用SurfaceTexture
            mCamera.startPreview();
        } catch (IOException ioe) {

        }
    }
```



### 4. 使用Android平台的MediaExtractor和MediaMuxer API 解析和封装mp4文件

[Android 使用 MediaExtractor 和 MediaMuxer 解析和封装 mp4 文件](https://rustfisher.github.io/2018/03/01/Android_note/Android-media_MediaMuxer_MediaExtractor_mp4/)

[https://www.jianshu.com/p/1ceef353ede0](https://www.jianshu.com/p/1ceef353ede0)

**MP4**
MP4或称MPEG-4第14部分，是一种标准的数字多媒体容器格式，MP4中的音频格式通常为AAC。

**MediaExtractor**
MediaExtractor可用于分离多媒体容器中视频track和音频track。

```
setDataSource()	//设置数据源，数据源可以是本地文件地址，也可以是网络地址
getTrackFormat(int index) //来获取各个track的MediaFormat，通过MediaFormat来获取track的详细信息，如MimeType、分辨率、采样频率、帧率等等
selectTrack(int index) //通过下标选择指定的通道
readSampleData(ByteBuffer buffer,int offset) //获取当前编码好的数据并存在指定好偏移量的buffer中
```

**MediaMuxer**
MediaMuxer可用于混合基本码流，将所有的信道的信息合成一个视频，目前输出格式支持MP4、Webm等。

**提取并输出MP4文件中的视频部分**
从一个MP4文件中提取出视频，得到不含音频的MP4文件。

实现流程，首先是使用 MediaExtractor 提取，然后使用 MediaMuxer 输出MP4文件。

* MediaExtractor 设置数据源，找到并选择视频轨道的格式和下标
* MediaMuxer 设置输出格式为 MUXER_OUTPUT_MPEG_4 ，添加前面选定的格式，调用 start() 启动
* MediaExtractor 读取帧数据，不停地将帧数据和相关信息传入 MediaMuxer
* 最后停止并释放 MediaMuxer 和 MediaExtractor，最后放在子线程中操作

**提取MP4文件中的音频部分，获取音频文件**



### 5. 学习Android平台OpenGl ES API，了解OpenGl开发的基本流程，使用OpenGl绘制一个三角形

[如何在Android APP中使用OpenGL ES](https://code.tutsplus.com/zh-hans/tutorials/how-to-use-opengl-es-in-android-apps--cms-28464)

现在几乎每个Android手机都有一个图形处理单元，或简称为GPU。顾名思义，就是专门处理与3D图形计算相关的硬件单元。目前有两种不同的API可用于与Android设备的GPU交互：Vulkan和OpenGL ES。

Vulkan仅适用于运行7.0或更高设备，而所有Android版本都支持OpenGl ES。

**GpenGL ES**

OpenGL是Open Graphics Library的缩写，它是一个独立于平台的API，可以创建硬件加速的3D图形，OpenGL ES是 OpenGL for Embedded Systems的缩写，是OpenGL API的一个子集。

OpenGL ES是一个非常低级的API，换句话说，它不提供任何让你快速创建或操纵3D对象的方法。相反，在使用它时，你需要手动管理任务，例如创建3D对象的各个顶点和面，计算各种3D变换，以及创建不同类型的着色器。

还值得一提的是，Android SDK和NDK可以让你在Java和C中编写与OpenGL ES相关的代码。

**项目设置**
OpenGL ES是Android框架的一部分，所以我们不必为项目添加任何依赖就能使用它们。

当然，有些设备不支持OpenGL ES，无法安装APP，所以声明使用：

```xml
<uses-feature android:glEsVersion="0x00020000" android:required="true" />
```

**创建画布**
Android框架为3D图形画布提供了两个控件：GLSurfaceView和TextureView。大多数开发人员喜欢使用GLSurfaceView，只有当他们打算在其他View控件上叠加3D图形时才会选择TextureView。

在指定GLSurfaceView宽高的时候通常设置相等，这样做很重要，因为OpenGL ES坐标系是一个正方形，如果必须使用矩形画布，请记住在计算投影矩阵时使用宽高比。

**创建3D对象**
虽然可以通过手动编写所有顶点的X、Y、Z坐标，但是非常麻烦，使用3D建模工具会容易些。

**创建缓冲区对象**
你不能将顶点和面的列表直接传递给OpenGL ES API中的方法。你必须先将其转换为缓冲对象。要存储顶点坐标数据，我们需要一个 FloatBuffer对象，对于仅由顶点索引组成的面部数据，ShortBuffer对象就足够了。

**创建着色器**
为了能够渲染我们的3D对象，我们必须为它创建一个顶点着色器和片段着色器。现在，可以将着色器看成一个非常简单的程序，用类似C语言的语言编写，称为OpenGL着色语言，简称GLSL。

顶点着色器负责处理3D对象的顶点，片段着色器（也称为像素着色器）负责着色3D对象的像素。

* 创建顶点着色器

  在项目的res/raw文件夹中创建一个名为vertex_shader.txt的新文件。

  顶点着色器必须有一个 attribute 全局变量，以便从Java代码接收顶点位置数据。此外，添加一个 uniform 全局变量以便于从 Java代码接收视图投影矩阵。

  在顶点着色器的 main() 方法内部，你必须设置 gl_position 值，它是 GLSL 内置变量，该变量决定顶点的最终位置。

  ```java
  attribute vec4 position;
  uniform mat4 matrix;
   
  void main() {
      gl_Position = matrix * position;
  }
  ```

* 创建片段着色器

  在项目的res/raw文件夹中创建一个名为fragment_shader.txt的新文件。

  ```java
  precision mediump float;
   
  void main() {
      gl_FragColor = vec4(1, 0.5, 0, 1.0);
  }
  ```

  创建一个简单的片段着色器，将橙色分配给所有像素。要将一个颜色分配给一个像素，在片段着色器的main()方法内部，使用 gl_FragColor 内置的变量。

* 编译着色器

  上面着色器代码是写在文件中的，我们要把它读取出来，转化为字符串。

  着色器的代码必须添加到 OpenGL ES着色器对象中，要创建一个新的着色器对象，请使用 GLES20类的 glCreateShader() 方法。根据要创建的着色器对象的类型，你可以传递 GL_VERTEX_SHADER  或者 GL_FRAGMENT_SHADER 给它。该方法返回一个整数，该正数作为着色器对象的引用。新创建的着色器对象不包含任何代码，要将着色器代码添加到着色器对象，必须使用 glShaderSource() 方法。

  ```java
  int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
  GLES20.glShaderSource(vertexShader, vertexShaderCode);
   
  int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
  GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
  ```

  现在可以将着色器对象传递给 glCompileShader() 方法来编译它们包含的代码。

  ```java
  GLES20.glCompileShader(vertexShader);
  GLES20.glCompileShader(fragmentShader);
  ```


