package frontEnd.setting;

import com.onlineStoreCom.entity.setting.ThemeDTO;
import frontEnd.setting.service.ThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/theme")
public class ThemeRestController {

    @Autowired
    private ThemeService themeService;

    @GetMapping
    public ThemeDTO getTheme() {
        return themeService.getResolvedTheme();
    }
}
