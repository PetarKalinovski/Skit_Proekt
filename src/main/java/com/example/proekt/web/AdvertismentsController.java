package com.example.proekt.web;

import com.example.proekt.model.*;
import com.example.proekt.service.AdvertisementService;
import com.example.proekt.service.ApartmentService;
import com.example.proekt.service.MessageThreadService;
import com.example.proekt.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class AdvertismentsController {

    private final AdvertisementService advertisementService;
    private final ApartmentService apartmentService;

    private final UserService userService;

    private final MessageThreadService messageThreadService;
    public AdvertismentsController(AdvertisementService advertisementService, ApartmentService apartmentService, UserService userService, MessageThreadService messageThreadService) {
        this.advertisementService = advertisementService;
        this.apartmentService = apartmentService;
        this.userService = userService;
        this.messageThreadService = messageThreadService;
    }

    @GetMapping("/apartments")
    public String listAll(@RequestParam(required = false) Double priceMore, @RequestParam(required = false)  Double priceLess,
                          @RequestParam(required = false)  MunicipalityType municipality, @RequestParam(required = false)  Double avgRatingMore,
                          @RequestParam(required = false)  Double avgRatingLess, @RequestParam(required = false) Double comments,
                          @RequestParam(required = false) Integer numRooms, @RequestParam(required = false)  Integer sizeMore,
                          @RequestParam(required = false)  Integer sizeLess,
                          @RequestParam(required = false)AdvertisementType advertisementType, Model model
                          ){
        List<Advertisement> ads;
        ads=this.advertisementService.filter( priceMore,  priceLess,  municipality, avgRatingMore,
                 avgRatingLess,comments,numRooms, sizeMore, sizeLess, advertisementType);

        model.addAttribute("ads", ads);
        model.addAttribute("smallSize",advertisementService.minSize());
        model.addAttribute("bigSize",advertisementService.maxSize());
        model.addAttribute("bigPrice", advertisementService.maxPrice());
        model.addAttribute("smallPrice", advertisementService.minPrice());
//        model.addAttribute("rating", this.advertisementService.ratingAvg());
        model.addAttribute("municipalities", MunicipalityType.values());
        model.addAttribute("types", AdvertisementType.values());
        return "list";
    }
    @GetMapping("/apartments/add/ad")
    public String showAdd(Model model, Principal principal) {
        model.addAttribute("apartments", apartmentService.listAll());
        model.addAttribute("municipalities", MunicipalityType.values());
        model.addAttribute("types", AdvertisementType.values());
        model.addAttribute("user", principal.getName());
        return "adForm";
    }
    @GetMapping("/apartments/edit/ad/{id}")
    public String showEdit(@PathVariable Long id, Model model) {
        model.addAttribute("ad", advertisementService.findById(id));
        model.addAttribute("apartments", apartmentService.listAll());
        model.addAttribute("municipalities", MunicipalityType.values());
        model.addAttribute("types", AdvertisementType.values());

        return "adForm";
    }

    @GetMapping("/apartments/details/{id}")
    public String detailsApartments(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("ad", advertisementService.findById(id));
        model.addAttribute("apartments", apartmentService.listAll());
        model.addAttribute("municipalities", MunicipalityType.values());
        model.addAttribute("types", AdvertisementType.values());

        String username=principal.getName();
        String user2=advertisementService.findById(id).getOwner().getUsername();
        MessageThread messageThread;
            if(messageThreadService.findByUser1AndUser2AndAdvertisement(username,user2,id)==null){
                messageThread=messageThreadService.create(username,user2,id);
            }
            else
                messageThread=messageThreadService.findByUser1AndUser2AndAdvertisement(username,user2,id);
            model.addAttribute("threadId", messageThread.getId());



        return "details";
    }


    @PostMapping("/apartments")
    public String createAd(@RequestParam Long apartment,@RequestParam AdvertisementType type,@RequestParam Double price, Principal principal){
        String username=principal.getName();
        this.advertisementService.create(apartment,type,price,username);
        return "redirect:/apartments";
    }

    @PostMapping("/apartments/{id}")
    public String editAd(@PathVariable Long id, @RequestParam Long apartment,@RequestParam AdvertisementType type,@RequestParam Double price, Principal principal){
        User user=userService.findByUsername(principal.getName());
        if(! advertisementService.findById(id).getOwner().equals(user)){
            throw new AccessDeniedException("You do not have permission to edit this advertisement");
        }
        this.advertisementService.update(id,apartment,type,price);
        return "redirect:/apartments";
    }

    @PostMapping("/apartments/delete/ad/{id}")
    public String deleteAd(@PathVariable Long id) {
        messageThreadService.deleteByAdvertisement(id);
        this.advertisementService.delete(id);
        return "redirect:/apartments";
    }

    @PostMapping("/apartments/rate/{id}")
    public String rateApartment(@PathVariable Long id, @RequestParam Double rating, Principal principal) {
        if (rating >= 1 && rating <= 5) {
            String username = principal.getName();
            advertisementService.addRating(rating,id,username);
        }

        return "redirect:/apartments/details/" + id;
    }
    @PostMapping("/apartments/comments/{id}")
    public String addComment(@PathVariable Long id, @RequestParam String comment) {
        if (comment != null && !comment.trim().isEmpty()) {
            advertisementService.addComment(comment,id);
        }
        return "redirect:/apartments/details/" + id;
    }
}
