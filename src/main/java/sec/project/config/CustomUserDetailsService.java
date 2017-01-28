package sec.project.config;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sec.project.domain.Account;
import sec.project.domain.Note;
import sec.project.repository.AccountRepository;
import sec.project.repository.NoteRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private NoteRepository noteRepository;

    @PostConstruct
    public void init() {

        Account account = new Account();
        account.setUsername("ted");
        account.setPassword("$2a$06$rtacOjuBuSlhnqMO2GKxW.Bs8J6KI0kYjw/gtF0bfErYgFyNTZRDm");
        accountRepository.save(account);
        
        Note note = new Note();
        note.setAccount(account);
        note.setPublicNote(Boolean.TRUE);
        note.setTitle("Public note by ted");
        note.setText("Bubblegum");
        noteRepository.save(note);
        
        note = new Note();
        note.setAccount(account);
        note.setPublicNote(Boolean.FALSE);
        note.setTitle("Private note by ted");
        note.setText("Gummi Bears");
        noteRepository.save(note);
        
        account = new Account();
        account.setUsername("mister");
        account.setPassword("$2a$10$nKOFU.4/iK9CqDIlBkmMm.WZxy2XKdUSlImsG8iKsAP57GMcXwLTS");
        accountRepository.save(account);
        
        
        note = new Note();
        note.setAccount(account);
        note.setPublicNote(Boolean.TRUE);
        note.setTitle("Mister president announcement");
        note.setText("Hey hey hey!");
        noteRepository.save(note);
        
        note = new Note();
        note.setAccount(account);
        note.setPublicNote(Boolean.FALSE);
        note.setTitle("I am sad");
        note.setText("I am so sad but no one else will ever find out as this is a private note.");
        noteRepository.save(note);
        
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));

    }
}
