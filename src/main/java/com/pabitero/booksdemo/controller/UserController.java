package com.pabitero.booksdemo.controller;

import com.pabitero.booksdemo.entity.User;
import com.pabitero.booksdemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ModelAndView getUsers() {
        log.info("Inside getUsers in UserController");
        ModelAndView mav = new ModelAndView("books/userList");
        mav.addObject("users", userService.findAllUsers());
        return mav;
    }

    @PostMapping("/users")
    @ResponseBody
    public String createUser(User user) {
        log.info("Inside createUser in UserController");
        userService.saveUser(user);
        return "user/success";
    }

    @GetMapping("/login")
    public ModelAndView goToLoginPage() {
        log.info("Inside goToLoginPage in UserController");
        ModelAndView mav = new ModelAndView("books/login");
        return mav;
    }

    @PostMapping("/login")
    @ResponseBody
    public ModelAndView verifyLogin(User user, HttpServletRequest request) {
        log.info("Inside verifyLogin in UserController");
        String email = user.getEmail();
        String password = user.getPassword();

        User userInDb = userService.findUserByEmail(email);

        /*TODO fix index.html and method below to add user object to session so that name will show
        *   when logged in
        */
        ModelAndView mav = new ModelAndView();
        if (userInDb != null) {
            if (userInDb.getPassword().equals(password)) {
                Optional<HttpSession> session = Optional.of(request.getSession());
                if (session.isPresent()) {
                    mav.setViewName("books/index");
                    return mav;
                } else {
                    session.get().setAttribute("user", userInDb);
                    mav.setViewName("books/index");
                    mav.addObject("user", userInDb);
                    return mav;
                }
            } else {
                mav.setViewName("books/errorPage");
                return mav;
            }
        } else {
            mav.setViewName("books/index");
            return mav;
        }
    }

    @GetMapping("/register")
    public ModelAndView goToRegisterPage(HttpServletRequest request) {
        log.info("Inside goToRegisterPage in UserController");
        ModelAndView mav = new ModelAndView("books/registration");
        mav.addObject(new User());
        mav.addObject(request.getSession());
        return mav;
    }

    @PostMapping("/register")
    @ResponseBody
    public ModelAndView processRegistration(User user) {
        log.info("Inside processRegistration in UserController");
        userService.saveUser(user);
        ModelAndView mav = new ModelAndView("books/success");
        mav.addObject(user);
        return mav;
    }
}
