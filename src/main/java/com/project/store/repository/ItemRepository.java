package com.project.store.repository;

import com.project.store.domain.item.Item;
import com.project.store.domain.product.Product;
import com.project.store.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author rahul
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {


    List<Item> findByUser(User user);

    List<Item> findByProduct(Product product);

}