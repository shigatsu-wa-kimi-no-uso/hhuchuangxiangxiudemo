package team.hhu.chuangxiangxiudemo.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import team.hhu.chuangxiangxiudemo.service.AsyncTaskService;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;

import java.util.concurrent.Callable;

/**
 * DataAccessException捕捉器
 * @author XuJinchen
 */

@Slf4j
public final class DataAccessExceptionHandler
{
    public static <T> void invoke(OperationResult<T> result, Callable<T> method,boolean enabledRollback)
    {
        try
        {
            result.setReturnValue(method.call());
            result.setStatus(ResultConstants.OPERATION_OK);
        }
        catch (Exception e)
        {
            LogUtil.error(e.getMessage());
            result.setStatus(ResultConstants.DATA_ACCESS_ERROR);
            if(enabledRollback)
            {
                log.info(Thread.currentThread().getName()+" 有一回滚操作被执行");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
    }

    @Deprecated
    public static <T> void invokeAsync(OperationResult<T> result, Callable<T> method, boolean enabledRollback,
                                       AsyncTaskService threadMessage)
    {
        try
        {
            result.setReturnValue(method.call());
            result.setStatus(ResultConstants.OPERATION_OK);
        }
        catch (Exception e)
        {
            LogUtil.error(e.getMessage());
            result.setStatus(ResultConstants.DATA_ACCESS_ERROR);
            if(enabledRollback)
            {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
    }

    private DataAccessExceptionHandler(){}
}