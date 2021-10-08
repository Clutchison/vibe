package com.hutchison.vibe.repository;

import com.hutchison.vibe.model.entity.OwnerPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerPermissionRepository extends JpaRepository<OwnerPermission, Long> {

}
