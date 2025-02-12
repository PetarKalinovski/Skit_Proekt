package com.example.proekt.web;

import com.example.proekt.model.MunicipalityType;
import com.example.proekt.service.ApartmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ApartmentsController {
    private final ApartmentService apartmentService;

    public ApartmentsController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }
    @GetMapping("/apart")
    public String listAll(Model model) {
        model.addAttribute("apartments", apartmentService.listAll());
        return "apartmentList";
    }

    @GetMapping("/apart/add/apt")
    public String showApartment(Model model, Principal principal){
        model.addAttribute("municipalities", MunicipalityType.values());
        return "apartForm";
    }

    @GetMapping("/apart/edit/apt/{id}")
    public String EditApartment(@PathVariable Long id, Model model) {
        model.addAttribute("apa", apartmentService.findById(id));
        model.addAttribute("municipalities", MunicipalityType.values());
        return "apartForm";
    }

    @PostMapping("/apart")
    public String createApt(@RequestParam MunicipalityType municipality,
                            @RequestParam String address,
                            @RequestParam Integer numRooms,
                            @RequestParam Integer size,
                            @RequestParam List<String> imageUrls,
                            @RequestParam String title, Principal principal){

        this.apartmentService.create( municipality,  address,  numRooms,  size,
                imageUrls.stream().filter(url->url !=null && !url.isEmpty()).collect(Collectors.toList()),
                title, principal.getName());

        return "redirect:/apartments";
    }

    @PostMapping("/apart/{id}")
    public String editApt(  @PathVariable Long id,
                            @RequestParam MunicipalityType municipality,
                            @RequestParam String address,
                            @RequestParam Integer numRooms,
                            @RequestParam Integer size,
                            @RequestParam List<String> imageUrls,
                            @RequestParam String title){

        this.apartmentService.update(id, municipality,  address,  numRooms,  size,  imageUrls,title);

        return "redirect:/apartments";
    }

    @PostMapping("/apartments/delete/apt/{id}")
    public String deleteApt(@PathVariable Long id) {
        this.apartmentService.delete(id);
        return "redirect:/apartments";
    }
}
