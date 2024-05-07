package com.itheima.controller;

import com.itheima.exception.BusinessException;
import com.itheima.exception.SystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionAdvice {

    @ExceptionHandler(SystemException.class)
    public Result doSystemException(SystemException ex){
        //记录日志
        //发送消息给运维
        //发送邮件给开发人员，ex对象发送给开发人员
        return new Result(ex.getCode(),ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public Result doBusinessException(SystemException ex){
        return new Result(ex.getCode(),ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result doException(Exception ex){
        System.out.println("发生异常");
        return new Result(Code.SYSTEM_UNKNOW_ERR,"发生异常");
    }
}
