package com.securitydam.sdcc;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Properties;

public class GetReportFromEmail {

	static String EmailUsername = "MYMAIL@gmail.com";
	static String PasswordUsername = "MYPASSWORD";

	public GetReportFromEmail() throws Exception {
		try {

		Properties props = new Properties();
		props.put("mail.imap.host", "imap.gmail.com");
		props.put("mail.imap.port", "993");
		props.put("mail.imap.starttls.enable", "true");
//			props.put("mail.smtp.starttls.required", "true");
//			props.put("mail.smtp.ssl.protocols", "TLSv1.2");
			props.put("mail.imap.ssl.trust", "imap.gmail.com");
		Session emailSession = Session.getDefaultInstance(props);

		// create the imap store object and connect to the imap server
		Store store = emailSession.getStore("imaps");

		store.connect("imap.gmail.com", EmailUsername, PasswordUsername);

		// create the inbox object and open it
		Folder inbox = store.getFolder("Inbox");
		inbox.open(Folder.READ_WRITE);

		// retrieve the messages from the folder in an array and print it
		Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
		System.out.println("messages.length---" + messages.length);

		for (int i = 0, n = messages.length; i < n; i++) {
			Message message = messages[i];
			message.setFlag(Flags.Flag.SEEN, true);
			System.out.println("---------------------------------");
			System.out.println("Email Number " + (i + 1));
			System.out.println("Subject: " + message.getSubject());
			System.out.println("From: " + message.getFrom()[0]);
			System.out.println("Text: " + message.getContent().toString());

		}

		inbox.close(false);
		store.close();
	}
	 catch(NoSuchProviderException e){
			e.printStackTrace();
		} catch(MessagingException e){
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}

	}


	public static void readMyEmail(boolean deleteItAfter) throws MessagingException, IOException {
		Folder folder = null;
		Store store = null;
		try {
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");

			Session session = Session.getDefaultInstance(props, null);
			// session.setDebug(true);
			store = session.getStore("imaps");
			store.connect("imap.gmail.com",EmailUsername, PasswordUsername);
			folder = store.getFolder("Inbox");
			/* Others GMail folders :
			 * [Gmail]/All Mail   This folder contains all of your Gmail messages.
			 * [Gmail]/Drafts     Your drafts.
			 * [Gmail]/Sent Mail  Messages you sent to other people.
			 * [Gmail]/Spam       Messages marked as spam.
			 * [Gmail]/Starred    Starred messages.
			 * [Gmail]/Trash      Messages deleted from Gmail.
			 */
			folder.open(Folder.READ_WRITE);
			Message messages[] = folder.getMessages();
			System.out.println("No of Messages : " + folder.getMessageCount());
			System.out.println("No of Unread Messages : " + folder.getUnreadMessageCount());
			for (int i=0; i < messages.length; ++i) {
				System.out.println("****************************************");
				System.out.println("MESSAGE #" + (i + 1) + ":");
				Message msg = messages[i];
        /*
          if we don''t want to fetch messages already processed
          if (!msg.isSet(Flags.Flag.SEEN)) {
             String from = "unknown";
             ...
          }
        */
				String from = "unknown";
				if (msg.getReplyTo().length >= 1) {
					from = msg.getReplyTo()[0].toString();
				}
				else if (msg.getFrom().length >= 1) {
					from = msg.getFrom()[0].toString();
				}
				String subject = msg.getSubject();
				System.out.println("from: " +  from);
				System.out.println("subject: " + subject );
				int MessageNumber=	msg.getMessageNumber();

				System.out.println(MessageNumber);
				//System.out.println(msg.getContentType());
				Object content=msg.getContent();
				String result = "";
				if (msg.isMimeType("text/plain")) {
					result = msg.getContent().toString();
				} else if (msg.isMimeType("multipart/*")) {
					MimeMultipart mimeMultipart = (MimeMultipart) msg.getContent();
					result = getTextFromMimeMultipart(mimeMultipart);

				}
				System.out.println(result);
				EmailContainURL(result);
				msg.setFlag(Flags.Flag.SEEN,true);
 if (deleteItAfter==true){
				msg.setFlag(FLAGS.Flag.DELETED, true);}
				// you may want to replace the spaces with "_"
				// the TEMP directory is used to store the files
			//	saveParts(msg.getContent(), filename);
				// to delete the message
				// msg.setFlag(Flags.Flag.DELETED, true);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (folder != null) { folder.close(true); }
			if (store != null) { store.close(); }
		}
	}

		private static String getTextFromMimeMultipart(
				MimeMultipart mimeMultipart) throws Exception{
			String result = "";
			int count = mimeMultipart.getCount();
			for (int i = 0; i < count; i++) {
				BodyPart bodyPart = mimeMultipart.getBodyPart(i);
				if (bodyPart.isMimeType("text/plain")) {
					result = result + "\n" + bodyPart.getContent();
					break; // without break same text appears twice in my tests
				} else if (bodyPart.isMimeType("text/html")) {
					String html = (String) bodyPart.getContent();
					result = result + "\n" + html;
				} else if (bodyPart.getContent() instanceof MimeMultipart){
					result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
				}
			}
			return result;
		}

	public static void EmailContainURL(String result) {
		String URL_REGEX = "\\b((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?\\b";
		Pattern p = Pattern.compile(URL_REGEX);
		Matcher m = p.matcher(result);
		if (m.find()) {
			System.out.println("The Email contain an URL");
		}
		else{
			System.out.println("NO URL was found");

		}

	}


}
