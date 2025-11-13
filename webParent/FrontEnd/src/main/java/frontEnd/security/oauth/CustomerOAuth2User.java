package frontEnd.security.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomerOAuth2User implements OAuth2User {
	private final String clientName;
	private String fullName;
	private final OAuth2User oauth2User;

	public String picture() {
		Object pictureObj = oauth2User.getAttribute("picture");

		if (pictureObj instanceof Map<?, ?> map) {
			Object dataObj = map.get("data");
			if (dataObj instanceof Map<?, ?> dataMap) {
				Object url = dataMap.get("url");
				if (url instanceof String) {
					return (String) url;
				}
			}
		}
		else if (pictureObj instanceof  String){
			return (String) pictureObj;
		}
		return null;
	}

	public CustomerOAuth2User(OAuth2User user, String clientName  ) {
		this.oauth2User = user;
		this.clientName = clientName;

	}

	public OAuth2User getOauth2User() {
		return oauth2User;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return oauth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return oauth2User.getAuthorities();
	}

	@Override
	public String getName() {
		return oauth2User.getAttribute("name");
	}

	public String getEmail() {
		return oauth2User.getAttribute("email");
	}

	public String getFullName() {
		return fullName != null ? fullName : oauth2User.getAttribute("name");
	}


	public String getClientName() {
		return clientName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
