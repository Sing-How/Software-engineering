//  Read By 孙明宇
//  这个文件主要是支持软件运行过程中的运行异常处理

package net.micode.notes.gtask.exception;

public class NetworkFailureException extends Exception {
    //  serialVersionUID用于序列化时保持版本的兼容性，即在版本升级时反序列化仍保持对象的唯一性
    private static final long serialVersionUID = 2107610287180234136L;

    public NetworkFailureException() {
        super();
    }

    //  此处super()以及super (paramString)可认为是Exception ()和Exception (paramString)
    public NetworkFailureException(String paramString) {
        super(paramString);
    }

    public NetworkFailureException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
    }
}
