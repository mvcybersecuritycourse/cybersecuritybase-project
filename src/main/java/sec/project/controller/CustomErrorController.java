/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sec.project.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author mivii
 */
@ControllerAdvice
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";
    
    private final ErrorAttributes errorAttributes;
    
    @Autowired
    public CustomErrorController(ErrorAttributes errorAttributes) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }
    
    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletRequest rq, Exception ex) {
        ModelAndView mav = new ModelAndView();
        
        // Vulnerability: exception is passed to view and stack trace printed on the page.
        // Fix by either commenting the following line or by removing stack trace printing code from the view (error.html).
        mav.addObject("exception", ex);
        mav.addObject("url", rq.getRequestURL());
        mav.setViewName(getErrorPath());
        
        return mav;
    }
    
    @RequestMapping(value = PATH)
    public String error(Model model) {
        return getErrorPath();
    }
    
    @Override
    public String getErrorPath() {
        return PATH;
    }
    
}
