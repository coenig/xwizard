package eas.miscellaneous.useful.autoComplete;

import java.io.Serializable;

public interface SuggestMatcher extends Serializable {
	public boolean matches(String dataWord, String searchWord);
}