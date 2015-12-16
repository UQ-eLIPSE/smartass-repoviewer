package au.edu.uq.smartassrepoeditor.templates.texparser;

import java.util.Map;

public class RCall extends ResultNode {
	Map<String, String> result;
	String error_str = "";

	public RCall(SimpleNode master_copy, Map<String, String> result) {
		super(master_copy);
		this.result = result;
	}

	public RCall(SimpleNode master_copy, String error) {
		super(master_copy);
		error_str = error;
	}

	@Override
	public String getSection(String section) {
		if(result==null)
			return error_str;
		String s = result.get(section);
		if(s==null)
			return "";
		else
			return s;
	}

}
