package sec.project.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import sec.project.domain.Account;
import sec.project.domain.Note;
import sec.project.repository.AccountRepository;
import sec.project.repository.NoteRepository;

@Controller
public class NotebookController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private NoteRepository noteRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String list(Model model) {
        List<Note> notes = noteRepository.findAll();
        // Leave public notes only
        List<Note> publicNotes = new ArrayList<Note>();
        for (Note note : notes) {
            if (note.getPublicNote()) {
                publicNotes.add(note);
            }
        }
        model.addAttribute("notes", publicNotes);
        return "index";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String searchNotes(Model model, @RequestParam(value = "searchterm", required = true) String searchTerm) {

        List<Note> notes = noteRepository.findByTitleContainsIgnoreCase(searchTerm);
        // Leave public notes only
        List<Note> publicNotes = new ArrayList<Note>();
        for (Note note : notes) {
            if (note.getPublicNote()) {
                publicNotes.add(note);
            }
        }
        model.addAttribute("notes", publicNotes);

        return "results";
    }

    @RequestMapping(value = "/write", method = RequestMethod.GET)
    public String writeNote() {
        return "write";
    }

    @RequestMapping(value = "/write", method = RequestMethod.POST)
    public String submitNote(@RequestParam String title, @RequestParam String text, @RequestParam(value = "publicnote", required = false) String publicNote) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authName = auth.getName();
        Account currentAccount = accountRepository.findByUsername(authName);
        Note note = new Note();
        note.setAccount(currentAccount);
        note.setPublicNote(publicNote != null);
        note.setTitle(title);
        note.setText(text);
        noteRepository.save(note);

        return "redirect:/mynotes";
    }

    @RequestMapping(value = "/editnote/{noteId}", method = RequestMethod.GET)
    public String editNote(Model model, @PathVariable Long noteId) {

        Note note = noteRepository.findById(noteId);
        /* Flaw: do not check if caller is authorized to edit the note */
        model.addAttribute("note", note);

        return "editnote";
    }

    @RequestMapping(value = "/editnote/{noteId}", method = RequestMethod.POST)
    public String saveEditedNote(@PathVariable Long noteId, @RequestParam String title, @RequestParam String text, @RequestParam(value = "publicnote", required = false) String publicNote, @RequestParam(value = "action", required = true) String action, Model model) {

        if (action.equals("cancel")) {
            return "redirect:/mynotes";
        }

        /* Flaw: Not checking if current user is authorized to edit the note */
        /* This block here... */
        Note note = noteRepository.findById(noteId);
        note.setPublicNote(publicNote != null);
        note.setTitle(title);
        note.setText(text);
        noteRepository.save(note);
        model.addAttribute("note", note);

        /* ...should be replaced with the following: 
           Check that current user is the author of the note
           and thus allowed to edit it */
        /*
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authName = auth.getName();
        Account currentAccount = accountRepository.findByUsername(authName);
        Note note = noteRepository.findById(noteId);
        if (note.getAccount().getUsername().equals(currentAccount.getUsername())) {
            note.setPublicNote(publicNote != null);
            note.setTitle(title);
            note.setText(text);
            noteRepository.save(note);
            model.addAttribute("note", note);
        }
        */

        return "editnote";
    }

    @RequestMapping(value = "/deletenote", method = RequestMethod.POST)
    public String deleteNote(Model model, @RequestParam Long noteId) {

        // Delete and redirect to mynotes page which should be the only place where delete option is displayed
        // Now this is vulnerable to CSRF attack
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authName = auth.getName();
        Account currentAccount = accountRepository.findByUsername(authName);
        Note note = noteRepository.findById(noteId);
        if (note != null) {
            if (note.getAccount().getUsername().equals(currentAccount.getUsername())) {
                // Authorized to delete
                noteRepository.delete(noteId);
            }
        }

        return "redirect:mynotes";
    }

    @RequestMapping(value = "/mynotes")
    public String displayMyNotes(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authName = auth.getName();

        List<Note> notes = noteRepository.findAll();
        // Leave current user's notes only
        List<Note> myNotes = new ArrayList<Note>();
        for (Note note : notes) {
            if (note.getAccount().getUsername().equals(authName)) {
                myNotes.add(note);
            }
        }
        model.addAttribute("notes", myNotes);

        return "mynotes";
    }

    @RequestMapping(value = "/viewnote/{noteId}")
    public String viewNote(Model model, @PathVariable Long noteId) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authName = auth.getName();
        Account currentAccount = accountRepository.findByUsername(authName);

        Note note = noteRepository.findById(noteId);

        // Here is a flaw: view any note if you know its id.
        // Fix by removing next line and uncommenting the following code block that is currently commented.
        model.addAttribute("note", note);
        /*
         if (note != null) {
            if (
                    (note.getAccount().getUsername().equals(currentAccount.getUsername())) 
                    || (note.getPublicNote())
                    ){
                // Authorized to view
                model.addAttribute("note", note);
            }
        }
         */

        return "viewnote";
    }

    @RequestMapping(value = "/login")
    public String loadLogin(Model model) {
        List<Note> notes = noteRepository.findAll();
        // Leave public notes only
        List<Note> publicNotes = new ArrayList<Note>();
        for (Note note : notes) {
            if (note.getPublicNote()) {
                publicNotes.add(note);
            }
        }
        model.addAttribute("notes", publicNotes);
        return "login";
    }

}
