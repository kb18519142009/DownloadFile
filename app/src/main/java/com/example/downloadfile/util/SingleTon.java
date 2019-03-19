package com.example.downloadfile.util;

import android.util.Log;

/**
 * Created by kangbaibai on 2019/3/19.
 * 测试静态内部类的初始化时机，调用类的非常量静态变量、类的静态方法会触发类的其它静态变量的初始化
 */

/**
 * 我们知道一个类(class)要被使用必须经过装载，连接，初始化这样的过程
 * <p>
 * 连接阶段又可以分为三个子步骤：验证、准备和解析。
 * 验证就是要确保java类型数据格式 的正确性，并适于JVM使用。
 * 准备阶段，JVM为静态变量分配内存空间，并设置默认值，注意，这里是设置默认值，比如说int型的变量会被赋予默认值0 。在这个阶段，JVM可能还会为一些数据结构分配内存，目的 是提高运行程序的性能，比如说方法表。
 * 解析过程就是在类型的常量池中寻找类、接口、字段和方法的符号引用，把这些符号引用替换成直接引用。这个阶段可以被推迟到初始化之后，当程序运行的过程中真正使用某个符号引用的时候 再去解析它。
 * <p>
 * 类会在首次被“主动使用”时执行初始化，为类（静态）变量赋予正确的初始值。在Java代码中，一个正确的初始值是通过类变量初始化语句或者静态初始化块给出的。而我们这里所说的主动使用 包括：
 * 1. 创建类的实例
 * 2. 调用类的静态方法
 * 3. 使用类的非常量静态字段
 * 4. 调用Java API中的某些反射方法
 * 5. 初始化某个类的子类
 * 6. 含有main()方法的类启动时
 * <p>
 * 初始化一个类包括两个步骤：
 * 1、 如果类存在直接父类的话，且直接父类还没有被初始化，则先初始化其直接父类
 * 2、 如果类存在一个初始化方法，就执行此方法
 */

public class SingleTon {
    public static String TAG = "SingleTon";
    private static final String LOG = "log";
//    public static final SingleTon INSTANCE = SingleTonHoler.INSTANCE;

    static {
        Log.d(TAG, "instance initializer: 我是静态代码块" + LOG);
    }

    {
        Log.d(TAG, "instance initializer: 我是普通代码块" + LOG);
    }

    private SingleTon() {
        Log.d(TAG, "SingleTon: 单例初始化");
    }

    public static void get() {
    }

    private static class SingleTonHoler {
        private static final String SINGLETONHOLER_TAG = "SingleTonHoler";
        private static String SINGLETONHOLER_LOG = "singletonholer_log";

        static {
            Log.d(SINGLETONHOLER_TAG, "static initializer: " + SINGLETONHOLER_LOG);
        }

        {
            Log.d(SINGLETONHOLER_TAG, "instance initializer: 我是普通代码块" + SINGLETONHOLER_LOG);
        }

        private static SingleTon INSTANCE = new SingleTon();
    }

    public static SingleTon getInstance() {
        return SingleTonHoler.INSTANCE;
    }
}


