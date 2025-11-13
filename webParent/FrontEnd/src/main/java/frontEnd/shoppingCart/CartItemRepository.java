package frontEnd.shoppingCart;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.shoppingCart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String>{

     List<CartItem> findByCustomer(Customer customer);

    @Query("SELECT c FROM CartItem c WHERE c.customer = ?1 AND c.product = ?2")
    CartItem findByCustomerAndProduct(Customer customer, Product product);

    @Query("UPDATE CartItem c set  c.quantity=?1 WHERE c.customer = ?2 and c.product.id=?3")
    @Modifying
    Integer updateQuantity(Integer quantity, Customer customer, Integer product);

    @Query("DELETE CartItem c  WHERE c.customer.id = ?1 and c.product.id=?2")
    @Modifying
    Integer deleteByCustomerAndProduct( Integer customer, Integer product);

    @Query("DELETE CartItem c  WHERE c.customer.id = ?1 ")
    @Modifying
    Integer deleteByCustomer( Integer customer);


}