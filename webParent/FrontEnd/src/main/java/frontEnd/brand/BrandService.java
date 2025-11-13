package frontEnd.brand;

import com.onlineStoreCom.entity.brand.Brand;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
public class BrandService {


    public static final int BRANDS_PER_PAGE = 10;

    @Autowired
    private BrandRepository repository;


    public List<Brand> listAll() {

        return repository.findAll();
    }

    public Boolean existsById(Integer id) {
        return repository.findById(id).isPresent();
    }


//    public Brand findById(Integer id) throws CategoryNotFoundException {
//        try {
//
//
//            return repository.findById(id).get();
//
//
//        } catch (NoSuchElementException ex) {
//
//
//            throw new CategoryNotFoundException("Could not find any Category with ID " + id);
//        }
//    }
//
//    public List<Brand> listByPage(PageInfo pageInfo, int pageNum, String sortField, String sortDir, String keyWord) {
//        Sort sort = Sort.by(sortField);
//
//
//        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
//
//        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
//
//        Page<Brand> pageUsers = null;
//
//        if (keyWord != null) {
//            pageUsers = repository.findAll(keyWord, pageable);
//        } else {
//            pageUsers = repository.findAll(pageable);
//        }
//
//        pageInfo.setTotalElements(pageUsers.getTotalElements());
//        pageInfo.setTotalPages(pageUsers.getTotalPages());
//
//
//        return pageUsers.getContent();
//    }
//
//
//    public void delete(Integer id) throws BrandNotFoundException {
//        Integer countById = repository.countById(id);
//
//        if (countById == null || countById == 0) {
//            throw new BrandNotFoundException("Could not find any brand with ID " + id);
//        }
//
//        repository.deleteById(id);
//    }
//
//    public String checkUnique(Integer id, String name) {
//        boolean isCreatingNew = (id == null || id == 0);
//        Brand brandByName = repository.findByName(name);
//        System.out.println("isCreatingNew   " + isCreatingNew);
//        if (isCreatingNew) {
//            if (brandByName != null) return "Duplicate";
//
//        } else {
//            if (brandByName != null && brandByName.getId() != id) {
//                return "Duplicate";
//            }
//        }
//
//        return "Ok";
//    }
//
//
//    public Brand saveBrand(Brand brand) {
//        return repository.saveAndFlush(brand);
//
//    }


}
