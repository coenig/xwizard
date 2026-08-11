package eas.miscellaneous.useful.autoComplete.matcher;

import eas.miscellaneous.useful.autoComplete.SuggestMatcher;

public class ContainsMatcher implements SuggestMatcher {
    private static final long serialVersionUID = 8930467465726529694L;

    @Override
	public boolean matches(String dataWord, String searchWord) {
		return dataWord.contains(searchWord);
	}
}