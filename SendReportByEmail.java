import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class SendReportByEmail {

	String EmailUsername = "EmailUsername@gmail.com";
	String PasswordUsername = "PasswordUsername";
	String sentTo = "sentTo@Gmail.com";
	static String FinalReport = "";

	public SendReportByEmail(String ReportDirectory) throws Exception {
		if (new File(ReportDirectory).exists()) {

			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
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
				message.setSubject("The Qa test report with tag");

				/////// ADD ATTACHMENT
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText("Here's the file");
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);

				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(ReportDirectory);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(ReportDirectory);
				multipart.addBodyPart(messageBodyPart);
				message.setContent(multipart);

				Transport.send(message);

				System.out.println("Message Sent!");

			} catch (MessagingException mex) {
				mex.printStackTrace();
			}
		} else
			System.out.println("    The Report didnt Created or found in the " + ReportDirectory);
	}

	public String CurrentDateTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

}
