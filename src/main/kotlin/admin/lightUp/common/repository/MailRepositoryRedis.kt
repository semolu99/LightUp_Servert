package admin.lightUp.common.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class MailRepositoryRedis(
    private val mailRedisTemplate: RedisTemplate<String, String>,
    private val checkedRedisTemplate: RedisTemplate<String, String>
) {
    companion object {
        private const val KEY_PREFIX = "mail"
    }
    private val timeOutMail: Long = 1000 * 60 * 10
    private val timeOutMailChecked: Long = 1000 * 60 * 30

    fun saveMail(email: String, mailCode: String) {
        mailRedisTemplate.opsForValue().set(email, mailCode, timeOutMail, TimeUnit.MILLISECONDS)
    }

    fun saveChecked(email: String){
        val checked = "Checked"
        checkedRedisTemplate.opsForValue().set(email, checked, timeOutMailChecked,TimeUnit.MILLISECONDS)
    }

    fun findByMailCode(email: String): String? {
        val key = mailRedisTemplate.keys(email).firstOrNull()
        return key?.let { mailRedisTemplate.opsForValue().get(it) }
    }

    fun findByMailChecked(loginId: String):String?{
        val key = mailRedisTemplate.keys(loginId).firstOrNull()
        return key?.let { checkedRedisTemplate.opsForValue().get(it) }
    }

    fun deleteMailByEmail(loginId: String){
        val key = mailRedisTemplate.keys(loginId)
        mailRedisTemplate.delete(key)
        saveChecked(loginId)
    }

    fun deleteCheckByEmail(loginId: String){
        val key = mailRedisTemplate.keys(loginId)
        checkedRedisTemplate.delete(key)
    }

}