package com.onlineStore.admin.usersAndCustomers.customer.service;

import com.onlineStore.admin.setting.country.CountryRepository;
import com.onlineStore.admin.usersAndCustomers.customer.repository.CustomersRepository;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.exception.CustomerNotFoundException;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomersRepository customerRepo;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public static final int USERS_PER_PAGE = 5;

    public List<Customer> listAllUsers() {
        return customerRepo.findAll();
    }

    public Customer findById(Integer id) {
        return customerRepo.getReferenceById(id);
    }

    public void UdpateCustomerEnableStatus(Integer id, Boolean enable) {
        customerRepo.enableCustomer(id, enable);

    }

    public void delete(Integer id) throws CustomerNotFoundException {
        Integer count = customerRepo.countById(id);
        if (count == null || count == 0) {
            throw new CustomerNotFoundException("Could not find any customers with ID " + id);
        }

        customerRepo.deleteById(id);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public Page<Customer> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Pageable pageable = createPageable(pageNum, sortField, sortDir);

        return (keyword != null) ? customerRepo.findAll(keyword, pageable) : customerRepo.findAll(pageable);
    }

    private void savePhoto(Customer customer, MultipartFile multipartFile, String dirName) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        customer.setImage(fileName);

        Customer savedCustomer = saveCustomer(customer);

        String uploadDir = dirName + savedCustomer.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    }

    private Pageable createPageable(int pageNum, String sortField, String sortDir) {
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        return PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
    }

    public Customer saveCustomer(Customer customer) {
        boolean isUpdating = (customer.getId() != null);

        if (isUpdating) {
            Customer existingCustomer = customerRepo.findById(customer.getId()).orElse(null);

            if (existingCustomer != null) {
                // Preserve Password if empty
                if (customer.getPassword() == null || customer.getPassword().trim().isEmpty()) {
                    customer.setPassword(existingCustomer.getPassword());
                } else {
                    encodePassword(customer);
                }

                // Preserve critical system fields
                customer.setCreatedTime(existingCustomer.getCreatedTime());
                customer.setVerificationCode(existingCustomer.getVerificationCode());
                customer.setAuthenticationType(existingCustomer.getAuthenticationType());
                customer.setRestPasswordToken(existingCustomer.getRestPasswordToken());

                // Keep enabled status from DB if not explicitly handled (or force true if
                // desired logic)
                // Assuming admin form might change enabled status, but if null preserve?
                // For safety in this context (Admin saving), usually we override with Form
                // data.
                // But createdTime MUST be preserved.

                // If the form didn't pass "enabled", it might be false.
                // However, the original code FORCED "true".
                // Let's preserve DB value if the input object seems "incomplete" regarding
                // status
                // But usually Admin form has "Enabled" checkbox.
                // If admin unchecks it, it comes as false.
                // So we should respect the incoming `customer.isEnabled()` if it's bound.
                // BUT original code set it to true ALWAYS. Let's assume user wants to control
                // it via form
                // or preserve if not in form.
                // For now: Preserve logic of setting createdTime/verificationCode.

            } else {
                // Should not happen if ID exists
                encodePassword(customer); // Fallback
            }
        } else {
            // New Customer
            encodePassword(customer);
            customer.setCreatedTime(new Date());
            customer.setVerificationCode(RandomStringUtils.randomAlphabetic(64));
            customer.setEnabled(true);
        }

        return customerRepo.saveAndFlush(customer);
    }

    private void encodePassword(Customer customer) {

        String encodePassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodePassword);

    }

    public boolean isEmailUnique(Integer id, String email) {
        Long tenantId = com.onlineStoreCom.tenant.TenantContext.getTenantId();
        System.out.println("AG-DEBUG: [isEmailUnique] Check for email: '" + email + "' @ TenantID: " + tenantId);

        // AG-TENANT-ISO-001: Check uniqueness ONLY within the current tenant scope
        Customer customerByEmail = customerRepo.findByEmailAndTenantId(email, tenantId);

        System.out.println("AG-DEBUG: [isEmailUnique] Result found: "
                + (customerByEmail != null ? "YES (ID: " + customerByEmail.getId() + ")" : "NO"));

        if (customerByEmail == null)
            return true;

        boolean isCreatingNew = (id == null);

        if (isCreatingNew) {
            return false;
        } else {
            if (customerByEmail.getId() != id) {
                return false;
            }
        }
        return true;
    }
}
