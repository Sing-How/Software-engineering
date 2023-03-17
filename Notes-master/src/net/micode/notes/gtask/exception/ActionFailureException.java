//read by 曾梦媛
//处理小米便签运行过程中的运行异常
package net.micode.notes.gtask.exception;

public class ActionFailureException extends RuntimeException {
    //serialVersionUID相当于java类的身份证。主要用于版本控制
    //serialVersionUID作用是序列化时保持版本的兼容性，即在版本升级时反序列化仍保持对象的唯一性
    private static final long serialVersionUID = 4425249765923293627L;

    public ActionFailureException() {
        super();
    }
    //在JAVA类中使用super来引用父类的成分，用this来引用当前对象
    //用super来引用父类对象
    //即此处super()以及super (paramString)可认为是RuntimeException ()和RuntimeException (paramString)
    public ActionFailureException(String paramString) {
        super(paramString);
    }

    public ActionFailureException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
    }
}
