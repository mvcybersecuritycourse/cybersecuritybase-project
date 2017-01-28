/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sec.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.Note;

/**
 *
 * @author mivii
 */
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    Note findById(Long id);
    
    List<Note> findByTitleContainsIgnoreCase(String titlePart);
    
}
