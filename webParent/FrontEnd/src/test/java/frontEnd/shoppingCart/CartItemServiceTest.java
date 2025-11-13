package frontEnd.shoppingCart;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CartItemServiceTest {


    @MockBean
    private CartItemRepository repository;

    @InjectMocks
    private ShoppingCartService service;






////    @Test
////    public void checkExceptionNotFoundExpatiation()  {
////        Integer id =500L;
////        String email= "Computers";
////        String alias= "Computers";
////
////
////        User cat = new User(id,email);
////
////        Mockito.when( repository.findByEmail(email)).thenReturn(cat);
////
////        boolean result = service.isEmailUnique(id,email);
////
////
////        assertThat(result).isEqualTo(true);
////
////    }
//
////    @Test
////    public void checkUniqe()  {
////        Integer id = 0L;
////        String name= "cam";
////        String alias= "cassm";
////
////
//////        Category cat = new Category();
////
//////        Mockito.when( repository.findByName(name)).thenReturn(cat);
////
////        String result = service.checkUnique(id,name,alias);
////
//////        System.out.println(cat.getId());
////        System.out.println(result);
////
////        assertThat(result).isEqualTo("DuplicateName");
////
////    }
////
////
}
