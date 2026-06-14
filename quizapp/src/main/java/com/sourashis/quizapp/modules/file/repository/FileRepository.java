package com.sourashis.quizapp.modules.file.repository;

import com.sourashis.quizapp.modules.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByUuid(String uuid);

    List<File> findByUserId(Long userId);

    List<File> findByFileType(String fileType);
}
