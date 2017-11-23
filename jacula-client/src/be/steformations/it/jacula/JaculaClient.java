package be.steformations.it.jacula;

import javax.jms.*;

import be.steformations.it.jacula.dao.work.JmsMessage;
import be.steformations.it.jacula.dto.service.ClientAndStorageService;



public class JaculaClient {
	
	private static String jndi =
			"java:global/jacula-ear/jacula-ejb-soap-jms/EjbClientAndStorageServer";
	private static ClientAndStorageService service;
	
	public static void main(String [] args) throws Exception {

			java.util.Properties properties = new java.util.Properties();
			properties.put("java.naming.provider.url", "localhost:3700");
			javax.naming.InitialContext cxt = new javax.naming.InitialContext();
			javax.jms.ConnectionFactory factory = (javax.jms.ConnectionFactory) cxt.lookup("jms/JaculaTopicConnectionFactory");
			javax.jms.Topic topic = (Topic) cxt.lookup("jms/JaculaTopic");			
			javax.jms.Connection connection = factory.createConnection();
			javax.jms.Session session = connection.createSession();
			javax.jms.MessageConsumer consumer = session.createConsumer(topic);
			
			connection.start();
			while(true){
				System.out.println("JaculaClient.JaculaJmsConsumer en attente");
				javax.jms.Message message = consumer.receive();
				javax.jms.ObjectMessage objectMessage = (ObjectMessage) message;
				java.io.Serializable serial = objectMessage.getObject();
				JmsMessage jms = (JmsMessage) serial;
				System.out.println("JaculaClient.JaculaJmsConsumer a reçu " + jms.getClientId() + " et " + jms.getJobId());
				int id = Integer.parseInt(args[0]);
				System.out.println("JaculaClient.main() => id client = " + id);
				service = (ClientAndStorageService)cxt.lookup(jndi);
				if (id == jms.getClientId()){
					service.getJob(jms.getJobId());
				}
			}
		}
	
}
