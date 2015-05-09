package edu.sjsu.cmpe275.prj.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;





import edu.sjsu.cmpe275.prj.dao.*;
import edu.sjsu.cmpe275.prj.models.LandingPage;
import edu.sjsu.cmpe275.prj.dao.*;
import edu.sjsu.cmpe275.prj.models.Login;
import edu.sjsu.cmpe275.prj.models.book;
import edu.sjsu.cmpe275.prj.models.category;
import edu.sjsu.cmpe275.prj.models.HomePageModel;
import edu.sjsu.cmpe275.prj.models.statistics;
import edu.sjsu.cmpe275.prj.models.user;
import edu.sjsu.cmpe275.prj.utils.CheckSession;
import edu.sjsu.cmpe275.prj.utils.PlayPP;
import edu.sjsu.cmpe275.prjservices.SearchService;
import edu.sjsu.cmpe275.prjservices.UserRecordService;
 
@SuppressWarnings("unused")
@Controller
public class FirstController {

    
    private HomePageModel homepageModel;
    private user userModel;
    private book bookModel;
    private category categoryModel;
    private LandingPage landingPage;
    HttpSession session;
    private static Jedis jedis;
    
    @Autowired
    private SearchService searchService;
    
    @Autowired
   	private HttpSession httpSession;
   	
   	@Autowired
   	private CheckSession sessionService;
    
    //1.Creating the u.i for user sign up page
    @RequestMapping(value = "/signup",method = RequestMethod.GET)
    public ModelAndView initN() {
    	userModel = new user();
        return new ModelAndView("signup", "userdetails", userModel);

    }
    
    @RequestMapping(value = "/showuser/{userId}",method = RequestMethod.GET)
    public ModelAndView showBook(@PathVariable int userId) {
    	
    	if(!sessionService.checkAuth())
    	{
    		System.out.println("chk class wrked!");
    		Login login = new Login();
        	
    		
    	    return new ModelAndView("login", "logindetails", login);
    		
    		
    	}

    	ModelAndView mv = new ModelAndView();
    	userModel = new user();
    	
    	JPAUserDAO obj= new JPAUserDAO();
    	userModel = obj.getUser(userId);
	
        mv.addObject("userdetails", userModel);
        mv.setViewName("showuser");
        
        return mv;
    }
    
    
    @RequestMapping(value = "/signup/{userId}",method = RequestMethod.GET)
    public ModelAndView edituser(@PathVariable int userId, HttpServletRequest request) {

    	ModelAndView mv = new ModelAndView();
    	userModel = new user();
    	
    	JPAUserDAO obj= new JPAUserDAO();
    	userModel = obj.getUser(userId);
    	mv.addObject("path", "../editprofile");
        mv.addObject("userdetails", userModel);
        mv.setViewName("profileedit");
        
        return mv;
    }
    
   
    @RequestMapping(value = "/signup",method = RequestMethod.POST)
    public ModelAndView initN1(@ModelAttribute("userdetails")user userModel1, BindingResult bindingResult, 
            HttpServletRequest request,  HttpServletResponse response) 
    {
        try 
        {
        	String msg=null;
           
            //ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult,"id","id", "id can not be empty.");
            ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult,"name","name", "name not be empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "emailId", "emailId", "emailId cant be empty");
            //ValidationUtils.r
 
            JPAUserDAO tempEmail = new JPAUserDAO();
            
            String one = userModel1.getEmailId();
         	String two = ".edu";
            if(!(one.endsWith(two)))
            {
            	
            	
            	 
            	 
            	 ModelAndView mv = new ModelAndView();
            	
            	 mv.addObject("msg", "edu. email required");
                 mv.setViewName("signup");
            	
            	 return mv;
            	
            	 
            	 
            }	 
            if(tempEmail.getExistingEmail(userModel1.getEmailId()) > 0)
            {
            	 ModelAndView mv1 = new ModelAndView();
            	 mv1.addObject("msg", "user with this email already exists");
                 mv1.setViewName("signup");
            	
            	 return mv1;
            	 
            }	
            if (bindingResult.hasErrors())
            {
                //returning the errors on same page if any errors..
                return new ModelAndView("signup", "userdetails", userModel1);
            }
            else
            {
            	System.out.println("user model details here --" +userModel1.getName()+userModel1.getPhone());
            	// insert the record by calling the service
            	//userRecordService.insertUser(userModel1);
            	
            	userModel1.setActive(1);
            	System.out.println(PlayPP.sha1(userModel1.getPassword()));
            	userModel1.setPassword(PlayPP.sha1(userModel1.getPassword()));
            	JPAUserDAO obj= new JPAUserDAO();
            	int l =obj.insert(userModel1);
            	userModel1.setUserId(l);
            	JPAUserStatisticsDAO objUserStat = new JPAUserStatisticsDAO();
            	statistics userStatistics = new statistics();
            	userStatistics.setNoOfBookDeleted(0);
            	userStatistics.setNoOfBookPurchased(0);
            	userStatistics.setNoOfBookTransac(0);
            	userStatistics.setNoOfBookUploaded(0);
            	userStatistics.setRatingBuyer(0);
            	userStatistics.setRatingSeller(0);
            	userStatistics.setUser(userModel1);
            	objUserStat.insert(userStatistics);
            	System.out.println(l);

            	
            	Login loginModel = new Login();
            	ModelAndView model = new ModelAndView("login");
            	
            	model.addObject("logindetails", loginModel);

           	 	
           	 	return model;
          }
        } catch (Exception e) {
            System.out.println("Exception in FirstController "+e.getMessage());
            e.printStackTrace();
            return new ModelAndView("signup", "userdetails", userModel1);
        }
        
    }
    

    
    @RequestMapping(value = "/editprofile",method = RequestMethod.POST)
    public ModelAndView editProfile(@ModelAttribute("userdetails")user userModel1, BindingResult bindingResult, 
            HttpServletRequest request,  HttpServletResponse response) 
    {
    	if(!sessionService.checkAuth())
    	{
    		System.out.println("chk class wrked!");
    		Login login = new Login();
        	
    		
    	    return new ModelAndView("login", "logindetails", login);
    		
    		
    	}
        try 
        {
        	String msg=null;
           
            //ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult,"id","id", "id can not be empty.");
            ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult,"name","name", "name not be empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "emailId", "emailId", "emailId cant be empty");
            //ValidationUtils.r
 
            JPAUserDAO tempEmail = new JPAUserDAO();
            
            String one = userModel1.getEmailId();
         	
            if (bindingResult.hasErrors())
            {
                //returning the errors on same page if any errors..
            	 ModelAndView mv1 = new ModelAndView();
            	 mv1.addObject("userdetails", userModel1);
            	 mv1.addObject("path", "editprofile");
                 mv1.setViewName("profileedit");
            	
            	 return mv1;
                
            }
            else
            {
            	System.out.println("user model details here --" +userModel1.getName()+userModel1.getPhone());
            	// insert the record by calling the service
            	//userRecordService.insertUser(userModel1);
            	
            	//userModel1.setActive(1);
            	System.out.println("???? " + userModel1.getUserId());
            	userModel1.setPassword(PlayPP.sha1(userModel1.getPassword()));
            	JPAUserDAO obj= new JPAUserDAO();
            	
            	
            	user x =obj.getUser(userModel1.getUserId());
            	//userModel1.setUserId(l);
            	
            	
            	
            	x.setActive(1);
            	x.setAddress(userModel1.getAddress());
            	x.setAge(userModel1.getAge());
            	x.setName(userModel1.getName());
            	x.setPassword(userModel1.getPassword());
            	x.setPhone(userModel1.getPhone());

            	obj= new JPAUserDAO();
            	obj.update(x);
            	
            	
            	

            	
           	 	return new ModelAndView("redirect: /showuser/" + userModel1.getUserId());
          }
        } catch (Exception e) {
            System.out.println("Exception in FirstController "+e.getMessage());
            e.printStackTrace();
            return new ModelAndView("signup", "userdetails", userModel1);
        }
        
    }

    
    
    
    /*
     * This method loads the homepage on application startup.
     * Works on "/" mapping.     * */
    @RequestMapping(value = "/",method = RequestMethod.GET)
    public ModelAndView initM() {

		System.out.println("Landing Page");
		ModelAndView mv = new ModelAndView();
		// getting data
		landingPage = new LandingPage();
		JPALandingPageDAO obj = new JPALandingPageDAO();
		landingPage.setBooks(obj.getBooks());
		landingPage.setCategories(obj.getCategories());
		System.out.println(landingPage);
		mv.addObject("pagedetails", landingPage);
		mv.setViewName("home");
		return mv;
    }
    
 //karan code starts

    	
   

	 //method to testCassandra
    @RequestMapping(value = "/Cassandra",method = RequestMethod.GET)
    public ModelAndView initN09() {
    	
    	CassandraConnectionDAO.testCassandra();
		
		
		
    	return initN();
    }
    
    //method to testredis
    @RequestMapping(value = "/Redis",method = RequestMethod.GET)
    public ModelAndView initN10() {
    	
    	/*CassandraConnectionDAO.testCassandra();
    	userModel = new user();
    	jedis=new Jedis("localhost");
		jedis.connect();
		System.out.println("Successfully connected to the redis server");
		String [] Kar={"karan khanna","sjsu","se"};
		System.out.println(Kar);
		jedis.set("karan", "khanna");
		System.out.println("saving in redis");
		System.out.println("getting from redis---" + jedis.get("karan"));*/
		
		
		
       return initN();
    }
	//karan code ends
   
    //method to search
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public ModelAndView Search(
    		@RequestParam(value ="searchbox", required = true, defaultValue = "C++") String input,
    		HttpServletRequest request) {
    	System.out.println(input);
    	List<book> lis = searchService.getAllResults(input);
    	System.out.println("size is--"+lis.size());
    	System.out.println("price is--"+lis.get(0).getPrice());
    	System.out.println(searchService.getAllResults(input));
    	System.out.println("go into search dude");
    	System.out.println("Landing Page");
		ModelAndView mv = new ModelAndView();
		// getting data
		
		mv.addObject("pagedetails", lis);
		mv.setViewName("searchResults");
		return mv;
    	//return new ModelAndView("home");
    } 
    
}