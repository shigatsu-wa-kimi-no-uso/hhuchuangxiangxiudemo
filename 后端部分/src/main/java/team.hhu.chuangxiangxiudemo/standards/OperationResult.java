package team.hhu.chuangxiangxiudemo.standards;


/**
 *服务层函数统一返回值规范
 * 必须返回操作状态
 * 返回值为null时，泛型T使用Void
 */

public class OperationResult<T>
{
    private ResultConstants status;

    private T returnValue;

    public ResultConstants getStatus()
    {
        return status;
    }

    public void setStatus(ResultConstants status)
    {
        this.status = status;
    }

    public T getReturnValue()
    {
        return returnValue;
    }

    public void setReturnValue(T returnValue)
    {
        this.returnValue = returnValue;
    }
}
