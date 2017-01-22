package com.zhangyu.SpringBootAndRabbit.controller;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.zhangyu.SpringBootAndRabbit.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Description:接收消息.
 * Created by zhangyu on 2017/1/22.
 */
@RestController
@EnableAutoConfiguration
public class RecvController implements RabbitTemplate.ConfirmCallback{

    private RabbitTemplate rabbitTemplate;
    /**
     * 配置发送消息的rabbitTemplate，因为是构造方法，所以不用注解Spring也会自动注入（应该是新版本的特性）
     * @param rabbitTemplate
     */
    public RecvController(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
        //设置消费回调
        this.rabbitTemplate.setConfirmCallback(this);
    }

    @RequestMapping("receive")
    public void receiveMsg() throws IOException, InterruptedException, TimeoutException {

//        QueueingConsumer queueingConsumer = new QueueingConsumer(App.context.getBean(ConnectionFactory.class).);
        ConnectionFactory facotry = new ConnectionFactory();
        facotry.setUsername("guest");
        facotry.setPassword("guest");
        facotry.setVirtualHost("/");
        facotry.setHost("127.0.0.1");

        Connection connection = facotry.newConnection();
        Channel channel = connection.createChannel();

        channel.basicQos(1);

        channel.exchangeDeclare("my-mq-exchange","topic",true);

//        String queueName = channel.queueDeclare("queue_one",true,true,true,null).getQueue();

        String queueName = channel.queueDeclare().getQueue();

        String routingKey = "queue_one_key1";

        channel.queueBind(queueName,"my-mq-exchange",routingKey);

        QueueingConsumer consumer = new QueueingConsumer(channel);

        channel.basicConsume(queueName,false,consumer);

        //获取消息
        System.out.print("@@@@@@@@@@@@@等待输出消息");

        while(true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String msg = new String(delivery.getBody());
            String key = delivery.getEnvelope().getRoutingKey();

            System.out.println("  Received '" + key + "':'" + msg + "'");
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false); //确定该消息已成功消费


        }



//        rabbitTemplate.receive("queue_one1");

//        System.out.print(rabbitTemplate.getExchange());
    }

    /**
     * 消息的回调，主要是实现RabbitTemplate.ConfirmCallback接口
     * 注意，消息回调只能代表成功消息发送到RabbitMQ服务器，不能代表消息被成功处理和接受
     */
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println(" 回调id:" + correlationData);
        if (ack) {
            System.out.println("消息成功消费");
        } else {
            System.out.println("消息消费失败:" + cause+"\n重新发送");

        }
    }
}
