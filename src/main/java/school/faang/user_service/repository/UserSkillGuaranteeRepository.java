package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.List;

public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO user_skill_guarantee (user_id, skill_id, guarantor_id)
            VALUES (?1, ?2, ?3) returning id
            """)
    Long create(long userId, long skillId,long guarantorId);

    @Query(nativeQuery = true, value = """
            SELECT s.* FROM skill s
            JOIN user_skill_guarantee usg ON s.id = usg.skill_id
            WHERE usg.user_id = ?1 AND usg.guarantor_id = ?2
            """)
    List<Skill> findAllSkillsGuaranteedToUserByGuarantee(long userId, long guarantorId);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM user_skill_guarantee u
            WHERE u.user_id = ?1 AND u.skill_id = ?2 AND u.guarantor_id=?3
            """)
    void deleteSkillGuaranteedToUserByGuarantee(long userId, long skillId, long guarantorId);

}