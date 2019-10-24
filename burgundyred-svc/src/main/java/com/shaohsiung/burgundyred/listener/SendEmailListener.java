package com.shaohsiung.burgundyred.listener;

import com.shaohsiung.burgundyred.document.ProductDocument;
import com.shaohsiung.burgundyred.model.User;
import com.shaohsiung.burgundyred.repository.ProductDocumentRepository;
import com.shaohsiung.burgundyred.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import  com.shaohsiung.burgundyred.converter.ObjectBytesConverter;

@Slf4j
@Component
public class SendEmailListener {
    private String ACTIVATE_USER_PATH = "http://localhost:8080/user/activate/%s/%s";

    private String USER_ACTIVATE_PREFIX = "user_activate_key_";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JavaMailSender javaMailSender;

    @RabbitListener(queues = "topic.email")
    public void sendEmail(byte[] bytes) throws Exception{
        User user = (User) ObjectBytesConverter.getObjectFromBytes(bytes);
        SimpleMailMessage mainMessage = new SimpleMailMessage();
        mainMessage.setFrom("shaohsiung@126.com");
        mainMessage.setTo(user.getEmail());
        mainMessage.setSubject("账户激活-勃艮第红");
        //发送的内容
        String uuid_token = AppUtils.UUID();
        redisTemplate.opsForValue().set(USER_ACTIVATE_PREFIX + user.getId(), uuid_token);
        String message= "请点击链接来激活您的账户：" + String.format(ACTIVATE_USER_PATH, user.getId(), uuid_token);
        mainMessage.setText(message);
        javaMailSender.send(mainMessage);
        log.info(message);
        log.info("发送用户激活邮件成功！用户id：{}", user.getId());
    }
}
