package com.securitydam.sdcc;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Properties;

public class GetReportFromEmail {

	static String EmailUsername = "MYMAIL@gmail.com";
	static String PasswordUsername = "MYPASSWORD";
	static String FinalReport = "";

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


	public static void doit() throws MessagingException, IOException {
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
				System.out.println("Saving ... " + subject +" " + from);
				// you may want to replace the spaces with "_"
				// the TEMP directory is used to store the files
				String filename = "c:/temp/" +  subject;
			//	saveParts(msg.getContent(), filename);
				msg.setFlag(Flags.Flag.SEEN,true);
				// to delete the message
				// msg.setFlag(Flags.Flag.DELETED, true);
			}
		}
		finally {
			if (folder != null) { folder.close(true); }
			if (store != null) { store.close(); }
		}
	}


}
