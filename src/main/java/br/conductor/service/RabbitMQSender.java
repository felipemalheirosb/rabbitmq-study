package br.conductor.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;

import br.conductor.conector.RabbitMQService;
import br.conductor.config.RabbitMQConfig;
import br.conductor.modelo.Plugin;

@Service
public class RabbitMQSender {

	@Value("${sgr.rabbit.exchange}")
	private String exchange;

	@Value("${sgr.rabbit.routingkey}")
	private String routingKey;

	@Autowired
	private RabbitMQService service;

	@Autowired
	private RabbitMQConfig config;
	
	public void send() throws IOException, Exception {
		service.criarConexao("unifacisa-lab"); //virtualHost
		service.criarChannel(config);
		service.basicPublish(config);
	}
	
	public void sendAntigo(Plugin plugin) throws IOException, Exception {

		service.criarConexao("unifacisa-lab"); //virtualHost
		service.criarChannel(config);
		service.basicPublishAntigo(plugin, config);

	}
	
	public void consume() throws Exception, TimeoutException  {
		service.criarConexao("unifacisa-lab"); //virtualhost
		service.criarChannel(config);
		Channel c = service.getChannel();
		service.rabbitConsumer(c);
	}
}
