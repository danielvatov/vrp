package net.vatov.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class CommonLoggingAspect {

    private final static Logger logger = LoggerFactory.getLogger(CommonLoggingAspect.class);

    @Pointcut("execution(* net.vatov.algorithm.clarkewright.ClarkeWright.*(..))")
    protected void logging() {
    }

    @Around("logging()")
    public Object doTrace(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
        final StringBuilder joinPointName = new StringBuilder(thisJoinPoint.getThis().getClass().getSimpleName())
                .append(".").append(thisJoinPoint.getSignature().getName()).append("(");
        final StringBuilder justName = new StringBuilder(joinPointName);
        final Object[] args = thisJoinPoint.getArgs();
        if (null != args && args.length > 0) {
            for (int i = 0; i < args.length; ++i) {
                joinPointName.append(args[i]).append(", ");
            }
            joinPointName.deleteCharAt(joinPointName.length() - 1);
            joinPointName.deleteCharAt(joinPointName.length() - 1);
        }
        joinPointName.append(")");
        justName.append(")");

        long startTime = System.currentTimeMillis();
        Object ret = thisJoinPoint.proceed();
        logger.trace("executed {} in {} ms returns {}", new Object[] { joinPointName, System.currentTimeMillis() - startTime,
                ret });
        return ret;
    }
}
