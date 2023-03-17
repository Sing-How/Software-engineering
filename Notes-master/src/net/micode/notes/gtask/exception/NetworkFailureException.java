//read by 曾梦媛
//处理小米便签运行过程中的网络异常

package net.micode.notes.gtask.exception;

public class NetworkFailureException extends Exception {
    //serialVersionUID相当于java类的身份证。主要用于版本控制
    //serialVersionUID作用是序列化时保持版本的兼容性，即在版本升级时反序列化仍保持对象的唯一性
    private static final long serialVersionUID = 2107610287180234136L;

    public NetworkFailureException() {
        super();
    }
    //在JAVA类中使用super来引用父类的成分，用this来引用当前对象
    //用super来引用父类对象
    //即此处super()以及super (paramString)可认为是Exception ()和Exception (paramString)
    public NetworkFailureException(String paramString) {
        super(paramString);
    }

    public NetworkFailureException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
    }
}
