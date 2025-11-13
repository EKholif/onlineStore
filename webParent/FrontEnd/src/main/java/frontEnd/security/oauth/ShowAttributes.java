package frontEnd.security.oauth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class ShowAttributes {

    @GetMapping("/attributess")
    public String showAttributes(@AuthenticationPrincipal OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Print all attributes to console
        System.out.println("=== OAuth2 Attributes ===");
        attributes.forEach((key, value) -> {
            System.out.println(key + " : " + value);
        });

        // Or return as a simple view or REST endpoint if you want
        return "redirect:/"; // Or show them in a page
    }


    @GetMapping("/attributes")
    public String showAttributes(@AuthenticationPrincipal OAuth2User user, Model model) {
        model.addAttribute("attributes", user.getAttributes());
        return "attributes"; // attributes.html
    }

}