# Функционалности

## Страна каде се листаат огласи за станови/куќи(`/apartments`)

### Релеватни модели:
Класа apartment која се состои од листа од слики, квадратура, број соби и корисник кој ја креирал адреса итн...

Класа advertisement која се состои од  апартман, цена, и дали е за рентање или купување, листа од оцени, метод за наоѓање на средна оцена и листа од коментари.

### Функционалности на оваа страна
Функционалности за бришење и едитирање на овие 2 класи ако си корисникот кој ги креирал

Во едитирањето можеш да смениш стан за кој е огласот, но само на станови кои твоето корисничко име ги креирало

На страната за листање огласи има филтри. 3 од нив се "range" тип. Може да се бира минимална и максимална големина на стан, цена и оценка.
Другите филтри се бирање по општина, и бирање дали станува збор за купување или издавање. И има филтер за број на соби бирање.

Кодот од контролерот за оваа страна каде освен прикажување на огласи се праќаат и филтрите:
```java
public String listAll(@RequestParam(required = false) Double priceMore, @RequestParam(required = false)  Double priceLess,  @RequestParam(required = false)  MunicipalityType municipality, @RequestParam(required = false)  Double avgRatingMore,  @RequestParam(required = false)  Double avgRatingLess, @RequestParam(required = false) Double comments,  @RequestParam(required = false) Integer numRooms, @RequestParam(required = false)  Integer sizeMore,  @RequestParam(required = false)  Integer sizeLess, @RequestParam(required = false)AdvertisementType advertisementType, Model model){  

        List<Advertisement> ads;
        
        ads=this.advertisementService.filter( priceMore,  priceLess,
        municipality, avgRatingMore, avgRatingLess,comments,numRooms, sizeMore,
        sizeLess, advertisementType);  
  
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
```


Има копче за креирање на нов оглас.

 Линкови за login, logout и register.

## Корисници
Има 2 типа на корисници. Admin и User. Ако си User можеш да едитираш и да бришеш твои огласи. Ако си Admin можеш исто така да едитираш и да бришеш твои огласи, но можеш да ги бришеш сите други.

За користење на другите страни од оваа апликација треба да се биде логиран.

## Страна за детали на оглас (`apartments/details/{id}`)

Овде се претставени податоците детално во класите оглас и стан.
Има слики кои можат да се кликнат и да се минува низ нив со стрелки.

Има можност да се рангира огласот, со селектриање на бројот на ѕвезди и кликање на коче  submit rating. Ако 2 пати се кликне, рејтингот на тој корисник се менува во последното селектирано.

Методот за рангирање во контролерот:
```java
@PostMapping("/apartments/rate/{id}")  
public String rateApartment(@PathVariable Long id, @RequestParam Double rating, Principal principal) {  
    if (rating >= 1 && rating <= 5) {  
        String username = principal.getName();  
        advertisementService.addRating(rating,id,username);  
    }  
  
    return "redirect:/apartments/details/" + id;  
}
```

Во сервисниот слој:
```java
@Override  
public Advertisement addRating(Double rating, Long id, String username) {  
    Advertisement advertisement= this.findById(id);  
    advertisement.getRatings().put(username,rating);  
    return advertisementRepository.save(advertisement);  
}
```



Има можност за пишување на анонимни коментари на страната. Сличен е кодот за методот како и тој погоре.

На оваа страна се наоѓаат копчињата за едитирање на оглас и за негово бришење. Тие се заштитени од Spring Security така што не може да се пристапи до нив без правилна автентикација.

### Message класа функционалности
Исто во зависност од дали корисникот е креатор на огласот, има 2 можни линка.
Ако корисникот сака да стапи во контакт со креаторот на огласот има линк до страна каде има chat. 

Додека ако корисникот е креаторот има можен линк до сите chats со сите корисници кои пишале порака на тој спесифичен оглас.
И може на сите од нив да им се врати назад на пораките.

Ова работи со 2 класи. Класа за пораки каде има праќач на порака, примач, време на праќање и самата порака.

Другата класа се вика MessageThread каде има 2 корисници, листа од пораки и огласот на кој се разменуваат овие 2 пораки.

Post метода во контролер за праќање на порака:
```java
@PostMapping("/send/{id}")  
public String sendMessage(@PathVariable Long id, @RequestParam String user, @RequestParam String content) {  
    MessageThread mst = messageThreadService.findById(id);  
    String user2;  
    if (user.equals(mst.getUser2().getUsername()))  
        user2 = mst.getUser1().getUsername();  
    else        user2 = mst.getUser2().getUsername();  
    Message m = messageService.sendMessage(user, user2, content);  
    messageThreadService.addAMessage(mst.getId(), m.getId());  
  
    return "redirect:/messages/conversation/" + mst.getId();  
}
```
