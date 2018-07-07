package top.omooo.audiovideotask.task_5;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * OpenGl 三角形类
 */
public class MyTriangle {
    private FloatBuffer vertexBuffer;
    private static final String vertexShaderCode =
            "attribute vec4 vPosition;" + "void main(){ "
                    + " gl_Position = vPosition;"
                    + "}";
    //所有的浮点值都是中等精度（precision mediump float;）
    //可以选择把这个值设为“低”( precision lowp float; )或者“高”( precision highp float; )
    private static final String fragmentShaderCode =
            "precision mediump float;"
                    + "uniform vec4 vColor;"
                    + "void main(){"
                    + " gl_FragColor = vColor;"
                    + "}";
    //每个点由三个数值定义
    private static final int COORDS_PER_VERTEX = 3;
    private static float triangleCoords[] = {
            0.0f, 0.62f, 0.0f,
            -0.5f, -0.3f, 0.0f,
            0.5f, -0.3f, 0.0f
    };
    //设置red，green，blue 和 alpha颜色值
    private float color[] = {0.6367f, 0.7695f, 0.2227f, 1.0f};
    private final int mProgram;

    public MyTriangle() {
        //初始化顶点ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        //使用硬件指定的字节顺序 一般而言是ByteOrder.LITTLE_ENDIAN
        byteBuffer.order(ByteOrder.nativeOrder());
        //从ByteBuffer中创建FloatBuffer
        vertexBuffer = byteBuffer.asFloatBuffer();
        //把预置的坐标值填入FloatBuffer
        vertexBuffer.put(triangleCoords);
        //设置从第一个坐标开始
        vertexBuffer.position(0);

        int vertexShader = MyGlRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGlRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //创建空的OpenGL ES Program
        mProgram = GLES20.glCreateProgram();
        //ES Program加入顶点着色器
        GLES20.glAttachShader(mProgram, vertexShader);
        //ES Program加入片段着色器
        GLES20.glAttachShader(mProgram, fragmentShader);
        //创建可执行的OpenGL ES程序
        GLES20.glLinkProgram(mProgram);

    }

    private int mPositionHandle;
    private int mColorHandle;

    private static final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private static final int vertexStride = COORDS_PER_VERTEX * 4;  //每个顶点四个字节

    public void draw() {
        //将程序添加到OpenGL ES环境中
        GLES20.glUseProgram(mProgram);

        //获取顶点着色器的vPosition成员位置

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //激活这个三角形的handle
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        //准备这个三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false
                , vertexStride, vertexBuffer);
        //获取片段着色器的颜色成员信息
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
}
