package com.utfpr.edu.br.pw45s.notifications.exception;

public class EmailSendingException extends RuntimeException {
	private final String toEmail;
	private final String subject;

	public EmailSendingException(String toEmail, String subject, Throwable cause) {
		super("Failed to send email to '%s' with subject '%s'".formatted(toEmail, subject), cause);
		this.toEmail = toEmail;
		this.subject = subject;
	}

	public String getToEmail() {
		return toEmail;
	}

	public String getSubject() {
		return subject;
	}
}
