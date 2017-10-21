package ec.com.yacare.y4all.lib.stun;

public class UnknownMessageAttributeException extends MessageAttributeParsingException {
	private static final long serialVersionUID = 5375193544145543299L;
	
	private MessageAttributeInterface.MessageAttributeType type;
	
	public UnknownMessageAttributeException(String mesg, MessageAttributeInterface.MessageAttributeType type) {
		super(mesg);
		this.type = type;
	}
	
	public MessageAttributeInterface.MessageAttributeType getType() {
		return type;
	}
}
