package me.krzysztofprogramming.resourcesservice.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
class ResourcesController {

    @GetMapping("/protected")
    @ResponseStatus(HttpStatus.OK)
    public String getProtectedResources() {
        return "Protected";
    }

    @GetMapping("/public")
    @ResponseStatus(HttpStatus.OK)
    public String getPublicResources() {
        return "Public";
    }

    @GetMapping("/admin")
    @ResponseStatus(HttpStatus.OK)
    public String getAdminResources() {
        return "Admin";
    }
}
