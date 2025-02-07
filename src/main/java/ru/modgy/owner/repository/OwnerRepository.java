package ru.modgy.owner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.modgy.owner.model.Owner;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByMainPhoneOrOptionalPhone(String mainPhoneNumber, String optionalPhoneNumber);

    @Query(value = """
            select owner from Owner as owner where :direction = 'name'
             and (lower(owner.firstName) like concat('%', lower(:searchLine) , '%')
             or lower(owner.middleName) like concat ('%', lower(:searchLine), '%')
             or lower(owner.lastName) like concat ('%', lower(:searchLine), '%'))
             or :direction ='phone'
             and (owner.mainPhone like concat ('%', :searchLine, '%')
             or owner.optionalPhone like concat ('%', :searchLine, '%'))
            """)
    List<Owner> searchOwner(
            @Param("searchLine") String searchLine,
            @Param("direction") String direction
    );

    Integer deleteOwnerById(Long ownerId);
}
