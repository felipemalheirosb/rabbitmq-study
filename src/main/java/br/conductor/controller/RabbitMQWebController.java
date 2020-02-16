package br.conductor.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.conductor.service.RabbitMQSender;

@RestController
@RequestMapping(value = "/springboot-rabbitmq")
public class RabbitMQWebController {

	@Autowired
	private RabbitMQSender rabbitMQSender; 


//	@GetMapping(value = "/producer")
//	public String producer(@RequestParam("nome") String nome, @RequestParam("description") String description) throws IOException, Exception {
//
//		Plugin plugin = new Plugin();
//		//plugin.setNome(nome);
//		plugin.setDescription(description);
//
//		rabbitMQSender.sendAntigo(plugin);
//
//		return "Mensagem enviada para o RabbitMQ do Malheiros com sucesso";
//	}
//	
//	@GetMapping(value = "/producer")
//	public String producer() throws IOException, Exception {
//
//		rabbitMQSender.send();
//
//		return "Mensagem enviada para o RabbitMQ do Malheiros com sucesso";
//	}
//	
	@GetMapping(value = "/producer")
	public String producer() throws IOException, Exception {
		
		rabbitMQSender.consume();

		return "Mensagem consumida do RabbitMQ do Malheiros com sucesso";
	}
}
