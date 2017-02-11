package nebulous.requests;

import java.io.Serializable;

public abstract class Request implements Serializable{
	
	private static final long serialVersionUID = 400L;

	public enum RequestType { LOGIN, USER }
	
	private RequestType request = null;
	
	public Request(RequestType type) {
		this.request = type;
	}

	public RequestType getRequestType() {
		return request;
	}

}
