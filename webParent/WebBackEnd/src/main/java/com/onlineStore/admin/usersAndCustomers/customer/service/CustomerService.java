package com.onlineStore.admin.usersAndCustomers.customer.service;


import com.onlineStore.admin.usersAndCustomers.customer.repository.CustomersRepository;
import com.onlineStore.admin.setting.country.CountryRepository;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStore.admin.category.services.PageInfo;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.exception.CustomerNotFoundException;
import com.onlineStoreCom.entity.setting.Country.Country;
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


    public List<Customer> listByPage(PageInfo pageInfo, int pageNum, String sortField, String sortDir, String keyword) {
        Pageable pageable = createPageable(pageNum, sortField, sortDir);

        Page<Customer> customersByPage = (keyword != null) ? customerRepo.findAll(keyword, pageable) : customerRepo.findAll(pageable);

        setPageInfo(pageInfo, customersByPage);

        return customersByPage.getContent();
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

    private void setPageInfo(PageInfo pageInfo, Page<?> page) {
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPages(page.getTotalPages());
    }

    public Customer saveCustomer(Customer customer) {



        if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
            Customer saved =customerRepo.getReferenceById(customer.getId());
            customer.setPassword(saved.getPassword());
            customer.setEnabled(true);

        }else {

            System.out.println("rrrrrrrrrr      "+ customer.getPassword());
            encodePassword(customer);
            customer.setEnabled(true);
        }


        customer.setCreatedTime(new Date());

        String randomCode = RandomStringUtils.randomAlphabetic(64);
        customer.setVerificationCode( randomCode);


        return customerRepo.saveAndFlush(customer);
    }

    private void encodePassword(Customer customer) {

        String encodePassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodePassword);

    }

}
