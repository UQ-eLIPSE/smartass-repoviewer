package au.edu.uq.smartassrepoeditor.templates.texparser;

public class UnparsedTexNode extends SimpleNode {
	String code;

	public UnparsedTexNode(String code) {
		super(0);
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return code;
	}

}
