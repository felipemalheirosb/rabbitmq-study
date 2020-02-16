package br.conductor.conector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import br.conductor.config.RabbitMQConfig;
import br.conductor.modelo.Plugin;
import lombok.Getter;

@Service
public class RabbitMQService {

	// atributos para conexão do rabbit na 161
	@Value("${spring.rabbitmq.host}")
	private String host;

	@Value("${spring.rabbitmq.port}")
	private int port;

	@Value("${spring.rabbitmq.username}")
	private String username;

	@Value("${spring.rabbitmq.password}")
	private String password;

	@Getter
	private Connection connection;

	@Getter
	private Channel channel;
	

	// criando conexão com o RabbitMQ
	public void criarConexao(String virtualhost) throws IOException, TimeoutException {

		ConnectionFactory factory;

		if (connection == null) {
			factory = new ConnectionFactory();

			factory.setUsername(this.username);
			factory.setPassword(this.password);
			factory.setHost(this.host);
			factory.setPort(this.port);
			factory.setVirtualHost(virtualhost);

			factory.setAutomaticRecoveryEnabled(true);

			connection = factory.newConnection();
			// Conexão foi criada
		}
	}
	
	// criando channel para envio de mensagem
	public void criarChannel(RabbitMQConfig config) throws IOException {

		channel = connection.createChannel();

		Map<String, Object> args = new HashMap<>();
		args.put("x-delayed-type", "direct");
		
		channel.exchangeDeclare(config.getExchange(), "x-delayed-message", true, false, args);
		channel.queueDeclare(config.getQueueName(), true, false, false, null);
		channel.queueBind(config.getQueueName(), config.getExchange(), "lab.routingkey");
	}

	// criar o publisher para disponibilizar a messagem(objeto) para ser consumida
	// pelo consumer do SGRcore

	public void basicPublishAntigo(Plugin plugin, RabbitMQConfig config) throws UnsupportedEncodingException, IOException {

		channel.basicPublish(config.getExchange(), "lab.routingkey", null, plugin.toString().getBytes("UTF-8"));
	}

	public void basicPublish(RabbitMQConfig config) throws Exception {

		String mensagem = null;
		
		//Gson gson = new Gson();

		BufferedReader br = new BufferedReader(new FileReader("C:\\plugins\\springboot-rabbitmq\\src\\main\\resources\\plugins.json"));

		// Converte  JSON para objeto Java
		//Plugin obj = gson.fromJson(br, Plugin.class);
		
		final Gson gson = new GsonBuilder().setFieldNamingStrategy(strategy).create();
		
		Type listType = new TypeToken<ArrayList<Plugin>>(){}.getType();
		List<Plugin>list = new Gson().fromJson(br, listType);

		mensagem = gson.toJson(list);

		channel.basicPublish(config.getExchange(), "lab.routingkey", null, mensagem.toString().getBytes("UTF-8"));

	}
	
	// criar o consumer para disponibilizar a messagem(objeto) para ser consumida
	public void rabbitConsumer(Channel c) throws IOException, TimeoutException {

		Consumer consumer = new DefaultConsumer(c) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String mensagem = new String(body, "UTF-8");
				System.out.println("Mensagem recebida: " + mensagem);
			}
		};
		
		c.basicConsume("lab.queue", true, consumer); 
	}
	
	//tratamento do JSON
	final FieldNamingStrategy strategy = new FieldNamingStrategy() {
	    public String translateName(final Field f) {
	        final String fieldName = f.getName();
	        if (fieldName.equalsIgnoreCase("releases")) {
	            return "releases";
	        }

	        final Class<?> declaringClass = f.getDeclaringClass();
	        final String className = declaringClass.getSimpleName().toLowerCase();

	        return className + "_" + fieldName.toLowerCase();
	    }
	};

}