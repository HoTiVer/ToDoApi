package org.example.todoapi.Repository;

import org.example.todoapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Query("select u from User u where u.email = :username")
    User findByEmail(String username);

    @Query("select u.id from User u where u.email = :name")
    Long findIdByEmail(String name);
}
