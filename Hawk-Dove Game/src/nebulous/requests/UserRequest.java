package nebulous.requests;

public class UserRequest extends Request{

	private static final long serialVersionUID = 402L;

	public UserRequest() {
		super(RequestType.LOGIN);
	}

}
