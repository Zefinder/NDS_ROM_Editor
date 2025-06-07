package pokemon.manager;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class LogsManager implements Manager {

	private static final LogsPrintStream INFO_PRINT_STREAM = new LogsPrintStream(System.out, MessageNature.INFO);
	private static final LogsPrintStream WARNING_PRINT_STREAM = new LogsPrintStream(System.out, MessageNature.WARNING);
	private static final LogsPrintStream ERROR_PRINT_STREAM = new LogsPrintStream(System.err, MessageNature.ERROR);
	
	private static final LogsManager instance = new LogsManager();
	
	private LogsManager() {
	}
	
	public void logInfo(Object o) {
		INFO_PRINT_STREAM.println(o);
	}
	
	public void logWarning(Object o) {
		WARNING_PRINT_STREAM.println(o);
	}
	
	public void logError(Object o) {
		ERROR_PRINT_STREAM.println(o);
	}
	
	@Override
	public void initManager() {
		System.setOut(INFO_PRINT_STREAM);
		System.setErr(ERROR_PRINT_STREAM);
	}
	
	public static LogsManager getInstance() {
		return instance;
	}
	
	private enum MessageNature {
		INFO, WARNING, ERROR
	}
	
	private static class Message {
		
		private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
		
		private String messageContent;
		private MessageNature nature;
		private Date date;
		
		public Message(String messageContent, MessageNature nature) {
			this.messageContent = messageContent;
			this.nature = nature;
			this.date = Date.from(Instant.now());
		}
		
		@Override
		public String toString() {
			return "[%s %s]: %s".formatted(DATE_FORMAT.format(date), nature.name(), messageContent);
		}
	}
	
	private static class LogsPrintStream extends PrintStream {

		private MessageNature nature;
		
		public LogsPrintStream(OutputStream out, MessageNature nature) {
			super(out);
			this.nature = nature;
		}
		
		private void println(Message message) {
			super.println(message.toString());
		}
		
		@Override
		public void println(String x) {
			Message message = new Message(x, nature);
			println(message);
		}
		
		@Override
		public void println(Object x) {
			Message message = new Message(x.toString(), nature);
			println(message);
		}
	}

}
