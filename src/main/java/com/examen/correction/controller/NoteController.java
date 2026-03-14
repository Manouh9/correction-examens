package com.examen.correction.controller;

import com.examen.correction.model.Note;
import com.examen.correction.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @GetMapping
    public String listeNotes(Model model) {
        model.addAttribute("notes", noteRepository.findAll());
        return "notes/liste";
    }
}