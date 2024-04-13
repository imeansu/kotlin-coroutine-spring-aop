package com.example.coroutineaop.aop

import com.example.coroutineaop.cache.Cache
import com.example.coroutineaop.cache.CoroutineCache
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.context.expression.MethodBasedEvaluationContext
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.core.KotlinDetector
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Method

@Aspect
@Component
class CacheAspect(
    private val coroutineCache: CoroutineCache<Any>,
    private val cache: Cache<Any>
) {
    private val expressionParser = SpelExpressionParser()
    private val nameDiscoverer = DefaultParameterNameDiscoverer()

    @Pointcut("args(.., kotlin.coroutines.Continuation)")
    fun suspendFunctionPointCut() {}

    // for both spring 6.1.0+ and earlier
    @Around("@annotation(com.example.coroutineaop.aop.CoroutineCacheable) && suspendFunctionPointCut()")
    fun coroutineCacheable(joinPoint: ProceedingJoinPoint): Any? {
        val coroutineCacheable = (joinPoint.signature as MethodSignature).method.getAnnotation(CoroutineCacheable::class.java)
        val prefix = coroutineCacheable.prefix
        val expire = coroutineCacheable.ttlSecond

        val keyValue = getKeyValue(joinPoint, coroutineCacheable.keyExpression)
        val cacheKey = "$prefix:$keyValue"

        return joinPoint.runCoroutine {
            coroutineCache.get(cacheKey)?.let { cached ->
                return@runCoroutine cached
            }

            joinPoint.proceedCoroutine().let { rtn ->
                if (rtn is Mono<*>) {
                    // for spring 6.1.0 and later
                    rtn.awaitSingleOrNull()?.let { result ->
                        coroutineCache.put(cacheKey, result)
                        return@runCoroutine result
                    }
                } else {
                    rtn?.let {
                        coroutineCache.put(cacheKey, rtn)
                        return@runCoroutine rtn
                    }
                }
            }
        }
    }

    // only for spring 6.1.0 and later
    @Around("@annotation(com.example.coroutineaop.aop.Cacheable)")
    fun cacheable(joinPoint: ProceedingJoinPoint): Any? {
        val method = (joinPoint.signature as MethodSignature).method
        val cacheable = method.getAnnotation(Cacheable::class.java)
        val prefix = cacheable.prefix
        val expire = cacheable.ttlSecond

        val keyValue = getKeyValue(joinPoint, cacheable.keyExpression)
        val cacheKey = "$prefix:$keyValue"

        cache.get(cacheKey)?.let { cached ->
            return cached
        }

        return joinPoint.proceed().let { rtn ->
            if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isSuspendingFunction(method)) {
                return@let (rtn as Mono<*>).map { result ->
                    cache.put(cacheKey, result)
                    result
                }
            }

            rtn?.let {
                cache.put(cacheKey, rtn)
                rtn
            }
        }
    }

    fun getKeyValue(joinPoint: ProceedingJoinPoint, keyExpression: String): Any? {
        val theMethod = (joinPoint.signature as MethodSignature).method
        val expression = expressionParser.parseExpression(keyExpression)

        val rootObject = SpelRootObject(theMethod, joinPoint.args)
        val context = MethodBasedEvaluationContext(rootObject, theMethod, joinPoint.args, nameDiscoverer)

        return expression.getValue(context)
    }
}

class SpelRootObject(
    private val method: Method,
    private val args: Array<Any?>
)