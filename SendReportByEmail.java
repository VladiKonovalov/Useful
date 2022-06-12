import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;


public class SendReportByEmail {

	String EmailUsername = "EmailUsername@gmail.com";
	String PasswordUsername = "PasswordUsername";
	String sentTo = "sentTo@Gmail.com";
	static String FinalReport = "";

	public SendReportByEmail(String sentTo, String ReportDirectory, String ReportDirectory2) throws Exception {

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		props.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(EmailUsername, PasswordUsername);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(EmailUsername));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sentTo));
			message.setSubject("The DB Compare report");
			// message.setFileName("Report.html");

			/////// ADD ATTACHMENT
			BodyPart messageBodyPart = new MimeBodyPart();
			BodyPart messageBodyPart2 = new MimeBodyPart();

			messageBodyPart.setText("Here's the file");
//			message.setText(FinalReport);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();
			message.setContent(addAttachment(messageBodyPart, multipart, ReportDirectory));
			message.setContent(addAttachment(messageBodyPart2, multipart, ReportDirectory2));

			Transport.send(message);

			System.out.println("Message Sent!");

		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	private static Multipart addAttachment(BodyPart messageBodyPart, Multipart multipart, String filename)
			throws Exception {
		if (new File(filename).exists()) {
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(Paths.get(filename).getFileName().toString());
			multipart.addBodyPart(messageBodyPart);
		}
		return multipart;

	}

	public String CurrentDateTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

}
