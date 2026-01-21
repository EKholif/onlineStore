package com.onlineStore.admin.service;

import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStore.admin.utility.paging.PagingAndSortingHelper;
import com.onlineStore.admin.utility.paging.PagingAndSortingParam;
import com.onlineStore.services.service.ServiceService;
import com.onlineStoreCom.entity.service.Service;
import com.onlineStoreCom.entity.service.ServiceLocationType;
import com.onlineStoreCom.exception.ServiceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping("/services")
    public String listFirstPage() {
        return "redirect:/services/page/1?sortField=name&sortDir=asc";
    }

    @GetMapping("/services/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listServices", moduleURL = "/services/page/") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {
        Page<Service> page = serviceService.listByPage(pageNum, helper.getSortField(), helper.getSortDir(),
                helper.getKeyword());
        helper.updateModelAttributes(pageNum, page);
        return "services/services";
    }

    @GetMapping("/services/new")
    public String newService(Model model) {
        model.addAttribute("service", new Service());
        model.addAttribute("pageTitle", "Create New Service");
        model.addAttribute("locationTypes", ServiceLocationType.values());
        return "services/service_form";
    }

    @PostMapping("/services/save")
    public String saveService(Service service, RedirectAttributes ra,
                              @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            service.setMainImage(fileName);
            Service savedService = serviceService.save(service);

            // AG-ASSET-PATH-007: Use new hierarchical structure for services
            String uploadDir = "tenants/" + savedService.getTenantId() + "/assets/services/" + savedService.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (service.getMainImage().isEmpty())
                service.setMainImage(null);
            serviceService.save(service);
        }

        ra.addFlashAttribute("message", "The service has been saved successfully.");
        return "redirect:/services";
    }

    @GetMapping("/services/edit/{id}")
    public String editService(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Service service = serviceService.get(id);
            model.addAttribute("service", service);
            model.addAttribute("pageTitle", "Edit Service (ID: " + id + ")");
            model.addAttribute("locationTypes", ServiceLocationType.values());

            return "services/service_form";
        } catch (ServiceNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/services";
        }
    }

    @GetMapping("/services/delete/{id}")
    public String deleteService(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
        try {
            serviceService.delete(id);
            ra.addFlashAttribute("message", "The service ID " + id + " has been deleted successfully");
        } catch (ServiceNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/services";
    }

    @GetMapping("/services/{id}/enabled/{status}")
    public String updateServiceEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
                                             RedirectAttributes redirectAttributes) {
        serviceService.updateServiceEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The Service ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/services";
    }
}
