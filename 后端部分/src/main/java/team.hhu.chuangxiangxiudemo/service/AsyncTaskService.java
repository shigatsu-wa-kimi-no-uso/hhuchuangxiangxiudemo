package team.hhu.chuangxiangxiudemo.service;

import lombok.extern.log4j.Log4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.utility.LogUtil;

import java.util.concurrent.*;

/**
 * 异步任务触发服务
 * @author XuJinchen
 */

@Log4j
@Service
@Component
public class AsyncTaskService
{

    @Async("localAsyncExecutor")
    public <V> Future<OperationResult<V>> invoke(@NotNull Callable<OperationResult<V>> method)
    {
        OperationResult<V> result;
        long start = System.currentTimeMillis();
        long end;
        log.info(Thread.currentThread().getName() + ":" + method + " 线程启动成功");
        try
        {
            result = method.call();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result=null;
        }
        end = System.currentTimeMillis();
        log.info("线程用时:" + (end - start) + "ms");
        return new AsyncResult<>(result);
    }



    public <V>  OperationResult<V> getResult(@NotNull Future<OperationResult<V>> future, long timeout, TimeUnit timeUnit)
    {
        OperationResult<V> result=new OperationResult<>();
        try
        {
            log.info("请求获取结果");
            OperationResult<V> res=future.get(timeout, timeUnit);
            if(res != null)
            {
                result.setReturnValue(res.getReturnValue());
                result.setStatus(res.getStatus());
            }
            else
            {
                result.setStatus(ResultConstants.OPERATION_UNDEFINED_ERROR);
            }
        }
        catch (TimeoutException e)
        {
            e.printStackTrace();
            result.setStatus(ResultConstants.OPERATION_TIMEDOUT);
            return result;
        }
        catch ( InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            result.setStatus(ResultConstants.OPERATION_UNDEFINED_ERROR);
            return result;
        }
        return result;
    }


}

